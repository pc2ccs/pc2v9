package edu.csus.ecs.pc2.core.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All JUnit tests for core.model package.
 * 
 * @author pc2@ecs.csus.edu
 * $version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.model");
        //$JUnit-BEGIN$
        suite.addTestSuite(TimeFormatTest.class);
        suite.addTestSuite(SiteTest.class);
        suite.addTestSuite(AccountListTest.class);
        suite.addTestSuite(ProblemTest.class);
        suite.addTestSuite(ProblemTest.class);
        //$JUnit-END$
        return suite;
    }

}
