package com.girlmath.app.controller;

import com.girlmath.app.model.Debt;
import com.girlmath.app.service.DebtService;
import com.girlmath.app.service.SnowballService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/snowball")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SnowballController {

    private final SnowballService snowballService;
    private final DebtService debtService;

    @GetMapping("/calculate")
    public ResponseEntity<List<Map<String, Object>>> calculate(
            @RequestParam(defaultValue = "0") Double extraPayment) {
        List<Debt> debts = debtService.getAllDebts();
        List<Map<String, Object>> result = snowballService.calculateSnowball(debts, extraPayment);
        return ResponseEntity.ok(result);
    }
}