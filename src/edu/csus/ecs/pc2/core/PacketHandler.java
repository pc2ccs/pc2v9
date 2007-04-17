package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IModel;
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
 * Process packets. In {@link #handlePacket(IController, IModel, Packet, ConnectionHandlerID) handlePacket} a packet is unpacked, model is updated, and
 * controller used to send packets as needed.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public final class PacketHandler {

    private PacketHandler() {

    }

    /**
     * Take each input packet, update the model, send out packets as needed.
     * 
     * @param controller
     * @param model
     * @param packet
     * @param connectionHandlerID 
     */
    public static void handlePacket(IController controller, IModel model, Packet packet, ConnectionHandlerID connectionHandlerID) {

        Type packetType = packet.getType();

        info("handlePacket " + packet);
        PacketFactory.dumpPacket(System.err, packet);
        
        ClientId fromId = packet.getSourceId();

        if (packetType.equals(Type.MESSAGE)) {
            PacketFactory.dumpPacket(System.err, packet);
            
        } else if (packetType.equals(Type.RUN_SUBMISSION_CONFIRM)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.addRun(run);
            sendToJudgesAndOthers (model, controller, packet, isThisSite(model, run));
            
        } else if (packetType.equals(Type.RUN_SUBMISSION)) {
            // RUN submitted by team to server

            Run submittedRun = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            Run run = model.acceptRun(submittedRun, runFiles);

            // Send to team
            Packet confirmPacket = PacketFactory.createRunSubmissionConfirm(model.getClientId(), fromId, run);
            controller.sendToClient(confirmPacket);
            
            // Send to clients and servers
            sendToJudgesAndOthers(model, controller, confirmPacket, true);

        } else if (packetType.equals(Type.LOGIN_FAILED)) {
            String message = PacketFactory.getStringValue(packet, PacketFactory.MESSAGE_STRING);
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
            
        } else if (packetType.equals(Type.RUN_NOTAVAILABLE)) {
            // Run not available from server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.runNotAvailable(run);
            
            sendToJudgesAndOthers(model, controller, packet, isThisSite(model, run));
            
        } else if (packetType.equals(Type.RUN_AVAILABLE)) {
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            model.availableRun (run);
            
            sendToJudgesAndOthers(model, controller, packet, isThisSite(model,run));
            
        } else if (packetType.equals(Type.RUN_JUDGEMENT)) {
            // Judgement from judge to server
            // TODO security code insure that this judge/admin can make this change
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            JudgementRecord judgementRecord = (JudgementRecord) PacketFactory.getObjectValue(packet, PacketFactory.JUDGEMENT_RECORD);
            RunResultFiles runResultFiles = (RunResultFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_RESULTS_FILE);
            ClientId whoJudgedRunId  = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            judgeRun(run, model,controller,judgementRecord,runResultFiles, whoJudgedRunId);

        } else if (packetType.equals(Type.RUN_UNCHECKOUT)) {
            // Cancel run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId whoCanceledId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            cancelRun (run, model, controller, whoCanceledId);
          
            
        } else if (packetType.equals(Type.ADD_SETTING)) {
            addNewSetting (packet, model, controller);
            
        } else if (packetType.equals(Type.RUN_CHECKOUT)) {
            // Run from server to judge
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            checkedOutRun (model, controller, run, runFiles, whoCheckedOut);
            
            sendToJudgesAndOthers(model, controller, packet, false);
            
        } else if (packetType.equals(Type.RUN_REQUEST)) {
            // Request Run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            ClientId requestFromId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            requestRun (run, model, controller, requestFromId);
            
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {

            if (isServer(packet.getDestinationId())) {
                /**
                 * Add the originating server into login list, if
                 * this client is a server.
                 */
                model.addLogin(fromId, connectionHandlerID);
            }
            
            if (! model.isLoggedIn()){
                info(" handlePacket original LOGIN_SUCCESS before ");
                loadDataIntoModel(packet, controller, model, connectionHandlerID);
                info(" handlePacket original LOGIN_SUCCESS after -- all settings loaded "); 
            } else {
                info(" handlePacket LOGIN_SUCCESS - from another site, no update of contest data: "+packet);
            }

        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            StaticLog.unclassified("Unhandled Packet ",exception);
        }
    }
    
    /**
     * Add a new setting from another server.
     * @param packet
     * @param model
     * @param controller
     */
    private static void addNewSetting(Packet packet, IModel model, IController controller) {
        
        Site site = (Site) PacketFactory.getObjectValue(packet, PacketFactory.SITE);
        if (site != null){
            model.addSite(site);
            if (isServer(model.getClientId())){
                sendToJudgesAndOthers(model, controller, packet, false);
            }
        }
    }

    private static boolean isThisSite(IModel model, Run run) {
        return run.getSiteNumber() == model.getSiteNumber();
    }

    /**
     * Send to all logged in Judges, Admins, Boards and optionally sites.
     * 
     * This sends all sorts of packets to all logged in clients (other than
     * teams).   Typically sendToServers is set if this is the originating
     * site, if not done then a nasty circular path will occur.
     * 
     * @param model
     * @param controller
     * @param packet
     * @param sendToServers send To other server.
     */
    private static void sendToJudgesAndOthers (IModel model, IController controller, Packet packet, boolean sendToServers) {
        
        if (model.getClientId().getClientType().equals(ClientType.Type.SERVER)){
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
     * @param model
     * @param run
     * @param runFiles
     */
    private static void checkedOutRun(IModel model,IController controller, Run run, RunFiles runFiles, ClientId whoCheckedOutId) {
        model.addRun(run, runFiles, whoCheckedOutId);
        
        // TODO code for if checkout run from another site.

    }

    private static boolean isSuperUser (ClientId id){
        return id.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }
    
    private static void cancelRun(Run run, IModel model, IController controller, ClientId whoCanceledRun) {
        
        if (isServer(model.getClientId())) {

            if (!isThisSite(model, run)) {

                // TODO: send cancel to other server, multi-site
                System.out.println(" send cancel to other server ");

            } else {

                // TODO: insure that this client checked out the run or send back a "oh no you didn't!"

                model.cancelRunCheckOut(run, whoCanceledRun);

                Run availableRun = model.getRun(run.getElementId());
                Packet availableRunPacket = PacketFactory.createRunAvailable(model.getClientId(), whoCanceledRun, availableRun);

                sendToJudgesAndOthers(model, controller, availableRunPacket, true);
            }

        } else {
            // Client, update status and done.
            
            model.updateRun(run, RunStates.NEW, whoCanceledRun);
        }
    }

    private static void judgeRun(Run run, IModel model, IController controller, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoJudgedId) {
        
        if (isServer(model.getClientId())){
            
            if (! isThisSite(model, run)) {
                
                // TODO: forward packet to other site
                
                info("TODO: forward packet to other site");
                
            } else {
                // This site's run
                
                judgementRecord.setWhenJudgedTime(model.getContestTime().getElapsedMins());

                model.addRunJudgement(run, judgementRecord, runResultFiles, whoJudgedId);
                
                Run theRun = model.getRun(run.getElementId());

                Packet judgementPacket = PacketFactory.createRunJudgement(model.getClientId(), run.getSubmitter(), theRun, judgementRecord, runResultFiles);

                if (judgementRecord.isSendToTeam()) {
                    controller.sendToClient(judgementPacket);
                }

                // TODO: code - make work multi site
                /**
                 * To make this work multi site create a new packet type of RUN_JUDGEMENT_UPDATE
                 * then when a server gets it send it to call clients as a RUN_JUDGEMENT.
                 * Right now this just notifies local clients. 
                 */
                sendToJudgesAndOthers(model, controller, judgementPacket, false);
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
     * 
     * @param run
     * @param model
     * @param controller
     * @param whoRequestsRunId
     */
    private static void requestRun(Run run, IModel model, IController controller, ClientId whoRequestsRunId) {

        if (isServer(model.getClientId())) {

            if (!isThisSite(model, run)) {

                ClientId serverClientId = new ClientId(run.getSiteNumber(), ClientType.Type.SERVER, 0);
                if (model.isLoggedIn(serverClientId)) {

                    // TODO: send run request to other server
                    info(" send run request to other server " + serverClientId);

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

                    sendToJudgesAndOthers(model, controller, checkOutPacket, true);
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
     * @param model
     */
    private static void addRunsToModel (Packet packet, IModel model) {

        try {
            Run [] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
            if (runs != null) {
                for (Run run : runs) {
                    model.addRun(run);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            StaticLog.unclassified("Exception logged ", e);
        }
    }
    
    /**
     * Unpack and add list of clarifications to model.
     * 
     * @param packet
     * @param model
     */
    private static void addClarificationsToModel (Packet packet, IModel model) {

        try {
            Clarification [] clarifications =(Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
            if (clarifications != null) {
                for (Clarification clarification : clarifications) {
                    model.addClarification(clarification);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            e.printStackTrace();
            StaticLog.unclassified("Exception logged ", e);
        }
    }

    /**
     * Add contest data into the model.
     * 
     * This will read a packet and load the data into the model.
     * <br>
     * This should only be execute with the first LOGIN_SUCCESS that
     * this module processes. 
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
     * @param controller
     * @param model
     */
    private static void loadDataIntoModel(Packet packet, IController controller, IModel model, ConnectionHandlerID connectionHandlerID) {


        try {
            Language[] languages = (Language[]) PacketFactory.getObjectValue(packet, PacketFactory.LANGUAGE_LIST);
            if (languages != null) {
                for (Language language : languages) {
                    model.addLanguage(language);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
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
            StaticLog.unclassified("Exception logged ", e);
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
            StaticLog.unclassified("Exception logged ", e);
        }

        try {
            ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
            if (contestTime != null) {
                model.addContestTime(contestTime);
            }
            
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
        }

        addContestTimesToModel (packet, model);

        addSitesToModel (packet, model);
        
        addRunsToModel (packet, model);
        
        addClarificationsToModel (packet, model);
        
        ClientId clientId = null;
        
        try {
            clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            if (clientId != null) {
                model.setClientId(clientId);
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
        }

        controller.setSiteNumber(clientId.getSiteNumber());
        
        if (model.isLoggedIn()){
            
            // show main UI
            controller.startMainUI(model.getClientId());
 
            // Login to other sites
            loginToOtherSites (packet, model, controller);
        }else{
            String message = "Trouble logging in, check logs";
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }
    }

    private static void addContestTimesToModel(Packet packet, IModel model) {
        try {
            ContestTime [] contestTimes = (ContestTime[]) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME_LIST);
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
            StaticLog.unclassified("Exception logged ", e);
        }
    }

    /**
     * Unpack and add a list of sites to the model.
     * 
     * @param packet
     * @param model
     */
    private static void addSitesToModel(Packet packet, IModel model) {
        try {
            Site[] sites = (Site[]) PacketFactory.getObjectValue(packet, PacketFactory.SITE_LIST);
            if (sites != null) {
                for (Site site : sites) {
                    model.addSite(site);
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
        }
        
        
    }

    /**
     * Login to other servers.
     * 
     * Sends a login request packet to sites that
     * this server is nog logged into.
     * 
     * @param packet contains list of other servers
     * @param model
     * @param controller
     */
    private static void loginToOtherSites(Packet packet, IModel model, IController controller) {
        try {
            ClientId [] listOfLoggedInUsers = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOGGED_IN_USERS);
            for (ClientId id : listOfLoggedInUsers){
                if (isServer (id)){
                    if ( ! model.isLoggedIn(id)){
                        controller.sendServerLoginRequest (id.getSiteNumber());
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
        }
        
    }

    private static boolean isServer(ClientId id) {
        return id.getClientType().equals(ClientType.Type.SERVER);
    }

    /**
     * TODO - a temporary logging routine.
     * 
     * @param s
     */
    public static void info(String s) {
        System.err.println(s);
        StaticLog.unclassified(s) ;
    }
    public static void info(String s, Exception ex) {
        System.err.println(s);
        ex.printStackTrace();
        StaticLog.unclassified(s, ex) ;
    }
    
    
}
