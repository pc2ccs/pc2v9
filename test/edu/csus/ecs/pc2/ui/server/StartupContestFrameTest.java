package edu.csus.ecs.pc2.ui.server;

import edu.csus.ecs.pc2.core.model.Profile;
import junit.framework.TestCase;

/**
 * Test Statup Contest Frame
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StartupContestFrameTest extends TestCase {

    /**
     * Test the frame.
     * @param args
     */
    public static void main(String[] args) {
        final StartupContestFrame frame = new StartupContestFrame();
        frame.setVisible(true);

        frame.setRunnable(new Runnable() {
            public void run() {
                String password = frame.getContestPassword();
                Profile profile = frame.getSelectedProfile();
                System.out.println("Password is " + password);
                System.out.println("Profile is " + profile);
                System.out.println("           " + profile.getDescription());
                System.out.println("           " + profile.getProfilePath());
                System.out.println("exiting StartupContestFrameTest.");
                System.exit(2);
            }
        });
    }
}
