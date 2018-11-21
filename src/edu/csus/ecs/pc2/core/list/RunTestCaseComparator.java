package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.RunTestCaseResult;

/**
 * Comparator, order by {@link RunTestCaseResult#getTestNumber()}.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunTestCaseComparator implements Comparator<RunTestCaseResult>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1320110133092277426L;

    public int compare(RunTestCaseResult one, RunTestCaseResult two) {
        return one.getTestNumber() - two.getTestNumber();
    }

}
