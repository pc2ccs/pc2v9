package edu.csus.ecs.pc2.ui.server;

import java.awt.Dialog.ModalityType;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Profile;

public class StartupContestDialogTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public static void main(String[] args) {
        StartupContestDialog dialog = new StartupContestDialog();
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
        
        String password = dialog.getContestPassword();
        Profile profile = dialog.getProfile();
        System.out.println("Password is " + password);
        System.out.println("Profile is " + profile);
        System.out.println("           " + profile.getDescription());
        System.out.println("           " + profile.getProfilePath());
        System.out.println("exiting StartupContestFrameTest.");
        
        System.exit(0);
        
    }


}
