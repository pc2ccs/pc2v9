package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Group;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$

public class EditGroupFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -6248957592340866836L;

    private IContest contest;

    private IController controller;

    private GroupPane groupPane = null;

    /**
     * This method initializes
     * 
     */
    public EditGroupFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 278));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getGroupPane());
        this.setTitle("New Group");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getGroupPane().setContestAndController(contest, controller);
        getGroupPane().setParentFrame(this);

    }

    public void setGroup(Group group) {
        if (group == null) {
            setTitle("Add New Group");
        } else {
            setTitle("Edit Group " + group.getDisplayName());
        }
        getGroupPane().setGroup(group);
    }

    public String getPluginTitle() {
        return "Edit Group Frame";
    }

    /**
     * This method initializes groupPane
     * 
     * @return edu.csus.ecs.pc2.ui.GroupPane
     */
    private GroupPane getGroupPane() {
        if (groupPane == null) {
            groupPane = new GroupPane();
        }
        return groupPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
