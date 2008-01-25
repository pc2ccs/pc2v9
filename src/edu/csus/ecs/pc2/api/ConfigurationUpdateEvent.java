package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Contest Data Update, both configuration and run-time.
 * 
 * This event is triggered when any configuration data is changed (added, removed, updated).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
 // $HeadURL$
 
public class ConfigurationUpdateEvent {

    /**
     * Actions for ConfigurationUpdateEvent.
     * 
     * @see ConfigurationUpdateEvent
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Action {
        /**
         * Reset.
         * 
         */
        RESET,
        /**
         * Account added (ClientId populated).
         */
        ACCOUNT_ADDED,
        /**
         * Account removed (ClientId populated).
         */
        ACCOUNT_REMOVED,
        /**
         * Account updated (ClientId populated).
         */
        ACCOUNT_UPDATED,
        /**
         * Title updated (String populated)
         */
        TITLE_UPDATED,
        /**
         * Language added (ElementId populated).
         */
        LANGUAGE_ADDED,
        /**
         * Language removed (ElementId populated).
         */
        LANGUAGE_REMOVED,
        /**
         * Language updated (ElementId populated).
         */
        LANGUAGE_UPDATED,
        /**
         * Problem added (ElementId populated).
         */
        PROBLEM_ADDED,
        /**
         * Problem removed (ElementId populated).
         */
        PROBLEM_REMOVED,
        /**
         * Problem updated (ElementId populated).
         */
        PROBLEM_UPDATED,
        /**
         * Judgement added (ElementId populated).
         */
        JUDGEMENT_ADDED,
        /**
         * Judgement removed (ElementId populated).
         */
        JUDGEMENT_REMOVED,
        /**
         * Judgement updated (ElementId populated).
         */
        JUDGEMENT_UPDATED,
    }

    private Action action;

    private ClientId clientId;

    private ElementId elementId;

    private String string;

    // TODO DOC PARAMS

    /**
     * @param action
     * @param string
     */
    public ConfigurationUpdateEvent(Action action, String string) {
        super();
        this.action = action;
        this.string = string;
    }

    /**
     * @param action
     * @param elementId
     */
    public ConfigurationUpdateEvent(Action action, ElementId elementId) {
        super();
        this.action = action;
        this.elementId = elementId;
    }

    /**
     * @param action
     * @param clientId
     */
    public ConfigurationUpdateEvent(Action action, ClientId clientId) {
        super();
        this.action = action;
        this.clientId = clientId;
    }

    /**
     * get ClientId for account.
     * 
     * @return ClientId for account.
     */
    public ClientId getClientId() {
        return clientId;
    }

    /**
     * Get ElementId for changed problem, language or judgement.
     * 
     * @return ElementId for changed problem, language or judgement.
     */
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @return string value.
     */
    public String getString() {
        return string;
    }

    /**
     * Get the action triggered.
     * 
     * @return the action triggered.
     */
    public Action getAction() {
        return action;
    }

}
