package edu.csus.ecs.pc2.core.transport;

import java.util.Vector;

import edu.csus.ecs.pc2.core.model.SubmittedRun;

/**
 * A Quick transport.
 * 
 * Transport which only works in the same JVM.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class QuickTransport {

    public static final String SVN_ID = "$Id$";

    private TransportReceiver transportReceiverServer;

    private Vector<TransportReceiver> receiverList = new Vector<TransportReceiver>();
    
    public QuickTransport() {
    }
        

    public TransportReceiver getTransportReceiverServer() {
        return transportReceiverServer;
    }


    /**
     * Set the server transport
     * @param transportReceiverServer
     */
    public void setTransportReceiverServer(TransportReceiver transportReceiverServer) {
        this.transportReceiverServer = transportReceiverServer;
    }


    public QuickTransport(TransportReceiver transportReceiverServer, TransportReceiver transportReceiverClient) {
        super();
        this.transportReceiverServer = transportReceiverServer;
        addClientTransport(transportReceiverClient);
    }

    /**
     * Add a new client to transport list.
     * 
     * @param transportReceiver
     */
    private void addClientTransport(TransportReceiver transportReceiver) {
        receiverList.addElement(transportReceiver);
        System.out.println(" debug22 "+receiverList.size());
    }

    /**
     * Send to server.
     * @param submittedRun
     */
    public void sendToServer(SubmittedRun submittedRun) {
        transportReceiverServer.receiveSubmittedRun(submittedRun);
    }

    /**
     * Send to all clients.
     * @param submittedRun
     */
    public void sendToClient(SubmittedRun submittedRun) {
        for (int i = 0; i < receiverList.size() && i < 2; i++) {
            receiverList.elementAt(i).receiveNewRun(submittedRun);
        }
    }

}
