// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This class encapsulates comparison operations between local and remote CCS contest configurations, including determining whether two configurations are equivalent and obtaining differences between
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
     * @param remoteConfig
     *            a RemoteContestConfiguration object containing the configuration of a remote CCS contest
     */
    ShadowContestComparator(RemoteContestConfiguration remoteConfig) {
        this.remoteConfig = remoteConfig;
    }

    /**
     * Compares the configuration of the specified local PC2 contest with that of the remote contest which was specified when this ShadowContestComparator was constructed; returns true if the
     * configurations are equivalent, false if not.
     * 
     * @param contest
     *            a local PC2 contest
     * 
     * @return true if the configurations are equivalent, otherwise false
     */
    public boolean isSameAs(IInternalContest contest) {
        List<String> diffs = diff(contest);
        if (diffs == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Compares the configuration of the specified local contest against the remote contest configuration specified when this ShadowContestComparator was constructed, returning a list of differences
     * between the two contest configurations.
     * 
     * @param contest
     *            a local PC2 contest whose configuration is to be compared against that of a remote contest
     * @return a List of Strings describing differences between contest configuration, or null if no differences exist
     */
    public List<String> diff(IInternalContest localContest) {

        List<String> retList = null;

        // get the map describing the remote contest config
        Map<String, String> remoteConfigMap = remoteConfig.getRemoteContestConfigurationMap();

        // check if there's anything in the map
        if (remoteConfigMap != null && !remoteConfigMap.isEmpty()) {

            // there's something in the map; allocate an ArrayList to hold return values
            retList = new ArrayList<String>();

            // process each element in the map
            for (String key : remoteConfigMap.keySet()) {

                switch (key) {

                    case "problems":

                        boolean problemsDiffer = false;

                        // TODO: code here to compare problem element under key with problems in local contest
                        // and set problemsDiffer = true if they differ

                        if (problemsDiffer) {
                            // code here to add remote "problems" element into retList
                        }
                        break;

                    case "languages":

                        boolean languagesDiffer = false;

                        // TODO: code here to compare languages element under key with languages in local contest
                        // and set the corresponding boolean = true if they differ

                        if (languagesDiffer) {
                            // code here to add remote "languages" element into retList
                        }
                        break;

                    case "teams":

                        boolean teamsDiffer = false;

                        // TODO: code here to compare teams element under key with teams in local contest
                        // and set the corresponding boolean = true if they differ

                        if (teamsDiffer) {
                            // code here to add remote "teams" element into retList
                        }
                        break;

                    case "judgement_types":

                        boolean judgementTypesDiffer = false;

                        // TODO: code here to compare judgement_types element under key with judgementtypes in local contest
                        // and set the corresponding boolean = true if they differ

                        if (judgementTypesDiffer) {
                            // code here to add remote "judgementtypes" element into retList
                        }
                        break;

                    case "organizations":

                        boolean organizationsDiffer = false;

                        // TODO: code here to compare organizations element under key with organizations in local contest
                        // and set the corresponding boolean = true if they differ

                        if (organizationsDiffer) {
                            // code here to add remote "organizations" element into retList
                        }
                        break;

                    case "groups":

                        boolean groupsDiffer = false;

                        // TODO: code here to compare groups element under key with groups in local contest
                        // and set the corresponding boolean = true if they differ

                        if (groupsDiffer) {
                            // code here to add remote "groups" element into retList
                        }
                        break;

                    case "contestState":

                        boolean contestStatesDiffer = false;

                        // TODO: code here to compare contest state element under key with contest state in local contest
                        // and set the corresponding boolean = true if they differ

                        if (contestStatesDiffer) {
                            // code here to add remote "contest state" element into retList
                        }
                        break;

                }
            }

        }
        
        return retList;

    }

}
