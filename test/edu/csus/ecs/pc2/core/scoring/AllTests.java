package edu.csus.ecs.pc2.core.scoring;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All tests for scoring package. 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.scoring");
        //$JUnit-BEGIN$
        suite.addTestSuite(DefaultScoringAlgorithmTest.class);
        suite.addTestSuite(NewScoringAlgorithmTest.class);
        suite.addTestSuite(NewScoringAlgorithmDFATest.class);
        suite.addTestSuite(NewScoringAlgorithmTestLong.class);
        //$JUnit-END$
        return suite;
    }

}
