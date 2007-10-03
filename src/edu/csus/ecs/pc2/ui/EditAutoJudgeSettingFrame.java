/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Edit AutoJudge settings JFrame, holds AutoJudgeSettingsPane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class EditAutoJudgeSettingFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 7466524895460714723L;

    private IContest contest;

    private IController controller;

    private AutoJudgeSettingsPane autoJudgeSettingsPane = null;

    /**
     * This method initializes
     * 
     */
    public EditAutoJudgeSettingFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(551, 280));
        this.setContentPane(getAutoJudgeSettingsPane());
        FrameUtilities.centerFrame(this);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getAutoJudgeSettingsPane().setContestAndController(contest, controller);
        getAutoJudgeSettingsPane().setParentFrame(this);
    }

    public String getPluginTitle() {
        return "Edit Auto Judge Settings Frame";
    }

    public void setClientSetting(ClientId clientId, ClientSettings clientSettings) {

        if (clientId == null) {
            throw new IllegalArgumentException("client id is null");
        }

        if (clientSettings == null) {
            clientSettings = new ClientSettings(clientId);
        }

        getAutoJudgeSettingsPane().setClientSettings(clientSettings);
        getAutoJudgeSettingsPane().setVisible(true);
    }

    /**
     * This method initializes autoJudgeSettingsPane
     * 
     * @return edu.csus.ecs.pc2.ui.AutoJudgeSettingsPane
     */
    private AutoJudgeSettingsPane getAutoJudgeSettingsPane() {
        if (autoJudgeSettingsPane == null) {
            autoJudgeSettingsPane = new AutoJudgeSettingsPane();
        }
        return autoJudgeSettingsPane;
    }

} // @jve:decl-index=0:visual-constraint="13,15"

