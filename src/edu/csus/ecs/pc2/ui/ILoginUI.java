// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;


/**
 * Login UI.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ILoginUI extends UIPlugin {

    void regularCursor();

    void setStatusMessage(String message);

    void disableLoginButton();

    void dispose();

}
