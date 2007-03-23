package edu.csus.ecs.pc2.transport;

import edu.csus.ecs.pc2.core.SubmittedRun;

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

    private TransportReceiver transportReceiverClient;

    public QuickTransport(TransportReceiver transportReceiverServer, TransportReceiver transportReceiverClient) {
        super();
        this.transportReceiverServer = transportReceiverServer;
        this.transportReceiverClient = transportReceiverClient;
    }

    public void sendToServer(SubmittedRun submittedRun) {
        transportReceiverServer.receiveSubmittedRun(submittedRun);
    }

    public void sendToClient(SubmittedRun submittedRun) {
        transportReceiverClient.receiveSubmittedRun(submittedRun);

    }

}
