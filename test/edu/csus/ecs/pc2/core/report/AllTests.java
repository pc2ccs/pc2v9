package edu.csus.ecs.pc2.core.report;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All JUnit tests for core.model package.
 * 
 * @author pc2@ecs.csus.edu
 * $version $Id: AllTests.java 2044 2010-03-12 07:15:09Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/test/edu/csus/ecs/pc2/core/model/AllTests.java $
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.report");
        //$JUnit-BEGIN$
        suite.addTestSuite(ExportYamlReportTest.class);
        suite.addTestSuite(ExtractorTest.class);
        suite.addTestSuite(ExtractRunsTest.class);
        //$JUnit-END$
        return suite;
    }

}
