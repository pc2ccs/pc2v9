package test.edu.csus.ecs.pc2.core;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test driver for this package.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */
// $HeadURL$
public class AllTests {

    /**
     * svn id.
     */
    public static final String SVN_ID = "$Id$";

    // TODO is this main necessary?? dal
    public static void main(String[] args) {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core");
        // $JUnit-BEGIN$
        suite.addTestSuite(ClientIdTest.class);
        suite.addTestSuite(ElementIdTest.class);
        // $JUnit-END$
        return suite;
    }

    /**
     * Will throw an exception if called.
     */
    protected AllTests() {
        super();
        throw new UnsupportedOperationException(); // prevents calls from subclass
    }
}
