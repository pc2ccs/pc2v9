package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileSaveFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -2571995618077528278L;

    private ProfileSavePane profileSavePane = null;

    /**
     * This method initializes
     * 
     */
    public ProfileSaveFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(562, 293));
        this.setTitle("Export Profile Settings");
        this.setContentPane(getProfileSavePane());

    }

    /**
     * This method initializes profileSavePane
     * 
     * @return edu.csus.ecs.pc2.ui.ProfileSavePane
     */
    private ProfileSavePane getProfileSavePane() {
        if (profileSavePane == null) {
            profileSavePane = new ProfileSavePane();
            profileSavePane.setParentFrame(this);
        }
        return profileSavePane;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        // TODO Auto-generated method stub

    }

    public String getPluginTitle() {
        return "ProfileSaveFrame";
    }

    public void setSaveButtonName(String name) {
        getProfileSavePane().setSaveButtonName(name);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
