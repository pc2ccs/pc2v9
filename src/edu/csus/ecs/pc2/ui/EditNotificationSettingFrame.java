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
//        this.setContentPane(getNotificationSettingPane());
        this.setTitle("Edit Notification Setting");

        FrameUtilities.centerFrame(this);
    }

    // TODO add when notification settings pane ready
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {

//        getNotificationSettingPane().setContestAndController(inContest, inController);
//        getNotificationSettingPane().setParentFrame(this);
    }

    public String getPluginTitle() {
        return "Edit Notification Setting Frame";
    }
    
//    public static void main(String[] args) {
//        EditNotificationSettingFrame frame = new EditNotificationSettingFrame();
//        frame.setClientId (new ClientId(1,Type.TEAM,3));
//        frame.setVisible(true);
//    }


} // @jve:decl-index=0:visual-constraint="10,10"
