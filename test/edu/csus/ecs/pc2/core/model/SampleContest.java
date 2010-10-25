package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.FileStorage;

/**
 * Create Sample InternalContest and InternalController.
 * 
 * Also has a method to create a Site class instance.
 * 
 * @see #createContest(int, int, int, int)
 * @see #createController(IInternalContest, boolean, boolean)
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SampleContest {

    private boolean debugMode = false;

    private Random random = new Random();
    
    public static final int DEFAULT_PORT_NUMBER = 50002;
    private int defaultPortNumber = DEFAULT_PORT_NUMBER;

    /**
     * Create a new Site class instance.
     * 
     * @param siteNumber
     *            site number
     * @param siteName
     *            title for site
     * @param hostName
     *            if null, assigned to localhost
     * @param portNumber
     *            if 0 assigned 50002 + (siteNumber-1)* 1000
     * @return
     */
    public Site createSite(int siteNumber, String siteName, String hostName, int portNumber) {
        Site site = new Site(siteName, siteNumber);

        Properties props = new Properties();
        if (hostName == null) {
            props.put(Site.IP_KEY, "localhost");
        }

        if (portNumber == 0) {
            portNumber = defaultPortNumber + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
    }
    
    public IInternalContest createContest(int siteNumber, int numSites, int numTeams, int numJudges, boolean initAsServer) {

        String contestPassword = "Password 101";
        Profile profile = new Profile("Default.");
        return createContest(siteNumber, numSites, numTeams, numJudges, initAsServer, profile, contestPassword);
    }

    /**
     * Create an instance of contest with languages, problems, teams and judges.
     * 
     * @param numSites
     *            number of sites to create
     * @param numTeams
     *            number of teams to create
     * @param numJudges
     *            number of judges to create
     * @param setAsServer
     * @return
     */
    public IInternalContest createContest(int siteNumber, int numSites, int numTeams, int numJudges, boolean initAsServer, Profile profile, String contestPassword) {

        String[] languages = { LanguageAutoFill.JAVATITLE, LanguageAutoFill.DEFAULTTITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.PERLTITLE, LanguageAutoFill.MSCTITLE, "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing", "Faulty Towers", "London Bridge", "Finnigans Bluff" };
        String[] judgements = { "Stupid programming error", "Misread problem statement", "Almost there", "You have no clue", "Give up and go home", "Consider switching to another major",
                "How did you get into this place ?", "Contact Staff - you have no hope" };

        InternalContest contest = new InternalContest();

        contest.setSiteNumber(siteNumber);

        for (int i = 0; i < numSites; i++) {
            Site site = createSite(i + 1, "Site " + (i + 1), null, 0);
            contest.addSite(site);
        }

        for (String langName : languages) {
            Language language = new Language(langName);
            String [] values = LanguageAutoFill.getAutoFillValues(langName);
            if (values[0].trim().length() != 0){
                fillLanguage (language, values);
            }
            language.setSiteNumber(siteNumber);
            contest.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            problem.setSiteNumber(siteNumber);
            contest.addProblem(problem);
        }
        
        Problem generalProblem = new Problem("General.");
        contest.setGeneralProblem(generalProblem);

        Judgement judgementYes = new Judgement("Yes.");
        contest.addJudgement(judgementYes);

        for (String judgementName : judgements) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }

        if (numTeams > 0) {
            contest.generateNewAccounts(Type.TEAM.toString(), numTeams, true);
        }

        if (numJudges > 0) {
            contest.generateNewAccounts(Type.JUDGE.toString(), numJudges, true);
        }

        ContestTime contestTime = new ContestTime(siteNumber);
        contest.addContestTime(contestTime);

        if (initAsServer) {
            ClientId serverId = new ClientId(siteNumber, Type.SERVER, 0);
            contest.setClientId(serverId);
        }
        
        contest.setProfile(profile);
        contest.setContestPassword(contestPassword);
        return contest;
    }

    private void fillLanguage(Language language, String[] values) {
//        values array
//        0 Title for Language 
//        1 Compiler Command Line 
//        2 Executable Identifier Mask 
//        3 Execute command line 

        language.setCompileCommandLine(values[1]);
        language.setExecutableIdentifierMask(values[2]);
        language.setProgramExecuteCommandLine(values[3]);
    }
    
    public IInternalController createController(IInternalContest contest, boolean isServer, boolean isRemote) {
        return createController(contest, null, isServer, isRemote);
    }

    /**
     * Create a InternalController.
     * 
     * @param contest
     *            model for controller
     * @param isServer
     *            is this a server controller ?
     * @param isRemote
     *            is this a remote site ?
     * @param storageDirectory where this controller would save its packets.
     * @return
     */
    public IInternalController createController(IInternalContest contest, String storageDirectory, boolean isServer, boolean isRemote) {

        // Start site 1
        InternalController controller = new InternalController(contest);
        controller.setUsingMainUI(false);

        if (isServer) {
            controller.setContactingRemoteServer(isRemote);
            String[] argsSiteOne = { "--server", "--skipini" };
            int siteNumber = contest.getSiteNumber();

            // As of 2008-01-20 start sets site number to zero.
            controller.start(argsSiteOne);

            // set InternalContest back to original site number
            contest.setSiteNumber(siteNumber);
            
            if (storageDirectory != null){
                FileStorage storage = new FileStorage(storageDirectory);
                contest.setStorage(storage);
                controller.initializeStorage(storage);
            }
        } else {
            controller.start(null);

        }

        return controller;
    }
    
    public static String getTestDirectoryName(){
        String testDir = "testing";
        
        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }
    
    public static String getTestDirectoryName(String subDirName) {
        String testDir = getTestDirectoryName() + File.separator + subDirName;

        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }


    
    /**
     * Populate Language, Problems and Judgements.
     * 
     * @param contest
     */
    public void populateContest(IInternalContest contest) {

        String[] languages = { "Java", LanguageAutoFill.JAVATITLE, LanguageAutoFill.DEFAULTTITLE, LanguageAutoFill.GNUCPPTITLE, LanguageAutoFill.PERLTITLE, LanguageAutoFill.MSCTITLE, "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };
        String[] judgements = { "No no", "No no no", "No - judges are confused" };

        for (String langName : languages) {
            Language language = new Language(langName);
            String [] values = LanguageAutoFill.getAutoFillValues(langName);
            if (values[0].trim().length() != 0){
                fillLanguage (language, values);
            }            
            contest.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            contest.addProblem(problem);
        }

        // Only put judgements in if there are no judgements
        if (contest.getJudgements().length == 0) {

            Judgement judgementYes = new Judgement("Yes");
            contest.addJudgement(judgementYes);

            for (String judgementName : judgements) {
                contest.addJudgement(new Judgement(judgementName));
            }
        }

    }

    /**
     * Get all sites' teams.
     * @param contest
     * @return
     */
    protected Account[] getTeamAccounts(IInternalContest contest) {
        Vector<Account> accountVector = contest.getAccounts(ClientType.Type.TEAM);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }
    
    /**
     * Get site's teams.
     * 
     * @param contest
     * @param siteNumber
     * @return
     */
    protected Account[] getTeamAccounts(IInternalContest contest, int siteNumber) {
        Vector<Account> accountVector = contest.getAccounts(ClientType.Type.TEAM, siteNumber);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }
    
        

    public Run[] createRandomRuns(IInternalContest contest, int numberRuns, boolean randomTeam, boolean randomProblem, boolean randomLanguage, int siteNumber) {

        Run[] runs = new Run[numberRuns];

        Account[] accounts = getTeamAccounts(contest);
        Language[] languages = contest.getLanguages();
        Problem[] problems = contest.getProblems();

        int numRuns = contest.getRuns().length;
        
        if (siteNumber != 0){
            accounts = getTeamAccounts(contest, siteNumber);
        }
        
        if (accounts.length == 0){
            new Exception("No accounts for site "+siteNumber).printStackTrace();
            return new Run[0];
        }

        for (int i = 0; i < numberRuns; i++) {
            Problem problem = problems[0];
            Language language = languages[0];
            ClientId teamId = accounts[0].getClientId();

            if (randomTeam) {
                int randomLangIndex = random.nextInt(languages.length);
                language = (Language) languages[randomLangIndex];
            }

            if (randomTeam) {
                int randomProblemIndex = random.nextInt(problems.length);
                problem = (Problem) problems[randomProblemIndex];
            }

            if (randomTeam) {
                int randomTeamIndex = random.nextInt(accounts.length);
                teamId = accounts[randomTeamIndex].getClientId();
            }

            Run run = new Run(teamId, language, problem);
            run.setElapsedMins(9 + i);
            run.setNumber(++numRuns);
            runs[i] = run;
        }
        return runs;
        
    }

    /**
     * Generate a number of new runs.
     * 
     * @param contest
     * @param numberRuns
     * @param randomTeam
     * @param randomProblem
     * @param randomLanguage
     * @return
     */
    public Run[] createRandomRuns(IInternalContest contest, int numberRuns, boolean randomTeam, boolean randomProblem, boolean randomLanguage) {
        return createRandomRuns( contest,  numberRuns,  randomTeam,  randomProblem,  randomLanguage, 0);
    }

    /**
     * Create new run and add to contest run list.
     * 
     * @param contest
     * @param clientId
     * @param language
     * @param problem
     * @param elapsed
     * @return
     */
    public Run createRun(IInternalContest contest, ClientId clientId, Language language, Problem problem, long elapsed) {
        int numRuns = contest.getRuns().length;
        Run run = new Run(clientId, language, problem);
        run.setElapsedMins(elapsed);
        run.setNumber(++numRuns);
        try {
            contest.addRun(run);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return run;
    }

    /**
     * Create new run and add to contest run list.
     * 
     * @param contest
     * @param clientId
     * @param problem
     * @return
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Run createRun(IInternalContest contest, ClientId clientId, Problem problem) throws IOException, ClassNotFoundException, FileSecurityException {
        int numRuns = contest.getRuns().length;
        Run run = new Run(clientId, contest.getLanguages()[0], problem);
        run.setElapsedMins(9 + numRuns);
        run.setNumber(++numRuns);
        contest.addRun(run);
        return run;
    }
    
    /**
     * Create a copy of the run (not a clone, but close)  and add to contest run list.
     * 
     * This references the input run's JugementRecords instead of
     * cloning them.  This run will have a different getElementId() and
     * a getNumber() which represents the next run number.
     * 
     * @param contest
     * @param run
     * @param cloneJudgements
     * @return
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Run copyRun (IInternalContest contest, Run run, boolean cloneJudgements) throws IOException, ClassNotFoundException, FileSecurityException{
        Run newRun = new Run(run.getSubmitter(), contest.getLanguage(run.getLanguageId()), contest.getProblem(run.getProblemId()));
        newRun.setElapsedMins(run.getElapsedMins());
        newRun.setDeleted(run.isDeleted());
        newRun.setNumber(contest.getRuns().length);
        newRun.setStatus(RunStates.NEW);
        
        if (cloneJudgements){
            for (JudgementRecord judgementRecord : newRun.getAllJudgementRecords()){
                newRun.addJudgement(judgementRecord);
            }
            newRun.setStatus(run.getStatus());
        }
        contest.addRun(newRun);
        return newRun;
    }
    
    public Site createSite(int nextSiteNumber, String hostName, int port) {
        return createSite(nextSiteNumber, hostName, port, null);
    }
    
    public Site createSite(int nextSiteNumber, String hostName, int port, String siteName) {
        if (siteName == null){
            siteName = new String("Site " + nextSiteNumber);
        }
        Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, hostName);
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site" + nextSiteNumber);
        return site;
    }
    
    public Site createSite (IInternalContest contest, String siteName){
        int nextSiteNumber = contest.getSites().length + 1;
        int newPortNumber = DEFAULT_PORT_NUMBER + (nextSiteNumber - 1) * 1000;
        Site site = createSite(nextSiteNumber, "localhost", newPortNumber, siteName);
        return site;
    }

    public Site[] createSites(IInternalContest contest, int count) {
        Site[] sites = new Site[count];
        for (int i = 0; i < count; i++) {
            int nextSiteNumber = contest.getSites().length + i + 1;
            int newPortNumber = DEFAULT_PORT_NUMBER + (nextSiteNumber - 1) * 1000;
            Site site = createSite(nextSiteNumber, "localhost", newPortNumber, null);
            sites[i] = site;
        }
        return sites;
    }

    public Profile[] createProfiles(IInternalContest contest, int count) {
        Profile[] profiles = new Profile[count];
        for (int i = 0; i < count; i++) {
            int nextProfileNumber = contest.getProfiles().length + i + 1;
            Profile profile = new Profile("Profile " + nextProfileNumber);
            profiles[i] = profile;
        }
        return profiles;
    }

    /**
     * add a judged run to list of runs in a contest.
     * 
     * Fields in runInfoLine:
     * 
     * <pre>
     * 0 - run id, int
     * 1 - team id, int
     * 2 - problem letter, char
     * 3 - elapsed, int
     * 4 - solved, String &quot;Yes&quot; or No (or full No judgement text)
     * 5 - send to teams, Yes or No
     * 
     * Example:
     * &quot;6,5,A,12,Yes&quot;
     * &quot;6,5,A,12,Yes,Yes&quot;
     * 
     * </pre>
     * 
     * @param contest
     * @param runInfoLine
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public Run addARun(InternalContest contest, String runInfoLine) throws IOException, ClassNotFoundException, FileSecurityException {

        // get last judge
        Account[] accounts = (Account[]) contest.getAccounts(Type.JUDGE).toArray(new Account[contest.getAccounts(Type.JUDGE).size()]);
        ClientId judgeId = accounts[accounts.length - 1].getClientId();

        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement yesJudgement = contest.getJudgements()[0];
        Judgement noJudgement = contest.getJudgements()[1];

        String[] data = runInfoLine.split(",");

        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]

        int runId = getIntegerValue(data[0]);
        int teamId = getIntegerValue(data[1]);
        String probLet = data[2];
        int elapsed = getIntegerValue(data[3]);

        boolean solved = data[4].equals("Yes");

        boolean sendToTeams = true;
        if (data.length > 5) {
            sendToTeams = data[5].equals("Yes");
        }

        int problemIndex = probLet.charAt(0) - 'A';
        Problem problem = problemList[problemIndex];
        ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamId);

        Run run = new Run(clientId, languageId, problem);
        run.setNumber(runId);
        run.setElapsedMins(elapsed);

        // Use a default No entry
        ElementId judgementId = noJudgement.getElementId();

        if (solved) {
            judgementId = yesJudgement.getElementId();
        } else {

            // Try to find No judgement

            for (Judgement judgement : contest.getJudgements()) {
                if (judgement.toString().equalsIgnoreCase(data[5])) {
                    judgementId = judgement.getElementId();
                }
            }
        }
        JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);
        judgementRecord.setSendToTeam(sendToTeams);
        contest.addRun(run);

        checkOutRun(contest, run, judgeId);
        contest.addRunJudgement(run, judgementRecord, null, judgeId);

        if (debugMode) {
            System.out.print("Send to teams " + run.getJudgementRecord().isSendToTeam() + " ");
            System.out.println("Added run " + run);
        }

        return contest.getRun(run.getElementId());

    }

    private void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) {
        try {
            contest.checkoutRun(run, judgeId, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Add runs to contest (via acceptRun)
     * 
     * @param contest
     * @param runs
     * @param filename
     *            name of file to submit
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void addRuns(IInternalContest contest, Run[] runs, String filename) throws IOException, ClassNotFoundException, FileSecurityException {

        if (!new File(filename).exists()) {
            throw new IllegalArgumentException("filename is null");
        }

        for (Run run : runs) {
            RunFiles runFiles = new RunFiles(run, filename);
            contest.acceptRun(run, runFiles);
        }
    }
    
  
    
    /**
     * Print the report to the filename.
     * @param filename
     * @param selectedReport
     * @param filter
     * @param inContest
     * @param inController
     */
    public void printReport (String filename, IReport selectedReport, Filter filter, IInternalContest inContest, IInternalController inController){
        
        if (filter == null){
            filter = new Filter();
        }

        try {
            selectedReport.setContestAndController(inContest, inController);
            selectedReport.setFilter(filter);
            selectedReport.createReportFile(filename, filter);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Judgement getYesJudgement(IInternalContest contest) {
        return contest.getJudgements()[0];
    }
    
    public int getDefaultPortNumber() {
        return defaultPortNumber;
    }
    
    public void setDefaultPortNumber(int defaultPortNumber) {
        this.defaultPortNumber = defaultPortNumber;
    }
}
