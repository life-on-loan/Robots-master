package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.FrameProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Класс загрузки параметров окна из файла
 */
public class FrameLoader implements AutoCloseable {
    private final FileInputStream fileInputStream;
    private final ObjectMapper objectMapper;

    /** Путь до файла с параметрами окон в корневом каталоге пользователя */
    private static final String SAVED_STATE_PATH = System.getProperty("user.home") + File.separator + "javaRobotsState.txt";

    public FrameLoader() {
        objectMapper = new ObjectMapper();
        try {
            fileInputStream = new FileInputStream(SAVED_STATE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при чтении состояния приложения из файла: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при чтении состояния приложения из файла: " + e.getMessage());
        }
    }

    /**
     * Метод загрузки параметров из файла
     * @return лист параметров
     * @throws RuntimeException - ошибка при чтении объекта при работе с файлом
     */
    public List<FrameProperties> loadProperties() {
        try {
            return readValue(fileInputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при чтении состояния приложения из файла: " + e.getMessage());
        }
    }

    /**
     * Чтение сохранённых параметров окон
     *
     * @param content - массив байт для чтения
     * @return список параметров
     * @throws RuntimeException - ошибка при чтении объекта при работе с файлом
     */
    private List<FrameProperties> readValue(byte[] content) {
        try {
            return objectMapper.readValue(content, new TypeReference<List<FrameProperties>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при чтении состояния приложения из файла: " + e.getMessage());
        }
    }
}
