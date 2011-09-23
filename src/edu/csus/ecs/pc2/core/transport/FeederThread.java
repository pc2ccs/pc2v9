package edu.csus.ecs.pc2.core.transport;

import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Event Feeder Thread.
 * 
 * Create connection to socket, start thread {@link EventFeeder} that
 * sends event feed XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
class FeederThread implements Runnable {

    private boolean debugFlag = true;

    private ServerSocket server;

    private EventFeeder eventFeeder = null;

    /**
     * Is thread running?. Set to false to halt thread.
     */
    private boolean running = true;

    private IInternalContest contest;

    public FeederThread(ServerSocket server, IInternalContest contest) {
        super();
        this.server = server;
        this.contest = contest;
    }

    public void run() {

        try {
            Socket connection = server.accept();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

            eventFeeder = new EventFeeder(contest, out);
            new Thread(eventFeeder).start();

            if (debugFlag) {
                System.out.println("Opened and sent event feed.");
            }
        } catch (SocketException ex) {
            running = false; // NOP, needed to avoid CC warning.
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (debugFlag) {
            System.out.println("FeederThread done");
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void halt() {
        running = false;
        if (eventFeeder != null) {
            eventFeeder.halt();
        }
    }
}
