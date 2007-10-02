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
    private static final long serialVersionUID = -3366149177414867115L;

    private AutoJudgeSettingsPane editAutoJudgePane = null;

    private IContest contest;

    private IController controller;

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
        this.setSize(new java.awt.Dimension(499, 233));
        this.setContentPane(getEditAutoJudgePane());

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getEditAutoJudgePane().setContestAndController(contest, controller);
        getEditAutoJudgePane().setParentFrame(this);
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

        setTitle("Edit Auto Judge Settings ");
        getEditAutoJudgePane().setClientSettings(clientSettings);
    }

    /**
     * This method initializes editAutoJudgePane
     * 
     * @return edu.csus.ecs.pc2.ui.AutoJudgeSettingsPane
     */
    private AutoJudgeSettingsPane getEditAutoJudgePane() {
        if (editAutoJudgePane == null) {
            editAutoJudgePane = new AutoJudgeSettingsPane();
        }
        return editAutoJudgePane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
