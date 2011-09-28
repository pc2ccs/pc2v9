package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.net.ServerSocket;

import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Event Feed Server.
 * 
 * Module to start a {@link FeederThread} socket server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedServer {

    private boolean debugFlag = true;

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
    public void startSocketListener(int port, IInternalContest contest) throws IOException {

        if (server != null) {
            throw new IOException("EventFeedServer already running (on port " + portUsed + ")");
        }

        this.portUsed = port;

        server = new ServerSocket(portUsed);

        if (debugFlag) {
            System.out.println("Started socket on port " + portUsed);
        }

        feederThread = new FeederThread(server, contest);
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
        }
    }

    public int getPort() {
        return portUsed;
    }
}
