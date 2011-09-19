package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Category Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ICategoryListener.java 2078 2010-04-09 04:40:52Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/model/ICategoryListener.java $
public interface ICategoryListener {

    /**
     * New Category.
     * @param event
     */
    void categoryAdded(CategoryEvent event);

    /**
     * Category information has changed.
     * @param event
     */
    void categoryChanged(CategoryEvent event);

    /**
     * Category has been removed.
     * @param event
     */
    void categoryRemoved(CategoryEvent event);

    /**
     * Refresh all Categories.
     * @param categoryEvent
     */
    void categoryRefreshAll(CategoryEvent event);
}
