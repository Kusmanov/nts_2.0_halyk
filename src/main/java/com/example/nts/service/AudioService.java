package com.example.nts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class AudioService {
    private static final Logger logger = LogManager.getLogger(AudioService.class);

    // Поле для хранения текущего Clip
    private Clip clip;
    // Добавляем волатильность для работы с потоками
    private volatile boolean playbackStopped;

    public void playback(String language, List<String> files) {
        playbackStopped = false;

        for (String file : files) {
            String resourcePath = "/audio/" + language + "/" + file;

            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    logger.error("Файл {} не найден в ресурсах", resourcePath);
                    break;
                }

                try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is))) {
                    // Перед созданием нового Clip — останавливаем старый
                    if (clip != null && clip.isRunning()) {
                        clip.stop();
                        clip.close();
                    }

                    // Получаем Clip (объект для воспроизведения)
                    clip = AudioSystem.getClip();
                    clip.open(audioStream);

                    // Воспроизводим аудиофайл
                    clip.start();

                    logger.info("Воспроизводится аудиофайл: {}", file);

                    // Устанавливаем задержку, чтобы clip успел стартовать
                    while (!clip.isActive()) {
                        Thread.sleep(100);
                    }

                    // Ждем завершения воспроизведения
                    while (clip.isActive() && !playbackStopped) {
                        Thread.sleep(100);
                    }

                    clip.stop();

                    if (playbackStopped) {
                        logger.info("Воспроизведение было остановлено");
                        break;
                    }
                }
            } catch (UnsupportedAudioFileException e) {
                logger.error("Формат файла {} не поддерживается", file);
            } catch (IOException e) {
                logger.error("Ошибка чтения файла: {}", file, e);
            } catch (LineUnavailableException e) {
                logger.error("Ошибка воспроизведения файла: {}", file, e);
            } catch (Exception e) {
                logger.info("Процесс воспроизведения был прерван", e);
                Thread.currentThread().interrupt();
            }
        }

        if (clip != null) {
            clip.close();
        }
    }

    public void stopPlayback() {
        playbackStopped = true;
        try {
            Thread.sleep(200); // Задержка должна быть длительнее чем в методе playback
        } catch (Exception e) {
        }
    }
}
