package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.controller.PrintController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test for ResetContestFrame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ResetContestFrameTest extends TestCase {

    public static void main(String[] args) {
        ResetContestFrame frame = new ResetContestFrame();
        frame.setSize(new Dimension(480, 250));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FrameUtilities.centerFrame(frame);

        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createContest(1, 1, 12, 12, false);
        IInternalController controller = new PrintController();

        frame.setContestAndController(contest, controller);

        frame.setVisible(true);
    }

}
