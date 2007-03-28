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
 */

// $HeadURL$
public final class StaticLog {

    public static final String SVN_ID = "$Id$";

    /**
     * The log to be written to.
     */
    private static Log log = null;
//    Log log = new Log("edu.ecs.csus.pc2", null, "logs", "pc2static");
    
    private StaticLog() {
        // empty private constructor because codestyle needed it.
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
