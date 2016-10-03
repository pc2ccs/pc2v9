package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * A Frame for editing the Scheduled Start Time in the contest model.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class EditScheduledStartTimeFrame extends JFrame implements UIPlugin {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private IInternalContest model;

    private IInternalController controller;

    private EditScheduledStartTimePane editScheduledStartTimePane = null;

    /**
     * This constructor initializes this frame to contain an {@link EditScheduledStartTimePane}.
     * 
     */
    public EditScheduledStartTimeFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this frame to contain an {@link EditScheduledStartTimePane}.
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(549, 400));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getEditScheduledStartTimePane());
        this.setTitle("Set Contest Scheduled Start Time");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inModel, IInternalController inController) {
        this.model = inModel;
        this.controller = inController;

        getEditScheduledStartTimePane().setContestAndController(model, controller);
        getEditScheduledStartTimePane().setParentFrame(this);
    }
    
    /**
     * This method sets the title for (this) frame depending on whether a Scheduled Start Time
     * has already been set (as determined by the contents of the received {@link ContestInformation} object),
     * then passes the received {@link ContestInformation} to the {@link EditScheduledStartTimePane}
     * contained in the frame.
     * 
     * @param contestInfo the current ContestInformation
     */
    public void setContestInfo(ContestInformation contestInfo) {
        if (contestInfo == null || contestInfo.getScheduledStartTime()==null) {
            setTitle("Set Scheduled Start Time");
        } else {
            setTitle("Update Scheduled Start Time");
        }
        getEditScheduledStartTimePane().setContestInfo(contestInfo);
    }

    public String getPluginTitle() {
        return "Edit ScheduledStartTime Frame";
    }

    /**
     * This method returns an initialized {@link EditScheduledStartTimePane}.
     * 
     * @return an edu.csus.ecs.pc2.ui.EditScheduledStartTimePane
     */
    private EditScheduledStartTimePane getEditScheduledStartTimePane() {
        if (editScheduledStartTimePane == null) {
            editScheduledStartTimePane = new EditScheduledStartTimePane();
        }
        return editScheduledStartTimePane;
    }


} // @jve:decl-index=0:visual-constraint="10,10"
