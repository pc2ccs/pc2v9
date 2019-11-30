// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT;

/**
 * This class encapsulates a configuration obtained from a remote CLICS Contest API; in other words
 * it is a "model" (in the MVC sense) for a remote contest configuration.
 * 
 * It is used to hold a representation of a remote contest configuration during Shadow CCS operations.
 * It provides methods to check whether this RemoteContestConfiguration is "the same as" a local PC2
 * contest configuration, and to obtain a list of the differences between two configurations.
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
     * @param remoteConfigMap a Map giving the contest configuration obtained from a remote CCS 
     */
    public RemoteContestConfiguration(Map<IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT, 
                                            List<AbstractRemoteConfigurationObject>> remoteConfigMap){
        this.map = remoteConfigMap;
    }
    
    /**
     * Returns the Map containing the keywords and corresponding values (obtained from a remote CCS)
     *  which was used to construct this RemoteContestConfiguration.
     * 
     * @return a Map of the key:value pairs in the remote contest configuration
     */
    public Map<IRemoteContestAPIAdapter.REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> 
                    getRemoteContestConfigurationMap() {
        return map;

    }
        
        
    /**
     * Compares the configuration of the specified local PC2 contest with that of the remote contest which was 
     * specified when this RemoteContestConfiguration was constructed; returns true if the
     * configurations are equivalent, false if not.
     * 
     * @param contest
     *            a local PC2 contest
     * 
     * @return true if the configurations are equivalent, otherwise false
     */
    public boolean isSameAs(IInternalContest pc2Contest) {
        List<String> diffs = diff(pc2Contest);
        if (diffs == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares the configuration of the specified local contest against the remote contest configuration 
     * specified when this RemoteContestConfiguration was constructed, returning a list of differences
     * between the two contest configurations.
     * 
     * @param contest
     *            a local PC2 contest whose configuration is to be compared against that of a remote contest
     * @return a List of Strings describing differences between contest configuration, or null if no differences exist
     */
    public List<String> diff(IInternalContest localContest) {

        List<String> retList = null;

        // check if there's anything in the map
        if (map != null && !map.isEmpty()) {

            // there's something in the map; allocate an ArrayList to hold return values
            retList = new ArrayList<String>();

            // process each element in the map
            for (REMOTE_CONFIGURATION_ELEMENT key : map.keySet()) {

                switch (key) {

                    case CONFIG_PROBLEMS  :

                        boolean problemsDiffer = false;

                        // TODO: code here to compare problem element under key with problems in local contest
                        // and set problemsDiffer = true if they differ

                        if (problemsDiffer) {
                            // code here to add remote "problems" element into retList
                        }
                        break;

                    case CONFIG_LANGUAGES:

                        boolean languagesDiffer = false;

                        // TODO: code here to compare languages element under key with languages in local contest
                        // and set the corresponding boolean = true if they differ

                        if (languagesDiffer) {
                            // code here to add remote "languages" element into retList
                        }
                        break;

                    case CONFIG_TEAMS:

                        boolean teamsDiffer = false;

                        // TODO: code here to compare teams element under key with teams in local contest
                        // and set the corresponding boolean = true if they differ

                        if (teamsDiffer) {
                            // code here to add remote "teams" element into retList
                        }
                        break;

                    case CONFIG_JUDGEMENT_TYPES:

                        boolean judgementTypesDiffer = false;

                        // TODO: code here to compare judgement_types element under key with judgementtypes in local contest
                        // and set the corresponding boolean = true if they differ

                        if (judgementTypesDiffer) {
                            // code here to add remote "judgementtypes" element into retList
                        }
                        break;

                    case CONFIG_ORGANIZATIONS:

                        boolean organizationsDiffer = false;

                        // TODO: code here to compare organizations element under key with organizations in local contest
                        // and set the corresponding boolean = true if they differ

                        if (organizationsDiffer) {
                            // code here to add remote "organizations" element into retList
                        }
                        break;

                    case CONFIG_GROUPS:

                        boolean groupsDiffer = false;

                        // TODO: code here to compare groups element under key with groups in local contest
                        // and set the corresponding boolean = true if they differ

                        if (groupsDiffer) {
                            // code here to add remote "groups" element into retList
                        }
                        break;

                    case CONFIG_CONTEST_STATE:

                        boolean contestStatesDiffer = false;

                        // TODO: code here to compare contest state element under key with contest state in local contest
                        // and set the corresponding boolean = true if they differ

                        if (contestStatesDiffer) {
                            // code here to add remote "contest state" element into retList
                        }
                        break;
                        
                    default:
                        //TODO: figure out a better way to handle this issue?
                         throw new UnsupportedOperationException("Unknown Configuration element in remote contest config");

                }
            }

        }
        
        return retList;

    }
        



}
