package com.girlmath.app.service;

import com.girlmath.app.model.SavingsGoal;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SavingsProjectionService {

    public Map<String, Object> calculateProjection(SavingsGoal goal) {
        double remaining = goal.getTargetAmount() - goal.getCurrentSavings();

        if (remaining <= 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("goalName", goal.getGoalName());
            result.put("targetAmount", goal.getTargetAmount());
            result.put("currentSavings", goal.getCurrentSavings());
            result.put("monthsRemaining", 0);
            result.put("projectedDate", "Goal already reached!");
            result.put("percentComplete", 100.0);
            return result;
        }

        int months = (int) Math.ceil(remaining / goal.getMonthlyContribution());

        // Calculate projected completion date
        java.time.LocalDate projectedDate = java.time.LocalDate.now().plusMonths(months);

        double percentComplete = (goal.getCurrentSavings() / goal.getTargetAmount()) * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("goalName", goal.getGoalName());
        result.put("targetAmount", goal.getTargetAmount());
        result.put("currentSavings", goal.getCurrentSavings());
        result.put("remainingAmount", Math.round(remaining * 100.0) / 100.0);
        result.put("monthlyContribution", goal.getMonthlyContribution());
        result.put("monthsRemaining", months);
        result.put("projectedDate",  projectedDate.toString());
        result.put("percentComplete", Math.round(percentComplete * 100.0) / 100.0);
        return result;
    }
}
