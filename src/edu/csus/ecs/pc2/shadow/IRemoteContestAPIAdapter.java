// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

/**
 * This interface defines the facilities which implementations of adapters to be used in Shadow Mode for connecting to
 * Remote CCS Contest API endpoints must implement.
 * 
 * At least two implementations are expected:  MockRemoteContestAPIAdapter, which provides a local "mock"
 * implementation of a Remote CCS CLICS Contest API, and RemoteContestAPIAdapter, which provides an
 * implementation of an actual connection to a remote CCS.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
interface IRemoteContestAPIAdapter {
    
    public enum REMOTE_CONFIGURATION_ELEMENT {CONFIG_PROBLEMS, CONFIG_LANGUAGES, CONFIG_JUDGEMENT_TYPES, CONFIG_GROUPS,
                                        CONFIG_ORGANIZATIONS, CONFIG_TEAMS, CONFIG_CONTEST_STATE} 

    /**
     * Returns a remote contest configuration object that contains:
     * <pre>
     *   - judgement types
     *   - languages
     *   - problems
     *   - groups
     *   - organizations
     *   - teams
     *   - contest state
     * </pre>
     * @return
     */
    public RemoteContestConfiguration getRemoteContestConfiguration();
    
    /**
     * Test connection to remote CCS API.
     * @return true if could connect, otherwise fale;
     */
    boolean testConnection();
    
    
    String getRemoteJSON(String endpoint);

}
