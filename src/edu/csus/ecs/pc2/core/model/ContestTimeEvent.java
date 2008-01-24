package edu.csus.ecs.pc2.core.model;


/**
 * InternalContest Time event.
 * 
 * The InternalContest has started, stopped or changed.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ContestTimeEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * InternalContest Time added.
         */
        DELETED,
        /**
         * ContestTime added.
         */
        ADDED,
        /**
         * Updated ContestTime.
         */
        CHANGED,
        /**
         * Clock has been started.
         */
        CLOCK_STARTED,
        /**
         * Clock has been stopped
         */
        CLOCK_STOPPED,

    }

    private Action action;

    private ContestTime contestTime;

    private int siteNumber;

    /**
     * 
     * @param contestTimeAction
     * @param contestTime
     * @param siteNumber
     *            which site this event is for
     */
    public ContestTimeEvent(Action contestTimeAction, ContestTime contestTime, int siteNumber) {
        super();
        this.action = contestTimeAction;
        this.contestTime = contestTime;
        this.siteNumber = siteNumber;
    }

    public Action getAction() {
        return action;
    }

    public ContestTime getContestTime() {
        return contestTime;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

}
