package com.girlmath.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_date", nullable = false, columnDefinition = "DATE")
    private java.time.LocalDate paymentDate;

    @Column
    private String notes;

    @Column(nullable = false)
    private Double balanceAfterPayment;
}
