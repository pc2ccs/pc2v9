// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.util.Calendar;

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

    /**
     * Returns the time at which the contest was first started, or null if the contest has never
     * been started.  Note that the returned time is never affected by any "pause" operations
     * which occur after the contest has been first started.
     */
    public Calendar getContestStartTime() {
        return contestTime.getContestStartTime();
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
    
    @Override
    public String toString() {
        String retStr = "ContestTimeImplementation (aka 'ContestClockImplementation'): [";
        retStr += "elementId:" + this.elementId + ";";
        retStr += "contestLengthSecs:" + contestTime.getContestLengthSecs() + ";";
        retStr += "elapsedSecs:" + contestTime.getElapsedSecs() + ";";
        retStr += "remainingSecs:" + contestTime.getRemainingSecs();
        retStr += "]";
        return retStr ;
        
    }
    
}
