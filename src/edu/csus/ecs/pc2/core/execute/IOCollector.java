// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import edu.csus.ecs.pc2.core.log.Log;

/**
 * Collects Input and Output for a process.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class IOCollector extends Thread {

    private BufferedInputStream bufReader;

    private BufferedOutputStream outWriter;

    private boolean stopIt = false;

    @SuppressWarnings("unused")
    private ExecuteTimer localTimer = null;

    private long maxFileSize;

    private Log log = null;

    private static final String NL = System.getProperty("line.separator");
    
    /**
     * 
     * @param log
     * @param inReader
     * @param printer
     * @param myTimer
     * @param newMaxFileSize maximum number of bytes that are read.
     */
    public IOCollector(Log log, BufferedInputStream inReader, BufferedOutputStream printer, ExecuteTimer myTimer, long newMaxFileSize) {
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
        int c;
        byte[] cbuf = new byte[32768];

        try {
            
            c = bufReader.read(cbuf);
            //read input from the specified input stream until either something invokes haltme() (e.g. operator hits "Terminate" button),
            // EOF is reached, or the input size from the stream exceeds maxFileSize
            while ((!stopIt) && // Stopped when someone hit terminate button
                    (c != -1) && // not EOF
                    (offset < (maxFileSize)) // not over max size
            ) {
                offset += c;
                outWriter.write(cbuf, 0, c);
                c = bufReader.read(cbuf);
            }

            if (stopIt) {
                outWriter.write((NL+"Output halted by operator"+NL).getBytes());
            }

            if (offset >= (maxFileSize)) {
                outWriter.write((NL+"Output exceeds maximum file size " + new Long(maxFileSize).toString()+NL).getBytes());
            }
            
            //we only get here when the above loop terminates (EOF was reached, stopIt was true, or maxFileSize was reached).
            //since there might still be data in the input stream, we read the rest of it (and discard it)
            try {
                c = bufReader.read(cbuf);
                while ((!stopIt) && // Stopped when someone hit terminate button
                        (c != -1) // not EOF
                ) {
                    c = bufReader.read(cbuf);
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
