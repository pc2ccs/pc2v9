package edu.csus.ecs.pc2.core.strategies;

import edu.csus.ecs.pc2.core.IThrottleStrategy;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * This class implements an {@link IThrottleStrategy} which accepts every run.
 * This is primarily intended as a mechanism for testing the ThrottleStrategy mechanism,
 * but it could also be used as a default implementation where "no throttling" is desired.
 * 
 * @author John Clevenger
 *
 */
public class AcceptAllStrategy implements IThrottleStrategy {

    @Override
    public boolean accept(Run run) {
        //accept every run
        return true;
    }

}
