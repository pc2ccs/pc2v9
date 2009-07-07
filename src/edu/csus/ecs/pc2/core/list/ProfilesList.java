package edu.csus.ecs.pc2.core.list;

import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Profile}s.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesList extends ElementList {

    /**
     * 
     */
    private static final long serialVersionUID = 8053018965607735092L;

    /**
     * 
     * @param profile
     *            {@link Profile} to be added.
     */
    public void add(Profile profile) {
        super.add(profile);
    }

    /**
     * Return list of Languages.
     * 
     * @return list of {@link Profile}.
     */
    public Profile[] getList() {
        Profile[] theList = new Profile[size()];

        if (theList.length == 0) {
            return theList;
        }

        theList = (Profile[]) values().toArray(new Profile[size()]);
        return theList;
    }
}
