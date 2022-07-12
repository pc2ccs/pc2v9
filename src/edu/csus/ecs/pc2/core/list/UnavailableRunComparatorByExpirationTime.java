// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.UnavailableRun;

/**
 * Unavailable Run Comparator -- compares Unavailable Run objects based on their expiration times.
 *
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 */

public class UnavailableRunComparatorByExpirationTime implements Comparator<UnavailableRun>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(UnavailableRun run1, UnavailableRun run2) {
        
        return Long.compare(run1.getExpirationTimeInSecs(), run2.getExpirationTimeInSecs());
    }
}
