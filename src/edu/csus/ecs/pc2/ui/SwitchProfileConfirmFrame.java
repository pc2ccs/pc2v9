package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Frame for Profile Save.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SwitchProfileConfirmFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -2571995618077528278L;

    private SwitchProfileConfirmPane switchProfileConfirmPane = null;

    /**
     * This method initializes
     * 
     */
    public SwitchProfileConfirmFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(387, 194));
        this.setTitle("Switch Profile");
        this.setContentPane(getProfileSavePane());

        FrameUtilities.centerFrame(this);

    }

    /**
     * This method initializes profileSavePane
     * 
     * @return edu.csus.ecs.pc2.ui.ProfileSavePane
     */
    private SwitchProfileConfirmPane getProfileSavePane() {
        if (switchProfileConfirmPane == null) {
            switchProfileConfirmPane = new SwitchProfileConfirmPane();
            switchProfileConfirmPane.setParentFrame(this);
        }
        return switchProfileConfirmPane;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        getProfileSavePane().setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "SwitchProfileConfirmPane";
    }

    public void setProfile(Profile profile) {
        this.setTitle("Switch Profile to "+profile.getName()+" ("+profile.getDescription()+")");
        getProfileSavePane().setProfile(profile);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
