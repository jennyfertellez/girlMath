package com.girlmath.app.service;

import com.girlmath.app.model.Debt;
import com.girlmath.app.model.SavingsGoal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DebtService debtService;
    private final SavingsGoalService savingsGoalService;
    private final SnowballService snowballService;
    private final SavingsProjectionService savingsProjectionService;

    public Map<String, Object> getDashboardSummary() {
        List<Debt> debts = debtService.getAllDebts();
        List<SavingsGoal> goals = savingsGoalService.getAllGoals();

        // Total debt remaining
        double totalDebt = debts.stream()
                .mapToDouble(Debt::getBalance)
                .sum();

        // Total minimum payments
        double totalMinimumPayments = debts.stream()
                .mapToDouble(Debt::getMinimumPayment)
                .sum();

        // Snowball payoff timeline
        List<Map<String, Object>> snowball = snowballService.calculateSnowball(debts, 0.0);
        int monthsToDebtFree = snowball.size();

        // Savings projections
        List<Map<String, Object>> savingsProjections = goals.stream()
                .map(savingsProjectionService::calculateProjection)
                .toList();

        // Total saved across all goals
        double totalSaved = goals.stream()
                .mapToDouble(SavingsGoal::getCurrentSavings)
                .sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDebt", Math.round(totalDebt * 100.0) / 100.0);
        summary.put("totalMinimumPayments", Math.round(totalMinimumPayments * 100.0) / 100.0);
        summary.put("monthsToDebtFree", monthsToDebtFree);
        summary.put("numberOfDebts", debts.size());
        summary.put("numberOfSavingsGoals", goals.size());
        summary.put("totalSaved", Math.round(totalSaved * 100.0) / 100.0);
        summary.put("savingsProjections", savingsProjections);
        summary.put("debts", debts);

        return summary;
    }
}