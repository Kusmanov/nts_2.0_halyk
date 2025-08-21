package com.example.nts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AudioService {
    private static final Logger logger = LogManager.getLogger(AudioService.class);

    // Поле для хранения текущего Clip
    private Clip clip;
    // Добавляем волатильность для работы с потоками
    private volatile boolean playbackStopped;

    public void playback(String language, List<String> files) {
        for (String file : files) {
            // Указываем путь к аудио файлу
            File audioFile = new File("audio/" + language + "/" + file);

            if (!audioFile.exists()) {
                logger.error("Файл " + file + " не найден");
                break;
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
                // Получаем Clip (объект для воспроизведения)
                clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Воспроизводим аудиофайл
                clip.start();

                logger.info("Воспроизводится аудиофайл: " + file);

                // Устанавливаем задержку, чтобы clip успел стартовать
                while (!clip.isActive()) {
                    Thread.sleep(100);
                }

                // Ждем завершения воспроизведения
                while (clip.isActive() && !playbackStopped) {
                    Thread.sleep(100);
                }

                clip.stop();

                // Останавливаем выполнение кода, если воспроизведение было остановлено
                if (playbackStopped) {
                    logger.info("Воспроизведение было остановлено");
                    break; // Прекращаем воспроизведение
                }
            } catch (UnsupportedAudioFileException e) {
                logger.error("Формат файла " + file + " не поддерживается");
            } catch (IOException e) {
                logger.error("Ошибка чтения файла: " + file);
            } catch (LineUnavailableException e) {
                logger.error("Ошибка воспроизведения файла: " + file);
            } catch (Exception e) {
                logger.info("Процесс воспроизведения был прерван");
                Thread.currentThread().interrupt();
            }
        }

        clip.close();
    }

    public void stopPlayback() {
        playbackStopped = true;
        try {
            Thread.sleep(200); // Задержка должна быть длительнее чем в методе playback
        } catch (Exception e) {
        }
        playbackStopped = false;
    }
}
