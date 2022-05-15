package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс, отвечающий за данные для окна логов
 */
public class LogWindowSource {
    /** Количество сообщений в логах */
    private final int m_iQueueLength;

    /** Структура данных, отвечающая за хранение сообщений */
    private final LinkedList<LogEntry> m_messages;

    /** Подписчики на событие изменения данных */
    private final List<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<LogEntry>();
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
    public synchronized void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        if (size() == m_iQueueLength)
            m_messages.removeFirst();
        m_messages.addLast(entry);
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    /**
     * Возвращает количество хранимых сообщений
     */
    public int size() { return m_messages.size(); }

    /**
     * Возвращает итератор по сообщениям в заданном диапазоне
     *
     * @param startFrom - индекс начала сообщений
     * @param count     - количество сообщений для отображения
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= m_messages.size()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(m_messages.subList(startFrom, Math.min(startFrom + count, m_messages.size())));
    }

    /**
     * Возвращает итератор по всем хранимым сообщениям
     */
    public Iterable<LogEntry> all() { return new ArrayList<>(m_messages); }
}
