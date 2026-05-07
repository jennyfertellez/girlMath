package com.girlmath.app.repository;

import com.girlmath.app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByDebt_IdOrderByPaymentDateDesc(Long debtId);
}