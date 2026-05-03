package com.girlmath.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.girlmath.app.model.Debt;
import com.girlmath.app.repository.DebtRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SnowballControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        debtRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyList_whenNoDebtsExist() throws Exception {
        mockMvc.perform(get("/api/snowball/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnPayoffTimeline_whenDebtsExist() throws Exception {
        Debt debt = new Debt(null, "Chase Credit Card", 1000.0, 0.0, 100.0, 100.0, "CREDIT_CARD", 1500.0);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)));

        mockMvc.perform(get("/api/snowball/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].month", is(1)));
    }

    @Test
    void shouldPayOffFaster_withExtraPayment() throws Exception {
        Debt debt = new Debt(null, "Chase Credit Card", 1000.0, 0.0, 100.0, 100.0, "CREDIT_CARD", 3000.0);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)));

        String withoutExtra = mockMvc.perform(get("/api/snowball/calculate?extraPayment=0"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String withExtra = mockMvc.perform(get("/api/snowball/calculate?extraPayment=500"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        int monthsWithout = objectMapper.readTree(withoutExtra).size();
        int monthsWith = objectMapper.readTree(withExtra).size();

        assert monthsWith < monthsWithout;
    }

    @Test
    void shouldReturnMonthField_inEachEntry() throws Exception {
        Debt debt = new Debt(null, "Medical Bill", 500.0, 0.0, 100.0, 100.0, "MEDICAL", 500.0);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)));

        mockMvc.perform(get("/api/snowball/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month", is(1)))
                .andExpect(jsonPath("$[4].month", is(5)));
    }

}
