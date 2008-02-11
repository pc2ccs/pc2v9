package edu.csus.ecs.pc2.api.implementation;

import java.util.Vector;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IContestClock;
import edu.csus.ecs.pc2.api.IGroup;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.core.model.Account;
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
    
    private RunListenerList runListenerList = new RunListenerList();
    
    private ConfigurationListenerList configurationListenerList = new ConfigurationListenerList();

    public Contest(IInternalContest contest) {
        this.contest = contest;
        runListenerList.setContest(contest);
        configurationListenerList.setContest(contest);
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
        Site site = contest.getSite(contest.getSiteNumber());
        return site.getDisplayName();
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
            runImplementations[i] = new RunImplementation(runs[i], contest);
        }
        return runImplementations;
    }

    public void addRunListener(IRunEventListener runEventListener) {
        runListenerList.addRunListener(runEventListener);
    }

    public void removeRunListener(IRunEventListener runEventListener) {
        runListenerList.removeRunListener(runEventListener);
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
        // TODO Auto-generated method stub
        return null;
    }

    public IStanding[] getStandings() {
        // TODO Auto-generated method stub
        return null;
    }
}
