package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 */

public class ContestComparisonTest extends AbstractTestCase {

    private static final String NEW_LINE = ContestComparison.NEW_LINE;

    public void testEmptyContest() throws Exception {

        InternalContest contest = new InternalContest();
        InternalContest contest2 = new InternalContest();

        String s = new ContestComparison().comparisonList(contest, contest2);

        assertEquals("Expecting no difference ", "", s);
    }

    /**
     * Compare empty contest with loaded contest.
     * 
     * @throws Exception
     */
    public void testNewLoadedContest() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = new InternalContest();
        IInternalContest contest2 = sample.createStandardContest();

        String actual = new ContestComparison().comparisonList(contest, contest2);

//         printExpectedDeclaration(actual);

        String expected = "Add 3 Sites;Add 6 Problems;Add 6 Languages;Add 134 Accounts;Add 1 administrator;Add 120 teams;Add 12 judges;Add 1 scoreboard;Add 9 Judgements;";

        expected = expected.replaceAll("; ", NEW_LINE);
        expected = expected.replaceAll(";", NEW_LINE);

        assertEquals("Expecting no difference ", expected, actual);
    }

    public void testAddOneProblem() throws Exception {

        IInternalContest contest = new InternalContest();
        IInternalContest contest2 = new InternalContest();

        SampleContest sample = new SampleContest();
        Problem[] problems = sample.createStandardContest().getProblems();
        contest2.addProblem(problems[0]);

        String actual = new ContestComparison().comparisonList(contest, contest2);

//         printExpectedDeclaration(actual);

        String expected = "Add 1 Problem;";

        expected = expected.replaceAll("; ", NEW_LINE);
        expected = expected.replaceAll(";", NEW_LINE);

        assertEquals("Expecting no difference ", expected, actual);

    }

    /**
     * Compare identical contests.
     * 
     * @throws Exception
     */
    public void testContestsOne() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();
        IInternalContest contest2 = sample.createStandardContest();
        contest2.addSite(sample.createSite(contest2, "Site 4"));
        contest2.addSite(sample.createSite(contest2, "Site 5"));
        
        /**
         * Add AJ settings to both contests
         */
        
        Problem [] problems = contest.getProblems();
        Problem [] ajProblems = { problems[0], problems[1] };
        
        addAJSetting(contest, 2, ajProblems);
        addAJSetting(contest, 4, ajProblems);
        
        problems = contest2.getProblems();
        Problem [] ajProblems2 = { problems[0], problems[1] };
        
        addAJSetting(contest2, 2, ajProblems2);
        addAJSetting(contest2, 4, ajProblems2);
        addAJSetting(contest2, 6, ajProblems2);
        addAJSetting(contest2, 12, ajProblems2);
        
        

        String actual = new ContestComparison().comparisonList(contest, contest2);

//        printExpectedDeclaration(actual);

        String expected = "Replace 3 Sites;Add 2 Sites;Replace all 6 Problems;Replace all 6 Languages;"
                + "Replace all 134 Accounts;Replace all 1 administrator;Replace all 120 teams;Replace all 12 judges;"
                + "Replace all 1 scoreboard;Replace all 9 Judgements;Replace 2 AJ Settingss;Add 2 AJ Settingss;";

        expected = expected.replaceAll("; ", NEW_LINE);
        expected = expected.replaceAll(";", NEW_LINE);

        assertEquals("Expecting no difference ", expected, actual);
    }

    /**
     * Print expected output declaration given actual output.
     * 
     * @param actual
     */
    public void printExpectedDeclaration(String actual) {

        System.out.println(actual);
        actual = actual.replaceAll(NEW_LINE, ";");
        System.out.println("String expected = \"" + actual + "\";");

    }

    public void testContestsTwo() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = new InternalContest();
        IInternalContest contest2 = sample.createStandardContest();

        String actual = new ContestComparison().comparisonList(contest, contest2);

//         printExpectedDeclaration(actual);

        String expected = "Add 3 Sites;Add 6 Problems;Add 6 Languages;Add 134 Accounts;Add 1 administrator;Add 120 teams;Add 12 judges;Add 1 scoreboard;Add 9 Judgements;";

        expected = expected.replaceAll("; ", NEW_LINE);
        expected = expected.replaceAll(";", NEW_LINE);

        assertEquals("Expecting no difference ", expected, actual);
    }
    
    /**
     * Test add AJ settiongs
     */
    public void testAJSettings() throws Exception {
        
        SampleContest sample = new SampleContest();

        IInternalContest contest = new InternalContest();
        IInternalContest contest2 = sample.createStandardContest();
        
        Problem [] problems = contest2.getProblems();
        
        Problem [] ajProblems = { problems[0], problems[1] };
        
        addAJSetting(contest2, 2, ajProblems);
        addAJSetting(contest2, 4, ajProblems);
        addAJSetting(contest2, 6, ajProblems);
        addAJSetting(contest2, 12, ajProblems);

        String actual = new ContestComparison().comparisonList(contest, contest2);

//        printExpectedDeclaration(actual);

        String expected = "Add 3 Sites;Add 6 Problems;Add 6 Languages;Add 134 Accounts;Add 1 administrator;Add 120 teams;Add 12 judges;Add 1 scoreboard;Add 9 Judgements;Add 4 AJ Settingss;";


        expected = expected.replaceAll("; ", NEW_LINE);
        expected = expected.replaceAll(";", NEW_LINE);

        assertEquals("Expecting no difference ", expected, actual);
        
    }

    /**
     * Add a new AJ for a judge
     * @param contest
     * @param judgeNumber judge clientnumber
     * @param ajProblems list of problems 
     */
    private void addAJSetting(IInternalContest contest, int judgeNumber, Problem[] ajProblems) {
        
        Filter filter = new Filter();
        for (Problem problem : ajProblems) {
            filter.addProblem(problem);
        }
        
        ClientId clientId = new ClientId(contest.getSiteNumber(), Type.JUDGE, judgeNumber);
        ClientSettings settings = contest.getClientSettings(clientId);
        if (settings == null){
            settings = new ClientSettings(clientId);
        }
        settings.setAutoJudgeFilter(filter);
        contest.updateClientSettings(settings);
        
    }

}
