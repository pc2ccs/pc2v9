// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * Contest Clock.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintClockInfo extends APIAbstractTest {

    @Override
    public void printTest() {
        IContestClock clock = getContest().getContestClock();
        print("Clock:");
        print(" length=" + clock.getContestLengthSecs() + " (" + ContestTime.formatTime(clock.getContestLengthSecs()) + ")");
        print(" remaining=" + clock.getRemainingSecs() + " (" + ContestTime.formatTime(clock.getRemainingSecs()) + ")");
        print(" elapsed=" + clock.getElapsedSecs() + " (" + ContestTime.formatTime(clock.getElapsedSecs()) + ")");
        println();
    }

    @Override
    public String getTitle() {
        return "getContestClock";
    }
}
