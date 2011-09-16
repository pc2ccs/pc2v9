package edu.csus.ecs.pc2.exports.ccs;

import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test team.tsv
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class TeamdataTest extends TestCase {


    private static final String[] GIRL_NAMES = { "Abigail", "Aimee", "Alexandra", "Alice", "Alisha", "Amber", "Amelia", "Amelie", "Amy", "Anna", "Ava", "Bethany", "Brooke", "Caitlin", "Charlotte",
            "Chloe", "Daisy", "Eleanor", "Elizabeth", "Ella", "Ellie", "Emilia", "Emily", "Emma", "Erin", "Esme", "Eva", "Eve", "Evelyn", "Evie", "Faith", "Florence", "Francesca", "Freya", "Georgia",
            "Grace", "Gracie", "Hannah", "Harriet", "Heidi", "Hollie", "Holly", "Imogen", "Isabel", "Isabella", "Isabelle", "Isla", "Isobel", "Jasmine", "Jessica", "Julia", "Katie", "Keira", "Lacey",
            "Lauren", "Layla", "Leah", "Lexi", "Lexie", "Libby", "Lilly", "Lily", "Lola", "Lucy", "Lydia", "Maddison", "Madison", "Maisie", "Maria", "Martha", "Matilda", "Maya", "Megan", "Mia",
            "Millie", "Molly", "Mya", "Niamh", "Nicole", "Olivia", "Paige", "Phoebe", "Poppy", "Rebecca", "Rose", "Rosie", "Ruby", "Sara", "Sarah", "Scarlett", "Sienna", "Skye", "Sofia", "Sophia",
            "Sophie", "Summer", "Tia", "Tilly", "Zara", "Zoe", };

    private static final String[] BOYS_NAMES = { "Aaron", "Adam", "Aidan", "Aiden", "Alex", "Alexander", "Alfie", "Archie", "Arthur", "Ashton", "Austin", "Bailey", "Ben", "Benjamin", "Bradley",
            "Brandon", "Callum", "Cameron", "Charles", "Charlie", "Christopher", "Connor", "Daniel", "David", "Dylan", "Edward", "Elliot", "Ellis", "Ethan", "Evan", "Ewan", "Finlay", "Finley",
            "Freddie", "Frederick", "Gabriel", "George", "Harley", "Harrison", "Harry", "Harvey", "Hayden", "Henry", "Isaac", "Jack", "Jacob", "Jake", "James", "Jamie", "Jayden", "Joe", "Joel",
            "John", "Joseph", "Joshua", "Jude", "Kai", "Kian", "Kieran", "Kyle", "Leo", "Leon", "Lewis", "Liam", "Logan", "Louie", "Louis", "Luca", "Lucas", "Luke", "Mason", "Matthew", "Max",
            "Michael", "Mohammad", "Mohammed", "Morgan", "Muhammad", "Nathan", "Noah", "Oliver", "Oscar", "Owen", "Reece", "Reuben", "Rhys", "Riley", "Robert", "Ryan", "Sam", "Samuel", "Sebastian",
            "Stanley", "Taylor", "Theo", "Thomas", "Toby", "Tyler", "William", "Zachary", };

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

        Account[] teams = sample.getTeamAccounts(contest);
        
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
                    account.setDisplayName("Team " + BOYS_NAMES[bi++]);
                    break;
                case JUDGE:
                    account.setDisplayName("Judge " + GIRL_NAMES[gi++]);
                    break;
                default:
                    break;
            }
        }
    }

    public void testSimple() throws Exception {

        IInternalContest contest = sample.createContest(1, 1, 15, 5, true);

        setAccountNamesAndGroups(contest);

        Teamdata teamdata = new Teamdata();

        String[] lines = teamdata.getTeamData(contest);

        assertEquals("Expected number of lines ", 16, lines.length);
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

}
