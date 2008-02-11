package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.ISite;

/**
 * This class describes an Event representing the situation that some piece of contest configuration information has changed.
 * <p>
 * Each individual {@link ContestEvent} contains multiple fields which together act to describe the change which has occured.
 * First, every  {@link ContestEvent} contains an {@link EventType} (an enum element) identifying the general category of event -- 
 * whether it is related to changes in Accounts, Languages, Problems, etc.
 * <p>
 * Next, each {@link ContestEvent} contains detailed information which is event-type dependent.  For example, events of type
 * {@link EventType#PROBLEM} (related to changes in contest problem configuration) contain an object of type {@link edu.csus.ecs.pc2.api.IProblem} 
 * describing the problem configuration change which has occurred.
 * <P>
 * Clients implementing the PC<sup>2</sup> API {@link edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener} interface can 
 * determine the details of a contest configuration update event by first obtaining the {@link EventType} from the received event, and 
 * subsequently using the appropriate {@link ContestEvent} methods to obtain the configuration change details.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @see EventType
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestEvent {

    /**
     * This enum identifies the types of configuration change events which can occur.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum EventType {
        /**
         * Client (account) information.
         */
        CLIENT,
        /**
         * Contest Problem.
         */
        PROBLEM,
        /**
         * Contest Language.
         */
        LANGUAGE,
        /**
         * Contest Clock.
         */
        CONTEST_CLOCK,
        /**
         * Contest title.
         */
        CONTEST_TITLE,
        /**
         * Judgement.
         */
        JUDGEMENT,
        /**
         * Account (Team) Group.
         */
        GROUP,
        /**
         * Site.
         */
        SITE,
    };

    private EventType eventType;

    private IClient client;

    private IContestClock contestClock;

    private IGroup group;

    private IJudgement judgement;

    private ILanguage language;

    private IProblem problem;
    
    private String contestTitle;
    
    private ISite site;

    public ContestEvent(EventType eventType, ISite site) {
        super();
        this.eventType = eventType;
        this.site = site;
    }

    // TODO document
    public ISite getSite() {
        return site;
    }

    /**
     * Construct an event representing a change in the contest title.
     * @param eventType
     * @param contestTitle the new contest title 
     */
    public ContestEvent(EventType eventType, String contestTitle) {
        super();
        // TODO Auto-generated constructor stub
        this.eventType = eventType;
        this.contestTitle = contestTitle;
    }
    
    /**
     * Construct an event representing a change in a contest Group.
     * @param eventType
     * @param group the changed contest group 
     */
    public ContestEvent(EventType eventType, IGroup group) {
        super();
        this.eventType = eventType;
        this.group = group;
    }

    /**
     * Construct an event representing a change in a contest Client (account).
     * @param eventType
     * @param client the changed contest client 
     */
   public ContestEvent(EventType eventType, IClient client) {
        super();
        this.eventType = eventType;
        this.client = client;
    }

   /**
    * Construct an event representing a change in contest clock (time) information).
    * @param eventType
    * @param contestClock the changed contest clock 
    */
   public ContestEvent(EventType eventType, IContestClock contestClock) {
        super();
        this.eventType = eventType;
        this.contestClock = contestClock;
    }

   /**
    * Construct an event representing a change in a contest Judgement.
    * @param eventType
    * @param judgement the changed Judgement 
    */
   public ContestEvent(EventType eventType, IJudgement judgement) {
        super();
        this.eventType = eventType;
        this.judgement = judgement;
    }

   /**
    * Construct an event representing a change in a contest Language.
    * @param eventType
    * @param language the changed contest language 
    */
    public ContestEvent(EventType eventType, ILanguage language) {
        super();
        this.eventType = eventType;
        this.language = language;
    }

    /**
     * Construct an event representing a change in a contest Problem.
     * @param eventType
     * @param problem the changed contest problem 
     */
    public ContestEvent(EventType eventType, IProblem problem) {
        super();
        this.eventType = eventType;
        this.problem = problem;
    }

    /**
     * Get the client associated with events of type {@link EventType#CLIENT}.
     * 
     * @see #getEventType()
     */
    public IClient getClient() {
        return client;
    }

    /**
     * Get Contest Clock info associated with events of type {@link EventType#CONTEST_CLOCK}.
     * 
     * @see #getEventType()
     */
    public IContestClock getContestClock() {
        return contestClock;
    }

    /**
     * Get the type of this event.
     * 
     * @return the event type for this event.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Get the Group info associated with events of type {@link EventType#GROUP}.
     * 
     * @see #getEventType()
     */
    public IGroup getGroup() {
        return group;
    }

    /**
     * Get the Judgement associated with events of type {@link EventType#JUDGEMENT}.
     * 
     * @see #getEventType()
     */
    public IJudgement getJudgement() {
        return judgement;
    }

    /**
     * Get the Language info associated with events of type {@link EventType#LANGUAGE}.
     * 
     * @see #getEventType()
     */
    public ILanguage getLanguage() {
        return language;
    }

    /**
     * Get the Problem info associated with events of type {@link EventType#PROBLEM}.
     * 
     * @see #getEventType()
     */
    public IProblem getProblem() {
        return problem;
    }
    
    /**
     * Get the Contest Title associated with events of type {@link EventType#CONTEST_TITLE}.
     * @return contest title.
     */
    public String getContestTitle() {
        return contestTitle;
    }
    
}
