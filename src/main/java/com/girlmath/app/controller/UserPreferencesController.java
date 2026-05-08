package com.girlmath.app.controller;

import com.girlmath.app.model.UserPreferences;
import com.girlmath.app.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping
    public ResponseEntity<UserPreferences> getPreferences(){
        return ResponseEntity.ok(userPreferencesService.getPreferences());
    }

    @PutMapping
    public ResponseEntity<UserPreferences> updatePreferences(@RequestBody UserPreferences userPreferences){
        return ResponseEntity.ok(userPreferencesService.updatePreferences(userPreferences));
    }
}
