package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import junit.framework.TestCase;

/**
 * Test program for Auto Judge Status Frame.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeStatusFrameTest extends TestCase {
    
    public void testNull() throws Exception {
        
    }
    
    
    public static void main(String[] args) {
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(4, 4, 12, 12, true);
        IInternalController controller = sample.createController(contest, true, false);
        
        AutoJudgeStatusFrame frame = new AutoJudgeStatusFrame();
        frame.setContestAndController(contest, controller);
        frame.setVisible(true);
        
        
    }

}
