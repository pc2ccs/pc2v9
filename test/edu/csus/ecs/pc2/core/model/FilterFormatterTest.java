package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Test for filter format.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FilterFormatterTest extends TestCase {
    
    private boolean debugMode = false;

    public static void main(String[] args) {
    }

    public FilterFormatterTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProblemList() {

        IInternalContest contest;

        SampleContest sampleContest = new SampleContest();

        contest = sampleContest.createContest(2, 2, 20, 2, false);

        Filter filter = new Filter();

        FilterFormatter filterFormatter = new FilterFormatter();

        String s = filterFormatter.format("%O", contest, filter);
        assertEquals("Filter Off test", "Off", s);

        filter.setFilterOn();

        s = filterFormatter.format("%O", contest, filter);
        /**
         * This is OFF because there are no criteria to filter on so, returns Off
         */
        assertEquals("Filter Off test", "Off", s);

        Problem[] problems = contest.getProblems();

        Problem problem = problems[0];
        filter.addProblem(problem);

        assertTrue("Problem Filter on ", filter.isFilteringProblems());
        assertTrue("Problem Filter one problem ", filter.isFilteringProblems());

        ElementId[] problemIds = filter.getProblemIdList();
        assertEquals("Problem filter same a problem filtered ", problem.getElementId(), problemIds[0]);

        s = filterFormatter.format("%O", contest, filter);
        assertEquals("Filter On test", "On", s);

        // Format for %P

        s = filterFormatter.format("%P", contest, filter);
        assertEquals("Problem list with one problem ", s, problem.toString());

        Problem problem2 = problems[1];
        filter.addProblem(problem2);

        String result = problem + ", " + problem2;
        s = filterFormatter.format("%P", contest, filter);
        assertEquals("Problem list with two problems ", s, result);

        Vector<Account> ve = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) ve.toArray(new Account[ve.size()]);

        Arrays.sort(accounts, new AccountComparator());

        accounts[0].setDisplayName("Uno");
        accounts[1].setDisplayName("Dos");
        accounts[2].setDisplayName("Tres");
        accounts[3].setDisplayName("Quatro");
        accounts[4].setDisplayName("Cinco");
        accounts[5].setDisplayName("Seis");

        filter.addAccount(accounts[0]);
        filter.addAccount(accounts[1]);
        filter.addAccount(accounts[2]);

        filter.addAccount(accounts[4]);
        filter.addAccount(accounts[5]);

        filter.addProblem(problems[2]);

        Language[] languages = contest.getLanguages();
        filter.addLanguage(languages[0]);
        filter.addLanguage(languages[1]);

        Judgement[] judgements = contest.getJudgements();

        filter.addJudgement(judgements[3]);

        s = filterFormatter.format("%#T", contest, filter);
        assertEquals("Number of teams", s, filter.getAccountList().length + "");

        s = filterFormatter.format("%#J", contest, filter);
        assertEquals("Number of judgements", s, filter.getJudgementIdList().length + "");

        s = filterFormatter.format("%#L", contest, filter);
        assertEquals("Number of languages", s, filter.getLanguageIdList().length + "");

        s = filterFormatter.format("%#P", contest, filter);
        assertEquals("Number of problems", s, filter.getProblemIdList().length + "");

        if (debugMode) {
            printAllSpecifiers("Yea", contest, filter);
        }
    }

    void printAllSpecifiers(String prefix, IInternalContest contest, Filter filter) {
        String[] names = { FilterFormatter.ACCOUNT_SPECIFIER, FilterFormatter.JUDGMENTS_SPECIFIER, FilterFormatter.LANGUAGES_SPECIFIER, FilterFormatter.NUMBER_ACCOUNTS_SPECIFIER,
                FilterFormatter.NUMBER_JUDGEMENTS_SPECIFIER, FilterFormatter.NUMBER_LANGUAGES_SPECIFIER, FilterFormatter.NUMBER_PROBLEMS_SPECIFIER, FilterFormatter.PROBLEMS_SPECIFIER,
                FilterFormatter.SHORT_ACCOUNT_NAMES_SPECIFIER, FilterFormatter.TEAM_LIST_SPECIFIER, FilterFormatter.TEAM_LONG_LIST_SPECIFIER, };

        Arrays.sort(names);

        FilterFormatter filterFormatter = new FilterFormatter();
        for (String string : names) {
            System.out.println(prefix + " " + string + " '" + filterFormatter.format(string, contest, filter) + "'");
        }

    }

}
