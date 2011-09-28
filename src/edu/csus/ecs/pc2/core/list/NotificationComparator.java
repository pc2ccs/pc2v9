package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Notification;

/**
 * Sort Notifications by site and then number.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationComparator implements Comparator<Notification>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3140920987885227359L;

    public int compare(Notification n1, Notification n2) {
        int site1 = n1.getSiteNumber();
        int site2 = n2.getSiteNumber();
        if (site1 == site2) {
            return n1.getNumber() - n2.getNumber();
        } else {
            return site1 - site2;
        }
    }

}
