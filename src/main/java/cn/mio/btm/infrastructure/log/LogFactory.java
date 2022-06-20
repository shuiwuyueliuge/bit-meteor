package cn.mio.btm.infrastructure.log;

/**
 *
 */
public class LogFactory {

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
    }
}
