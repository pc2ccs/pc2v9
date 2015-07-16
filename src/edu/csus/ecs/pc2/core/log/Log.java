package edu.csus.ecs.pc2.core.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Based on the Java Logging API.
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
 * @version $Id$
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/log/Log.java$
public class Log extends Logger {

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

    public static final String LOG_DIRECTORY_NAME = "logs";

    private LogStreamHandler streamHandler = null;
    
    private Handler consoleHandler = null;
    
    private Handler fileHandler = null;
    
    private String logfilename = null;
    
    /**
     * Start a log in the logs/ directory with logFileName
     * @param logFileName name to log messages to.
     */
    public Log (String logFileName){
        this ("edu.ecs.csus.pc2", null, LOG_DIRECTORY_NAME, logFileName);
    }
    
    /**
     * Create/Start log in directoryName with logFileName.
     * @param directoryName
     * @param logFileName
     */
    public Log (String directoryName, String logFileName){
        this ("edu.ecs.csus.pc2", null, directoryName, logFileName);
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
        /**
         * default to pass thru all levels, the FileHandlers will FilterByLevel
         */
        // Logger.getLogger(arg0);
        this.setLevel(Level.ALL);
        setFileHandlers(logDirectory, logFilename);
    }

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
        if (baseFileName.endsWith(".log")) {
            baseFileName = baseFileName.substring(0, baseFileName.length() - 4);
        }

        /**
         * save the list of existing handlers so we can remove them remove after we setup the new handlers
         */
        Handler[] existingHandlers = getHandlers();
        try {
            File file = new File(logDirBase);
            // FilterByLevel filter = new FilterByLevel();
            // filter.setLevel(Level.ALL);
            if (!file.exists()) {
                file.mkdir();
            }
            logfilename = logDirBase + File.separator + baseFileName + "-%u.log";
            fileHandler = new FileHandler(logfilename, true);

            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new LogFormatter(true));
            addHandler(fileHandler);

            if (streamHandler == null) {
                streamHandler = new LogStreamHandler();
                streamHandler.setFormatter(new LogFormatter(false));
                streamHandler.setLevel(Level.ALL);
                addHandler(streamHandler);
            }

        } catch (IOException e) {
            System.err.println("IOexception in setFileHandlers "+e.getMessage());
        } catch (Exception e) {
            System.err.println("exception in setFileHandlers "+e.getMessage());
            e.printStackTrace();
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

        /** 
         * new handlers established, remove the old handlers
         */

        for (Handler handler : existingHandlers) {
            if (handler instanceof FileHandler) {
                handler.close();
                removeHandler(handler);
            }
        }

        String location = logDirBase;
        try {
            location = new File(logDirBase).getCanonicalPath();
        } catch (IOException e) {
            info("Unable to getCanonicalPath of "+logDirBase+":"+e.getMessage());
        }
        config("Now logging to " + location + " directory.");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
         // close our handlers first
        close();
        super.finalize();
    }

    public void close() throws Throwable {
        // SOMEDAY why is this not being called and/or working?
        // dal what does not working mean?   A better comment next time please.
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
        if (existingHandlers != null) {
            for (int i = 0; i < existingHandlers.length; i++) {
                if (existingHandlers[i] != null) {
                    removeHandler(existingHandlers[i]);
                    existingHandlers[i].close();
                }
            }
        }

    }
    
    public LogStreamHandler getStreamHandler() {
        return streamHandler;
    }
    
    /**
     * Start sending log message to console.
     * 
     * Adds a console handler to this logger.
     */
    public void startConsoleLogger(){
        
        if (consoleHandler == null){
            consoleHandler = new ConsoleHandler();
        }
        addHandler(consoleHandler);
    }
    
    /**
     * Stop sending log message to console.
     * 
     * @see #startConsoleLogger()
     */
    public void stopConsoleLogger(){
        if (consoleHandler != null){
            removeHandler(consoleHandler);
            consoleHandler.close();
            consoleHandler = null;
        }
    }
    
    public Logger getLogger(){
        return this;
    }
    
    public String getLogfilename() {
        return logfilename;
    }
}
