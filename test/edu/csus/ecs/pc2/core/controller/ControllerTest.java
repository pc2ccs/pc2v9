package edu.csus.ecs.pc2.core.controller;

import java.io.File;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test for Controller.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ControllerTest extends TestCase {

    private IContest contest;

    private IController controller;

    protected void setUp() throws Exception {
        super.setUp();
        SampleContest sampleContest = new SampleContest();
        contest = sampleContest.createContest(2, 4, 12, 6, true);
        controller = sampleContest.createController(contest, true, false);

        String loadFile = "pc2v9.ini";
        File dir = new File(loadFile);
        if (!dir.exists()) {
            // TODO, try to find this path in the environment
            dir = new File("projects" + File.separator +"pc2v9" + File.separator + loadFile);
            if (!dir.exists()) {
                System.err.println("could not find " + loadFile);
            } else {
                loadFile = dir.getAbsolutePath();
            }
        }

        // Add 22 random runs
        Run [] runs = sampleContest.createRandomRuns(contest, 22, true, true, true);
        sampleContest.addRuns(contest, runs, loadFile);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSecuritySet() {

        int level = controller.getSecurityLevel();
        controller.setSecurityLevel(level);
        assertEquals("Expected " + level + " got " + controller.getSecurityLevel(), controller.getSecurityLevel(), level);

        level = Controller.SECURITY_HIGH_LEVEL;
        controller.setSecurityLevel(level);
        assertEquals("Expected " + level + " got " + controller.getSecurityLevel(), controller.getSecurityLevel(), level);

        level = Controller.SECURITY_NONE_LEVEL;
        controller.setSecurityLevel(level);
        assertEquals("Expected " + level + " got " + controller.getSecurityLevel(), controller.getSecurityLevel(), level);
    }
    
}
