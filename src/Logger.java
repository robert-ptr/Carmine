import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

enum LogLevel
{
    INFO,
    WARN,
    ERROR,
    FATAL,
    DEBUG,
    TRACE,
}

public class Logger {
    private Logger() {}

    public static void log(String message, LogLevel level)
    {
        switch (level)
        {
            case INFO:
                System.out.println("INFO:" + message);
                break;
            case WARN:
                System.out.println("WARN: " + message);
                break;
            case ERROR:
                System.err.println("ERROR: " + message);
                break;
            case FATAL:
                System.err.println("FATAL: " + message);
                break;
            case DEBUG:
                System.out.println("DEBUG: " + message);
                break;
            case TRACE:
                System.out.println("TRACE: " + message);
                break;
            default:
                System.err.println(message);
        }
    }

    public static void log(Token token, String message, LogLevel level)
    {
        if (level == LogLevel.FATAL || level == LogLevel.ERROR)
            System.err.print(token.line + " ");
        else
            System.out.print(token.line + " ");

        log(message, level);
    }

    public static void log(Expr expr, String message, LogLevel level)
    {
        if (level == LogLevel.FATAL || level == LogLevel.ERROR)
            System.err.print(expr.getLine() + " ");
        else
            System.out.print(expr.getLine() + " ");

        log(message, level);
    }
}
