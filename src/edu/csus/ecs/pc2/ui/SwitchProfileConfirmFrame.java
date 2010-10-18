package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Frame to enter contest password for profile switch.
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
        this.setContentPane(getSwitchProfileConfirmPane());

        FrameUtilities.centerFrame(this);

    }

    /**
     * This method initializes profileSavePane
     * 
     * @return edu.csus.ecs.pc2.ui.ProfileSavePane
     */
    private SwitchProfileConfirmPane getSwitchProfileConfirmPane () {
        if (switchProfileConfirmPane == null) {
            switchProfileConfirmPane = new SwitchProfileConfirmPane();
            switchProfileConfirmPane.setParentFrame(this);
        }
        return switchProfileConfirmPane;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        getSwitchProfileConfirmPane().setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "SwitchProfileConfirm Frame";
    }

    public void setProfile(Profile profile) {
        this.setTitle("Switch Profile to "+profile.getName()+" ("+profile.getDescription()+")");
        getSwitchProfileConfirmPane().setProfile(profile);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
