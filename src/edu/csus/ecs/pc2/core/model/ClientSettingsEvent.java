package edu.csus.ecs.pc2.core.model;

/**
 * A ClientSettings and a event state {@link edu.csus.ecs.pc2.core.model.ClientSettingsEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 */

//$HeadURL$
public class ClientSettingsEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * ClientSettings Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {
        /**
         * A new clientSettings.
         */
        ADDED,
        /**
         * Modified ClientSettings.
         */
        CHANGED, 
        /**
         * ClientSettings deleted.
         */
        DELETED, 
        /**
         * Reload/refresh ClientSettings.
         */
        REFRESH_ALL,

    }

    private Action action;
    
    private ClientId clientId;

    private ClientSettings clientSettings;

    public ClientSettingsEvent(Action problemAction, ClientId clientId, ClientSettings clientSettings) {
        super();
        this.action = problemAction;
        this.clientId = clientId;
        this.clientSettings = clientSettings;
    }

    public Action getAction() {
        return action;
    }

    public ClientSettings getClientSettings() {
        return clientSettings;
    }

    public ClientId getClientId() {
        return clientId;
    }

    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }
}
