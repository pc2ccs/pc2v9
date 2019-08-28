// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.ui.FrameUtilities.HorizontalPosition;
import edu.csus.ecs.pc2.ui.FrameUtilities.VerticalPosition;

/**
 * Test the frame
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class ProblemGroupPaneTest extends AbstractTestCase {

    /**
     * Unit testing for enable/disable checkboxes in ProblemGroupPane.
     * @throws Exception
     */
    public void testProblemPane() throws Exception {

        setDebugMode(false);
        // setDebugMode(true);  // set this to true before checking in 

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        sample.assignSampleGroups(contest, "One", "Two");

        assertNotNull(contest);
        assertEquals(2, contest.getGroups().length);

        if (isDebugMode()) {

            IInternalController controller = sample.createController(contest, true, false);

            EditProblemFrame editProbleFrame = new EditProblemFrame();
            editProbleFrame.setContestAndController(contest, controller);

            JPanePlugin pane;
            boolean useProbGroupPane = false;
            useProbGroupPane = true;
            
            if (useProbGroupPane) {

                ProblemGroupPane problemGroupPane = new ProblemGroupPane();
                EditProblemPane editPane = new EditProblemPane();
                problemGroupPane.setContestAndController(contest, controller);
                problemGroupPane.setParentPane(editPane);

                Problem problem = contest.getProblems()[0];
                problemGroupPane.setProblem(problem);

                pane = problemGroupPane;

            } else {
                /**
                 * Test frame for even more granular testing of checkboxes.
                 */
                TestingFrameTwo temp = new TestingFrameTwo();
                pane = temp;
            }

            TestingFrame frame = new TestingFrame(pane);
            frame.setSize(new Dimension(600, 600));
            FrameUtilities.setFramePosition(frame, HorizontalPosition.LEFT, VerticalPosition.TOP);
            frame.setVisible(true);

            if (isDebugMode()){
                int seconds = 240;
                System.err.println("testProblemPane: Sleeping for "+seconds+" seconds.");
                Thread.sleep(seconds * Constants.MS_PER_SECOND);
            }
        }
    }
}
