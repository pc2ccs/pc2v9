package edu.csus.ecs.pc2.exports.ccs;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test team.tsv.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class TeamdataTest extends TestCase {
    
    private static final String TAB = "\t";

    private SampleContest sample = new SampleContest();

    public void testEmpty() throws Exception {

        IInternalContest contest = new InternalContest();

        Teamdata teamdata = new Teamdata();

        String[] lines = teamdata.getTeamData(contest);

        assertEquals("Expected number of lines ", 1, lines.length);

    }
    

    void setAccountNamesAndGroups (IInternalContest contest) {
        
        if (contest.getGroups().length == 0){
            Group group1 = new Group("Mississippi");
            group1.setGroupId(1024);
            contest.addGroup(group1);

            Group group2 = new Group("Arkansas");
            group2.setGroupId(2048);
            contest.addGroup(group2);
        }

        Account[] teams = SampleContest.getTeamAccounts(contest);
        
        Group [] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());

        sample.assignTeamGroup(contest, groups[0], 0, teams.length / 2);
        sample.assignTeamGroup(contest, groups[1], teams.length / 2, teams.length);

        int bi = 0;
        int gi = 0;

        Account[] accounts = contest.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            switch (account.getClientId().getClientType()) {
                case TEAM:
                    account.setDisplayName("Team " + SampleContest.BOYS_NAMES[bi++]);
                    break;
                case JUDGE:
                    account.setDisplayName("Judge " + SampleContest.GIRL_NAMES[gi++]);
                    break;
                default:
                    break;
            }
        }
    }

    public void testSimple() throws Exception {

        int numberTeams = 15;
        
        IInternalContest contest = sample.createContest(1, 1, numberTeams, 5, true);
        
//        Account[] accounts = contest.getAccounts();
//        for (Account account : accounts) {
//            System.out.println("account "+account+" "+account.getClientId().getClientNumber());
//        }

        setAccountNamesAndGroups(contest);

        Teamdata teamdata = new Teamdata();

        String[] lines = teamdata.getTeamData(contest);

        assertEquals("Expected number of lines ", numberTeams + 1, lines.length);
        
//        for (String string : lines) {
//            System.out.println(string);
//        }

        String line = lines[1];
        String[] fields = line.split("\t");
        assertEquals("Expect second line team number  ", Integer.toString(1), fields[0]);

        line = lines[lines.length - 1];
        fields = line.split("\t");
        assertEquals("Expect last line team number  ", Integer.toString(numberTeams), fields[0]);
    }
    
    public static void main(String[] args) {

        IInternalContest contest = new SampleContest().createContest(1, 1, 15, 5, true);
       
        TeamdataTest teamdataTest = new TeamdataTest();
        teamdataTest.setAccountNamesAndGroups(contest);

        Teamdata teamdata = new Teamdata();

        String[] lines = teamdata.getTeamData(contest);
        
        for (String line : lines){
            System.out.println(line);
        }
    }
    
    
    public Account[] getTeamAccounts(IInternalContest inContest) {
        Vector<Account> accountVector = inContest.getAccounts(ClientType.Type.TEAM);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }
    
    /**
     * Test teamDataLine.
     * 
     * @throws Exception
     */
    public void testteamDataLine() throws Exception {
        
        Teamdata teamdata = new Teamdata();
        IInternalContest contest = new SampleContest().createStandardContest();
        
        Account [] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        
        Account accountOne = accounts[0];
        
        // Field Description Example Type
        // 1 Team number 22 integer
        // 2 Reservation ID 24314 integer
        // 3 Group ID 4 integer
        // 4 Team name Hoos string
        // 5 Institution name University of Virginia string
        // 6 Institution short name U Virginia string
        // 7 Country USA string ISO 3166-1 alpha-3
        
        String institutionName = "";
        String institutionShortName = "";
        String country = "";
        
        String displayName = "Team 1 Display Name";
        accountOne.setDisplayName(displayName);
        updateAccountFields(accountOne, institutionName, institutionShortName, country);

        String actual = teamdata.teamDataLine(contest, accountOne);
//        String expected = "1" + TAB + "3001" + TAB + "0" + TAB + displayName + TAB + institutionName + TAB + institutionShortName + TAB + country;
        
        institutionName = "undefined";;
        institutionShortName = "undefined";
        country = "XXX";
        
        
        String expected = "1" + TAB + "3001" + TAB + "0" + TAB + displayName + TAB + institutionName + TAB + institutionShortName + TAB + country;
        assertEquals(expected, actual);
        
        Account accountMid = accounts[accounts.length / 2];
        
        institutionName = "University of Virginia";
        institutionShortName = "UOV";
        country = "USA";
        
        updateAccountFields(accountMid, institutionName, institutionShortName, country);

        actual = teamdata.teamDataLine(contest, accountMid);
        expected = "61" + TAB + "3061" + TAB + "0" + TAB + "team61" +  TAB + institutionName + TAB + institutionShortName + TAB + country;
        assertEquals(expected, actual);

    }


    private void updateAccountFields(Account account, String institutionName, String institutionShortName, String countryCode) {
        
        account.setLongSchoolName(institutionName);
        account.setShortSchoolName(institutionShortName);
        account.setCountryCode(countryCode);
        
    }


}
