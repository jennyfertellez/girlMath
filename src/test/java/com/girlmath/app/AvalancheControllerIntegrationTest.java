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
class AvalancheControllerIntegrationTest {

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
    void shouldReturnEmptyList_whenNoDebtsExists() throws Exception {
        mockMvc.perform(get("/api/avalanche/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnPayoffTimeline_whenDebtsExist() throws Exception {
        Debt debt = new Debt(null, "Chase Credit Card", 1000.0, 0.0,
                100.0, 100.0, "CREDIT_CARD", null, null, false);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)));

        mockMvc.perform(get("/api/avalanche/calculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].month", is(1)));
    }

    @Test
    void shouldSortByHighestInterest_first() throws Exception {
        Debt highInterest = new Debt(null, "High Interest Card", 1000.0, 25.0,
                50.0, 50.0, "CREDIT_CARD", null, null, false);
        Debt lowInterest = new Debt(null, "Low Interest Loan", 1000.0, 5.0,
                50.0, 50.0, "STUDENT_LOAN", null, null, false);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(highInterest)));

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lowInterest)));

        mockMvc.perform(get("/api/avalanche/calculate?extraPayment=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void shouldExcludePaidOffDebts() throws Exception {
        Debt activeDebt = new Debt(null, "Active Card", 1000.0, 20.0,
                100.0, 100.0, "CREDIT_CARD", null, null, false);
        Debt paidDebt = new Debt(null, "Paid Off Card", 500.0, 15.0,
                50.0, 50.0, "CREDIT_CARD", null, null, true);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activeDebt)));

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paidDebt)));

        String response = mockMvc.perform(get("/api/avalanche/calculate"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Should not contain paid off debt in timeline
        assert !response.contains("Paid Off Card");
    }

    @Test
    void shouldPayOffFaster_withExtraPayment() throws Exception {
        Debt debt = new Debt(null, "Chase Credit Card", 1000.0, 0.0,
                100.0, 100.0, "CREDIT_CARD", null, null, false);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)));

        String withoutExtra = mockMvc.perform(get("/api/avalanche/calculate?extraPayment=0"))
                .andReturn().getResponse().getContentAsString();

        String withExtra = mockMvc.perform(get("/api/avalanche/calculate?extraPayment=500"))
                .andReturn().getResponse().getContentAsString();

        int monthsWithout = objectMapper.readTree(withoutExtra).size();
        int monthsWith = objectMapper.readTree(withExtra).size();

        assert monthsWith < monthsWithout;
    }
}
