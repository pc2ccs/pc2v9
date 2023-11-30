// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationEventListener;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.ISite;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.api.IVersionInfo;
import edu.csus.ecs.pc2.api.RunStates;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * API IContest implementation.  
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class Contest implements IContest, UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 292457136351847397L;
    
    private VersionInfo versionInfo = new VersionInfo();

    private boolean loggedIn = true;

    private IInternalContest contest = null;
    
    private IInternalController controller = null;
    
    private RunListenerList runListenerList = new RunListenerList();
    
    private ConnectionEventListenerList connectionEventListenerList = new ConnectionEventListenerList();
    
    private ClarificationListenerList clarificationListenerList = new ClarificationListenerList();
    
    private ConfigurationListenerList configurationListenerList = new ConfigurationListenerList();
    
    private GenerateStandings generateStandings = new GenerateStandings();
    
    private Log log = null;
    
    private VersionInfoImplementation versionInfoImplementation = null;

    public Contest(IInternalContest contest, IInternalController controller, Log log) {
        this.log = log;
        setContestAndController(contest, controller);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getTitle() {
        Account account = contest.getAccount(contest.getClientId());
        return account.getDisplayName();
    }

    public ITeam[] getTeams() {

        Vector<Account> vector = contest.getAccounts(ClientType.Type.TEAM);
        TeamImplementation[] teamImplementations = new TeamImplementation[vector.size()];

        for (int i = 0; i < teamImplementations.length; i++) {
            teamImplementations[i] = new TeamImplementation(vector.elementAt(i), contest);
        }
        return teamImplementations;
    }

    public String getSiteName(int siteNumber) {
        Site site = contest.getSite(siteNumber);
        return site.getDisplayName();
    }

    public String getContestTitle() {
        return contest.getContestInformation().getContestTitle();
    }

    public String getSiteName() {
        return getSiteName(contest.getSiteNumber());
    }

    public ILanguage[] getLanguages() {
        Language[] languages = contest.getLanguages();
        LanguageImplementation[] implementations = new LanguageImplementation[languages.length];

        for (int i = 0; i < languages.length; i++) {
            implementations[i] = new LanguageImplementation(languages[i]);
        }
        return implementations;
    }

    public IProblem[] getAllProblems() {
        Problem[] problems = contest.getProblems();
        ProblemImplementation[] implementations = new ProblemImplementation[problems.length];

        for (int i = 0; i < problems.length; i++) {
            implementations[i] = new ProblemImplementation(problems[i], contest);
        }
        return implementations;
    }
    
    @Override
    public IProblem[] getProblems() {
        Problem[] problems = contest.getProblems();
        ArrayList<ProblemImplementation> implementations = new ArrayList<>();

        for (int i = 0; i < problems.length; i++) {
            if (problems[i].isActive()) {
                implementations.add(new ProblemImplementation(problems[i], contest));
            }
        }

        return (ProblemImplementation[]) implementations.toArray(new ProblemImplementation[implementations.size()]);
    }
    
    public IJudgement[] getJudgements() {
        Judgement[] judgements = contest.getJudgements();
        JudgementImplementation[] implementations = new JudgementImplementation[judgements.length];

        for (int i = 0; i < judgements.length; i++) {
            implementations[i] = new JudgementImplementation(judgements[i]);
        }
        return implementations;
    }

    public IRun[] getRuns() {
        Run[] runs = contest.getRuns();
        RunImplementation[] runImplementations = new RunImplementation[runs.length];

        for (int i = 0; i < runs.length; i++) {
            runImplementations[i] = new RunImplementation(runs[i], contest, controller);
        }
        return runImplementations;
    }

    public void addRunListener(IRunEventListener runEventListener) {
        runListenerList.addRunListener(runEventListener);
    }

    public void removeRunListener(IRunEventListener runEventListener) {
        runListenerList.removeRunListener(runEventListener);
    }
    
    public void addConnectionListener(IConnectionEventListener connectionEventListener) {
        connectionEventListenerList.addConnectionListener(connectionEventListener);
    }

    public void removeConnectionListener(IConnectionEventListener connectionEventListener) {
        connectionEventListenerList.removeConnectionListener(connectionEventListener);
    }

    public void addContestConfigurationUpdateListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        configurationListenerList.addContestUpdateConfigurationListener(contestUpdateConfigurationListener);
    }

    public void removeContestConfigurationUpdateListener(IConfigurationUpdateListener contestUpdateConfigurationListener) {
        configurationListenerList.removeContestUpdateConfigurationListener(contestUpdateConfigurationListener);
    }

    public IContestClock getContestClock() {
        return new ContestTimeImplementation(contest.getContestTime());
    }

    public IGroup[] getGroups() {
        Group[] groups = contest.getGroups();
        GroupImplementation[] groupImplementations = new GroupImplementation[groups.length];
        for (int i = 0; i < groups.length; i++) {
            groupImplementations[i] = new GroupImplementation(groups[i], contest);
        }
        return groupImplementations;
    }

    public IClient getMyClient() {
        return new ClientImplementation(contest.getClientId(), contest);
    }

    public boolean isContestClockRunning() {
        return contest.getContestTime().isContestRunning();
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public IStanding getStanding(ITeam team) {
        IStanding[] standings = generateStandings.getStandings(contest, log);
        if (standings == null) {
            return null;
        }
        for (IStanding standing : standings) {
            if (team.getAccountNumber() == standing.getClient().getAccountNumber() && team.getSiteNumber() == standing.getClient().getSiteNumber()) {
                return standing;
            }
        }
        return null;
    }

    public IStanding[] getStandings() {
        return generateStandings.getStandings(contest, log);
    }

    public ISite[] getSites() {

        Site[] sites = contest.getSites();
        SiteImplementation[] siteImplementations = new SiteImplementation[sites.length];
        for (int i = 0; i < sites.length; i++) {
            siteImplementations[i] = new SiteImplementation(sites[i]);
        }
        return siteImplementations;
    }

    public String getServerHostName() {
        return controller.getHostContacted();
    }

    public int getServerPort() {
        return controller.getPortContacted();
    }

    public IClarification[] getClarifications() {
        
        Clarification[] clarifications = contest.getClarifications();
        ClarificationImplementation[] clarificationImplementations = new ClarificationImplementation[clarifications.length];

        for (int i = 0; i < clarifications.length; i++) {
            clarificationImplementations[i] = new ClarificationImplementation(clarifications[i], contest, controller);
        }
        return clarificationImplementations;
    }

    public void removeClarificationListener(IClarificationEventListener clarificationEventListener) {
        clarificationListenerList.removeClarificationListener(clarificationEventListener);
    }
    
    public void addClarificationListener(IClarificationEventListener clarificationEventListener) {
        clarificationListenerList.addClarificationListener(clarificationEventListener);
    }

    public String getPluginTitle() {
        return "API Contest class";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        runListenerList.setContestAndController(contest, controller);
        clarificationListenerList.setContestAndController(contest, controller);
        configurationListenerList.setContest(contest);
        connectionEventListenerList.setContestAndController(contest, controller, this);
    }

    public IProblemDetails[] getProblemDetails() {
        IProblemDetails[] list = generateStandings.getProblemDetails(contest, log);
        Arrays.sort(list, new ProblemDetailsComparator());
        return list;
    }

    public boolean isCCSTestMode() {
        return contest.getContestInformation().isCcsTestMode();
    }
    
    public IProblem[] getClarificationCategories() {
        Problem problem = contest.getGeneralProblem();
        ProblemImplementation[] implementations = new ProblemImplementation[1];
        implementations[0] = new ProblemImplementation(problem, contest);
        return implementations;
    }
    
    @Override
    public IClient[] getClientsAllSites() {
        Vector<Account> accounts = contest.getAccounts(ClientType.Type.ALL);
        return createClientList(accounts);
    }

    @Override
    public IClient[] getClients() {
        Vector<Account> accounts = contest.getAccounts(ClientType.Type.ALL);
        ArrayList<Account> list = new ArrayList<Account>();
        
        for (Account account : accounts) {
            if(account.getSiteNumber() == contest.getSiteNumber()){
                list.add(account);
            }
        }
        return createClientList(list);
    }

    private IClient[] createClientList(List<Account> accounts) {
        List<IClient> list = new ArrayList<IClient>();

        for (Account account : accounts) {
            ClientImplementation client = new ClientImplementation(account.getClientId(), contest);
            list.add(client);
        }
        return (IClient[]) list.toArray(new IClient[list.size()]);
    }


    @Override
    public RunStates getRunState(IRun run) {

        RunStates value = RunStates.UNKNOWN;

        if (run == null) {
            throw new IllegalArgumentException("run is null");
        }

        if (isAllowed(Type.JUDGE_RUN) || isAllowed(Type.EDIT_RUN)) {

            edu.csus.ecs.pc2.core.model.Run.RunStates state = getRunStateInternal(run);

            switch (state) {
                case NEW:
                    value = RunStates.NEW;
                    break;

                case BEING_COMPUTER_JUDGED:
                case BEING_JUDGED:
                    value = RunStates.BEING_JUDGED;
                    break;

                case BEING_RE_JUDGED:
                    value = RunStates.BEING_RE_JUDGED;
                    break;

                case JUDGED:
                    value = RunStates.JUDGED;
                    break;

                default:
                    // keep state at UNKNOWN.
                    break;
            }

        } else {
            throw new SecurityException("Not allowed to get run state");
        }

        return value;
    }

    /**
     * Get Run.
     * @param run a API Run
     * @return a Run
     */
    private Run getInternalRun(IRun run) {

        Run[] runList = contest.getRuns();
        for (Run run2 : runList) {
            if (run2.getNumber() == run.getNumber()) {
                if (run2.getSiteNumber() == run.getSiteNumber()) {
                    // found it
                    return run2;
                }
            }
        }
        return null;
    }

    private edu.csus.ecs.pc2.core.model.Run.RunStates getRunStateInternal(IRun run) {

        Run foundRun = getInternalRun(run);

        if (foundRun == null) {
            throw new IllegalArgumentException("No run found " + run);
        }

        return foundRun.getStatus();

    }
    
    private boolean isAllowed(Type type) {
        return contest.isAllowed(type);
    }
    
    

    @Override
    public String getMajorVersion() {
        String [] parts = getVersionParts(versionInfo.getVersionNumber());
        return parts[0];
    }

    /**
     * Input pc2 version string
     * @param versionInfo2
     * @return 3 elemement array, 0 = major, 1 = mino, 2 = rest
     */
    protected String[] getVersionParts(String versionString ) {
        String [] output = new String[3];
        
        Arrays.fill(output,"");
        
//        String versionString = info.getVersionNumber();
        
        Pattern pattern  = Pattern.compile("([0-9]+)[.]([0-9]+)(.*)");
        Matcher m = pattern.matcher(versionString);
        
        if (m.matches() && m.groupCount() == 3) {
            output[0] = m.group(1);
            output[1] = m.group(2);
            output[2] = m.group(3);
        } else {
            output[0] = versionString;
        }
        
        return output;
    }

    @Override
    public String getMinorVersion() {
        String [] parts = getVersionParts(versionInfo.getVersionNumber());
        return parts[1];
    }
    
    @Override
    public String getOtherVersionInfo(){
        String [] parts = getVersionParts(versionInfo.getVersionNumber());
        return parts[2];
    }

    @Override
    public String getBuildNumber() {
        return versionInfo.getBuildNumber();
    }

    @Override
    public String getFullVersionString() {
        return "Version " + versionInfo.getPC2Version() + " Build "+versionInfo.getBuildNumber();
    }
    
    @Override
    public IRun getRun(int siteNumber, int runNumber) {

        IRun[] runs = getRuns();
        for (IRun iRun : runs) {
            if (iRun.getNumber() == runNumber) {
                if (iRun.getSiteNumber() == siteNumber) {
                    return iRun;
                }
            }
        }

        return null;
    }

    @Override
    public IRun getRun(int runNumber) {
        return getRun(contest.getSiteNumber(), runNumber);
    }

    /**
     * This method returns the underlying {@link IInternalContest} (that is, the actual underlying contest model)
     * which this API {@link Contest} wraps.  This method should be used with caution; ordinarily, the methods 
     * exposed by this {@link Contest}'s {@link IContest} implementation should provide access to most of the
     * contest data which is needed.   (An example of when use of this method might be needed is when an API
     * client, such as a Scoreboard, needs construct a {@link DefaultScoringAlgorithm} instance and subsequently
     * needs to invoke a method in that instance which requires receiving an {@link IInternalContest} rather than
     * an API {@link IContest}.)
     * 
     * @return the underlying IInternalContest wrapped by this IContest implementation
     */
    public IInternalContest getInternalContest() {
        return contest ;
    }

    @Override
    public IVersionInfo getVersionInfo() {
        if (versionInfoImplementation == null) {
            versionInfoImplementation = new VersionInfoImplementation();
        }
        return versionInfoImplementation;
    }
}
