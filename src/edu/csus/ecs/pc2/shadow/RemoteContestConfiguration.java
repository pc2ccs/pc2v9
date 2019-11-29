// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.List;
import java.util.Map;

/**
 * This class encapsulates a configuration obtained from a remote CLICS Contest API; in other words
 * it is a "model" (in the MVC sense) for a remote contest configuration.
 * 
 * It is used to hold a representation of a remote contest configuration during Shadow CCS operations.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 * @author John Clevenger, PC^2 Team, pc2@ecs.csus.edu
 */
public class RemoteContestConfiguration {
    
    private Map<IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> map;
    
    /**
     * Constructs a RemoteContestConfiguration characterized by the given input map.
     * 
     * The input map is assumed to be a JSON representation of the configuration of
     * a remote contest, such as that obtained by invoking an implementation of
     * {@link IRemoteContestAPIAdapter#getRemoteContestConfiguration()}.
     * 
     * @param jsonConfigruationString a JSON String giving the contest configuration obtained from a remote CCS 
     */
    public RemoteContestConfiguration(Map<IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT, 
                                            List<AbstractRemoteConfigurationObject>> remoteConfigMap){
        this.map = remoteConfigMap;
    }
    
    /**
     * Returns a Map containing the keywords and corresponding values (obtained from a remote CCS)
     *  which was used to construct this RemoteContestConfiguration.
     * 
     * @return a Map of the key:value pairs in the remote contest configuration
     */
    public Map<IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> 
                    getRemoteContestConfigurationMap() {
        return map;

    }
        
        
        



}
