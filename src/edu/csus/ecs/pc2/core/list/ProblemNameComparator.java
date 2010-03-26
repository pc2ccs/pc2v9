package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Sort problems by display name order.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemNameComparator implements Comparator<Problem>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7320705781335549075L;

    public int compare(Problem problem1, Problem problem2) {
        return problem1.getDisplayName().compareTo(problem2.getDisplayName());
    }
}
