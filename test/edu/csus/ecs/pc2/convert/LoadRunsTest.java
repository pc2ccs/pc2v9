package edu.csus.ecs.pc2.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.imports.LoadICPCTSVData;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.report.ResolverEventFeedReport;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.ICPCTSVLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Unit Tests.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class LoadRunsTest extends AbstractTestCase {

    public static final String TEAMS_FILENAME = LoadICPCTSVData.TEAMS_FILENAME;

    public static final String GROUPS_FILENAME = LoadICPCTSVData.GROUPS_FILENAME;

    private String teamsFilename = "";

    private String groupsFilename = "";

    public LoadRunsTest(String name) {
        super(name);
    }

    public void testLoadCDP() throws Exception {

        String configDir = SampleCDP.getDir() + IContestLoader.CONFIG_DIRNAME;

        // startExplorer(configDir);

        try {

            IInternalContest contest = new InternalContest();

            contest = loadYaml(contest, configDir);

            Problem[] problems = contest.getProblems();

            assertEquals("Expecting N problems", 13, problems.length);

            Account[] teamAccounts = getTeamAccounts(contest);

            assertEquals("Expecting N problems", 13, problems.length);

            assertEquals("Expecting N Teams ", 128, teamAccounts.length);

        } catch (Exception e) {

            System.err.println(e.getMessage());
            throw e;
        }

    }

    /**
     * Load contest from contest.yaml.
     * 
     * @param contest
     * @param configDir
     * @return
     */
    private IInternalContest loadYaml(IInternalContest contest, String configDir) {

        // startExplorer(configDir);

        String contestYamlFile = configDir + File.separator + IContestLoader.DEFAULT_CONTEST_YAML_FILENAME;

        assertFileExists(contestYamlFile, "Contest yaml file ");

        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();

        contest = loader.fromYaml(contest, configDir, false);

        return contest;

    }

    private Account[] getTeamAccounts(IInternalContest contest) {
        Vector<Account> accounts = contest.getAccounts(Type.TEAM);
        return (Account[]) accounts.toArray(new Account[accounts.size()]);
    }

    /**
     * Test loading runs and EF runs content.
     * 
     * @throws Exception
     */
    public void testLoadEFRuns() throws Exception {

        String configDir = SampleCDP.getDir() + IContestLoader.CONFIG_DIRNAME;

        String inputEventFeed = SampleCDP.getEventFeedFilename();

        IInternalContest contest = new InternalContest();

        contest = loadYaml(contest, configDir);

        XMLDomParse1 parse1 = new XMLDomParse1();
        Document document = parse1.create(inputEventFeed);

        assertNotNull(document);

        String path = "/contest/run/*";
        NodeList nodes = parse1.getNodes(document, path);

        Properties[] runPropertyList = parse1.create(nodes, EventFeedRun.ID_TAG_NAME);

        List<EventFeedRun> runs = EventFeedRun.toRuns(runPropertyList, true);

        assertEquals("Expected run count ", 1494, runs.size());

        String startTimeDecimalString = "1432116000.00000"; // 2015 finals

        int zeroCount = 0;

        for (EventFeedRun evRun : runs) {
            long ms = evRun.getElapsedMS();

            if (ms <= 0) {
                zeroCount++;
            }

            if (isDebugMode() && zeroCount < 10) {
                long startMS = EventFeedUtilities.toMS(startTimeDecimalString);
                long runMS = EventFeedUtilities.toMS(evRun.getTime());
                long min = runMS / 60 / 100;
                System.out.println(" time is " + evRun.getTime() + " runMS = " + runMS + " contest start " + startMS + " min = " + min);
            }
        }

        assertEquals("Expecting no zero ms elapsed time ", 0, zeroCount);

    }

    /**
     * Load contest runs based on yaml.
     * 
     * @throws Exception
     */
    public void testLoadContestRuns() throws Exception {

        String configDir = SampleCDP.getDir() + IContestLoader.CONFIG_DIRNAME;

        String inputEventFeed = SampleCDP.getEventFeedFilename();

        String outputEventFeedFilename = getOutputTestFilename("ef." + this.getName() + ".xml");

        IInternalContest contest = new InternalContest();

        // startExplorer(getOutputDataDirectory());

        contest = loadYaml(contest, configDir);

        IInternalController controller = new SampleContest().createController(contest, true, false);

        XMLDomParse1 parse1 = new XMLDomParse1();
        Document document = parse1.create(inputEventFeed);

        assertNotNull(document);

        String path = "/contest/run/*";
        NodeList nodes = parse1.getNodes(document, path);

        Properties[] runPropertyList = parse1.create(nodes, EventFeedRun.ID_TAG_NAME);

        List<EventFeedRun> runs = EventFeedRun.toRuns(runPropertyList, true);

        LoadRuns loader = new LoadRuns();

        // Properties properties = new Properties();
        // properties.put(LoadRuns.CDPPATH, SampleCDP.getDir());

        try {

            loadDefaultJudgements(contest);

            contest = loader.updateContestFromEFRuns(contest, runs, SampleCDP.getDir());

            updateGroupAndTeams(contest, SampleCDP.getConfigDir());

            finalizeContest(contest);

            Run[] runs2 = contest.getRuns();

            assertEquals("Expecting all runs loaded ", runs.size(), runs2.length);

            ResolverEventFeedReport report = new ResolverEventFeedReport();

            report.setContestAndController(contest, controller);

            // report.createReportFile(outputEventFeedFilename, new Filter());

            String xml = report.createReportXML(new Filter());

            writeFile(outputEventFeedFilename, xml);

            compareFeeds(inputEventFeed, outputEventFeedFilename);

        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Compare event feeds.
     * 
     * @param inputEventFeed
     * @param outputEventFeedFilename
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    private void compareFeeds(String inputEventFeed, String outputEventFeedFilename) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {

        String inFile = getOutputTestFilename("eventfile.in.txt");
        String outFile = getOutputTestFilename("eventfile.out.txt");

        createEventFeedMetaFile(inputEventFeed, inFile);
//        System.out.println("Wrote file: " + inFile);
        createEventFeedMetaFile(outputEventFeedFilename, outFile);
//        System.out.println("Wrote file: " + outFile);

        // startExplorer(getOutputDataDirectory());
    }

    private void createEventFeedMetaFile(String eventFeedFile, String outputfilename) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {

        List<EventFeedRun> runs = new EFLoader().loadFile(eventFeedFile);
        Collections.sort(runs, new CompareByRunId());

        PrintWriter writer = null;
        writer = new PrintWriter(new FileOutputStream(outputfilename, false), true);

        for (EventFeedRun evRun : runs) {

            writer.print(" Run " + evRun.getId());
            writer.print(" teamAccount = " + evRun.getTeam());
            writer.print(" result  = " + evRun.getResult());
            writer.print(" time = " + evRun.getTime());
            writer.print(" problem = " + evRun.getProblem());
            writer.print(" language = " + evRun.getLanguage());
            writer.println();
        }

        writer.close();
        writer = null;
    }

    private void finalizeContest(IInternalContest contest) {

        // <finalized>
        // <comment>Certified by: Jeff, entered by Mikael</comment>
        // <last-bronze>12</last-bronze>
        // <last-gold>4</last-gold>
        // <last-silver>8</last-silver>
        // <time>18000.000000</time>
        // <timestamp>1432135103.277650</timestamp>
        // </finalized>
        // </contest>

        FinalizeData finalizeData = new FinalizeData();

        finalizeData.setGoldRank(4);
        finalizeData.setSilverRank(8);
        finalizeData.setBronzeRank(12);

        finalizeData.setCertified(true);
        finalizeData.setComment("Created by " + this.getName());

        contest.setFinalizeData(finalizeData);

    }

    private void writeFile(String filename, String xml) throws FileNotFoundException {

        PrintWriter printWriter = null;
        printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        printWriter.println(xml);
        printWriter.close();
        printWriter = null;
    }

    private String[] acronymList = { //
    "Yes;AC", //
            "No - Compilation Error;CE", //
            "No - Security Violation;SV", //
            "No - Time Limit Exceeded;TLE", //
            "No - Run Time Exception;RTE", //
            "No - Wrong Output;WA", //
            "Accepted;AC", //
            "Wrong Answer;WA", //
    };

    private void loadDefaultJudgements(IInternalContest contest) {

        // Judgement[] judgements = contest.getJudgements();
        // for (Judgement judgement : judgements) {
        // System.out.println("debug 22 found "+judgement.getAcronym()+" "+judgement.getDisplayName());
        // }
        // System.out.println("debug 22 there are "+judgements.length+" judgements.");

        for (String line : acronymList) {

            String[] fields = line.split(";");
            String name = fields[0];
            String acronym = fields[1];
            contest.addJudgement(new Judgement(name, acronym));
        }
    }

    /**
     * Test loading runs into model but not from yaml.
     * 
     * @throws Exception
     */
    public void testLoadRuns() throws Exception {

        String inputEventFeed = SampleCDP.getEventFeedFilename();

        String outputEventFeedFilename = getOutputTestFilename("ef." + this.getName() + ".xml");

        String dir = SampleCDP.getDir();
        ensureDirectory(dir);
        // startExplorer(dir);

        assertFileExists(inputEventFeed);

        XMLDomParse1 parse1 = new XMLDomParse1();
        Document document = parse1.create(inputEventFeed);

        assertNotNull(document);

        String path = "/contest/run/*";
        NodeList nodes = parse1.getNodes(document, path);

        Properties[] runPropertyList = parse1.create(nodes, EventFeedRun.ID_TAG_NAME);
        List<EventFeedRun> runs = EventFeedRun.toRuns(runPropertyList, true);

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createStandardContest();
        IInternalController controller = sampleContest.createController(contest, true, false);

        int numTeams = 128 - contest.getAccounts(Type.TEAM).size();
        contest.generateNewAccounts(Type.TEAM.toString(), numTeams, true);

        int pCount = EventFeedUtilities.getMaxProblem(runs);

        int numProblems = contest.getProblems().length;
        int missingProblems = pCount - numProblems;
        for (int i = 0; i < missingProblems; i++) {
            Problem problem = new Problem("Problem " + (i + 1));
            char let = 'A';
            let += i;
            contest.addProblem(problem);
            problem.setLetter("" + let);
            assertNotNull("Expecting letter", problem.getLetter());
        }

        numProblems = contest.getProblems().length;

        LoadRuns loader = new LoadRuns();

        try {

            contest = loader.updateContestFromEFRuns(contest, runs, SampleCDP.getDir());
            updateGroupAndTeams(contest, SampleCDP.getConfigDir());

            Run[] runs2 = contest.getRuns();

            assertEquals("Expecting all runs loaded ", runs.size(), runs2.length);

            ResolverEventFeedReport report = new ResolverEventFeedReport();

            report.setContestAndController(contest, controller);
            report.createReportFile(outputEventFeedFilename, new Filter());

        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }

        assertFileExists(outputEventFeedFilename);

        // startExplorer(".");

    }

    //
    // public void testEventFeedLoad() throws Exception {
    //
    // String dir = SampleCDP.getDir();
    // String inputEventFeed = SampleCDP.getEventFeedFilename();
    //
    // ensureDirectory(dir);
    // // startExplorer(dir);
    //
    // assertFileExists(inputEventFeed);
    //
    // XMLDomParse1 parse1 = new XMLDomParse1();
    // Document document = parse1.create(inputEventFeed);
    //
    // assertNotNull(document);
    //
    // String xPath = "/contest/run/*";
    // NodeList nodes = parse1.getNodes(document, xPath);
    //
    // Properties[] runPropertyList = parse1.create(nodes, EventFeedRun.ID_TAG_NAME);
    //
    // List<EventFeedRun> runs = EventFeedRun.toRuns(runPropertyList, true);
    //
    // Collections.sort(runs, new CompareByRunId());
    //
    // // print runs
    // // for (EventFeedRun eventFeedRun : runs) {
    // // System.out.println(eventFeedRun.getId() + " " + eventFeedRun.getResult() + " " + //
    // // eventFeedRun.getTime() + " " + eventFeedRun.getTimestamp());
    // // }
    //
    // assertEquals("Expecting runs ", 1494, runs.size());
    // assertEquals("Expecting first runid ", "20", runs.get(0).getId());
    // assertEquals("Expecting last  runid ", "1518", runs.get(runs.size()-1).getId());
    //
    // // fetch all runs.
    // runs = EventFeedRun.toRuns(runPropertyList, false);
    // assertEquals("Expecting runs ", 2988, runs.size());
    //
    // }

    /**
     * Load groups.tsv and teams.tsv files.
     * 
     * @param contest
     * @param dir
     * @throws Exception
     */
    private void updateGroupAndTeams(IInternalContest contest, String dir) throws Exception {

        if (contest.getSites().length == 0) {
            contest.addSite(new Site("Site 1", 1));
        }
        // TODO GF 3363 load groups.tsv

        // TODO GF 3363 load teams.tsv

        String filename = dir + File.separator + TEAMS_FILENAME;

        if (checkFiles(filename)) {

            Group[] groups = ICPCTSVLoader.loadGroups(groupsFilename);

            for (Group group : groups) {
                group.setSite(contest.getSites()[0].getElementId());
            }

            Account[] accounts = ICPCTSVLoader.loadAccounts(teamsFilename);

            List<Group> groupList = Arrays.asList(groups);
            List<Account> accountList = Arrays.asList(accounts);

            /**
             * Merge/update groups and account into existing groups and accounts, create if necessary
             */
            updateGroupsAndAccounts(contest, groupList, accountList);

            /**
             * Update Groups
             */
            Group[] updatedGroups = (Group[]) groupList.toArray(new Group[groupList.size()]);
            for (Group group : updatedGroups) {
                // getController().updateGroup(group);
                contest.updateGroup(group);
            }
            //
            /**
             * UpdateAccounts
             */
            Account[] updatedAccounts = (Account[]) accountList.toArray(new Account[accountList.size()]);
            // getController().updateAccounts(updatedAccounts);
            for (Account account : updatedAccounts) {
                contest.updateAccount(account);
            }
        }
    }

    /**
     * Lookup group by externalId
     * 
     * @param contest2
     * @param externalId
     * @return
     */
    private Group lookupGroup(IInternalContest contest2, int externalId) {

        Group[] groups = contest2.getGroups();
        for (Group group : groups) {
            if (group.getGroupId() == externalId) {
                return group;
            }
        }
        return null;
    }

    protected void updateGroupsAndAccounts(IInternalContest inContest, List<Group> groupList, List<Account> accountList) {

        int i = 0;

        for (Group group : groupList) {

            Group existingGroup = lookupGroup(inContest, group.getGroupId());
            if (existingGroup != null) {

                /**
                 * Update certain fields in group
                 */
                existingGroup.updateFrom(group);

            } else {
                existingGroup = group;
            }

            groupList.set(i, existingGroup);

            i++;
        }

        i = 0;

        for (Account account : accountList) {

            Account existingAccount = inContest.getAccount(account.getClientId());

            if (existingAccount != null) {
                existingAccount.updateFrom(account);
            } else {
                existingAccount = account;
            }

            accountList.set(i, existingAccount);
            i++;
        }

    }

    protected boolean checkFiles(String filename) throws Exception {

        File file = new File(filename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        if (filename.endsWith(TEAMS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            groupsFilename = groupsFilename.replaceFirst(TEAMS_FILENAME, GROUPS_FILENAME);
        } else if (filename.endsWith(GROUPS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            teamsFilename = teamsFilename.replaceFirst(GROUPS_FILENAME, TEAMS_FILENAME);
        } else {
            throw new Exception("Must select either " + TEAMS_FILENAME + " or " + GROUPS_FILENAME);
        }

        file = new File(teamsFilename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + teamsFilename);
        }

        file = new File(groupsFilename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + groupsFilename);
        }

        return true;

    }

}
