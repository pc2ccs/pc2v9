package config;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * This class provides Logging services for the WTI-API project.  Clients can obtain an instance of the Logging class
 * by invoking method {@link #getLogger()}, which returns a singleton {@link LoggingOld} object.
 * The class uses an underlying {@link java.util.logging.Logger} to handle the actual logging operations.
 * 
 * @author John Clevenger, PC^2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class LoggingOld {

	//the singleton Logging object (an instance of this class)
	static private LoggingOld logger = null;
	
	//the underlying Java logger
	private java.util.logging.Logger javaLogger;
	
	//private constructor to enforce singleton (call static getLogger() to obtain an instance of this class)
	private LoggingOld() {	}
	
	//the console logger to be used if/when console logging is enabled
	private ConsoleHandler consoleHandler;
	
	//flag to indicate whether console logging should be initially enabled
	public static boolean enableConsoleLoggingAtStartup = false;
	
	/**
	 * Record a log message in the log file at the "Info" level.
	 * @param msg the log message to record
	 */
	public void logInfo(String msg) {
		this.javaLogger.log(Level.INFO, msg);
	}
	
	/**
	 * Record a log message in the log file at the "SEVERE" (Error) level.
	 * @param msg the log message to record
	 */
	public void logError(String msg) {
		this.javaLogger.log(Level.SEVERE, msg);
	}
	
	/**
	 * Record a log message in the log file at the "WARNING" level.
	 * @param msg the log message to record
	 */
	public void logWarning(String msg) {
		this.javaLogger.log(Level.WARNING, msg);
	}
	
	/**
	 * By default, log messages are sent only to the Logger's <I>log file</i>; invoking this method
	 * causes the Logger to begin sending log messages to the console as well as to the log file.
	 */
	public void enableConsoleLogging() {
		if (consoleHandler==null) {
			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MyFormatter());
		}
		javaLogger.addHandler(consoleHandler);
	}
	
	/**
	 * Disables (turns off) logging to the console.  Console logging is off by default; see
	 * {@link LoggingOld#enableConsoleLogging} to turn logging on.
	 * This method has no effect if console logging is off when the method is called.
	 */
	public void disableConsoleLogging() {
		javaLogger.removeHandler(consoleHandler);
	}
	
	/**
	
	/**
	 * Returns a singleton instance of the {@link LoggingOld} class. The returned Logging object uses
	 * an underlying {@link java.util.logging.Logger} to do the actual logging.  
	 *  
	 * @return a {@link LoggingOld} object on which logging operations can be invoked
	 */
	static public LoggingOld getLogger() {
		if (logger==null) {
			logger = new LoggingOld();
			logger.javaLogger = java.util.logging.Logger.getLogger("WTILog");
			logger.javaLogger.setUseParentHandlers(false); //disables the default console logger in the root logger
			logger.setLogFile();
			if (enableConsoleLoggingAtStartup) {
				logger.enableConsoleLogging();
			}
		}
		return logger;
	}

	/**
	 * Sets a file as the place where logging will occur.
	 * Currently the log file is hard-coded as "logs/WTI.log"; a more flexible approach would be to
	 * provide support for selectable directory/file names. This would however be complicated by
	 * the static singleton nature of the class vs. the need to return an instance of the class.
	 */
	private void setLogFile() {
		try {
			boolean dirExists = insureDirectoryExists("logs");
			if (dirExists) {
				FileHandler fh = new java.util.logging.FileHandler("logs/WTI.log");
				fh.setFormatter(new MyFormatter());
				this.javaLogger.addHandler(fh);
			} else {
				throw new IOException("Could not create logs directory");
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Insure that a given directory exists.
     * 
     * Will use File.mkdirs() if needed to create directory.
     * 
     * @param directoryName the name of the directory to insure exists
     * 
     * @return true if the directory either already existed or has been created; false otherwise
     */
    private boolean insureDirectoryExists(String directoryName) {
    	File name = new File(directoryName);
    	
    	boolean success;
    	if (name.exists()) {
    		if (name.isDirectory()) {
    			//the directory already exists; we're done
    			success = true;
    		} else {
    			//there is already a FILE by that name which is not a directory; can't create directory
    			success = false ;
    		}
    	} else {
    		//the name doesn't exist at all; try creating it as a directory
	        success = name.mkdirs();
    	}
        
        return success;
    }
    
    /**
     * A class for formatting log records.  To change the format of log entries
     * for WTI-API logs, simply change the String returned by method format().
     * 
     * @author John Clevenger, PC^2 Development Team (pc2@ecs.csus.edu)
     *
     */
    public class MyFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
        	
        	//pull the original caller out of the stack (since the LogRecord will have the Java Logger's caller
        	// info, which will be the WTI Logging object's methods)
        	//Note that THIS WILL HAVE TO BE CHANGED if any change is made to the method calling sequence 
        	// anywhere within this Logging class (or within the java.util.logging classes for that matter...)
        	Exception ex = new Exception();
        	StackTraceElement[] stack = ex.getStackTrace();
			String callerClassName = stack[7].getClassName();
        	String callerMethodName = stack[7].getMethodName();
        	
            return 
            	new Date(record.getMillis()).toString() + "|"
            	+ record.getLevel() + "|"
            	+ "Thread " + record.getThreadID() + "|"
            	+ callerClassName + "|"
                + callerMethodName + "|"
                + record.getMessage()+"\n";
        }

    }

}
