package com.girlmath.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.girlmath.app.model.SavingsGoal;
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
class SavingsGoalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        savingsGoalRepository.deleteAll();
    }

    @Test
    void shouldReturnEmptyList_whenNoGoalsExist() throws Exception {
        mockMvc.perform(get("/api/savings-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void shouldCreateGoal_andReturnGoal() throws Exception {
        SavingsGoal savingsGoal = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savingsGoal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.goalName", is("Home Down Payment")))
                .andExpect(jsonPath("$.targetAmount", is((50000.0))));
    }

    @Test
    void shouldReturnAllGoals_afterMultipleCreated() throws Exception {
        SavingsGoal goal1 = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);
        SavingsGoal goal2 = new SavingsGoal(null, "Emergency Fund", 10000.0, 1000.0, 200.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal1)));

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal2)));

        mockMvc.perform(get("/api/savings-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldDeleteGoal_andReturnNoContent() throws Exception {
        SavingsGoal goal = new SavingsGoal(null, "Vacation Fund", 3000.0, 500.0, 100.0);

        String response = mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SavingsGoal created = objectMapper.readValue(response, SavingsGoal.class);

        mockMvc.perform(delete("/api/savings-goals/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/savings-goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldUpdateGoal_andReturnUpdatedGoal() throws Exception {
        SavingsGoal goal = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);

        String response = mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SavingsGoal updated = objectMapper.readValue(response, SavingsGoal.class);
        updated.setCurrentSavings(10000.0);

        mockMvc.perform(put("/api/savings-goals/" + updated.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentSavings", is(10000.0)));
    }
}
