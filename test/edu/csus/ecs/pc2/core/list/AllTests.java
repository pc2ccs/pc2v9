package edu.csus.ecs.pc2.core.list;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test all JUnits in core.list package.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.list");
        //$JUnit-BEGIN$
        suite.addTestSuite(LoginListTest.class);
        suite.addTestSuite(ClarificationListTest.class);
        suite.addTestSuite(RunListTest.class);
        suite.addTestSuite(RunResultsFileListTest.class);
        suite.addTestSuite(RunFilesListTest.class);
        //$JUnit-END$
        return suite;
    }

}
