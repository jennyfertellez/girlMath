package com.girlmath.app;

import com.girlmath.app.model.Debt;
import com.girlmath.app.service.AvalancheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AvalancheServiceTest {

    private AvalancheService avalancheService;

    @BeforeEach
    public void setUp() {
        avalancheService = new AvalancheService();
    }

    @Test
    void avalanche_shouldReturnEmptyList_whenNoDebts() {
        List<Map<String, Object>> result = avalancheService.calculateAvalanche(List.of(), 0.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void avalanche_shouldPayOffDebt_inExpectedMonths() {
        Debt debt = new Debt(null, "Test Card", 1000.0, 0.0, 100.0, 100.0, "CREDIT_CARD", 2500.0, null, false);
        List<Map<String, Object>> result = avalancheService.calculateAvalanche(List.of(debt), 0.0);
        assertEquals(10,  result.size());
    }

    @Test
    void avalanche_shouldPayOffFaster_withExtraPayment() {
        Debt debt = new Debt(null, "Test Card", 1000.0, 0.0, 100.0, 100.0, "CREDIT_CARD", 2500.0, null, false);
        List<Map<String, Object>> withoutExtra = avalancheService.calculateAvalanche(List.of(debt), 0.0);
        List<Map<String, Object>> withExtra = avalancheService.calculateAvalanche(List.of(debt), 200.0);
        assertTrue(withExtra.size() < withoutExtra.size());
    }

    @Test
    void avalanche_shouldSortByInterestRate_highestFirst() {
        Debt highInterest = new Debt(null, "High Interest", 1000.0, 25.0, 50.0, 50.0, "CREDIT_CARD", 2500.0, null, false);
        Debt lowInterest = new Debt(null, "Low Interest", 1000.0, 5.0, 50.0, 50.0, "STUDENT_LOAN", 3000.0, null, false);
        List<Map<String, Object>> result = avalancheService.calculateAvalanche(
                List.of(lowInterest, highInterest), 100.0);
        // High interest debt should be paid off first
        int highInterestPayoff = -1;
        int lowInterestPayoff = -1;
        for (int i = 0; i < result.size(); i++) {
            if ((double) result.get(i).get("High Interest") == 0.0 && highInterestPayoff == -1) {
                highInterestPayoff = i;
            }
            if ((double) result.get(i).get("Low Interest") == 0.0 && lowInterestPayoff == -1) {
                lowInterestPayoff = i;
            }
        }
        assertTrue(highInterestPayoff <= lowInterestPayoff);
    }

    @Test
    void avalanche_shouldHandleMultipleDebts() {
        Debt debt1 = new Debt(null, "Card A", 500.0, 20.0, 50.0, 50.0, "CREDIT_CARD", 2500.0, null, false);
        Debt debt2 = new Debt(null, "Card B", 1000.0, 15.0, 75.0, 75.0, "CREDIT_CARD", 2500.0, null, false);
        Debt debt3 = new Debt(null, "Loan", 2000.0, 5.0, 100.0, 100.0, "STUDENT_LOAN", 3000.0, null, false);
        List<Map<String, Object>> result = avalancheService.calculateAvalanche(
                List.of(debt1, debt2, debt3), 0.0);
        assertFalse(result.isEmpty());
        Map<String, Object> lastMonth = result.get(result.size() - 1);
        assertEquals(0.0, lastMonth.get("Card A"));
        assertEquals(0.0, lastMonth.get("Card B"));
        assertEquals(0.0, lastMonth.get("Loan"));
    }
}

