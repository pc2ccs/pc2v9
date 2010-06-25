package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IElementObject;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.ClientSettings}s.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ClientSettingsList extends BaseElementList {

    /**
     * 
     */
    private static final long serialVersionUID = -7236148308850761761L;

    /**
     * 
     * @param clientSettings
     *            {@link ClientSettings} to be added.
     */
    public void add(ClientSettings clientSettings) {
        super.add(clientSettings);

    }

    /**
     * Return list of ClientSettingss.
     * 
     * @return list of {@link ClientSettings}.
     */
    public ClientSettings[] getList() {
        ClientSettings[] theList = new ClientSettings[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (ClientSettings[]) values().toArray(new ClientSettings[size()]);
        return theList;
    }

    @Override
    public String getKey(IElementObject elementObject) {
        ClientSettings clientSettings = (ClientSettings) elementObject;
        return clientSettings.getClientId().toString();
    }
    
    public ClientSettings get(ClientId clientId){
        return (ClientSettings) super.get(new ClientSettings(clientId));
    }
}
