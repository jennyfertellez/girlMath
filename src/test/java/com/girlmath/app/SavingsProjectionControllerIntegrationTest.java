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
class SavingsProjectionControllerIntegrationTest {

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
        mockMvc.perform(get("/api/savings-projection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnProjection_forSingleGoal() throws Exception {
        SavingsGoal goal = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal)));

        mockMvc.perform(get("/api/savings-projection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].goalName", is("Home Down Payment")))
                .andExpect(jsonPath("$[0].targetAmount", is(50000.0)))
                .andExpect(jsonPath("$[0].percentComplete", is(10.0)))
                .andExpect(jsonPath("$[0].monthsRemaining", is(90)));
    }

    @Test
    void shouldReturnProjectionById() throws Exception {
        SavingsGoal goal = new SavingsGoal(null, "Emergency Fund", 10000.0, 2000.0, 200.0);

        String response = mockMvc.perform(post("/api/savings-goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goal)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SavingsGoal created = objectMapper.readValue(response, SavingsGoal.class);

        mockMvc.perform(get("/api/savings-projection/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName", is("Emergency Fund")))
                .andExpect(jsonPath("$.percentComplete", is(20.0)))
                .andExpect(jsonPath("$.monthsRemaining", is(40)));
    }

    @Test
    void shouldReturn100Percent_whenGoalAlreadyReached() throws Exception {
        SavingsGoal goal = new SavingsGoal(null, "Vacation Fund", 3000.0, 3000.0, 100.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal)));

        mockMvc.perform(get("/api/savings-projection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percentComplete", is(100.0)))
                .andExpect(jsonPath("$[0].monthsRemaining", is(0)))
                .andExpect(jsonPath("$[0].projectedDate", is("Goal already reached!")));
    }

    @Test
    void shouldReturnAllProjections_whenMultipleGoalsExist() throws Exception {
        SavingsGoal goal1 = new SavingsGoal(null, "Home Down Payment", 50000.0, 5000.0, 500.0);
        SavingsGoal goal2 = new SavingsGoal(null, "Emergency Fund", 10000.0, 1000.0, 200.0);

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal1)));

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goal2)));

        mockMvc.perform(get("/api/savings-projection"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}