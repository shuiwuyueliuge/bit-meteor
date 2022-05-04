package cn.mayu.bt.log;

/**
 *
 */
public class LogFactory {

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
    }
}
