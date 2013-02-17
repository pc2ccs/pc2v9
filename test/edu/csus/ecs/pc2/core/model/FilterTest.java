package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Test Filter class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$ 
 */

// $HeadURL$
public class FilterTest extends TestCase {
    
    // TODO test all other filter types (run, clar states, etc.)

    public void testProblemFilter() {
        Filter filter = new Filter();

        assertFalse("Problem Filter should be disabled ", filter.isFilteringProblems());
        
        filter.setUsingProblemFilter(true);
        assertTrue ("Problem Filter should be enabled ", filter.isFilteringProblems());
        
        filter.setUsingProblemFilter(false);
        assertFalse("Problem Filter should be disabled ", filter.isFilteringProblems());
        
        
        String [] problemTitles = {"Problem A", "Problem B", "Problem C" };
        
        Problem [] problems = new Problem[problemTitles.length];
        
        int i = 0;
        for (String name : problemTitles){
            problems[i] = new Problem(name);
            i++;
        }
        
        Problem notInFilterProblem = problems[2];
        Problem problemInFilter = problems[1];
        
        filter.addProblem(problemInFilter);
        assertTrue ("Problem Filter should be enabled ", filter.isFilteringProblems());
        
        assertTrue("Problem "+problemInFilter+" should be found in filter ", filter.matches(problemInFilter));

        filter.addProblem(problems[0]);
        
        assertTrue("Problem "+problemInFilter+" should be found in filter ", filter.matches(problemInFilter));
        assertFalse("Problem "+notInFilterProblem+" should not be found in filter ", filter.matches(notInFilterProblem));
        
        filter.removeProblem(problemInFilter);
        assertFalse("Problem "+problemInFilter+" should not be found in filter ", filter.matches(problemInFilter));

        filter.addProblem(problemInFilter);
        filter.addProblem(problemInFilter);
        
        assertTrue("Problem "+problemInFilter+" should be found in filter ", filter.matches(problemInFilter));
        
    }

    public void testProblemFilterList() {
        
        Filter filter = new Filter();

        String [] problemTitles = {"Problem A", "Problem B", "Problem C" };
        
        Problem [] problems = new Problem[problemTitles.length];
        
        int i = 0;
        for (String name : problemTitles){
            problems[i] = new Problem(name);
            i++;
        }
        
        ElementId [] elementIds = filter.getProblemIdList();
        
        assertTrue ("No elements ids for problems ", elementIds.length == 0);
        
        for (Problem problem : problems){
            filter.addProblem(problem);
        }
        
        elementIds = filter.getProblemIdList();
        
//        for (ElementId elementId : elementIds){
//            System.out.println("Problem in filter: "+elementId);
//        }

        assertTrue ("Should be "+problems.length+" in list found "+elementIds.length, elementIds.length == problems.length);
        
    }
    
    /**
     * Test match for the elapsed time range in filter.
     * 
     */
    public void testElapsedTime(){
        
        Problem problem = new Problem("First Problem");
        Language language = new Language("LangName");

        ClientId clientId = new ClientId(1, ClientType.Type.TEAM, 4);

        Run run = new Run(clientId, language, problem);

        /**
         * Set submission time to 220 minutes.
         */
        run.setElapsedMins(220);

        Judgement judgementYes = new Judgement("Yes");
        judgementYes.setAcronym(Judgement.ACRONYM_ACCEPTED);

        ElementId judgementId = judgementYes.getElementId();

        ClientId judgerClientId = new ClientId(1, Type.JUDGE, 4);
        boolean solved = true;
        JudgementRecord record = new JudgementRecord(judgementId, judgerClientId, solved, false);
        int testNumber = 1;

        RunTestCase testCase = new RunTestCase(run, record, testNumber, solved);
        testCase.setElapsedMS(run.getElapsedMS());

        Filter filter = new Filter();
        
        /**
         * Set start elapsed time to 200, 220 > 200 so should 'match'
         */
        filter.setStartElapsedTime(200);
        
        assertTrue("Should match time range ", filter.matchesElapsedTime(run));
        assertTrue("Should match time range ", filter.matchesElapsedTime(testCase));
        
        /**
         * Set end time, range is no 200 - 220, 220 <= 220
         */
        filter.setEndElapsedTime(220);
        assertTrue("Should match time range ", filter.matchesElapsedTime(run));
        assertTrue("Should match time range ", filter.matchesElapsedTime(testCase));
        
        /**
         * Set time to 22, 22 is not between range of 200 - 220 
         */
        
        run.setElapsedMins(22);
        testCase.setElapsedMS(run.getElapsedMS());
        
//        System.out.println("debug 22 - run      "+run.getElapsedMS());
//        System.out.println("debug 22 - testcase "+testCase.getElapsedMS());
        
        assertFalse("Should NOT match time range ", filter.matchesElapsedTime(run));
        assertFalse("Should NOT match time range ", filter.matchesElapsedTime(testCase));
        
        /**
         * Set range to 4 - 5 hours.
         */
        long mins = Constants.MINUTES_PER_HOUR * 4;
        filter.setStartElapsedTime(mins);
        filter.setEndElapsedTime(mins + Constants.MINUTES_PER_HOUR);
        
        /**
         * 
         */
        run.setElapsedMins(mins + 30);
        testCase.setElapsedMS(run.getElapsedMS());
        
        assertTrue("Should match time range ", filter.matchesElapsedTime(run));
        assertTrue("Should match time range ", filter.matchesElapsedTime(testCase));
        
        
    }
    
    public void testClientType(){
        
        Filter filter = new Filter();
        filter.addClientType(Type.TEAM);
        
        ClientId clientId = new ClientId(1,ClientType.Type.TEAM, 4);
        Account account = new Account(clientId, "pass", 1);
        
        assertTrue("Match on ClientType Team ", filter.matches(account));
        
        ClientId judgeId = new ClientId(1,ClientType.Type.JUDGE, 4);
        account = new Account(judgeId, "pass", 1);

        assertFalse("Match on ClientType Team for "+account, filter.matches(account));
        
        filter = new Filter();
        filter.addAccount(account);
        
        assertTrue("Match on Account "+account, filter.matches(account));
    }
    
    public void testAccountFiltering() throws Exception {

        int numTeams = 3;
        int numJudges = 12;
        int numAdmins = 1;

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(3, 3, numTeams, numJudges, true);
        contest.generateNewAccounts(Type.ADMINISTRATOR.toString(), numAdmins, true);
        // IInternalController controller = sample.createController(contest, true, false);

        Account[] accounts = contest.getAccounts();

        Filter filter = new Filter();
        assertEquals("Filtered Accounts should match", accounts.length, filter.countAccounts(accounts));

        // All accounts from site 3 no matches
        filter.addSite(1);
        assertEquals("Filtered Accounts should match", 0, filter.countAccounts(accounts));

        filter.addSite(3);
        assertEquals("Filtered Accounts should match", accounts.length, filter.countAccounts(accounts));

        filter.clearSiteList();

        filter.addPermission(Permission.Type.SUBMIT_RUN);

//        for (Account account : accounts) {
//            if (filter.matches(account)) {
//                System.out.println("Matches " + account);
//            }
//        }

        assertEquals("Filtered Accounts should match", numTeams + numAdmins, filter.countAccounts(accounts));

        filter.clearPermissionsList();
        filter.addPermission(Permission.Type.EDIT_RUN);
        assertEquals("Filtered Accounts should match", numAdmins, filter.countAccounts(accounts));

        filter.addPermission(Permission.Type.JUDGE_RUN);
        filter.addPermission(Permission.Type.ADD_SITE);
        assertEquals("Filtered Accounts should match", numAdmins, filter.countAccounts(accounts));

        filter.clearPermissionsList();
        filter.addPermission(Permission.Type.JUDGE_RUN);
        assertEquals("Filtered Accounts should match", numAdmins + numJudges, filter.countAccounts(accounts));

    }
    
    
    
}
