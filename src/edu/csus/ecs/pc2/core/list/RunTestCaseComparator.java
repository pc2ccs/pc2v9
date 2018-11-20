package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.RunTestCase;

/**
 * Comparator, order by {@link RunTestCase#getTestNumber()}.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunTestCaseComparator implements Comparator<RunTestCase>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1320110133092277426L;

    public int compare(RunTestCase one, RunTestCase two) {
        return one.getTestNumber() - two.getTestNumber();
    }

}
