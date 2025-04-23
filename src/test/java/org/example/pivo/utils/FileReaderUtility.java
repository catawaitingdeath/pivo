package org.example.pivo.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;

import static java.util.Objects.requireNonNull;

@UtilityClass
public class FileReaderUtility {
    private static final String PREFIX = "/";
    private static final ObjectMapper MAPPER = objectMapper();

    public static ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * Читает файл из ресурсов и возвращает его содержимое как строку.
     *
     * @param path путь к файлу в ресурсах, без указания __files
     * @return содержимое файла как строка
     */
    @SneakyThrows
    public static String readFile(String path) {
        String fullPath = path;
        if (!path.startsWith(PREFIX)) {
            fullPath = PREFIX + path;
        }
        fullPath = "__files" + fullPath;
        return new String(
                requireNonNull(
                        FileReaderUtility.class.getClassLoader().getResourceAsStream(fullPath),
                        "file '" + fullPath + "' not found"
                ).readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    /**
     * Читает файл из ресурсов и возвращает его содержимое как объект целевого класса.
     *
     * @param path  путь к файлу в ресурсах, без указания __files
     * @param clazz Указание на класс целевого объекта
     * @param <T>   Класс целевого объекта
     * @return Объект класса T, считанный из файла
     */
    @SneakyThrows
    public static <T> T readFile(String path, Class<T> clazz) {
        String json = readFile(path);
        return MAPPER.readValue(json, clazz);
    }

    /**
     * Читает файл из ресурсов и возвращает его содержимое как объект целевого класса.
     *
     * @param path          путь к файлу в ресурсах, без указания __files
     * @param typeReference указание на класс целевого объекта в виде {@link TypeReference} (полезно для джериков)
     * @param <T>           Класс целевого объекта
     * @return Объект класса T, считанный из файла
     */
    @SneakyThrows
    public static <T> T readFile(String path, TypeReference<T> typeReference) {
        String json = readFile(path);
        return MAPPER.readValue(json, typeReference);
    }
}

