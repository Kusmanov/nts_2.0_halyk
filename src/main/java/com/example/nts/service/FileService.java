package com.example.nts.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    // Метод для получения названий файлов по указанному пути
    public List<String> getFilenames(String dirPath) {
        List<String> filenames = new ArrayList<>();
        try {
            Path path = Paths.get(dirPath);

            // Получаем файлы из директории
            Files.list(path).forEach(resourcePath -> {
                if (Files.isRegularFile(resourcePath)) {
                    String filename = resourcePath.getFileName().toString();
                    filenames.add(filename);
                }
            });
        } catch (IOException e) {
            logger.error("Ошибка получения названий файлов по пути {}", dirPath);
        }
        return filenames;
    }

    // Метод получения списка соответствий файлов для экранов, кнопок или суммы
    public List<String> getMappingFilenames(String resourcePath) {
        try {
            Path filePath = Paths.get(resourcePath);
            // Чтение всех строк файла в список
            List<String> mapping = Files.readAllLines(filePath);
            // Применяем trim() к каждому элементу и сохраняем результат
            mapping = mapping.stream()
                    .map(String::trim)
                    .toList();
            return mapping;
        } catch (IOException e) {
            logger.error("Ошибка получения соответствий файлов для {}", resourcePath);
        }
        return new ArrayList<>();
    }

    // Метод для получения названий ресурсных файлов по указанному пути
    public List<String> getFilesInResources(String dirPath) {
        List<String> filesInResources = new ArrayList<>();

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            // Загружаем все ресурсы внутри указанного каталога и его подкаталогов
            Resource[] resources = resolver.getResources("classpath*:" + dirPath + "/**");

            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    // Добавляем путь к файлу в список
                    filesInResources.add(resource.getURI().toString().replaceFirst(".*!/+", ""));
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка получения списка фалов в директории с ресурсами приложения: {}", dirPath);
        }

        return filesInResources;
    }

    // Копирование файлов из ресурсной директории в корневую директорию приложения, по такому же относительному пути
    public void copyFileFromResourcesToRoot(String resourcePath) {
        try {
            // Получаем ресурс из classpath
            Resource resource = resourceLoader.getResource("classpath:" + resourcePath);

            // Проверяем, существует ли ресурс
            if (!resource.exists()) {
                logger.error("Директория с ресурсами не найдена: {}", resourcePath);
                return;
            }

            // Получаем URI ресурса
            Path sourcePath = Paths.get(resource.getURI());

            // Определяем целевой путь в корневой директории приложения
            Path targetPath = Paths.get(resourcePath);

            // Создаем родительскую директорию, если она не существует
            Files.createDirectories(targetPath.getParent());

            // Копируем файл, если его не было ранее
            if (!Files.exists(targetPath)) {
                try (InputStream inputStream = resource.getInputStream();
                     OutputStream outputStream = new FileOutputStream(targetPath.toFile())) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    logger.info("Файл скопирован из ресурсной директории {} в корневую директорию {}", sourcePath, targetPath);
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка копирования файла из ресурсной в корневую директорию для {}", resourcePath);
        }
    }
}
