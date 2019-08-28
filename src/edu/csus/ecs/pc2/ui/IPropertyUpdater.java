// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.Properties;

/**
 * A Property Updater interface.
 * 
 * This is used as a call back for when a Properties has
 * been updated.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IPropertyUpdater {

    /**
     * When properties are updated, this method will be invoked.
     * 
     * @param properties
     */
    void updateProperties(Properties properties);

}
