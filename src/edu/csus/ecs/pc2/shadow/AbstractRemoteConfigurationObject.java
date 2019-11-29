// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.

package edu.csus.ecs.pc2.shadow;

import java.util.Map;

import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT;

/**
 * This class is the parent class (base class) for concrete representations of remote contest
 * configuation elements such as Problems, Languages, etc.
 * 
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class AbstractRemoteConfigurationObject {

    private REMOTE_CONFIGURATION_ELEMENT elementType;
    private Map<String, String> map;

    
    /**
     * Contructs a Remote Configuration Object of the specified type and with the specified map (values).
     */
    public AbstractRemoteConfigurationObject(IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT configElementType, 
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
    IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT getConfigurationType(){
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
