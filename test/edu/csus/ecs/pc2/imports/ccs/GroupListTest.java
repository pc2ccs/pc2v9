package edu.csus.ecs.pc2.imports.ccs;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;

/**
 * Test for GroupList.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: GroupListTest.java 178 2011-04-08 19:33:23Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/imports/ccs/GroupListTest.java $
public class GroupListTest extends TestCase {

    private String testDirectory = ".";

    protected void setUp() throws Exception {
        super.setUp();

        String testDir = "testdata" + File.separator + "ccs";
        String projectPath = JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate " + testDir);
        }

        testDirectory = projectPath + File.separator + testDir;
    }

    private String findTestDataFile(String filename) throws IOException {

        String name = testDirectory + File.separator + filename;

        if (new File(name).exists()) {
            return name;
        } else {
            System.err.println("could not find " + name);
            throw new IOException("Unable to locate " + name);
        }
    }

    public void testLoad() throws Exception {

        String groupFile = "groups.tsv";
        Group[] groups = new GroupList().loadlist(findTestDataFile(groupFile));

        assertEquals("Expected # groups in " + groupFile, 6, groups.length);

    }

}
