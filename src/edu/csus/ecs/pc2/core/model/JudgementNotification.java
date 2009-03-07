package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;


/**
 * A single judgement notification setting.
 * 
 * This setting is a boolean (to allow notification or not)
 * and a cutoff time, the number of minutes before the end
 * of the contest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementNotification implements Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = 5468535083280239720L;

    /**
     * Minutes before end of contest to suppress notifications.
     */
    private int cuttoffMinutes = 0;

    /**
     * Should notifications be suppressed ?
     */
    private boolean notificationSuppressed = false;
    
    public JudgementNotification() {
        
    }

    public JudgementNotification(boolean notificationSent, int cuttoffMinutes) {
        super();
        this.notificationSuppressed = notificationSent;
        this.cuttoffMinutes = cuttoffMinutes;
    }
    
    public boolean isSameAs(JudgementNotification judgementNotificationIn) {
        return (judgementNotificationIn.isNotificationSupressed() == isNotificationSupressed())
        && (judgementNotificationIn.getCuttoffMinutes() == getCuttoffMinutes());
    }
    

    public int getCuttoffMinutes() {
        return cuttoffMinutes;
    }

    public void setCuttoffMinutes(int cuttoffMinutes) {
        this.cuttoffMinutes = cuttoffMinutes;
    }

    public boolean isNotificationSupressed() {
        return notificationSuppressed;
    }

    public void setNotificationSupressed(boolean suppressNotification) {
        this.notificationSuppressed = suppressNotification;
    }

}
