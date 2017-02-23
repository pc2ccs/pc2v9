package edu.csus.ecs.pc2.validator;

import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorTest;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test Suite validator.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.validator");
        // $JUnit-BEGIN$
        suite.addTestSuite(PC2ValidatorTest.class);
        suite.addTestSuite(ClicsValidatorTest.class);
        // $JUnit-END$
        return suite;
    }
}
