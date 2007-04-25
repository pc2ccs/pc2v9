package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.ClarificationList;
import edu.csus.ecs.pc2.core.list.ConnectionHandlerList;
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
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Clarification.ClarificationStates;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Implementation of IModel.
 * 
 * This model is not responsible for logic, just storage. So, for example, {@link #cancelRunCheckOut(Run, ClientId)} will simply
 * update the Run but will not check whether the run should be cancelled. The Controller should be used to check whether a Run
 * should be cancelled. Other logic of this sort is in the Controller, not the Model.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Model implements IModel {

    public static final String SVN_ID = "$Id$";

    private ClientId localClientId = null;

    private Vector<IRunListener> runListenerList = new Vector<IRunListener>();

    private Vector<IClarificationListener> clarificationListenerList = new Vector<IClarificationListener>();

    private Vector<IProblemListener> problemListenerList = new Vector<IProblemListener>();

    private Vector<ILanguageListener> languageListenerList = new Vector<ILanguageListener>();

    private Vector<ILoginListener> loginListenerList = new Vector<ILoginListener>();

    private Vector<IContestTimeListener> contestTimeListenerList = new Vector<IContestTimeListener>();

    private Vector<IJudgementListener> judgementListenerList = new Vector<IJudgementListener>();

    private Vector<ISiteListener> siteListenerList = new Vector<ISiteListener>();

    private Vector<IConnectionListener> connectionListenerList = new Vector<IConnectionListener>();
 
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

    private ClarificationList clarificationList = new ClarificationList();

    private SiteList siteList = new SiteList();
    
    private ConnectionHandlerList connectionHandlerList = new ConnectionHandlerList();

    private int siteNumber = 1;

    /**
     * List of all defined problems. Contains deleted problems too.
     */
    private ProblemList problemList = new ProblemList();
    
    private ProblemDataFilesList problemDataFilesList = new ProblemDataFilesList();

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
    
    private Site createFakeSite (int nextSiteNumber){
        Site site = new Site("Site "+nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, "localhost");
        int port = 50002 + (nextSiteNumber-1)* 1000;
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site"+nextSiteNumber);
        return site;
    }

    /**
     * Initialize Model with data.
     */
    public void initializeWithFakeData() {

        String[] probNames = { "A - Sum of Squares", "B - Sumit", "C - Hello", "D - GoodBye" };

        for (String problemName : probNames) {
            Problem problem = new Problem(problemName);
            String baseName = problemName.substring(4).toLowerCase();
            problem.setDataFileName(baseName +".dat");
            problem.setAnswerFileName(baseName+".ans");
            problem.setReadInputDataFromSTDIN(false);
            problem.setTimeOutInSeconds(180);
            addProblem(problem);
            
            ClientId clientId = new ClientId(1, Type.ADMINISTRATOR, 1);
            String question = "Why is problem "+problemName+" so hard ?";
            Clarification clarification = new Clarification(clientId, problem, question);
            acceptClarification(clarification);
        }

        // TO ALL clar
        ClientId clientId = new ClientId(1, Type.ADMINISTRATOR, 1);
        String question = "Why are all the problems so hard so hard ?";
        Problem problem = getProblems()[0];
        Clarification clarification = new Clarification(clientId, problem, question);
        Clarification newClar = acceptClarification(clarification);
        answerClarification(newClar, "Because We say so", clientId, true);

        Language language = createLanguageFromAutoFill(LanguageAutoFill.JAVATITLE);
        addLanguage(language);

        language = createLanguageFromAutoFill(LanguageAutoFill.GNUCTITLE);
        addLanguage(language);

        language = createLanguageFromAutoFill(LanguageAutoFill.MSCTITLE);
        addLanguage(language);
        
        // Generate the server account
        generateNewAccounts(ClientType.Type.SERVER.toString(), 1, true);

        ContestTime contestTime = new ContestTime();
        contestTime.setElapsedMins(9);
        contestTime.startContestClock();

        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "No - It's just really bad",
                "No - judges enjoyed a good laugh", "You've been bad - contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            addJudgement(judgement);
        }
        
        // Add root account 
        generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), 1, true);
        
        Site site = createFakeSite (1);
        site.setActive(true);
        siteList.add(site);
    }



    private Language createLanguageFromAutoFill(String key) {
        
        String [] values = LanguageAutoFill.getAutoFillValues(key);
        Language language = new Language(key);
        language.setCompileCommandLine(values[1]);
        language.setExecutableIdentifierMask(values[2]);
        language.setProgramExecuteCommandLine(values[3]);
        language.setSiteNumber(getSiteNumber());
        return language;
    }

    public void addRunListener(IRunListener runListener) {
        runListenerList.addElement(runListener);
    }

    public void removeRunListener(IRunListener runListener) {
        runListenerList.removeElement(runListener);
    }
    
    public void addClarificationListener(IClarificationListener clarificationListener) {
        clarificationListenerList.addElement(clarificationListener);
    }

    public void removeClarificationListener(IClarificationListener clarificationListener) {
        clarificationListenerList.remove(clarificationListener);
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
            } else if (contestTimeEvent.getAction() == ContestTimeEvent.Action.CHANGED) {
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
            } else if (siteEvent.getAction() == SiteEvent.Action.CHANGED) {
                siteListenerList.elementAt(i).siteChanged(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.LOGIN) {
                siteListenerList.elementAt(i).siteLoggedOn(siteEvent);
            } else if (siteEvent.getAction() == SiteEvent.Action.LOGOFF) {
                siteListenerList.elementAt(i).siteLoggedOff(siteEvent);
            } else {
                siteListenerList.elementAt(i).siteAdded(siteEvent);
            }
        }
    }
    

    private void fireConnectionListener(ConnectionEvent connectionEvent) {
        for (int i = 0; i < connectionListenerList.size(); i++) {

            if (connectionEvent.getAction() == ConnectionEvent.Action.ESTABLISHED) {
                connectionListenerList.elementAt(i).connectionEstablished(connectionEvent);
            } else if (connectionEvent.getAction() == ConnectionEvent.Action.DROPPED) {
                connectionListenerList.elementAt(i).connectionDropped(connectionEvent);
            } else {
                throw new UnsupportedOperationException("Unknown connection action " + connectionEvent.getAction());
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
    
    public void updateSite (Site site){
        siteList.add(site);
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.CHANGED, site);
        fireSiteListener(siteEvent);
        
    }
    
    public void addAccount(Account account) {
        accountList.add(account);
        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED,account);
        fireAccountListener(accountEvent);
    }

    public Judgement[] getJudgements() {
        return judgementDisplayList.getList();
    }

    /**
     * Accept Run, add new run into server.
     */
    public Run acceptRun (Run run, RunFiles runFiles) {
        run.setElapsedMins(getContestTime().getElapsedMins());
        run.setSiteNumber(getSiteNumber());
        Run newRun = runList.addNewRun(run); // this set the run number.
        if (runFiles != null) {
            runFilesList.add(newRun, runFiles);
        }
        addRun(newRun);
        return newRun;
    }
    
    /**
     * Accept Clarification, add run into this server.
     * 
     * @param clarification
     * @return
     */
    public Clarification acceptClarification (Clarification clarification){
        clarification.setElapsedMins(getContestTime().getElapsedMins());
        clarification.setSiteNumber(getSiteNumber());
        Clarification newClarification = clarificationList.addNewClarification(clarification);
        addClarification(clarification);
        return newClarification;
    }
    
    public void answerClarification (Clarification clarification, String answer, ClientId whoAnsweredIt, boolean sendToAll){
        Clarification updatedClarification = clarificationList.updateClarification(clarification, ClarificationStates.ANSWERED, whoAnsweredIt, answer, sendToAll);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ANSWERED_CLARIFICATION, updatedClarification);
        fireClarificationListener(clarificationEvent);
    }
    
    public void updateClarification (Clarification clarification){
        clarificationList.updateClarification(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHANGED, clarification);
        fireClarificationListener(clarificationEvent);
    }
    
    /**
     * Add a run to run list, notify listeners.
     * 
     * @param run
     */
    public void addRun (Run run){
        runList.add(run);
        RunEvent runEvent = new RunEvent(RunEvent.Action.ADDED, run, null);
        fireRunListener(runEvent);
    }
    
    public void addRun(Run run, RunFiles runFiles, ClientId whoCheckedOutRunId) {
        runList.add(run); 
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHECKEDOUT_RUN, run, runFiles);
        runEvent.setSentToClientId(whoCheckedOutRunId);
        fireRunListener(runEvent);
    }
    
    public void availableRun(Run run) {
        runList.add(run); 
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_AVIALABLE, run, null);
        fireRunListener(runEvent); 
    }


    /**
     * Generate accounts.
     * 
     * @see edu.csus.ecs.pc2.core.model.IModel#generateNewAccounts(java.lang.String, int, int, boolean)
     * @param clientTypeName
     * @param count
     * @param active
     */
    public Vector<Account> generateNewAccounts(String clientTypeName, int count, boolean active) {
        return generateNewAccounts(clientTypeName, count, 1, active);
    }

    /**
     * @see edu.csus.ecs.pc2.core.model.IModel#generateNewAccounts(java.lang.String, int, int, boolean)
     */
    public Vector<Account> generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active) {
        ClientType.Type type = ClientType.Type.valueOf(clientTypeName.toUpperCase());

        Vector<Account> newAccounts = accountList.generateNewAccounts(type, count, startNumber, PasswordType.JOE, siteNumber, active);

        for (int i = 0; i < newAccounts.size(); i++) {
            Account account = newAccounts.elementAt(i);
            AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.ADDED, account);
            fireAccountListener(accountEvent);
        }
        return newAccounts;
    }
    
    /**
     * Generate new sites
     * 
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
        if (isServer()){
            
            /**
             * These are actions that can only be taken when the server knows 
             * which site number it is, until this point the server has been 
             * not assigned a site number.
             */
            
            runList = new RunList(clientId.getSiteNumber(), true);
            runFilesList = new RunFilesList(clientId.getSiteNumber());
            if (getContestTime() == null){
                ContestTime contestTime = new ContestTime();
                contestTime.setSiteNumber(getSiteNumber());
                contestTime.startContestClock(); // TODO remove this start contest, eventually
                addContestTime(contestTime);
            }
        }
    }

    public Site[] getSites() {
        return siteList.getList();
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

    public void addConnectionListener(IConnectionListener connectionListener) {
        connectionListenerList.addElement(connectionListener);
    }

    public void removeConnectionListener(IConnectionListener connectionListener) {
        connectionListenerList.remove(connectionListener);
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
    
    public ConnectionHandlerID[] getConnectionHandleIDs() {
        return connectionHandlerList.getList();
    }


    public void removeLogin(ClientId sourceId) {
        loginList.remove(sourceId);
        ConnectionHandlerID connectionHandlerID = getConnectionHandleID(sourceId);
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.LOGOFF, sourceId, connectionHandlerID, "Logoff");
        fireLoginListener(loginEvent);
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
        if (contestTime2 == null && inSiteNumber == getSiteNumber()){
            /**
             * Insure that this Contest Time is created.
             */
            StaticLog.info("Warning getContestTime time for "+inSiteNumber+" does not exist, created it. ");
            contestTime2 = new ContestTime();
            contestTime2.setSiteNumber(inSiteNumber);
            contestTime2.startContestClock();
            addContestTime(contestTime2);
        }
        return  contestTime2;
    }
    
    public ContestTime[] getContestTimes() {
        return contestTimeList.getList();
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

    public void addContestTime(ContestTime contestTime) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        contestTimeList.add(contestTime);
        ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.ADDED, contestTime, contestTime.getSiteNumber());
        fireContestTimeListener(contestTimeEvent);
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
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, newRun, null);
        runEvent.setWhoModifiedRun(whoUpdatedRun);
        fireRunListener(runEvent);
    }

    public void runNotAvailable(Run run) {
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_NOT_AVIALABLE, run, null);
        fireRunListener(runEvent);
    }

    // Check out run
    public void updateRun (Run run, RunStates newState, ClientId whoChangedRun) {
        runList.updateRun(run,newState);
        if (newState.equals(RunStates.BEING_JUDGED)){
            runCheckOutList.put(run.getElementId(), whoChangedRun);
        }

        Run newRun = runList.get(run.getElementId());
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, newRun, null);
        runEvent.setWhoModifiedRun(whoChangedRun);
        fireRunListener(runEvent);
    }

    public RunFiles getRunFiles(Run run) {
        return runFilesList.getRunFiles(run);
    }

    public void addRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedItId) {
        
        Run theRun = runList.get(run);
        ClientId whoCheckedOut = runCheckOutList.get(run.getElementId());
        ClientId whoChangedItId = judgementRecord.getJudgerClientId();
        
        if (whoCheckedOut == null) {
            // No one did this ?
            
            Exception ex = new Exception ("addRunJudgement - not in checkedout list, whoCheckedOut is null ");
            StaticLog.log("debug ", ex);
            info("Exception in log"+ex.getMessage());


        } else if ( ! whoChangedItId.equals(whoCheckedOut))  {
            // The judge who submitted this judgement is different than who actually judged it ?

            Exception ex = new Exception ("addRunJudgement - who checked out and who it is differ ");
            StaticLog.log("debug ", ex);
            info("Exception in log"+ex.getMessage());

            
        } else {
            // Judge is ok.
            info("debug all is well, continuing... ");
            
        }
        runList.updateRun(theRun, judgementRecord); // this sets run to JUDGED
        info("debug  updated run to judged "+theRun );
        
        if (whoCheckedOut != null){
            info("debug found checked out by "+whoCheckedOut+" judgement updated by "+judgementRecord.getJudgerClientId());
            runCheckOutList.remove(whoCheckedOut);
        }
        theRun = runList.get(run);
        
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, theRun, null);
        fireRunListener(runEvent);
        
    }

    public void cancelRunCheckOut(Run run, ClientId fromId) {

        ClientId whoCheckedOut = runCheckOutList.get(run.getElementId());
// if (fromId.equals(whoCheckedOut)) {
// // TODO security code, handle this problem.
// StaticLog.unclassified("Security Warning canceling "+run+", not checked out by "+whoCheckedOut);
// }
        
        runCheckOutList.remove(whoCheckedOut);
        runList.updateRun(run, RunStates.NEW);
        Run theRun = runList.get(run);
        
        RunEvent runEvent = new RunEvent(RunEvent.Action.RUN_AVIALABLE, theRun, null);
        fireRunListener(runEvent);
    }

    public ClientId getRunCheckedOutBy(Run run) {
        return runCheckOutList.get(run.getElementId());
    }

    
    public Clarification [] getClarifications(){
        return clarificationList.getList();
    }
    
    public void updateLanguage(Language language) {
        languageList.update(language);
        LanguageEvent languageEvent = new LanguageEvent(LanguageEvent.Action.CHANGED, language);
        fireLanguageListener(languageEvent);
    }

    public void updateProblem(Problem problem) {
        problemList.update(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.CHANGED, problem);
        fireProblemListener(problemEvent);
    }

    public void updateContestTime(ContestTime contestTime, int inSiteNumber) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        if (inSiteNumber != contestTime.getSiteNumber()){
            throw new IllegalArgumentException("contestTime site number ("+contestTime+") does not match "+inSiteNumber);
        }
        contestTimeList.update(contestTime);
        ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CHANGED, contestTime, contestTime.getSiteNumber());
        fireContestTimeListener(contestTimeEvent);

    }

    public void updateContestTime(ContestTime contestTime) {
        if (contestTime == null) {
            throw new IllegalArgumentException("contestTime is null");
        }
        contestTimeList.update(contestTime);
        ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CHANGED, contestTime, contestTime.getSiteNumber());
        fireContestTimeListener(contestTimeEvent);
    }

    public void updateJudgement(Judgement judgement) {
        judgementList.update(judgement);
        JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.CHANGED, judgement);
        fireJudgementListener(judgementEvent);
    }

    public void changeSite(Site site) {
        siteList.update(site);
        SiteEvent siteEvent = new SiteEvent(SiteEvent.Action.CHANGED, site);
        fireSiteListener(siteEvent);
    }

    public void updateRun(Run run) {
        runList.updateRun(run);
        RunEvent runEvent = new RunEvent(RunEvent.Action.CHANGED, run, null);
        fireRunListener(runEvent);
    }

    public void updateAccount(Account account) {
        accountList.update(account);
        AccountEvent accountEvent = new AccountEvent(AccountEvent.Action.CHANGED, account);
        fireAccountListener(accountEvent);
    }

    public Language getLanguage(ElementId elementId) {
        return (Language) languageList.get(elementId);
    }
    
    public ContestTime getContestTime (ElementId elementId) {
        return (ContestTime) contestTimeList.get(elementId);
    }


    public Problem getProblem(ElementId elementId) {
        return (Problem) problemList.get(elementId);
    }

    public Judgement getJudgement(ElementId elementId) {
        return (Judgement) judgementList.get(elementId);
    }

    public Account getAccount(ClientId inClientId) {
        return (Account) accountList.getAccount(inClientId);
    }

    public Site getSite(int number) {
        Site []  sites = siteList.getList();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        return sites[number - 1];
    }
    



    private void fireClarificationListener(ClarificationEvent clarificationEvent) {
        for (int i = 0; i < clarificationListenerList.size(); i++) {

            if (clarificationEvent.getAction() == ClarificationEvent.Action.ADDED) {
                clarificationListenerList.elementAt(i).clarificationAdded(clarificationEvent);
            } else if (clarificationEvent.getAction() == ClarificationEvent.Action.DELETED) {
                clarificationListenerList.elementAt(i).clarificationRemoved(clarificationEvent);
            } else {
                clarificationListenerList.elementAt(i).clarificationChanged(clarificationEvent);
            }
        }
    }

    public void addClarification(Clarification clarification) {
        clarificationList.add(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ADDED, clarification);
        fireClarificationListener(clarificationEvent);
    }

    public void removeClarification(Clarification clarification) {
        clarificationList.delete(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.DELETED, clarification);
        fireClarificationListener(clarificationEvent);
    }

    public void changeClarification(Clarification clarification) {
        clarificationList.updateClarification(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHANGED, clarification);
        fireClarificationListener(clarificationEvent);
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID) {
        connectionEstablished(connectionHandlerID, new Date());
    }

    public void connectionEstablished(ConnectionHandlerID connectionHandlerID, Date connectDate) {
        connectionHandlerList.add(connectionHandlerID, connectDate);
        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.ESTABLISHED, connectionHandlerID);
        fireConnectionListener(connectionEvent);
    }

    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        connectionHandlerList.remove(connectionHandlerID);
        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.DROPPED, connectionHandlerID);
        fireConnectionListener(connectionEvent);
    }

    public ConnectionHandlerID[] getConnectionHandlerIDs() {
        return connectionHandlerList.getList();
    }

    public Clarification[] getClarifications(ClientId clientId) {

        Vector<Clarification> clientClarifications = new Vector<Clarification>();
        Enumeration<Clarification> enumeration = clarificationList.getClarList();
        while (enumeration.hasMoreElements()) {
            Clarification clarification = (Clarification) enumeration.nextElement();

            if (clarification.isSendToAll() || clientId.equals(clarification.getSubmitter())) {
                clientClarifications.add(clarification);
            }
        }
        return (Clarification[]) clientClarifications.toArray(new Clarification[clientClarifications.size()]);
    }

    public Run[] getRuns(ClientId clientId) {
        Vector<Run> clientClarifications = new Vector<Run>();
        Enumeration<Run> enumeration = runList.getRunList();
        while (enumeration.hasMoreElements()) {
            Run clarification = (Run) enumeration.nextElement();

            if (clientId.equals(clarification.getSubmitter())) {
                clientClarifications.add(clarification);
            }
        }
        return (Run[]) clientClarifications.toArray(new Run[clientClarifications.size()]);
    }

    public void addProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        problemList.add(problem);
        problemDataFilesList.add(problemDataFiles);
        
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.ADDED,problem, problemDataFiles);
        fireProblemListener(problemEvent);
    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        problemList.update(problem);
        problemDataFilesList.update(problemDataFiles);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.CHANGED,problem, problemDataFiles);
        fireProblemListener(problemEvent);
    }

    public ProblemDataFiles getProblemDataFiles(Problem problem) {
        return (ProblemDataFiles) problemDataFilesList.get(problem);
    }
}
