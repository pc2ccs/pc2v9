package edu.csus.ecs.pc2.core;

import java.util.Vector;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.ISubmission;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.ProblemDataFilesList;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Process all incoming packets.
 * 
 * Process packets. In {@link #handlePacket(IController, IContest, Packet, ConnectionHandlerID) handlePacket} a packet is unpacked, contest is updated, and controller used to send packets as needed.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class PacketHandler {

    private IContest contest;

    private IController controller;
    
    public PacketHandler(IController controller, IContest contest) {
        this.controller = controller;
        this.contest = contest;
    }

    /**
     * Take each input packet, update the contest, send out packets as needed.
     * 
     * @param packet
     * @param connectionHandlerID
     */
    public void handlePacket(Packet packet, ConnectionHandlerID connectionHandlerID) {

        Type packetType = packet.getType();

        info("handlePacket start " + packet);
        PacketFactory.dumpPacket(System.err, packet); System.err.flush();

        ClientId fromId = packet.getSourceId();

        if (packetType.equals(Type.MESSAGE)) {
            PacketFactory.dumpPacket(System.err, packet);

            if (isThisSite(packet.getDestinationId().getSiteNumber())) {
                if (!packet.getDestinationId().getClientType().equals(ClientType.Type.SERVER)) {
                    controller.sendToClient(packet);
                }
            } else {
                String message = (String) PacketFactory.getObjectValue(packet, PacketFactory.MESSAGE_STRING);
                Packet messagePacket = PacketFactory.createMessage(contest.getClientId(), packet.getDestinationId(), message);
                int siteNumber = packet.getDestinationId().getSiteNumber();
                controller.sendToRemoteServer(siteNumber, messagePacket);
            }
        } else if (packetType.equals(Type.RUN_SUBMISSION_CONFIRM)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            contest.addRun(run);
            if (isServer()){
                sendToJudgesAndOthers(packet, isThisSite(run));
            }

        } else if (packetType.equals(Type.RUN_SUBMISSION)) {
            // RUN submitted by team to server

            Run submittedRun = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            Run run = contest.acceptRun(submittedRun, runFiles);

            // Send to team
            Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(contest.getClientId(), fromId, run);
            controller.sendToClient(confirmPacket);

            // Send to clients and servers
            sendToJudgesAndOthers( confirmPacket, true);
            
        } else if (packetType.equals(Type.CLARIFICATION_SUBMISSION)) {
            // Clarification submitted by team to server
            
            Clarification submittedClarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            Clarification clarification = contest.acceptClarification(submittedClarification);
            
            // Send to team
            Packet confirmPacket = PacketFactory.createClarSubmissionConfirm(contest.getClientId(), fromId, clarification);
            controller.sendToClient(confirmPacket);
            
            // Send to clients and other servers
            sendToJudgesAndOthers(confirmPacket, true);

        } else if (packetType.equals(Type.CLARIFICATION_SUBMISSION_CONFIRM)) {
            Clarification clarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            contest.addClarification(clarification);
            sendToJudgesAndOthers( packet, isThisSite(clarification));
            
        } else if (packetType.equals(Type.LOGIN_FAILED)) {
            String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
            contest.loginDenied(packet.getDestinationId(), connectionHandlerID, message);

        } else if (packetType.equals(Type.RUN_NOTAVAILABLE)) {
            // Run not available from server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            contest.runNotAvailable(run);

            sendToJudgesAndOthers( packet, isThisSite(run));

        } else if (packetType.equals(Type.FORCE_DISCONNECTION)) {
            sendForceDisconnection (packet);

        } else if (packetType.equals(Type.ESTABLISHED_CONNECTION)) {
            ConnectionHandlerID inConnectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);

            if (isServer()) {
                controller.sendToAdministrators(packet);
                if (isThisSite(packet.getSourceId())){
                    controller.sendToServers(packet);
                }
            } else {
                contest.connectionEstablished(inConnectionHandlerID);
            }

        } else if (packetType.equals(Type.DROPPED_CONNECTION)) {
            ConnectionHandlerID inConnectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);
            if (isServer()) {
                controller.sendToServers(packet);
                if (isThisSite(packet.getSourceId())){
                    controller.sendToServers(packet);
                }
            } else {
                contest.connectionEstablished(inConnectionHandlerID);
            }

        } else if (packetType.equals(Type.RUN_AVAILABLE)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            contest.availableRun(run);

            sendToJudgesAndOthers( packet, isThisSite(run));

        } else if (packetType.equals(Type.RUN_JUDGEMENT)) {
            // Judgement from judge to server
            // TODO security code insure that this judge/admin can make this change
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);
            RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
            ClientId whoJudgedRunId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            judgeRun(run,  judgementRecord, runResultFiles, whoJudgedRunId);

        } else if (packetType.equals(Type.RUN_JUDGEMENT_UPDATE)) {
            sendJudgementUpdate(packet);
            
        } else if (packetType.equals(Type.RUN_UPDATE)) {
            updateRun (packet);

        } else if (packetType.equals(Type.RUN_UPDATE_NOTIFICATION)) {
            sendRunUpdateNotification(packet);
            
        } else if (packetType.equals(Type.RUN_UNCHECKOUT)) {
            // Cancel run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId whoCanceledId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            cancelRun(packet, run,  whoCanceledId);

        } else if (packetType.equals(Type.START_CONTEST_CLOCK)) {
            // Admin to server, start the clock
            startContest(packet);
            
        } else if (packetType.equals(Type.STOP_CONTEST_CLOCK)) {
            // Admin to server, stop the cloeck
            stopContest(packet);

        } else if (packetType.equals(Type.UPDATE_CONTEST_CLOCK)) {
            // Admin to server, stop the cloeck
            updateContestClock(packet);
            
        } else if (packetType.equals(Type.CLOCK_STARTED)) {
            Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
            contest.startContest(siteNumber);
            ContestTime contestTime = contest.getContestTime(siteNumber);
            ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            info("Clock for site "+contestTime.getSiteNumber()+" started by "+clientId+" elapsed "+contestTime.getElapsedTimeStr());
            
        } else if (packetType.equals(Type.CLOCK_STOPPED)) {
            Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
            contest.stopContest(siteNumber);
            ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            ContestTime contestTime = contest.getContestTime(siteNumber);
            info("Clock for site "+contestTime.getSiteNumber()+" stopped by "+clientId+" elapsed "+contestTime.getElapsedTimeStr());

        } else if (packetType.equals(Type.ADD_SETTING)) {
            addNewSetting(packet);
            
        } else if (packetType.equals(Type.GENERATE_ACCOUNTS)) {
            generateAccounts (packet);

        } else if (packetType.equals(Type.UPDATE_SETTING)) {
            updateSetting(packet);

        } else if (packetType.equals(Type.RUN_CHECKOUT)) {
            // Run from server to clients
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            contest.addRun(run, runFiles, whoCheckedOut);
            
            sendToJudgesAndOthers( packet, false);

        } else if (packetType.equals(Type.RUN_REQUEST)) {
            // Request Run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            Boolean readOnly = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.READ_ONLY);
            if (readOnly != null) {
                fetchRun(packet, run, requestFromId, readOnly.booleanValue());

            } else {
                requestRun(packet, run, requestFromId);
            }

        } else if (packetType.equals(Type.LOGOUT)) {
            // client logged out
            logoutClient(packet);

        } else if (packetType.equals(Type.LOGIN)) {
            // client logged in
            loginClient(packet); 
            
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {
            // from server to client/server on a successful login

            if (isServer(packet.getDestinationId())) {
                /**
                 * Add server into login list.
                 */
                contest.addLogin(fromId, connectionHandlerID);
            }

            if (!contest.isLoggedIn()) {
                info(" handlePacket original LOGIN_SUCCESS before ");
                loadDataIntoModel(packet, connectionHandlerID);
                info(" handlePacket original LOGIN_SUCCESS after -- all settings loaded ");
            } else {
                loadSettingsFromRemoteServer(packet, connectionHandlerID);
                info(" handlePacket LOGIN_SUCCESS - from another site -- all settings loaded " + packet);
            }

        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            controller.getLog().log(Log.WARNING,"Unhandled Packet ", exception);
        }
        
        info("handlePacket end " + packet);

    }
    
    private void updateContestClock(Packet packet) {

        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);

        if (isServer()) {
            if (isThisSite(contestTime.getSiteNumber())) {
                contest.updateContestTime(contestTime);
                ContestTime updatedContestTime = contest.getContestTime(siteNumber);
                controller.getLog().info(
                        "Contest Settings updated by " + who + " running=" + updatedContestTime.isContestRunning() + " elapsed = " + updatedContestTime.getElapsedTimeStr() + " remaining= "
                                + updatedContestTime.getRemainingTimeStr() + " length=" + updatedContestTime.getContestLengthStr());
                Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
                controller.sendToTeams(updatePacket);
                sendToJudgesAndOthers(updatePacket, true);
            } else {
                controller.sendToRemoteServer(siteNumber, packet);
            }
        } else {
            controller.sendToTeams(packet);
            sendToJudgesAndOthers(packet, true);
        }
    }

    private boolean isThisSite(ClientId sourceId) {
        return isThisSite(sourceId.getSiteNumber());
    }

    private void sendForceDisconnection(Packet packet) {

        ConnectionHandlerID connectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);
        ClientId clientToLogoffId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {

            if (clientToLogoffId != null) {
                if (contest.isRemoteLoggedIn(clientToLogoffId)) {
                    // send logoff to other site
                    controller.sendToRemoteServer(clientToLogoffId.getSiteNumber(), packet);
                } else {
                    controller.removeConnection(connectionHandlerID);
                }

            } else if (connectionHandlerID != null) {
                if (contest.isConnected(connectionHandlerID)) {
                    // local connection, drop it now
                    controller.forceConnectionDrop(connectionHandlerID);
                } else {
                    // send to all servers, could be connected anywhere
                    controller.sendToServers(packet);
                }
            }
        } else {

            if (clientToLogoffId != null) {
                controller.removeLogin(clientToLogoffId);
            } else if (connectionHandlerID != null) {
                controller.removeConnection(connectionHandlerID);
            }
        }
    }

    /**
     * Update from admin to server.
     * 
     * @param packet
     */
    private void updateRun(Packet packet){
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);
        RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        ClientId whoChangedRun = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        
        if (isServer()) {
            if (isThisSite(run)) {

                // TODO security check
                // check permission, check user type
                
//                Account account = contest.getAccount(packet.getSourceId());
//                if (account.isAllowed(Permission.Type.EDIT_RUN)){
//                    // ok to update run
//                }
                
                if (isSuperUser(packet.getSourceId())) {
                    info("updateRun by " + packet.getSourceId() + " " + run);
                    if (judgementRecord != null) {
                        // TODO code add runResultsFiles
                        run.addJudgement(judgementRecord);
                        contest.updateRun(run, whoChangedRun);

                        
                    } else {
                        contest.updateRun(run, whoChangedRun);
                    }
                } else {
                    throw new SecurityException("Non-admin user "+packet.getSourceId()+" attempted to update run "+run);
                }
                
                Run theRun = contest.getRun(run.getElementId());
                Packet runUpdatedPacket = PacketFactory.createRunUpdateNotification(contest.getClientId(), PacketFactory.ALL_SERVERS, theRun, whoChangedRun);
                sendToJudgesAndOthers(runUpdatedPacket, true);
                
                if (theRun.isJudged() && theRun.getJudgementRecord().isSendToTeam()) {
                    // Send to team who sent it, send to other server if needed.
                    Packet notifyPacket = PacketFactory.clonePacket(contest.getClientId(), run.getSubmitter(), runUpdatedPacket);
                    controller.sendToClient(notifyPacket);
                }
                
            } else {
                controller.sendToRemoteServer(run.getSiteNumber(), packet);
            }

        } else {
            if (contest.isLocalLoggedIn(run.getSubmitter())) {
                controller.sendToClient(packet);
            }
            sendToJudgesAndOthers(packet, false);
        }
    }
    
    private void loginClient(Packet packet) {

        if (contest.isLoggedIn()) {
            ClientId whoLoggedIn = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            ConnectionHandlerID connectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);

            if (isServer()) {
                contest.addLogin(whoLoggedIn, connectionHandlerID);
                sendToJudgesAndOthers(packet, false);
            } else {
                contest.addLogin(whoLoggedIn, connectionHandlerID);
            }
        } else {
            info("Note: got a LOGIN packet before LOGIN_SUCCESS " + packet);
        }

    }

    private void logoutClient(Packet packet) {
        
        ClientId whoLoggedOff = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        if (isServer()){
            contest.removeLogin(whoLoggedOff);
            sendToJudgesAndOthers(packet, false);
        }else{
            contest.removeLogin(whoLoggedOff);
        }
    }

    /**
     * Send judgement to judges, servers, admins and boards.
     * @param packet
     */
    private void sendJudgementUpdate(Packet packet) {
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoModifiedRun = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()){
            contest.updateRun(run, whoModifiedRun);
            sendToJudgesAndOthers(packet, false);
        } else {
            contest.updateRun(run, whoModifiedRun);
        }
    }
    
    /**
     * Update from server to everyone else.
     * @param packet
     */
    private void sendRunUpdateNotification(Packet packet) {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoModifiedRun = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {
            contest.updateRun(run, whoModifiedRun);
            sendToJudgesAndOthers(packet, false);
        } else {
            contest.updateRun(run, whoModifiedRun);
        }
    }

    private void generateAccounts(Packet packet) {
        
        ClientType.Type type = (ClientType.Type) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_TYPE);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        Integer count = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.COUNT);
        Integer startCount = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.START_COUNT);
        Boolean active = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.CREATE_ACCOUNT_ACTIVE);
        
        if (isServer()){
            
            if (isThisSite(siteNumber)){
                
                // get vector of new accounts.
                Vector<Account> accountVector = contest.generateNewAccounts(type.toString(), count.intValue(), startCount.intValue(), active);
                Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
                Packet newAccountsPacket = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, accounts);
                sendToJudgesAndOthers(newAccountsPacket, true);
                
            } else {
                
                controller.sendToRemoteServer(siteNumber.intValue(), packet);
            }
            
        } else {
            throw new SecurityException("Client "+contest.getClientId()+" was send generate account packet "+packet);
        }
    }

    /**
     * This starts the contest and sends notification to other servers/clients.
     * @param contestTime
     * @param sourceServerId
     */
    private void startContest(Packet packet) {
        
        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);

        if (isThisSite(siteNumber)){
            contest.startContest(siteNumber);
            ContestTime updatedContestTime = contest.getContestTime(siteNumber);
            controller.getLog().info("Clock STARTED by "+who+" elapsed = "+updatedContestTime.getElapsedTimeStr());
            Packet startContestPacket = PacketFactory.createContestStarted(contest.getClientId(), PacketFactory.ALL_SERVERS, updatedContestTime.getSiteNumber(), who);
            controller.sendToTeams(startContestPacket);
            sendToJudgesAndOthers(startContestPacket, true);
        } else {
            controller.sendToRemoteServer(siteNumber, packet);
        }
    }

    /**
     *  This stops the contest and sends notification to other servers/clients.
     */
    private void stopContest(Packet packet) {
        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);

        if (isThisSite(siteNumber)){
            contest.stopContest(siteNumber);
            ContestTime updatedContestTime = contest.getContestTime(siteNumber);
            controller.getLog().info("Clock STOPPED by "+who+" elapsed = "+updatedContestTime.getElapsedTimeStr());
            Packet stopContestPacket = PacketFactory.createContestStopped(contest.getClientId(), PacketFactory.ALL_SERVERS, updatedContestTime.getSiteNumber(), who);
            controller.sendToTeams(stopContestPacket);
            sendToJudgesAndOthers(stopContestPacket, true);
        } else {
            controller.sendToRemoteServer(siteNumber, packet);
        }
    }

    /**
     * Add a new setting from another server.
     * 
     * @param packet
     */
    private void addNewSetting(Packet packet) {

        boolean sendToTeams = false;

        Site site = (Site) PacketFactory.getObjectValue(packet, PacketFactory.SITE);
        if (site != null) {
            contest.addSite(site);
            sendToTeams = true;
        }

        Judgement judgement = (Judgement) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT);
        if (judgement != null) {
            contest.addJudgement(judgement);
            sendToTeams = true;
        }

        Language language = (Language) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE);
        if (language != null) {
            contest.addLanguage(language);
            sendToTeams = true;
        }

        Problem problem = (Problem) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM);
        ProblemDataFiles problemDataFiles = (ProblemDataFiles) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_DATA_FILES);
        if (problem != null) {
            if (problemDataFiles != null){
                contest.addProblem(problem, problemDataFiles);
            } else {
                contest.addProblem(problem);
            }
            sendToTeams = true;
        }

        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
        if (contestTime != null) {
            contest.addContestTime(contestTime);
            sendToTeams = true;
        }

        Account oneAccount = (Account) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT);
        if (oneAccount != null) {
            if (isServer()) {
                if (contest.isLocalLoggedIn(oneAccount.getClientId())) {
                    Packet addPacket = PacketFactory.clonePacket(contest.getClientId(), oneAccount.getClientId(), packet);
                    controller.sendToClient(addPacket);
                }
            }
        }

        Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
        if (accounts != null) {
            for (Account account : accounts) {
                if (contest.getAccount(account.getClientId()) == null) {
                    contest.addAccount(account);
                }
                if (isServer()) {
                    if (contest.isLocalLoggedIn(account.getClientId())) {
                        Packet addPacket = PacketFactory.clonePacket(contest.getClientId(), oneAccount.getClientId(), packet);
                        controller.sendToClient(addPacket);
                    }
                }
            }
        }

        if (isServer()) {
            Packet addPacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
            boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
            sendToJudgesAndOthers(addPacket, sendToOtherServers);
            
            if (sendToTeams) {
                controller.sendToTeams(addPacket);
            }
        }
    }

    private void updateSetting(Packet packet) {

        boolean sendToTeams = false;
        
        Site site = (Site) PacketFactory.getObjectValue(packet, PacketFactory.SITE);
        if (site != null) {
            contest.updateSite(site);
            sendToTeams = true;
        }
        
        Judgement judgement = (Judgement) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT);
        if (judgement != null) {
            contest.updateJudgement(judgement);
            sendToTeams = true;
        }

        Language language = (Language) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE);
        if (language != null) {
            contest.updateLanguage(language);
            sendToTeams = true;
        }

        Problem problem = (Problem) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM);
        ProblemDataFiles problemDataFiles = (ProblemDataFiles) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_DATA_FILES);
        if (problem != null) {
            if (problemDataFiles != null){
                contest.updateProblem(problem, problemDataFiles);
            } else {
                contest.updateProblem(problem);
            }
            sendToTeams = true;
        }

        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
        if (contestTime != null) {
            contest.updateContestTime(contestTime);
            sendToTeams = true;
        }

        Account oneAccount = (Account) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT);
        if (oneAccount != null) {
            contest.updateAccount(oneAccount);
            if (isThisSite(oneAccount.getClientId().getSiteNumber())) {
                if (isServer()) {
                    Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), oneAccount.getClientId(), packet);
                    controller.sendToClient(updatePacket);
                }
            }
        }
        Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
        if (accounts != null) {
            for (Account account : accounts) {
                if (contest.getAccount(account.getClientId()) == null) {
                    contest.addAccount(account);
                }
                if (isServer()) {
                    if (contest.isLocalLoggedIn(account.getClientId())) {
                        Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), account.getClientId(), packet);
                        controller.sendToClient(updatePacket);
                    }
                }
            }
        }

        if (isServer()) {
            Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
            boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
            sendToJudgesAndOthers(updatePacket, sendToOtherServers);
            
            if (sendToTeams) {
                controller.sendToTeams(updatePacket);
            }
        }
    }
    
    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    private boolean isThisSite(ISubmission submission) {
        return submission.getSiteNumber() == contest.getSiteNumber();
    }

    /**
     * Send to all logged in Judges, Admins, Boards and optionally to other sites.
     * 
     * This sends all sorts of packets to all logged in clients (other than teams). Typically sendToServers is set if this is the originating site, if not done then a nasty circular path will occur.
     * 
     * @param packet
     * @param sendToServers
     *            send To other server.
     */
    public void sendToJudgesAndOthers(Packet packet, boolean sendToServers) {

        if (contest.getClientId().getClientType().equals(ClientType.Type.SERVER)) {
            // If I am a server
            // forward to clients on this site.
            controller.sendToAdministrators(packet);
            controller.sendToJudges(packet);
            controller.sendToScoreboards(packet);
            if (sendToServers) {
                controller.sendToServers(packet);
            }
        } // else not a server, just return.
    }

    private boolean isSuperUser(ClientId id) {
        return id.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }

    private void cancelRun(Packet packet, Run run, ClientId whoCanceledRun) {

        if (isServer()) {

            if (!isThisSite(run)) {

                controller.sendToRemoteServer(run.getSiteNumber(), packet);

            } else {

                // TODO: insure that this client checked out the run or send back a "oh no you didn't!"

                contest.cancelRunCheckOut(run, whoCanceledRun);

                Run availableRun = contest.getRun(run.getElementId());
                Packet availableRunPacket = PacketFactory.createRunAvailable(contest.getClientId(), whoCanceledRun, availableRun);

                sendToJudgesAndOthers( availableRunPacket, true);
            }

        } else {
            // Client, update status and done.

            run.setStatus(RunStates.NEW);
            contest.updateRun(run, whoCanceledRun);
        }
    }

    private void judgeRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedId) {

        if (isServer()) {

            if (!isThisSite(run)) {

                Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), run, judgementRecord, runResultFiles);
                controller.sendToRemoteServer(run.getSiteNumber(), judgementPacket);

            } else {
                // This site's run

                judgementRecord.setWhenJudgedTime(contest.getContestTime().getElapsedMins());

                contest.addRunJudgement(run, judgementRecord, runResultFiles, whoJudgedId);

                Run theRun = contest.getRun(run.getElementId());

                Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), theRun, judgementRecord, runResultFiles);
                if (judgementRecord.isSendToTeam()) {
                    // Send to team who sent it, send to other server if needed.
                    controller.sendToClient(judgementPacket);
                }
                
                Packet judgementUpdatePacket = PacketFactory.createRunJudgmentUpdate(contest.getClientId(), PacketFactory.ALL_SERVERS, theRun, whoJudgedId);
                sendToJudgesAndOthers( judgementUpdatePacket, true);
            }

        } else {
            contest.updateRun(run, judgementRecord.getJudgerClientId());
        }
    }
    
    /**
     * Checkout a run.
     * 
     * @see #fetchRun(Packet, Run, ClientId, boolean)
     * @param packet
     * @param run
     * @param whoRequestsRunId
     */
    private void requestRun(Packet packet, Run run, ClientId whoRequestsRunId) {
        fetchRun(packet,run,whoRequestsRunId, false);
    }
    
    /**
     * Fetch a run.
     * 
     * Either checks out run (marks as {@link edu.csus.ecs.pc2.core.contest.Run.RunStates#BEING_JUDGED BEING_JUDGED}) and send to everyone, or send a
     * {@link edu.csus.ecs.pc2.core.packet.PacketType.Type#RUN_NOTAVAILABLE RUN_NOTAVAILABLE}.
     * <P>
     * If readOnly is false, will checkout run. <br>
     * if readOnly is true will fetch run without setting
     * the run as "being judged".
     * 
     * @param packet 
     * @param run
     * @param whoRequestsRunId
     * @param readOnly - get a read only copy (aka do not checkout/select run).
     */
    private void fetchRun(Packet packet, Run run, ClientId whoRequestsRunId, boolean readOnly) {

        if (isServer()) {

            if (!isThisSite(run)) {

                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (contest.isLocalLoggedIn(serverClientId)) {

                    // send request to remote server
                    Packet requestPacket = PacketFactory.createRunRequest(contest.getClientId(), serverClientId, run, whoRequestsRunId, readOnly);
                    controller.sendToRemoteServer(run.getSiteNumber(), requestPacket);

                } else {

                    // send NOT_AVAILABLE back to client
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(contest.getClientId(), whoRequestsRunId, run);
                    controller.sendToClient(notAvailableRunPacket);
                }

            } else {
                // This Site's run, if we can check it out and send to client

                Run theRun = contest.getRun(run.getElementId());
                
                if (readOnly) {
                    // just get run and sent it to them.
                    
                    theRun = contest.getRun(run.getElementId());
                    RunFiles runFiles = contest.getRunFiles(run);

                    // send to Judge
                    Packet checkOutPacket = PacketFactory.createCheckedOutRun(contest.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId);
                    controller.sendToClient(checkOutPacket);

                } else if (run.getStatus() == RunStates.NEW || isSuperUser(whoRequestsRunId)) {

                    theRun.setStatus(RunStates.BEING_JUDGED);
                    contest.updateRun(theRun, whoRequestsRunId);

                    theRun = contest.getRun(run.getElementId());
                    RunFiles runFiles = contest.getRunFiles(run);

                    // send to Judge
                    Packet checkOutPacket = PacketFactory.createCheckedOutRun(contest.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId);
                    controller.sendToClient(checkOutPacket);

                    sendToJudgesAndOthers(checkOutPacket, true);
                    
                } else {
                    // Unavailable
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(contest.getClientId(), whoRequestsRunId, run);
                    controller.sendToClient(notAvailableRunPacket);
                }
            }
        } else {

            throw new SecurityException("requestRun - sent to client " + contest.getClientId());

        }
    }

    /**
     * Unpack and add list of runs to contest.
     * 
     * @param packet
     */
    private void addRunsToModel(Packet packet) {

        try {
            Run[] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
            if (runs != null) {
                for (Run run : runs) {
                    if ( (!isServer()) || (!isThisSite(run))) {
                        contest.addRun(run);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    /**
     * Unpack and add list of clarifications to contest.
     * 
     * @param packet
     */
    private void addClarificationsToModel(Packet packet) {

        try {
            Clarification[] clarifications = (Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {

                    if ( (!isServer()) || (!isThisSite(clarification))) {
                        contest.addClarification(clarification);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }
    
    /**
     * Unpack and add list of accounts to contest.
     * 
     * @param packet
     */
    private void addAccountsToModel (Packet packet) {

        try {
            
            Account [] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
            if (accounts != null) {
                for (Account account : accounts) {

                    if ( (!isServer()) || (!isThisSite(account))) {
                        contest.addAccount(account);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }
    
    /**
     * Unpack and add list of connection ids to contest.
     * 
     * @param packet
     */
    private void addConnectionIdsToModel(Packet packet) {

        try {
            ConnectionHandlerID[] connectionHandlerIDs = (ConnectionHandlerID[]) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID_LIST);
            if (connectionHandlerIDs != null) {
                for (ConnectionHandlerID connectionHandlerID : connectionHandlerIDs) {
                    contest.connectionEstablished(connectionHandlerID);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }

    }
    
    /**
     * Add logins to contest.
     * 
     * @param packet
     */
    private void addLoginsToModel(Packet packet) {
        
        try {
            
            ClientId [] clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOGGED_IN_USERS);
            if (clientIds != null){
                for (ClientId clientId : clientIds){
                    if ( (!isServer()) || (!isThisSite(clientId.getSiteNumber()))) {
                        
                        // TODO someday soon load logins with their connectionIds
                        ConnectionHandlerID fakeId = new ConnectionHandlerID("Fake-Site"+clientId.getSiteNumber());
                        contest.addLogin(clientId, fakeId);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }

    private boolean isThisSite(Account account) {
        return account.getSiteNumber() == contest.getSiteNumber();
    }
    
    private void loadSettingsFromRemoteServer(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        addContestTimesToModel(packet);

        addSitesToModel(packet);

        addRunsToModel(packet);

        addClarificationsToModel(packet);

        addAccountsToModel(packet);

        // TODO debug from remote servers
//        addConnectionIdsToModel(packet);
//        
//        addLoginsToModel (packet);
        
    }

    /**
     * Add contest data into the contest.
     * 
     * This will read a packet and load the data into the contest. <br>
     * This should only be execute with the first LOGIN_SUCCESS that this module processes.
     * <P>
     * It processes:
     * <ol>
     * <li> {@link PacketFactory#CLIENT_ID}
     * <li> {@link PacketFactory#SITE_NUMBER}
     * <li> {@link PacketFactory#SITE_LIST}
     * <li> {@link PacketFactory#LANGUAGE_LIST}
     * <li> {@link PacketFactory#PROBLEM_LIST}
     * <li> {@link PacketFactory#JUDGEMENT_LIST}
     * <li> {@link PacketFactory#CONTEST_TIME}
     * <ol>
     * 
     * @param packet
     */
    private void loadDataIntoModel(Packet packet, ConnectionHandlerID connectionHandlerID) {

        ClientId clientId = null;

        try {
            clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            if (clientId != null) {
                contest.setClientId(clientId);
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        controller.setSiteNumber(clientId.getSiteNumber());
        
        try {
            Language[] languages = (Language[]) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE_LIST);
            if (languages != null) {
                for (Language language : languages) {
                    contest.addLanguage(language);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
        
        addProblemsToModel(packet);

        
        try {
            Judgement[] judgements = (Judgement[]) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_LIST);
            if (judgements != null) {
                for (Judgement judgement : judgements) {
                    contest.addJudgement(judgement);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        try {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            if (contestTime != null) {
                contest.addContestTime(contestTime);
            }

        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        addContestTimesToModel(packet);
        
        addSitesToModel(packet);

        addRunsToModel(packet);

        addClarificationsToModel(packet);
        
        addAccountsToModel (packet);
        
        addConnectionIdsToModel (packet);
        
//        addLoginsToModel(packet);

        if (contest.isLoggedIn()) {
            
            // insure that this site's contest time exists 
            if (contest.getContestTime() == null){
                ContestTime contestTime = new ContestTime();
                contestTime.setSiteNumber(contest.getSiteNumber());
                contest.addContestTime(contestTime);
            }

            // show main UI
            controller.startMainUI(contest.getClientId());

            // Login to other sites
            loginToOtherSites(packet);
        } else {
            String message = "Trouble logging in, check logs";
            contest.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }
    }

    /**
     * Add both problems and problem data files into contest.
     * 
     * @param packet
     */
    private void addProblemsToModel(Packet packet) {

        // First add all the problem data files to a list.
        
        ProblemDataFilesList problemDataFilesList = new ProblemDataFilesList();
        
        try {
            ProblemDataFiles[] problemDataFiles = (ProblemDataFiles[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_DATA_FILES);
            if (problemDataFiles != null) {
                for (ProblemDataFiles problemDataFile : problemDataFiles) {
                    problemDataFilesList.add(problemDataFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
        
        try {
            Problem[] problems = (Problem[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_LIST);
            if (problems != null) {
                for (Problem problem : problems) {
                    
                    // Now add both problem and potentially problem data files into contest.
                    
                    contest.addProblem(problem, (ProblemDataFiles) problemDataFilesList.get(problem));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    private void addContestTimesToModel(Packet packet) {
        try {
            ContestTime[] contestTimes = (ContestTime[]) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME_LIST);
            if (contestTimes != null) {
                for (ContestTime contestTime : contestTimes) {
                    if (contest.getSiteNumber() != contestTime.getSiteNumber()) {
                        // Update other sites contestTime, do not touch ours.
                        if (contest.getContestTime(contestTime.getSiteNumber()) != null) {
                            contest.updateContestTime(contestTime);
                        } else {
                            contest.addContestTime(contestTime);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    /**
     * Unpack and add a list of sites to the contest.
     * 
     * @param packet
     * @param contest
     */
    private void addSitesToModel(Packet packet) {
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    contest.addSite(site);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    /**
     * Login to all other servers.
     * 
     * 
     * 
     * @param packet
     *            contains list of other servers
     */
    private void loginToOtherSites(Packet packet) {
        try {
            for (Site site : contest.getSites()) {
                if (!isThisSite(site.getSiteNumber())) {
                    // TODO code - do not log into servers that are already logged in.
                    controller.sendServerLoginRequest(site.getSiteNumber());
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }

    /**
     * Is the input ClientId a server. 
     * @param id
     * @return
     */
    private boolean isServer(ClientId id) {
        return id.getClientType().equals(ClientType.Type.SERVER);
    }
    
    /**
     * Is this client a server.
     * @return true if is a server.
     */
    private boolean isServer(){
        return isServer(contest.getClientId());
    }
    
    public void info(String s) {
        controller.getLog().warning(s);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
    }

    public void info(String s, Exception exception) {
        controller.getLog().log (Log.WARNING, s, exception);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
        exception.printStackTrace(System.err);
    }

}
