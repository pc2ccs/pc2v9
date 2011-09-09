package edu.csus.ecs.pc2.imports.ccs;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 *  Test Suite for core util.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id: AllTests.java 1562 2008-07-30 04:39:31Z laned $
 */

public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.util");
        //$JUnit-BEGIN$
        suite.addTestSuite(GroupListTest.class);
        suite.addTestSuite(TeamListTest.class);
        suite.addTestSuite(ContestYAMLLoaderTest.class);
        //$JUnit-END$
        return suite;
    }

}
