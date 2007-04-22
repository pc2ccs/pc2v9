package edu.csus.ecs.pc2.core;

import java.util.Vector;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.ISubmission;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
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
 * Process packets. In {@link #handlePacket(IController, IModel, Packet, ConnectionHandlerID) handlePacket} a packet is unpacked, model is updated, and controller used to send packets as needed.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class PacketHandler {

    private IModel model;

    private IController controller;

    public PacketHandler(IController controller, IModel model) {
        this.controller = controller;
        this.model = model;
    }

    /**
     * Take each input packet, update the model, send out packets as needed.
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

        } else if (packetType.equals(Type.RUN_SUBMISSION_CONFIRM)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.addRun(run);
            sendToJudgesAndOthers( packet, isThisSite(run));

        } else if (packetType.equals(Type.RUN_SUBMISSION)) {
            // RUN submitted by team to server

            Run submittedRun = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            Run run = model.acceptRun(submittedRun, runFiles);

            // Send to team
            Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(model.getClientId(), fromId, run);
            controller.sendToClient(confirmPacket);

            // Send to clients and servers
            sendToJudgesAndOthers( confirmPacket, true);
            
        } else if (packetType.equals(Type.CLARIFICATION_SUBMISSION)) {
            // Clarification submitted by team to server
            
            Clarification submittedClarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            Clarification clarification = model.acceptClarification(submittedClarification);
            
            // Send to team
            Packet confirmPacket = PacketFactory.createClarSubmissionConfirm(model.getClientId(), fromId, clarification);
            controller.sendToClient(confirmPacket);
            
            // Send to clients and other servers
            sendToJudgesAndOthers(confirmPacket, true);

        } else if (packetType.equals(Type.CLARIFICATION_SUBMISSION_CONFIRM)) {
            Clarification clarification = (Clarification)  PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
            model.addClarification(clarification);
            sendToJudgesAndOthers( packet, isThisSite(clarification));
            
        } else if (packetType.equals(Type.LOGIN_FAILED)) {
            String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);

        } else if (packetType.equals(Type.RUN_NOTAVAILABLE)) {
            // Run not available from server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.runNotAvailable(run);

            sendToJudgesAndOthers( packet, isThisSite(run));

        } else if (packetType.equals(Type.RUN_AVAILABLE)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.availableRun(run);

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
            
        } else if (packetType.equals(Type.RUN_UNCHECKOUT)) {
            // Cancel run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId whoCanceledId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            cancelRun(packet, run,  whoCanceledId);

        } else if (packetType.equals(Type.START_CONTEST_CLOCK)) {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            ClientId sourceServerId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            startContest(contestTime,  contestTime.getSiteNumber(), sourceServerId);

        } else if (packetType.equals(Type.STOP_CONTEST_CLOCK)) {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            ClientId sourceServerId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            stopContest(contestTime,  contestTime.getSiteNumber(), sourceServerId);

        } else if (packetType.equals(Type.ADD_SETTING)) {
            addNewSetting(packet);
            
        } else if (packetType.equals(Type.GENERATE_ACCOUNTS)) {
            generateAccounts (packet);

        } else if (packetType.equals(Type.UPDATE_SETTING)) {
            updateSetting(packet);

        } else if (packetType.equals(Type.RUN_CHECKOUT)) {
            // Run from server to judge
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            model.addRun(run, runFiles, packet.getDestinationId());
            
            sendToJudgesAndOthers( packet, false);

        } else if (packetType.equals(Type.RUN_REQUEST)) {
            // Request Run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            requestRun(packet, run,  requestFromId);

        } else if (packetType.equals(Type.LOGOUT)) {
            logoutClient(packet);

        } else if (packetType.equals(Type.LOGIN)) {
            loginClient(packet); 
            
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {

            if (isServer(packet.getDestinationId())) {
                /**
                 * Add the originating server into login list, if this client is a server.
                 */
                model.addLogin(fromId, connectionHandlerID);
            }

            if (!model.isLoggedIn()) {
                info(" handlePacket original LOGIN_SUCCESS before ");
                loadDataIntoModel(packet, connectionHandlerID);
                info(" handlePacket original LOGIN_SUCCESS after -- all settings loaded ");
            } else {
                info(" handlePacket LOGIN_SUCCESS - from another site, no update of contest data: " + packet);
            }

        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            controller.getLog().log(Log.WARNING,"Unhandled Packet ", exception);
        }
        
        info("handlePacket start " + packet);

    }

    private void loginClient(Packet packet) {
        
        if ( model.isLoggedIn() ){
        ClientId whoLoggedIn = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        ConnectionHandlerID connectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);

        if (isServer()) {

            // TODO add to login list 
            
            sendToJudgesAndOthers(packet, false);
        } else {
            model.addLogin(whoLoggedIn, connectionHandlerID);
        }
        } else {
            info("Note: got a LOGIN packet before LOGIN_SUCCESS "+packet);
        }
        
    }

    private void logoutClient(Packet packet) {
        
        ClientId whoLoggedOff = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        if (isServer()){
            // TODO add login to login list
            
            sendToJudgesAndOthers(packet, false);
        }else{
            model.removeLogin(whoLoggedOff);
        }
    }

    /**
     * Send judgement to judges, servers, admins and boards.
     * @param packet
     */
    private void sendJudgementUpdate(Packet packet) {
        
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);

        if (isServer()){
            model.updateRun(run);
            sendToJudgesAndOthers(packet, false);
        } else {
            model.updateRun(run);
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
                Vector<Account> accountVector = model.generateNewAccounts(type.toString(), count.intValue(), startCount.intValue(), active);
                Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
                Packet newAccountsPacket = PacketFactory.createAddSetting(model.getClientId(), PacketFactory.ALL_SERVERS, accounts);
                sendToJudgesAndOthers(newAccountsPacket, true);
                
            } else {
                
                controller.sendToRemoteServer(siteNumber.intValue(), packet);
            }
            
        } else {
            throw new SecurityException("Client "+model.getClientId()+" was send generate account packet "+packet);
        }
    }

    private void startContest(ContestTime contestTime, int siteNumber, ClientId sourceServerId) {

        if (model.getClientId().getClientType().equals(ClientType.Type.SERVER)) {

            model.startContest(siteNumber);

            Packet startClockPacket = PacketFactory.createStartContestClock(sourceServerId, PacketFactory.ALL_SERVERS, contestTime);
            sendToJudgesAndOthers( startClockPacket, false);

        } else {
            model.startContest(siteNumber);
        }
    }

    private void stopContest(ContestTime contestTime, int siteNumber, ClientId sourceServerId) {

        if (model.getClientId().getClientType().equals(ClientType.Type.SERVER)) {

            model.stopContest(siteNumber);

            Packet stopClockPacket = PacketFactory.createStopContestClock(sourceServerId, PacketFactory.ALL_SERVERS, contestTime);
            sendToJudgesAndOthers( stopClockPacket, false);

        } else {
            model.stopContest(siteNumber);
        }

    }

    /**
     * Add a new setting from another server.
     * 
     * @param packet
     */
    private void addNewSetting(Packet packet) {

        Site site = (Site) PacketFactory.getObjectValue(packet, PacketFactory.SITE);
        if (site != null) {
            model.addSite(site);
            if (isServer()) {
                boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
                sendToJudgesAndOthers( packet, sendToOtherServers);
            }
        }

        Language language = (Language) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE);
        if (language != null) {
            model.addLanguage(language);
            if (isServer()) {
                boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
                sendToJudgesAndOthers( packet, sendToOtherServers);
            }
        }

        Problem problem = (Problem) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM);
        if (problem != null) {
            model.addProblem(problem);
            if (isServer()) {
                boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
                sendToJudgesAndOthers( packet, sendToOtherServers);   
            }
        }

        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
        if (contestTime != null) {
            model.addContestTime(contestTime);
            if (isServer()) {
                boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
                sendToJudgesAndOthers( packet, sendToOtherServers);   
            }
        }

        Account [] accounts = (Account []) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
        if (accounts != null) {
            for (Account account : accounts) {
                if (model.getAccount(account.getClientId()) == null) {
                    model.addAccount(account);
                }
            }
            if (isServer()) {
                sendToJudgesAndOthers(packet, false);
            }
        }
    }

    private void updateSetting(Packet packet) {

        Site site = (Site) PacketFactory.getObjectValue(packet, PacketFactory.SITE);
        if (site != null) {
            model.updateSite(site);
            if (isServer()) {
                sendToJudgesAndOthers( packet, false);
            }
        }

        Language language = (Language) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE);
        if (language != null) {
            model.updateLanguage(language);
            if (isServer()) {
                sendToJudgesAndOthers( packet, false);
            }
        }

        Problem problem = (Problem) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM);
        if (problem != null) {
            model.updateProblem(problem);
            if (isServer()) {
                sendToJudgesAndOthers( packet, false);
            }
        }

        ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
        if (contestTime != null) {
            model.updateContestTime(contestTime);
            if (isServer()) {
                sendToJudgesAndOthers( packet, false);
            }
        }

    }
    
    private boolean isThisSite(int siteNumber) {
        return siteNumber == model.getSiteNumber();
    }

    private boolean isThisSite(ISubmission submission) {
        return submission.getSiteNumber() == model.getSiteNumber();
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

        if (model.getClientId().getClientType().equals(ClientType.Type.SERVER)) {
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

    /**
     * Handle Check out run, add to model, trigger listeners.
     * 
     * @param run
     * @param runFiles
     * @param whoCheckedOutId
     */
    private void checkedOutRun( Run run, RunFiles runFiles, ClientId whoCheckedOutId) {

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

                model.cancelRunCheckOut(run, whoCanceledRun);

                Run availableRun = model.getRun(run.getElementId());
                Packet availableRunPacket = PacketFactory.createRunAvailable(model.getClientId(), whoCanceledRun, availableRun);

                sendToJudgesAndOthers( availableRunPacket, true);
            }

        } else {
            // Client, update status and done.

            model.updateRun(run, RunStates.NEW, whoCanceledRun);
        }
    }

    private void judgeRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedId) {

        if (isServer()) {

            if (!isThisSite(run)) {

                Packet judgementPacket = PacketFactory.createRunJudgement(model.getClientId(), run.getSubmitter(), run, judgementRecord, runResultFiles);
                controller.sendToRemoteServer(run.getSiteNumber(), judgementPacket);

            } else {
                // This site's run

                judgementRecord.setWhenJudgedTime(model.getContestTime().getElapsedMins());

                model.addRunJudgement(run, judgementRecord, runResultFiles, whoJudgedId);

                Run theRun = model.getRun(run.getElementId());

                Packet judgementPacket = PacketFactory.createRunJudgement(model.getClientId(), run.getSubmitter(), theRun, judgementRecord, runResultFiles);
                if (judgementRecord.isSendToTeam()) {
                    // Send to team who sent it, send to other server if needed.
                    controller.sendToClient(judgementPacket);
                }
                
                Packet judgementUpdatePacket = PacketFactory.createRunJudgmentUpdate(model.getClientId(), PacketFactory.ALL_SERVERS, theRun, whoJudgedId);
                sendToJudgesAndOthers( judgementUpdatePacket, true);
            }

        } else {
            model.updateRun(run, run.getStatus(), judgementRecord.getJudgerClientId());
        }
    }

    /**
     * Checkout run.
     * 
     * Either checks out run (marks as {@link edu.csus.ecs.pc2.core.model.Run.RunStates#BEING_JUDGED BEING_JUDGED}) and send to everyone, or send a
     * {@link edu.csus.ecs.pc2.core.packet.PacketType.Type#RUN_NOTAVAILABLE RUN_NOTAVAILABLE}.
     * @param packet 
     * 
     * @param run
     * @param whoRequestsRunId
     */
    private void requestRun(Packet packet, Run run, ClientId whoRequestsRunId) {

        if (isServer()) {

            if (!isThisSite(run)) {

                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (model.isLoggedIn(serverClientId)) {

                    // send request to remote server
                    controller.sendToRemoteServer(run.getSiteNumber(), packet);

                } else {

                    // send NOT_AVAILABLE back to client
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(model.getClientId(), whoRequestsRunId, run);
                    controller.sendToClient(notAvailableRunPacket);
                }

            } else {
                // This Site's run

                Run theRun = model.getRun(run.getElementId());
                if (run.getStatus() == RunStates.NEW || isSuperUser(whoRequestsRunId)) {

                    model.updateRun(theRun, RunStates.BEING_JUDGED, whoRequestsRunId);

                    theRun = model.getRun(run.getElementId());
                    RunFiles runFiles = model.getRunFiles(run);

                    // send to Judge
                    Packet checkOutPacket = PacketFactory.createCheckedOutRun(model.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId);
                    controller.sendToClient(checkOutPacket);

                    sendToJudgesAndOthers( checkOutPacket, true);
                } else {
                    // Unavailable
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(model.getClientId(), whoRequestsRunId, run);
                    controller.sendToClient(notAvailableRunPacket);
                }
            }
        } else {

            throw new SecurityException("requestRun - sent to client " + model.getClientId());

        }
    }

    /**
     * Unpack and add list of runs to model.
     * 
     * @param packet
     */
    private void addRunsToModel(Packet packet) {

        try {
            Run[] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
            if (runs != null) {
                for (Run run : runs) {
                    if ( (!isServer()) || (!isThisSite(run))) {
                        model.addRun(run);
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
     * Unpack and add list of clarifications to model.
     * 
     * @param packet
     */
    private void addClarificationsToModel(Packet packet) {

        try {
            Clarification[] clarifications = (Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {

                    if ( (!isServer()) || (!isThisSite(clarification))) {
                        model.addClarification(clarification);
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
     * Unpack and add list of accounts to model.
     * 
     * @param packet
     */
    private void addAccountsToModel (Packet packet) {

        try {
            
            Account [] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
            if (accounts != null) {
                for (Account account : accounts) {

                    if ( (!isServer()) || (!isThisSite(account))) {
                        model.addAccount(account);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    private boolean isThisSite(Account account) {
        return account.getSiteNumber() == model.getSiteNumber();
    }

    /**
     * Add contest data into the model.
     * 
     * This will read a packet and load the data into the model. <br>
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
                model.setClientId(clientId);
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
                    model.addLanguage(language);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        try {
            Problem[] problems = (Problem[]) PacketFactory.getObjectValue(packet, PacketFactory.PROBLEM_LIST);
            if (problems != null) {
                for (Problem problem : problems) {
                    model.addProblem(problem);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        try {
            Judgement[] judgements = (Judgement[]) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_LIST);
            if (judgements != null) {
                for (Judgement judgement : judgements) {
                    model.addJudgement(judgement);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }

        try {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            if (contestTime != null) {
                model.addContestTime(contestTime);
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

        if (model.isLoggedIn()) {

            // show main UI
            controller.startMainUI(model.getClientId());

            // Login to other sites
            loginToOtherSites(packet);
        } else {
            String message = "Trouble logging in, check logs";
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }
    }

    private void addContestTimesToModel(Packet packet) {
        try {
            ContestTime[] contestTimes = (ContestTime[]) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME_LIST);
            if (contestTimes != null) {
                for (ContestTime contestTime : contestTimes) {
                    if (model.getSiteNumber() != contestTime.getSiteNumber()) {
                        // Update other sites contestTime, do not touch ours.
                        if (model.getContestTime(contestTime.getSiteNumber()) != null) {
                            model.updateContestTime(contestTime);
                        } else {
                            model.addContestTime(contestTime);
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
     * Unpack and add a list of sites to the model.
     * 
     * @param packet
     * @param model
     */
    private void addSitesToModel(Packet packet) {
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    model.addSite(site);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
        }
    }

    /**
     * Login to other servers.
     * 
     * Sends a login request packet to sites that this server is nog logged into.
     * 
     * @param packet
     *            contains list of other servers
     */
    private void loginToOtherSites(Packet packet) {
        try {
            ClientId[] listOfLoggedInUsers = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOGGED_IN_USERS);
            for (ClientId id : listOfLoggedInUsers) {
                if (isServer(id) && !isThisSite(id.getSiteNumber())) {
                    if (!model.isLoggedIn(id)) {
                        controller.sendServerLoginRequest(id.getSiteNumber());
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            controller.getLog().log(Log.WARNING,"Exception logged ", e);
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
        return isServer(model.getClientId());
    }

    /**
     * TODO - a temporary logging routine.
     */
    public void info(String s) {
        System.err.println(s);
        controller.getLog().log(Log.INFO, s);
    }

    // TODO temporary logging routine
    public void info(String s, Exception ex) {
        System.err.println(s);
        ex.printStackTrace();
        controller.getLog().log(Log.INFO, s, ex);
    }

}
