package edu.csus.ecs.pc2.core.model;


/**
 * A site event.
 * 
 * @see edu.csus.ecs.pc2.core.model.ISiteListener
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL
public class SiteEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * A site logoff.
         */
        LOGOFF,
        /**
         * A new site defined.
         */
        ADDED,
        /**
         * A site logoff.
         */
        LOGIN,
        /**
         * A site removed.
         */
        DELETED,
        /**
         * A site modified.
         */
        CHANGED,

    }

    private Action action = Action.ADDED;

    private Site site;

    public SiteEvent(Action action, Site site) {
        super();
        this.action = action;
        this.site = site;
    }

    public SiteEvent(Site site) {
        super();
        this.site = site;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

}
