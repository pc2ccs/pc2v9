package edu.csus.ecs.pc2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.shadow.AbstractRemoteConfigurationObject;
import edu.csus.ecs.pc2.shadow.AbstractRemoteConfigurationObject.REMOTE_CONFIGURATION_ELEMENT;

/**
 * Convert pc2 model into remote contest model.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestConverter {

    /**
     * Convert pc2 internal contest to remote config map.
     * 
     * @param remoteConfigMap
     * @param contest pc2 contest model
     * @return remoteConfig Map
     */
    public static Map<REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> createConfigMap(
            Map<REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>> remoteConfigMap, IInternalContest contest) {

        if (remoteConfigMap == null) {
            remoteConfigMap = new HashMap<AbstractRemoteConfigurationObject.REMOTE_CONFIGURATION_ELEMENT, List<AbstractRemoteConfigurationObject>>();
        }

        // TODO add contest information

        List<AbstractRemoteConfigurationObject> contestStateList = new ArrayList<AbstractRemoteConfigurationObject>();
        
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put(IContestLoader.CONTEST_NAME_KEY, contest.getTitle());
        contestStateList.add(new AbstractRemoteConfigurationObject(REMOTE_CONFIGURATION_ELEMENT.CONFIG_CONTEST_STATE, valueMap));

        remoteConfigMap.put(REMOTE_CONFIGURATION_ELEMENT.CONFIG_CONTEST_STATE, contestStateList);

        // TODO Add teams
        
        Map<String, String> teamMap = new HashMap<>();
        
        Vector<Account> accounts = contest.getAccounts(Type.TEAM);
        
        for (int i = 0; i < accounts.size(); i++) {
            Account team = accounts.get(i);
            teamMap.put("team"+team.getClientId().getClientNumber()+".title", team.getInstitutionName());
        }
        
        contestStateList.add(new AbstractRemoteConfigurationObject(REMOTE_CONFIGURATION_ELEMENT.CONFIG_TEAMS, teamMap));
        
        // TODO add problems

        // TODO add languages

        return remoteConfigMap;
    }

}
