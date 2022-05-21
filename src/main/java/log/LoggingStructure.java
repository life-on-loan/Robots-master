package log;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Структура хранения сообщений лога
 */
public class LoggingStructure implements Iterable<LogEntry> {
    private Node head;
    private Node tall;
    private final int maxSize;
    private boolean isIterating = false;
    private int currentSize = 0;

    public LoggingStructure(int size) {
        maxSize = size;
    }

    /**
     * Добавление новых сообщений в структуру с потенциальным удалением старых
     * @param message - сообщение, которое нужно добавить
     */
    public synchronized void addWithRemoveOldMessage(LogEntry message) throws Exception {
        if (isIterating) {
            throw new Exception();
        }
        Node newHead = new Node(message);
        if (head != null) {
            newHead.setNext(head);
            head.setPrevious(newHead);
        }
        head = newHead;
        if (tall == null) {
            tall = head;
        }
        // случай "старых" сообщений
        if (currentSize == maxSize) {
            Node newTall = tall.previous();
            newTall.setNext(null);
            tall = newTall;
        } else
            currentSize++;
    }

    /**
     * Возвращает количество хранимых сообщений
     */
    public int size() {
        return currentSize;
    }

    /**
     * Потокобезопасный итератор по структуре
     */
    @Override
    public Iterator<LogEntry> iterator() {
        return new Iterator<>() {
            Node next = head;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public LogEntry next() {
                if (hasNext()) {
                    Node result = next;
                    next = next.next();
                    return result.message();
                } else {
                    Logger.error("Нет следующего элемента для итерирования");
                    throw new NoSuchElementException();
                }
            }
        };
    }

    /**
     * Возвращает сообщения из заданного диапазона
     *
     * @param startFrom - индекс начала сообщений
     * @param indexTo   - индекс конца сообщений
     */
    public synchronized Iterable<LogEntry> subList(int startFrom, int indexTo) {
        isIterating = true;
        Iterator<LogEntry> iterator = new Iterator<>() {
            int currentIndex = 0;
            Node currentHead = head;

            @Override
            public boolean hasNext() {
                while (currentIndex < startFrom) {
                    currentHead = currentHead.next();
                    currentIndex++;
                }
                boolean hasNext = currentHead.next() != null && currentIndex < indexTo;
                if (!hasNext) {
                    isIterating = false;
                }
                return hasNext;
            }

            @Override
            public LogEntry next() {
                if (this.hasNext()) {
                    Node result = currentHead;
                    currentHead = head.next();
                    currentIndex++;
                    return result.message();
                } else
                    throw new NoSuchElementException();
            }
        };

        return () -> iterator;
    }
}