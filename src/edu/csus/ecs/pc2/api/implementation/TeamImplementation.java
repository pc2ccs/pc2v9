// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.util.HashMap;

import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Implementation for ITeam.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TeamImplementation extends ClientImplementation implements ITeam {

    private String displayName;

    private String shortName;

    private HashMap<ElementId, IGroup> groups = null;
    private IGroup primaryGroup = null;

    public TeamImplementation(ClientId submitter, IInternalContest internalContest) {
        super(submitter, internalContest);

        Account account = internalContest.getAccount(submitter);
        if (account != null) {
            setAccountValues(account, internalContest);
        } else {
            displayName = submitter.getName();
            shortName = submitter.getName();
        }
    }

    private void setAccountValues(Account account, IInternalContest contest) {
        displayName = account.getDisplayName();
        shortName = account.getClientId().getName();
        if (account.getGroupIds() != null) {
            for(ElementId elementId: account.getGroupIds()) {
                if (contest.getGroup(elementId) != null){
                    if(groups == null) {
                        groups = new HashMap<ElementId, IGroup>();
                    }
                    GroupImplementation group = new GroupImplementation(elementId, contest);
                    groups.put(elementId, group);
                    if(primaryGroup == null && account.getPrimaryGroupId() == elementId) {
                        primaryGroup = group;
                    }
                }
            }
        } else {
            groups = null;
        }
    }

    public TeamImplementation(Account account, IInternalContest contest) {
        super(account.getClientId(), contest);
        setAccountValues(account, contest);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public HashMap<ElementId, IGroup> getGroups() {
        return groups;
    }

    @Override
    public String getLoginName() {
        return shortName;
    }

    @Override
    public IGroup getPrimaryGroup() {
        return primaryGroup;
    }

}
