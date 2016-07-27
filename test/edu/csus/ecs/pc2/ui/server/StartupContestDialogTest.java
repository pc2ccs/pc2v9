package edu.csus.ecs.pc2.ui.server;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Test for  StartupContestDialog.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StartupContestDialogTest extends TestCase {

    public void testNull() throws Exception {
        
    }
    
    public static void main(String[] args) {
        StartupContestDialog dialog = new StartupContestDialog();
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
