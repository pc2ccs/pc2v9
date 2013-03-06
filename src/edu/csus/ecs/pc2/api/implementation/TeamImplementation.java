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

    private String displayName;

    private String shortName;

    private IGroup group = null;

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
        if (account.getGroupId() != null) {
            // SOMEDAY ensure that groupId is not null
            // this happened on load of teams.tsv and groups.tsv
            if (contest.getGroup(account.getGroupId()) != null){
                group = new GroupImplementation(account.getGroupId(), contest);
            }
        }
    }

    public TeamImplementation(Account account, IInternalContest contest) {
        super(account.getClientId(), contest);
        setAccountValues(account, contest);
    }

    public String getDisplayName() {
        return displayName;
    }

    public IGroup getGroup() {
        return group;
    }

    public String getLoginName() {
        return shortName;
    }

}
