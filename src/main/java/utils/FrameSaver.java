package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gui.FrameProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Класс записи параметров окна в файла
 */
public class FrameSaver implements AutoCloseable {
    private final FileOutputStream fileOutputStream;
    private final ObjectWriter objectWriter;

    /** Путь до файла с параметрами окон в корневом каталоге пользователя */
    public static final String SAVED_STATE_PATH = System.getProperty("user.home") + File.separator + "javaRobotsState.txt";

    public FrameSaver() {
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            fileOutputStream = new FileOutputStream(SAVED_STATE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при записи состояния приложения в файл: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при записи состояния приложения в файл: " + e.getMessage());
        }
    }

    /**
     * Метод записи параметров окна в файл
     *
     * @param properties - параметры окна
     */
    public void write(List<FrameProperties> properties) {
        try {
            fileOutputStream.write(propertiesToJSON(properties).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при записи состояния приложения в файл: " + e.getMessage());
        }
    }

    /**
     * Метод создающий JSON из параметров окна
     *
     * @param properties - параметры окна
     * @return сгенерированный JSON в виде строки
     */
    private String propertiesToJSON(List<FrameProperties> properties) {
        try {
            return objectWriter.writeValueAsString(properties);
        } catch (IOException e) {
            throw new RuntimeException("Возникла ошибка при записи состояния приложения в файл: " + e.getMessage());
        }
    }
}
