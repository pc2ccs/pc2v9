package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
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
 * IModel model = new Model();<br>
 * IController controller = new Controller (model);<br>
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
 *      IModel model = new Model();<br>
 *      IController controller = new Controller (model);<br>
 *      controller.start(args);<br>
 * } <br>
 * </blockquote>
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IController {

    /**
     * Submit a run to the server.
     * @param problem
     * @param language
     * @param filename
     * @throws Exception
     */
    void submitRun(Problem problem, Language language, String filename) throws Exception;

    void setSiteNumber(int i);

    void setContestTime(ContestTime contestTime);

    /**
     * Send to client, if necessary forward to another server.
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
     * Start Controller with command line arguments.
     * @param stringArray
     */
    void start(String[] stringArray);

    /**
     * Login to server.
     * @param loginName
     * @param password
     */
    void login(String loginName, String password);

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
    
    void updateRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles);

    void sendServerLoginRequest(int siteNumber);

    void updateSite(Site newSite);

    void startContest(int inSiteNumber);
    
    void stopContest(int inSiteNumber);
    
    void updateProblem(Problem problem);
    
    void updateProblem(Problem problem, ProblemDataFiles problemDataFiles);
    
    ProblemDataFiles getProblemDataFiles (Problem problem);

    Log getLog();

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
     * Force connection off.
     * 
     * @param connectionHandlerID
     */
    void forceConnectionDrop(ConnectionHandlerID connectionHandlerID);

    /**
     * Remove connection from connection list.
     */
    void removeConnection(ConnectionHandlerID connectionHandlerID);
    
    void shutdownTransport();
}
