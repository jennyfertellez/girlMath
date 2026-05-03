package com.girlmath.app.service;

import com.girlmath.app.model.Debt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvalancheService {

    public List<Map<String, Object>> calculateAvalanche(List<Debt> debts, Double extraPayment) {

        // Sort by interest rate descending (avalanche method)
        List<Debt> sorted = new ArrayList<>(debts);
        sorted.sort((a, b) -> Double.compare(b.getInterestRate(), a.getInterestRate()));

        // Deep copy balances
        double[] balances = sorted.stream()
                .mapToDouble(Debt::getBalance)
                .toArray();

        double[] minimums = sorted.stream()
                .mapToDouble(Debt::getMinimumPayment)
                .toArray();

        List<Map<String, Object>> results = new ArrayList<>();
        int month = 0;
        int maxMonths = 600;

        while (hasRemainingDebt(balances) && month < maxMonths) {
            month++;
            double extra = extraPayment;

            for (int i = 0; i < balances.length; i++) {
                if (balances[i] <= 0) continue;

                Debt debt = sorted.get(i);
                double monthlyInterest = (debt.getInterestRate() / 100) / 12 * balances[i];
                balances[i] += monthlyInterest;

                // Apply minimum payment
                double payment = Math.min(minimums[i], balances[i]);
                balances[i] -= payment;

                // Apply extra payment to highest interest debt first
                if (extra > 0 && balances[i] > 0) {
                    double extraApplied = Math.min(extra, balances[i]);
                    balances[i] -= extraApplied;
                    extra -= extraApplied;
                }

                // Roll minimum into extra when paid off
                if (balances[i] <= 0) {
                    balances[i] = 0;
                    extra += minimums[i];
                }
            }

            Map<String, Object> monthSnapshot = new LinkedHashMap<>();
            monthSnapshot.put("month", month);
            for (int i = 0; i < sorted.size(); i++) {
                monthSnapshot.put(sorted.get(i).getName(),
                        Math.max(0, Math.round(balances[i] * 100.0) / 100.0));
            }
            results.add(monthSnapshot);
        }

        return results;
    }

    private boolean hasRemainingDebt(double[] balances) {
        for (double b : balances) {
            if (b > 0) return true;
        }
        return false;
    }
}