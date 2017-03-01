package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test for Edit Filter Frame
 * 
 * @author Douglas A. Lane, PC^2 Team, &gt;pc2@ecs.csus.edu&lt;
 */

public class EditFilterFrameTest extends TestCase {

    public void testNull() throws Exception {
        
    }


    public static void main(String[] args) {

        final Filter filter = new Filter();

        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(3, 3, 12, 12, false);
        IInternalController controller = sampleContest.createController(contest, true, false);

        Runnable callback = new Runnable() {
            public void run() {
                System.out.println("Runnable - filter: " + filter);
            }
        };

        EditFilterFrame editFilterFrame = new EditFilterFrame(filter, "text Filter", callback);
        editFilterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFilterFrame.setContestAndController(contest, controller);
        editFilterFrame.setVisible(true);
    }

}
