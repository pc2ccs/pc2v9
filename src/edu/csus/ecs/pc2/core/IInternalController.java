// Copyright (C) 1989-2020 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.IOException;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IPacketListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.core.transport.ITransportManager;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.ILogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * Provides the methods to start PC<sup>2</sup> clients and servers.
 * <P>
 * An example of starting a server: 
 * <P>
 * <code>
 * public static void main(String[] args) {<br>
 * <blockquote> 
 *      IInternalContest contest = new InternalContest();<br>
 *      IInternalController controller = new InternalController (contest);<br>
 *      String serverArgs = "--server"; controller.start(serverArgs);<br>
 * </blockquote> 
 * } 
 * </code>
 * <P>
 * 
 * To start a client: 
 * <P>
 * <code>
 * public static void main(String[] args) {<br>
 * <blockquote>
 *      IInternalContest contest = new InternalContest();<br>
 *      IInternalController controller = new InternalController (contest);<br>
 *      controller.start(args);<br>
 * </blockquote>
 * } 
 * </code>
 * <P>
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IInternalController {

    /**
     * Submit a Judge Run to the server.
     * 
     * @param problem the {@link Problem} for which the Submission applies
     * @param language the {@link Language} used in the Submission
     * @param mainFileName the name of the file containing the main program source code
     * @param otherFiles an array of {@link SerializedFile}s containing additional source files being submitted with the Run
     * 
     * @throws Exception if an error occurs while attempting to send the Run to the Server
     */
    void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] otherFiles) throws Exception;

    /**
     * Submit a Judge Run to the server.
     * 
     * @param problem the {@link Problem} for which the Submission applies
     * @param language the {@link Language} used in the Submission
     * @param mainFileName the name of the file containing the main program source code
     * @param otherFiles an array of {@link SerializedFile}s containing additional source files being submitted with the Run
     * @param overrideSubmissionTimeMS a value which, if non-zero, is to be used as the submission time of the Judge Run; 
     *              only has effect when Contest Information "CCS Test Mode" is true
     * @param overrideRunId a value which, if non-zero, is to be used as the RunId for the submission instead of any
     *              internally-assigned RunId; only has effect when Contest Information "CCS Test Mode" is true
     *              
     * @throws Exception if an error occurs while attempting to send the Run to the Server
     */
    void submitJudgeRun(Problem problem, Language language, String mainFileName, SerializedFile[] otherFiles, 
                        long overrideSubmissionTimeMS, long overrideRunId) throws Exception;

    /**
     * Submit a Judge Run to the server.
     * 
     * @param problem the {@link Problem} for which the Submission applies
     * @param language the {@link Language} used in the Submission
     * @param mainFile a {@link SerializedFile} containing the main program source code
     * @param otherFiles an array of {@link SerializedFile}s containing additional source files being submitted with the Run
     * @param overrideSubmissionTimeMS a value which, if non-zero, is to be used as the submission time of the Judge Run; 
     *              only has effect when Contest Information "CCS Test Mode" is true
     * @param overrideRunId a value which, if non-zero, is to be used as the RunId for the submission instead of any
     *              internally-assigned RunId; only has effect when Contest Information "CCS Test Mode" is true
     *              
     * @throws Exception if an error occurs while attempting to send the Run to the Server
     */
    void submitJudgeRun(Problem problem, Language language, SerializedFile mainFile, SerializedFile[] otherFiles, 
                        long overrideSubmissionTimeMS, long overrideRunId) throws Exception;


    void setSiteNumber(int i);

    void setContestTime(ContestTime contestTime);

    /**
     * Send to client (or server), if necessary forward to another server.
     * 
     * @param packet
     */
    void sendToClient(Packet packet);

    /**
     * Send to all logged in servers.
     * 
     * @param packet
     */
    void sendToServers(Packet packet);

    /**
     * Send to a remote server.
     * 
     * @param siteNumber
     * @param packet
     */
    void sendToRemoteServer(int siteNumber, Packet packet);

    /**
     * Send to all judges on local site.
     * 
     * @param packet
     */
    void sendToJudges(Packet packet);

    /**
     * Send to all administrators on local site.
     * 
     * @param packet
     */
    void sendToAdministrators(Packet packet);

    /**
     * Send to all scoreboard on local site.
     * 
     * @param packet
     */
    void sendToScoreboards(Packet packet);

    /**
     * Send to all teams on local site.
     * 
     * @param packet
     */
    void sendToTeams(Packet packet);

    /**
     * Send to all spectator/API clients
     * 
     * @param packet
     */
    void sendToSpectators(Packet packet);

    /**
     * Start InternalController with command line arguments.
     * 
     * @param stringArray
     */
    void start(String[] stringArray);

    /**
     * Login to server, start MainUI.
     * 
     * @param loginName
     * @param password
     */
    void login(String loginName, String password);

    /**
     * Login to server, wait for login
     * 
     * @param loginName
     * @param password
     * @return
     * @throws Exception
     */
    IInternalContest clientLogin(IInternalContest internalContest, String loginName, String password) throws Exception;

    /**
     * Logoff a client.
     * 
     * Logs this client off, or sends request to log client off.
     * 
     * @param clientId
     */
    void logoffUser(ClientId clientId);

    /**
     * Start the UI for the input client.
     * 
     * @param clientId
     */
    void startMainUI(ClientId clientId);

    /**
     * Request a run from the server.
     * 
     * @param run
     *            - the run to retrieve
     * @param readOnly
     *            - do not check out run just fetch the run.
     * @param computerJudge
     *            - is this a computer judger
     */
    void checkOutRun(Run run, boolean readOnly, boolean computerJudge);

    /**
     * Request to checkout a judged run, to rejudge the run.
     * 
     * @param theRun
     */
    void checkOutRejudgeRun(Run theRun);

    /**
     * Submit judgement from run to judge.
     * 
     * @param run
     * @param judgementRecord
     */
    void submitRunJudgement(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles);

    /**
     * Cancel selected run.
     * 
     * @param run
     */
    void cancelRun(Run run);

    void addNewSite(Site site);

    void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles);
    
    void addNewProblem(Problem [] problem, ProblemDataFiles [] problemDataFiles);

    void addProblem(Problem problem);

    /**
     * Add a new Judgement.
     * 
     * @param judgement
     */
    void addNewJudgement(Judgement judgement);

    /**
     * Replace judgement list with new judgement list.
     * 
     * @param judgementList
     */
    void setJudgementList(Judgement[] judgementList);

    void removeJudgement(Judgement judgement);

    void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles);

    void sendServerLoginRequest(int inSiteNumber) throws Exception;
    
    /**
     * Is this controller using GUI. 
     * @return true if using GUI, false if using text only
     */
    boolean isUsingGUI();
    
    /**
     * Is this controller suppressing display of Connections grids.
     * @return true if GUIs using this controller should suppress display of Connections grids.
     */
    boolean isSuppressConnectionsPaneDisplay();

    /**
     * Is this controller suppressing display of Logins grids.
     * @return true if GUIs using this controller should suppress display of Logins grids.
     */
    boolean isSuppressLoginsPaneDisplay();

    /**
     * Update site in contest, send site update packet to other servers and admins.
     * @param newSite updated site info.
     */
    void updateSite(Site newSite);

    void updateProblem(Problem problem);

    void updateProblem(Problem problem, ProblemDataFiles problemDataFiles);
    
    /**
     * Send packet to local server to switch profile.
     * 
     * @param currentProfile profile to switch from
     * @param switchToProfile profile to switch to
     */
    void switchProfile (Profile currentProfile, Profile switchToProfile, String contestPassword);

    /**
     * Clone profile (and potentially switch to the new profile).
     * 
     * @param profile current profile
     * @param settings set of changes to clone
     * @param switchNow true means switch to new profile now
     */
    void cloneProfile (Profile profile, ProfileCloneSettings settings, boolean switchNow);

    ProblemDataFiles getProblemDataFiles(Problem problem);

    /**
     * Get contest log.
     * 
     * @return
     */
    Log getLog();

    /**
     * Send message to server that needs attention/resolution.
     * 
     * @param event
     *            optional event
     * @param message
     *            message about the event/circumstances.
     * @param contestSecurityException
     *            optional exception
     */
    void sendSecurityMessage(String event, String message, ContestSecurityException contestSecurityException);

    /**
     * Generate new accounts on a server.
     * 
     * @param clientTypeName
     * @param siteNumber
     *            site number to generate accounts.
     * @param count
     * @param startNumber
     * @param active
     */
    void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active);

    /**
     * Generate new accounts for current site.
     * 
     * @param clientTypeName
     * @param count
     * @param startNumber
     * @param active
     */
    void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active);

    /**
     * Submit a clarification.
     * 
     * @param problem
     * @param question
     */
    void submitClarification(Problem problem, String question);

    /**
     * Request clarification to answer.
     * 
     * @param clarification
     * @param readOnly
     */
    void checkOutClarification(Clarification clarification, boolean readOnly);

    /**
     * Cancel requested clarification.
     * 
     * @param clarification
     */
    void cancelClarification(Clarification clarification);

    /**
     * Answer a clarification.
     * 
     * @param clarification
     */
    void submitClarificationAnswer(Clarification clarification);

    /**
     * Force connection off.
     * 
     * Remove local connection, or send to server to remove connection.
     * 
     * @param connectionHandlerID
     */
    void forceConnectionDrop(ConnectionHandlerID connectionHandlerID);

    void updateClientSettings(ClientSettings clientSettings);

    void updateContestInformation(ContestInformation contestInformation);

    void removeLogin(ClientId clientId);

    void requestChangePassword(String oldPassword, String newPassword);

    /**
     * Remove connection from connection list.
     */
    void removeConnection(ConnectionHandlerID connectionHandlerID);

    void shutdownTransport();

    void startContest(int inSiteNumber);

    void stopContest(int inSiteNumber);

    void startAllContestTimes();

    void stopAllContestTimes();

    void addNewLanguage(Language language);

    void addNewLanguages(Language[] languages);
    
    void updateLanguage(Language language);

    void updateLanguages(Language[] languages);

    void addNewGroup(Group group);
    
    void addNewGroups(Group[] groups);

    void updateGroup(Group group);

    void updateGroups(Group[] groups);

    void addNewAccount(Account account);

    void addNewAccounts(Account[] account);

    void updateAccount(Account account);

    void updateAccounts(Account[] account);

    void addNewBalloonSettings(BalloonSettings newBalloonSettings);

    void updateBalloonSettings(BalloonSettings newBalloonSettings);

    /**
     * Load contest settings from disk and initialize InternalContest.
     * @param contest 
     * 
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    void initializeServer(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException;
    
    void initializeStorage (IStorage storage);

    void addNewClientSettings(ClientSettings newClientSettings);

    void updateContestTime(ContestTime newContestTime);

    /**
     * Get Security Level.
     * 
     * @return current security level
     */
    int getSecurityLevel();

    void setSecurityLevel(int securityLevel);

    /**
     * Send packet to server.
     * 
     * Also can be used to send a packet from  this
     * server to this server as a consistent interface
     * to the server (esp from classes like ServerView)
     * 
     * @param packet
     */
    void sendToLocalServer(Packet packet);

    /**
     * Get name of host contacted.
     * 
     * On server, gets name of host where listener listens.
     * 
     * @return name of host server
     */
    String getHostContacted();

    /**
     * Get port number of host contacted.
     * 
     * On server, gets port where listening.
     * 
     * @return
     */
    int getPortContacted();

    /**
     * Gets a run from the server.
     * 
     * Does not checkout run, simply requests the run info. Security note: only a non-team client can request runs.
     * 
     * @param run
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    void fetchRun(Run run) throws IOException, ClassNotFoundException, FileSecurityException;

    /**
     * Send a compiling message to the Server.
     * 
     * @param run
     */
    void sendCompilingMessage(Run run);

    /**
     * Send a executing message to the Server.
     * 
     * @param run
     */
    void sendExecutingMessage(Run run);

    /**
     * Send a validating message to the Server.
     * 
     * @param run
     */
    void sendValidatingMessage(Run run);

    boolean isClientAutoShutdown();

    void setClientAutoShutdown(boolean clientAutoShutdown);

    /**
     * Reset contest.
     * 
     * @param clientResettingContest
     */
    void resetContest(ClientId clientResettingContest, boolean eraseProblems, boolean eraseLanguages);

    /**
     * Update existing judgement.
     * 
     * @param newJudgement
     */
    void updateJudgement(Judgement newJudgement);

    /**
     * Update current Profile information.
     * 
     * @param profile
     */
    void updateProfile(Profile profile);

    void setContest(IInternalContest newContest);

    /**
     * Register a plugin.
     * 
     * @param plugin
     */
    void register(UIPlugin plugin);
    
    /**
     * Get list of plugins.
     * 
     * @return
     */
    UIPlugin[] getPluginList();

    /**
     * Update/replace contest and controller for all registered UI Plugins.
     * 
     * @param inContest
     * @param inController
     */
    void updateContestController(IInternalContest inContest, IInternalController inController);

    void addPacketListener(IPacketListener packetListener);

    void removePacketListener(IPacketListener packetListener);
    
    void incomingPacket (Packet packet);
    
    void outgoingPacket (Packet packet);
    
    /**
     * Start Log Window.
     * 
     * Only starts if {@link #isUsingGUI()} returns true;
     * 
     * @param contest
     */
    ILogWindow startLogWindow(IInternalContest contest);

    /**
     * Show log Window.
     * @param showWindow set LogWindow visible.
     */
    void showLogWindow(boolean showWindow);
    
    boolean isLogWindowVisible();

    /**
     * Log an exception.
     * @param string
     * @param e
     */
    void logWarning(String string, Exception e);

    /**
     * Send Sync Submissions packet.
     * 
     * Send a packet to tell all servers to sync up their
     * submission and other local data with all servers.
     * 
     * @param profile
     */
    void syncProfileSubmissions(Profile profile);

    /**
     * Send shutdown all servers packet.
     */
    void sendShutdownAllSites();

    /**
     * Send shutdown server packet.
     * 
     * @param siteNumber site number to shut down.
     */
    void sendShutdownSite(int siteNumber);

    /**
     * Shutdown this server.
     * 
     * @param requestor
     */
    void shutdownServer(ClientId requestor);

    /**
     * Shutdown all remote servers.
     * 
     * Sends packet to all servers to shutdown.
     * 
     * @param requestor
     */
    void shutdownRemoteServers(ClientId requestor);

    /**
     * Shutdown remove server (Server).
     * 
     * @param requestor
     * @param siteNumber
     */
    void shutdownServer(ClientId requestor, int siteNumber);

    /**
     * Update Finalize data
     * @param data 
     */
    void updateFinalizeData(FinalizeData data);
    
    /**
     * Using GUI?.
     * 
     * @param usingGUI true means show GUI message, false means do not show GUI messages.
     */
    void setUsingGUI(boolean usingGUI);
    
    void updateCategories(Category[] categories);

    void updateCategory(Category newCategory);

    void addNewCategory(Category newCategory);

    void startPlayback(PlaybackInfo playbackInfo);
        
    /**
     * Send submitted run to Run Submission Interface.
     * @param run
     * @param runFiles
     */
    void sendRunToSubmissionInterface (Run run, RunFiles runFiles);

    /**
     * Send auto registration request.
     */
    void autoRegister(String teamInformation);

    /**
     * Send to all logged in Judges, Admins, Boards and optionally to other sites.
     * 
     * This sends all sorts of packets to all logged in clients (except teams). 
     * Typically sendToServers is set if this is the originating site, if not done then a nasty circular path will occur.
     * 
     * @param packet
     * @param sendToServers if true then send to other server.
     */
    void sendToJudgesAndOthers(Packet packet, boolean sendToServers);

    /**
     * Override the connection manager.
     * 
     * @see ITransportManager
     * @param connectionManager
     */
    void setConnectionManager(ITransportManager connectionManager);
    
    /**
     * Creates an {@link AutoStarter} if none exists, and then instructs the AutoStarter to update its Scheduled Start Task to correspond to the Scheduled Start Time information in the
     * {@link ContestInformation} object in the received {@link IInternalContest}.
     * 
     * @param aContest
     *            - the Contest (Model) containing the Scheduled Start Time information
     * @param aController
     *            - the Controller to which this request applies
     */
    void updateAutoStartInformation(IInternalContest aContest, IInternalController aController) ;

    /**
     * Get contest model.
     * @return
     */
    IInternalContest getContest();

    /**
     * Submit a run to the server for a different client.
     * 
     * @param submitter - override submitter, if used the logged in client must have  Permission.Type.SHADOW_PROXY_TEAM selected.
     * @param problem
     * @param language
     * @param mainSubmissionFile
     * @param additionalFiles
     * @param overrideTimeMS
     * @param overrideRunId
     */
    void submitRun(ClientId submitter, Problem problem, Language language, SerializedFile mainSubmissionFile, SerializedFile[] additionalFiles, long overrideTimeMS, long overrideRunId);

    /**
     * Submit a run to the server for a different client with entry_point
     * 
     * @param submitter - override submitter, if used the logged in client must have  Permission.Type.SHADOW_PROXY_TEAM selected.
     * @param problem
     * @param language
     * @param entry_point Java/Kotlin main class entry point
     * @param mainSubmissionFile
     * @param additionalFiles
     * @param overrideTimeMS
     * @param overrideRunId
     */
    void submitRun(ClientId submitter, Problem problem, Language language, String entry_point, SerializedFile mainSubmissionFile, SerializedFile[] additionalFiles, long overrideTimeMS, long overrideRunId);
}
