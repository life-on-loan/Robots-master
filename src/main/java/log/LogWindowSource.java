package log;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.synchronizedList;

/**
 * Класс, отвечающий за данные для окна логов
 */
public class LogWindowSource {
    /** Количество сообщений в логах */
    private final int m_iQueueLength;

    /** Структура данных, отвечающая за хранение сообщений */
    private final LoggingStructure m_messages;

    /** Подписчики на событие изменения данных */
    private final List<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new LoggingStructure(iQueueLength);
        m_listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Подписка на обноление
     * @param listener - объект, желающий подписаться на обновление
     */
    public void registerListener(LogChangeListener listener) {
        m_listeners.add(listener);
    }

    /**
     * Отписка от обновлений
     * @param listener - объект, желающий отписаться от обновления
     */
    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    /**
     * Потокобезопасное добавление сообщения в лог (с потенциальным удалением старых) за О(1)
     * @param logLevel   - уровень сообщения
     * @param strMessage - содержание сообщения
     */
    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        try {
            m_messages.addWithRemoveOldMessage(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    /**
     * Возвращает сообщения из заданного диапазона
     * @param startFrom - индекс начала сообщений
     * @param count     - количество сообщений для отображения
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= m_messages.size()) {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, m_messages.size());
        return m_messages.subList(startFrom, indexTo);
    }

    /**
     * Возвращает все хранимые сообщения
     */
    public Iterable<LogEntry> all() {
        return m_messages;
    }
}
