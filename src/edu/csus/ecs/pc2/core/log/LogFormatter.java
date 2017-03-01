package edu.csus.ecs.pc2.core.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A simpler log formatter.
 *
 * Returns a log line in the following format: <br>
 * <code>YYMMDD HHMMSS &lt;level&gt; &lt;method_name&gt; message </code>
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LogFormatter extends Formatter {

    /**
     * Format for Date.
     */
    private SimpleDateFormat formatter = new SimpleDateFormat(
            "yyMMdd HHmmss.SSS");

    // private SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss z");

    /**
     * New line, OS specific.
     */
    private String separator = System.getProperty("line.separator");

    /**
     *
     */
    public LogFormatter(boolean useSeparator) {
        super();
        if (!useSeparator) {
            separator = "";
        }
        // TODO Auto-generated constructor stub
    }

    private String getDateString(Date inDate) {
        return formatter.format(inDate.getTime());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(LogRecord logRecord) {
        String level;
        if (logRecord.getLevel().toString().equalsIgnoreCase("finest")) {
            level = "DEBUG";
        } else {
            level = logRecord.getLevel().toString();
        }

        // TODO Added source class sans package

        Date date = new Date(logRecord.getMillis());
        String line = getDateString(date) + "|" + level + "|"
                + Thread.currentThread().getName() + "|"
                + logRecord.getSourceMethodName() + "|"
                + logRecord.getMessage() + separator;

        if (logRecord.getThrown() != null) {
            // Add throwable (stack track on exception) to log

            Throwable throwable = logRecord.getThrown();
            line = line + "|" + throwable.getClass().getName() + ": "
                    + throwable.getMessage() + separator;

            StackTraceElement[] elements = throwable.getStackTrace();
            for (StackTraceElement stackTraceElement : elements) {
                String sourceName = "(Unknown Source)";
                if (stackTraceElement.getFileName() != null) {
                    sourceName = "(" + stackTraceElement.getFileName() + ":"
                            + stackTraceElement.getLineNumber() + ")";
                }
                line = line + "|" + "    at "
                        + stackTraceElement.getClassName() + "." 
                        + stackTraceElement.getMethodName() + " " + sourceName
                        + separator;
            }
        }

        return line;
    }

}
