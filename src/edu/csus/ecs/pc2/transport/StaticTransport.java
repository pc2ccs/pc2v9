package edu.csus.ecs.pc2.transport;

import edu.csus.ecs.pc2.core.SubmittedRun;

/**
 * A Static implementation of a simple transport.
 * 
 * This class registers holds a single instance of {@link edu.csus.ecs.pc2.transport.TransmissionIfier}.
 * 
 * @author Douglas A. Lane
 * 
 */
public final class StaticTransport {

    private StaticTransport() {

    }

    private static TransmissionIfier transmissionIfier;

    public static void sendToServer(SubmittedRun submittedRun) {
        transmissionIfier.sendToServer(submittedRun);
    }

    public static void sendToClient(SubmittedRun submittedRun) {
        transmissionIfier.sendToClient(submittedRun);
    }

    public static TransmissionIfier getTransmissionIfier() {
        return transmissionIfier;
    }

    public static void setTransmissionIfier(TransmissionIfier transmissionIfier) {
        StaticTransport.transmissionIfier = transmissionIfier;
    }
}
