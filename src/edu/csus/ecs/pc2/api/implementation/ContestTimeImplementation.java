package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * API IContestClock implementation.  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTimeImplementation implements IContestClock {

    private ContestTime contestTime;
    
    public ContestTimeImplementation(ContestTime contestTime) {
        this.contestTime = contestTime;
    }

    public long getRemainingSecs() {
        return contestTime.getRemainingSecs();
    }

    public long getContestLengthSecs() {
        return contestTime.getContestLengthSecs();
    }

    public long getElapsedSecs() {
        return contestTime.getElapsedSecs();
    }

    public boolean isContestClockRunning() {
        return contestTime.isContestRunning();
    }
}
