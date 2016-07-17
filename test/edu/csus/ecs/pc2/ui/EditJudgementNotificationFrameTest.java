package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditJudgementNotificationFrameTest extends TestCase {
    
    
    public void testNull() throws Exception {
        
    }

    public static void main(String[] args) {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);
        IInternalController controller = sample.createController(contest, true, false);

        EditJudgementNotificationFrame frame = new EditJudgementNotificationFrame();
        frame.setContestAndController(contest, controller);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);

    }

}
