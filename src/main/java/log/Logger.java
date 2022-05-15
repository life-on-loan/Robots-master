package log;

import java.util.concurrent.atomic.AtomicInteger;

public final class Logger
{
    private static final LogWindowSource defaultLogSource;
    static {
        defaultLogSource = new LogWindowSource(4);
    }
    /** Поле атомарного номера сообщения */
    private static final AtomicInteger messageNumber = new AtomicInteger(0);
    
    private Logger()
    {
    }

    public static void debug(String strMessage) {
        defaultLogSource.append(LogLevel.Debug, messageNumber.getAndIncrement() + " " + strMessage);
    }
    
    public static void error(String strMessage) {
        defaultLogSource.append(LogLevel.Error, messageNumber.getAndIncrement() + " " + strMessage);
    }

    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}
