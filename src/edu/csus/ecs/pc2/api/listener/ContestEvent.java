package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContestTime;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;

/**
 * Event where Contest data/state has changed.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestEvent {

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum EventType {
        /**
         * Account information.
         */
        LOGIN_ACCOUNT,
        /**
         * Problem.
         */
        PROBLEM,
        /**
         * Language.
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
         * Judement.
         */
        JUDGEMENT,
        /**
         * Group.
         */
        GROUP,
    };

    private EventType eventType;

    private IClient client;

    private IContestTime contestTime;

    private IGroup group;

    private IJudgement judgement;

    private ILanguage language;

    private IProblem problem;

    public ContestEvent(EventType eventType, IGroup group) {
        super();
        this.eventType = eventType;
        this.group = group;
    }

    public ContestEvent(EventType eventType, IClient client) {
        super();
        this.eventType = eventType;
        this.client = client;
    }

    public ContestEvent(EventType eventType, IContestTime contestTime) {
        super();
        this.eventType = eventType;
        this.contestTime = contestTime;
    }

    public ContestEvent(EventType eventType, IJudgement judgement) {
        super();
        this.eventType = eventType;
        this.judgement = judgement;
    }

    public ContestEvent(EventType eventType, ILanguage language) {
        super();
        this.eventType = eventType;
        this.language = language;
    }

    public ContestEvent(EventType eventType, IProblem problem) {
        super();
        this.eventType = eventType;
        this.problem = problem;
    }

    /**
     * Get Login Account/Client for event type {@link EventType#LOGIN_ACCOUNT}.
     * 
     * @see #getEventType()
     */
    public IClient getClient() {
        return client;
    }

    /**
     * Get Contest Clock info for event type {@link EventType#CONTEST_CLOCK}.
     * 
     * @see #getEventType()
     */
    public IContestTime getContestTime() {
        return contestTime;
    }

    /**
     * 
     * @return the event type for this event.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Get Group info for event type {@link EventType#GROUP}.
     * 
     * @see #getEventType()
     */
    public IGroup getGroup() {
        return group;
    }

    /**
     * Get Judgement for event type {@link EventType#JUDGEMENT}.
     * 
     * @see #getEventType()
     */
    public IJudgement getJudgement() {
        return judgement;
    }

    /**
     * Get Language info for event type {@link EventType#LOGIN_ACCOUNT}.
     * 
     * @see #getEventType()
     */
    public ILanguage getLanguage() {
        return language;
    }

    /**
     * Get Problem info for event type {@link EventType#PROBLEM}.
     * 
     * @see #getEventType()
     */
    public IProblem getProblem() {
        return problem;
    }
}
