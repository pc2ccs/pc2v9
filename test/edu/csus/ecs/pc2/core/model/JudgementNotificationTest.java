package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementNotificationTest extends TestCase {

    public static void main(String[] args) {
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.model.JudgementNotification.isSameAs(JudgementNotification)'
     */
    public void testIsSameAs() {
        
        JudgementNotification judgementNotification1 = new JudgementNotification(true, 100);
        JudgementNotification judgementNotification2 = new JudgementNotification(true, 100);
        
        assertTrue("identical", judgementNotification1.isSameAs(judgementNotification2));
        
        judgementNotification2 = new JudgementNotification(false, 100);
        assertFalse("notificationSent ", judgementNotification1.isSameAs(judgementNotification2));
        
        judgementNotification2 = new JudgementNotification(true, 2);
        assertFalse("notificationSent ", judgementNotification1.isSameAs(judgementNotification2));
        
        judgementNotification2 = new JudgementNotification(false, 5);
        assertFalse("notificationSent ", judgementNotification1.isSameAs(judgementNotification2));
        

    }

}
