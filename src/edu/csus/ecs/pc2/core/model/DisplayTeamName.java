package edu.csus.ecs.pc2.core.model;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * displays team name/mask per the TeamDisplayMask setting.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DisplayTeamName implements Serializable, UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1483822798361043778L;

    private IContest contest;

    private IController controller;
    
    private TeamDisplayMask teamDisplayMask = TeamDisplayMask.LOGIN_NAME_ONLY;

    public void setContestAndController(IContest inContest, IController inController) {
        contest = inContest;
        controller = inController;
    }

    public String getDisplayName(ClientId clientId) {
        return getDisplayName(clientId, teamDisplayMask);
    }

    public String getDisplayName(ClientId clientId, TeamDisplayMask inTeamDisplayMask) {
        if (clientId.getClientType().equals(Type.TEAM)) {
            Account account = null;
            
            switch (inTeamDisplayMask) {
                case NONE: 
                    return "***";
                    
                case DISPLAY_NAME_ONLY:
                    account = contest.getAccount(clientId);
                    return account.toString();

                case LOGIN_NAME_ONLY:
                    return clientId.getName();

                case ALIAS:
                    account = contest.getAccount(clientId);
                    if (account.getAliasName() != null && account.getAliasName().trim().length() > 0) {
                        return account.getAliasName();
                    } else {
                        if (controller != null){
                            controller.getLog().log(Log.WARNING, "Alias not created for " + clientId, new Exception("No alias for team " + clientId));
                        }
                        return account.getClientId().getName() + " (not aliased)";
                    }
                case NUMBERS_AND_NAME:
                    account = contest.getAccount(clientId);
                    return clientId.getClientNumber() + " " + account.toString();

                default:
                    return clientId.getName();
            }
        }

        return clientId.getName();
        
    }

    public String getPluginTitle() {
        return "DisplayTeamName";
    }

    public TeamDisplayMask getTeamDisplayMask() {
        return teamDisplayMask;
    }

    public void setTeamDisplayMask(TeamDisplayMask teamDisplayMask) {
        this.teamDisplayMask = teamDisplayMask;
    }

}
