package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SwitchProfileStatusFrameTest extends TestCase {
    
    public void testNull() throws Exception {
        
    }
    
    public static void main(String[] args) {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(4, 5, 22, 11, true);
        IInternalController controller = sample.createController(contest, true, false);
        
        Site [] sites = sample.createSites(contest, 4);
        
        for (Site site : sites ){
            contest.addSite(site);
        }

        SwitchProfileStatusFrame frame = new SwitchProfileStatusFrame();
        frame.setContestAndController(contest, controller);
        frame.setVisible(true);
        
        
    }

}
