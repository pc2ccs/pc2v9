package edu.csus.ecs.pc2.core.model;

/**
 * A category and a event state {@link edu.csus.ecs.pc2.core.model.CategoryEvent.Action}.
 * 
 * @version $Id: CategoryEvent.java 2092 2010-06-25 17:17:56Z laned $
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/core/model/CategoryEvent.java $
public class CategoryEvent {

    /**
     * Category Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * Category added.
         */
        ADDED,
        /**
         * Category modified.
         */
        CHANGED,
        /**
         * Reload/Refresh all categorys.
         */
        REFRESH_ALL,

    }

    private Action action;

    private Category category;

    public CategoryEvent(Action categoryAction, Category category) {
        super();
        this.action = categoryAction;
        this.category = category;
    }

    public Action getAction() {
        return action;
    }

    public Category getCategory() {
        return category;
    }

}
