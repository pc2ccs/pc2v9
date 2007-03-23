package edu.csus.ecs.pc2.transport;

import edu.csus.ecs.pc2.core.SubmittedRun;

/**
 * Basis for {@link edu.csus.ecs.pc2.transport.TransmissionIfier}.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface TransportReceiver {

    /**
     * Get a run from another module, very similar to receivePacket().
     * @param submittedRun
     */
    void receiveSubmittedRun(SubmittedRun submittedRun);

}
