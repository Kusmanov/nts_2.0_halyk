package com.example.nts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    private static final Logger logger = LogManager.getLogger(FileService.class);

    @Autowired
    private ResourceLoader resourceLoader;

    // Метод получения списка соответствий файлов для экранов, кнопок или суммы
    public List<String> getMapping(String resourcePath) {
        List<String> mapping = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.error("Файл не найден в ресурсах: {}", resourcePath);
                return mapping;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                mapping = reader.lines()
                        .map(String::trim)
                        .toList();
            }
        } catch (IOException e) {
            logger.error("Ошибка чтения ресурса: {}", resourcePath, e);
        }
        return mapping;
    }

    // Копирование файлов из ресурсной директории в корневую директорию приложения
    public void copyFile(String dir, String file) {
        String resourcePath = dir + "/" + file;
        String targetPathStr = resourcePath; // сохраняем с тем же относительным путём
        Resource resource = resourceLoader.getResource("classpath:" + resourcePath);

        if (!resource.exists()) {
            logger.error("Ресурс не найден в classpath: {}", resourcePath);
            return;
        }

        Path targetPath = Paths.get(targetPathStr);

        try {
            // Создаем родительскую директорию, если её нет
            if (targetPath.getParent() != null && !Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
                logger.info("Создана директория: {}", targetPath.getParent());
            }

            if (Files.exists(targetPath)) {
                return;
            }

            // Копируем потоками
            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = new FileOutputStream(targetPath.toFile())) {

                byte[] buffer = new byte[8192]; // 8 KB буфер
                int bytesRead;
                long totalBytes = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                logger.info("Файл {} успешно скопирован в {} ({} байт)", resourcePath, targetPath, totalBytes);
            }

        } catch (IOException e) {
            logger.error("Ошибка при копировании ресурса {} в {}", resourcePath, targetPath, e);
        }
    }

    // Метод для получения списка файлов внутри "resources"
    public List<String> getFileList(String resourcePath) {
        List<String> files = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            // Загружаем все ресурсы из указанной директории (и поддиректорий)
            Resource[] resources = resolver.getResources("classpath*:" + resourcePath + "/**");

            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    // имя файла (без пути внутри JAR)
                    files.add(resource.getFilename());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }
}
