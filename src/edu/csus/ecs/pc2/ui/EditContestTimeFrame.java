package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.ContestTime;

/**
 * Edit Contest Time Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class EditContestTimeFrame extends JFrame implements UIPlugin {


    /**
     * 
     */
    private static final long serialVersionUID = 3570106747020180994L;

    private IContest contest;

    private IController controller;

    private ContestTimePane contestTimePane = null;

    /**
     * This method initializes
     * 
     */
    public EditContestTimeFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 278));
        this.setContentPane(getContestTimePane());
        this.setTitle("New Contest Time");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getContestTimePane().setContestAndController(contest, controller);
        getContestTimePane().setParentFrame(this);

    }

    public void setContestTime(ContestTime contestTime) {
        if (contestTime == null) {
            setTitle("Add New Contest Time");
        } else {
            setTitle("Edit Contest Time for Site " + contestTime.getSiteNumber());
        }
        getContestTimePane().setContestTime(contestTime);
    }

    public String getPluginTitle() {
        return "Edit ContestTime Frame";
    }

    /**
     * This method initializes contestTimePane
     * 
     * @return edu.csus.ecs.pc2.ui.ContestTimePane
     */
    private ContestTimePane getContestTimePane() {
        if (contestTimePane == null) {
            contestTimePane = new ContestTimePane();
        }
        return contestTimePane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
