package carmine.compiler.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import carmine.compiler.structures.Expr;
import carmine.compiler.structures.Token;

public class CarmineLogger {
    private static final Logger log = LoggerFactory.getLogger(CarmineLogger.class);

    public static void log(String message, LogLevel level)
    {
        switch (level)
        {
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case TRACE:
                log.trace(message);
                break;
            default:
                System.err.println(message);
        }
    }

    public static void log(Token token, String message, LogLevel level)
    {
        log(token.getLine() + " " + message, level);
    }

    public static void log(Expr expr, String message, LogLevel level)
    {
        log(expr.getLine() + " " + message, level);
    }
}
