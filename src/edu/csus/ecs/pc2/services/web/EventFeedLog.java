package edu.csus.ecs.pc2.services.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Event fee log.
 * 
 * Contains all event feed log entries for current contest.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedLog {

    private String[] fileLines = new String[0];

    private FileOutputStream outStream;

    private static String logsDirectory = Log.LOG_DIRECTORY_NAME;

    private String logFileName = null;

    private String filename;

    private long old_file_size;

    /**
     * Load all events from events log
     * 
     * @param contest
     * @throws FileNotFoundException
     */
    public EventFeedLog(IInternalContest contest) throws FileNotFoundException {

        filename = getEventFeedLogName(contest.getContestIdentifier());
        setLogFileName(filename);

        // First read log

        readLog();

        // open log file for write/append
        outStream = new FileOutputStream(filename, true);
    }

    private void readLog() {
        try {
            fileLines = Utilities.loadFile(filename);
            old_file_size = new File(filename).length();
            System.out.println("debug 22 Loaded " + fileLines.length + " events from " + getLogFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEventFeedLogName(String id) {
        return logsDirectory + File.separator + "EventfeedLog_" + id + ".log";
    }

    public String[] getLogLines() {
        synchronized (filename) {
            long new_file_size = new File(filename).length();
            // only need to re-read log if the file size changed
            if (new_file_size != old_file_size) {
                // this will also update old_file_size
                readLog();
            }
        }
        return fileLines;
    }

    /**
     * Append event to event log.
     * 
     * @param eventString
     * @throws IOException
     */
    void writeEvent(String eventString) throws IOException {
        outStream.write(eventString.getBytes());
        outStream.flush();
        System.out.println("debug 22 writeEvent " + eventString);
    }

    public static void setLogsDirectory(String logsDirectory) {
        EventFeedLog.logsDirectory = logsDirectory;
    }

    void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public String getLogFileName() {
        return logFileName;
    }

    public void close() throws IOException {
        outStream.flush();
        outStream.close();
    }

}
