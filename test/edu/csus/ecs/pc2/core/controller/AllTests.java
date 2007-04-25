package edu.csus.ecs.pc2.core.controller;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All tests for package controller.]
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.controller");
        //$JUnit-BEGIN$
        suite.addTestSuite(MultiSiteTest.class);
//        suite.addTestSuite(RunFlowTest.class);
        //$JUnit-END$
        return suite;
    }

}
