package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditBalloonSettingsFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5032031472446676474L;

    private IInternalContest contest = null;

    private IInternalController controller = null;

    private EditBalloonSettingPane balloonSettingPane = null;

    /**
     * This method initializes
     * 
     */
    public EditBalloonSettingsFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(618,450));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getBalloonSettingPane());
        this.setTitle("Edit Balloon Settings");

        FrameUtilities.centerFrame(this);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getBalloonSettingPane().setContestAndController(contest, controller);
        getBalloonSettingPane().setParentFrame(this);
    }

    public String getPluginTitle() {
        return "Edit Balloon Settings Frame";
    }

    /**
     * This method initializes balloonSettingPane
     * 
     * @return edu.csus.ecs.pc2.ui.BalloonSettingPane
     */
    private EditBalloonSettingPane getBalloonSettingPane() {
        if (balloonSettingPane == null) {
            balloonSettingPane = new EditBalloonSettingPane();
        }
        return balloonSettingPane;
    }

    public void setBalloonSettings(BalloonSettings balloonSettings) {
        getBalloonSettingPane().setBalloonSettings(balloonSettings);
        if (balloonSettings != null) {
            this.setTitle("Edit Notification Settings for Site " + balloonSettings.getSiteNumber());
        } else {
            this.setTitle("Add Notification Settings");
        }

    }

} // @jve:decl-index=0:visual-constraint="10,10"
