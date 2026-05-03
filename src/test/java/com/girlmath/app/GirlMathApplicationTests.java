package com.girlmath.app;

import com.girlmath.app.model.Debt;
import com.girlmath.app.service.SnowballService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GirlMathApplicationTests {

    private SnowballService snowballService;

    @BeforeEach
    void setUp() {
        snowballService = new SnowballService();
    }

    @Test
    void snowball_shouldReturnEmptyList_whenNoDebts() {
        List<Map<String, Object>> result = snowballService.calculateSnowball(List.of(), 0.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void snowball_shouldPayOffDebt_inExpectedMonths() {
        Debt debt = new Debt(null, "Test Card", 1000.0, 0.0, 100.0, 100.0, "CREDIT_CARD", 25000.0, null);
        List<Map<String, Object>> result = snowballService.calculateSnowball(List.of(debt), 0.0);
        assertEquals(10, result.size());
    }

    @Test
    void snowball_shouldPayoffFaster_withExtraPayment() {
        Debt debt = new Debt(null, "Test Card", 1000.0, 0.0, 100.0, 100.0, "CREDIT_CARD", 2500.0, null);
        List<Map<String, Object>> withoutExtra = snowballService.calculateSnowball(List.of(debt), 0.0);
        List<Map<String, Object>> withExtra = snowballService.calculateSnowball(List.of(debt), 200.0);
        assertTrue(withExtra.size() < withoutExtra.size());
    }

    @Test
    void snowball_shouldSortByBalance_smallestFirst() {
        Debt big = new Debt(null, "Big Debt", 5000.0, 10.0, 100.0, 100.0, "CREDIT_CARD", 10000.0, null);
        Debt small = new Debt(null, "Small Debt", 500.0, 10.0, 50.0, 50.0, "CREDIT_CARD", 1500.0, null);
        List<Map<String, Object>> result = snowballService.calculateSnowball(List.of(big, small), 0.0);
        // Small debt should hit 0 before big debt
        Map<String, Object> firstMonth =  result.get(0);
        assertTrue(firstMonth.containsKey("Small Debt"));
        assertTrue(firstMonth.containsKey("Big Debt"));
    }
}
