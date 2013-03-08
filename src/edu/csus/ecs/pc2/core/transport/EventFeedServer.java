package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.net.ServerSocket;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Event Feed Server.
 * 
 * This server listens on the input port and when a connection is made
 * it creates a service that for each contest event will send CCS Event Feed XML 
 * to the client.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedServer {

    private FeederThread feederThread = null;

    private ServerSocket server = null;

    private boolean started = false;

    private int portUsed;

    /**
     * Stars and binds a event feed server to a socket.
     * 
     * @param port
     * @param contest
     * @throws IOException
     */
    public void startSocketListener(int port, IInternalContest contest, boolean filteredFeed) throws IOException {

        if (server != null) {
            throw new IOException("EventFeedServer already running (on port " + portUsed + ")");
        }

        this.portUsed = port;

        server = new ServerSocket(portUsed);

        if (Utilities.isDebugMode()) {
            log("Started socket on port " + portUsed);
        }

        feederThread = new FeederThread(server, contest, filteredFeed);
        new Thread(feederThread).start();

        started = true;
    }

    /**
     * Is Event Feed Server listening/running?.
     * 
     * @return
     */
    public boolean isListening() {
        if (started) {
            return feederThread.isRunning();
        } else {
            return false;
        }
    }

    /**
     * Halt Event Feed Server.
     * @throws IOException 
     */
    public void halt() throws IOException {
        if (started) {
            feederThread.halt();
            feederThread = null;
            server.close();
            server = null;
            started = false;
            if (Utilities.isDebugMode()) {
                log("Stopped socket on port " + portUsed);
            }
        }
    }

    private void log(String string) {
        System.out.println("debug - "+string);
        StaticLog.info(string);
    }

    public int getPort() {
        return portUsed;
    }
}
