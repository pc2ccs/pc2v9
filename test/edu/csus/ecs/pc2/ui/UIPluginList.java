package edu.csus.ecs.pc2.ui;

import java.util.Vector;

/**
 * Maintain a list of UI Plugins.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class UIPluginList {

    private Vector<UIPlugin> pluginList = new Vector<UIPlugin>();

    public UIPlugin register(UIPlugin plugin) {
        pluginList.add(plugin);
        return plugin;
    }

    public void remove(UIPlugin plugin) {
        pluginList.remove(plugin);
    }

    public UIPlugin[] getList() {
        return (UIPlugin[]) pluginList.toArray(new UIPlugin[pluginList.size()]);
    }

}
