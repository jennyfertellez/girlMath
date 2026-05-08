package com.girlmath.app.service;

import com.girlmath.app.model.UserPreferences;
import com.girlmath.app.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;

    public UserPreferences getPreferences() {
        return userPreferencesRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    // Create default preferences if none exist
                    UserPreferences defaults = new UserPreferences();
                    defaults.setPaycheckFrequency("BIMONTHLY");
                    defaults.setPreferredDebtMethod("SNOWBALL");
                    return userPreferencesRepository.save(defaults);
                });
    }

    public UserPreferences updatePreferences(UserPreferences updated) {
        UserPreferences existing = getPreferences();
        existing.setPaycheckDayOne(updated.getPaycheckDayOne());
        existing.setPaycheckDayTwo(updated.getPaycheckDayTwo());
        existing.setPaycheckFrequency(updated.getPaycheckFrequency());
        existing.setPreferredDebtMethod(updated.getPreferredDebtMethod());
        return userPreferencesRepository.save(existing);
    }
}
