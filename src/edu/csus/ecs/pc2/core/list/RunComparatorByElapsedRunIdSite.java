// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Run;

/**
 * Run Comparator, Order the runs by elapsed time (ms), run id, then site.
 *
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

public class RunComparatorByElapsedRunIdSite implements Comparator<Run>, Serializable {

    private static final long serialVersionUID = 1629965664816187817L;

    public int compare(Run run1, Run run2) {

        if (run1.getElapsedMS() == run2.getElapsedMS()) {
            
            if (run1.getNumber() == run2.getNumber()) {

                return Long.compare(run1.getSiteNumber(), run2.getSiteNumber());

            } else {
                return Long.compare(run1.getNumber(), run2.getNumber());
            }
        } else {
            return Long.compare(run1.getElapsedMS(), run2.getElapsedMS());
        }
    }
}
