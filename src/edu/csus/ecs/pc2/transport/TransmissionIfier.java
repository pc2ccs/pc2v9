package edu.csus.ecs.pc2.transport;

import edu.csus.ecs.pc2.core.SubmittedRun;

/**
 * A very very simple Transport.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TransmissionIfier {

    private TransportReceiver transportReceiverServer;

    private TransportReceiver transportReceiverClient;

    public TransmissionIfier(TransportReceiver transportReceiverServer, TransportReceiver transportReceiverClient) {
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
