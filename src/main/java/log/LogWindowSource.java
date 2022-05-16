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
    private final LinkedList<LogEntry> m_messages;

    /** Оболочка над структурой хранения сообщений, обеспечивающая синхронизированный доступ к ней */
    private List<LogEntry> syncMessages;

    /** Подписчики на событие изменения данных */
    private final List<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<LogEntry>();
        m_listeners = new CopyOnWriteArrayList<>();
        syncMessages = synchronizedList(m_messages);
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
     * Возвращает сообщения из заданного диапазона
     * @param startFrom - индекс начала сообщений
     * @param count     - количество сообщений для отображения
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        List result = new ArrayList();
        synchronized (syncMessages) {
            if (startFrom < 0 || startFrom >= syncMessages.size()) {
                return null;
            }
            int indexTo = Math.min(startFrom + count, syncMessages.size());
            result.addAll(syncMessages.subList(startFrom, indexTo));
        }
        return result;
    }

    /**
     * Возвращает все хранимые сообщения
     */
    public Iterable<LogEntry> all() {
        List result = new ArrayList();
        synchronized (syncMessages) {
            Iterator i = syncMessages.iterator();
            while (i.hasNext())
                result.add(i.next());
        }
        return result;
    }
}
