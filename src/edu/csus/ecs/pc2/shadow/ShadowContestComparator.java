// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.List;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class encapsulates comparison operations between local and remote CCS contest configurations,
 * including determining whether two configurations are equivalent and obtaining differences between
 * two configurations.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class ShadowContestComparator {
    
    private RemoteContestConfiguration remoteConfig;

    /**
     * Constructs a comparator for the specified remote contest.
     * 
     * @param remoteConfig a RemoteContestConfiguration object containing the configuration of a remote CCS contest
     */
    ShadowContestComparator (RemoteContestConfiguration remoteConfig){
        this.remoteConfig = remoteConfig;
    }
    
    /**
     * Compares the configuration of the specified local PC2 contest with that of the remote contest which
     * was specified when this ShadowContestComparator was constructed; returns true if the configurations
     * are equivalent, false if not.
     * 
     * @param contest a local PC2 contest
     * 
     * @return true if the configurations are equivalent, otherwise false
     */
    public boolean isSameAs(IInternalContest contest){
        List<String> diffs = diff(contest);
        if (diffs==null) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Compares the configuration of the specified local contest against the remote contest
     * configuration specified when this ShadowContestComparator was constructed, returning a list
     * of differences between the two contest configurations.
     *  
     * @param contest a local PC2 contest whose configuration is to be compared against that of a remote contest
     * @return a List of Strings describing differences between contest configuration, or null if no differences exist
     */
    public List<String> diff (IInternalContest localContest) {
        // TODO Bug 1261 code
        throw new NotImplementedException();
    }


    
    
}
