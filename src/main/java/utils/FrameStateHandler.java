package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.FrameProperties;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Класс для сохранения/восстановления (обработки) состояния окон
 */
public class FrameStateHandler {
    /** Путь до файла с параметрами окон в корневом каталоге пользователя */
    private static final String SAVED_STATE_PATH = System.getProperty("user.home") + File.separator + "javaRobotsState.txt";
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Метод загрузки параметров из файла
     * @return лист параметров
     * @throws RuntimeException - ошибка при чтении объекта при работе с файлом
     */
    public List<FrameProperties> loadProperties() {
        if (new File(SAVED_STATE_PATH).exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(SAVED_STATE_PATH)) {
                return objectMapper.readValue(fileInputStream.readAllBytes(), new TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Возникла ошибка при чтении состояния приложения из файла: " + e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Метод записи параметров окна в файл
     *
     * @param properties - параметры окна
     */
    public void write(List<FrameProperties> properties) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(SAVED_STATE_PATH)){
            fileOutputStream.write(objectMapper.writer().withDefaultPrettyPrinter()
                    .writeValueAsString(properties).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при записи состояния приложения в файл: " + e.getMessage(), e);
        }
    }
}
