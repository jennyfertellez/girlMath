package com.girlmath.app.service;

import com.girlmath.app.model.Debt;
import com.girlmath.app.model.Payment;
import com.girlmath.app.repository.DebtRepository;
import com.girlmath.app.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DebtRepository debtRepository;

    @Transactional
    public Payment logPayment(Long debtId, Double amount, String notes) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt with id " + debtId + " not found"));

        // Update the debt balance
        double newBalance = Math.max(0, debt.getBalance() - amount);
        debt.setBalance(Math.round(newBalance * 100.0) / 100.0);

        // Mark as paid off if balance hits zero
        if (debt.getBalance() == 0) {
            debt.setPaidOff(true);
        }

        debtRepository.save(debt);

        // Record the payment
        Payment payment = new Payment();
        payment.setDebt(debt);
        payment.setAmount(amount);
        payment.setPaymentDate(java.time.LocalDate.now());
        payment.setNotes(notes);
        payment.setBalanceAfterPayment(debt.getBalance());

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByDebt(Long debtId) {
        return paymentRepository.findByDebt_IdOrderByPaymentDateDesc(debtId);
    }

    public  List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
