// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

package edu.csus.ecs.pc2.shadow;

import java.util.Map;

/**
 * This class is the parent class (base class) for concrete representations of remote contest
 * configuation elements such as Problems, Languages, etc.
 * 
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class AbstractRemoteConfigurationObject {
    
    public enum REMOTE_CONFIGURATION_ELEMENT {CONFIG_PROBLEMS, CONFIG_LANGUAGES, CONFIG_JUDGEMENT_TYPES, CONFIG_GROUPS,
        CONFIG_ORGANIZATIONS, CONFIG_TEAMS, CONFIG_CONTEST_STATE} 


    private REMOTE_CONFIGURATION_ELEMENT elementType;
    private Map<String, String> map;

    
    /**
     * Contructs a Remote Configuration Object of the specified type and with the specified map (values).
     */
    public AbstractRemoteConfigurationObject(REMOTE_CONFIGURATION_ELEMENT configElementType, 
                                            Map<String,String> configurationValue) {
        this.elementType = configElementType;
        this.map = configurationValue ;
    }
    
    /**
     * Returns the {@link IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT} type
     * associated with this remote configuration object.
     * 
     * @return the REMOTE_CONFIGURATION_ELEMENT type for this object
     */
    REMOTE_CONFIGURATION_ELEMENT getConfigurationType(){
        return elementType ;
    }
    
    /**
     * Returns the Map containing the key:value pairs describing this object's contents
     * 
     * @return the map
     */
    public Map<String, String> getMap() {
        return map;
    }


}
