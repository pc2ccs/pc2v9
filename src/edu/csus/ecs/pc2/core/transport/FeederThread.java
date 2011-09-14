package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.EventFeedXML;

/**
 * Thread to run Event Feeder Server.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
class FeederThread implements Runnable {

    private boolean debugFlag = true;

    private ServerSocket server;

    /**
     * Is thread running?.
     * Set to false to halt thread.
     */
    private boolean running = true;

    private IInternalContest contest;

    public FeederThread(ServerSocket server, IInternalContest contest) {
        super();
        this.server = server;
        this.contest = contest;
    }

    public void run() {

        while (running) {

            try {
                Socket connection = server.accept();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

                String eventFeedXML = getEventFeedInitial();
                out.write(eventFeedXML);

                out.close();
                connection.close();
                if (debugFlag) {
                    System.out.println("Opened and sent event feed.");
                }
            } catch (SocketException ex) {
                running = false; // NOP, needed to avoid CC warning.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (debugFlag) {
            System.out.println("FeederThread stopped");
        }
    }

    private String getEventFeedInitial() {

        EventFeedXML eventFeedXML = new EventFeedXML();

        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><contest><error>initiallized in getEventFeedInitial</error></contest>";
        try {
            xmlString = eventFeedXML.toXML(contest);
        } catch (IOException e) {
            e.printStackTrace(); // TODO CCS log this
            xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><contest><exception>" + e.getMessage() + "</exception></contest>";
        }

        return xmlString;
    }

    public boolean isRunning() {
        return running;
    }

    public void halt() {
        running = false;
    }
}
