package com.example.nts.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class JNIService {
    // JNI методы
    private native String convertToKazakh(int number);

    private native String convertToRussian(int number);

    private native String convertToEnglish(int number);

    // Оберточный метод для возврата последовательности в виде списка
    public List<String> getSequence(String language, int number) {
        return switch (language) {
            case "kz" -> {
                String sequence = convertToKazakh(number);
                if (sequence != null) {
                    yield Arrays.asList(sequence.split(" "));
                }
                yield null;
            }
            case "ru" -> {
                String sequence = convertToRussian(number);
                if (sequence != null) {
                    yield Arrays.asList(sequence.split(" "));
                }
                yield null;
            }
            case "en" -> {
                String sequence = convertToEnglish(number);
                if (sequence != null) {
                    yield Arrays.asList(sequence.split(" "));
                }
                yield null;
            }
            default -> null;
        };
    }
}
