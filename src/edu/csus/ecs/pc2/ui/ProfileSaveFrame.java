package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Frame for Profile Save.
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
        this.setSize(new Dimension(527, 560));
        this.setTitle("Export Profile Settings");
        this.setContentPane(getProfileSavePane());
        
        FrameUtilities.centerFrame(this);

    }

    /**
     * This method initializes profileSavePane
     * 
     * @return edu.csus.ecs.pc2.ui.ProfileSavePane
     */
    private ProfileSavePane getProfileSavePane() {
        if (profileSavePane == null) {
            profileSavePane = new ProfileSavePane();
            profileSavePane.setSize(new Dimension(526, 620));
            profileSavePane.setPreferredSize(new Dimension(259, 633));
            profileSavePane.setMinimumSize(new Dimension(259, 633));
            profileSavePane.setParentFrame(this);
        }
        return profileSavePane;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        getProfileSavePane().setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "ProfileSaveFrame";
    }

    /**
     * This sets the save button name and populates the GUI.
     * 
     * @param name
     */
    public void setSaveButtonName(String name) {
        getProfileSavePane().setSaveButtonName(name);
    }
    
//    @Override
//    public void setVisible(boolean b) {
//        this.setSize(new Dimension(527, 524));
//        super.setVisible(b);
//    }

} // @jve:decl-index=0:visual-constraint="10,10"
