package com.example.nts.component;

import com.example.nts.service.FileService;
import com.example.nts.service.JNIService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class Initializing {
    private static final Logger logger = LogManager.getLogger(Initializing.class);

    @Autowired
    FileService fileService;

    @Autowired
    JNIService jniService;

    @PostConstruct
    public void init() {
        try {
            // Копируем содержимое ресурса lib в корневую директорию
            for (String file : fileService.getFileList("lib")) {
                fileService.copyFile("lib", file);
            }


            // Копируем содержимое ресурса setup в корневую директорию
            for (String file : fileService.getFileList("setup")) {
                fileService.copyFile("setup", file);
            }

            // Загружаем ntt.dll
            try {
                String dllResourcePath = "lib/ntt.dll";
                String dllPath = Paths.get("").toAbsolutePath() + "/" + dllResourcePath;
                System.load(dllPath);
                logger.info("Библиотека ntt.dll загружена");
            } catch (UnsatisfiedLinkError e) {
                logger.error("Ошибка загрузки библиотеки ntt.dll");
            }

            // Вызываем библиотеку для проверки наличия ключа V2C
            if (jniService.getSequence("kz", 42) == null) {
                logger.error("Отсутствует ключ V2C");
                System.exit(1);
            }

        } catch (Exception e) {
            logger.error("Ошибка инициализации: ", e);
        }
    }
}
