package edu.csus.ecs.pc2.core.log;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Based on the Java Logging API
 * <p>
 * Sample Usage:
 * </p>
 *
 * <pre>
 *
 *         Log log;
 *         { before use }
 *         log = new Log();
 *         log.setFileHandlers(&quot;logs&quot;);
 * </pre>
 *
 * <p>
 * opens(with overwrite) a file for each log level in the directory specified to setFileHandlers
 * </p>
 *
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/log/Log.java$
public class Log extends Logger {

    public static final String SVN_ID = "$Id$";

    /**
     * SEVERE is a message level indicating a serious failure.
     * <p>
     * In general SEVERE messages should describe events that are of considerable importance and which will prevent normal program
     * execution. They should be reasonably intelligible to end users and to system administrators.
     *
     * @see Logger#severe(java.lang.String)
     *      </p>
     */
    public static final Level SEVERE = Level.SEVERE;

    /**
     * WARNING is a message level indicating a potential problem.
     * <p>
     * In general WARNING messages should describe events that will be of interest to end users or system managers, or which
     * indicate potential problems.
     * </p>
     *
     * @see Logger#warning(java.lang.String)
     */
    public static final Level WARNING = Level.WARNING;

    /**
     * INFO is a message level for informational messages.
     * <p>
     * Typically INFO messages will be written to the console or its equivalent. So the INFO level should only be used for
     * reasonably significant messages that will make sense to end users and system admins.
     * </p>
     *
     * @see Logger#info(java.lang.String)
     */
    public static final Level INFO = Level.INFO;

    /**
     * CONFIG is a message level for static configuration messages.
     * <p>
     * CONFIG messages are intended to provide a variety of static configuration information, to assist in debugging problems that
     * may be associated with particular configurations. For example, CONFIG message might include the CPU type, the graphics
     * depth, the GUI look-and-feel, etc.
     * </p>
     *
     * @see Logger#config(java.lang.String)
     */
    public static final Level CONFIG = Level.CONFIG;

    /**
     * FINE is a message level providing tracing information.
     *
     * @see Logger#fine(java.lang.String)
     */
    public static final Level FINE = Level.FINE;

    /**
     * FINER indicates a fairly detailed tracing message. (aka DEBUG)
     *
     * @see Logger#finer(java.lang.String)
     */
    public static final Level FINER = Level.FINER;

    /**
     * DEBUG indicates a fairly detailed tracing message. (aka FINEST)
     *
     * @see Logger#finest(java.lang.String)
     */
    public static final Level DEBUG = Level.FINEST;

    /**
     * FINEST indicates a highly detailed tracing message.
     *
     * @see Logger#finest(java.lang.String)
     */
    public static final Level FINEST = Level.FINEST;

    private LogStreamHandler streamHandler = null;
    
    private boolean isWindowVisible = false;

    /**
     * Start log to ?? // TODO what does this do? .
     *
     * @see Logger
     * @param name
     * @param resourceBundleName
     */
    public Log(String name, String resourceBundleName) {
        super(name, resourceBundleName);
        // default to pass thru all levels, the FileHandlers will FilterByLevel

        this.setLevel(Level.ALL);
    }
    
    /**
     * Start a log in the logs/ directory with logFileName
     * @param logFileName name to log messages to.
     */
    public Log (String logFileName){
        this ("edu.ecs.csus.pc2", null, "logs", logFileName);
    }

    /**
     * Start log to logDirectory and logFilename.
     *
     * @see #setFileHandlers(String, String)
     * @param name
     * @param resourceBundleName
     * @param logDirectory
     * @param logFilename
     */
    public Log(String name, String resourceBundleName, String logDirectory,
            String logFilename) {
        super(name, resourceBundleName);
        // default to pass thru all levels, the FileHandlers will FilterByLevel
        // Logger.getLogger(arg0);
        this.setLevel(Level.ALL);
        setFileHandlers(logDirectory, logFilename);
    }

    // public static Log getLogger(String arg0, String arg1, String arg2)
    // {
    // logger = Logger.getLogger(arg0);
    // logger.setLevel(Level.ALL);
    // setFileHandlers(arg1, arg2);
    // return logger;
    // }
    /**
     * the Constructor
     */
    // public Log()
    // {
    // logger();
    // logger = logger.getLogger("edu.csus.ecs.pc2");
    // default to pass thru all levels, the FileHandlers will FilterByLevel
    // logger.setLevel(Level.ALL);
    // }
    // public void setLogDir(String strDirectory)
    // {
    // // logger = logger.getLogger("edu.csus.ecs.pc2");
    // // default to pass thru all levels, the FileHandlers will FilterByLevel
    // logger.setLevel(Level.ALL);
    // setFileHandlers(strDirectory);
    // }
    /**
     * This method starts the new FileHanders writing to logDirBase, and then removes the old handlers. Does nothing if we are
     * currently writing to logDirBase
     *
     * @param logDirBase
     *            base directory for log file.
     * @param baseFileName
     *            base name for log file, will append .log to name if .log not present
     */
    public void setFileHandlers(String logDirBase, String baseFileName) {
        // System.out.println("setFileHandlers before handlers =
        // "+getHandlers().length);
        if (baseFileName.endsWith(".log")) {
            baseFileName = baseFileName.substring(0, baseFileName.length() - 4);
        }
        // System.out.println("old="+oldLogDirBase+","+oldBaseFileName+"
        // new="+logDirBase+","+baseFileName);
        /*
         * save the list of existing handlers so we can remove them remove after we setup the new handlers
         */
        Handler[] existingHandlers = getHandlers();
        // System.out.println("setFileHandlers existingHandlers =
        // "+existingHandlers.length);
        FileHandler fh = null;
        try {
            File file = new File(logDirBase);
            // FilterByLevel filter = new FilterByLevel();
            // filter.setLevel(Level.ALL);
            if (!file.exists()) {
                file.mkdir();
            }
            fh = new FileHandler(logDirBase + File.separator + baseFileName
                    + "-%u.log", true);
            // System.out.println("writing to "+logDirBase + File.separator
            // + baseFileName + "-%u.log");
            fh.setLevel(Level.ALL);
            fh.setFormatter(new LogFormatter(true));
            addHandler(fh);

            if (streamHandler == null) {
                streamHandler = new LogStreamHandler();
                streamHandler.setFormatter(new LogFormatter(false));
                streamHandler.setLevel(Level.ALL);
                addHandler(streamHandler);
            }

        } catch (Exception e) {
            System.err.println("exception in setFileHandlers");
            e.printStackTrace();
            // TODO: handle exception
        }

        // setup the new handlers
        // FileHandler fh;
        // fh = setupFileHandler(Level.FINEST, logDirBase);
        // logger.addHandler(fh);
        // fh = setupFileHandler(Level.FINER, logDirBase);
        // logger.addHandler(fh);
        // fh = setupFileHandler(Level.FINE, logDirBase);
        // logger.addHandler(fh);
        // fh = setupFileHandler(Level.CONFIG, logDirBase);
        // logger.addHandler(fh);
        // fh = setupFileHandler(Level.INFO, logDirBase);
        // logger.addHandler(fh);
        // fh = setupFileHandler(Level.WARNING, logDirBase);
        // logger.addHandler(fh);
        // fh = setupFileHandler(Level.SEVERE, logDirBase);
        // logger.addHandler(fh);

        // System.out.println("setFileHandlers middle handlers =
        // "+getHandlers().length);
        // new handlers established, remove the old handlers

        for (Handler handler : existingHandlers) {
            if (handler instanceof FileHandler) {
                handler.close();
                removeHandler(handler);
            }
        }

        // System.out.println("setFileHandlers after handlers =
        // "+getHandlers().length);

        config("Now logging to " + logDirBase + " directory.");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        // System.out.println("finalize called");
        // log.config("finalize called in logger");
        // // close our handlers first
        close();
        super.finalize();
    }

    public void close() throws Throwable {
        // TODO why is this not being called and/or working?
        Handler[] handlers = getHandlers();
        closeHandlers(handlers);
    }

    /**
     * Takes an array of
     *
     * @link java.util.logging.Handler removes each handler from the logger and closes the handler.
     *
     * @param existingHandlers
     */
    private void closeHandlers(Handler[] existingHandlers) {
        // System.out.println("closeHandlers invoked");
        if (existingHandlers != null) {
            // System.out.println("existinghandlers is
            // "+existingHandlers.length);
            for (int i = 0; i < existingHandlers.length; i++) {
                // System.out.println("found a handler");
                if (existingHandlers[i] != null) {
                    // System.out.println("attempting to close
                    // "+existingHandlers.getClass().getName());
                    removeHandler(existingHandlers[i]);
                    existingHandlers[i].close();
                }
            }
        }

    }

    public LogStreamHandler getStreamHandler() {
        return streamHandler;
    }
}
