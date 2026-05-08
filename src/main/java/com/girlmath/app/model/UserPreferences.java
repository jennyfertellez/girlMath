package com.girlmath.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer paycheckDayOne;

    @Column
    private Integer paycheckDayTwo;

    @Column
    private String paycheckFrequency;

    @Column
    private String preferredDebtMethod; // SNOWBALL or AVALANCHE
}
