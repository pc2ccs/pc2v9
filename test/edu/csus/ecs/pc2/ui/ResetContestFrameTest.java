// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import junit.framework.TestCase;

/**
 * Test for ResetContestFrame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ResetContestFrameTest extends TestCase {
    
    public void testNull() throws Exception {
        
    }

    public static void main(String[] args) {
        ResetContestFrame frame = new ResetContestFrame();
        frame.setSize(new Dimension(480, 250));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FrameUtilities.centerFrame(frame);

//        SampleContest sampleContest = new SampleContest();
//        IInternalContest contest = sampleContest.createContest(1, 1, 12, 12, false);
//        IInternalController controller = new PrintController();
//
//        frame.setContestAndController(contest, controller);

        frame.setVisible(true);
    }

}
