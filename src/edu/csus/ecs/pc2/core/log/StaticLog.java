package edu.csus.ecs.pc2.core.log;

/**
 * A Log which can be used in any class.
 * 
 * Instead of passing a Log to every low level class, this
 * class was created.  Wherever possible use getLog() to
 * get the actual Log class instance, if that is not
 * available use this class. 
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class StaticLog {

    /**
     * The log to be written to.
     */
    private static Log log = null;
//    Log log = new Log("edu.ecs.csus.pc2", null, "logs", "pc2static");
    
    private StaticLog() {
        // empty private constructor because codestyle needed it.
    }

    /**
     * A method that indicates that this is Unclassified log message.
     * 
     * This message should be changed to an actual log message.
     * @param message
     * @param ex
     */
    public static void unclassified(String message, Exception ex) {
        log.log(Log.WARNING, "U " + message, ex);
    }
    
    /**
     * @see #unclassified(String, Exception)
     * @param message
     */
    public static void unclassified(String message) {
        log.log(Log.WARNING, "U " + message);
    }

    public static void warning(String message) {
        log.warning(message);
    }

    public static void info(String message) {
        log.finer(message);
    }

    public static void log(String message, Exception ex) {
        log.log(Log.WARNING, message, ex);
    }

    public static Log getLog() {
        return log;
    }

    public static void setLog(Log log) {
        StaticLog.log = log;
    }
}
