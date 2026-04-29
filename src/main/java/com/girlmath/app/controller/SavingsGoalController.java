package com.girlmath.app.controller;

import com.girlmath.app.model.SavingsGoal;
import com.girlmath.app.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings-goals")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @GetMapping
    public ResponseEntity<List<SavingsGoal>> getAllGoals() {
        return  ResponseEntity.ok(savingsGoalService.getAllGoals());
    }

    @PostMapping
    public ResponseEntity<SavingsGoal> addGoal(@RequestBody SavingsGoal savingsGoal) {
        return ResponseEntity.ok(savingsGoalService.addGoal(savingsGoal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoal> updateGoal(@PathVariable Long id, @RequestBody SavingsGoal savingsGoal) {
        return ResponseEntity.ok(savingsGoalService.updateGoal(id, savingsGoal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        savingsGoalService.deleteGoal(id);
        return ResponseEntity.ok().build();
    }
}
