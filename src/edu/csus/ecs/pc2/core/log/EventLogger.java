package edu.csus.ecs.pc2.core.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * A Event Logger, writes and reads LogEvents.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

public class EventLogger {

    /**
     * Name of file being written to.
     */
    private String outputLogFilename;

    /**
     * 
     */
    private String inputLogFilename;

    /**
     * The output stream/file.
     */
    private ObjectOutputStream objectOutputStream = null;

    /**
     * The output file stream.
     */
    private FileOutputStream fileOutputStream = null;

    /**
     * The input object stream.
     */
    private ObjectInputStream objectInputStream = null;

    /**
     * The input file stream.
     */
    private FileInputStream fileInputStream = null;

    /**
     * Append or write event to output file.
     * 
     * @param logEvent
     * @throws Exception
     */
    public void writeEvent(LogEvent logEvent) throws Exception {
        appendObjectToLog(logEvent);
    }

    private boolean appendObjectToLog(Serializable serializable) throws Exception {
        if (objectOutputStream == null) {
            throw new Exception("EventLog is not open for writing");
        }
        objectOutputStream.writeObject(serializable);
        objectOutputStream.flush();
        return true;
    }

    /**
     * 
     * @param filename
     * @param forInput
     *            if true opens a log file to read, if false opens to write.
     * @throws Exception
     * @see #openLogFile(String)
     */
    public void openLogFile(String filename, boolean forInput) throws Exception {
        if (forInput) {
            openLogFileForInput(filename);
        } else {
            openLogFile(filename);
        }
    }

    /**
     * Open log file to read.
     * 
     * @param filename
     * @throws Exception
     */
    private void openLogFileForInput(String filename) throws Exception {
        // TODO Auto-generated method stub
        if (objectInputStream != null) {
            objectInputStream.close();
            objectInputStream = null;
            fileInputStream = null;
        }

        if (new File(filename).exists()) {
            fileInputStream = new FileInputStream(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            inputLogFilename = filename;
        } else {
            throw new Exception("File " + filename + " does not exist ");
        }
    }

    /**
     * Open log file for write.
     * 
     * @param filename
     * @throws IOException
     */
    public void openLogFile(String filename) throws IOException {

        if (objectOutputStream != null) {
            objectOutputStream.close();
            objectOutputStream = null;
        }

        if (new File(filename).exists()) {
            // Open for append
            fileOutputStream = new FileOutputStream(filename, true);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            outputLogFilename = filename;
        } else {
            fileOutputStream = new FileOutputStream(filename);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            outputLogFilename = filename;
        }
    }

    /**
     * The name of the opened log file that is being read.
     * 
     * @return file name
     */
    public String getInputLogFilename() {
        return inputLogFilename;
    }

    /**
     * The name of the file which is opened and being written to.
     * 
     * @return file name
     */
    public String getOutputLogFilename() {
        return outputLogFilename;
    }

    /**
     * Read minNumberToRead LogEvents from input log.
     * 
     * @param minNumberToRead -
     *            mininum number of records to read.
     * @return array of LogEvent
     * @throws Exception
     */
    public LogEvent[] readLogEvents(long minNumberToRead) throws Exception {

        if (objectInputStream == null) {
            throw new Exception("Log not opened for input, must open log for input before attempting to read");
        }

        Vector<LogEvent> events = new Vector<LogEvent>();
        long numRead = 0;

        if (minNumberToRead < 1) {
            minNumberToRead = Long.MAX_VALUE;
        }

        Object object = null;
        do {
            try {
                object = objectInputStream.readObject();
            } catch (java.io.EOFException eof) {
                object = null;
            }
            if (object != null && object instanceof LogEvent) {
                events.addElement((LogEvent) object);
            } else if (object != null) {
                throw new Exception("Found object " + object.getClass().getName() + " expecting LogEvent in file " + inputLogFilename);
            }
            numRead++;
        } while (object != null && numRead < minNumberToRead);

        return (LogEvent[]) events.toArray(new LogEvent[events.size()]);
    }

    /**
     * Closes all opened files.
     * 
     * @throws IOException
     */
    public void closeAll() throws IOException {

        if (objectInputStream != null) {
            objectInputStream.close();
            objectInputStream = null;
            fileInputStream = null;
        }

        if (objectOutputStream != null) {
            objectOutputStream.flush();
            objectOutputStream.close();
            objectOutputStream = null;
            fileOutputStream = null;
        }
    }

}
