package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedReader;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Collects Input and Output for a process.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IOCollector extends Thread {

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
        int charCount;
        char[] cbuf = new char[32768];

        try {
            
            charCount = bufReader.read(cbuf);
            while ((!stopIt) && // Stopped when someone hit terminate button
                    (charCount != -1) && // not EOF
                    (offset < (maxFileSize)) // over max size
            ) {
                offset++;
                outWriter.write(cbuf, 0, charCount);
                charCount = bufReader.read(cbuf);
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
                charCount = bufReader.read(cbuf);
                while ((!stopIt) && // Stopped when someone hit terminate button
                        (charCount != -1) // not EOF
                ) {
                    charCount = bufReader.read(cbuf);
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
