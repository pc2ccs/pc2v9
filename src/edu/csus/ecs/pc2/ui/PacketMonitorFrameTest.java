package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import junit.framework.TestCase;

/**
 * packet Monitor JUnit
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketMonitorFrameTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void main(String[] args) {
        
        SampleContest sampleContest = new SampleContest();
        
        IInternalContest inContest = sampleContest.createContest(4, 5, 22, 11, true);
        IInternalController inController = sampleContest.createController(inContest, true, false);
        
        PacketMonitorFrame frame = new PacketMonitorFrame();
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setContestAndController(inContest, inController);
        frame.setVisible(true);
    }
}
