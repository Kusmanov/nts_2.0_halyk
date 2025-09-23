package com.example.nts.controller;

import com.example.nts.dto.PlaybackRequest;
import com.example.nts.service.*;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/audio")
@Tag(name = "Audio playback")
public class AudioController {
    @Autowired
    AudioService audioService;

    @Autowired
    JNIService jniService;

    @Autowired
    FileService fileService;

    @Autowired
    ValidateService validateService;

    /* PLAYBACK */
    @Operation(summary = "Playback audio files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/playback")
    public ResponseEntity<Map<String, List<String>>> playback(@RequestBody @Valid PlaybackRequest request) {
        Map<String, List<String>> response = new HashMap<>();
        List<String> sequence = new ArrayList<>();

        String language = request.getLanguage();

        // Валидация языка
        if (!validateService.isValidLanguage(language)) {
            response.put("error", Collections.singletonList("invalid language"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<String> audioFiles = request.getAudioFiles();
        List<String> filenames = fileService.getFileList("audio/" + language);

        // Валидация имени файлов
        if (!validateService.isValidFiles(audioFiles, filenames)) {
            response.put("available", filenames);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Формирование последовательности слов для воспроизведения
        for (String audioFile : audioFiles) {
            try {
                int intNumber = Integer.parseInt(audioFile);

                // Валидация числа
                if (!validateService.isValidNumber(intNumber)) {
                    response.put("error", Collections.singletonList("invalid number"));
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                for (String strNumber : jniService.getSequence(language, intNumber)) {
                    sequence.add(strNumber + ".wav");
                }
            } catch (Exception e) {
                // Если текст не является числом, то он добавляется без изменений
                sequence.add(audioFile);
            }
        }

        audioService.stopPlayback();
        audioService.playback(language, sequence);

        response.put("successful", sequence);
        return ResponseEntity.ok(response);
    }

    /* STOP-PLAYBACK */
    @Operation(summary = "Stop playback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/stop-playback")
    public String stopPlayback() {
        audioService.stopPlayback();
        return "successful";
    }
}
