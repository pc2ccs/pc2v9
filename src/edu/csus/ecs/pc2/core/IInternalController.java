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
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * Provides the methods to start PC<sup>2</sup> clients and servers.
 * <P>
 * An example of starting a server:
 * 
 * public static void main(String[] args) {<br>
 * <br>
 * <blockquote> IInternalContest contest = new InternalContest();<br>
 * IInternalController controller = new InternalController (contest);<br>
 * String serverArgs = "--server"; controller.start(serverArgs);<br>
 * </blockquote> } <br>
 * <P>
 * 
 * To start a client: <code>
 * public static void main(String[] args) {<br>
 * <blockquote>
 *      <br>
 *      IInternalContest contest = new InternalContest();<br>
 *      IInternalController controller = new InternalController (contest);<br>
 *      controller.start(args);<br>
 * } <br>
 * </blockquote>
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IInternalController {

    /**
     * Submit a run to the server.
     * 
     * @param problem
     * @param language
     * @param filename
     * @throws Exception
     */
    void submitRun(Problem problem, Language language, String filename, SerializedFile[] otherFiles) throws Exception;

    void setSiteNumber(int i);

    void setContestTime(ContestTime contestTime);

    /**
     * Send to client (or server), if necessary forward to another server.
     * 
     * @param confirmPacket
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

    void updateLanguage(Language language);

    void addNewGroup(Group group);

    void updateGroup(Group group);

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
     * @param contest
     * @param packetHandler
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
    LogWindow startLogWindow(IInternalContest contest);

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
     * @param clientRequestingShutdown
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

}
