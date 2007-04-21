package edu.csus.ecs.pc2.core.model;

import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Specifies methods used to manipulate contest data.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IModel {

    void addLanguage(Language language);

    void addProblem(Problem problem);

    void addContestTime(ContestTime contestTime);

    void addJudgement(Judgement judgement);

    void addSite(Site site);
    
    void connectionEstablished(ConnectionHandlerID connectionHandlerID) ;

    void connectionDropped(ConnectionHandlerID connectionHandlerID) ;

    /**
     * Update the run information.
     * 
     * To add a judgement, use {@link #addRunJudgement(Run, JudgementRecord, RunResultFiles, ClientId)}
     * 
     * @param run
     */
    void updateRun(Run run);
    
    void updateSite (Site site);

    void updateLanguage(Language language);

    void updateProblem(Problem problem);

    void updateContestTime(ContestTime contestTime, int inSiteNumber);
    
    void addAccount(Account account);

    /**
     * Update current contest time values.
     * 
     * @param contestTime
     */
    void updateContestTime(ContestTime contestTime);

    void updateJudgement(Judgement judgement);

    void changeSite(Site site);

    void updateAccount(Account account);

    /**
     * Start Contest Clock at site.
     * 
     * @param siteNumber
     */
    void startContest(int siteNumber);

    /**
     * Stop Contest Clock at site.
     * 
     * @param siteNumber
     */
    void stopContest(int siteNumber);

    /**
     * Add a run into the contest data, return updated Submitted Run.
     * 
     * @param submittedRun
     * @return Submitted Run with id and elapsedtime
     */
    Run acceptRun(Run submittedRun, RunFiles runFiles);

    /**
     * Add a run into the runList.
     * 
     * This just adds the run into the list, unlike acceptRun, this does not increment the run.
     * 
     * @param run
     */
    void addRun(Run run);

    /**
     * Add a run from the server, a run ready to be judged.
     * 
     * @param run
     *            submitted run
     * @param runFiles
     *            submitted run files (like source files)
     */
    void addRun(Run run, RunFiles runFiles, ClientId whoCheckedOutRunId);

    /**
     * Add new accounts.
     * 
     * @see AccountList#generateNewAccounts(Type, int, int, PasswordType, int, boolean)
     * @param clientTypeName
     *            name of client type, "team", "judge", etc.
     * @param count
     *            number of accounts to add
     * @param active
     *            set to True if the accounts are active
     *            
     */
    void generateNewAccounts(String clientTypeName, int count, boolean active);

    /**
     * Add new accounts, client number starting at startNumber.
     * @param clientTypeName
     * @param count
     * @param startNumber
     * @param active
     */
    void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active);

    /**
     * Add new sites.
     * 
     * @see AccountList#generateNewAccounts(Type, int, int, PasswordType, int, boolean)
     * @param count
     * @param active
     */
    void generateNewSites(int count, boolean active);

    /**
     * Add a new account listener.
     * 
     * @param accountListener
     */
    void addAccountListener(IAccountListener accountListener);

    /**
     * Remove a account listener.
     * 
     * @param accountListener
     */
    void removeAccountListener(IAccountListener accountListener);

    /**
     * Fetch all defined problems.
     * 
     * @return array of Problem
     */
    Problem[] getProblems();

    /**
     * Fetch all defined judgements.
     */
    Judgement[] getJudgements();

    /**
     * Fetch all defined sites.
     */
    Site[] getSites();

    /**
     * Fetch all defined languages.
     * 
     * @return array of Language
     */
    Language[] getLanguages();

    /**
     * get title for this logged in client.
     */
    String getTitle();

    /**
     * @return ClientId for the logged in client.
     */
    ClientId getClientId();

    void addRunListener(IRunListener runListener);

    void removeRunListener(IRunListener runListener);
    
    void addClarificationListener(IClarificationListener clarificationListener);

    void removeClarificationListener(IClarificationListener clarificationListener);

    void addProblemListener(IProblemListener problemListener);

    void removeProblemListener(IProblemListener problemListener);

    void addLanguageListener(ILanguageListener languageListener);

    void removeLanguageListener(ILanguageListener languageListener);

    void addLoginListener(ILoginListener loginListener);

    void removeLoginListener(ILoginListener loginListener);

    void addContestTimeListener(IContestTimeListener contestTimeListener);

    void removeContestTimeListener(IContestTimeListener contestTimeListener);

    void addJudgementListener(IJudgementListener judgementListener);

    void removeJudgementListener(IJudgementListener judgementListener);

    void addSiteListener(ISiteListener siteListener);

    void removeSiteListener(ISiteListener siteListener);
    
    void addConnectionListener(IConnectionListener connectionListener) ;

    void removeConnectionListener(IConnectionListener connectionListener);


    Run getRun(ElementId id);

    Vector<Account> getAccounts(Type type, int siteNumber);

    Vector<Account> getAccounts(Type type);
    
    Site getSite (int siteNumber);

    /**
     * return true if account exists and valid password matches.
     * 
     * @param clientId
     * @param password
     * @return true if valid password for input clientId
     */
    boolean isValidLoginAndPassword(ClientId clientId, String password);

    /**
     * add a already validated login and connection to list.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    void addLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID);

    /**
     * Lookup a client id given a ConnectionHandlerID.
     * 
     * @param connectionHandlerID
     * @return ClientId or null if not found.
     */
    ClientId getLoginClientId(ConnectionHandlerID connectionHandlerID);

    /**
     * 
     * @param sourceId
     * @return true if logged in.
     */
    boolean isLoggedIn(ClientId sourceId);

    /**
     * Has this module been logged in, loaded with data, authenticated.
     */
    boolean isLoggedIn();

    /**
     * Lookup ConnectionHandlerID for a given ClientId.
     * 
     * @param clientId
     * @return ClientId or null if not found.
     */
    ConnectionHandlerID getConnectionHandleID(ClientId clientId);

    /**
     * Logoff, remove user from login list.
     * 
     * @param clientId
     */
    void removeLogin(ClientId clientId);

    int getSiteNumber();

    ContestTime getContestTime();

    ContestTime getContestTime(int siteNumber);

    void setClientId(ClientId clientId);

    void setSiteNumber(int number);

    Enumeration<ClientId> getLoggedInClients(Type type);

    void loginDenied(ClientId clientId, ConnectionHandlerID connectionHandlerID, String message);

    void initializeWithFakeData();

    /**
     * Fetch all runs.
     */
    Run[] getRuns();

    void runUpdated(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoUpdatedRun);

    /**
     * Add a run not available, notify listeners.
     * 
     * @param run
     */
    void runNotAvailable(Run run);

    /**
     * Update the run.
     * 
     * Check out run or just update state. 
     * 
     * @param run
     * @param newState
     * @param whoChangedRun
     */
    void updateRun(Run run, RunStates newState, ClientId whoChangedRun);

    /**
     * Get submitted files for input run.
     * 
     * @param run
     */
    RunFiles getRunFiles(Run run);

    /**
     * Add a run judgement.
     * 
     * @param run
     * @param judgementRecord
     * @param runResultFiles
     * @param judgeId
     */
    void addRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId judgeId);

    /**
     * Cancel a checked out run.
     * 
     * @param run
     * @param fromId
     */
    void cancelRunCheckOut(Run run, ClientId fromId);

    /**
     * Returns which user has checked out the input run.
     * 
     * @param run
     * @return clientId, or null if not checked out.
     */
    ClientId getRunCheckedOutBy(Run run);

    /**
     * Available run, a canceled run.
     * 
     * @param run
     */
    void availableRun(Run run);
    
    /**
     * Fetch all clarifications.
     */
    Clarification [] getClarifications();
    
    /**
     * add clarification into model.
     * @param clarification
     */
    void addClarification(Clarification clarification);
    
    /**
     * add new clarification onto server. 
     * 
     * @param clarification
     * @return clarification with new id and timestamp.
     */
    Clarification acceptClarification (Clarification clarification);

    /**
     * remove clarification from model.
     * @param clarification
     */
    void removeClarification(Clarification clarification);

    /**
     * change/update clarification in model.
     * @param clarification
     */
    void changeClarification(Clarification clarification);

    Language getLanguage(ElementId elementId);

    Problem getProblem(ElementId elementId);

    Judgement getJudgement(ElementId elementId);

    Account getAccount(ClientId id);

    ContestTime[] getContestTimes();

    ConnectionHandlerID[] getConnectionHandleIDs();

    ContestTime getContestTime(ElementId elementId);


}
