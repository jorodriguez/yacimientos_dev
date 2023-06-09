package lector.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nlopez
 * @param <E>
 */
public class UtilLog4j<E> implements Serializable {

    /**
     * 06-mar-2014 Joel Rodriguez Es muy importante que esta variable estatica
     * se declare, ya que esta misma se usa en el archivo de propiedades
     *
     */
    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        System.setProperty("current.date", dateFormat.format(Calendar.getInstance().getTime()));
    }

    public static final UtilLog4j log = new UtilLog4j<>();
    private Logger logger;

    public UtilLog4j() {

        try {
            
          System.setProperty("logback.configurationFile", "/logback.xml");

        } catch (Exception e) {
            java.util.logging.Logger.getAnonymousLogger().log(Level.SEVERE, "", e);
        }
    }

    public Logger getLogger(String clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public void debug(E t, String logs) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.debug(logs);
    }

    public void debug(E t, String message, Object[] params) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.debug(MessageFormat.format(message, params));
    }
    
    public void debug(E t, String message, Throwable e) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.debug(message, e);
    }

    public void info(Throwable exception) {
        logger.info("", exception);
    }

    public void info(Object exception) {
        logger.info("", exception);

    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(E t, String message, Object[] params) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.info(MessageFormat.format(message, params));
    }

    public void info(E t, String logs) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.info(logs);

    }

    public void info(E t, String logs, Throwable exception) {
        logger = LoggerFactory.getLogger(t.getClass());

        if (exception == null) {
            logger.info(logs, exception);
        } else {
            logger.info(logs);
        }
    }

    public void info(E t, String message, Object[] params, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.info(MessageFormat.format(message, params), tr);
    }

    public void warn(Throwable exception) {
        logger.warn("", exception);
    }

    public void warn(E t, String logs) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.warn(logs);
    }

    public void warn(E t, String message, Object[] params) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.warn(MessageFormat.format(message, params));
    }

    public void warn(E t, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.warn("", tr);
    }

    public void warn(E t, String logs, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.warn(logs, tr);
    }

    public void warn(E t, String message, Object[] params, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.warn(MessageFormat.format(message, params), tr);
    }

    public void error(Throwable exception) {
        logger.error("", exception);
    }

    public void error(E t, String logs) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(logs);
    }

    public void error(E t, String message, Object[] params) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(MessageFormat.format(message, params));
    }

    public void error(E t, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error("", tr);
    }

    public void error(E t, String logs, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(logs, tr);
    }

    public void error(E t, String message, Object[] params, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(MessageFormat.format(message, params), tr);
    }

    public void fatal(Throwable exception) {
        logger.error("", exception);
    }

    public void fatal(E t, String logs) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error("", logs);
    }

    public void fatal(E t, String message, Object[] params) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(MessageFormat.format(message, params));
    }

    public void fatal(E t, Throwable tt) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error("", tt);
    }

    public void fatal(E t, String logs, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(logs, tr);
    }

    public void fatal(E t, String message, Object[] params, Throwable tr) {
        logger = LoggerFactory.getLogger(t.getClass());
        logger.error(MessageFormat.format(message, params), tr);
    }

}
