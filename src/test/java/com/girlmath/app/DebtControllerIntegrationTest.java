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
class DebtControllerIntegrationTest {

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
        mockMvc.perform(get("/api/debts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldCreateDebt_andReturnIt() throws Exception {
        Debt debt = new Debt(null, "Chase Credit Card", 2500.0, 24.99, 65.0, 200.0, "CREDIT_CARD", 5000.0, null, false, null);

        mockMvc.perform(post("/api/debts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Chase Credit Card")))
                .andExpect(jsonPath("$.balance", is(2500.0)));
    }

    @Test
    void shouldReturnAllDebts_afterMultipleCreated() throws Exception {
        Debt debt1 = new Debt(null, "Chase Credit Card", 2500.0, 24.99, 65.0, 200.0, "CREDIT_CARD", 5000.0, null, false, null);
        Debt debt2 = new Debt(null, "Student Loan", 18500.0, 5.05, 190.0, 250.0, "STUDENT_LOAN", 100000.0, null, false, null);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt1)));

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt2)));

        mockMvc.perform(get("/api/debts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldDeleteDebt_andReturnNoContent() throws Exception {
        Debt debt = new Debt(null, "Medical Bill", 1200.0, 0.0, 50.0, 100.0, "MEDICAL", 1200.0, null, false, null);

        String response = mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Debt created =  objectMapper.readValue(response, Debt.class);

        mockMvc.perform(delete("/api/debts/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/debts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldUpdateDebt_andReturnUpdated() throws Exception {
        Debt debt = new Debt(null, "Chase Credit Card", 2500.0, 24.99, 65.0, 200.0, "CREDIT_CARD", 3000.0, null, false, null);

        String response = mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Debt created =  objectMapper.readValue(response, Debt.class);
        created.setBalance(1500.0);

        mockMvc.perform(put("/api/debts/" + created.getId())
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance", is(1500.0)));
    }
}
