package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DisplayTeamNameTest extends TestCase {

    public DisplayTeamNameTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOne (){
        
        Contest contest = new Contest();
        
        contest.setSiteNumber(22);
        Vector<Account> accounts = contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 4, true);
        
        DisplayTeamName displayTeamName = new DisplayTeamName();
        displayTeamName.setContestAndController(contest, null);
        
        ClientId id = accounts.firstElement().getClientId();
        
        for (TeamDisplayMask teamDisplayMask : TeamDisplayMask.values()){
            displayTeamName.setTeamDisplayMask(teamDisplayMask);
            System.out.print(id+" is "+displayTeamName.getTeamDisplayMask()+" ");
            System.out.println(displayTeamName.getDisplayName(id));
        }
        
    }
}
