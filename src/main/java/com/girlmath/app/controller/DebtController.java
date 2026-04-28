package com.girlmath.app.controller;

import com.girlmath.app.model.Debt;
import com.girlmath.app.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DebtController {

    private final DebtService debtService;

    @GetMapping
    public ResponseEntity<List<Debt>> getAllDebts() {
        return ResponseEntity.ok(debtService.getAllDebts());
    }

    @PostMapping
    public ResponseEntity<Debt> addDebt(@RequestBody Debt debt) {
        return ResponseEntity.ok(debtService.addDebt(debt));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Debt> updateDebt(@PathVariable Long id, @RequestBody Debt debt) {
        return ResponseEntity.ok(debtService.updateDebt(id, debt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Debt> deleteDebt(@PathVariable Long id) {
        debtService.deleteDebt(id);
        return ResponseEntity.noContent().build();
    }
}
