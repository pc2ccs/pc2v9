package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.ContestTimeList;
import edu.csus.ecs.pc2.core.list.JudgementDisplayList;
import edu.csus.ecs.pc2.core.list.JudgementList;
import edu.csus.ecs.pc2.core.list.LanguageDisplayList;
import edu.csus.ecs.pc2.core.list.LanguageList;
import edu.csus.ecs.pc2.core.list.LoginList;
import edu.csus.ecs.pc2.core.list.ProblemDisplayList;
import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.list.RunFilesList;
import edu.csus.ecs.pc2.core.list.RunList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Implementation of IModel.
 * 
 * This model is not responsible for logic, just storage.  So, for example,
 * {@link #cancelRunCheckOut(Run, ClientId)} will simply update the Run
 * but will not check whether the run should be cancelled.  The Controller
 * should be used to check whether a Run should be cancelled.  Other logic
 * of this sort is in the Controller, not the Model.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Model implements IModel {

    public static final String SVN_ID = "$Id$";

    private ClientId localClientId = null;

    private Vector<IRunListener> runListenerList = new Vector<IRunListener>();

    private Vector<IProblemListener> problemListenerList = new Vector<IProblemListener>();

    private Vector<ILanguageListener> languageListenerList = new Vector<ILanguageListener>();

    private Vector<ILoginListener> loginListenerList = new Vector<ILoginListener>();

    private Vector<IContestTimeListener> contestTimeListenerList = new Vector<IContestTimeListener>();

    private Vector<IJudgementListener> judgementListenerList = new Vector<IJudgementListener>();

    private Vector<ISiteListener> siteListenerList = new Vector<ISiteListener>();
    
    /**
     * Contains name of client (judge or admin) who checks out the run.
     */
    private Hashtable<ElementId, ClientId> runCheckOutList = new Hashtable<ElementId, ClientId>(200);

    private AccountList accountList = new AccountList();

    private Vector<IAccountListener> accountListenerList = new Vector<IAccountListener>();

    private LoginList loginList = new LoginList();

    private ContestTimeList contestTimeList = new ContestTimeList();

    private RunList runList = new RunList();

    private RunFilesList runFilesList = new RunFilesList();

    private SiteList siteList = new SiteList();

    private int siteNumber = 1;

    /**
     * List of all defined problems. Contains deleted problems too.
     */
    private ProblemList problemList = new ProblemList();

    /**
     * List of all problems displayed to users, in order. Does not contain deleted problems.
     */
    private ProblemDisplayList problemDisplayList = new ProblemDisplayList();

    /**
     * List of all languages. Contains deleted problems too.
     */
    private LanguageList languageList = new LanguageList();

    /**
     * List of all displayed languages, in order. Does not contain deleted languages.
     */
    private LanguageDisplayList languageDisplayList = new LanguageDisplayList();

    /**
     * List of all displayed judgements, in order. Does not contain deleted judgements.
     */
    private JudgementDisplayList judgementDisplayList = new JudgementDisplayList();

    /**
     * List of all judgements. Contains deleted judgements too.
     */
    private JudgementList judgementList = new JudgementList();

    /**
     * Initialize Model with data.
     */
    public void initializeWithFakeData() {

        String[] probNames = { "A - Sum of Squares", "B - Sumit", "C - Hello", "D - GoodBye" };

        for (String problemNames : probNames) {
            Problem problem = new Problem(problemNames);
            addProblem(problem);
        }

        String[] langNames = { "Java", "BASIC", "C++", "ANSI C", "APL" };

        for (String languageName : langNames) {
            Language language = new Language(languageName);
            addLanguage(language);
        }

        // Generate the server account
        generateNewAccounts(ClientType.Type.SERVER.toString(), 1, true);

        ContestTime contestTime = new ContestTime();
        contestTime.setElapsedMins(9);
        contestTime.startContestClock();

        addContestTime(contestTime, siteNumber);

        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "No - It's just really bad",
                "No - judges enjoyed a good laugh", "You've been bad - contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            addJudgement(judgement);
        }

        Site site = new Site("Site 1/auto", 1);
        site.setPassword("site1");
        site.setActive(true);
        siteList.add(site);
    }

    public void addRunListener(IRunListener runListener) {
        runListenerList.addElement(runListener);
    }

    public void removeRunListener(IRunListener runListener) {
        runListenerList.removeElement(runListener);
    }

    public void addContestTimeListener(IContestTimeListener contestTimeListener) {
        contestTimeListenerList.addElement(contestTimeListener);
    }

    public void removeContestTimeListener(IContestTimeListener contestTimeListener) {
        contestTimeListenerList.removeElement(contestTimeListener);
    }

    public void addJudgementListener(IJudgementListener judgementListener) {
        judgementListenerList.addElement(judgementListener);
    }

    public void removeJudgementListener(IJudgementListener judgementListener) {
        judgementListenerList.remove(judgementListener);
    }

    private void fireRunListener(RunEvent runEvent) {
        for (int i = 0; i < runListenerList.size(); i++) {

            if (runEvent.getAction() == RunEvent.Action.ADDED) {
                runListenerList.elementAt(i).runAdded(runEvent);
            } else if (runEvent.getAction() == RunEvent.Action.DELETED) {
                runListenerList.elementAt(i).runRemoved(runEvent);
            } else {
                runListenerList.elementAt(i).runChanged(runEvent);
            }
        }
    }

    private void fireContestTimeListener(ContestTimeEvent contestTimeEvent) {
        for (int i = 0; i < contestTimeListenerList.size(); i++) {

            if (contestTimeEvent.getAction() == ContestTimeEvent.Action.ADDED) {
                contestTimeListenerList.elementAt(i).contestTimeAdded(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.DELETED) {
                contestTimeListenerList.elementAt(i).contestTimeRemoved(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CLOCK_STARTED) {
                contestTimeListenerList.elementAt(i).contestStarted(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CLOCK_STOPPED) {
                contestTimeListenerList.elementAt(i).contestStopped(contestTimeEvent);
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.UPDATED) {
                contestTimeListenerList.elementAt(i).contestTimeChanged(contestTimeEvent);
            } else {
                contestTimeListenerList.elementAt(i).contestTimeChanged(contestTimeEvent);
            }
        }
    }

    private void fireProblemListener(ProblemEvent problemEvent) {
        for (int i = 0; i < problemListenerList.size(); i++) {

            if (problemEvent.getAction() == ProblemEvent.Action.ADDED) {
                problemListenerList.elementAt(i).problemAdded(problemEvent);
            } else if (problemEvent.getAction() == ProblemEvent.Action.DELETED) {
                problemListenerList.elementAt(i).problemRemoved(problemEvent);
            } else {
                problemListenerList.elementAt(i).problemChanged(problemEvent);
            }
        }
    }

    private void fireLanguageListener(LanguageEvent languageEvent) {
        for (int i = 0; i < languageListenerList.size(); i++) {

            if (languageEvent.getAction() == LanguageEvent.Action.ADDED) {
                languageListenerList.elementAt(i).languageAdded(languageEvent);
            } else if (languageEvent.getAction() == LanguageEvent.Action.DELETED) {
                languageListenerList.elementAt(i).languageRemoved(languageEvent);
            } else {
                languageListenerList.elementAt(i).languageChanged(languageEvent);
            }
        }
    }

    private void fireLoginListener(LoginEvent loginEvent) {
        for (int i = 0; i < loginListenerList.size(); i++) {

            if (loginEvent.getAction() == LoginEvent.Action.NEW_LOGIN) {
                loginListenerList.elementAt(i).loginAdded(loginEvent);
            } else if (loginEvent.getAction() == LoginEvent.Action.LOGOFF) {
                loginListenerList.elementAt(i).loginRemoved(loginEvent);
            } else if (loginEvent.getAction() == LoginEvent.Action.LOGIN_DENIED) {
                loginListenerList.elementAt(i).loginDenied(loginEvent);
            } else {
                throw new UnsupportedOperationException("Unknown login action " + loginEvent.getAction());
            }
        }
    }

    private void fireJudgementListener(JudgementEvent judgementEvent) {
        for (int i = 0; i < judgementListenerList.size(); i++) {

            if (judgementEvent.getAction() == JudgementEvent.Action.ADDED) {
                judgementListenerList.elementAt(i).judgementAdded(judgementEvent);
            } else if (judgementEvent.getAction() == JudgementEvent.Action.DELETED) {
                judgementListenerList.elementAt(i).judgementRemoved(judgementEvent);
            } else {
                judgementListenerList.elementAt(i).judgementChanged(judgementEvent);
            }
        }
    }

    private void fireSiteListener(SiteEvent siteEvent) {
        for (int i = 0; i < siteListenerList.size(); i++) {

            if (siteEvent.getAction() == SiteEvent.Action.ADDED) {
                siteListenerList.elementAt(i).siteAdded(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.DELETED) {
                siteListenerList.elementAt(i).siteRemoved(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.LOGIN) {
                siteListenerList.elementAt(i).siteLoggedOn(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.LOGOFF) {
                siteListenerList.elementAt(i).siteLoggedOff(siteEvent);
            } else {
                siteListenerList.elementAt(i).siteAdded(siteEvent);
            }
        }
    }


    public void addLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        loginList.add(inClientId, connectionHandlerID);
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.NEW_LOGIN, inClientId, connectionHandlerID, "New");
        fireLoginListener(loginEvent);
    }
    
    public void loginDenied (ClientId clientId, ConnectionHandlerID connectionHandlerID, String message){
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.LOGIN_DENIED, clientId, connectionHandlerID, message);
        fireLoginListener(loginEvent);
    }

    public void addLanguage(Language language) {
        languageDisplayList.add(language);
        languageList.add(language);
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.ADDED, language);
        fireLanguageListener(languageEvent);
    }

    public void addProblem(Problem problem) {
        problemDisplayList.add(problem);
        problemList.add(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.ADDED, problem);
        fireProblemListener(problemEvent);
    }

    public void addJudgement(Judgement judgement) {
        judgementDisplayList.add(judgement);
        judgementList.add(judgement);
        JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.ADDED, judgement);
        fireJudgementListener(judgementEvent);
    }

    public void addSite(Site site) {
        siteList.add(site);
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.ADDED, site);
        fireSiteListener(siteEvent);
    }

    public Judgement[] getJudgements() {
        return judgementDisplayList.getList();
    }

    /**
     * Accept Run, add new run onto server.
     * 
     * On Server, adds run to run list, increments run number.
     */
    public Run acceptRun (Run run, RunFiles runFiles) {
        run.setElapsedMins(getContestTime().getElapsedMins());
        if (runFiles != null) {
            runFilesList.add(run, runFiles);
        }
        Run newRun = runList.addNewRun(run); // this set the run number.
        addRun(newRun);
        return newRun;
    }
    
    /**
     * Add a run to run list, notify listeners.
     * @param run
     */
    public void addRun (Run run){
        runList.add(run);
        RunEvent runEvent = new RunEvent(RunEvent.Action.ADDED, run, null);
        fireRunListener(runEvent);
    }
    
    public void addRun(Run run, RunFiles runFiles) {
        runList.add(run); 
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHECKEDOUT_RUN, run, runFiles);
        fireRunListener(runEvent);
    }
    
    public void availableRun(Run run) {
        runList.add(run); 
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_AVIALABLE, run, null);
        fireRunListener(runEvent); 
    }



    public void generateNewAccounts(String clientTypeName, int count, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName.toUpperCase());
        int numberAccounts = accountList.getAccounts(type, siteNumber).size();

        accountList.generateNewAccounts(type, count, PasswordType.JOE, siteNumber, active);

        for (int i = 0; i < count; i++) {
            ClientId nextClientId = new ClientId(siteNumber, type, 1 + i + numberAccounts);
            Account account = accountList.getAccount(nextClientId);
            AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED, account);
            fireAccountListener(accountEvent);
        }
    }
    
    /**
     * Generate new sites
     * @param count
     * @param active
     */
    public void generateNewSites (int count, boolean active){

        int numSites = siteList.size();

        for (int i = 0; i < count; i++) {
            int nextSiteNumber = i + numSites + 1;
            Site site = new Site ("Site "+nextSiteNumber, nextSiteNumber);
            site.setPassword("site"+nextSiteNumber); // JOE password
            site.setActive(active);
            addSite(site);
        }
    }

    public void addAccountListener(IAccountListener accountListener) {
        accountListenerList.addElement(accountListener);

    }

    public void removeAccountListener(IAccountListener accountListener) {
        accountListenerList.removeElement(accountListener);
    }

    private void fireAccountListener(AccountEvent accountEvent) {
        for (int i = 0; i < accountListenerList.size(); i++) {

            if (accountEvent.getAction() == AccountEvent.Action.ADDED) {
                accountListenerList.elementAt(i).accountAdded(accountEvent);
            } else {
                accountListenerList.elementAt(i).accountModified(accountEvent);
            }
        }
    }

    public Problem[] getProblems() {
        return problemDisplayList.getList();
    }

    public Language[] getLanguages() {
        return languageDisplayList.getList();
    }

    public ClientId getClientId() {
        return localClientId;
    }

    public void setClientId(ClientId clientId) {
        this.localClientId = clientId;
    }

    public Site[] getSites() {
        return siteList.getList();
    }

    /**
     * Return frame class name.
     */
    public String getFrameName() {
        String typeName = localClientId.getClientType().toString();

        // TODO change this to a table lookup

        return typeName.charAt(0) + typeName.substring(1).toLowerCase() + "View";
    }

    public String getTitle() {
        ClientId id = getClientId();
        String titleCase = id.getClientType().toString();
        titleCase = titleCase.charAt(0) + titleCase.substring(1);
        return titleCase + " " + id.getClientNumber() + " (Site " + id.getSiteNumber() + ")";
    }

    public void addProblemListener(IProblemListener problemListener) {
        problemListenerList.addElement(problemListener);
    }

    public void removeProblemListener(IProblemListener problemListener) {
        problemListenerList.remove(problemListener);
    }

    public void addLanguageListener(ILanguageListener languageListener) {
        languageListenerList.addElement(languageListener);
    }

    public void removeLanguageListener(ILanguageListener languageListener) {
        languageListenerList.remove(languageListener);
    }

    public void addLoginListener(ILoginListener loginListener) {
        loginListenerList.addElement(loginListener);
    }

    public void removeLoginListener(ILoginListener loginListener) {
        loginListenerList.remove(loginListener);
    }

    public void addSiteListener(ISiteListener siteListener) {
        siteListenerList.add(siteListener);
    }

    public void removeSiteListener(ISiteListener siteListener) {
        siteListenerList.remove(siteListener);
    }

    public Run getRun(ElementId id) {
        return runList.get(id);
    }

    public Vector<Account> getAccounts(Type type, int inSiteNumber) {
        return accountList.getAccounts(type, inSiteNumber);
    }

    public Vector<Account> getAccounts(Type type) {
        return accountList.getAccounts(type);
    }

    public boolean isValidLoginAndPassword(ClientId inClientId, String password) {
        return accountList.isValidLoginAndPassword(inClientId, password);
    }

    public ClientId getLoginClientId(ConnectionHandlerID connectionHandlerID) {
        return loginList.getClientId(connectionHandlerID);
    }

    public boolean isLoggedIn(ClientId sourceId) {
        return loginList.isLoggedIn(sourceId);
    }
    
    public boolean isLoggedIn(){
        return localClientId != null;
    }

    public ConnectionHandlerID getConnectionHandleID(ClientId sourceId) {
        return loginList.getConnectionHandleID(sourceId);
    }

    public void removeLogin(ClientId sourceId) {
        loginList.remove(sourceId);
    }

    public int getSiteNumber() {
        return siteNumber;
    }
    
    public void setSiteNumber(int number) {
        this.siteNumber = number;
    }

    
    @SuppressWarnings("unused")
    // TODO remove isServer method ?
    private boolean isServer() {
        return getClientId().getClientType().equals(ClientType.Type.SERVER);
    }

    /**
     * Get this site's contest time.
     */
    public ContestTime getContestTime() {
        return getContestTime(getSiteNumber());
    }

    public ContestTime getContestTime(int inSiteNumber) {
        ContestTime contestTime2 = contestTimeList.get(inSiteNumber);
        if (contestTime2 == null){
            StaticLog.info("getContestTime time for "+inSiteNumber+" does not exist, created it. ");
            contestTime2 = new ContestTime();
            contestTime2.startContestClock();
            addContestTime(contestTime2, inSiteNumber);
        }
        return  contestTime2;
    }

    public void startContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STARTED, contestTime,
                    inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Attempted to start clock site " + inSiteNumber);
        }
    }

    public void stopContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STOPPED, contestTime,
                    inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Attempted to stop clock site " + inSiteNumber);
        }
    }

    public void addContestTime(ContestTime contestTime, int inSiteNumber) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        contestTimeList.add(inSiteNumber, contestTime);
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.ADDED, contestTime, inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Attempted to stop clock site " + inSiteNumber);
        }
    }

    public Enumeration<ClientId> getLoggedInClients(Type type) {
        return loginList.getClients(type);
    }

    public static void info(String s) {
        System.err.println(Thread.currentThread().getName() + " " + s);
    }

    public Run[] getRuns() {
        return runList.getList();
    }

    public void runUpdated (Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoUpdatedRun) {
        // TODO handle run RunResultsFiles
        runList.updateRun(run, judgementRecord);
        Run newRun = runList.get(run.getElementId());
        RunEvent runEvent = new RunEvent(RunEvent.Action.UPDATED, newRun, null);
        runEvent.setWhoModifiedRun(localClientId);
        fireRunListener(runEvent);
    }

    public void runNotAvailable(Run run) {
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_NOT_AVIALABLE, run, null);
        fireRunListener(runEvent);
    }

    public void updateRun(Run run, RunStates newState, ClientId whoChangedRun) {
        runList.updateRun(run,newState);
        runCheckOutList.put(run.getElementId(), whoChangedRun);
        Run newRun = runList.get(run.getElementId());
        RunEvent runEvent = new RunEvent(RunEvent.Action.UPDATED, newRun, null);
        runEvent.setWhoModifiedRun(whoChangedRun);
        fireRunListener(runEvent);
    }

    public RunFiles getRunFiles(Run run) {
        return runFilesList.getRunFiles(run);
    }

    public void addRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedItId) {
        
        Run theRun = runList.get(run);
        ClientId whoCheckedOut = runCheckOutList.get(run.getElementId());
//        
//        if (whoCheckedOut == null || whoCheckedOut.equals(whoJudgedItId)){
//            // TODO security code, handle this problem.
//            System.err.println("Security Warning "+run+" not checked out by "+whoJudgedItId);
//        }
        
        runList.updateRun(theRun, judgementRecord);
        runCheckOutList.remove(whoCheckedOut);
        theRun = runList.get(run);
        
        RunEvent runEvent = new RunEvent(RunEvent.Action.UPDATED, theRun, null);
        fireRunListener(runEvent);
        
    }

    public void cancelRunCheckOut(Run run, ClientId fromId) {

        ClientId whoCheckedOut = runCheckOutList.get(run.getElementId());
//        if (fromId.equals(whoCheckedOut)) {
//            // TODO security code, handle this problem.
//            StaticLog.unclassified("Security Warning canceling "+run+",  not checked out by "+whoCheckedOut);
//        }
        
        runCheckOutList.remove(whoCheckedOut);
        runList.updateRun(run, RunStates.NEW);
        Run theRun = runList.get(run);
        
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_AVIALABLE, theRun, null);
        fireRunListener(runEvent);
    }

    public ClientId getRunCheckedOutBy(Run run) {
        return runCheckOutList.get(run.getElementId());
    }

    public void updateLanguage(Language language) {
        // TODO code 

    }

    public void updateProblem(Problem problem) {
        // TODO code 

    }

    public void updateContestTime(ContestTime contestTime, int siteNum) {
        // TODO code 

    }

    public void updateContestTime(ContestTime contestTime) {
        // TODO code 

    }

    public void updateJudgement(Judgement judgement) {
        // TODO code 

    }

    public void updateSite(Site site) {
        // TODO code 

    }

    public void updateRun(Run run) {
        // TODO code 

    }

    public void updateAccount(Account account) {
        // TODO code 

    }


}
