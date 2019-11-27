// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class encapsulates the configuration obtained from a remote CLICS Contest API.
 * 
 * It is used to hold a representation of a remote contest configuration during Shadow CCS operations.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class RemoteContestConfiguration {
    
    private String remoteJSONConfiguation;
    
    /**
     * Constructs a RemoteContestConfiguration characterized by the given input data string.
     * 
     * The input data string is assumed to be a JSON representation of the configuration of
     * a remote contest, such as that obtained by invoking an implementation of
     * {@link IRemoteContestAPIAdapter#getRemoteContestConfiguration()}.
     * 
     * The class stores the JSON remote configuration representation, and supports obtaining
     * a 
     * 
     * @param jsonConfigruationString a JSON String giving the contest configuration obtained from a remote CCS 
     */
    public RemoteContestConfiguration(String jsonConfigruationString){
        this.remoteJSONConfiguation = jsonConfigruationString;
    }
    
    /**
     * Returns the JSON string giving the remote contest configuration.
     * The returned string is exactly the confiuration string which was used to construct this
     * RemoteContestConfiguration object.
     * 
     * @return a JSON string giving the remote contest configuration
     */
    public String getRemoteContestConfigurationString() {
        return remoteJSONConfiguation;
    }
    
    /**
     * Returns a Map<String><String> containing the keywords and corresponding values
     * found in the JSON string which was used to construct this RemoteContestConfiguration.
     * 
     * @return a Map of the key:value pairs in the remote contest configuration string
     */
    public Map<String,String> getRemoteContestConfigurationMap() {
        
        if (remoteJSONConfiguation==null || remoteJSONConfiguation.trim().equals("")) {
            return null;
        }
        
        
        ObjectMapper mapper = new ObjectMapper();
        
        //TODO: use the mapper to pull problems, languages, teams, etc. from the remoteJSONConfiguration string
        // and return them in a map.
        
        return null;
    }
}
