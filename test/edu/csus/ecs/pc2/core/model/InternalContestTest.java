package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class InternalContestTest extends TestCase {

    private static final String CONFIG_ACCOUNTS = "ACCOUNTS";

    private static final String CONFIG_BALLOON_SETTINGS = "BALLOON_SETTINGS";

    private static final String CONFIG_CLARIFICATION = "CLARIFICATION";

    private static final String CONFIG_CLIENT_SETTINGS = "CLIENT_SETTINGS";

    private static final String CONFIG_CONTESTTIMES = "CONTESTTIMES";

    private static final String CONFIG_GROUPS = "GROUPS";

    private static final String CONFIG_JUDGEMENTS = "JUDGEMENTS";

    private static final String CONFIG_LANGUAGES = "LANGUAGES";

    private static final String CONFIG_PROBLEMS = "PROBLEMS";

    private static final String CONFIG_PROBLEM_DATAFILES = "PROBLEM_DATAFILES";

    private static final String CONFIG_PROFILES = "PROFILES";

    private static final String CONFIG_RUNS = "RUNS";

    private static final String CONFIG_RUNFILES = "RUNFILES";

    private static final String CONFIG_RUNRESULTSFILE = "RUNRESULTSFILE";

    private static final String CONFIG_SITES = "SITES";

    private static final String CONFIG_SITE_NUMBER = "SITE_NUMBER";

    private static final String CONFIG_GENERAL_PROBLEM = "GENERAL_PROBLEM";

    private static final String CONFIG_CONTEST_INFORMATION = "CONTEST_INFORMATION";

    private static final String CONFIG_CONTESTIDENTIFIER = "CONTESTIDENTIFIER";

    private static final String CONFIG_CONTEST_PASSWORD = "CONTEST_PASSWORD";

    private static final String CONFIG_CLIENTID = "CLIENTID";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Sinmle clone test.
     * 
     * @throws Exception
     */
    public void testClone() throws Exception {

        InternalContest contest1 = new InternalContest();
        InternalContest contest2 = new InternalContest();

        compareContests("testClone identical", contest1, contest2);

        Group group = new Group("Group 1 Title");
        contest1.addGroup(group);
        group = new Group("Group 1 Title");
        contest2.addGroup(group);

        compareContests("identical", contest1, contest2);
    }

    public void testCloneComplex() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest1 = sampleContest.createContest(1, 2, 22, 2, true);
        
        InternalContest contest2 = new InternalContest();

        Profile profile = new Profile("Profile A");
        String profileBasePath = "";
        String password = "foo";
        ProfileCloneSettings settings = new ProfileCloneSettings("name", "title", password.toCharArray());
        
        settings.setCopyAccounts(true);
        settings.setCopyContestSettings(false);
        
        IInternalContest contest3 = contest1.clone(contest2, profile, profileBasePath, settings);
        
        compareContests("testClone identical", contest1, contest3);
    }

    /**
     * Compares the contests and returns differences.
     * 
     * Each line starts with the config area followed by a colon. ex. JUDGEMENTS: or LANGUAGES:
     * <P>
     * The config areas are constants starting with CONFIG_ in this class.
     * 
     * @param contest1
     * @param contest2
     * @return
     */
    public String[] rawCompareContests(IInternalContest contest1, IInternalContest contest2) {

        Vector<String> failures = new Vector<String>();

        // private AccountList accountList = new AccountList();
        // private BalloonSettingsList balloonSettingsList = new BalloonSettingsList();
        // private ClarificationList clarificationList = new ClarificationList();
        // private ClientId localClientId = null;
        // private ClientSettingsList clientSettingsList = new ClientSettingsList();
        // private ContestInformation contestInformation = new ContestInformation();
        // private ContestTimeList contestTimeList = new ContestTimeList();
        // private GroupDisplayList groupDisplayList = new GroupDisplayList();
        // private GroupList groupList = new GroupList();
        // private int siteNumber = 1;
        // private JudgementDisplayList judgementDisplayList = new JudgementDisplayList();
        // private LanguageDisplayList languageDisplayList = new LanguageDisplayList();
        // private Problem generalProblem = null;
        // private ProblemDataFilesList problemDataFilesList = new ProblemDataFilesList();
        // private ProblemList problemList = new ProblemList();
        // private ProfilesList profileList = new ProfilesList();
        // private RunFilesList runFilesList = new RunFilesList();
        // private RunList runList = new RunList();
        // private RunResultsFileList runResultFilesList = new RunResultsFileList();
        // private SiteList siteList = new SiteList();
        // private String contestIdentifier = null;
        // private String contestPassword;

        failures.addElement(CONFIG_BALLOON_SETTINGS + ": no_match");
        failures.addElement(CONFIG_CLARIFICATION + ": no_match");
        failures.addElement(CONFIG_CLIENT_SETTINGS + ": no_match");
        failures.addElement(CONFIG_CONTESTTIMES + ": no_match");

        failures.addElement(CONFIG_JUDGEMENTS + ": no_match");
        failures.addElement(CONFIG_LANGUAGES + ": no_match");
        failures.addElement(CONFIG_PROBLEMS + ": no_match");
        failures.addElement(CONFIG_PROBLEM_DATAFILES + ": no_match");
        failures.addElement(CONFIG_PROFILES + ": no_match");
        failures.addElement(CONFIG_RUNS + ": no_match");
        failures.addElement(CONFIG_RUNFILES + ": no_match");
        failures.addElement(CONFIG_RUNRESULTSFILE + ": no_match");

        failures.addElement(CONFIG_GENERAL_PROBLEM + ": no_match");
        failures.addElement(CONFIG_CONTEST_INFORMATION + ": no_match");
        failures.addElement(CONFIG_CONTESTIDENTIFIER + ": no_match");
        failures.addElement(CONFIG_CONTEST_PASSWORD + ": no_match");
        failures.addElement(CONFIG_CLIENTID + ": no_match");

        failures = new Vector<String>();

        if (contest1.getSiteNumber() != contest2.getSiteNumber()) {
            failures.addElement(CONFIG_SITE_NUMBER + ": " + contest1.getSiteNumber() + " vs " + contest2.getSiteNumber());
        }

        Site[] sites = contest1.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Site site : sites) {

            Site otherSite = contest2.getSite(site.getSiteNumber());

            if (otherSite == null) {
                failures.addElement(CONFIG_SITES + ": < " + site);
            } else {
                compare(failures, CONFIG_SITES, "Site Name", site.getDisplayName(), otherSite.getDisplayName());
                compare(failures, CONFIG_SITES, "Site Password", site.getPassword(), otherSite.getPassword());
                compare(failures, CONFIG_SITES, "Site Element Id", site.getElementId(), otherSite.getElementId());

                compare(failures, CONFIG_SITES, "Site Host", (String) site.getConnectionInfo().get(Site.IP_KEY), (String) otherSite.getConnectionInfo().get(Site.IP_KEY));
                compare(failures, CONFIG_SITES, "Site Port", (String) site.getConnectionInfo().get(Site.PORT_KEY), (String) otherSite.getConnectionInfo().get(Site.PORT_KEY));
            }
        }

        sites = contest2.getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        for (Site site : sites) {

            Site otherSite = contest1.getSite(site.getSiteNumber());

            if (otherSite == null) {
                failures.addElement(CONFIG_SITES + ": > " + site);
            }
        }

        for (Group group : contest1.getGroups()) {

            Group otherGroup = contest2.getGroup(group.getElementId());
            if (otherGroup == null) {
                failures.addElement(CONFIG_GROUPS + ": < " + group + " " + group.getElementId());
            } else {
                compare(failures, CONFIG_GROUPS, "Group Name", group.getDisplayName(), otherGroup.getDisplayName());
                compare(failures, CONFIG_GROUPS, "Group Id", group.getElementId(), otherGroup.getElementId());
            }
        }
        for (Group group : contest2.getGroups()) {

            Group otherGroup = contest1.getGroup(group.getElementId());
            if (otherGroup == null) {
                failures.addElement(CONFIG_GROUPS + ": > " + group + " " + group.getElementId());
            }
        }

        Account[] accounts = contest1.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            Account otherAccount = contest2.getAccount(account.getClientId());

            if (otherAccount == null) {
                failures.addElement(CONFIG_ACCOUNTS + ": < " + account + " " + account.getElementId());
            } else {
                compare(failures, CONFIG_ACCOUNTS, "Display Name", account.getDisplayName(), account.getDisplayName());
                compare(failures, CONFIG_ACCOUNTS, "Group", account.getGroupId(), account.getGroupId());
                compare(failures, CONFIG_ACCOUNTS, "Alias", account.getAliasName(), account.getAliasName());

                // TODO all the rest of the account fields.
            }
        }

        accounts = contest2.getAccounts();
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            Account otherAccount = contest1.getAccount(account.getClientId());

            if (otherAccount == null) {
                failures.addElement(CONFIG_ACCOUNTS + ": < " + account + " " + account.getElementId());
            }
        }

        return (String[]) failures.toArray(new String[failures.size()]);

    }

    private void compare(Vector<String> failures, String contestConfigArea, String comment, ElementId elementId, ElementId elementId2) {

        if (elementId == null && elementId2 == null) {
            return;
        }

        String elementIdString = null;
        if (elementId != null) {
            elementIdString = elementId.toString();
        }
        String elementIdString2 = null;
        if (elementId2 != null) {
            elementIdString2 = elementId2.toString();
        }

        compare(failures, contestConfigArea, comment, elementIdString, elementIdString2);
    }

    private void compare(Vector<String> vector, String contestConfigArea, String comment, String string1, String string2) {

        if (string2 == null) {
            vector.add(contestConfigArea + ": " + comment + " (null" + " vs " + string2 + ")");
        } else if (!string1.equals(string2)) {
            vector.add(contestConfigArea + ": " + comment + " (" + string1 + " vs " + string2 + ")");
        }
    }

    private String[] compareContests(String comment, IInternalContest contest1, IInternalContest contest2) {
        String[] differences = rawCompareContests(contest1, contest2);

        if (differences.length > 0) {
            System.out.println("There were differences in: " + comment);
            for (String line : differences) {
                System.err.println(line);
            }
        }
        return differences;
    }

}
