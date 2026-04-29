package com.girlmath.app.controller;

import com.girlmath.app.model.SavingsGoal;
import com.girlmath.app.service.SavingsGoalService;
import com.girlmath.app.service.SavingsProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/savings-projection")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SavingsProjectionController {

    private final SavingsProjectionService savingsProjectionService;
    private final SavingsGoalService savingsGoalService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProjections() {
        List<Map<String, Object>> projections = savingsGoalService.getAllGoals()
                .stream()
                .map(savingsProjectionService::calculateProjection)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProjectionById(@PathVariable Long id) {
        SavingsGoal goal = savingsGoalService.getAllGoals()
                .stream()
                .filter(g -> g.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        return ResponseEntity.ok(savingsProjectionService.calculateProjection(goal));
    }
}