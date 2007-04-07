package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;

/**
 * Represents functions provided by modules comprising the contest engine.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface IController {

    void submitRun(Problem problem, Language language, String filename) throws Exception;

    void setSiteNumber(int i);

    void setContestTime(ContestTime contestTime);

    /**
     * Send to client, if necessary forward to another server.
     * @param confirmPacket
     */
    void sendToClient(Packet confirmPacket);

    void sendToServers(Packet packet);

    void sendToJudges(Packet packet);

    void sendToAdministrators(Packet packet);

    void sendToScoreboards(Packet packet);

    void sendToTeams(Packet packet);

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
     * @param run
     */
    void checkOutRun (Run run);
    
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

}
