package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestComparisonTest extends AbstractTestCase {

    String NEW_LINE = ContestComparison.NEW_LINE;

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

        // printExpectedDeclaration(actual);

        String expected = "Add 6 Problems;Add 6 Languages;Add 134 Accounts;Add 1 administrator;Add 120 teams;Add 12 judges;Add 1 scoreboard;Add 3 Sites;Add 9 Judgements;";

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

        // printExpectedDeclaration(actual);

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

        String actual = new ContestComparison().comparisonList(contest, contest2);

//         printExpectedDeclaration(actual);

        String expected = "Replace all 6 Problems;Replace all 6 Languages;Replace all 134 Accounts;Replace all 1 administrator;Replace all 120 teams;Replace all 12 judges;Replace all 1 scoreboard;Replace all 3 Sites;Replace all 9 Judgements;Replace all 1 AJ Settings;";

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

    public void testContestsTow() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = new InternalContest();
        IInternalContest contest2 = sample.createStandardContest();

        String actual = new ContestComparison().comparisonList(contest, contest2);

        // printExpectedDeclaration(actual);

        String expected = "Add 6 Problems;Add 6 Languages;Add 134 Accounts;Add 1 administrator;Add 120 teams;Add 12 judges;Add 1 scoreboard;Add 3 Sites;Add 9 Judgements;";

        expected = expected.replaceAll("; ", NEW_LINE);
        expected = expected.replaceAll(";", NEW_LINE);

        assertEquals("Expecting no difference ", expected, actual);
    }

}
