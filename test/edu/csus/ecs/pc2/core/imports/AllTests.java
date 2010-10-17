package edu.csus.ecs.pc2.core.imports;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author PC2
 *
 */
public final class AllTests {
    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.imports");
        //$JUnit-BEGIN$
        suite.addTestSuite(LoadAccountsTest.class);
        suite.addTestSuite(LoadICPCDataTest.class);
        suite.addTestSuite(ContestXMLTest.class);
        //$JUnit-END$
        return suite;
    }

}
