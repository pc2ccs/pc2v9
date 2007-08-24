package edu.csus.ecs.pc2.core.model;

import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.ClarificationList;
import edu.csus.ecs.pc2.core.list.ClientSettingsList;
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
 * Implementation of IContest.
 * 
 * This model is not responsible for logic, just storage. So, for example, {@link #cancelRunCheckOut(Run, ClientId)} will simply
 * update the Run but will not check whether the run should be cancelled. The Controller should be used to check whether a Run
 * should be cancelled. Other logic of this sort is in the Controller, not the Contest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
// $Id$
public class Contest implements IContest {

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
 
    private Vector<IClientSettingsListener> clientSettingsListenerList = new Vector<IClientSettingsListener>();
    
    private Vector<IContestInformationListener> contestInformationListenerList = new Vector<IContestInformationListener>();

    /**
     * Contains name of client (judge or admin) who checks out the run.
     */
    private Hashtable<ElementId, ClientId> runCheckOutList = new Hashtable<ElementId, ClientId>(200);

    private AccountList accountList = new AccountList();

    private Vector<IAccountListener> accountListenerList = new Vector<IAccountListener>();

    /**
     * Logins on this site.
     * 
     * These are all logins that have been authenticated by the
     * local server.
     */
    private LoginList localLoginList = new LoginList();
    
    /**
     * Logins on other sites.
     * 
     * These are all the logins taht have been authenticated
     * by a remote server.
     */
    private LoginList remoteLoginList = new LoginList();
    
    /**
     * Connections on this site.
     */
    private ConnectionHandlerList localConnectionHandlerList = new ConnectionHandlerList();

    /**
     * Connections on other sites
     */
    private ConnectionHandlerList remoteConnectionHandlerList = new ConnectionHandlerList();

    private ContestTimeList contestTimeList = new ContestTimeList();

    private RunList runList = new RunList();

    private RunFilesList runFilesList = new RunFilesList();

    private ClarificationList clarificationList = new ClarificationList();

    private SiteList siteList = new SiteList();
    
    /**
     * List of all settings for client.
     */
    private ClientSettingsList clientSettingsList = new ClientSettingsList();
    
    /**
     * Contest Information (like title).
     */
    private ContestInformation contestInformation = new ContestInformation();
    
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
    
    public void initializeSubmissions(int siteNum) {
        
        runList = new RunList(siteNum, true);
        runFilesList = new RunFilesList(siteNum);
        clarificationList = new ClarificationList(siteNum, true);
        
        try {
            runList.loadFromDisk(siteNum);
        } catch (Exception e) {
            StaticLog.log("Trouble loading runs from disk ", e);
        }
        try {
            clarificationList.loadFromDisk(siteNum);
        } catch (Exception e) {
            StaticLog.log("Trouble loading clarifications from disk ", e);
        }
        
    }

    /**
     * Initialize Contest with data.
     */
    public void initializeStartupData() {

        if (siteList.size() == 0){
            Site site = createFakeSite (1);
            site.setActive(true);
            siteList.add(site);
        }
        
        ContestTime contestTime = new ContestTime();
        contestTime.setSiteNumber(1);
        addContestTime(contestTime);
        
        generateNewAccounts(ClientType.Type.SERVER.toString(), 1, true);

        String[] judgementNames = { "Yes", "No - Compilation Error", "No - Run-time Error", "No - Time-limit Exceeded", "No - Wrong Answer", "No - Excessive Output", "No - Output Format Error",
                "No - Other - Contact Staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            addJudgement(judgement);
        }
        
        // Add root account 
        generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), 1, true);
        
        contestInformation.setContestTitle("Default Contest Title");
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

    public void addLocalLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        localLoginList.add(inClientId, connectionHandlerID);
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.NEW_LOGIN, inClientId, connectionHandlerID, "New");
        fireLoginListener(loginEvent);
    
    }

    public void addRemoteLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        remoteLoginList.add(inClientId, connectionHandlerID);
        LoginEvent loginEvent = new LoginEvent(LoginEvent.Action.NEW_LOGIN, inClientId, connectionHandlerID, "New");
        fireLoginListener(loginEvent);
    }

    public void addLogin(ClientId inClientId, ConnectionHandlerID connectionHandlerID) {
        if (inClientId.getSiteNumber() == siteNumber){
            localLoginList.add(inClientId, connectionHandlerID);
        } else {
            remoteLoginList.add(inClientId, connectionHandlerID);
        }
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
    
    public void answerClarification(Clarification clarification, String answer, ClientId whoAnsweredIt, boolean sendToAll) {

        if (clarificationList.get(clarification) != null) {
            Clarification answerClarification = clarificationList.updateClarification(clarification, ClarificationStates.ANSWERED, whoAnsweredIt, answer, sendToAll);
            ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ANSWERED_CLARIFICATION, answerClarification);
            fireClarificationListener(clarificationEvent);
        } else {
            clarificationList.add(clarification);
            Clarification updatedClarification =  clarificationList.get(clarification);
            ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.ANSWERED_CLARIFICATION, updatedClarification);
            fireClarificationListener(clarificationEvent);
        }
    }
    
    public void updateClarification (Clarification clarification, ClientId whoChangedIt) {
        clarificationList.updateClarification(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHANGED, clarification);
        clarificationEvent.setWhoModifiedClarification(whoChangedIt);
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
        runFilesList.add(run, runFiles);
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
     * @see edu.csus.ecs.pc2.core.model.IContest#generateNewAccounts(java.lang.String, int, int, boolean)
     * @param clientTypeName
     * @param count
     * @param active
     */
    public Vector<Account> generateNewAccounts(String clientTypeName, int count, boolean active) {
        return generateNewAccounts(clientTypeName, count, 1, active);
    }

    /**
     * @see edu.csus.ecs.pc2.core.model.IContest#generateNewAccounts(java.lang.String, int, int, boolean)
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
//        if (isServer()){
            
            /**
             * Now that this model know its site number, we can now create
             * the site specific database directories and files, or load
             * them as needed. 
             */
            
//            runList = new RunList(clientId.getSiteNumber(), true);
//            runFilesList = new RunFilesList(clientId.getSiteNumber());
//            clarificationList = new ClarificationList(clientId.getSiteNumber(), true);
            
//        }
    }

    public Site[] getSites() {
        return siteList.getList();
    }

    public String getTitle() {
        ClientId id = getClientId();
        if (id == null){
            return "(Client not logged in)";
        }
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
    
    public Clarification getClarification(ElementId id){
        return clarificationList.get(id);
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
        ClientId clientId = localLoginList.getClientId(connectionHandlerID);
        if (clientId == null){
            clientId = remoteLoginList.getClientId(connectionHandlerID);
        }
        return clientId;
    }
    
    public boolean isLoggedIn(){
        return localClientId != null;
    }
    
    public boolean isRemoteLoggedIn(ClientId clientId){
        return remoteLoginList.isLoggedIn(clientId);
    }
    public boolean isLocalLoggedIn(ClientId clientId) {
        return localLoginList.isLoggedIn(clientId);
    }

    public ConnectionHandlerID getConnectionHandleID(ClientId sourceId) {
        ConnectionHandlerID connectionHandlerID = localLoginList.getConnectionHandleID(sourceId);
        if (connectionHandlerID == null) {
            connectionHandlerID = remoteLoginList.getConnectionHandleID(sourceId);
        }
        return connectionHandlerID;
    }
    
    public boolean isConnected(ConnectionHandlerID connectionHandlerID) {
        return localConnectionHandlerList.get(connectionHandlerID) != null;
    }
    

    public boolean isConnectedToRemoteSite(ConnectionHandlerID connectionHandlerID) {
        return remoteConnectionHandlerList.get(connectionHandlerID) != null;
    }

    
    public ConnectionHandlerID[] getConnectionHandleIDs() {

        ConnectionHandlerID[] localList = localConnectionHandlerList.getList();
        ConnectionHandlerID[] remoteList = remoteConnectionHandlerList.getList();

        if (localList.length > 0 && remoteList.length > 0) {
            // Add both list together
            ConnectionHandlerID[] allConnections = new ConnectionHandlerID[localList.length + remoteList.length];
            System.arraycopy(localList, 0, allConnections, 0, localList.length);
            System.arraycopy(remoteList, 0, allConnections, localList.length, remoteList.length);
            return allConnections;
        } else if (localList.length > 0) {
            return localList;
        } else { // must either be only remoteList 
            return remoteList;
        }
    }


    public void removeLogin(ClientId sourceId) {
        if (sourceId.getSiteNumber() == siteNumber) {
            localLoginList.remove(sourceId);
        } else {
            remoteLoginList.remove(sourceId);
        }
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
        return contestTimeList.get(inSiteNumber);
    }
    
    public ContestTime[] getContestTimes() {
        return contestTimeList.getList();
    }

    public void startContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        contestTime.startContestClock();
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STARTED, contestTime, inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Unable to start clock site " + inSiteNumber + " not found");
        }
    }

    public void stopContest(int inSiteNumber) {
        ContestTime contestTime = getContestTime(inSiteNumber);
        contestTime.stopContestClock();
        if (contestTime != null) {
            ContestTimeEvent contestTimeEvent = new ContestTimeEvent(ContestTimeEvent.Action.CLOCK_STOPPED, contestTime, inSiteNumber);
            fireContestTimeListener(contestTimeEvent);
        } else {
            throw new SecurityException("Unable to stop clock site " + inSiteNumber + " not found");
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
    
    public Enumeration<ClientId> getLocalLoggedInClients(Type type) {
        return localLoginList.getClients(type);
    }

    public Enumeration<ClientId> getLoggedInClients(Type type) {
        Enumeration<ClientId> localClients = localLoginList.getClients(type);
        Enumeration<ClientId> remoteClients = remoteLoginList.getClients(type);

        Vector <ClientId> v = new Vector<ClientId>();
        
        while (localClients.hasMoreElements()) {
            ClientId element = (ClientId) localClients.nextElement();
            v.addElement(element);
        }
        
        while (remoteClients.hasMoreElements()) {
            ClientId element = (ClientId) remoteClients.nextElement();
            v.addElement(element);
        }
        
        return v.elements();
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
    public void updateRun (Run run, ClientId whoChangedRun) {
        runList.updateRun(run);
        Run newRun = runList.get(run.getElementId());
        if (newRun.getStatus().equals(RunStates.BEING_JUDGED)){
            runCheckOutList.put(run.getElementId(), whoChangedRun);
        }

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
            
            if (whoChangedItId.getClientType().equals(Type.ADMINISTRATOR)){
                info("Admin updating run "+run);
                
            } else {
                Exception ex = new Exception ("addRunJudgement - who checked out and who it is differ ");
                info("Exception in log"+ex.getMessage());
            }
            
        } else {
            // Judge is ok.
            info("debug all is well in addRunJudgement, continuing... ");
            
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
        run.setStatus(RunStates.NEW);
        runList.updateRun(run);
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
        languageDisplayList.update(language);
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
    
    public void addClarification(Clarification clarification, ClientId whoCheckedOutId) {
        clarificationList.add(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CHANGED, clarification);
        clarificationEvent.setWhoModifiedClarification(whoCheckedOutId);
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
        localConnectionHandlerList.add(connectionHandlerID, connectDate);
        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.ESTABLISHED, connectionHandlerID);
        fireConnectionListener(connectionEvent);
    }

    public void connectionDropped(ConnectionHandlerID connectionHandlerID) {
        localConnectionHandlerList.remove(connectionHandlerID);
        ConnectionEvent connectionEvent = new ConnectionEvent(ConnectionEvent.Action.DROPPED, connectionHandlerID);
        fireConnectionListener(connectionEvent);
    }

    public ConnectionHandlerID[] getConnectionHandlerIDs() {
        return localConnectionHandlerList.getList();
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
        problemDisplayList.add(problem);
        problemList.add(problem);
        if (problemDataFiles != null){
            problemDataFilesList.add(problemDataFiles);
        }
        
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.ADDED,problem, problemDataFiles);
        fireProblemListener(problemEvent);
    }

    public void updateProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        problemList.update(problem);
        problemDataFilesList.update(problemDataFiles);
        problemDisplayList.update(problem);
        ProblemEvent problemEvent = new ProblemEvent(ProblemEvent.Action.CHANGED,problem, problemDataFiles);
        fireProblemListener(problemEvent);
    }

    public ProblemDataFiles getProblemDataFile(Problem problem) {
        return (ProblemDataFiles) problemDataFilesList.get(problem);
    }

    public ProblemDataFiles[] getProblemDataFiles() {
        return problemDataFilesList.getList();
    }

    public void cancelClarificationCheckOut(Clarification clarification, ClientId whoCancelledIt) {
        clarificationList.updateClarification(clarification, ClarificationStates.NEW, whoCancelledIt);
        Clarification theClarification = clarificationList.get(clarification);
        ClarificationEvent clarificationEvent = new ClarificationEvent(ClarificationEvent.Action.CLARIFICATION_AVIALABLE, theClarification);
        fireClarificationListener(clarificationEvent);
    }
    
    private void fireClientSettingsListener(ClientSettingsEvent clientSettingsEvent) {
        for (int i = 0; i < clientSettingsListenerList.size(); i++) {

            if (clientSettingsEvent.getAction() == ClientSettingsEvent.Action.ADDED) {
                clientSettingsListenerList.elementAt(i).clientSettingsAdded(clientSettingsEvent);
            } else if (clientSettingsEvent.getAction() == ClientSettingsEvent.Action.DELETED) {
                clientSettingsListenerList.elementAt(i).clientSettingsRemoved(clientSettingsEvent);
            } else {
                clientSettingsListenerList.elementAt(i).clientSettingsChanged(clientSettingsEvent);
            }
        }
    }

    public void addClientSettings(ClientSettings clientSettings) {
        clientSettingsList.add(clientSettings);
        ClientSettingsEvent clientSettingsEvent = new ClientSettingsEvent(ClientSettingsEvent.Action.ADDED, clientSettings.getClientId(), clientSettings);
        fireClientSettingsListener(clientSettingsEvent);
    }
    
    public ClientSettings getClientSettings() {
        return (ClientSettings) clientSettingsList.get(getClientId());
    }
    
    public ClientSettings getClientSettings(ClientId clientId) {
        return (ClientSettings) clientSettingsList.get(clientId);
    }
    
    public ClientSettings [] getClientSettingsList() {
        return clientSettingsList.getList();
    }
    
    public void updateClientSettings(ClientSettings clientSettings) {
        clientSettingsList.update(clientSettings);
        ClientSettingsEvent clientSettingsEvent = new ClientSettingsEvent(ClientSettingsEvent.Action.CHANGED, clientSettings.getClientId(), clientSettings);
        fireClientSettingsListener(clientSettingsEvent);
    }

    public void addClientSettingsListener(IClientSettingsListener clientSettingsListener) {
        clientSettingsListenerList.addElement(clientSettingsListener);
    }

    public void removeClientSettingsListener(IClientSettingsListener clientSettingsListener) {
        clientSettingsListenerList.remove(clientSettingsListener);
    }

    private void fireContestInformationListener(ContestInformationEvent contestInformationEvent) {
        for (int i = 0; i < contestInformationListenerList.size(); i++) {

            if (contestInformationEvent.getAction() == ContestInformationEvent.Action.ADDED) {
                contestInformationListenerList.elementAt(i).contestInformationAdded(contestInformationEvent);
            } else if (contestInformationEvent.getAction() == ContestInformationEvent.Action.DELETED) {
                contestInformationListenerList.elementAt(i).contestInformationRemoved(contestInformationEvent);
            } else {
                contestInformationListenerList.elementAt(i).contestInformationChanged(contestInformationEvent);
            }
        }
    }

    
    public void addContestInformation(ContestInformation inContestInformation) {
        this.contestInformation = inContestInformation;
        ContestInformationEvent contestInformationEvent = new ContestInformationEvent(ContestInformationEvent.Action.ADDED, contestInformation);
        fireContestInformationListener(contestInformationEvent);
    }

    public void updateContestInformation(ContestInformation inContestInformation) {
        this.contestInformation = inContestInformation;
        ContestInformationEvent contestInformationEvent = new ContestInformationEvent(ContestInformationEvent.Action.CHANGED, contestInformation);
        fireContestInformationListener(contestInformationEvent);
    }

    public void addContestInformationListener(IContestInformationListener contestInformationListener) {
        contestInformationListenerList.addElement(contestInformationListener);
    }

    public void removeContestInformationListener(IContestInformationListener contestInformationListener) {
        contestInformationListenerList.remove(contestInformationListener);
    }

    public ContestInformation getContestInformation() {
        return contestInformation;
    }

    public void setJudgementList(Judgement[] judgements) {

        // Remove all judgements from display list and fire listeners
        for (Judgement judgement : judgementDisplayList.getList()) {
            JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.DELETED, judgement);
            fireJudgementListener(judgementEvent);
        }

        judgementDisplayList = new JudgementDisplayList();

        for (Judgement judgement : judgements) {
            Judgement judgementFromList = (Judgement) judgementList.get(judgement.getElementId());
            if (judgementFromList == null) {
                // if not in list add to list.
                judgementList.add(judgement);
            }
            judgementDisplayList.add(judgement);
            JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.ADDED, judgement);
            fireJudgementListener(judgementEvent);
        }
    }

    public void removeJudgement(Judgement judgement) {

        int idx = judgementDisplayList.indexOf(judgement);
        if (idx != -1){
            judgementDisplayList.remove(idx);
            JudgementEvent judgementEvent = new JudgementEvent(JudgementEvent.Action.DELETED, judgement);
            fireJudgementListener(judgementEvent);
        }
    }
    
}
