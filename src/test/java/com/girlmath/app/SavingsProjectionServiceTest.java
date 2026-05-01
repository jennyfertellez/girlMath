package com.girlmath.app;

import com.girlmath.app.model.SavingsGoal;
import com.girlmath.app.service.SavingsProjectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SavingsProjectionServiceTest {

    private SavingsProjectionService savingsProjectionService;

    @BeforeEach
    public void setUp() {
        savingsProjectionService = new SavingsProjectionService();
    }

    @Test
    void projection_shouldReturn100percent_whenGoalAlreadyReached() {
        SavingsGoal savingsGoal = new SavingsGoal(null, "Emergency Fund", 5000.0, 5000.0, 200.0);
        Map<String, Object> result = savingsProjectionService.calculateProjection(savingsGoal);
        assertEquals(100.0, result.get("percentComplete"));
        assertEquals(0, result.get("monthsRemaining"));
        assertEquals("Goal already reached!", result.get("projectedDate"));
    }

    @Test
    void projection_shouldCalculateCorrectMonthsRemaining() {
        SavingsGoal goal = new SavingsGoal(null, "Home Down Payment", 10000.0, 0.0, 500.0);
        Map<String, Object> result = savingsProjectionService.calculateProjection(goal);
        assertEquals(20, result.get("monthsRemaining"));
    }

    @Test
    void projection_shouldCalculateCorrectPercentageComplete() {
        SavingsGoal goal = new SavingsGoal(null, "Vacation Fund", 2000.0, 500.0, 100.0);
        Map<String, Object> result = savingsProjectionService.calculateProjection(goal);
        assertEquals(25.0, result.get("percentComplete"));
    }

    @Test
    void projection_shouldCalculateCorrectRemainingAmount() {
        SavingsGoal goal = new SavingsGoal(null, "Car Fund", 8000.0, 3000.0, 250.0);
        Map<String, Object> result = savingsProjectionService.calculateProjection(goal);
        assertEquals(5000.0, result.get("remainingAmount"));
    }

    @Test
    void projection_shouldReturnFutureProjectedDate() {
        SavingsGoal goal = new SavingsGoal(null, "Emergency Fund", 5000.0, 0.0, 500.0);
        Map<String, Object> result = savingsProjectionService.calculateProjection(goal);
        String projectedDate = (String) result.get("projectedDate");
        assertTrue(projectedDate.compareTo(java.time.LocalDate.now().toString()) > 0);
    }
}
