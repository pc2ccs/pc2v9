package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Create Sample InternalContest and InternalController.
 * 
 * Also has a method to create a Site.
 * 
 * @see #createContest(int, int, int, int)
 * @see #createController(IInternalContest, boolean, boolean)
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SampleContest {

    private Random random = new Random();

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
            portNumber = 50002 + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
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
    public IInternalContest createContest(int siteNumber, int numSites, int numTeams, int numJudges, boolean initAsServer) {

        String[] languages = { "Java", "C++", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing", "Faulty Towers", "London Bridge", "Finnigans Bluff" };

        InternalContest contest = new InternalContest();
        
        contest.setSiteNumber(siteNumber);
        
        for (int i = 0; i < numSites; i++) {
            Site site = createSite(i + 1, "Site " + (i + 1), null, 0);
            contest.addSite(site);
        }

        for (String langName : languages) {
            Language language = new Language(langName);
            contest.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            contest.addProblem(problem);
        }

        if (numTeams > 0) {
            contest.generateNewAccounts(Type.TEAM.toString(), numTeams, true);
        }

        if (numJudges > 0) {
            contest.generateNewAccounts(Type.JUDGE.toString(), numJudges, true);
        }

        ContestTime contestTime = new ContestTime(siteNumber);
        contest.addContestTime(contestTime);
        
        if (initAsServer){
            ClientId serverId = new ClientId(siteNumber, Type.SERVER, 0);
            contest.setClientId(serverId);
        }

        return contest;
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
     * @return
     */
    public IInternalController createController(IInternalContest contest, boolean isServer, boolean isRemote) {

        // Start site 1
        InternalController controller = new InternalController(contest);
        controller.setUsingMainUI(false);

        if (isServer) {
            controller.setContactingRemoteServer(isRemote);
            String[] argsSiteOne = { "--server" };
            int siteNumber = contest.getSiteNumber();
            
            // As of 2008-01-20 start sets site number to zero.
            controller.start(argsSiteOne);
            
            // set InternalContest back to original site number
            contest.setSiteNumber(siteNumber);
        } else {
            controller.start(null);

        }

        return controller;

    }

    /**
     * Populate Language, Problems and Judgements.
     * 
     * @param contest
     */
    public void populateContest(IInternalContest contest) {

        String[] languages = { "Java", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };
        String[] judgements = { "No no", "No no no", "No - judges are confused" };

        for (String langName : languages) {
            Language language = new Language(langName);
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

    protected Account[] getTeamAccounts(IInternalContest contest) {
        // this sites team accounts
        Vector<Account> accountVector = contest.getAccounts(ClientType.Type.TEAM);

        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
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

        Run[] runs = new Run[numberRuns];

        Account[] accounts = getTeamAccounts(contest);
        Language[] languages = contest.getLanguages();
        Problem[] problems = contest.getProblems();

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
            runs[i] = run;
        }
        return runs;
    }

    /**
     * Add runs to contest (via acceptRun)
     * @param contest
     * @param runs
     * @param filename name of file to submit
     */
    public void addRuns (IInternalContest contest, Run [] runs, String filename){
        
        if (! new File(filename).exists()){
            throw new IllegalArgumentException("filename is null");
        }

        for (Run run : runs){
            RunFiles runFiles = new RunFiles(run, filename);
            contest.acceptRun(run, runFiles);
        }
    }
}
