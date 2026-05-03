package com.girlmath.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "debts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Double minimumPayment;

    @Column(nullable = false)
    private Double monthlyBudget;

    @Column(nullable = false)
    private String debtType;

    @Column
    private Double creditLimit;
}