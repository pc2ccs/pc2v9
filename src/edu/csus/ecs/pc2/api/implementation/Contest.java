package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationEventListener;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.ISite;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ITeam;
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

/**
 * API IContest implementation.  
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class Contest implements IContest {

    private boolean loggedIn = true;

    private IInternalContest contest = null;
    
    private IInternalController controller = null;
    
    private RunListenerList runListenerList = new RunListenerList();
    
    private ConnectionEventListenerList connectionEventListenerList = new ConnectionEventListenerList();
    
    private ClarificationListenerList clarificationListenerList = new ClarificationListenerList();
    
    private ConfigurationListenerList configurationListenerList = new ConfigurationListenerList();
    
    private GenerateStandings generateStandings = new GenerateStandings();
    
    private Log log = null;

    public Contest(IInternalContest contest, IInternalController controller, Log log) {
        this.contest = contest;
        this.controller = controller;
        this.log = log;
        runListenerList.setContestAndController(contest, controller);
        clarificationListenerList.setContestAndController(contest, controller);
        configurationListenerList.setContest(contest);
        connectionEventListenerList.setContestAndController(contest, controller, this);
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

    public IProblem[] getProblems() {
        Problem[] problems = contest.getProblems();
        ProblemImplementation[] implementations = new ProblemImplementation[problems.length];

        for (int i = 0; i < problems.length; i++) {
            implementations[i] = new ProblemImplementation(problems[i], contest);
        }
        return implementations;
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

 
}
