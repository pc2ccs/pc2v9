package edu.csus.ecs.pc2.core.export;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test all JUnits in core package.
 * 
 * @author pc2@ecs.csus.edu
 */

//$HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/test/edu/csus/ecs/pc2/core/AllTests.java $
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core");
        //$JUnit-BEGIN$
        suite.addTestSuite(ExportYAMLTest.class);
        //$JUnit-END$
        return suite;
    }

}
