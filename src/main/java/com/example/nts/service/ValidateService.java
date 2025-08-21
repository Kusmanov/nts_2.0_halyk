package com.example.nts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidateService {
    private static final Logger logger = LogManager.getLogger(ValidateService.class);

    public boolean isValidLanguage(String language) {
        return language.equals("kz") || language.equals("ru") || language.equals("en");
    }

    public boolean isValidNumber(int intNumber) {
        return intNumber >= -999_999_999 && intNumber <= 999_999_999;
    }

    public boolean isValidFiles(List<String> audioFiles, List<String> filenames) {
        for (String file : audioFiles) {
            if (!filenames.contains(file)) {
                if (!file.chars().allMatch(Character::isDigit)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidName(String id, List<String> filenames) {
        return filenames.contains(id);
    }
}
