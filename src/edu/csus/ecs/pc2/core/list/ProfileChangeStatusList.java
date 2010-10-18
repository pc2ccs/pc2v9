package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.ProfileChangeStatus;

/**
 * Maintain a list of {@#link ProfileChangeStatus}.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ProfileChangeStatusList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = 2427690513644624376L;

    public void add(ProfileChangeStatus profileChangeStatus) {
        super.add(profileChangeStatus);

    }

    public ProfileChangeStatus[] getList() {
        ProfileChangeStatus[] theList = new ProfileChangeStatus[size()];

        if (theList.length == 0) {
            return theList;
        }
        return (ProfileChangeStatus[]) values().toArray(new ProfileChangeStatus[size()]);
    }
    
    public boolean updateStatus(ProfileChangeStatus profileChangeStatus, ProfileChangeStatus.Status status) {
        ProfileChangeStatus profileStatus = (ProfileChangeStatus) get(profileChangeStatus);
        profileStatus.setStatus(status);
        return super.update(profileStatus);
    }

}
