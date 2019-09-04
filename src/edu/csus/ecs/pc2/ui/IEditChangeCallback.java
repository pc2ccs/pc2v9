// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import javax.swing.JComponent;

/**
 * Used as a call back when data has been changeed on a UI.
 *  
 *  This interface is used as a call back to a frame so an
 *  update or cancel button can be changed/updates.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IEditChangeCallback {
    
    /**
     * Called when an item is changed
     */
    void itemChanged(JComponent component);

}
