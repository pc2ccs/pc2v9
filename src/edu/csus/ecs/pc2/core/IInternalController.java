package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * Provides the methods to start PC<sup>2</sup> clients and servers.
 * <P>
 * An example of starting a server:
 * 
 * public static void main(String[] args) {<br>
 * <br>
 * <blockquote>
 * IInternalContest contest = new InternalContest();<br>
 * IInternalController controller = new InternalController (contest);<br>
 * String serverArgs = "--server";
 * controller.start(serverArgs);<br>
 * </blockquote>
 * } <br>
 * <P>
 * 
 * To start a client: 
 * <code>
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
     * @param confirmPacket
     */
    void sendToClient(Packet confirmPacket);

    /**
     * Send to all logged in servers.
     * @param packet
     */
    void sendToServers(Packet packet);
    
    /**
     * Send to a remote server.
     * @param siteNumber
     * @param packet
     */
    void sendToRemoteServer (int siteNumber, Packet packet);

    /**
     * Send to all judges on local site.
     * @param packet
     */
    void sendToJudges(Packet packet);

    /**
     * Send to all administrators on local site.
     * @param packet
     */
    void sendToAdministrators(Packet packet);

    /**
     * Send to all scoreboard on local site.
     * @param packet
     */
    void sendToScoreboards(Packet packet);

    /**
     * Send to all teams on local site.
     * @param packet
     */
    void sendToTeams(Packet packet);

    /**
     * Start InternalController with command line arguments.
     * @param stringArray
     */
    void start(String[] stringArray);

    /**
     * Login to server, start MainUI.
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
    IInternalContest clientLogin(String loginName, String password) throws Exception;
    
    /**
     * Logoff a client.
     * 
     * Either a force logoff or the client themselves logs off.
     * @param clientId
     */
    void logoffUser(ClientId clientId);

    /**
     * Start the UI for the input client.
     * @param clientId
     */
    void startMainUI(ClientId clientId);
    
    /**
     * Request a run from the server.
     * @param run - the run to retrieve
     * @param readOnly - do not check out run just fetch the run.
     */
    void checkOutRun (Run run, boolean readOnly);
    
    
    /**
     * Request to checkout a judged run, to rejudge the run.
     * @param theRun
     */
    void checkOutRejudgeRun(Run theRun);
    
    /**
     * Submit judgement from run to judge.
     * @param run
     * @param judgementRecord
     */
    void submitRunJudgement (Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles);
    
    /**
     * Cancel selected run.
     * @param run
     */
    void cancelRun (Run run);
    
    void addNewSite (Site site);
    
    void addNewProblem(Problem problem, ProblemDataFiles problemDataFiles);

    void addProblem(Problem problem);

    /**
     * Add a new Judgement.
     * @param judgement
     */
    void addNewJudgement(Judgement judgement);
    
    /**
     * Replace judgement list with new judgement list.
     * @param judgementList
     */
    void setJudgementList (Judgement [] judgementList);

    void removeJudgement (Judgement judgement);

    void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles);

    void sendServerLoginRequest(int inSiteNumber) throws Exception;

    void updateSite(Site newSite);

    void updateProblem(Problem problem);
    
    void updateProblem(Problem problem, ProblemDataFiles problemDataFiles);
    
    ProblemDataFiles getProblemDataFiles (Problem problem);

    Log getLog();
    
    /**
     * Send message to server that needs attention/resolution.
     * 
     * @param event optional event
     * @param message message about the event/circumstances.
     * @param exception optional exception
     */
    void sendPriorityMessage(String event, String message, Exception exception);

    /**
     * Generate new accounts on a server.
     * 
     * @param clientTypeName
     * @param siteNumber site number to generate accounts.
     * @param count
     * @param startNumber
     * @param active
     */
    void generateNewAccounts(String clientTypeName, int siteNumber, int count, int startNumber, boolean active);
    
    /**
     * Generate new accounts for current site.
     * @param clientTypeName
     * @param count
     * @param startNumber
     * @param active
     */
    void generateNewAccounts(String clientTypeName, int count, int startNumber, boolean active);

    /**
     * Submit a clarification.
     * @param problem
     * @param question
     */
    void submitClarification(Problem problem, String question);
    
    /**
     * Request clarification to answer.
     * @param clarification
     * @param readOnly
     */
    void checkOutClarification (Clarification clarification, boolean readOnly);
    
    /**
     * Cancel requested clarification.
     * @param clarification
     */
    void cancelClarification (Clarification clarification);
    
    /**
     * Answer a clarification.
     * @param clarification
     */
    void submitClarificationAnswer (Clarification clarification);
    
    /**
     * Force connection off.
     * 
     * @param connectionHandlerID
     */
    void forceConnectionDrop(ConnectionHandlerID connectionHandlerID);
    
    void updateClientSettings (ClientSettings clientSettings);
    
    void updateContestInformation (ContestInformation contestInformation);
    
    void removeLogin (ClientId clientId);

    /**
     * Remove connection from connection list.
     */
    void removeConnection(ConnectionHandlerID connectionHandlerID);
    
    void shutdownTransport();
    
    void startContest (int inSiteNumber);
    
    void stopContest (int inSiteNumber);
    
    void startAllContestTimes();
    
    void stopAllContestTimes();

    void addNewLanguage(Language language);
    
    void updateLanguage(Language language);

    void addNewGroup(Group group);
    
    void updateGroup(Group group);

    void addNewAccount (Account account);
    
    void addNewAccounts (Account [] account);
    
    void updateAccount (Account account);
    
    void updateAccounts (Account [] account);
    
    void writeConfigToDisk ();

    void addNewBalloonSettings(BalloonSettings newBalloonSettings);

    void updateBalloonSettings(BalloonSettings newBalloonSettings);

    /**
     * Load contest settings from disk and initialize InternalContest. 
     */
    void initializeServer();

    void addNewClientSettings(ClientSettings newClientSettings);

    void updateContestTime(ContestTime newContestTime);

    /**
     * Get Security Level.
     * 
     * @return curent security level
     */
    int getSecurityLevel();
    
    void setSecurityLevel(int securityLevel);
    
}
