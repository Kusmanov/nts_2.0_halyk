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
        if ("kz".equals(language)) {
            String sequence = convertToKazakh(number);
            return sequence != null ? Arrays.asList(sequence.split(" ")) : null;
        } else if ("ru".equals(language)) {
            String sequence = convertToRussian(number);
            return sequence != null ? Arrays.asList(sequence.split(" ")) : null;
        } else if ("en".equals(language)) {
            String sequence = convertToEnglish(number);
            return sequence != null ? Arrays.asList(sequence.split(" ")) : null;
        } else {
            return null;
        }
    }
}
