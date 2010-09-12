package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.ui.JPanePlugin;

/**
 * Sort in plugin name order.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JPluginPaneNameComparator implements Comparator<JPanePlugin>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6142520044578182336L;

    public int compare(JPanePlugin plugin1, JPanePlugin plugin2) {
        return plugin1.getPluginTitle().compareTo(plugin2.getPluginTitle());
    }

}
