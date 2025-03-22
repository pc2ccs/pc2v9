/**
 * 
 */
package edu.csus.ecs.pc2.core.strategies;

import edu.csus.ecs.pc2.core.IThrottleStrategy;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * This class implements an {@link IThrottleStrategy} which rejects every run.
 * This is primarily intended as a mechanism for testing the ThrottleStrategy mechanism,
 * since there isn't really any practical reason for rejecting all runs. 
 * 
 * @author John Clevenger
 *
 */
public class RejectAllStrategy implements IThrottleStrategy {

    @Override
    public boolean accept(Run run) {
        //reject every run
        return false;
    }

}
