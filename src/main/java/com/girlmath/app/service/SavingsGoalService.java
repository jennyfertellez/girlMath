package com.girlmath.app.service;

import com.girlmath.app.model.SavingsGoal;
import com.girlmath.app.repository.SavingsGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;

    public List<SavingsGoal> getAllGoals() {
        return savingsGoalRepository.findAll();
    }

    public SavingsGoal addGoal(SavingsGoal goal) {
        return savingsGoalRepository.save(goal);
    }

    public SavingsGoal updateGoal(Long id, SavingsGoal updatedGoal) {
        SavingsGoal existing = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        existing.setGoalName(updatedGoal.getGoalName());
        existing.setTargetAmount(updatedGoal.getTargetAmount());
        existing.setCurrentSavings(updatedGoal.getCurrentSavings());
        existing.setMonthlyContribution(updatedGoal.getMonthlyContribution());
        return savingsGoalRepository.save(existing);
    }

    public void deleteGoal(Long id) {
        savingsGoalRepository.deleteById(id);
    }
}
