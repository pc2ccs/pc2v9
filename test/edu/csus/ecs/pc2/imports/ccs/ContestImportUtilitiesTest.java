// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.util.ArrayList;

import edu.csus.ecs.pc2.core.exception.YamlLoadException;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Test the Contest Import Utilities
 * 
 * @author John Buck
 *
 */
public class ContestImportUtilitiesTest extends AbstractTestCase {
    private String loadDir = "testdata" + File.separator;

    protected void setUp() throws Exception {
        String projectPath = JUnitUtilities.locate(loadDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + loadDir);
        }
        File dir = new File(projectPath + File.separator + loadDir);
        if (dir.exists()) {
            loadDir = dir.toString() + File.separator;
        } else {
            System.err.println("could not find " + loadDir);
            throw new Exception("Unable to locate " + loadDir);
        }
        super.setUp();
    }
    
    /**
     * Tests whether parsing of the testdata.yaml files work
     * 
     * @throws Exception
     */
    public void testgetTestCaseFileNames() throws Exception {
        String inputTestDirectory = getDataDirectory(this.getName()) + File.separator;
        
        ArrayList<TestCaseInfo> secret0 = ContestImportUtilities.getTestCaseFileNames(inputTestDirectory);
        assertEquals("Expecting judge datafiles ", 0, secret0.size());
        
        ArrayList<TestCaseInfo> secret1 = ContestImportUtilities.getTestCaseFileNames(inputTestDirectory + "secret1");
        assertEquals("Expecting judge datafiles for secret1 ", 34, secret1.size());
        
        try {
            ArrayList<TestCaseInfo> secret2 = ContestImportUtilities.getTestCaseFileNames(inputTestDirectory + "secret2");
            failTest("Expecting missing answer file, but none are missing for secret2");
        } catch (YamlLoadException e) {
            assertEquals("Expecting missing answer ", "Missing answer file 02-loop-jump.ans for input file 02-loop-jump.in", e.getMessage());
        }
    }

}
