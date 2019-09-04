// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

/**
 * Listener for all Change Password Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IChangePasswordListener {

    /**
     * Password changed.
     * 
     * @param event
     */
    void passwordChanged(PasswordChangeEvent event);

    /**
     * Password not changed.
     * 
     * @param event
     */
    void passwordNotChanged(PasswordChangeEvent event);
}
