package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Implementation for ITeam.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TeamImplementation extends ClientImplementation implements ITeam {

    private String longName;

    private String shortName;

    private IGroup group;

    public TeamImplementation(ClientId submitter, IInternalContest internalContest) {
        this(internalContest.getAccount(submitter), internalContest);
    }

    public TeamImplementation(Account account, IInternalContest internalContest) {
        super(account.getClientId(), internalContest);
        
        longName = account.getDisplayName();
        shortName = account.getClientId().getName();
        if (account.getGroupId() == null) {
            group = new GroupImplementation("");
        } else {
            group = new GroupImplementation(account.getGroupId(), internalContest);
        }
    }

    public String getLongName() {
        return longName;
    }

    public IGroup getGroup() {
        return group;
    }

    public String getShortName() {
        return shortName;
    }

    public String getTitle() {
        return longName;
    }
}
