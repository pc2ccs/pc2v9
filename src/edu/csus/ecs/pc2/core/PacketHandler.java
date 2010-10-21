package edu.csus.ecs.pc2.core;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.ClarificationUnavailableException;
import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.exception.ProfileCloneException;
import edu.csus.ecs.pc2.core.exception.ProfileException;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.exception.UnableToUncheckoutRunException;
import edu.csus.ecs.pc2.core.list.ClientIdComparator;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.list.RunFilesList;
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
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ISubmission;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.MessageEvent.Area;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileChangeStatus.Status;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunExecutionStatus;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.Packet;
import edu.csus.ecs.pc2.core.packet.PacketFactory;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;
import edu.csus.ecs.pc2.profile.ProfileManager;
import edu.csus.ecs.pc2.ui.UIPlugin;

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
     * @throws Exception 
     */
    public void handlePacket(Packet packet, ConnectionHandlerID connectionHandlerID) throws Exception {

        Type packetType = packet.getType();
        
        info("handlePacket start " + packet);
        PacketFactory.dumpPacket(controller.getLog(), packet, "handlePacket");
        if (Utilities.isDebugMode()) {
            PacketFactory.dumpPacket(System.out, packet, "debug handlePacket");
        }
        
        ClientId fromId = packet.getSourceId();

        Clarification clarification;

        switch (packetType) {
            case MESSAGE:
                PacketFactory.dumpPacket(System.err, packet, null);
                handleMessagePacket(packet);
                break;
            case RUN_SUBMISSION_CONFIRM:
                handleRunSubmissionConfirmation(packet);
                break;
            case RUN_SUBMISSION:
                // RUN submitted by team to server
                runSubmission(packet, fromId);
                break;
            case RUN_SUBMISSION_CONFIRM_SERVER:
                // RUN send from one server to another 
                handleRunSubmissionConfirmationServer (packet, fromId);
                break;
            case CLARIFICATION_SUBMISSION:
                // Clarification submitted by team to server
                confirmSubmission(packet, fromId);
                break;
            case CLARIFICATION_ANSWER:
                // Answer from client to server
                answerClarification(packet, connectionHandlerID);
                break;
            case CLARIFICATION_ANSWER_UPDATE:
                // Answer from server to client
                sendAnswerClarification(packet);
                break;
            case CLARIFICATION_SUBMISSION_CONFIRM:
                clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
                contest.addClarification(clarification);
                if (isServer()) {
                    sendToJudgesAndOthers(packet, isThisSite(clarification));
                }
                break;
            case CLARIFICATION_UNCHECKOUT:
                // Clarification cancel or un-checkout, client to server
                cancelClarificationCheckOut(packet, connectionHandlerID);
                break;
            case CLARIFICATION_CHECKOUT:
                // The clarification that was checked out, sent from server to clients
                checkoutClarification(packet, connectionHandlerID);
                break;
            case CLARIFICATION_AVAILABLE:
                // Server to client, run was canceled, now available
                sendClarificationAvailable(packet);
                break;
            case LOGIN_FAILED:
                String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
                contest.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
                break;
            case CLARIFICATION_NOT_AVAILABLE:
                // Run not available from server
                Clarification clar = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
                contest.clarificationNotAvailable(clar);
                if (isServer()) {
                    sendToJudgesAndOthers(packet, isThisSite(clar));
                }
                break;
            case RUN_NOTAVAILABLE:
                // Run not available from server
                handleRunNotAvailable(packet);
                break;
            case FORCE_DISCONNECTION:
                sendForceDisconnection(packet);
                break;
            case ESTABLISHED_CONNECTION:
                establishConnection(packet, connectionHandlerID);
                break;
            case DROPPED_CONNECTION:
                droppedConnection(packet, connectionHandlerID);
                break;
            case RUN_AVAILABLE:
                runAvailable(packet);
                break;
            case RUN_JUDGEMENT:
                // Judgement from judge to server
                acceptRunJudgement(packet, connectionHandlerID);
                break;
            case RUN_JUDGEMENT_UPDATE:
                sendJudgementUpdate(packet);
                break;
            case RUN_UPDATE:
                updateRun(packet, connectionHandlerID);
                break;
            case RUN_UPDATE_NOTIFICATION:
                sendRunUpdateNotification(packet);
                break;
            case RUN_UNCHECKOUT:
                // Cancel run from requestor to server
                handleRunUnCheckout(packet, connectionHandlerID);
                break;
            case START_ALL_CLOCKS:
                // Start All Clocks from admin to server
                startContest(packet, connectionHandlerID);

                if (isThisSite(packet.getSourceId())) {
                    controller.sendToServers(packet);
                }
                break;
            case STOP_ALL_CLOCKS:
                // Start All Clocks from admin to server
                stopContest(packet, connectionHandlerID);

                if (isThisSite(packet.getSourceId())) {
                    controller.sendToServers(packet);
                }
                break;
            case START_CONTEST_CLOCK:
                // Admin to server, start the clock
                startContest(packet, connectionHandlerID);
                break;

            case STOP_CONTEST_CLOCK:
                // Admin to server, stop the clock
                stopContest(packet, connectionHandlerID);
                break;
            case UPDATE_CONTEST_CLOCK:
                // Admin to server, stop the clock
                updateContestClock(packet);
                break;
            case CLOCK_STARTED:
                // InternalContest Clock started sent from server to clients
                startClock(packet);
                break;
            case CLOCK_STOPPED:
                // InternalContest Clock stopped sent from server to clients
                clockStopped(packet);
                break;
         
            default:
                /**
                 * To avoid the method too many lines limit, the switch
                 * for packetType is handled by this method.
                 */
                handleOtherPacketTypes(packetType, fromId, packet, connectionHandlerID);
        } 
    }             

    /**
     * Handle packets that are not handled by {@link #handlePacket(Packet, ConnectionHandlerID)}.
     * 
     * 
     * @param packetType
     * @param fromId
     * @param packet
     * @param connectionHandlerID
     * @throws Exception 
     */
    private void handleOtherPacketTypes(Type packetType, ClientId fromId, Packet packet, ConnectionHandlerID connectionHandlerID) throws Exception {

        switch (packetType) {

            case ADD_SETTING:
                addNewSetting(packet);
                break;
            case DELETE_SETTING:
                deleteSetting(packet);
                break;
            case GENERATE_ACCOUNTS:
                generateAccounts(packet);
                break;
            case UPDATE_SETTING:
                updateSetting(packet);
                break;
            case RUN_CHECKOUT:
                // Fall through
            case RUN_CHECKOUT_NOTIFICATION:
                // Run from server to clients
                runCheckout(packet, packetType);
                break;
            case RUN_REJUDGE_CHECKOUT:
                // Run from server to clients
                runCheckout(packet, packetType); // this works for rejudge as well.
                break;
            case CLARIFICATION_REQUEST:
                requestClarification(packet, connectionHandlerID);
                break;
            case RUN_REQUEST:
                // Request Run from requestor to server
                runRequest(packet, connectionHandlerID);
                break;
            case RUN_REJUDGE_REQUEST:
                // REJUDGE Request Run from requestor to server
                requestRejudgeRun(packet, connectionHandlerID);
                break;
            case LOGOUT:
                // client logged out
                logoutClient(packet);
                break;
            case LOGIN:
                // client logged in
                loginClient(packet);
                break;
            case PASSWORD_CHANGE_REQUEST:
                // Client requests password change
                attemptChangePassword(packet);
                break;
            case PASSWORD_CHANGE_RESULTS:
                // Server to individual client
                // An Update Settings packet will be used to update Admin and Servers (update Account)
                handlePasswordChangeResults(packet);
                break;
            case LOGIN_SUCCESS:
                // from server to client/server on a successful login
                loginSuccess(packet, connectionHandlerID, fromId);
                break;
            case SERVER_SETTINGS:
                // This is settings from a recently logged in server
                handleServerSettings(packet, connectionHandlerID);
                break;
            case RECONNECT_SITE_REQUEST:
                reconnectSite(packet);
                break;
            case SECURITY_MESSAGE:
                // From server to admins
                handleSecurityMessage(packet);
                break;
            case FETCH_RUN:
                // From judge (non-team) to sever
                requestFetchedRun(packet, connectionHandlerID);
                break;

            case FETCHED_REQUESTED_RUN:
                // from server to non-team client.
                handleFetchedRun(packet, connectionHandlerID);
                break;

            case RUN_EXECUTION_STATUS:
                // from server to server
                // from judge client to server
                // from server to spectator clients
                handleRunExecutionStatus(packet, connectionHandlerID);
                break;

            case RESET_ALL_CONTESTS:
                resetAllSites(packet, connectionHandlerID);
                break;

            case CLONE_PROFILE:
                handleCloneProfile(packet, connectionHandlerID);
                break;

            case SWITCH_PROFILE:
                handleSwitchProfile(packet, connectionHandlerID);
                break;

            case UPDATE_CLIENT_PROFILE:
                handleUpdateClientProfile(packet, connectionHandlerID);
                break;

            case FETCH_RUN_FILES:
                handleFetchRunFiles(packet, connectionHandlerID);

            case UPDATE_RUN_FILES:
                handleRunFilesList(packet, connectionHandlerID);
                break;

            case REQUEST_SERVER_STATUS:
                handleRequestServerStatus (packet, connectionHandlerID);
                break;
                
            case SERVER_STATUS:
                handleServerStatus(packet, connectionHandlerID);
                break;
                
            case SYNCHRONIZE_REMOTE_DATA: 
                handleSynchronizeRemoteData(packet, connectionHandlerID);
                break;
                
            case REQUEST_REMOTE_DATA:
                // Update remote data from other server.
                handleRequestRemoteData(packet, connectionHandlerID);
                break;

            case UPDATE_REMOTE_DATA:
                loginSuccess(packet, connectionHandlerID, fromId);
                break;
           
            default:
                Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
                controller.getLog().log(Log.WARNING, "Unhandled Packet ", exception);

        }

        info("handlePacket end " + packet);
    }

    /**
     * Send a packet to all servers to request remote data.
     * 
     * This will start the process for a server to synchronize
     * its remote data by sending a request to all servers. 
     * 
     * @param packet
     * @param connectionHandlerID
     */
    private void handleSynchronizeRemoteData(Packet packet, ConnectionHandlerID connectionHandlerID) {
        Packet requestPacket = PacketFactory.createRequestRemoteDataPacket(getServerClientId(), PacketFactory.ALL_SERVERS);
        controller.sendToServers(requestPacket);
    }

    /**
     * 
     * @param packet
     * @param connectionHandlerID
     */
    private void handleRequestRemoteData(Packet packet, ConnectionHandlerID connectionHandlerID) {
       
        ClientId remoteServerId = new ClientId(packet.getSourceId().getSiteNumber(), ClientType.Type.SERVER, 0);
        Packet requestedPacket = createLoginSuccessPacket(remoteServerId, contest.getContestPassword());
        Packet remoteDataPacket = PacketFactory.clonePacket(Type.UPDATE_REMOTE_DATA, getServerClientId(), remoteServerId, requestedPacket);
        controller.sendToClient(remoteDataPacket);
    }

    private void handleRunSubmissionConfirmationServer(Packet packet, ClientId fromId) throws IOException, ClassNotFoundException, FileSecurityException  {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            
            contest.addRun(run, runFiles);
            
            if (isServer()) {
                Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(contest.getClientId(), fromId, run);
                sendToJudgesAndOthers(confirmPacket, false);
            } else {
                if (Utilities.isDebugMode()){
                    Exception ex = new Exception("Non server was send a "+packet.getType()+" packet");
                    ex.printStackTrace();
                    controller.logWarning("Unexpected packet on client", ex);
                }
            }
        
    }

    /**
     * Handle switch packet.
     * 
     * @param packet
     * @param connectionHandlerID
     * @throws ProfileException 
     * @throws FileSecurityException 
     */
    private void handleSwitchProfile(Packet packet, ConnectionHandlerID connectionHandlerID) throws ProfileException {

        // inProfile the original profile
        // Profile inProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);

        Profile newProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.NEW_PROFILE);
        String contestPassword = (String) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_PASSWORD);
        
        if (newProfile.getSiteNumber() == 0){
            newProfile.setSiteNumber(contest.getSiteNumber());
        }

        if (contestPassword == null) {
            // Use existing contest password if no contest password specified.
            contestPassword = contest.getContestPassword();
        }
        
        if (!new File(newProfile.getProfilePath()).isDirectory()) {
            
            throw new ProfileException("Unable to switch - can not find profile on disk");
            
            // FIXME code up Profile does not exist on this server, so must try to switch on originating server.
//            /**
//             * Profile does not exist on this server, so must try to switch on originating server.
//             */
//            if (newProfile.getSiteNumber() != contest.getSiteNumber()) {
//                ClientId remoteServerId = new ClientId(newProfile.getSiteNumber(), ClientType.Type.SERVER, 0);
//                Packet forwardPacket = PacketFactory.clonePacket(getServerClientId(), remoteServerId, packet);
//                controller.sendToClient(forwardPacket);
//                return;
//            }
        }
        
        ProfileManager manager = new ProfileManager();

        if (manager.isProfileAvailable(newProfile, contestPassword.toCharArray())) {

            IInternalContest newContest = switchProfile(contest, newProfile, contestPassword.toCharArray(), true);
            contest = newContest;
            
            sendStatusToServers (packet, newProfile);

        } else {
            throw new ProfileException("Can not switch profiles, invalid contest password");
        }
    }

    /**
     * Switch to new profile.
     * 
     * Load new profile from disk, replace instances of old contest with new contests including all data.
     * 
     * @param currentContest contest switch from 
     * @param newProfile profile to switch to
     * @param contestPassword contest password for newProfile
     * @param sendToOtherServers true if this is the first server switching profiles, false if not
     * @param packet 
     * @return new contest based on newProfile
     * @throws ProfileException
     */
    protected IInternalContest switchProfile(IInternalContest currentContest, Profile newProfile, char[] contestPassword, boolean sendToOtherServers) throws ProfileException {
        return switchProfile( currentContest,  newProfile,  contestPassword,  sendToOtherServers, null, sendToOtherServers);
    }
    
    /**
     * Switch profile on this server then send out switch to all local clients. 
     *  
     * @param currentContest
     * @param newProfile
     * @param contestPassword
     * @param sendToOtherServers
     * @param packet
     * @param sendToServers 
     * @return
     * @throws ProfileException
     */
    protected IInternalContest switchProfile(IInternalContest currentContest, Profile newProfile, char[] contestPassword, boolean sendToOtherServers, Packet packet, boolean sendToServers)
            throws ProfileException {

        ProfileManager manager = new ProfileManager();
        
        IInternalContest newContest = new InternalContest();

        newContest.setClientId(contest.getClientId());
        if (newContest.getSiteNumber() == 0){
            newContest.setSiteNumber(contest.getSiteNumber());
        }
        
        info("switchProfile start - to "+newProfile+" as Site "+contest.getSiteNumber()+" as user "+newContest.getClientId());
        
        IStorage storage = manager.getProfileStorage(newProfile, contestPassword);
        newContest.setStorage(storage);
        
        info("switchProfile save config to "+storage.getDirectoryName());
        
        newContest.setContestPassword(new String(contestPassword));

        try {
            /**
             * This loads configuration and re-loads submissions and un-checksout submissions.
             */
            newContest.readConfiguration(contest.getSiteNumber(), controller.getLog());
        } catch (Exception e) {
            throw new ProfileException(newProfile, "Unable to read configuration ", e);
        }
        
        if (newContest.getProfile() == null){
            newContest.setProfile(newProfile);
        }
        
        try {
            newContest.storeConfiguration(controller.getLog());
        } catch (Exception e) {
            throw new ProfileException(newProfile, "Unable to store configuration ", e);
        }

        /**
         * Remove listeners so that they are no longer referenced
         */
        contest.removeAllListeners(); // remove all listeners from old contest/profile

        contest.cloneAllLoginAndConnections(newContest);
        
        System.out.println("debug 22  contest hash before "+contest);
        
        /*
         * Set new contest and add all listeners.
         */
        controller.updateContestController(newContest, controller);

        contest = newContest; // set contest in this (PacketHandler);

        System.out.println("debug 22  contest hash after  "+contest);

        if (packet != null){
            /**
             * Load configuration information from remoteServer
             */
            
            updateSitesToModel(packet);
            
            // This will load information and also re-log us into other servers
            // to get the most up to date information from those servers. 
            

            ContestLoader loader = new ContestLoader();
            loadSettingsFromRemoteServer (loader, packet, null);
            loader = null;
        }

        contest.fireAllRefreshEvents();
        
        storeProfiles();
        
        sendOutChangeProfileToAllClients(contest, currentContest.getProfile(), newProfile, new String(contestPassword), sendToServers);
        
        info("switchProfile start - to "+newProfile+" as Site "+contest.getSiteNumber()+" as user "+newContest.getClientId());

        return contest;
    }


    /**
     * Create and send current profile to all clients and servers.
     * 
     * @param newContest
     * @param currentProfile
     * @param switchToProfile
     * @param contestPassword used to encrypt/decrypt config files.
     * @param b 
     */
    protected void sendOutChangeProfileToAllClients(IInternalContest newContest, Profile currentProfile, Profile switchToProfile, String contestPassword, boolean sendToServers) {

        ContestLoginSuccessData data ;
        Packet packet ;
        
        if (sendToServers){
            data = createContestLoginSuccessData(newContest, getServerClientId(), contestPassword);
            packet = PacketFactory.createUpdateProfileClientPacket(getServerClientId(), PacketFactory.ALL_SERVERS, currentProfile, switchToProfile, data);

            // Servers get the same packet
            sendClonePacketToUsers(packet, ClientType.Type.SERVER, newContest, false);
        }

        // Create packed for Admin, Judge, boards
        data = createContestLoginSuccessData(newContest, getServerClientId(), null);
        packet = PacketFactory.createUpdateProfileClientPacket(getServerClientId(), PacketFactory.ALL_SERVERS, currentProfile, switchToProfile, data);

        ClientType.Type[] typeList = { //
        ClientType.Type.ADMINISTRATOR, //
                ClientType.Type.JUDGE, //
                ClientType.Type.SCOREBOARD, //
        // ClientType.Type.EXECUTOR , //
        // ClientType.Type.SPECTATOR, //
        // ClientType.Type.OTHER , //
        };

        for (ClientType.Type type : typeList) {
            sendClonePacketToUsers(packet, type, newContest, true);
        }

        /**
         * Handle new profile/config packets for teams on this server.
         */
        ClientId[] teams = contest.getLocalLoggedInClients(ClientType.Type.TEAM);

        for (ClientId clientId : teams) {
            
            if (newContest.getAccount(clientId) != null) {
                // Account exists in new profile/config

                // Team's get their specific data in their packet (their runs, their clars, no judges data files)
                data = createContestLoginSuccessData(newContest, clientId, null);
                packet = PacketFactory.createUpdateProfileClientPacket(getServerClientId(), clientId, currentProfile, switchToProfile, data);
                controller.sendToClient(packet);
            } else {
                // Account/user does not exist in new profile/config
                controller.getLog().info("Not sending UPDATE_CLIENT_PROFILE to client not found in new profile/account list " + clientId);
                // TODO logoff/disconnect client
            }

        }
    }

    /**
     * Send cloned packet to user class (ClientType).
     * 
     * Will loop through all logged in users for ClientType and send packet to users.
     * 
     * To send to only accounts/user logins that exist in the new profile/contest configuration,
     * set the confirmUserExists to true.
     * 
     * @param packet packet to send
     * @param type class of user to send to.
     * @param newContest data used to confirm that account/clientId exists.
     * @param confirmUserExists - true means confirm that each clientId exists, false means send out without confirmation.
     */
    private void sendClonePacketToUsers(Packet packet, edu.csus.ecs.pc2.core.model.ClientType.Type type, IInternalContest newContest, boolean confirmUserExists) {
        ClientId[] users = contest.getLocalLoggedInClients(type);
        
        for (ClientId clientId : users) {
            
            try {
                if (!confirmUserExists) {
                    // send unconditional to client
                    packet = PacketFactory.clonePacket(getServerClientId(), clientId, packet);
                    controller.sendToClient(packet);
                } else if (newContest.getAccount(clientId) != null) {
                    // Account exists in new profile, send it packet
                    packet = PacketFactory.clonePacket(getServerClientId(), clientId, packet);
                    controller.sendToClient(packet);
                } else {
                    controller.getLog().info("Not sending UPDATE_CLIENT_PROFILE to client not found in new profile/account list " + clientId);
                    // TODO logoff/disconnect client
                }
            } catch (Exception e) {
                controller.logWarning("Trouble sending clone packet to "+clientId, e);
            }
        }
    }

    /**
     * Update Client Profile handle new contest profile/settings. 
     * 
     * @param packet
     * @param connectionHandlerID
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     * @throws ProfileException 
     * @throws ProfileCloneException 
     */
    private void handleUpdateClientProfile(Packet packet, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException, FileSecurityException, ProfileException,
            ProfileCloneException {

        if (isServer()) {
            
            /**
             * Switch Profile or create new Profile files on the server 
             */
            
            //  switch/load new profile storage
            
            Profile newProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.NEW_PROFILE);
            String contestPassword = (String) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_PASSWORD);
            
            newProfile.setSiteNumber(contest.getSiteNumber());

            if (contestPassword == null) {
                // Use existing contest password if no contest password specified.
                contestPassword = contest.getContestPassword();
            }

            ProfileManager manager = new ProfileManager();
            
            if (manager.createProfilesPathandFiles(newProfile, contestPassword)) {

                /**
                 * If the profiles paths need to be created, then we need to create the profile.
                 */

                IStorage storage = createStorage(newProfile, contestPassword);

                int siteNumber = contest.getSiteNumber();
                IInternalContest contest2 = initializeContest(storage, siteNumber);

                createProfileFromPacket(newProfile, contest2, packet, connectionHandlerID);

                /**
                 * Then save the new contest information.
                 */
                contest2.storeConfiguration(controller.getLog());
            } 
            
            if (manager.isProfileAvailable(newProfile, contestPassword.toCharArray())) {

                IInternalContest newContest = switchProfile(contest, newProfile, contestPassword.toCharArray(), false, packet, false);
                contest = newContest;
                
                sendStatusToServers (packet, newProfile);

            } else {
                throw new ProfileException("Can not switch profiles, invalid contest password");
            }
            

        } else {

            // This is a update/new profile so all data is reset then re-added from this packet

            contest.resetSubmissionData();
            contest.resetConfigurationData();
  
            unRegisterPlugins();
            
            ContestLoader loader = new ContestLoader();
            loader.loadDataIntoModel(contest, controller, packet, connectionHandlerID);
            loader = null;
            
            reRegisterPlugins();
            
            info("handleUpdateClientProfile fireAllRefreshEvents start");
            long start = new Date().getTime();
            
            System.err.println("debug22 handleUpdateClientProfile fireAllRefreshEvents start "+new Date());
            
            contest.fireAllRefreshEvents();
            
            long elapsed = (new Date().getTime() - start) / 1000;
            
            System.err.println("debug22 handleUpdateClientProfile fireAllRefreshEvents done "+new Date());
            System.err.println("debug22 handleUpdateClientProfile fireAllRefreshEvents elapsed "+elapsed);
            
        }
    }

    /**
     * Remove all listeners from model.
     */
    private void unRegisterPlugins() {
        contest.removeAllListeners(); 


    }

    /**
     * Add all listeners into model.
     */
  private void reRegisterPlugins() {

        for (UIPlugin plugin : controller.getPluginList()) {

            try {
                plugin.setContestAndController(contest, controller);

                controller.getLog().info("plugin.setContestAndController for " + plugin.getPluginTitle());

            } catch (Exception e) {
                controller.logWarning("Problem registering plugin "+plugin.getPluginTitle(), e);
            }
        }
    }



    private IInternalContest initializeContest(IStorage storage, int siteNumber) {
        
        IInternalContest newContest = new InternalContest();
        
        newContest.setSiteNumber(siteNumber);
        newContest.setStorage(storage);
        
        newContest.initializeSubmissions(siteNumber);
        
        return newContest;
    }

    private IStorage createStorage(Profile newProfile, String contestPassword) throws ProfileCloneException {

        String profilePath = newProfile.getProfilePath();

        try {
            new File(profilePath).mkdirs();
        } catch (Exception e) {
            throw new ProfileCloneException("Unable to create profile dir " + profilePath, e);
        }

        if (!new File(profilePath).isDirectory()) {
            throw new ProfileCloneException("Unable to use profile dir " + profilePath);
        }
        
        String databaseDirectoryName = profilePath + File.separator + "db." + contest.getSiteNumber();

        try {
            new File(databaseDirectoryName).mkdirs();
        } catch (Exception e) {
            throw new ProfileCloneException("Unable to create DB dir " + profilePath, e);
        }
        
        FileSecurity fileSecurity = new FileSecurity(databaseDirectoryName);

        try {
            fileSecurity.saveSecretKey(contestPassword.toCharArray());
        } catch (Exception e) {
            throw new ProfileCloneException(e);
        }
        
        return fileSecurity;
    }

    /**
     * Create profile from the input packet.
     * 
     * 
     * 
     * @param newProfile the profile to store/load/create.
     * @param contest2 the contest where settings will be loaded.
     * @param packet a {@link edu.csus.ecs.pc2.core.packet.PacketType.Type#UPDATE_CLIENT_PROFILE}.
     * @param connectionHandlerID 
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void createProfileFromPacket(Profile newProfile, IInternalContest contest2, Packet packet, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException,
            FileSecurityException {
        
        if (packet.getType().equals(Type.UPDATE_CLIENT_PROFILE)){

            ContestLoader loader = new ContestLoader();
            loader.loadDataIntoModel(contest2, controller, packet, connectionHandlerID);
            loader = null;
            
        } else {
            new ProfileException("Can not create profile from packet type "+packet.getType().toString());
        }
        
    }

    /**
     * RunFiles from a remote site, add these to this site.
     * 
     * @param packet
     * @param connectionHandlerID
     * @throws Exception 
     */
    private void handleRunFilesList(Packet packet, ConnectionHandlerID connectionHandlerID) throws Exception {

        RunFiles[] files = (RunFiles[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES_LIST);
        
        if (files != null) {

            for (RunFiles runFiles : files) {
                try {
                    Run run = contest.getRun(runFiles.getRunId());
                    if (!isThisSite(run.getSiteNumber())) {
                        contest.updateRunFiles(run, runFiles);
                    } else {
                        throw new Exception("Will not update local run files " + run);
                    }
                } catch (Exception e) {
                    controller.logWarning("Unable to save run files", e);
                }
            }
        } else {
            throw new Exception("RUN_FILES are null from packet "+packet);
        }
        
    }
    
    private void handleFetchRunFiles(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        int siteNumber = packet.getSourceId().getSiteNumber();
        int lastRunId = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.RUN_ID);
        sendRunFilesToServer(siteNumber, lastRunId);
    }

    private void handleCloneProfile(Packet packet, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException, FileSecurityException, ProfileCloneException, ProfileException {
        
        // FIXME code security check only Admins can change profiles
        
//        prop.put(CLIENT_ID, source);
//        ClientId adminClientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        
//        prop.put(PROFILE, profile2);
//        Profile currentProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);
        
        ProfileCloneSettings settings =  (ProfileCloneSettings) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE_CLONE_SETTINGS);
        boolean switchProfileNow = ((Boolean) PacketFactory.getObjectValue(packet, PacketFactory.SWITCH_PROFILE)).booleanValue();
        
        cloneContest (settings, switchProfileNow);
        
    }
    
    private void cloneContest (ProfileCloneSettings settings, boolean switchProfileNow) throws ProfileCloneException, ProfileException, IOException, ClassNotFoundException, FileSecurityException {
        
        Profile newProfile = new Profile(settings.getName());
        newProfile.setDescription(settings.getDescription());
        newProfile.setSiteNumber(contest.getSiteNumber());
        
        Profile addedProfile = contest.addProfile(newProfile);
        
        InternalContest newContest = new InternalContest();
        newContest.setSiteNumber(contest.getSiteNumber());
        
        /**
         * This clones the existing contest based on the settings,
         * including copying and saving all settings on disk.
         */
        contest.clone(newContest, addedProfile, settings);
        
        contest.storeConfiguration(controller.getLog());

        storeProfiles();
        
        if (switchProfileNow ){
            switchProfile(contest, newProfile, contest.getContestPassword().toCharArray(), true);
            
        } else {
            Packet addPacket = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, addedProfile);
            sendToJudgesAndOthers(addPacket, true);
        }
    }


    private void handleServerSettings(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        ContestLoader loader = new ContestLoader();
        loadSettingsFromRemoteServer(loader, packet, connectionHandlerID);
        loader = null;
        
        info(" handlePacket SERVER_SETTINGS - from another site -- all settings loaded " + packet);

        if (isServer()) {
            sendToJudgesAndOthers(packet, false);
        }
    }

    /**
     * 
     * @param packet
     */
    private void handleRunNotAvailable(Packet packet) {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        contest.runNotAvailable(run);

        if (isServer()) {
            ClientId clientId = packet.getDestinationId();
            if (isThisSite(clientId)) {
                controller.sendToClient(packet);
            }
        }
    }
    
    private void handleRunUnCheckout(Packet packet, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoCanceledId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        cancelRun(packet, run, whoCanceledId, connectionHandlerID);
    }

    private void handleRunSubmissionConfirmation(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        contest.addRun(run);
        if (isServer()) {
            sendToJudgesAndOthers(packet, isThisSite(run));
        }
    }

    /**
     * Handles a reset all contest from admin.
     * 
     * Checks security that allows this client (Admin hopefully) to reset this site and then send reset to all other sites.
     * 
     * @param packet
     * @param connectionHandlerID
     * @throws ContestSecurityException
     * @throws ProfileCloneException
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws ProfileException
     */
    private void resetAllSites(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, ProfileCloneException, ProfileException, IOException, ClassNotFoundException,
            FileSecurityException {

        ClientId sourceId = packet.getSourceId();

        ClientId adminClientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        // check permission
        securityCheck(Permission.Type.RESET_CONTEST, adminClientId, connectionHandlerID);

        if (isServer()) {
            // Only servers are allowed to reset client or other server contest
            Profile profile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);

            // TODO insure that the profile that they are resettings is THIS profile

            resetContest(packet, profile);
        } else {
            /**
             * Some non-server tried to send a reset to a client or server.
             */
            throw new ContestSecurityException(sourceId, connectionHandlerID, sourceId + " not allowed to " + Permission.Type.RESET_CONTEST);
        }
    }

    /**
     * Reset the contest.
     * 
     * Clone current profile, prepend with the name "Backup of " and mark inactive,
     * then switch to the newly cloned profile.
     * 
     * @param packet
     * @param profile
     * @throws ProfileCloneException
     * @throws ProfileException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    private void resetContest(Packet packet, Profile profile) throws ProfileCloneException, ProfileException, IOException, ClassNotFoundException, FileSecurityException {
        
        Boolean eraseProblems = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.DELETE_PROBLEM_DEFINITIONS);
        Boolean eraseLanguages = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.DELETE_LANGUAGE_DEFINITIONS);

        if (isServer()) {

            ClientId adminClientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            
            if (! isAllowed(adminClientId, Permission.Type.SWITCH_PROFILE)){
                info("permission is not granted to "+adminClientId+" to reset");
            } else {
                info("permission is granted to "+adminClientId+" to reset");
            }
            
            /**
             * Hide the current copy of this profile, give it a new name.
             */
            Profile updatedProfile = contest.getProfile();
            updatedProfile.setActive (false);
            updatedProfile.setName("Backup of "+updatedProfile.getName());
            
            /**
             * This clears all submission data and counters.
             */

            IInternalContest newContest = new InternalContest();
            newContest.setClientId(contest.getClientId());
            if (newContest.getSiteNumber() == 0){
                newContest.setSiteNumber(contest.getSiteNumber());
            }
            
            newContest.addProfile(updatedProfile); // Add old hiddent profile into new profile's list
            
            String title = contest.getContestInformation().getContestTitle();
            String description = profile.getDescription();
            String password = contest.getContestPassword();
            ProfileCloneSettings settings = new ProfileCloneSettings(profile.getName(), description, password.toCharArray(), contest.getProfile());
            
            settings.setContestTitle(title);
            
            settings.setResetContestTimes( true );
            settings.setCopyAccounts( true );
            settings.setCopyContestSettings( true );
            settings.setCopyGroups( true );
            settings.setCopyJudgements( true );
            settings.setCopyNotifications( true );
            
            settings.setCopyLanguages( !eraseLanguages );
            settings.setCopyProblems( !eraseProblems );
            settings.setCopyRuns( false );
            settings.setCopyClarifications( false );
            
            settings.setContestPassword(contest.getContestPassword().toCharArray());
            
            cloneContest (settings, true);
            
        } else {
            
            // TODO remove this unused code (with profiles this code is no longer used)
            
            // Set Contest Profile
            contest.setProfile(profile);
            
            resetContestData(eraseProblems, eraseLanguages);
            
            contest.fireAllRefreshEvents();
        }
    }
    
    /**
     * Clear all auto judge problems for this contest site/client.
     * 
     */
    private void removeAllProblemsFromAutoJudging(){
        Vector <Account> vectorAccounts = contest.getAccounts(ClientType.Type.JUDGE, contest.getSiteNumber());
        Account [] accounts = (Account[]) vectorAccounts.toArray(new Account[vectorAccounts.size()]);
        
        for (Account account : accounts){
            ClientSettings clientSettings = new ClientSettings(account.getClientId());
            clientSettings.setAutoJudging(false);
            clientSettings.setAutoJudgeFilter(new Filter());
        }
    }

    private void resetContestData(Boolean eraseProblems, Boolean eraseLanguages) { 

        contest.resetSubmissionData();

        if (eraseProblems != null && eraseProblems.booleanValue()) {
            for (Problem problem : contest.getProblems()) {
                contest.deleteProblem(problem);
            }
            
            removeAllProblemsFromAutoJudging();
        }

        if (eraseLanguages != null && eraseLanguages.booleanValue()) {
            for (Language language : contest.getLanguages()) {
                contest.deleteLanguage(language);
            }
        }
    }
    
    /**
     * Get a SERVER client id.
     * 
     * This is a generic send to all server and clients ClientId.
     * 
     * @return a generic all sites server client id
     */
    private ClientId getServerClientId() {
        return new ClientId(contest.getSiteNumber(), ClientType.Type.SERVER, 0);
    }

    private void handleRunExecutionStatus(Packet packet, ConnectionHandlerID connectionHandlerID) {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId judgeClientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        RunExecutionStatus status = (RunExecutionStatus) PacketFactory.getObjectValue(packet, PacketFactory.RUN_STATUS);
        
        if (isServer()) {

            if (!isThisSite(judgeClientId)) {
                
                // If this is not a run status from this site, then send to spectators/API only
                
                sendToSpectatorsAndSites(packet, false);
                
            } else {
                // packet from this site, send to all spectators/API and to other servers.

              Packet runExecuteStatusPacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
              sendToSpectatorsAndSites(runExecuteStatusPacket, true);
            }
        } else {
            // Accept and process this packet (for the API)
            contest.updateRunStatus(run, status, judgeClientId);
        }
    }

    private void requestFetchedRun(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoRequestsRunId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        
        securityCheck(Permission.Type.ALLOWED_TO_FETCH_RUN, whoRequestsRunId, connectionHandlerID);
        
        if (isServer()) {

            if (!isThisSite(run)) {
                
                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (contest.isLocalLoggedIn(serverClientId)) {

                    // send request to remote server
                    Packet fetchRunPacket = PacketFactory.createFetchRun(serverClientId, whoRequestsRunId, run, serverClientId);
                    controller.sendToRemoteServer(run.getSiteNumber(), fetchRunPacket);

                } else {

                    // send NOT_AVAILABLE back to client
                    Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(contest.getClientId(), whoRequestsRunId, run);
                    controller.sendToClient(notAvailableRunPacket);
                }

            } else {
                // This Site's run, if we can check it out and send to client

                Run theRun = contest.getRun(run.getElementId());

                // just get run and sent it to them.

                theRun = contest.getRun(run.getElementId());
                RunFiles runFiles = contest.getRunFiles(run);

                RunResultFiles[] runResultFiles = contest.getRunResultFiles(run);

                // send to Client/Judge
                Packet fetchedRunPacket = PacketFactory.createFetchedRun(contest.getClientId(), whoRequestsRunId, theRun, runFiles, whoRequestsRunId, runResultFiles);
                controller.sendToClient(fetchedRunPacket);
            }
        } else {
            // non-server
            throw new SecurityException("requestRun - sent to client " + contest.getClientId());
        }
    }

    private void runAvailable(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        contest.availableRun(run);

        if (isServer()) {
            sendToJudgesAndOthers(packet, isThisSite(run));
        }
    }

    private void runSubmission(Packet packet, ClientId fromId) throws IOException, ClassNotFoundException, FileSecurityException {
        Run submittedRun = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
        Run run = contest.acceptRun(submittedRun, runFiles);

        // Send to team
        Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(contest.getClientId(), fromId, run);
        controller.sendToClient(confirmPacket);

        // Send to clients and servers
        if (isServer()) {
            sendToJudgesAndOthers(confirmPacket, false);
            Packet dupSubmissionPacket = PacketFactory.createRunSubmissionConfirmation(contest.getClientId(), fromId, run, runFiles);
            controller.sendToServers(dupSubmissionPacket);
        }

    }

    private void confirmSubmission(Packet packet, ClientId fromId) {

        Clarification submittedClarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        Clarification clarification = contest.acceptClarification(submittedClarification);

        // Send to team
        Packet confirmPacket = PacketFactory.createClarSubmissionConfirm(contest.getClientId(), fromId, clarification);
        controller.sendToClient(confirmPacket);

        // Send to clients and other servers
        if (isServer()) {
            sendToJudgesAndOthers(confirmPacket, true);
        }
    }

    private void runRequest(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Boolean readOnly = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.READ_ONLY);
        Boolean computerJudge = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.COMPUTER_JUDGE);
        if (readOnly != null) {
            checkoutRun(packet, run, requestFromId, readOnly.booleanValue(), computerJudge.booleanValue(), connectionHandlerID);

        } else {
            requestRun(packet, run, requestFromId, connectionHandlerID, computerJudge);
        }
        
    }

    private void clockStopped(Packet packet) {
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        contest.stopContest(siteNumber);
        ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        ContestTime contestTime = contest.getContestTime(siteNumber);
        info("Clock for site " + contestTime.getSiteNumber() + " stopped by " + clientId + " elapsed " + contestTime.getElapsedTimeStr());

        if (isServer()) {
            controller.sendToTeams(packet);
            sendToJudgesAndOthers(packet, false);
        }
    }

    private void startClock(Packet packet) {
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        contest.startContest(siteNumber);
        ContestTime contestTime = contest.getContestTime(siteNumber);
        ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        info("Clock for site " + contestTime.getSiteNumber() + " started by " + clientId + " elapsed " + contestTime.getElapsedTimeStr());

        if (isServer()) {
            controller.sendToTeams(packet);
            sendToJudgesAndOthers(packet, false);
        }
    }

    private void handlePasswordChangeResults(Packet packet) {
        
        ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Boolean passwordChanged = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.PASSWORD_CHANGED);
        String message = (String) PacketFactory.getObjectValue(packet, PacketFactory.MESSAGE_STRING);
        
        String mess;
        if (passwordChanged.booleanValue()){
            mess = "Password changed "+ message;
        } else {
            mess = "Password NOT changed "+ message;
        }
        
        controller.getLog().log(Log.INFO, mess);
        
        contest.passwordChanged (passwordChanged.booleanValue(), clientId, message);
        
    }

    /**
     * Change password request from client.
     * 
     * This change assumes only that a client is changing their own password.
     * If the client requesting is not from this site, their password will NOT 
     * be changed.
     * 
     * @param packet input change password packet
     */
    private void attemptChangePassword(Packet packet) {

        ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        String password = (String) PacketFactory.getObjectValue(packet, PacketFactory.PASSWORD);
        String newPassword = (String) PacketFactory.getObjectValue(packet, PacketFactory.NEW_PASSWORD);

        if (clientId == null || password == null || newPassword == null) {
            // TODO invalid request, send it back
            String mess = "Invalid request ";
            if (password == null) {
                mess += " password not specified";
            }
            if (newPassword == null) {
                mess += " no new password specified ";
            }

            sendPasswordResultsBackToClient(packet.getSourceId(), false, mess);

        } else if (!isThisSite(clientId)) {
            // Not this site client changing their password, something just wrong, spoof ??
            // Note that admin uses an update account method to change passwords.

            String mess = "Security Warning client from other site tried to change password " + clientId;
            controller.getLog().log(Log.WARNING, mess);

            // Send Security warning to all admins and servers

            Packet violationPacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, mess, packet.getSourceId(), null, null, packet);

            controller.sendToAdministrators(violationPacket);
            controller.sendToServers(violationPacket);

            // send them back a - not likely.
            sendPasswordResultsBackToClient(clientId, false, "Can not change password from site " + clientId);

        } else {
            
            try {
                if (contest.isValidLoginAndPassword(clientId, password)) {

                    // Got a correct current password, update their password

                    Account account = contest.getAccount(clientId);
                    account.setPassword(newPassword);
                    contest.updateAccount(account);
                    
                    account = contest.getAccount(clientId);

                    sendPasswordResultsBackToClient(clientId, true, "Password changed");

                    // Send this update to all servers and such.
                    Packet updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), account.getClientId(), contest.getAccount(account.getClientId()));

                    controller.sendToAdministrators(updatePacket);
                    controller.sendToServers(updatePacket);
                }
            } catch (Exception e) {
                sendPasswordResultsBackToClient(clientId, false, "Current password does not match, try again");
            }
        }
    }

    /**
     * Send password results back to client.
     * 
     * @param clientId client who requested password change.
     * @param changed was password changed?
     * @param message error or confirm message.
     */
    private void sendPasswordResultsBackToClient(ClientId clientId, boolean changed, String message) {
        
        Packet passwordChangeResult = PacketFactory.createPasswordChangeResult(clientId, clientId, changed, message);
        controller.sendToClient(passwordChangeResult);
    }

    protected void droppedConnection(Packet packet, ConnectionHandlerID connectionHandlerID) {
        ConnectionHandlerID inConnectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);
        if (isServer()) {
            if (isThisSite(packet.getSourceId())) {
                controller.sendToServers(packet);
            }
            sendToJudgesAndOthers(packet, false);
            contest.connectionDropped(inConnectionHandlerID);
        } else {
            contest.connectionDropped(inConnectionHandlerID);
        }
    }

    private void handleSecurityMessage(Packet inPacket) {

        ClientId clientId = (ClientId) PacketFactory.getObjectValue(inPacket, PacketFactory.CLIENT_ID);
        String message = (String) PacketFactory.getObjectValue(inPacket, PacketFactory.MESSAGE);
        ContestSecurityException contestSecurityException = (ContestSecurityException) PacketFactory.getObjectValue(inPacket, PacketFactory.EXCEPTION);
        // Packet packet = (Packet)PacketFactory.getObjectValue(inPacket, PacketFactory.PACKET);

        controller.getLog().log(Log.WARNING, "Security violation " + clientId + " " + message);

        contest.newSecurityMessage(clientId, "", message, contestSecurityException);

        if (isServer()) {
            Packet forwardPacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, inPacket);
            controller.sendToAdministrators(forwardPacket);
        }
    }

    private void establishConnection(Packet packet, ConnectionHandlerID connectionHandlerID) {

        ConnectionHandlerID inConnectionHandlerID = (ConnectionHandlerID) PacketFactory.getObjectValue(packet, PacketFactory.CONNECTION_HANDLE_ID);

        if (isServer()) {
            controller.sendToAdministrators(packet);
            if (isThisSite(packet.getSourceId())) {
                controller.sendToServers(packet);
            }
            contest.connectionEstablished(inConnectionHandlerID);
        } else {
            contest.connectionEstablished(inConnectionHandlerID);
        }

    }

    protected void checkoutClarification(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
        ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {
            securityCheck(Permission.Type.ANSWER_CLARIFICATION, whoCheckedOut, connectionHandlerID);
        }

        contest.updateClarification(clarification, whoCheckedOut);
        if (isServer()) {
            sendToJudgesAndOthers(packet, false);
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
     * This checks the client permissions settings and if the client does not have permission to do the permission (type) throws a security exception.
     * 
     * @param type
     * @param clientId
     * @param connectionHandlerID
     */
    protected void securityCheck(Permission.Type type, ClientId clientId, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException {

        if (controller.getSecurityLevel() < InternalController.SECURITY_HIGH_LEVEL) {
            return;
        }

        if (!isAllowed(clientId, type)) {
            throw new ContestSecurityException(clientId, connectionHandlerID, clientId + " not allowed to " + type);
        }
    }

    private void acceptRunJudgement(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);
        RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        ClientId whoJudgedRunId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        judgeRun(run, judgementRecord, runResultFiles, whoJudgedRunId, connectionHandlerID, packet);

    }

    /**
     * Process checkout run packets.
     * 
     * @param packet
     * @param packetType either {@link Type.RUN_CHECKOUT} or {@link Type.RUN_CHECKOUT_NOTIFICATION}
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void runCheckout(Packet packet, Type packetType) throws IOException, ClassNotFoundException, FileSecurityException {

        // Run checkout OR run re-judge checkout
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        switch (packetType) {
            case RUN_REJUDGE_CHECKOUT:
                // Fall through
            case RUN_CHECKOUT:
                RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
                RunResultFiles[] runResultFiles = (RunResultFiles[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
                contest.updateRun(run, runFiles, whoCheckedOut, runResultFiles);
                break;

            case RUN_CHECKOUT_NOTIFICATION:
                
                // only process this notification if the run was checked out by someone else
                if (!contest.getClientId().equals(whoCheckedOut)) {
                    contest.updateRun(run, whoCheckedOut);
                }
                
                break;
            default:
                controller.getLog().log(Log.WARNING, "Attempted to runCheckout with packet: " + packet);
                break;

        }

        if (isServer()) {
            sendToJudgesAndOthers(packet, false);
        }

    }

    private void handleFetchedRun (Packet packet, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException, FileSecurityException{
        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
        ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        RunResultFiles[] runResultFiles = (RunResultFiles[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        contest.updateRun(run, runFiles, whoCheckedOut, runResultFiles);
    }
    
    /**
     * Re-judge run request, parse packet, attempt to checkout run.
     * 
     * @param packet
     * @param connectionHandlerID
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void requestRejudgeRun(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {

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

                    securityCheck(Permission.Type.REJUDGE_RUN, whoRequestsRunId, connectionHandlerID);

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
        if (siteNumber != null) {
            try {
                controller.getLog().log(Log.INFO, "Client " + packet.getSourceId() + " requests reconnection to site " + siteNumber);
                controller.sendServerLoginRequest(siteNumber.intValue());
            } catch (Exception e) {
                controller.getLog().log(Log.WARNING, "Unable to send reconnection request to ", e);
            }
        }
    }

    private void handleMessagePacket(Packet packet) throws Exception {
        
        if (isServer()){

            String message = (String) PacketFactory.getObjectValue(packet, PacketFactory.MESSAGE_STRING);
            Area area = (Area) PacketFactory.getObjectValue(packet, PacketFactory.MESSAGE_AREA);

            if (isThisSite(packet.getDestinationId().getSiteNumber())) {
                if (!packet.getDestinationId().getClientType().equals(ClientType.Type.SERVER)) {
                    controller.sendToClient(packet);
                }
            } else {
                Packet messagePacket = PacketFactory.createMessage(contest.getClientId(), packet.getDestinationId(), area, message);
                int siteNumber = packet.getDestinationId().getSiteNumber();
                controller.sendToRemoteServer(siteNumber, messagePacket);
            }
            
            contest.addMessage(area, packet.getSourceId(), packet.getDestinationId(), message);
            
        } else {
            String message = (String) PacketFactory.getObjectValue(packet, PacketFactory.MESSAGE_STRING);
            Area area = (Area) PacketFactory.getObjectValue(packet, PacketFactory.MESSAGE_AREA);
            if (message == null){
                throw new Exception ("Message null in packet "+packet);
            } else {
                contest.addMessage(area, packet.getSourceId(), packet.getDestinationId(), message);
            }
        }
    }
    
    private void insureProfileDirectory(Profile profile) {
        
        String profileDirectory = profile.getProfilePath();
        
        if (! new File(profileDirectory).isDirectory()){
            new File(profileDirectory).mkdirs();
        }
    }

    private void loginSuccess(Packet packet, ConnectionHandlerID connectionHandlerID, ClientId fromId) throws IOException, ClassNotFoundException, FileSecurityException {
        
        ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (!contest.isLoggedIn()) {
            // Got the first LOGIN_SUCCESS, first connection into server.

            if (isServer(clientId)) {
                String uberSecretatPassworden = (String) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_PASSWORD);
                if (uberSecretatPassworden == null) {
                    StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR ");
                    System.err.println("FATAL ERROR - Contest Security Password is null ");
                    System.exit(44);
                }
                
                Profile theProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);
                if (theProfile == null) {
                    StaticLog.getLog().log(Log.SEVERE, "FATAL ERROR - Profile is null");
                    System.err.println("FATAL ERROR - Profile is null ");
                    System.exit(44);
                }
                
                insureProfileDirectory(theProfile);
                theProfile.setSiteNumber(clientId.getSiteNumber());
                
                String baseDirectoryName = theProfile.getProfilePath() + File.separator + "db." + clientId.getSiteNumber();

                FileSecurity fileSecurity = new FileSecurity(baseDirectoryName);
                controller.initializeStorage(fileSecurity);

                try {
                    fileSecurity.verifyPassword(uberSecretatPassworden.toCharArray());

                } catch (FileSecurityException fileSecurityException) {
                    if (fileSecurityException.getMessage().equals(FileSecurity.KEY_FILE_NOT_FOUND)) {

                        try {
                            fileSecurity.saveSecretKey(uberSecretatPassworden.toCharArray());
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

                contest.setStorage(fileSecurity);
                contest.setContestPassword(uberSecretatPassworden);
            }

            contest.setSiteNumber(clientId.getSiteNumber());
            ContestLoader loader = new ContestLoader();
            loader.loadDataIntoModel(contest, controller, packet, connectionHandlerID);
            loader = null;
            
            if (isServer()) {
                storeProfiles();
            }

            otherLoginActivities(packet, connectionHandlerID);
            startEvalLog();
            info(" handlePacket original LOGIN_SUCCESS after -- all settings loaded ");

            if (isServer()) {

                if (contest.isLocalLoggedIn(fromId)) {
                    contest.removeLogin(fromId);
                }

                if (contest.isRemoteLoggedIn(fromId)) {
                    contest.removeRemoteLogin(fromId);
                }

                // Add the other site as a local login
                contest.addLocalLogin(fromId, connectionHandlerID);

                // Send settings packet to the server we logged into
                controller.sendToClient(createContestSettingsPacket(packet.getSourceId()));
                
                sendRequestForRunfFiles (packet, packet.getSourceId().getSiteNumber());
            }

        } else if (isServer(packet.getDestinationId())) {
            // Got a LOGIN_SUCCESS from another server
            if (contest.isRemoteLoggedIn(fromId)) {
                contest.removeRemoteLogin(fromId);
            }

            // Add the other site as a local login
            contest.addLocalLogin(fromId, connectionHandlerID);

            ContestLoader loader = new ContestLoader();
            loadSettingsFromRemoteServer(loader, packet, connectionHandlerID);
            loader = null;
         

            controller.sendToClient(createContestSettingsPacket(packet.getSourceId()));

        } else {
            // If logged in client, should not get another LOGIN_SUCCESS
            Exception ex = new Exception("Client " + contest.getClientId() + " received unexpected packet, not logged in but got a " + packet);
            controller.getLog().log(Log.WARNING, ex.getMessage(), ex);
        }
    }

    private void startEvalLog() {
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

    private void updateContestClock(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {

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

        } else {
            controller.sendToTeams(packet);
            if (isServer()) {
                sendToJudgesAndOthers(packet, true);
            }
        }

        if (isServer()) {
            contest.storeConfiguration(controller.getLog());
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
                if (contest.isLocalLoggedIn(clientToLogoffId)) {
                    controller.removeConnection(connectionHandlerID);
                } else if (! isServer(clientToLogoffId)) {
                    // send client logoff to other site
                    controller.sendToRemoteServer(clientToLogoffId.getSiteNumber(), packet);
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void updateRun(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);

        // TODO add runResultsFiles to updated run results.
        // RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
        ClientId whoChangedRun = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {
            if (isThisSite(run)) {

                // TODO security check updateRun

                // Account account = contest.getAccount(packet.getSourceId());
                // if (account.isAllowed(Permission.Type.EDIT_RUN)){
                // // ok to update run
                // }

                securityCheck(Permission.Type.EDIT_RUN, packet.getSourceId(), connectionHandlerID);

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
                    throw new SecurityException("Non-admin user " + packet.getSourceId() + " attempted to update run " + run);
                }

                Run theRun = contest.getRun(run.getElementId());
                Packet runUpdatedPacket = PacketFactory.createRunUpdateNotification(contest.getClientId(), PacketFactory.ALL_SERVERS, theRun, whoChangedRun);
                sendToJudgesAndOthers(runUpdatedPacket, true);
                
                /**
                 * Send Judgement Notification to Team or not.
                 */

                if (theRun.isJudged() && theRun.getJudgementRecord().isSendToTeam()) {
                    
                    Packet notifyPacket = PacketFactory.clonePacket(contest.getClientId(), run.getSubmitter(), runUpdatedPacket);
                    sendJudgementToTeam (notifyPacket, theRun);
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
     * Login from a server,
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
                        } else {
                            contest.addRemoteLogin(whoLoggedIn, connectionHandlerID);
                            sendToJudgesAndOthers(packet, false);
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

    /**
     * Got logoff packet from either a server or client.
     * @param packet
     */
    private void logoutClient(Packet packet) {

        ClientId whoLoggedOff = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        if (isServer()) {

            // TODO Security code - only allow certain users to logoff other users
            // TEST CASE - attempt to logoff client as say a team 
//            throw new SecurityException("Client " + contest.getClientId() + " attempted to logoff another client "+whoLoggedOff);
            
            if (isServer(whoLoggedOff)){

                // Special logic, ignore any logoff from any client about any server.
                // Only disconnect logic will work on a server.
                
                controller.getLog().info("No logoff server allowed, logoff packet "+packet+" ignored");
                
            } else if (contest.isLocalLoggedIn(whoLoggedOff)) {
                // Logged into this server, so we log them off and send out packet.
                controller.logoffUser(whoLoggedOff);
                
            } else {
                // Log them off, only notify local clients.
                if (isServer(packet.getSourceId()) && whoLoggedOff.getSiteNumber() == packet.getSourceId().getSiteNumber()){
                    /**
                     * Logoff from a remote server for that site's remote client
                     * and notify local clients
                     */
                    contest.removeRemoteLogin(whoLoggedOff);
                    sendToJudgesAndOthers(packet, false);
                } else {
                    /**
                     * In this block, client is not logged in locally, client is not a notification
                     * from another server.
                     */
                    // Send to the server where client is logged in.
                    controller.sendToRemoteServer(whoLoggedOff.getSiteNumber(), packet);
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void sendJudgementUpdate(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {

        Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
        ClientId whoModifiedRun = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);

        if (isServer()) {
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void sendRunUpdateNotification(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {

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
     * 
     * @param packet
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void generateAccounts(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {

        ClientType.Type type = (ClientType.Type) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_TYPE);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        Integer count = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.COUNT);
        Integer startCount = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.START_COUNT);
        Boolean active = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.CREATE_ACCOUNT_ACTIVE);

        if (isServer()) {

            if (isThisSite(siteNumber)) {

                // get vector of new accounts.
                Vector<Account> accountVector = contest.generateNewAccounts(type.toString(), count.intValue(), startCount.intValue(), active);
                Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);

                contest.storeConfiguration(controller.getLog());

                Packet newAccountsPacket = PacketFactory.createAddSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, accounts);
                sendToJudgesAndOthers(newAccountsPacket, true);

            } else {

                controller.sendToRemoteServer(siteNumber.intValue(), packet);
            }

        } else {
            throw new SecurityException("Client " + contest.getClientId() + " was sent generate account packet " + packet);
        }
    }

    /**
     * This starts the contest and sends notification to other servers/clients.
     * 
     * @param connectionHandlerID
     * @param contestTime
     * @param sourceServerId
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void startContest(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {

        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        if (packet.getType().equals(Type.START_ALL_CLOCKS)) {
            siteNumber = new Integer(contest.getSiteNumber());
        }

        if (isThisSite(siteNumber)) {

            securityCheck(Permission.Type.START_CONTEST_CLOCK, packet.getSourceId(), connectionHandlerID);

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
            contest.storeConfiguration(controller.getLog());
        }
    }

    /**
     * This stops the contest and sends notification to other servers/clients.
     * 
     * @param connectionHandlerID
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void stopContest(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {
        ClientId who = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        Integer siteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        if (packet.getType().equals(Type.STOP_ALL_CLOCKS)) {
            siteNumber = new Integer(contest.getSiteNumber());
        }

        if (isThisSite(siteNumber)) {

            securityCheck(Permission.Type.STOP_CONTEST_CLOCK, who, connectionHandlerID);

            contest.stopContest(siteNumber);
            ContestTime updatedContestTime = contest.getContestTime(siteNumber);
            controller.getLog().info("Clock STOPPED by " + who + " elapsed = " + updatedContestTime.getElapsedTimeStr());
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

        if (isServer()) {
            contest.storeConfiguration(controller.getLog());
        }
    }

    private void deleteSetting(Packet packet) {

        // TODO code deleteSetting

    }

    /**
     * Add a new setting from another server.
     * 
     * @param packet
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void addNewSetting(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {

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
            if (problemDataFiles != null) {
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
        
        Profile profile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);
        if (profile != null) {
            contest.addProfile(profile);
        }
        
        Packet updatePacket = null;

        Account oneAccount = (Account) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT);
        if (oneAccount != null) {
            if (isServer()) {
                if (isThisSite(oneAccount)) {
                    ClientId clientId = oneAccount.getClientId();
                    
                    // Add account, this assigns the new account a client number.
                    Vector<Account> accountVector = contest.generateNewAccounts(clientId.getClientType().toString(), 1, true);
                    Account addedAccount = accountVector.firstElement();

                    // Update/clone new account
                    addedAccount.setDisplayName(oneAccount.getDisplayName());
                    addedAccount.setPassword(oneAccount.getPassword());
                    addedAccount.clearListAndLoadPermissions(oneAccount.getPermissionList());
                    contest.updateAccount(addedAccount);

                    // create updated packet to be sent to others, if this is the Server.
                    updatePacket = PacketFactory.createUpdateSetting(contest.getClientId(), PacketFactory.ALL_SERVERS, contest.getAccount(addedAccount.getClientId()));
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
                        Packet newPacket;
                        if (account.getClientId().getClientType().equals(ClientType.Type.TEAM)) {
                            newPacket = PacketFactory.createUpdateSetting(contest.getClientId(), account.getClientId(), contest.getAccount(account.getClientId()));
                        } else {
                            newPacket = PacketFactory.clonePacket(contest.getClientId(), account.getClientId(), packet);
                        }
                        controller.sendToClient(newPacket);
                    }
                }
            }
        }

        ClientSettings clientSettings = (ClientSettings) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS);
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
            
            contest.storeConfiguration(controller.getLog());
            
            storeProfiles();
            
            boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());

            if (updatePacket != null) {
                sendToJudgesAndOthers(updatePacket, sendToOtherServers);
            } else {
                Packet addPacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
                sendToJudgesAndOthers(addPacket, sendToOtherServers);
                if (sendToTeams) {
                    controller.sendToTeams(addPacket);
                }
            }
        }
    }

    /**
     * Handle a UPDATE_SETTING packet.
     * 
     * @param packet
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void updateSetting(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {

        boolean sendToTeams = false;
        
        Packet oneUpdatePacket = null;

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
            if (problemDataFiles != null) {
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
        
        Profile profile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);
        if (profile != null) {
            contest.updateProfile(profile);
            if (contest.getProfile().equals(profile)){
                contest.setProfile(profile);
            }
            if (isServer()){
                storeProfiles();
            }
        }

        Account oneAccount = (Account) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT);
        if (oneAccount != null) {
            contest.updateAccount(oneAccount);
            if (isThisSite(oneAccount.getClientId().getSiteNumber())) {
                if (isServer()) {
                    oneUpdatePacket = PacketFactory.clonePacket(contest.getClientId(), oneAccount.getClientId(), packet);
                    controller.sendToClient(oneUpdatePacket);
                }
            }
        }
        Account[] accounts = (Account[]) PacketFactory.getObjectValue(packet, PacketFactory.ACCOUNT_ARRAY);
        if (accounts != null) {
            // TODO change these from Vector to something lightweight
            Vector<Account> addAccountsVector = new Vector<Account>();
            Vector<Account> updateAccountsVector = new Vector<Account>();
            for (Account account : accounts) {
                // split this into 2 lists, then call the bulk version
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

        ClientSettings clientSettings = (ClientSettings) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_SETTINGS);
        if (clientSettings != null) {
            contest.updateClientSettings(clientSettings);
            if (isServer()) {

                ClientId toId = clientSettings.getClientId();
                // judges will get this with sendToJudgesAndOthers below
                if (!isJudge(toId) && contest.isLocalLoggedIn(toId)) {
                    Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), toId, packet);
                    controller.sendToClient(updatePacket);
                }
            }
        }

        if (isServer()) {

            contest.storeConfiguration(controller.getLog());
            
            storeProfiles();
            
            boolean sendToOtherServers = isThisSite(packet.getSourceId().getSiteNumber());
            
            if (oneUpdatePacket != null) {
                sendToJudgesAndOthers(oneUpdatePacket, sendToOtherServers);
            } else {
                Packet updatePacket = PacketFactory.clonePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, packet);
                sendToJudgesAndOthers(updatePacket, sendToOtherServers);

                if (sendToTeams) {
                    controller.sendToTeams(updatePacket);
                }
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

        if (isServer()) {
            // If I am a server forward to clients on this site.

            controller.sendToAdministrators(packet);
            controller.sendToJudges(packet);
            controller.sendToScoreboards(packet);
            if (sendToServers) {
                controller.sendToServers(packet);
            }
        } else {
            info("Warning - tried to send packet to others (as non server) " + packet);
            Exception ex = new Exception("User " + packet.getSourceId() + " tried to send packet to judges and others");
            controller.getLog().log(Log.WARNING, "Warning - tried to send packet to others (as non server) " + packet, ex);
        }
    }

    /**
     * Send to spectators and servers
     * @param packet
     * @param sendToServers
     */
    public void sendToSpectatorsAndSites(Packet packet, boolean sendToServers) {

        if (isServer()) {
            controller.sendToSpectators(packet);
            if (sendToServers) {
                controller.sendToServers(packet);
            }
        } else {
            info("Warning - tried to send packet to others (as non server) " + packet);
            Exception ex = new Exception("User " + packet.getSourceId() + " tried to send packet to judges and others");
            controller.getLog().log(Log.WARNING, "Warning - tried to send packet to others (as non server) " + packet, ex);
        }
    }


    private boolean isSuperUser(ClientId id) {
        return id.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }

    public void cancelRun(Packet packet, Run run, ClientId whoCanceledRun, ConnectionHandlerID connectionHandlerID) throws IOException, ClassNotFoundException, FileSecurityException {

        if (isServer()) {

            if (!isThisSite(run)) {

                controller.sendToRemoteServer(run.getSiteNumber(), packet);

            } else {

                // TODO handle Security violation

                /**
                 * If there is a problem then there is no requirement (due to lack of analysis) to notify the client canceling the run.
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

                    Packet violationPacket = PacketFactory.createSecurityMessagePacket(contest.getClientId(), PacketFactory.ALL_SERVERS, e.getMessage(), whoCanceledRun, connectionHandlerID, null,
                            packet);

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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void cancelClarificationCheckOut(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {
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
                // securityCheck(Permission.Type.ANSWER_CLARIFICATION, whoCancelledIt, connectionHandlerID);

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

    private void sendClarificationAvailable(Packet packet) throws IOException, ClassNotFoundException, FileSecurityException {
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
     * 
     * @param run
     * @param judgementRecord
     * @param runResultFiles
     * @param whoJudgedId
     * @param connectionHandlerID
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    protected void judgeRun(Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, 
            ClientId whoJudgedId, ConnectionHandlerID connectionHandlerID, Packet packet) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {

        if (isServer()) {

            if (!isThisSite(run)) {

                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                Packet judgementPacket = PacketFactory.clonePacket(contest.getClientId(), serverClientId, packet);
                controller.sendToRemoteServer(run.getSiteNumber(), judgementPacket);

            } else {
                // This site's run

                securityCheck(Permission.Type.JUDGE_RUN, whoJudgedId, connectionHandlerID);

                judgementRecord.setWhenJudgedTime(contest.getContestTime().getElapsedMins());

                contest.addRunJudgement(run, judgementRecord, runResultFiles, whoJudgedId);

                Run theRun = contest.getRun(run.getElementId());

                if (judgementRecord.isComputerJudgement()) {
                    if (contest.getProblem(theRun.getProblemId()).isManualReview()) {
                        if (contest.getProblem(theRun.getProblemId()).isPrelimaryNotification()) {

                            // Do not send RunResultFiles to the team
                            RunResultFiles rrf = runResultFiles;
                            if (run.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                                rrf = null;
                            }
                            Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), theRun, judgementRecord, rrf);
                            
                            sendJudgementToTeam (judgementPacket, theRun);
                        }
                    } else {

                        // Do not send RunResultFiles to the team
                        RunResultFiles rrf = runResultFiles;
                        if (run.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                            rrf = null;
                        }
                        Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), theRun, judgementRecord, rrf);
                        sendJudgementToTeam (judgementPacket, theRun);
                    }
                } else {

                    // Do not send RunResultFiles to the team
                    RunResultFiles rrf = runResultFiles;
                    if (run.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                        rrf = null;
                    }
                    Packet judgementPacket = PacketFactory.createRunJudgement(contest.getClientId(), run.getSubmitter(), theRun, judgementRecord, rrf);
                    sendJudgementToTeam (judgementPacket, theRun);
                }

                Packet judgementUpdatePacket = PacketFactory.createRunJudgmentUpdate(contest.getClientId(), PacketFactory.ALL_SERVERS, theRun, whoJudgedId);
                sendToJudgesAndOthers(judgementUpdatePacket, true);
            }

        } else {
            contest.updateRun(run, judgementRecord.getJudgerClientId());
        }
    }

    /**
     * Send Judgement to team
     * @param judgementPacket
     * @param run
     */
    private void sendJudgementToTeam(Packet judgementPacket, Run run) {
        
        if (run.isJudged() && run.getJudgementRecord().isSendToTeam()) {
            JudgementNotificationsList judgementNotificationsList = contest.getContestInformation().getJudgementNotificationsList();
            
            if (! RunUtilities.supppressJudgement(judgementNotificationsList, run, contest.getContestTime())){
                // Send to team who sent it, send to other server if needed.
                controller.sendToClient(judgementPacket);
            } else {
                controller.getLog().info("Notification not sent to "+run.getSubmitter()+" for run "+run);
            }
        } else {
            controller.getLog().warning("Attempted to send back unjudged run to team "+run);
        }
    }

    /**
     * Checkout a run.
     * 
     * @see #checkoutRun(Packet, Run, ClientId, boolean)
     * @param packet
     * @param run
     * @param whoRequestsRunId
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void requestRun(Packet packet, Run run, ClientId whoRequestsRunId, ConnectionHandlerID connectionHandlerID, boolean computerJudge) throws ContestSecurityException, IOException,
            ClassNotFoundException, FileSecurityException {
        checkoutRun(packet, run, whoRequestsRunId, false, computerJudge, connectionHandlerID);
    }
    
    private void requestClarification(Packet packet, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException, IOException, ClassNotFoundException, FileSecurityException {
        ElementId clarificationId = (ElementId) PacketFactory.getObjectValue(packet, PacketFactory.REQUESTED_CLARIFICATION_ELEMENT_ID);
        ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
        // Boolean readOnly = (Boolean) PacketFactory.getObjectValue(packet, PacketFactory.READ_ONLY);
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

                    // TODO send read only clar to them
                    info("requestClarification read-only not implemented, yet");
                } else {
                    try {
                        securityCheck(Permission.Type.ANSWER_CLARIFICATION, requestFromId, connectionHandlerID);

                        theClarification = contest.checkoutClarification(clarification, requestFromId);

                        // send to Judge
                        Packet checkOutPacket = PacketFactory.createCheckedOutClarification(contest.getClientId(), requestFromId, theClarification, requestFromId);
                        controller.sendToClient(checkOutPacket);

                        // TODO change this packet type so it is not confused with the actual checked out run.

                        sendToJudgesAndOthers(checkOutPacket, true);
                    } catch (ClarificationUnavailableException clarUnavailableException) {
                        controller.getLog().info("clarUnavailableException " + clarUnavailableException.getMessage());
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
     * if readOnly is true will fetch run without setting the run as "being judged".
     * 
     * @param packet
     * @param run
     * @param whoRequestsRunId
     * @param readOnly -
     *            get a read only copy (aka do not checkout/select run).
     * @param connectionHandlerID
     * @throws ContestSecurityException
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    private void checkoutRun(Packet packet, Run run, ClientId whoRequestsRunId, boolean readOnly, boolean computerJudge, ConnectionHandlerID connectionHandlerID) throws ContestSecurityException,
            IOException, ClassNotFoundException, FileSecurityException {

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
                        securityCheck(Permission.Type.JUDGE_RUN, whoRequestsRunId, connectionHandlerID);

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

                        Packet checkOutNotificationPacket = PacketFactory.createCheckedOutRunNotification(contest.getClientId(), whoRequestsRunId, theRun, whoRequestsRunId);
                        sendToJudgesAndOthers(checkOutNotificationPacket, true);
                        
                    } catch (RunUnavailableException runUnavailableException) {
                        controller.getLog().info("runUnavailableException " + runUnavailableException.getMessage());
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
     * 
     * Loads settings from other server, submissions, etc.  Sends
     * out a login packet to any "new" servers which this site
     * is not logged into. 
     * 
     * @param packet
     * @param connectionHandlerID
     */
    private void loadSettingsFromRemoteServer(ContestLoader loader, Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        int remoteSiteNumber = packet.getSourceId().getSiteNumber();

        loader.addRemoteContestTimesToModel(contest, controller, packet, remoteSiteNumber);

        loader.addRemoteRunsToModel(contest, controller, packet);
        
        sendRequestForRunfFiles (packet, remoteSiteNumber);

        loader.addRemoteClarificationsToModel(contest, controller, packet, remoteSiteNumber);

        loader.addRemoteAccountsToModel(contest, controller, packet, remoteSiteNumber);

        // difficult to know which site these connections are for...
        // addConnectionIdsToModel(packet);

        loader.addRemoteAllClientSettingsToModel(contest, controller, packet, remoteSiteNumber);

        loader.addRemoteLoginsToModel(contest, controller, packet, remoteSiteNumber);
    }

    /**
     * Send request for run files, if needed.
     * 
     * Determines whether this site has all run files for a remote site.
     * If there is a need to sync, will send a packet to sync with that
     * other site.
     *  
     * @param packet
     * @param remoteSiteNumber
     */
    private void sendRequestForRunfFiles(Packet packet, int remoteSiteNumber) {

        Run[] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
        if (runs != null) {

            RunFilesList list = new RunFilesList(contest.getStorage());
            int localLastRunId = list.getLastRunFilesRunId(remoteSiteNumber);
            int lastRemoteRunId = getLastRunId(runs, remoteSiteNumber);

            if (localLastRunId < lastRemoteRunId) {
                sendRunFilesRequestToServer(remoteSiteNumber, localLastRunId);
            } // else { no files to fetch
            
        } // else no runs from that site, ok.
    }
    
    /**
     * Get maximum run id for the site.
     * 
     * @param runs list of runs
     * @param siteNumber 
     * @return 0 if no runs, else the last run id at the site.
     */
    private int getLastRunId(Run[] runs, int siteNumber) {
        int maxRunId = 0;
        
        for (Run run : runs){
            if (run.getSiteNumber() == siteNumber){
                maxRunId = Math.max(run.getNumber(), maxRunId);
            }
        }
        return maxRunId;
    }

    /**
     * Send a request to remote server for RunFiles.
     * 
     * This sends a packet that request that any run id which is lastRunId or greater be sent to this server. <br>
     * 
     * @param siteNumber
     *            site number to request RunFiles from
     * @param lastRunId
     *            last run id on this site
     */
    private void sendRunFilesRequestToServer(int siteNumber, int lastRunId) {

        ClientId remoteServerId = new ClientId(siteNumber, ClientType.Type.SERVER, 0);
        Packet fetchPacket = PacketFactory.createFetchRunFilesPacket (contest.getClientId(), remoteServerId, lastRunId);
        System.out.println("Send req for run files "+fetchPacket);
        controller.sendToClient(fetchPacket);
    }

    /**
     * Send run files to other site server.
     * 
     * @param siteNumber
     * @param lastRunId
     */
    private void sendRunFilesToServer(int siteNumber, int lastRunId) {

        Run[] runs = getLocalRunsStartingAt(lastRunId);
        RunFiles[] files = getRunFiles(runs);
        ClientId remoteServerId = new ClientId(siteNumber, ClientType.Type.SERVER, 0);
        Packet runFilesPacket = PacketFactory.createRunFilesPacket(contest.getClientId(), remoteServerId, files);
        controller.sendToClient(runFilesPacket);
    }

    private RunFiles[] getRunFiles(Run[] runs) {
        
        Vector<RunFiles> runFilesList = new Vector<RunFiles>();

        for (Run run : runs) {
            try {
                RunFiles runfiles = contest.getRunFiles(run);
                runFilesList.add(runfiles);
            } catch (Exception e) {
                controller.logWarning("Unable to get RunFiles for run "+run, e);
            }
        }

        return (RunFiles[]) runFilesList.toArray(new RunFiles[runFilesList.size()]);
    }

    /**
     * Returns local run list starting at run lastRunId. 
     * 
     * @param lastRunId
     * @return
     */
    private Run[] getLocalRunsStartingAt(int lastRunId) {

        Filter filter = new Filter();
        filter.setSiteNumber(contest.getSiteNumber());

        Run[] runs = filter.getRuns(contest.getRuns());

        Vector<Run> listOfRuns = new Vector<Run>();

        for (Run run : runs) {
            if (run.getNumber() > lastRunId) {
                listOfRuns.add(run);
            }
        }

        return (Run[]) listOfRuns.toArray(new Run[listOfRuns.size()]);
    }




    private void otherLoginActivities(Packet packet, ConnectionHandlerID connectionHandlerID) {

        if (contest.isLoggedIn()) {

            // insure that this site's contest time exists
            if (contest.getContestTime() == null) {
                ContestTime contestTime = new ContestTime();
                contestTime.setSiteNumber(contest.getSiteNumber());
                contest.addContestTime(contestTime);
            }

            // show main UI
            controller.startMainUI(contest.getClientId());

            // Login to other sites
            if (isServer()) {
                loginToOtherSites(packet);
            }
        } else {
            String message = "Trouble logging in, check logs";
            contest.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }

    }

    /**
     * Merge profiles.properties and model/contest and store to disk.
     * <br><br>
     * Merges the contest profile list and the list of profiles stored
     * to the profiles.properties file.
     * <br><br>
     * Creates the profiles.properties file if needed.
     */
    private void storeProfiles() {

        ProfileManager manager = new ProfileManager();

        try {

            Profile[] list = new Profile[0];
            if (manager.hasDefaultProfile()){
                list = manager.load();
            }

            if (list.length <= 1 && contest.getProfiles().length == 1) {
                manager.storeDefaultProfile(contest.getProfile());
            } else {
                manager.mergeProfiles(contest);
                manager.store(contest.getProfiles(), contest.getProfile());
            }

        } catch (Exception e) {
            controller.logWarning("Problem saving/loading profiles from profile properties file", e);
            e.printStackTrace();
        }

    }

    /**
     * Update sites in model.
     * 
     * There may be the case where more servers are defined
     * on a switch of a profile.
     * 
     * @param packet
     */
    private void updateSitesToModel(Packet packet) {
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    info("updateSitesToModel " + site);
                    contest.updateSite(site);
                }
            }
        } catch (Exception e) {
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
            ClientId remoteServerId = new ClientId(site.getSiteNumber(), ClientType.Type.SERVER, 0);

            if (contest.isLocalLoggedIn(remoteServerId)) {
                info("loginToOtherSites site " + site.getSiteNumber() + " already logged in, not attempting to login");
            } else if (!isThisSite(site.getSiteNumber())) {
                try {
                    if (contest.isRemoteLoggedIn(remoteServerId)) {
                        controller.sendServerLoginRequest(site.getSiteNumber());
                    } else if (contest.isLocalLoggedIn(remoteServerId)) {
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
    private ClientId[] getAllRemoteLoggedInUsers() {

        Vector<ClientId> clientList = new Vector<ClientId>();

        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId[] users = contest.getRemoteLoggedInClients(ctype);
            for (ClientId clientId : users) {
                clientList.addElement(clientId);
            }
        }
        if (clientList.size() == 0) {
            return new ClientId[0];
        } else {
            ClientId[] clients = (ClientId[]) clientList.toArray(new ClientId[clientList.size()]);
            return clients;
        }
    }

    /**
     * Return an array of all logged in users.
     * 
     * @return array of clientId's.
     */
    private ClientId[] getAllLocalLoggedInUsers() {

        Vector<ClientId> clientList = new Vector<ClientId>();

        for (ClientType.Type ctype : ClientType.Type.values()) {

            ClientId[] users = contest.getLocalLoggedInClients(ctype);
            for (ClientId clientId : users) {
                clientList.addElement(clientId);
            }
        }
        if (clientList.size() == 0) {
            return new ClientId[0];
        } else {
            ClientId[] clients = (ClientId[]) clientList.toArray(new ClientId[clientList.size()]);
            return clients;
        }
    }

    Packet createContestSettingsPacket(ClientId clientId, Packet packet) {
        return PacketFactory.createContestSettingsPacket(contest.getClientId(), clientId, packet);
    }

    public Packet createContestSettingsPacket(ClientId clientId) {
        return PacketFactory.createContestSettingsPacket(contest.getClientId(), clientId, createLoginSuccessPacket(clientId, null));
    }

    public ContestLoginSuccessData createContestLoginSuccessData(IInternalContest inContest, ClientId clientId, String contestSecurityPassword) {

        Run[] runs = null;
        Clarification[] clarifications = null;
        ProblemDataFiles[] problemDataFiles = new ProblemDataFiles[0];
        ClientSettings[] clientSettings = null;
        Account[] accounts = null;
        Site[] sites = null;
        Profile [] profiles = null;

        if (inContest.getClientSettings(clientId) == null) {
            ClientSettings clientSettings2 = new ClientSettings(clientId);
            clientSettings2.put("LoginDate", new Date().toString());
            inContest.addClientSettings(clientSettings2);
        }

        /**
         * This is where client specific settings are created before sending them to client.
         */

        if (clientId.getClientType().equals(ClientType.Type.TEAM)) {
            runs = inContest.getRuns(clientId);
            clarifications = inContest.getClarifications(clientId);
            clientSettings = new ClientSettings[1];
            clientSettings[0] = inContest.getClientSettings(clientId);
            accounts = new Account[1];
            accounts[0] = inContest.getAccount(clientId);
            // re-build the site list without passwords, all they really need is the number & name
            Site[] realSites = inContest.getSites();
            sites = new Site[realSites.length];
            for (int i = 0; i < realSites.length; i++) {
                sites[i] = new Site(realSites[i].getDisplayName(), realSites[i].getSiteNumber());
            }
            Profile profile = inContest.getProfile();
            profiles = new Profile[1];
            profiles[0] = profile;
        } else {
            runs = inContest.getRuns();
            clarifications = inContest.getClarifications();
            problemDataFiles = inContest.getProblemDataFiles();
            clientSettings = inContest.getClientSettingsList();
            accounts = getAllAccounts();
            sites = inContest.getSites();
            profiles = inContest.getProfiles();
        }

        ContestLoginSuccessData contestLoginSuccessData = new ContestLoginSuccessData();
        contestLoginSuccessData.setAccounts(accounts);
        contestLoginSuccessData.setBalloonSettingsArray(inContest.getBalloonSettings());
        contestLoginSuccessData.setClarifications(clarifications);
        contestLoginSuccessData.setClientSettings(clientSettings);
        contestLoginSuccessData.setConnectionHandlerIDs(inContest.getConnectionHandleIDs());
        contestLoginSuccessData.setContestTimes(inContest.getContestTimes());
        contestLoginSuccessData.setGroups(inContest.getGroups());
        contestLoginSuccessData.setJudgements(inContest.getJudgements());
        contestLoginSuccessData.setLanguages(inContest.getLanguages());
        contestLoginSuccessData.setRemoteLoggedInUsers(getAllRemoteLoggedInUsers());
        contestLoginSuccessData.setLocalLoggedInUsers(getAllLocalLoggedInUsers());
        contestLoginSuccessData.setProblemDataFiles(problemDataFiles);
        contestLoginSuccessData.setProblems(inContest.getProblems());
        contestLoginSuccessData.setRuns(runs);
        contestLoginSuccessData.setSites(sites);
        contestLoginSuccessData.setGeneralProblem(inContest.getGeneralProblem());
        contestLoginSuccessData.setContestIdentifier(inContest.getContestIdentifier().toString());
        contestLoginSuccessData.setProfile(inContest.getProfile());
        contestLoginSuccessData.setProfiles(inContest.getProfiles());

        contestLoginSuccessData.setContestTime(inContest.getContestTime());
        contestLoginSuccessData.setSiteNumber(inContest.getSiteNumber());
        contestLoginSuccessData.setContestInformation(inContest.getContestInformation());

        if (isServer(clientId)) {
            contestLoginSuccessData.setContestSecurityPassword(contestSecurityPassword);
        }

        return contestLoginSuccessData;
    }

    /**
     * Create a login success packet.
     * 
     * 
     * @param clientId
     * @return Packet containing contest settings
     */
    public Packet createLoginSuccessPacket(ClientId targetClientId, String contestSecurityPassword) {

        ContestLoginSuccessData data = createContestLoginSuccessData(contest, targetClientId, contestSecurityPassword);

        Packet loginSuccessPacket = PacketFactory.createLoginSuccess(contest.getClientId(), targetClientId, contest.getContestTime(), contest.getSiteNumber(), contest.getContestInformation(), data);

        return loginSuccessPacket;
    }
    
    /**
     * Got a status from the other server.
     * @param packet
     * @param connectionHandlerID
     */
    private void handleServerStatus(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        Site site = (Site) PacketFactory.getObjectValue(packet, PacketFactory.SITE);
        Profile inProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);
        Status status = (Status) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE_STATUS);
        
        contest.updateSiteStatus(site, inProfile, status);
        
        if (isServer()){
            sendToJudgesAndOthers(packet, false);
        }
    }

    /**
     * Handle incoming request on a server.
     * 
     * @param packet
     * @param connectionHandlerID
     */
    private void handleRequestServerStatus(Packet packet, ConnectionHandlerID connectionHandlerID) {
        
        int targetSiteNumber = (Integer) PacketFactory.getObjectValue(packet, PacketFactory.SITE_NUMBER);
        ClientId remoteServerId = new ClientId(targetSiteNumber, ClientType.Type.SERVER, 0);
        
        if (isThisSite(targetSiteNumber)){
            
            Profile expectedProfile = (Profile) PacketFactory.getObjectValue(packet, PacketFactory.PROFILE);
            sendStatusToServers(packet, expectedProfile);
 
        } else {
            /**
             * Send to target server.
             */
            Packet clonePacket = PacketFactory.clonePacket(getServerClientId(), remoteServerId, packet);
            controller.sendToClient(clonePacket);
        }

    }

    /**
     * Send a status packe to all logged in servers.
     * 
     * @param packet
     * @param expectedProfile
     */
    private void sendStatusToServers(Packet packet, Profile expectedProfile) {
        
        Status status = Status.NOTREADY;
        if (contest.getProfile().equals(expectedProfile)){
            status = Status.READY;
        }
        
        ClientId remoteServerId = new ClientId(packet.getSourceId().getSiteNumber(), ClientType.Type.SERVER, 0);
        Site site = contest.getSite(contest.getSiteNumber());
        Packet statusPacket = PacketFactory.createServerStatusPacket(getServerClientId(), remoteServerId, contest.getProfile(), status, site);
        
        controller.sendToServers(statusPacket);
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

    private boolean isJudge(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.JUDGE);
    }

    /**
     * Is this client a server.
     * 
     * @return true if is a server.
     */
    private boolean isServer() {
        return isServer(contest.getClientId());
    }

    public void info(String s) {
        // System.err.flush();
        controller.getLog().info(s);
        // System.err.println(Thread.currentThread().getName() + " " + s);
        // System.err.flush();
    }
}
