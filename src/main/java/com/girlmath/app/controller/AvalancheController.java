package com.girlmath.app.controller;

import com.girlmath.app.model.Debt;
import com.girlmath.app.service.AvalancheService;
import com.girlmath.app.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avalanche")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AvalancheController {

    private final AvalancheService avalancheService;
    private final DebtService debtService;

    @GetMapping("/calculate")
    public ResponseEntity<List<Map<String, Object>>> calculate(
            @RequestParam(defaultValue = "0") Double extraPayment) {
        List<Debt> debts = debtService.getAllDebts()
                .stream()
                .filter(d -> d.getPaidOff() == null || !d.getPaidOff())
                .toList();
        List<Map<String, Object>> result = avalancheService.calculateAvalanche(debts, extraPayment);
        return ResponseEntity.ok(result);
    }
}