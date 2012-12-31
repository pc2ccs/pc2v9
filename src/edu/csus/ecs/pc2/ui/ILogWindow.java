package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Log window interface.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ILogWindow extends UIPlugin {

    void setTitle(String string);

    void setVisible(boolean showWindow);

    boolean isVisible();

    void dispose();
}
