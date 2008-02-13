package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * API IContestClock implementation.  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO rename this class to ContestClockImplementation

// $HeadURL$
public class ContestTimeImplementation implements IContestClock {

    private ContestTime contestTime;
    
    private ElementId elementId;
    
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ContestTimeImplementation) {
            ContestTimeImplementation contestTimeImplementation = (ContestTimeImplementation) obj;
            return (contestTimeImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }
    
}
