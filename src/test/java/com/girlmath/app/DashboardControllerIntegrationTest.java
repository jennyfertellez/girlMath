package com.girlmath.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.girlmath.app.model.Debt;
import com.girlmath.app.model.SavingsGoal;
import com.girlmath.app.repository.DebtRepository;
import com.girlmath.app.repository.SavingsGoalRepository;
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
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        debtRepository.deleteAll();
        savingsGoalRepository.deleteAll();
    }

    @Test
    void shouldReturnDashboard_withZeroValues_whenNoData() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDebt", is(0.0)))
                .andExpect(jsonPath("$.totalSaved", is(0.0)))
                .andExpect(jsonPath("$.numberOfDebts", is(0)))
                .andExpect(jsonPath("$.numberOfSavingsGoals", is(0)));
    }

    @Test
    void shouldReturnCorrectTotalDebt_whenDebtsExist() throws Exception {
        Debt debt1 = new Debt(null, "Chase Credit Card", 2500.0, 24.99, 65.0, 200.0, "CREDIT_CARD", 120000.0, null);
        Debt debt2 = new Debt(null, "Student Loan", 18500.0, 5.05, 190.0, 250.0, "STUDENT_LOAN", 100000.0, null);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt1)));

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt2)));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDebt", is(21000.0)))
                .andExpect(jsonPath("$.numberOfDebts", is(2)));
    }

    @Test
    void shouldReturnCorrectTotalSaved_whenGoalsExist() throws Exception {
        SavingsGoal goal1 = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);
        SavingsGoal goal2 = new SavingsGoal(null, "Emergency Fund", 10000.0, 2000.0, 200.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal1)));

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal2)));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSaved", is(7000.0)))
                .andExpect(jsonPath("$.numberOfSavingsGoals", is(2)));
    }

    @Test
    void shouldReturnSavingsProjections_inDashboard() throws Exception {
        SavingsGoal goal = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal)));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savingsProjections", hasSize(1)))
                .andExpect(jsonPath("$.savingsProjections[0].goalName", is("Home Down Payment")));
    }

    @Test
    void shouldReturnMonthsToDebtFree_whenDebtsExist() throws Exception {
        Debt debt = new Debt(null, "Medical Bill", 500.0, 0.0, 100.0, 100.0, "MEDICAL", 500.0, null);

        mockMvc.perform(post("/api/debts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debt)));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthsToDebtFree", is(5)));
    }

}
