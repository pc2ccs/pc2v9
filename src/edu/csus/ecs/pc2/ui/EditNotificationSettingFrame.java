package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Edit Notification Settings Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditNotificationSettingFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6002651539759525754L;

    private NotificationSettingPane notificationSettingPane = null;

    /**
     * This method initializes
     * 
     */
    public EditNotificationSettingFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(600, 350));
        this.setContentPane(getNotificationSettingPane());
        this.setTitle("Edit Notification Setting");

    }

    /**
     * This method initializes notificationSettingPane
     * 
     * @return edu.csus.ecs.pc2.ui.NotificationSettingPane
     */
    private NotificationSettingPane getNotificationSettingPane() {
        if (notificationSettingPane == null) {
            notificationSettingPane = new NotificationSettingPane();
        }
        return notificationSettingPane;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

        getNotificationSettingPane().setContestAndController(inContest, inController);

    }

    public String getPluginTitle() {
        return "Edit Notification Setting Frame";
    }

} // @jve:decl-index=0:visual-constraint="10,10"
