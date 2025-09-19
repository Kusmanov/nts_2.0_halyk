package com.example.nts.controller;

import com.example.nts.dto.*;
import com.example.nts.service.FileService;
import com.example.nts.service.ValidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mapping")
@Tag(name = "Get mapping filenames")
public class MappingController {
    private static final Logger logger = LogManager.getLogger(MappingController.class);

    @Autowired
    ValidateService validateService;

    @Autowired
    FileService fileService;

    /* SCREEN */
    @Operation(summary = "Get mapping filenames for screen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/screen")
    public ResponseEntity<Map<String, List<String>>> screen(@RequestBody @Valid ScreenMappingRequest request) {
        Map<String, List<String>> response = new HashMap<>();

        String language = request.getLanguage();

        // Валидация языка
        if (!validateService.isValidLanguage(language)) {
            response.put("error", Collections.singletonList("invalid language"));
            logger.error("Ошибка валидации языка: " + language);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String screenID = request.getScreenID() + ".txt"; // Добавляем .txt к тексту
        List<String> filenames = fileService.getFileList("mapping/screen/" + language);

        // Валидация screen ID
        if (!validateService.isValidName(screenID, filenames)) {
            response.put("Screen not found, available", filenames);
            logger.error("Ошибка валидации имени файла: " + screenID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("mapping", fileService.getMapping("mapping/screen/" + language + "/" + screenID));
        logger.info("Успешно получены соответствия из {} для воспроизведения экрана на {}", screenID, language);
        return ResponseEntity.ok(response);
    }

    /* BUTTON */
    @Operation(summary = "Get mapping filenames for button")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/button")
    public ResponseEntity<Map<String, List<String>>> button(@RequestBody @Valid ButtonMappingRequest request) {
        Map<String, List<String>> response = new HashMap<>();

        String language = request.getLanguage();

        // Валидация языка
        if (!validateService.isValidLanguage(language)) {
            response.put("error", Collections.singletonList("invalid language"));
            logger.error("Ошибка валидации языка: " + language);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String buttonID = request.getButtonID() + ".txt"; // Добавляем .txt к тексту
        List<String> filenames = fileService.getFileList("mapping/button/" + language);

        // Валидация Button ID
        if (!validateService.isValidName(buttonID, filenames)) {
            response.put("available", filenames);
            logger.error("Ошибка валидации имени файла: " + buttonID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("mapping", fileService.getMapping("mapping/button/" + language + "/" + buttonID));
        logger.info("Успешно получены соответствия из {} для воспроизведения кнопки на {}", buttonID, language);
        return ResponseEntity.ok(response);
    }

    /* AMOUNT */
    @Operation(summary = "Get mapping filenames for amount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/amount")
    public ResponseEntity<Map<String, List<String>>> amount(@RequestBody @Valid AmountMappingRequest request) {
        Map<String, List<String>> response = new HashMap<>();

        String language = request.getLanguage();

        // Валидация языка
        if (!validateService.isValidLanguage(language)) {
            response.put("error", Collections.singletonList("invalid language"));
            logger.error("Ошибка валидации языка: " + language);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String filename = request.getFilename();
        List<String> filenames = fileService.getFileList("mapping/amount/" + language);

        // Валидация названия файла
        if (!validateService.isValidName(filename, filenames)) {
            response.put("available", filenames);
            logger.error("Ошибка валидации имени файла: " + filename);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("mapping", fileService.getMapping("mapping/amount/" + language + "/" + filename));
        logger.info("Успешно получены соответствия из {} для воспроизведения введенной суммы на {}", filename, language);
        return ResponseEntity.ok(response);
    }

    /* TOTAL */
    @Operation(summary = "Get mapping filenames for total")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/total")
    public ResponseEntity<Map<String, List<String>>> total(@RequestBody @Valid TotalMappingRequest request) {
        Map<String, List<String>> response = new HashMap<>();

        String language = request.getLanguage();

        // Валидация языка
        if (!validateService.isValidLanguage(language)) {
            response.put("error", Collections.singletonList("invalid language"));
            logger.error("Ошибка валидации языка: " + language);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String filename = request.getFilename();
        List<String> filenames = fileService.getFileList("mapping/total/" + language);

        // Валидация названия файла
        if (!validateService.isValidName(filename, filenames)) {
            response.put("available", filenames);
            logger.error("Ошибка валидации имени файла: " + filename);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("mapping", fileService.getMapping("mapping/total/" + language + "/" + filename));
        logger.info("Успешно получены соответствия из {} для воспроизведения суммы пополнения на {}", filename, language);
        return ResponseEntity.ok(response);
    }

    /* BALANCE */
    @Operation(summary = "Get mapping filenames for balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/balance")
    public ResponseEntity<Map<String, List<String>>> balance(@RequestBody @Valid BalanceMappingRequest request) {
        Map<String, List<String>> response = new HashMap<>();

        String language = request.getLanguage();

        // Валидация языка
        if (!validateService.isValidLanguage(language)) {
            response.put("error", Collections.singletonList("invalid language"));
            logger.error("Ошибка валидации языка: " + language);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String filename = request.getFilename();
        List<String> filenames = fileService.getFileList("mapping/balance/" + language);

        // Валидация названия файла
        if (!validateService.isValidName(filename, filenames)) {
            response.put("available", filenames);
            logger.error("Ошибка валидации имени файла: " + filename);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("mapping", fileService.getMapping("mapping/balance/" + language + "/" + filename));
        logger.info("Успешно получены соответствия из {} для воспроизведения суммы баланса на {}", filename, language);
        return ResponseEntity.ok(response);
    }
}