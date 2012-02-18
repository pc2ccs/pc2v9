package edu.csus.ecs.pc2.core.util;

import java.io.File;

import junit.framework.TestCase;

/**
 * A base TestCase case with utilities.
 * 
 * All PC^2 TestCase should use this to get the test directory names
 * for individual JUnits, will locate test directory under ({@link #DEFAULT_PC2_TEST_DIRECTORY}) 
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AbstractTestCase extends TestCase {

    private String testDataDirectory = null;

    public static final String DEFAULT_PC2_TEST_DIRECTORY = "testdata";
    
    /**
     * Get full path to test directory name.
     * 
     * @return
     */
    private String getTestDataDirectory() {
        if (testDataDirectory == null) {

            String projectPath = JUnitUtilities.locate(DEFAULT_PC2_TEST_DIRECTORY);
            if (projectPath == null) {
                projectPath = "."; //$NON-NLS-1$
                System.err.println("AbstractTestCase: Warning - unable to locate in project " + DEFAULT_PC2_TEST_DIRECTORY);
            }
            testDataDirectory = projectPath + File.separator + DEFAULT_PC2_TEST_DIRECTORY;
        }
        return testDataDirectory;
    }

    /**
     * Get Data directory for the JUnit.
     * 
     * Name will be {@link #getDataDirectory()} name and extending class
     * name.
     * 
     * @return
     */
    public String getDataDirectory() {
        return getTestDataDirectory() + File.separator + getShortClassName();
    }
    
    public String getDataDirectory(String testName) {
        return getTestDataDirectory() + File.separator + getShortClassName() + File.separator + testName;
    }
    

    /**
     * Get test case (JUnit) test file name (full path).
     * 
     * @param testJUnitName
     * @return
     */
    public String getTestFilename(String filename) {
        return getDataDirectory() + File.separator + filename;
    }
    
    /**
     * get this class name (without package).
     * @return
     */
    protected String getShortClassName() {
        String className = this.getClass().getName();
        
        int n = className.lastIndexOf('.');
        if ( n > 0) {
            return className.substring(n + 1);
        } else {
            return className;
        }
    }
}
