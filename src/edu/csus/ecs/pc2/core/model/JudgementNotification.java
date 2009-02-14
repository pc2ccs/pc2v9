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

    public int getCuttoffMinutes() {
        return cuttoffMinutes;
    }

    public void setCuttoffMinutes(int cuttoffMinutes) {
        this.cuttoffMinutes = cuttoffMinutes;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    private int cuttoffMinutes;

    private boolean notificationSent = true;
    
    public JudgementNotification() {
        
    }

    public JudgementNotification(boolean notificationSent, int cuttoffMinutes) {
        super();
        this.notificationSent = notificationSent;
        this.cuttoffMinutes = cuttoffMinutes;
    }
}
