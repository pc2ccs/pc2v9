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
public class SwitchProfileStatusFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -2571995618077528278L;

    private SwitchProfileStatusPane switchProfileStatusPane = null;

    /**
     * This method initializes
     * 
     */
    public SwitchProfileStatusFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(557, 264));
        this.setContentPane(getSwitchProfileStatusPane());
        this.setTitle("Switch Profile");
        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        getSwitchProfileStatusPane().setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "SwitchProfileConfirmPane";
    }

    public void setProfile(Profile profile) {
        this.setTitle("Profile Change Status, to profile " + profile.getName() + " (" + profile.getDescription() + ")");
        getSwitchProfileStatusPane().setProfile(profile);
    }

    /**
     * This method initializes switchProfileStatusPane
     * 
     * @return edu.csus.ecs.pc2.ui.SwitchProfileStatusPane
     */
    private SwitchProfileStatusPane getSwitchProfileStatusPane() {
        if (switchProfileStatusPane == null) {
            switchProfileStatusPane = new SwitchProfileStatusPane();
            switchProfileStatusPane.setParentFrame(this);

        }
        return switchProfileStatusPane;
    }

    public void setNewContestPassword(String password) {
        getSwitchProfileStatusPane().setNewContestPassword(password);
    }

    public void setCurrentContestPassword(String password) {
        getSwitchProfileStatusPane().setCurrentContestPassword(password);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
