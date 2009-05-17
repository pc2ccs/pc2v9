package edu.csus.ecs.pc2.api.implementation;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test all JUnits in core package.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.api.implementation");
        //$JUnit-BEGIN$
        suite.addTestSuite(RunImplementationTest.class);
        suite.addTestSuite(TeamImplementationTest.class);
        
        //$JUnit-END$
        return suite;
    }

}
