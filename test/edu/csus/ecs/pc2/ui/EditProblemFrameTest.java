package edu.csus.ecs.pc2.ui;

// package edu.csus.ecs.pc2.ui;

import javax.swing.WindowConstants;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.NullController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Unit test for edit problem frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditProblemFrameTest extends TestCase {

    public static void main(String[] args) {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(1, 1, 12, 12, true);

        NullController nullController = new NullController("editproblemframe.log");

        Utilities.setDebugMode(true);

        EditProblemFrame frame = new EditProblemFrame();
        frame.setContestAndController(contest, nullController);
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}
