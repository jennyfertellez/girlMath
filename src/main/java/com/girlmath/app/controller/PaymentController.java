package com.girlmath.app.controller;

import com.girlmath.app.model.Payment;
import com.girlmath.app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{debtId}")
    public ResponseEntity<Payment> logPayment(
            @PathVariable Long debtId,
            @RequestBody Map<String, Object> body) {
        Double amount = Double.parseDouble(body.get("amount").toString());
        String notes = body.getOrDefault("notes", "").toString();
        return ResponseEntity.ok(paymentService.logPayment(debtId, amount, notes));
    }

    @GetMapping("/{debtId}")
    public ResponseEntity<List<Payment>> getPaymentsByDebt(@PathVariable Long debtId) {
        return ResponseEntity.ok(paymentService.getPaymentsByDebt(debtId));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
