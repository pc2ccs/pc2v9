package edu.csus.ecs.pc2.core.transport;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.report.EventFeedXML;

/**
 * Event Feed Server.
 * 
 * Module to start a socket server on a port to send event
 * feed XML to client.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedServer {

    private boolean listening = true;
    
    private boolean debugFlag = true;

    /**
     * Stars and binds a event feed server to a socket.
     * @param port
     * @param contest
     * @throws IOException
     */
    public void startSocketListener(int port, IInternalContest contest) throws IOException {

        ServerSocket server = new ServerSocket(port);
        
        if (debugFlag){
            System.out.println("Started socket on port "+port);
        }

        listening = true;
        
        while (listening) {

            try {
                Socket connection = server.accept();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                
                String eventFeedXML = getEventFeedInitial(contest);
                out.write(eventFeedXML);
                
                out.close();
                connection.close();
                if (debugFlag){
                    System.out.println("Opened and sent event feed.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getEventFeedInitial(IInternalContest contest) {
        
        EventFeedXML eventFeedXML = new EventFeedXML();
        
        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><contest><error>initiallized in getEventFeedInitial</error></contest>";
        try {
            xmlString = eventFeedXML.toXML(contest);
        } catch (IOException e) {
            e.printStackTrace(); // TODO CCS log this
            xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><contest><exception>"+e.getMessage()+"</exception></contest>";
        }
        
        return xmlString;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    /**
     * Halt socket server/listener.
     * 
     */
    public void halt() {
        listening = false;
    }
}
