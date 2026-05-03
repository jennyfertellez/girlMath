package com.girlmath.app.service;

import com.girlmath.app.model.Debt;
import com.girlmath.app.repository.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;

    public List<Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    public Debt addDebt(Debt debt) {
        return debtRepository.save(debt);
    }

    public Debt updateDebt(Long id, Debt updateDebt) {
        Debt existing = debtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Debt not found with id: " + id));
        existing.setName(updateDebt.getName());
        existing.setBalance(updateDebt.getBalance());
        existing.setInterestRate(updateDebt.getInterestRate());
        existing.setMinimumPayment(updateDebt.getMinimumPayment());
        existing.setMonthlyBudget(updateDebt.getMonthlyBudget());
        existing.setDebtType(updateDebt.getDebtType());
        existing.setCreditLimit(updateDebt.getCreditLimit());
        existing.setCustomDebtType(updateDebt.getCustomDebtType());
        existing.setPaidOff(updateDebt.getPaidOff());
        return debtRepository.save(existing);
    }

    public void deleteDebt(Long id) {
        debtRepository.deleteById(id);
    }
}
