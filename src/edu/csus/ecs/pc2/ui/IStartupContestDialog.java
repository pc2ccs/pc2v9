// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Contest password dialog.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IStartupContestDialog extends UIPlugin {

    void setVisible(boolean b);

    String getContestPassword();

    Profile getProfile();

}
