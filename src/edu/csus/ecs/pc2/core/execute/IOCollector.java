package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedReader;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Collects Input and Output for a process.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class IOCollector extends Thread {

    public static final String SVN_ID = "$Id$";

    private BufferedReader bufReader;

    private PrintWriter outWriter;

    private boolean stopIt = false;

    @SuppressWarnings("unused")
    private ExecuteTimer localTimer = null;

    private long maxFileSize;

    private Log log = null;

    /**
     * 
     * @param log
     * @param inReader
     * @param printer
     * @param myTimer
     * @param newMaxFileSize maximum number of bytes that are read.
     */
    public IOCollector(Log log, BufferedReader inReader, PrintWriter printer, ExecuteTimer myTimer, long newMaxFileSize) {
        this.log = log;
        maxFileSize = newMaxFileSize;
        bufReader = inReader;
        outWriter = printer;
        localTimer = myTimer;
    }

    protected void finalize() throws Throwable {
        bufReader = null;
        outWriter = null;
        localTimer = null;
    }

    public void haltMe() {
        stopIt = true;
    }

    /*
     * Collects all output into outWriter
     */
    public void run() {
        long offset = 0;
        int theChar;

        try {
            theChar = bufReader.read();
            while ((!stopIt) && // Stopped when someone hit terminate button
                    (theChar != -1) && // not EOF
                    (offset < (maxFileSize)) // over max size
            ) {
                offset++;
                outWriter.print((char) theChar);
                theChar = bufReader.read();
            }

            if (stopIt) {
                outWriter.println();
                outWriter.println("Output halted by operator");
            }

            if (offset >= (maxFileSize)) {
                outWriter.println();
                outWriter.println("Output exceeds maximum file size " + new Long(maxFileSize));
            }

            try {
                theChar = bufReader.read();
                while ((!stopIt) && // Stopped when someone hit terminate button
                        (theChar != -1) // not EOF
                ) {
                    theChar = bufReader.read();
                }

            } catch (Exception notImportant) {
                log.log(Log.DEBUG, "IOCollector:Run: caught notImportant exception");
                // If there is an exception we are done reading, no need to note it.
            }

        } catch (Exception ex) {
            log.log(Log.CONFIG, "ioCollector - Exception in run() ", ex);
        }
    }

}
