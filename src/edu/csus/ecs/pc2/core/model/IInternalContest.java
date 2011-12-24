package edu.csus.ecs.pc2.core.model;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.exception.ClarificationUnavailableException;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.exception.ProfileCloneException;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.exception.UnableToUncheckoutRunException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ProfileChangeStatus.Status;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.ISecurityMessageListener;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/** Specifies methods used to manipulate contest data.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IInternalContest {

    /**
     * 
     * @return contest password
     */
    String getContestPassword();

    /**
     * @param contestPassword
     */
    void setContestPassword(String contestPassword);

    void addLanguage(Language language);

    void deleteLanguage(Language language);

    void addProblem(Problem problem);
    
    void addReplaySetting (ReplaySetting replaySetting);
    
    /**
     * Add Notification into model from remote site.
     * 
     * Use {@link #acceptNotification(Notification)} to add {@link Notification}
     * to this site/server/model.
     * 
     * @see #acceptNotification(Notification)
     * 
     * @param notification
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    void addNotification(Notification notification) throws IOException, ClassNotFoundException, FileSecurityException;

    void addProblem(Problem problem, ProblemDataFiles problemDataFiles);

    void deleteProblem(Problem problem);
    
    void deleteReplaySetting(ReplaySetting replaySetting);
    
    void addCategory(Category category);
    
    void deleteCategory(Category category);

    void addContestTime(ContestTime contestTime);

    void addJudgement(Judgement judgement);

    void addGroup(Group group);

    /**
     * Replace judgement list.
     * 
     * @param judgementList
     */
    void setJudgementList(Judgement[] judgementList);

    void removeJudgement(Judgement judgement);

    void removeGroup(Group group);

    void addSite(Site site);

    void connectionEstablished(ConnectionHandlerID connectionHandlerID);

    void connectionEstablished(ConnectionHandlerID connectionHandlerID, Date connectDate);

    void connectionDropped(ConnectionHandlerID connectionHandlerID);

    void updateSite(Site site);
    
    /**
     * Update whether Site is running a particular profile or not.
     * 
     * @param site
     * @param inProfile
     * @param status 
     */
    void updateSiteStatus(Site site, Profile inProfile, Status status);

    void updateLanguage(Language language);

    void updateProblem(Problem problem);
    
    void updateReplaySetting(ReplaySetting replaySetting);

    
    /**
     * Update existing Notification.
     * 
     * Use {@link #acceptNotification(Notification)} to add Notification
     * into this server/model.
     * 
     * @see #acceptNotification(Notification)
     * 
     * @param notification
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    void updateNotification (Notification notification) throws IOException, ClassNotFoundException, FileSecurityException;
    
    void updateCategory(Category category);

    void updateProblem(Problem problem, ProblemDataFiles problemDataFiles);

    void updateBalloonSettings(BalloonSettings balloonSettings);

    ProblemDataFiles getProblemDataFile(Problem problem);

    ProblemDataFiles[] getProblemDataFiles();

    void updateContestTime(ContestTime contestTime, int inSiteNumber);

    void addAccount(Account account);

    void addBalloonSettings(BalloonSettings balloonSettings);

    /**
     * Update current contest time values.
     * 
     * @param contestTime
     */
    void updateContestTime(ContestTime contestTime);

    void updateJudgement(Judgement judgement);

    void updateGroup(Group group);

    void changeSite(Site site);

    void updateAccount(Account account);

    /**
     * Start InternalContest Clock at site.
     * 
     * @param siteNumber
     */
    void startContest(int siteNumber);

    /**
     * Stop InternalContest Clock at site.
     * 
     * @param siteNumber
     */
    void stopContest(int siteNumber);

    /**
     * Add a run into the contest data, return updated Submitted Run.
     * 
     * @param submittedRun
     * @return Submitted Run with id and elapsedtime
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    Run acceptRun(Run submittedRun, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException;
    
  
    /**
     * Add a run into the runList.
     * 
     * This just adds the run into the list, unlike acceptRun, this does not increment the run.
     * 
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void addRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Add a run from the server, a run ready to be judged.
     * 
     * @param run
     *            submitted run
     * @param runFiles
     *            submitted run files (like source files)
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void addRun(Run run, RunFiles runFiles, ClientId whoCheckedOutRunId) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Add/archive run and runfiles.
     * 
     * @param run
     * @param runFiles
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    void addRun(Run run, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException;


    /**
     * Add new accounts.
     * 
     * @see edu.csus.ecs.pc2.core.list.AccountList#generateNewAccounts(edu.csus.ecs.pc2.core.model.ClientType.Type, int, int, edu.csus.ecs.pc2.core.list.AccountList.PasswordType, int, boolean)
     * @param clientTypeName
     *            name of client type, "team", "judge", etc.
     * @param count
     *            number of accounts to add
     * @param active
     *            set to True if the accounts are active
     * @return Vector of Accounts created.
     */
    Vector<Account> generateNewAccounts(String clientTypeName, int count, boolean active);

    /**
     * Add new accounts, client number starting at startNumber.
     * 
     * @see edu.csus.ecs.pc2.core.list.AccountList#generateNewAccounts(edu.csus.ecs.pc2.core.model.ClientType.Type, int, int, edu.csus.ecs.pc2.core.list.AccountList.PasswordType, int, boolean)
     * @param clientTypeName
     * @param count
     * @param startNumber
     * @param active
     * @return Vector of Accounts created.
     */
    Vector<Account> generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active);

    /**
     * Add new sites.
     * 
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
     * Remove a balloonSettings listener.
     * 
     * @param balloonSettingsListener
     */
    void removeBalloonSettingsListener(IBalloonSettingsListener balloonSettingsListener);

    /**
     * Fetch all defined groups.
     * 
     * @return array of Group
     */
    Group[] getGroups();

    /**
     * Fetch all defined problems.
     * 
     * @return array of Problem
     */
    Problem[] getProblems();
    
    ReplaySetting[] getReplaySettings();
    
    Notification [] getNotifications();
    
    /**
     * Fetch all defined categories, includes non-hidden Problems too.
     * 
     * @return array of Category.
     */
    Category[] getCategories();

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
    
    void addNotificationListener (INotificationListener notificationListener);

    void removeClarificationListener(IClarificationListener clarificationListener);

    void addProblemListener(IProblemListener problemListener);
    
    void removeNotificationListener (INotificationListener notificationListener);

    void removeProblemListener(IProblemListener problemListener);
    
    void addCategoryListener(ICategoryListener categoryListener);

    void removeCategoryListener(ICategoryListener categoryListener);

    void addLanguageListener(ILanguageListener languageListener);

    void removeLanguageListener(ILanguageListener languageListener);

    void addChangePasswordListener(IChangePasswordListener changePasswordListener);

    void removeChangePasswordListener(IChangePasswordListener changePasswordListener);

    void addLoginListener(ILoginListener loginListener);

    void removeLoginListener(ILoginListener loginListener);

    void addContestTimeListener(IContestTimeListener contestTimeListener);

    void removeContestTimeListener(IContestTimeListener contestTimeListener);

    void addJudgementListener(IJudgementListener judgementListener);

    void removeJudgementListener(IJudgementListener judgementListener);

    void addMessageListener(IMessageListener messageListener);

    void removeMessageListener(IMessageListener messageListener);

    void addSiteListener(ISiteListener siteListener);

    void removeSiteListener(ISiteListener siteListener);

    void addConnectionListener(IConnectionListener connectionListener);

    void removeConnectionListener(IConnectionListener connectionListener);

    void addGroupListener(IGroupListener groupListener);

    void removeGroupListener(IGroupListener groupListener);

    void addProfileListener(IProfileListener profileListener);

    void removeProfileListener(IProfileListener profileListener);

    Run getRun(ElementId id);
    
    Account[] getAccounts();

    Vector<Account> getAccounts(Type type, int siteNumber);

    Vector<Account> getAccounts(Type type);

    BalloonSettings getBalloonSettings(int siteNumber);

    Site getSite(int siteNumber);

    /**
     * return true if account exists and valid password matches.
     * 
     * @param clientId
     * @param password
     * @return true if valid password for input clientId
     */
    boolean isValidLoginAndPassword(ClientId clientId, String password);

    /**
     * add any login from any site.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    void addLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID);

    /**
     * Add only as a local login.
     * 
     * This is for servers who are locally logged in.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    void addLocalLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID);

    /**
     * Add login as a remote login.
     * 
     * @param clientId
     * @param connectionHandlerID
     */
    void addRemoteLogin(ClientId clientId, ConnectionHandlerID connectionHandlerID);

    /**
     * Lookup a client id given a ConnectionHandlerID.
     * 
     * @param connectionHandlerID
     * @return ClientId or null if not found.
     */
    ClientId getLoginClientId(ConnectionHandlerID connectionHandlerID);

    /**
     * is local login (login to this server).
     * 
     * @param sourceId
     * @return true if logged in.
     */
    boolean isLocalLoggedIn(ClientId sourceId);

    /**
     * Return date when client logged in or null if not logged in.
     * 
     * Should use {@link #isLocalLoggedIn(ClientId)} not this method to check whether client logged in.
     * 
     * @param clientId
     * @return date client logged in
     */
    Date getLocalLoggedInDate(ClientId clientId);

    /**
     * Is logged into remote server.
     * 
     * @param clientId
     * @return true if client is logged into remote server
     */
    boolean isRemoteLoggedIn(ClientId clientId);

    /**
     * Has this module been logged in, loaded with data, authenticated.
     */
    boolean isLoggedIn();

    /**
     * Lookup ConnectionHandlerID for all sites.
     * 
     * @param clientId
     * @return ClientId or null if not found.
     */
    ConnectionHandlerID getConnectionHandleID(ClientId clientId);

    ClientId getClientId(ConnectionHandlerID connectionHandlerID);

    boolean isConnected(ConnectionHandlerID connectionHandlerID);

    boolean isConnectedToRemoteSite(ConnectionHandlerID connectionHandlerID);

    /**
     * Logoff, remove user from login list.
     * 
     * @param clientId
     */
    void removeLogin(ClientId clientId);

    /**
     * Remove client from remote login list
     * 
     * @param clientId
     */
    void removeRemoteLogin(ClientId clientId);

    /**
     * Get all connection ids for all sites.
     * 
     * @return all sites connection ids.
     */
    ConnectionHandlerID[] getConnectionHandlerIDs();

    int getSiteNumber();

    ContestTime getContestTime();

    ContestTime getContestTime(int siteNumber);

    void setClientId(ClientId clientId);

    void setSiteNumber(int number);

    /**
     * Is current module allowed to perform action based on their permissions?.
     * 
     * @param type
     * @return true if allowed, false if not.
     */
    boolean isAllowed(Permission.Type type);

    /**
     * Is client/module allowed to perform action based on their permissions?.
     * 
     * @param clientId
     * @param type
     * @return true if allowed, false if not.
     */
    boolean isAllowed(ClientId clientId, Permission.Type type);

    /**
     * Get all logins in contest.
     * 
     * @param type
     * @return array of all logged in clients
     */
    ClientId[] getAllLoggedInClients(Type type);

    /**
     * Get all locally logged in clients.
     * 
     * @param type
     * @return array of all local logged in clients
     */
    ClientId[] getLocalLoggedInClients(Type type);

    /**
     * Get clients logged into other servers.
     * 
     * @param type
     * @return array of all remote logged in clients
     */
    ClientId[] getRemoteLoggedInClients(Type type);

    void loginDenied(ClientId clientId, ConnectionHandlerID connectionHandlerID, String message);

    /**
     * Initialize with startup data
     */
    void initializeStartupData(int siteNumber);

    /**
     * Load all submissions off disk.
     * 
     */
    void initializeSubmissions(int siteNumber);

    /**
     * Fetch all runs.
     */
    Run[] getRuns();

    /**
     * Update run status.
     * 
     * @param run
     * @param judgementRecord
     * @param runResultFiles
     * @param whoUpdatedRun
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void runUpdated(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoUpdatedRun) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Update Run compiling/executing/vaidating status.
     * 
     * @param run
     * @param status
     * @param whoUpdatedRun
     */
    void updateRunStatus(Run run, RunExecutionStatus status, ClientId whoUpdatedRun);

    /**
     * Add a run not available, notify listeners.
     * 
     * @param run
     */
    void runNotAvailable(Run run);

    /**
     * Attempt to checkout run.
     * 
     * @param run
     * @param whoChangedRun
     * @throws RunUnavailableException
     *             - if run already checked out or not NEW.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    Run checkoutRun(Run run, ClientId whoChangedRun, boolean reCheckoutRun, boolean computerJudge) throws RunUnavailableException, IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Unconditionally update the run.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * 
     */
    void updateRun(Run run, ClientId whoChangedRun) throws IOException, ClassNotFoundException, FileSecurityException;

    void updateRun(Run run, RunFiles runFiles, ClientId whoChangedRun, RunResultFiles[] runResultFiles) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Get submitted files for input run.
     * 
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    RunFiles getRunFiles(Run run) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Get run result files for input run.
     * 
     * @param run
     * @param judgementRecord
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    RunResultFiles[] getRunResultFiles(Run run) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Add a run judgement.
     * 
     * @param run
     * @param judgementRecord
     * @param runResultFiles
     * @param judgeId
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void addRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId judgeId) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Cancel a checked out run.
     * 
     * @param run
     * @param fromId
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void cancelRunCheckOut(Run run, ClientId fromId) throws UnableToUncheckoutRunException, IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Returns which user has checked out the input run.
     * 
     * @param run
     * @return clientId, or null if not checked out.
     */
    ClientId getRunCheckedOutBy(Run run);

    /**
     * Returns which user has checked out the input run.
     * 
     * @param judgeID
     * @return Element id of runs.
     */
    ElementId[] getRunIdsCheckedOutBy(ClientId judgeID);

    /**
     * Available run, a canceled run.
     * 
     * @param run
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void availableRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException;

    Clarification getClarification(ElementId id);

    /**
     * Fetch all clarifications.
     */
    Clarification[] getClarifications();

    /**
     * add clarification into model.
     * 
     * @param clarification
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void addClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException;

    void addClarification(Clarification clarification, ClientId whoCheckedOutId) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * add new clarification onto server.
     * 
     * @param clarification
     * @return clarification with new id and timestamp.
     */
    Clarification acceptClarification(Clarification clarification);

    void answerClarification(Clarification clarification, String answer, ClientId whoAnsweredIt, boolean sendToAll);

    void updateClarification(Clarification clarification, ClientId whoChangedIt);

    /**
     * Add Notification to model, assign number and time.
     * 
     * Sets {@link Notification#setNumber(int)} and {@link Notification#setElapsedMS(long)}.
     * 
     * @param notification 
     * @return
     */
    Notification acceptNotification (Notification notification);
    
    /**
     * remove clarification from model.
     * 
     * @param clarification
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void removeClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * change/update clarification in model.
     * 
     * @param clarification
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    void changeClarification(Clarification clarification) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Attempt to checkout clarification.
     * 
     * @param clarification
     * @param whoChangedClar
     * @throws ClarificationUnavailableException
     *             - if clar already checked out or not NEW.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    Clarification checkoutClarification(Clarification clar, ClientId whoChangedClar) throws ClarificationUnavailableException, IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Add a clarification not available, notify listeners.
     * 
     * @param run
     */
    void clarificationNotAvailable(Clarification clar);

    Language getLanguage(ElementId elementId);

    Problem getProblem(ElementId elementId);
    
    Notification getNotification(ElementId elementId);
    
    Category getCategory (ElementId elementId);

    Judgement getJudgement(ElementId elementId);

    Account getAccount(ClientId id);

    Group getGroup(ElementId elementId);

    ContestTime[] getContestTimes();

    /**
     * Get both remote and local connection Ids.
     */
    ConnectionHandlerID[] getConnectionHandleIDs();

    ContestTime getContestTime(ElementId elementId);

    Clarification[] getClarifications(ClientId clientId);

    Run[] getRuns(ClientId clientId);

    void cancelClarificationCheckOut(Clarification clarification, ClientId whoCancelledIt) throws IOException, ClassNotFoundException, FileSecurityException;

    void addClientSettings(ClientSettings clientSettings);

    void updateClientSettings(ClientSettings clientSettings);

    void addClientSettingsListener(IClientSettingsListener clientSettingsListener);

    void removeClientSettingsListener(IClientSettingsListener clientSettingsListener);

    void addContestInformation(ContestInformation contestInformation);

    void updateContestInformation(ContestInformation contestInformation);
    
    void addContestInformationListener(IContestInformationListener contestInformationListener);

    void removeContestInformationListener(IContestInformationListener contestInformationListener);

    /**
     * Get contest info, like title.
     * 
     * @return the contest info
     */
    ContestInformation getContestInformation();

    /**
     * Get individual client settings
     * 
     * @return client settings
     */
    ClientSettings getClientSettings();

    ClientSettings getClientSettings(ClientId clientId);

    ClientSettings[] getClientSettingsList();

    /**
     * Maximum MSecs for each retry.
     * 
     * Used to calculate a "random" connection retry, when client disconnected.
     * 
     * @return milliseconds
     */
    int getMaxRetryMSecs();

    /**
     * Maximum number of connection retries before taking action.
     * 
     * Action might be putting a GUI in front of the user.
     * 
     * @return maximum unattended retry attempts
     */
    int getMaxConnectionRetries();

    /**
     * Get all Balloon Settings.
     * 
     * @return array of the Balloon Settings
     */
    BalloonSettings[] getBalloonSettings();

    BalloonSettings getBalloonSettings(ElementId elementId);

    void addBalloonSettingsListener(IBalloonSettingsListener implementation);

    void setGeneralProblem(Problem newGeneralProblem);

    Problem getGeneralProblem();

    void addAccounts(Account[] accounts);

    void updateAccounts(Account[] accounts);

    Log getSecurityAlertLog();

    void newSecurityMessage(ClientId sourceId, String string, String string2, ContestSecurityException contestSecurityException);

    void addSecurityMessageListener(ISecurityMessageListener securityMessageListener);

    void removeSecurityMessageListener(ISecurityMessageListener securityMessageListener);

    /**
     * Password change attempted.
     * 
     * @param success
     *            if true, password was changed
     * @param clientId
     *            which client's attempted to change their password
     * @param message
     *            a helpful message about why the password was or was not changed
     */
    void passwordChanged(boolean success, ClientId clientId, String message);

    /**
     * Clear data caches and fires REFRESH_ALL events.
     * 
     * Clears caches for clars and runs , removes all checked out status'.
     * Resets next run Id and next Clarification id.
     * 
     */
    void resetSubmissionData();
    
    /**
     * Clear all data except submissions.
     * 
     * clears accounts, languages, problems, etc.
     */
    void resetConfigurationData();

    /**
     * Send run compiling, executing and validating status.
     * 
     * @return
     */
    boolean isSendAdditionalRunStatusMessages();

    /**
     * Is this object from the current contest configuration ?.
     * 
     * Different Profiles have different contest identifiers, this method returns true if the contest info is from the same configuration.
     * 
     * @param object
     * @return
     */
    boolean contestIdMatches(String identifier);

    /**
     * return a Contest Identifier.
     * 
     * The Contest Identifier is stored in the Profile, so the {@link #setProfile(Profile)} method is used to update the Profile.
     * 
     * 
     * @return a unique identifier for this configuration/profile.
     */
    String getContestIdentifier();

    /**
     * Set the contest identifier
     * 
     * @param contestId
     */
    void setContestIdentifier(String contestId);

    /**
     * Set the Profile/configuration identifier for this contest instance.
     * 
     */
    void setProfile(Profile profile);

    /**
     * Get the Profile/configuration identifier for this contest instance.
     * 
     * @return
     */
    Profile getProfile();
    
    /**
     * Get saved Profile/configuration.
     * @param id
     * @return profile or null if not found
     */
    Profile getProfile(ElementId id);

    /**
     * Get list of profiles.
     * 
     * @return
     */
    Profile[] getProfiles();

    Profile addProfile(Profile profile);

    Profile updateProfile(Profile profile);

    void deleteProfile(Profile profile);

    boolean storeConfiguration(Log log) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Set the storage reader/writer.
     * 
     * @param storage
     */
    void setStorage(IStorage storage);
    
    /**
     * Get the Storage
     * 
     * @return
     */
    IStorage getStorage();

    /**
     * Read in configuration, and submissions, unchecks out submissions.
     * 
     * @param siteNumber
     * @param log
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    boolean readConfiguration(int siteNumber, Log log) throws IOException, ClassNotFoundException, FileSecurityException;
    
    /**
     * Clone a contest settings/submissions based on settings.
     * 
     * Does not switch profiles - just makes a copy of it based on the
     * current profile and the ProfileCloneSettings.
     * <P>
     * To switch profiles see/use {@link edu.csus.ecs.pc2.core.model.IInternalController#cloneProfile(Profile, ProfileCloneSettings, boolean)};
     * 
     * @param contest
     * @param newProfile 
     * @param settings
     * @return cloned contest
     * @throws ProfileCloneException
     */
    IInternalContest clone(IInternalContest contest, Profile newProfile, ProfileCloneSettings settings) throws ProfileCloneException;

    /**
     * Get settings used to clone/create this profile.
     * 
     * @return settings used to create/generate this profile or null
     */
    ProfileCloneSettings getProfileCloneSettings();
    
    /**
     * Store the profile clone settings that created the current Profile.
     * @param profileCloneSettings
     * @return
     */
    ProfileCloneSettings setProfileCloneSettings(ProfileCloneSettings profileCloneSettings);

    /**
     * clone/import Clarifications from input contest.
     * 
     * @param inputContest
     * @param newProfile
     * @throws ProfileCloneException
     */
    void cloneClarifications(IInternalContest inputContest, Profile newProfile) throws ProfileCloneException;

    /**
     * clone/import Run and Run Files from input contest.
     * 
     * @param inputContest
     * @param newProfile
     * @throws ProfileCloneException
     */
    void cloneRunsAndRunFiles(IInternalContest inputContest, Profile newProfile) throws ProfileCloneException;

    /**
     * Remove all listener instances from contest.
     */
    void removeAllListeners();

    /**
     * Send a refresh all event to all listeners.
     */
    void fireAllRefreshEvents();

    /**
     * Copied all current login and connection established information.
     * 
     * @param newContest contest to copy information to.
     */
    void cloneAllLoginAndConnections(IInternalContest newContest) throws CloneException;

    /**
     * Are RunFiles present?
     * 
     * @param run
     * @return true if RunFiles are available.
     */
    boolean isRunFilesPresent(Run run) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * A message has come from source to destination.
     * 
     * @param source
     * @param destination
     * @param message
     */
    void addMessage(MessageEvent.Area area, ClientId source, ClientId destination, String message);

    /**
     * Add or update Run files.
     * @param run
     * @param runFiles
     */
    void updateRunFiles(Run run, RunFiles runFiles) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * 
     * @return null if no data or FinalizeData 
     */
    FinalizeData getFinalizeData();

    /**
     * Set Contest FinalizeData.
     * @param data
     */
    void setFinalizeData(FinalizeData data);

    /**
     * get notification for team and problem.
     * 
     * @param submitter
     * @param problemId
     * @return null if no notification
     */
    Notification getNotification(ClientId submitter, ElementId problemId);

    /**
     * Sets up the default Clarification Categories,
     * if no categories are present.
     */
    void setupDefaultCategories();
    
}
