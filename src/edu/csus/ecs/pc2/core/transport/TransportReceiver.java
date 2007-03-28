package edu.csus.ecs.pc2.core.transport;

import edu.csus.ecs.pc2.core.model.SubmittedRun;

/**
 * Basis for {@link edu.csus.ecs.pc2.core.transport.QuickTransport}.
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
