/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 * 
 */
public class ChangePasswordFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1776246146720515119L;
    private ChangePasswordPane changePasswordPane = null;

    /**
     * 
     */
    public ChangePasswordFrame() {
        super();
        initialize();
        // TODO Auto-generated constructor stub
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(500,200));
        this.setMaximumSize(new java.awt.Dimension(500,200));
        this.setPreferredSize(new java.awt.Dimension(500,200));
        this.setTitle("Change Password");
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(new java.awt.Dimension(500,200));
        this.setVisible(true);
        this.setContentPane(getChangePasswordPane());

    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.ui.UIPlugin#setContestAndController(edu.csus.ecs.pc2.core.model.IInternalContest, edu.csus.ecs.pc2.core.IInternalController)
     */
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        changePasswordPane.setContestAndController(inContest, inController);
        changePasswordPane.setParentFrame(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.ui.UIPlugin#getPluginTitle()
     */
    public String getPluginTitle() {
        // TODO Auto-generated method stub
        return "Change Password Frame";
    }

    public ChangePasswordPane getChangePasswordPane() {
        if (changePasswordPane == null) {
            changePasswordPane = new ChangePasswordPane();
        }
        return changePasswordPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
