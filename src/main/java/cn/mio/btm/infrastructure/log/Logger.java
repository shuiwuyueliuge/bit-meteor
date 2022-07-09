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

    private final String className;

    public Logger(java.util.logging.Logger log, String className) {
        this.log = log;
        this.className = className;
        String level = System.getProperty("log.level");
        this.level = level == null ? "info" : level.toLowerCase(Locale.ROOT);
    }

    public void info(String msg) {
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (Objects.equals(level, "info") || Objects.equals(level, "error") || Objects.equals(level, "debug")) {
            log.info("[" + Thread.currentThread().getName() + "] INFO:" + className + ":" + line + " - " + msg);
        }
    }

    public void debug(String msg) {
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (Objects.equals(level, "debug") || Objects.equals(level, "error")) {
            log.info("[" + Thread.currentThread().getName() + "] DEBUG:" + className + ":" + line + " - " + msg);
        }
    }

    public void error(String msg, Throwable e) {
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (Objects.equals(level, "info") || Objects.equals(level, "error")) {
            log.log(Level.SEVERE, "[" + Thread.currentThread().getName() +  "] ERROR:" + className + ":" + line + " - " + msg + " " + e.getMessage());
        }
    }
}
