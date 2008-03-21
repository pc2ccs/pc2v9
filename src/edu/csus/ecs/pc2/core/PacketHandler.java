package edu.csus.ecs.pc2.core;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.ClarificationUnavailableException;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.exception.UnableToUncheckoutRunException;
import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.log.EvaluationLog;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestLoginSuccessData;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
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
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Process all incoming packets.
 * 
 * Process packets. In {@link #handlePacket(Packet, ConnectionHandlerID) handlePacket} a packet is unpacked, contest is updated, and controller used to send packets as needed.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PacketHandler {

    private IInternalContest contest = null;

    private IInternalController controller = null;
    
    /**
     * Message handler for conditions where attention may be needed.
     */
    private EvaluationLog evaluationLog = null;

    public PacketHandler(IInternalController controller, IInternalContest contest) {
        this.controller = controller;
        this.contest = contest;
    }

    public PacketHandler(InternalController controller, IInternalContest contest) {
        // TODO remove this constructor and keep IInternalController one, at
        // this time that change causes a NoSuchMethodException
       
        this.controller = controller;
        this.contest = contest;
    }

    /**
     * Take each input packet, update the contest, send out packets as needed.
     * 
     * @param packet
     * @param connectionHandlerID
     */
    public void handlePacket(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        Type packetType = packet.getType();

        info("handlePacket start " + packet);
        PacketFactory.dumpPacket(controller.getLog(),packet, "handlePacket");
        
        if (isServer() || isAdministrator()){
          PacketFactory.dumpPacket(System.err,packet, "handlePacket");
        }

        ClientId fromId = packet.getSourceId();

        if (packetType.equals(Type.MESSAGE)) {
            PacketFactory.dumpPacket(System.err, packet, null);
            handleMessagePacket (packet);
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
            if (isServer()) {
                sendToJudgesAndOthers( confirmPacket, true);
            }            
        } else if (packetType.equals(Type.CLARIFICATION_SUBMISSION)) {
            // Clarification submitted by team to server
            
            Clarification submittedClarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            Clarification clarification = contest.acceptClarification(submittedClarification);
            
            // Send to team
            Packet confirmPacket = PacketFactory.createClarSubmissionConfirm(contest.getClientId(), fromId, clarification);
            controller.sendToClient(confirmPacket);
            
            // Send to clients and other servers
            if (isServer()) {
                sendToJudgesAndOthers(confirmPacket, true);
            }

        } else if (packetType.equals(Type.CLARIFICATION_ANSWER)) {
            // Answer from client to server
            answerClarification (packet, connectionHandlerID);
            
        } else if (packetType.equals(Type.CLARIFICATION_ANSWER_UPDATE)) {
            // Answer from server to client
            sendAnswerClarification (packet);
            
        } else if (packetType.equals(Type.CLARIFICATION_SUBMISSION_CONFIRM)) {
            Clarification clarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            contest.addClarification(clarification);
            if (isServer()) {
                sendToJudgesAndOthers( packet, isThisSite(clarification));
            }
            
        } else if (packetType.equals(Type.CLARIFICATION_UNCHECKOUT)) {
            // Clarification cancel or un-checkout, client to server
            cancelClarificationCheckOut(packet, connectionHandlerID);

        } else if (packetType.equals(Type.CLARIFICATION_CHECKOUT)) {
            // The clarification that was checked out, sent from server to clients
            checkoutClarification(packet, connectionHandlerID);
        } else if (packetType.equals(Type.CLARIFICATION_AVAILABLE)) {
            // Server to client, run was canceled, now available
            sendClarificationAvailable(packet);
            
        } else if (packetType.equals(Type.LOGIN_FAILED)) {
            String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
            contest.loginDenied(packet.getDestinationId(), connectionHandlerID, message);

        } else if (packetType.equals(Type.CLARIFICATION_NOT_AVAILABLE)) {
            // Run not available from server
            Clarification clar = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            contest.clarificationNotAvailable(clar);

            if (isServer()) {
                sendToJudgesAndOthers( packet, isThisSite(clar));
            }
        } else if (packetType.equals(Type.RUN_NOTAVAILABLE)) {
            // Run not available from server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            contest.runNotAvailable(run);

            if (isServer()) {
                sendToJudgesAndOthers( packet, isThisSite(run));
            }

        } else if (packetType.equals(Type.FORCE_DISCONNECTION)) {
            sendForceDisconnection (packet);

        } else if (packetType.equals(Type.ESTABLISHED_CONNECTION)) {
            establishConnection (packet, connectionHandlerID);
        } else if (packetType.equals(Type.DROPPED_CONNECTION)) {
            droppedConnection (packet, connectionHandlerID);
            
        } else if (packetType.equals(Type.RUN_AVAILABLE)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            contest.availableRun(run);

            if (isServer()){
                sendToJudgesAndOthers( packet, isThisSite(run));
            }

        } else if (packetType.equals(Type.RUN_JUDGEMENT)) {
            // Judgement from judge to server
            acceptRunJudgement(packet, connectionHandlerID);

        } else if (packetType.equals(Type.RUN_JUDGEMENT_UPDATE)) {
            sendJudgementUpdate(packet);
            
        } else if (packetType.equals(Type.RUN_UPDATE)) {
            updateRun (packet, connectionHandlerID);

        } else if (packetType.equals(Type.RUN_UPDATE_NOTIFICATION)) {
            sendRunUpdateNotification(packet);
            
        } else if (packetType.equals(Type.RUN_UNCHECKOUT)) {
            // Cancel run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId whoCanceledId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            cancelRun(packet, run,  whoCanceledId, connectionHandlerID);
            
        } else if (packetType.equals(Type.START_ALL_CLOCKS)) {
            // Start All Clocks from admin to server
            startContest(packet, connectionHandlerID);
            
            if (isThisSite(packet.getSourceId())){
                controller.sendToServers(packet);
            }
            
        } else if (packetType.equals(Type.STOP_ALL_CLOCKS)) {
            // Start All Clocks from admin to server
            stopContest(packet, connectionHandlerID);
            
            if (isThisSite(packet.getSourceId())){
                controller.sendToServers(packet);
            }
            
        } else if (packetType.equals(Type.START_CONTEST_CLOCK)) {
            // Admin to server, start the clock
            startContest(packet, connectionHandlerID);
            
        } else if (packetType.equals(Type.STOP_CONTEST_CLOCK)) {
            // Admin to server, stop the clock
            stopContest(packet, connectionHandlerID);

        } else if (packetType.equals(Type.UPDATE_CONTEST_CLOCK)) {
            // Admin to server, stop the clock
            updateContestClock(packet);
            
        } else if (packetType.equals(Type.CLOCK_STARTED)) {
            // InternalContest Clock started sent from server to clients
            Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
            contest.startContest(siteNumber);
            ContestTime contestTime = contest.getContestTime(siteNumber);
            ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            info("Clock for site "+contestTime.getSiteNumber()+" started by "+clientId+" elapsed "+contestTime.getElapsedTimeStr());
            
            if (isServer()){
                controller.sendToTeams(packet);
                sendToJudgesAndOthers(packet, false);
            }
            
        } else if (packetType.equals(Type.CLOCK_STOPPED)) {
            // InternalContest Clock stopped sent from server to clients
            Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
            contest.stopContest(siteNumber);
            ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            ContestTime contestTime = contest.getContestTime(siteNumber);
            info("Clock for site "+contestTime.getSiteNumber()+" stopped by "+clientId+" elapsed "+contestTime.getElapsedTimeStr());

            if (isServer()){
                controller.sendToTeams(packet);
                sendToJudgesAndOthers(packet, false);
            }

        } else if (packetType.equals(Type.ADD_SETTING)) {
            addNewSetting(packet);
            
        } else if (packetType.equals(Type.DELETE_SETTING)) {
            deleteSetting(packet);
            
        } else if (packetType.equals(Type.GENERATE_ACCOUNTS)) {
            generateAccounts (packet);

        } else if (packetType.equals(Type.UPDATE_SETTING)) {
            updateSetting(packet);

        } else if (packetType.equals(Type.RUN_CHECKOUT)) {
            // Run from server to clients
            runCheckout (packet);

        } else if (packetType.equals(Type.RUN_REJUDGE_CHECKOUT)) {
            // Run from server to clients
            runCheckout (packet); // this works for rejudge as well.
            
        } else if (packetType.equals(Type.CLARIFICATION_REQUEST)) {
            requestClarification(packet, connectionHandlerID);

        } else if (packetType.equals(Type.RUN_REQUEST)) {
            // Request Run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            Boolean readOnly = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.READ_ONLY);
            Boolean computerJudge = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.COMPUTER_JUDGE);
            if (readOnly != null) {
                fetchRun(packet, run, requestFromId, readOnly.booleanValue(), computerJudge.booleanValue(), connectionHandlerID);

            } else {
                requestRun(packet, run, requestFromId, connectionHandlerID, computerJudge);
            }

        } else if (packetType.equals(Type.RUN_REJUDGE_REQUEST)) {
            // REJUDGE Request Run from requestor to server
            requestRejudgeRun (packet, connectionHandlerID);
            
        } else if (packetType.equals(Type.LOGOUT)) {
            // client logged out
            logoutClient(packet);

        } else if (packetType.equals(Type.LOGIN)) {
            // client logged in
            loginClient(packet); 
            
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {
            // from server to client/server on a successful login

            loginSuccess(packet, connectionHandlerID, fromId);
            
        } else if (packetType.equals(Type.SERVER_SETTINGS)) {

            // This is settings from a recently logged in server
            loadSettingsFromRemoteServer(packet, connectionHandlerID);
            info(" handlePacket SERVER_SETTINGS - from another site -- all settings loaded " + packet);
            
            if (isServer()) {
                sendToJudgesAndOthers(packet, false);
            }

        } else if (packetType.equals(Type.RECONNECT_SITE_REQUEST)) {

            reconnectSite (packet);
            
        } else if (packetType.equals(Type.SECURITY_MESSAGE)) {
            // From server to admins
            handleSecurityMessage(packet);
            
        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            controller.getLog().log(Log.WARNING,"Unhandled Packet ", exception);
        }
        
        info("handlePacket end " + packet);
        
    }

    protected void droppedConnection(Packet packet, ConnectionHandlerID connectionHandlerID) {
        ConnectionHandlerID inConnectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);
        if (isServer()) {
            if (isThisSite(packet.getSourceId())){
                controller.sendToServers(packet);
            }
            sendToJudgesAndOthers (packet, false);
            contest.connectionDropped(inConnectionHandlerID);
        } else {
            contest.connectionDropped(inConnectionHandlerID);
        }
    }

    private void handleSecurityMessage (Packet inPacket) {

        ClientId clientId = (ClientId) PacketFactory.getObjectValue(inPacket, PacketFactory.CLIENT_ID);
        String message = (String) PacketFactory.getObjectValue(inPacket, PacketFactory.MESSAGE);
        ContestSecurityException contestSecurityException = (ContestSecurityException) PacketFactory.getObjectValue(inPacket, PacketFactory.EXCEPTION);
//        Packet packet = (Packet)PacketFactory.getObjectValue(inPacket, PacketFactory.PACKET);
        
        controller.getLog().log(Log.WARNING, "Security violation "+clientId+" "+message);

        contest.newSecurityMessage(clientId, "", message, contestSecurityException);
        
        if (isServer()){
            Packet forwardPacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, inPacket);
            controller.sendToAdministrators(forwardPacket);
        }
    }

    private void establishConnection(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        ConnectionHandlerID inConnectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);

        if (isServer()) {
            controller.sendToAdministrators(packet);
            if (isThisSite(packet.getSourceId())){
                controller.sendToServers(packet);
            }
            contest.connectionEstablished(inConnectionHandlerID);
        } else {
            contest.connectionEstablished(inConnectionHandlerID);
        }


    }

    protected void checkoutClarification(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        
        Clarification clarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        
        if (isServer()){
            securityCheck (Permission.Type.ANSWER_CLARIFICATION, whoCheckedOut, connectionHandlerID);
        }
        
        contest.addClarification(clarification, whoCheckedOut);
        if (isServer()){
            sendToJudgesAndOthers( packet, false);
        }
    }

    /**
     * Is Client allowed to use Permission.
     * 
     * @param clientId
     * @param type
     * @return
     */
    protected boolean isAllowed(ClientId clientId, Permission.Type type) {
        try {
            Account account = contest.getAccount(clientId);
            if (account != null) {
                return account.getPermissionList().isAllowed(type);
            }
        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
        return false;
    }
    
    /**
     * Checks whether client is allowed to do particular activity +(Permission).
     * 
     * This checks the client permissions settings and if the client does not
     * have permission to do the permission (type) throws a security exception.
     * 
     * @param type
     * @param clientId
     * @param connectionHandlerID
     */
    protected void securityCheck(Permission.Type type, ClientId clientId, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        
        if (controller.getSecurityLevel() < InternalController.SECURITY_HIGH_LEVEL){
            return;
        }
        
        if (!isAllowed(clientId, type)) {
            throw new ContestSecurityException(clientId, connectionHandlerID, clientId + " not allowed to " + type);
        }
    }


    private void acceptRunJudgement(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);
        RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        ClientId whoJudgedRunId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        judgeRun(run,  judgementRecord, runResultFiles, whoJudgedRunId, connectionHandlerID);

    }

    /**
     * 
     * @param packet
     */
    private void runCheckout(Packet packet) {

        // Run checkout OR run re-judge checkout
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
        ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        RunResultFiles[] runResultFiles = (RunResultFiles[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        contest.updateRun(run, runFiles, whoCheckedOut, runResultFiles);
        
        if (isServer()){
            sendToJudgesAndOthers( packet, false);
        }
        
    }

    /**
     * Re-judge run request, parse packet, attempt to checkout run. 
     * @param packet
     * @param connectionHandlerID 
     * @throws ContestSecurityException 
     */
    private void requestRejudgeRun(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoRequestsRunId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        
        if (isServer()) {

            if (!isThisSite(run)) {

                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (contest.isLocalLoggedIn(serverClientId)) {

                    // send request to remote server
                    Packet requestPacket = PacketFactory.createRunRejudgeRequest(contest.getClientId(), serverClientId, run, whoRequestsRunId);
                    controller.sendToRemoteServer(run.getSiteNumber(), requestPacket);

                } else {

                    // send NOT_AVAILABLE back to client
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(contest.getClientId(), whoRequestsRunId, run);
                    controller.sendToClient(notAvailableRunPacket);
                }

            } else {
                // This Site's run, if we can check it out and send to client

                Run theRun = contest.getRun(run.getElementId());

                try {
                    
                    securityCheck (Permission.Type.REJUDGE_RUN, whoRequestsRunId, connectionHandlerID);
                    
                    theRun = contest.checkoutRun(run, whoRequestsRunId, true, false);
                    RunFiles runFiles = contest.getRunFiles(run);

                    // send to Judge
                    Packet checkOutPacket = PacketFactory.createRejudgeCheckedOut(contest.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId);
                    controller.sendToClient(checkOutPacket);

                    // TODO change this packet type so it is not confused with the actual checked out run.

                    sendToJudgesAndOthers(checkOutPacket, true);
                } catch (RunUnavailableException runUnavailableException) {
                    theRun = contest.getRun(run.getElementId());
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(contest.getClientId(), whoRequestsRunId, theRun);
                    controller.sendToClient(notAvailableRunPacket);
                }
            }
        } else {

            throw new SecurityException("requestRun - sent to client " + contest.getClientId());

        }

    }

    private void reconnectSite(Packet packet) {
        
       Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
       if (siteNumber != null){
           try {
            controller.getLog().log(Log.INFO, "Client "+packet.getSourceId()+" requests reconnection to site "+siteNumber);
            controller.sendServerLoginRequest(siteNumber.intValue());
        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Unable to send reconnection request to ", e);
        }
       }
    }

    private void handleMessagePacket(Packet packet) {

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

    }

    private void loginSuccess(Packet packet, ConnectionHandlerID connectionHandlerID, ClientId fromId) {

        if (!contest.isLoggedIn()) {
            // Got the first LOGIN_SUCCESS, first connection into server.
            
            ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            if (isServer(clientId)) {
                String uberSecretatPassworden = (String) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_PASSWORD);
                if (uberSecretatPassworden == null) {
                    StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ");
                    System.err.println("FATAL ERROR - Contest Security Password is null ");
                    System.exit(44);
                }

                new FileSecurity("db." + clientId.getSiteNumber());

                try {
                    FileSecurity.verifyPassword(uberSecretatPassworden.toCharArray());

                } catch (FileSecurityException fileSecurityException) {
                    if (fileSecurityException.getMessage().equals(FileSecurity.KEY_FILE_NOT_FOUND)) {

                        try {
                            FileSecurity.saveSecretKey(uberSecretatPassworden.toCharArray());
                        } catch (Exception e) {
                            StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", e);
                            System.err.println("FATAL ERROR " + e.getMessage() + " check logs");
                            System.exit(44);
                        }
                    } else {
                        StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", fileSecurityException);
                        System.err.println("FATAL ERROR " + fileSecurityException.getMessage() + " check logs");
                        System.exit(44);
                    }
                } catch (Exception e) {
                    StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ", e);
                    System.err.println("FATAL ERROR " + e.getMessage() + " check logs");
                    System.exit(44);
                }
                
                controller.setContestPassword(uberSecretatPassworden);
            }
            
            info(" handlePacket original LOGIN_SUCCESS before ");
            loadDataIntoModel(packet, connectionHandlerID);
            info(" handlePacket original LOGIN_SUCCESS after -- all settings loaded ");
            
            if (isServer()) {
                
                if (contest.isLocalLoggedIn(fromId)) {
                    contest.removeLogin(fromId);
                }
                
                if (contest.isRemoteLoggedIn(fromId)){
                    contest.removeRemoteLogin(fromId);
                }

                // Add the other site as a local login
                contest.addLocalLogin(fromId, connectionHandlerID);
                
                // Send settings packet to the server we logged into
                controller.sendToClient(createContestSettingsPacket(packet.getSourceId()));
            }
            
        } else if (isServer(packet.getDestinationId())){
            // Got a LOGIN_SUCCESS from another server
            if (contest.isRemoteLoggedIn(fromId)) {
                contest.removeRemoteLogin(fromId);
            }

            // Add the other site as a local login
            contest.addLocalLogin(fromId, connectionHandlerID);

            loadSettingsFromRemoteServer(packet, connectionHandlerID);
            
            controller.sendToClient(createContestSettingsPacket(packet.getSourceId()));
            
        } else {
            // If logged in client, should not get another LOGIN_SUCCESS
            Exception ex = new Exception("Client " + contest.getClientId() + " received unexpected packet, not logged in but got a " + packet);
            controller.getLog().log(Log.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Dump both local and remote server logins.
     * 
     * @param comment
     */
    private void dumpServerLoginLists(String comment) {

        info("dumpLoginLists (Site " + contest.getSiteNumber() + ") " + comment);

        ClientId[] clientIds = contest.getLocalLoggedInClients(edu.csus.ecs.pc2.core.model.ClientType.Type.SERVER);
        String message = "   " + clientIds.length + "  local logins:";
        for (ClientId clientId : clientIds) {
            message += " Site " + clientId.getSiteNumber();
        }
        info(message + ".");

        clientIds = contest.getRemoteLoggedInClients(edu.csus.ecs.pc2.core.model.ClientType.Type.SERVER);

        message = "   " + clientIds.length + " remote logins:";
        for (ClientId clientId : clientIds) {
            message += " Site " + clientId.getSiteNumber();
        }

        info(message + ".");
    }

    private void updateContestClock(Packet packet) {

        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);

        if (isServer()) {
            if (isThisSite(contestTime.getSiteNumber())) {
                
                // TODO securityCheck updateContestClock
                
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
            
            controller.writeConfigToDisk();

        } else {
            controller.sendToTeams(packet);
            if (isServer()) {
                sendToJudgesAndOthers(packet, true);
            }
        }
        
        if (isServer()){
            controller.writeConfigToDisk();
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
     * @throws ContestSecurityException 
     */
    private void updateRun(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException{
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);
        
        // TODO add runResultsFiles to updated run results.
//        RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        ClientId whoChangedRun = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        
        if (isServer()) {
            if (isThisSite(run)) {

                // TODO security check updateRun
                
//                Account account = contest.getAccount(packet.getSourceId());
//                if (account.isAllowed(Permission.Type.EDIT_RUN)){
//                    // ok to update run
//                }
                
                securityCheck (Permission.Type.EDIT_RUN, packet.getSourceId(), connectionHandlerID);
                
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
    
    /**
     * Login from a remote server.
     * 
     * @param packet
     */
    private void loginClient(Packet packet) {

        if (contest.isLoggedIn()) {
            ClientId whoLoggedIn = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            ConnectionHandlerID connectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);
            
            if (isServer()) {
                info("LOGIN from other site " + whoLoggedIn);

                if (!contest.isLocalLoggedIn(whoLoggedIn)) {
                    // if client not already logged in

                    if (!isThisSite(whoLoggedIn)) {
                        if (isServer(whoLoggedIn)) {
                            if (!contest.isRemoteLoggedIn(whoLoggedIn)) {
                                // Add to remote login list if not in list
                                contest.addRemoteLogin(whoLoggedIn, connectionHandlerID);
                                sendToJudgesAndOthers(packet, false);
                            }
                        }
                    }
                } else {
                    controller.getLog().log(Log.DEBUG, "LOGIN packet, server site " + whoLoggedIn + " logged onto " + packet.getSourceId() + ", already logged in on this site");
                }

            } else {
                contest.addLogin(whoLoggedIn, connectionHandlerID);
            }
        } else {
            info("Note: got a LOGIN packet before this site was logged in " + packet);
        }

    }

    private void logoutClient(Packet packet) {

        ClientId whoLoggedOff = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        if (isServer()) {

            if (contest.isLocalLoggedIn(whoLoggedOff)) {
                controller.logoffUser(whoLoggedOff);
                // Only send to servers if this clientId is NOT a server
                
                // TODO look at this logic, see if isServer(whoLoggedOff) is ok to use.
                sendToJudgesAndOthers(packet, false);
//                sendToJudgesAndOthers(packet, !isServer(whoLoggedOff));
            } else if (!isServer(whoLoggedOff)) {
                // if not a sever log in, forward packet to other server
                controller.sendToRemoteServer(whoLoggedOff.getSiteNumber(), packet);
            } else {
                // This is a server logoff from another server
                if (contest.isRemoteLoggedIn(whoLoggedOff)) {
                    contest.removeRemoteLogin(whoLoggedOff);
                    sendToJudgesAndOthers(packet, false);
                }
            }
        } else {
            contest.removeLogin(whoLoggedOff);
        }
    }

    /**
     * Send judgement to judges, servers, admins and boards.
     * 
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
    
    private void sendAnswerClarification(Packet packet) {
        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        ClientId whoModifiedClarification = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {
            contest.answerClarification(clarification, clarification.getAnswer(), whoModifiedClarification, clarification.isSendToAll());
            sendToJudgesAndOthers(packet, false);

            if (clarification.isSendToAll()) {
                // Send to all teams
                controller.sendToTeams(packet);

            } else if (isThisSite(clarification)) {
                // Send to team
                Packet answerPacket = PacketFactory.clonePacket(contest.getClientId(), clarification.getSubmitter(), packet);
                controller.sendToClient(answerPacket);
            }
        } else {
            contest.answerClarification(clarification, clarification.getAnswer(), whoModifiedClarification, clarification.isSendToAll());
        }
    }
    
    /**
     * Update from server to everyone else.
     * 
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

    /**
     * Generate local accounts for forward this request to another server.
     * @param packet
     */
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
                
                controller.writeConfigToDisk();
                
                Packet newAccountsPacket = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, accounts);
                sendToJudgesAndOthers(newAccountsPacket, true);
                
            } else {
                
                controller.sendToRemoteServer(siteNumber.intValue(), packet);
            }
            
        } else {
            throw new SecurityException("Client "+contest.getClientId()+" was sent generate account packet "+packet);
        }
    }

    /**
     * This starts the contest and sends notification to other servers/clients.
     * @param connectionHandlerID 
     * @param contestTime
     * @param sourceServerId
     * @throws ContestSecurityException 
     */
    private void startContest(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        if (packet.getType().equals(Type.START_ALL_CLOCKS)) {
            siteNumber = new Integer(contest.getSiteNumber());
        }

        if (isThisSite(siteNumber)) {
            
            securityCheck (Permission.Type.START_CONTEST_CLOCK, packet.getSourceId(), connectionHandlerID);
            
            contest.startContest(siteNumber);
            ContestTime updatedContestTime = contest.getContestTime(siteNumber);
            controller.getLog().info("Clock STARTED by " + who + " elapsed = " + updatedContestTime.getElapsedTimeStr());
            Packet startContestPacket = PacketFactory.createContestStarted(contest.getClientId(), PacketFactory.ALL_SERVERS, updatedContestTime.getSiteNumber(), who);
            controller.sendToTeams(startContestPacket);
            sendToJudgesAndOthers(startContestPacket, true);

        } else {
            if (packet.getType().equals(Type.START_ALL_CLOCKS)) {
                ClientId[] clientIds = contest.getLocalLoggedInClients(ClientType.Type.SERVER);
                for (ClientId clientId : clientIds) {
                    Packet startContestPacket = PacketFactory.createStartContestClock(contest.getClientId(), PacketFactory.ALL_SERVERS, siteNumber, packet.getSourceId());
                    controller.sendToRemoteServer(clientId.getSiteNumber(), startContestPacket);
                }
            } else {
                controller.sendToRemoteServer(siteNumber, packet);
            }
        }

        if (isServer()) {
            controller.writeConfigToDisk();
        }
    }

    /**
     * This stops the contest and sends notification to other servers/clients.
     * @param connectionHandlerID 
     * @throws ContestSecurityException 
     */
    private void stopContest(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        if (packet.getType().equals(Type.STOP_ALL_CLOCKS)) {
            siteNumber = new Integer(contest.getSiteNumber());
        }
        
        if (isThisSite(siteNumber)){
            
            securityCheck (Permission.Type.STOP_CONTEST_CLOCK, who, connectionHandlerID);
            
            contest.stopContest(siteNumber);
            ContestTime updatedContestTime = contest.getContestTime(siteNumber);
            controller.getLog().info("Clock STOPPED by "+who+" elapsed = "+updatedContestTime.getElapsedTimeStr());
            Packet stopContestPacket = PacketFactory.createContestStopped(contest.getClientId(), PacketFactory.ALL_SERVERS, updatedContestTime.getSiteNumber(), who);
            controller.sendToTeams(stopContestPacket);
            sendToJudgesAndOthers(stopContestPacket, true);
            
        } else {

            if (packet.getType().equals(Type.STOP_ALL_CLOCKS)) {
                ClientId[] clientIds = contest.getLocalLoggedInClients(ClientType.Type.SERVER);
                for (ClientId clientId : clientIds) {
                    Packet startContestPacket = PacketFactory.createStopContestClock(contest.getClientId(), PacketFactory.ALL_SERVERS, siteNumber, packet.getSourceId());
                    controller.sendToRemoteServer(clientId.getSiteNumber(), startContestPacket);
                }
            } else {
                controller.sendToRemoteServer(siteNumber, packet);
            }
        }
        
        if (isServer()){
            controller.writeConfigToDisk();
        }
    }
    
    private void deleteSetting(Packet packet) {
        
        // TODO code
        
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

        Group group = (Group) PacketFactory.getObjectValue(packet, PacketFactory.GROUP);
        if (group != null) {
            contest.addGroup(group);
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
        
        BalloonSettings balloonSettings = (BalloonSettings) PacketFactory.getObjectValue(packet, PacketFactory.BALLOON_SETTINGS);
        if (balloonSettings != null) {
            contest.addBalloonSettings(balloonSettings);
            sendToTeams = true;
        }

        Account oneAccount = (Account) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT);
        if (oneAccount != null) {
            if (isServer()) {
                if (isThisSite(oneAccount)){
                    ClientId clientId = oneAccount.getClientId();
                    
                    // Add account, this assigns the new account a client number.
                    Vector<Account> accountVector = contest.generateNewAccounts(clientId.getClientType().toString(), 1, true);
                    Account addedAccount = accountVector.firstElement();
                    
                    // Update/clone new account
                    addedAccount.setDisplayName(oneAccount.getDisplayName());
                    addedAccount.setPassword(oneAccount.getPassword());
                    addedAccount.clearListAndLoadPermissions(oneAccount.getPermissionList());
                    contest.updateAccount(oneAccount);

                    // Send new account to all servers, admins and judges
                    Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, contest.getAccount(oneAccount.getClientId()));
                    sendToJudgesAndOthers(updatePacket, true);
                }
            } else {
                contest.updateAccount(oneAccount);
            }
        }

        Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
        if (accounts != null) {
            // TODO change these from Vector to something lightweight
            Vector<Account> addAccountsVector = new Vector<Account>();
            for (Account account : accounts) {
                // split the incoming list between add and no-op updates
                if (contest.getAccount(account.getClientId()) == null) {
                    addAccountsVector.add(account);
                }
            }
            if (addAccountsVector.size() > 0) {
                contest.addAccounts(addAccountsVector.toArray(new Account[addAccountsVector.size()]));
            }
            if (isServer()) {
                for (Account account : accounts) {
                    if (contest.isLocalLoggedIn(account.getClientId())) {
                        Packet updatePacket;
                        if (account.getClientId().getClientType().equals(ClientType.Type.TEAM)) {
                            updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), account.getClientId(), contest.getAccount(account.getClientId()));
                        } else {
                            updatePacket = PacketFactory.clonePacket(contest.getClientId(), account.getClientId(), packet);
                        }
                        controller.sendToClient(updatePacket);
                    }
                }
            }
        }
        
        ClientSettings clientSettings = (ClientSettings)PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS);
        if (clientSettings != null) {
            contest.addClientSettings(clientSettings);
            
            ClientId toId = clientSettings.getClientId();
            if (isJudge(toId)) {
                // judge settings update send to judges and admins with auto judge settings (too)
                try {
                    // Only send to other servers if this client is at this site
                    // otherwise just send to judges and admins
                    sendToJudgesAndOthers(packet, isThisSite(toId));  
                } catch (Exception e) {
                    controller.getLog().log(Log.WARNING, "Exception logged ", e);
                }
            }

            if (contest.isLocalLoggedIn(clientSettings.getClientId())) {
                try {
                    Packet newSettingsPacket = PacketFactory.clonePacket(contest.getClientId(), toId, packet);
                    controller.sendToClient(newSettingsPacket);
                } catch (Exception e) {
                    controller.getLog().log(Log.WARNING, "Exception logged ", e);
                }
            }
        }
        
        if (isServer()) {
            
            controller.writeConfigToDisk();
            
            Packet addPacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
            boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
            sendToJudgesAndOthers(addPacket, sendToOtherServers);
            
            if (sendToTeams) {
                controller.sendToTeams(addPacket);
            }
        }
    }

    /**
     * Handle a UPDATE_SETTING packet.
     * 
     * @param packet
     */
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

        Group group = (Group) PacketFactory.getObjectValue(packet, PacketFactory.GROUP);
        if (group != null) {
            contest.updateGroup(group);
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
        
        BalloonSettings balloonSettings = (BalloonSettings) PacketFactory.getObjectValue(packet, PacketFactory.BALLOON_SETTINGS);
        if (balloonSettings != null) {
            contest.updateBalloonSettings(balloonSettings);
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
            // TODO change these from Vector to something lightweight
            Vector<Account> addAccountsVector = new Vector<Account>();
            Vector<Account> updateAccountsVector = new Vector<Account>();
            for (Account account : accounts) {
                // split this into 2 lists,  then call the bulk version
                if (contest.getAccount(account.getClientId()) == null) {
                    addAccountsVector.add(account);
                } else {
                    // existing account
                    updateAccountsVector.add(account);
                }
            }
            if (addAccountsVector.size() > 0) {
                contest.addAccounts(addAccountsVector.toArray(new Account[addAccountsVector.size()]));
            }
            if (updateAccountsVector.size() > 0) {
                contest.updateAccounts(updateAccountsVector.toArray(new Account[updateAccountsVector.size()]));
            }
            if (isServer()) {
                for (Account account : accounts) {
                    if (contest.isLocalLoggedIn(account.getClientId())) {
                        Packet updatePacket;
                        if (account.getClientId().getClientType().equals(ClientType.Type.TEAM)) {
                            updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), account.getClientId(), contest.getAccount(account.getClientId()));
                        } else {
                            updatePacket = PacketFactory.clonePacket(contest.getClientId(), account.getClientId(), packet);
                        }
                        controller.sendToClient(updatePacket);
                    }
                }
            }
        }
        
        ContestInformation contestInformation = (ContestInformation) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_INFORMATION);
        if (contestInformation != null) {
            contest.updateContestInformation(contestInformation);
            sendToTeams = true;
        }
        
        ClientSettings clientSettings = (ClientSettings)PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS);
        if (clientSettings != null){
            contest.updateClientSettings(clientSettings);
            if (isServer()) {

                ClientId toId = clientSettings.getClientId();
                if (isJudge(toId)) {
                    // judge settings update send to judges and admins with auto judge settings (too)
                    try {
                        // Only send to other servers if this client is at this site
                        // otherwise just send to judges and admins
                        sendToJudgesAndOthers(packet, isThisSite(toId));  
                    } catch (Exception e) {
                        controller.getLog().log(Log.WARNING, "Exception logged ", e);
                    }
                }
                
                if (contest.isLocalLoggedIn(clientSettings.getClientId())) {
                    Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), toId, packet);
                    controller.sendToClient(updatePacket);
                }
            }
        }

        if (isServer()) {
            
            controller.writeConfigToDisk();

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

        if (isServer()){
            // If I am a server forward to clients on this site.
            
            controller.sendToAdministrators(packet);
            controller.sendToJudges(packet);
            controller.sendToScoreboards(packet);
            if (sendToServers) {
                controller.sendToServers(packet);
            }
        } else {
            info("Warning - tried to send packet to others (as non server) "+packet);
            Exception ex = new Exception("User "+packet.getSourceId()+" tried to send packet to judges and others");
            controller.getLog().log(Log.WARNING, "Warning - tried to send packet to others (as non server) "+packet, ex);
        }
    }

    private boolean isSuperUser(ClientId id) {
        return id.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }
    
    public void cancelRun(Packet packet, Run run, ClientId whoCanceledRun, ConnectionHandlerID connectionHandlerID) {

        if (isServer()) {

            if (!isThisSite(run)) {

                controller.sendToRemoteServer(run.getSiteNumber(), packet);

            } else {

                // TODO handle Security violation
                
                /**
                 * If there is a problem then there is no requirement (due to lack of analysis)
                 * to notify the client canceling the run.
                 */
                
                // TODO do we send something back to client if unable to cancel run ? Or just be silent?

                try {
                    contest.cancelRunCheckOut(run, whoCanceledRun);
                    Run availableRun = contest.getRun(run.getElementId());
                    Packet availableRunPacket = PacketFactory.createRunAvailable(contest.getClientId(), whoCanceledRun, availableRun);
                    sendToJudgesAndOthers(availableRunPacket, true);
                    
                } catch (UnableToUncheckoutRunException e) {
                    
                    controller.getLog().log(Log.WARNING, "Security Warning " + e.getMessage(), e);

                    // Send Security warning to all admins and servers

                    Packet violationPacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, e.getMessage(), 
                            whoCanceledRun, connectionHandlerID, null, packet);

                    controller.sendToAdministrators(violationPacket);
                    controller.sendToServers(violationPacket);
                }
            }

        } else {
            contest.updateRun(run, whoCanceledRun);
        }
    }
    

    /**
     * UN checkout or cancel clarification checkout.
     * 
     * @param packet
     * @param connectionHandlerID 
     * @throws ContestSecurityException 
     */
    public void cancelClarificationCheckOut(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        ClientId whoCancelledIt = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {

            if (!isThisSite(clarification)) {

                ClientId destinationId = new ClientId(clarification.getSiteNumber(), ClientType.Type.SERVER, 0);
                Packet cancelPacket = PacketFactory.clonePacket(contest.getClientId(), destinationId, packet);
                controller.sendToRemoteServer(clarification.getSiteNumber(), cancelPacket);

            } else {
                // This site's clarification
                
                // TODO securityCheck cancelClarificationCheckOut
//                securityCheck(Permission.Type.ANSWER_CLARIFICATION, whoCancelledIt, connectionHandlerID);

                contest.cancelClarificationCheckOut(clarification, whoCancelledIt);

                Clarification theClarification = contest.getClarification(clarification.getElementId());

                Packet cancelPacket = PacketFactory.createClarificationAvailable(contest.getClientId(), PacketFactory.ALL_SERVERS, theClarification);

                if (isServer()) {
                    sendToJudgesAndOthers(cancelPacket, true);
                }
            }
        } else {
            contest.cancelClarificationCheckOut(clarification, whoCancelledIt);
        }
    }
    

    private void sendClarificationAvailable(Packet packet) {
        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        ClientId whoCancelledIt = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {
            contest.cancelClarificationCheckOut(clarification, whoCancelledIt);
            sendToJudgesAndOthers(packet, false);
        } else {
            contest.cancelClarificationCheckOut(clarification, whoCancelledIt);
        }

    }
    
    private void answerClarification(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        ClientId whoAnsweredIt = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {

            if (!isThisSite(clarification)) {

                ClientId destinationId = new ClientId(clarification.getSiteNumber(), ClientType.Type.SERVER, 0);
                Packet answerPacket = PacketFactory.clonePacket(contest.getClientId(), destinationId, packet);
                controller.sendToRemoteServer(clarification.getSiteNumber(), answerPacket);

            } else {
                // This site's clarification
                
                securityCheck(Permission.Type.ANSWER_CLARIFICATION, whoAnsweredIt, connectionHandlerID);

                contest.answerClarification(clarification, clarification.getAnswer(), whoAnsweredIt, clarification.isSendToAll());
                Clarification theClarification = contest.getClarification(clarification.getElementId());
                Packet answerPacket = PacketFactory.createAnsweredClarificationUpdate(contest.getClientId(), PacketFactory.ALL_SERVERS, theClarification, theClarification.getAnswer(), whoAnsweredIt);

                sendToJudgesAndOthers(answerPacket, true);
                if (clarification.isSendToAll()) {
                    controller.sendToTeams(answerPacket);
                } else {
                    Packet answerForTeamPacket = PacketFactory.clonePacket(contest.getClientId(), clarification.getSubmitter(), answerPacket);
                    controller.sendToClient(answerForTeamPacket);
                }

            }
        } else {
            contest.answerClarification(clarification, clarification.getAnswer(), whoAnsweredIt, clarification.isSendToAll());
        }
    }


    /**
     * Judge a run
     * @param run
     * @param judgementRecord
     * @param runResultFiles
     * @param whoJudgedId
     * @param connectionHandlerID 
     * @throws ContestSecurityException 
     */
    protected void judgeRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedId, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        if (isServer()) {

            if (!isThisSite(run)) {

                Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), run, judgementRecord, runResultFiles);
                controller.sendToRemoteServer(run.getSiteNumber(), judgementPacket);

            } else {
                // This site's run
                
                securityCheck (Permission.Type.JUDGE_RUN, whoJudgedId, connectionHandlerID);

                judgementRecord.setWhenJudgedTime(contest.getContestTime().getElapsedMins());

                contest.addRunJudgement(run, judgementRecord, runResultFiles, whoJudgedId);

                Run theRun = contest.getRun(run.getElementId());

                if (judgementRecord.isComputerJudgement()) {
                    if(contest.getProblem(theRun.getProblemId()).isPrelimaryNotification()) {
                        Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), theRun, judgementRecord, runResultFiles);
                        controller.sendToClient(judgementPacket);
                    }
                }
                else {
                    Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), theRun, judgementRecord, runResultFiles);
                    if (judgementRecord.isSendToTeam()) {
                        // Send to team who sent it, send to other server if needed.
                        controller.sendToClient(judgementPacket);
                    }
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
     * @throws ContestSecurityException 
     */
    private void requestRun(Packet packet, Run run, ClientId whoRequestsRunId, ConnectionHandlerID connectionHandlerID, boolean computerJudge) throws ContestSecurityException {
        fetchRun(packet,run,whoRequestsRunId, false, computerJudge, connectionHandlerID);
    }
    
    private void requestClarification(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {
        ElementId clarificationId = (ElementId) PacketFactory.getObjectValue(packet, PacketFactory.REQUESTED_CLARIFICATION_ELEMENT_ID);
        ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
//        Boolean readOnly = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.READ_ONLY);
        boolean readOnly = false;

        if (isServer()) {

            Clarification clarification = contest.getClarification(clarificationId);
            if (!isThisSite(clarification)) {

                ClientId serverClientId = new ClientId(clarification.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (contest.isLocalLoggedIn(serverClientId)) {

                    // send request to remote server
                    Packet requestPacket = PacketFactory.clonePacket(contest.getClientId(), serverClientId, packet);
                    controller.sendToRemoteServer(clarification.getSiteNumber(), requestPacket);

                } else {

                    // send NOT_AVAILABLE back to client
                    Packet notAvailableRunPacket = PacketFactory.createClarificationNotAvailable(contest.getClientId(), requestFromId, clarification, requestFromId);
                    controller.sendToClient(notAvailableRunPacket);
                }

            } else {
                Clarification theClarification = contest.getClarification(clarification.getElementId());
                // This Site's clar, if we can check it out and send to client

                if (readOnly) {
                    // just get run and sent it to them.

                    // TODO  send read only clar to them
                    info("requestClarification read-only not implemented, yet");
                } else {
                    try {
                        securityCheck (Permission.Type.ANSWER_CLARIFICATION, requestFromId, connectionHandlerID);
                        
                        theClarification = contest.checkoutClarification(clarification, requestFromId);
    
                        // send to Judge
                        Packet checkOutPacket = PacketFactory.createCheckedOutClarification(contest.getClientId(), requestFromId, theClarification, requestFromId);
                        controller.sendToClient(checkOutPacket);
    
                        // TODO change this packet type so it is not confused with the actual checked out run.
    
                        sendToJudgesAndOthers(checkOutPacket, true);
                    } catch (ClarificationUnavailableException clarUnavailableException) {
                        controller.getLog().info("clarUnavailableException "+clarUnavailableException.getMessage());
                        Packet notAvailableRunPacket = PacketFactory.createClarificationNotAvailable(contest.getClientId(), requestFromId, clarification, requestFromId);
                        controller.sendToClient(notAvailableRunPacket);
                    }
                }
            }
        } else {

            throw new SecurityException("requestClarification - sent to client " + contest.getClientId());
        }
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
     * @param connectionHandlerID 
     * @throws ContestSecurityException 
     */
    private void fetchRun(Packet packet, Run run, ClientId whoRequestsRunId, boolean readOnly, boolean computerJudge, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        if (isServer()) {

            if (!isThisSite(run)) {

                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (contest.isLocalLoggedIn(serverClientId)) {

                    // send request to remote server
                    Packet requestPacket = PacketFactory.createRunRequest(contest.getClientId(), serverClientId, run, whoRequestsRunId, readOnly, computerJudge);
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
                    
                    RunResultFiles[] runResultFiles = contest.getRunResultFiles(run);
                    

                    // send to Judge
                    Packet checkOutPacket = PacketFactory.createCheckedOutRun(contest.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId, runResultFiles);
                    controller.sendToClient(checkOutPacket);

                } else {
                    
                    try {
                        securityCheck (Permission.Type.JUDGE_RUN, whoRequestsRunId, connectionHandlerID);
                        
                        theRun = contest.checkoutRun(run, whoRequestsRunId, false, computerJudge);
                        
                        RunFiles runFiles = contest.getRunFiles(run);
                        if (runFiles == null) {
                            try {
                                contest.cancelRunCheckOut(run, whoRequestsRunId);
                            } catch (UnableToUncheckoutRunException e) {
                                controller.getLog().severe("Problem canceling run checkout after error getting run files.");
                            }
                            throw new RunUnavailableException("Error retrieving files.");
                        }

                        RunResultFiles[] runResultFiles = contest.getRunResultFiles(run);
                        // send to Judge
                        Packet checkOutPacket = PacketFactory.createCheckedOutRun(contest.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId, runResultFiles);
                        controller.sendToClient(checkOutPacket);

                        // TODO change this packet type so it is not confused with the actual checked out run.
                        
                        sendToJudgesAndOthers(checkOutPacket, true);
                    } catch (RunUnavailableException runUnavailableException) {
                        controller.getLog().info("runUnavailableException "+runUnavailableException.getMessage());
                        theRun = contest.getRun(run.getElementId());
                        Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(contest.getClientId(), whoRequestsRunId, theRun);
                        controller.sendToClient(notAvailableRunPacket);
                    }
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
    private void addAllRunsToModel(Packet packet) {

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
    private void addAllClarificationsToModel(Packet packet) {

        try {
            Clarification[] clarifications = (Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {

                    if ( (!isServer()) || (!isThisSite(clarification))) {
                        if (contest.getClarification(clarification.getElementId()) != null){
                            contest.updateClarification(clarification, null);
                        } else {
                            contest.addClarification(clarification);
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
     * Unpack and add list of accounts to contest.
     * 
     * @param packet
     */
    private void addAllAccountsToModel (Packet packet) {

        try {
            
            Account [] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
            if (accounts != null) {
                for (Account account : accounts) {
                    
                    if (isServer()){
                        if (! isThisSite(account)) {
                            contest.updateAccount(account);
                        }
                    } else {
                        contest.updateAccount(account);
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
    private void addAllConnectionIdsToModel(Packet packet) {

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
     * Add Logins into model.
     * 
     * @param packet
     */
    private void addLoginsToModel(Packet packet) {
        
        try {
            ClientId [] clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOGGED_IN_USERS);
            if (clientIds != null){
                for (ClientId clientId : clientIds){
                    if ( isServer()) {
                        // Is a server, only add remote logins
                        
                        if (! contest.isLocalLoggedIn(clientId) && !isThisSite(clientId.getSiteNumber())) {
                            // Only add into remote list on server, if they are not already logged in
                            // TODO someday soon load logins with their connectionIds
                            ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite"+clientId.getSiteNumber()+clientId);
                            
                            info("addLoginsToModel: Adding remote login "+clientId);
                            contest.addRemoteLogin(clientId, fakeId);
                        }
                        
                    } else {
                        // Not a server InternalController - add everything
                        
                        // TODO someday soon load logins with their connectionIds
                        ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber()+clientId);
                        contest.addRemoteLogin(clientId, fakeId);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }

    }
    

    @SuppressWarnings("unused")
    private void dumpClientList(ClientId[] clientIds, String comment) {
        if (clientIds == null || clientIds.length == 0) {
            info(comment + " no clients in list ");
        } else {
            Arrays.sort(clientIds, new ClientIdComparator());
            for (ClientId clientId : clientIds) {
                info(comment + " " + clientId);
            }
        }
    }

    private boolean isThisSite(Account account) {
        return account.getSiteNumber() == contest.getSiteNumber();
    }
    
    /**
     * Loads only the settings from the remote server into this server.
     * @param packet
     * @param connectionHandlerID
     */
    private void loadSettingsFromRemoteServer(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        
        int remoteSiteNumber = packet.getSourceId().getSiteNumber();
        
        addRemoteContestTimesToModel(packet, remoteSiteNumber);
        
//        updateSitesToModel(packet);
        
        addRemoteRunsToModel(packet, remoteSiteNumber);
        
        addRemoteClarificationsToModel(packet, remoteSiteNumber);
        
        addRemoteAccountsToModel(packet, remoteSiteNumber);
        
        // difficult to know which site these connections are for...
//        addConnectionIdsToModel(packet);
        
        addRemoteAllClientSettingsToModel (packet, remoteSiteNumber);
        
        addRemoteLoginsToModel (packet, remoteSiteNumber);
        
        if (isServer()){
            loginToOtherSites(packet);
        }
    }

   

    /**
     * Add logins from remote site.
     * @param packet
     * @param remoteSiteNumber
     */
    private void addRemoteLoginsToModel(Packet packet, int remoteSiteNumber) {

        try {

            ClientId[] clientIds = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOGGED_IN_USERS);
            if (clientIds != null) {
                for (ClientId clientId : clientIds) {
                    if (isServer(clientId)) {

                        if (!contest.isLocalLoggedIn(clientId)) {

                            if (!isThisSite(clientId.getSiteNumber())) {

                                // Only add into remote list on server, if they are not already logged in

                                // TODO someday soon load logins with their connectionIds
                                ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + clientId);

                                info("Adding remote login " + clientId);
                                contest.addRemoteLogin(clientId, fakeId);
                            }
                        }
                    } else if (remoteSiteNumber == clientId.getSiteNumber()) {
                        // TODO someday soon load logins with their connectionIds
                        ConnectionHandlerID fakeId = new ConnectionHandlerID("FauxSite" + clientId.getSiteNumber() + "-" + clientId);
                        contest.addRemoteLogin(clientId, fakeId);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }
    
    private void addRemoteAllClientSettingsToModel(Packet packet, int remoteSiteNumber) {

        try {
            ClientSettings[] clientSettings = (ClientSettings[]) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS_LIST);
            if (clientSettings != null) {
                for (ClientSettings clientSettings2 : clientSettings) {

                    if (remoteSiteNumber == clientSettings2.getSiteNumber()){
                        contest.updateClientSettings(clientSettings2);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }

    private void addRemoteAccountsToModel(Packet packet, int remoteSiteNumber) {
        try {

            Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
            if (accounts != null) {
                for (Account account : accounts) {
                    if (remoteSiteNumber == account.getSiteNumber()) {
                        contest.updateAccount(account);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    
    }

    private void addRemoteClarificationsToModel(Packet packet, int remoteSiteNumber) {
        try {
            Clarification[] clarifications = (Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {

                    if (remoteSiteNumber == clarification.getSiteNumber()) {
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
     * @param packet
     * @param remoteSiteNumber 
     */
    private void addRemoteRunsToModel(Packet packet, int remoteSiteNumber) {

        try {
            Run[] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
            if (runs != null) {
                for (Run run : runs) {
                    if ( remoteSiteNumber == run.getSiteNumber()) {
                        // TODO will this update work properly ?
                        contest.updateRun(run, packet.getSourceId());
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    
    private void addRemoteContestTimesToModel (Packet packet, int remoteSiteNumber) {
        try {
            ContestTime[] contestTimes = (ContestTime[]) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME_LIST);
            if (contestTimes != null) {
                for (ContestTime contestTime : contestTimes) {
                    if (remoteSiteNumber == contestTime.getSiteNumber()) {
                        // Update only other site's time
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
     * <li> {@link PacketFactory#GROUP_LIST}
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

        addSitesToModel(packet);
        
        if (isServer()){
            // Load local settings and initialize settings if necessary
            controller.initializeServer();
        }

        addLanguagesToModel(packet);
        
        addProblemsToModel(packet);

        addGroupsToModel(packet);

        try {
            Judgement[] judgements = (Judgement[]) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_LIST);
            if (judgements != null) {
                for (Judgement judgement : judgements) {
                    if (contest.getJudgement(judgement.getElementId()) != null){
                        contest.updateJudgement(judgement);
                    } else {
                        contest.addJudgement(judgement);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        try {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            if (contestTime != null) {
                if (isServer()) {
                    if ( isThisSite(contestTime.getSiteNumber())) {
                        controller.setContestTime (contestTime);
                    } else {
                        if (contest.getContestTime(contestTime.getSiteNumber()) == null) {
                            contest.addContestTime(contestTime);
                        } else {
                            contest.updateContestTime(contestTime);
                        }
                    }
                } else {
                    if (contest.getContestTime(contestTime.getSiteNumber()) == null) {
                        contest.addContestTime(contestTime);
                    } else {
                        contest.updateContestTime(contestTime);
                    }
                }
            }

        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
        
        try {
            ContestInformation contestInformation = (ContestInformation) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_INFORMATION);
            if (contestInformation != null) {
                contest.updateContestInformation(contestInformation);
            }

        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        addAllClientSettingsToModel (packet);
        
        initializeContestTime();

        addAllContestTimesToModel(packet);

        addAllRunsToModel(packet);

        addAllClarificationsToModel(packet);
        
        addAllAccountsToModel (packet);
        
        addAllConnectionIdsToModel (packet);
        
        addLoginsToModel(packet);
        
        addBalloonSettingsToModel (packet);
        
        
        try {
            Problem generalProblem = (Problem) PacketFactory.getObjectValue(packet, PacketFactory.GENERAL_PROBLEM);
            if (generalProblem != null) {
                contest.setGeneralProblem(generalProblem);
            }

        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged in General Problem ", e);
        }

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
            if (isServer()){
                loginToOtherSites(packet);
            }
        } else {
            String message = "Trouble logging in, check logs";
            contest.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }
        
        try {
            if (evaluationLog == null && isServer()) {
                Utilities.insureDir(Log.LOG_DIRECTORY_NAME);
                // this not only opens the log but registers this class to handle all run events.
                evaluationLog = new EvaluationLog(Log.LOG_DIRECTORY_NAME + File.separator + "evals.log", contest, controller);
                evaluationLog.getEvalLog().println("# Log opened " + new Date());
            }
        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }

    }

    private void addLanguagesToModel(Packet packet) {

        try {
            Language[] languages = (Language[]) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE_LIST);
            if (languages != null) {
                for (Language language : languages) {
                    if (contest.getLanguage(language.getElementId()) != null){
                        contest.updateLanguage(language);
                    } else {
                        contest.addLanguage(language);
                   }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        
    }

    private void addGroupsToModel(Packet packet) {

        try {
            Group[] groups = (Group[]) PacketFactory.getObjectValue(packet, PacketFactory.GROUP_LIST);
            if (groups != null) {
                for (Group group : groups) {
                    if (contest.getGroup(group.getElementId()) != null){
                        contest.updateGroup(group);
                    } else {
                        contest.addGroup(group);
                   }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        
    }

    /**
     * Initialize this sites contest time.
     *
     */
    private void initializeContestTime() {
        
        if (isServer()) {
            if (contest.getContestTime() == null) {
                ContestTime contestTime = new ContestTime(contest.getSiteNumber());
                contest.addContestTime(contestTime);
                info("Initialized contest time "+contestTime.getRemainingTimeStr()+" for site "+contestTime.getSiteNumber());
            }
        }
    }

    /**
     * Add Balloon Settings to model.
     * @param packet
     */
    private void addBalloonSettingsToModel(Packet packet) {
        try {
            BalloonSettings[] balloonSettings = (BalloonSettings[]) PacketFactory.getObjectValue(packet, PacketFactory.BALLOON_SETTINGS_LIST);
            if (balloonSettings != null) {
                for (BalloonSettings balloonSettings2 : balloonSettings) {
                    contest.updateBalloonSettings(balloonSettings2);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }

    /**
     * 
     * @param packet
     */
    private void addAllClientSettingsToModel(Packet packet) {
        try {
            ClientSettings[] clientSettings = (ClientSettings[]) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS_LIST);
            if (clientSettings != null) {
                for (ClientSettings clientSettings2 : clientSettings) {

                    ClientId clientId = clientSettings2.getClientId();
                    
                    if (isServer()) {
                        if (!isThisSite(clientId)) {
                            // only add settings if NOT this site.
                            contest.updateClientSettings(clientSettings2);
                        }
                    } else {
                        contest.updateClientSettings(clientSettings2);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
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
                    
                    ProblemDataFiles problemDataFiles = (ProblemDataFiles) problemDataFilesList.get(problem);
                    if (contest.getProblem(problem.getElementId()) != null) {
                        if (problemDataFiles == null){
                            contest.updateProblem(problem);
                        } else {
                            contest.updateProblem(problem, problemDataFiles);
                        }
                    } else {
                        if (problemDataFiles == null){
                            contest.addProblem(problem);
                        } else {
                            contest.addProblem(problem, problemDataFiles);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }
    
  
    private void addAllContestTimesToModel(Packet packet) {
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
     * Unconditionally add sites to model.
     * 
     * @param packet
     * @param contest
     */
    private void addSitesToModel(Packet packet) {
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    info("addSitesToModel " + site);
                    contest.updateSite(site);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING, "Exception logged ", e);
        }
    }

    /**
     * Login to all other servers.
     * 
     * @param packet
     *            contains list of other servers
     */
    private void loginToOtherSites(Packet packet) {
        
        dumpServerLoginLists("loginToOtherSites");
        
        for (Site site : contest.getSites()) {
            if (!isThisSite(site.getSiteNumber())) {
                try {

                    ClientId serverClientId = new ClientId(site.getSiteNumber(), ClientType.Type.SERVER, 0);
                    if (contest.isRemoteLoggedIn(serverClientId)) {
                        controller.sendServerLoginRequest(site.getSiteNumber());
                    } else if (contest.isLocalLoggedIn(serverClientId)){
                        info("Not logging into site " + site.getSiteNumber() + ", site already logged in");
                    } else {
                        info("Not logging into site " + site.getSiteNumber() + ", site is not connected to contest.");
                    }
                } catch (Exception e) {
                    controller.getLog().log(Log.WARNING, "Exception logging into other site ", e);
                }
            }
        }
    }
    
    /**
     * Return all accounts for all sites.
     * 
     * @return Array of all accounts in contest.
     */
    private Account[] getAllAccounts() {

        Vector<Account> allAccounts = new Vector<Account>();

        for (ClientType.Type ctype : ClientType.Type.values()) {
            if (contest.getAccounts(ctype).size() > 0) {
                Vector<Account> accounts = contest.getAccounts(ctype);
                allAccounts.addAll(accounts);
            }
        }

        Account[] accountList = (Account[]) allAccounts.toArray(new Account[allAccounts.size()]);
        return accountList;
    }
    
    /**
     * Return an array of all logged in users.
     * 
     * @return array of clientId's.
     */
    private ClientId [] getAllLoggedInUsers() {
        
        Vector<ClientId> clientList = new Vector<ClientId>();
        
        dumpServerLoginLists("getAllLoggedInUsers");

        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId [] users = contest.getAllLoggedInClients(ctype);
            for (ClientId clientId : users){
                clientList.addElement(clientId);
            }
        }
        if (clientList.size() == 0) {
            return new ClientId[0];
        } else {
            ClientId [] clients = (ClientId[]) clientList.toArray(new ClientId[clientList.size()]);
            return clients;
        }
    }
    
    Packet createContestSettingsPacket (ClientId clientId, Packet packet){
        return PacketFactory.createContestSettingsPacket(contest.getClientId(), clientId, packet);
    }
    
    public Packet createContestSettingsPacket (ClientId clientId) {
        return PacketFactory.createContestSettingsPacket(contest.getClientId(), clientId, createLoginSuccessPacket(clientId, null));
    }
    
    /**
     * Create a login success packet.
     * 
     * 
     * @param clientId
     * @return Packet containing contest settings
     */
    public Packet createLoginSuccessPacket (ClientId clientId, String contestSecurityPassword){

        Run[] runs = null;
        Clarification[] clarifications = null;
        ProblemDataFiles [] problemDataFiles = new ProblemDataFiles[0];
        ClientSettings [] clientSettings = null;
        Account[] accounts = null;
        Site[] sites = null;
        
        if (contest.getClientSettings(clientId) == null){
            ClientSettings clientSettings2 = new ClientSettings(clientId);
            clientSettings2.put("LoginDate", new Date().toString());
            contest.addClientSettings(clientSettings2);
        }
        
        /**
         * This is where client specific settings are created before
         * sending them to client.
         */

        if (clientId.getClientType().equals(ClientType.Type.TEAM)) {
            runs = contest.getRuns(clientId);
            clarifications = contest.getClarifications(clientId);
            clientSettings = new ClientSettings[1];
            clientSettings[0] = contest.getClientSettings(clientId);
            accounts = new Account[1];
            accounts[0] = contest.getAccount(clientId);
            // re-build the site list without passwords, all they really need is the number & name
            Site[] realSites = contest.getSites();
            sites = new Site[realSites.length];
            for (int i = 0; i < realSites.length; i++) {
                sites[i] = new Site(realSites[i].getDisplayName(), realSites[i].getSiteNumber());
            }
        } else {
            runs = contest.getRuns();
            clarifications = contest.getClarifications();
            problemDataFiles = contest.getProblemDataFiles();
            clientSettings = contest.getClientSettingsList();
            accounts = getAllAccounts();
            sites = contest.getSites();
        }

        ContestLoginSuccessData contestLoginSuccessData = new ContestLoginSuccessData();
        contestLoginSuccessData.setAccounts(accounts);
        contestLoginSuccessData.setBalloonSettingsArray(contest.getBalloonSettings());
        contestLoginSuccessData.setClarifications(clarifications);
        contestLoginSuccessData.setClientSettings(clientSettings);
        contestLoginSuccessData.setConnectionHandlerIDs(contest.getConnectionHandleIDs());
        contestLoginSuccessData.setContestTimes(contest.getContestTimes()); 
        contestLoginSuccessData.setGroups(contest.getGroups());
        contestLoginSuccessData.setJudgements(contest.getJudgements());
        contestLoginSuccessData.setLanguages(contest.getLanguages());
        contestLoginSuccessData.setLoggedInUsers(getAllLoggedInUsers());
        contestLoginSuccessData.setProblemDataFiles(problemDataFiles);
        contestLoginSuccessData.setProblems(contest.getProblems());
        contestLoginSuccessData.setRuns(runs);
        contestLoginSuccessData.setSites(sites);
        contestLoginSuccessData.setGeneralProblem(contest.getGeneralProblem());
        
        if (isServer(clientId)){
            contestLoginSuccessData.setContestSecurityPassword(contestSecurityPassword);
        }
        
        Packet loginSuccessPacket = PacketFactory.createLoginSuccess(contest.getClientId(), clientId, contest.getContestTime(), 
                contest.getSiteNumber(), contest.getContestInformation(), contestLoginSuccessData);
     
        return loginSuccessPacket;
    }


    /**
     * Is the input ClientId a server.
     * 
     * @param id
     * @return
     */
    private boolean isServer(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.SERVER);
    }
    
    private boolean isAdministrator() {
        ClientId id = contest.getClientId();
        return id != null && id.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }
    
    private boolean isJudge(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.JUDGE);
    }
    
    /**
     * Is this client a server.
     * @return true if is a server.
     */
    private boolean isServer(){
        return isServer(contest.getClientId());
    }
    
    public void info(String s) {
//        System.err.flush();
        controller.getLog().info(s);
//        System.err.println(Thread.currentThread().getName() + " " + s);
//        System.err.flush();
    }

    public void info(String s, Exception exception) {
//        System.err.flush();
        controller.getLog().log (Log.INFO, s, exception);
//        System.err.println(Thread.currentThread().getName() + " " + s);
//        System.err.flush();
//        exception.printStackTrace(System.err);
    }
 
}
