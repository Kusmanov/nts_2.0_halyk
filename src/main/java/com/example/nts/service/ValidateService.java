package com.example.nts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidateService {
    private static final Logger logger = LogManager.getLogger(ValidateService.class);

    public boolean isValidLanguage(String language) {
        if (language.equals("kz") || language.equals("ru") || language.equals("en")) {
            return true;
        }
        logger.error("Ошибка валидации языка: {}", language);
        return false;
    }

    public boolean isValidNumber(int intNumber) {
        if (intNumber >= -999_999_999 && intNumber <= 999_999_999) {
            return true;
        }
        logger.error("Ошибка валидации числа: {}", intNumber);
        return false;
    }

    public boolean isValidFiles(List<String> audioFiles, List<String> filenames) {
        for (String file : audioFiles) {
            if (!filenames.contains(file)) {
                if (!file.chars().allMatch(Character::isDigit)) {
                    logger.error("Ошибка валидации имен файлов: {}", audioFiles);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidName(String filename, List<String> filenames) {
        if (filenames.contains(filename)) {
            return true;
        }
        logger.error("Ошибка валидации имени файла: {}", filename);
        return false;
    }
}
