package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Run;

/**
 * Run Comparator, Order by elapsed, site number, team number
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunCompartorByElapsed implements Comparator<Run>, Serializable {

    private static final long serialVersionUID = 2539446475650710249L;

    public int compare(Run run1, Run run2) {

        long elapsed1 = run1.getElapsedMins();
        long elapsed2 = run2.getElapsedMins();

        if (elapsed1 == elapsed2) {
            if (run1.getSiteNumber() == run2.getSiteNumber()) {
                return run1.getNumber() - run2.getNumber();
            } else {
                return run1.getSiteNumber() - run2.getSiteNumber();
            }
        } else {
            return (int) (elapsed1 - elapsed2);
        }

    }

}
