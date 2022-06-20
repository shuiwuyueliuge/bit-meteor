package cn.mio.btm.infrastructure.log;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;

/**
 *
 */
public class Logger {

    private final java.util.logging.Logger log;

    private final String level;

    public Logger(java.util.logging.Logger log) {
        this.log = log;
        String level = System.getProperty("log.level");
        this.level = level == null ? "info" : level.toLowerCase(Locale.ROOT);
    }

    public void info(String msg) {
        if (Objects.equals(level, "info") || Objects.equals(level, "error") || Objects.equals(level, "debug")) {
            log.info(Thread.currentThread().getName() + " INFO" + " " + msg);
        }
    }

    public void debug(String msg) {
        if (Objects.equals(level, "debug") || Objects.equals(level, "error")) {
            log.info(Thread.currentThread().getName() + " DEBUG" + " " + msg);
        }
    }

    public void error(String msg, Exception e) {
        if (Objects.equals(level, "info") || Objects.equals(level, "error")) {
            log.log(Level.SEVERE, Thread.currentThread().getName() +  "ERROR" + " " + msg + " " + e.getMessage());
        }
    }
}
