package edu.csus.ecs.pc2.core.transport;

import edu.csus.ecs.pc2.core.model.SubmittedRun;

/**
 * A Static implementation of a simple transport.
 * 
 * This class registers holds a single instance of {@link edu.csus.ecs.pc2.transport.QuickTransport}.
 * 
 * @author Douglas A. Lane
 * 
 */
//$HeadURL$

public final class StaticTransport {
    
    public static final String SVN_ID = "$Id$";

    private StaticTransport() {

    }

    private static QuickTransport quickTransport;

    public static void sendToServer(SubmittedRun submittedRun) {
        quickTransport.sendToServer(submittedRun);
    }

    public static void sendToClient(SubmittedRun submittedRun) {
        quickTransport.sendToClient(submittedRun);
    }

    public static QuickTransport getQuickTransport() {
        return quickTransport;
    }

    public static void setQuickTransport(QuickTransport inQuickTransport) {
        StaticTransport.quickTransport = inQuickTransport;
    }
}
