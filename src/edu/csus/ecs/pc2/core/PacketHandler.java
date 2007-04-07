package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.log.StaticLog;
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
            judgeRun(run, model,controller,judgementRecord,runResultFiles, fromId);

        } else if (packetType.equals(Type.RUN_UNCHECKOUT)) {
            // Cancel run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            cancelRun (run, model, controller, fromId);
            
        } else if (packetType.equals(Type.RUN_CHECKOUT)) {
            // Run from server to judge
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            RunFiles runFiles = (RunFiles) PacketFactory.getObjectValue(packet, PacketFactory.RUN_FILES);
            checkedOutRun (model, controller, run, runFiles);
            
            sendToJudgesAndOthers(model, controller, packet, false);
            
        } else if (packetType.equals(Type.RUN_REQUEST)) {
            // Request Run from requestor to server
            Run run = (Run) PacketFactory.getObjectValue(packet, PacketFactory.RUN);
            requestRun (run, model, controller, fromId);
            
        } else if (packetType.equals(Type.LOGIN_SUCCESS)) {
            if (! model.isLoggedIn()){
                info(" handlePacket LOGIN_SUCCESS before ");
                loadDataIntoModel(packet, controller, model, connectionHandlerID);
                info(" handlePacket LOGIN_SUCCESS after -- all settings loaded "); 
            } else {
                info(" handlePacket LOGIN_SUCCESS again: "+packet); 
            }

        } else {

            Exception exception = new Exception("PacketHandler.handlePacket Unhandled packet " + packet);
            StaticLog.unclassified("Unhandled Packet ",exception);
        }
    }
    
    private static boolean isThisSite(IModel model, Run run) {
        return run.getSiteNumber() == model.getSiteNumber();
    }

    /**
     * Send to all logged in Judges, Admins, Boards and optionally sites.
     * 
     * This sends all sorts of packets to all logged in clients (other than
     * teams).   Typicall sendToServes is set if this is the originating
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
        }
    }

    /**
     * Handle Check out run, add to model, trigger listeners.
     * @param model
     * @param run
     * @param runFiles
     */
    private static void checkedOutRun(IModel model,IController controller, Run run, RunFiles runFiles) {
        model.addRun(run, runFiles);
        
        // TODO code for if checkout run from another site.

    }

    private static boolean isSuperUser (ClientId id){
        return id.getClientType().equals(ClientType.Type.ADMINISTRATOR);
    }
    
    private static void cancelRun(Run run, IModel model, IController controller, ClientId fromId) {
        
        Run theRun = model.getRun(run.getElementId());
        ClientId whoCheckedOut = model.getRunCheckedOutBy (run);
        
        // TODO code for if canceling run on another site.
        
        if (theRun == null){
            // TODO unable to cancel run?!
            StaticLog.unclassified("cancelRun ", new Exception("Unable to cancel (fetch) run "+run));
            
        } else if ((run.getStatus() == RunStates.BEING_JUDGED) || isSuperUser(fromId)){
            
            if (isSuperUser(fromId) || fromId.equals(whoCheckedOut)){
                
                // authorized to cancel the run.
                model.cancelRunCheckOut(theRun, fromId);
                
                Run availableRun = model.getRun(run.getElementId());
                Packet availableRunPacket = PacketFactory.createRunAvailable(model.getClientId(), fromId, availableRun);
                
                sendToJudgesAndOthers(model, controller, availableRunPacket, true);
                
            } else {
                // Un authorized
                StaticLog.unclassified("cancelRun ", new Exception("Unable to cancel run "+run+" user "+fromId+" "+whoCheckedOut));
                
            }
        } else {
            // un authorized
            StaticLog.unclassified("cancelRun ", new Exception("Unable to cancel run "+run+" user "+fromId+" "+whoCheckedOut));
        }
    }

    private static void judgeRun(Run run, IModel model, IController controller, JudgementRecord judgementRecord,
            RunResultFiles runResultFiles, ClientId fromId) {

        Run theRun = model.getRun(run.getElementId());
        
        // TODO code for if this run is on another site

        if (theRun == null) {
            // TODO code unable to get run
            throw new SecurityException("Unable to judge run "+run+" could not fetch run");
        } else {
            model.addRunJudgement(run, judgementRecord, runResultFiles, fromId);
            
            Packet judgementPacket = PacketFactory.createRunJudgement(model.getClientId(), fromId, theRun, judgementRecord, runResultFiles);

            if (judgementRecord.isSendToTeam()) {
                controller.sendToClient(judgementPacket);
            }
            
            sendToJudgesAndOthers(model, controller, judgementPacket, isThisSite(model, theRun));
        }
    }

    /**
     * Checkout run.
     * 
     * Either checks out run (marks as {@link edu.csus.ecs.pc2.core.model.Run.RunStates#BEING_JUDGED BEING_JUDGED}) and send to
     * everyone, or send a {@link edu.csus.ecs.pc2.core.packet.PacketType.Type#RUN_NOTAVAILABLE RUN_NOTAVAILABLE}.
     * 
     * @param run
     * @param fromId
     * @param model
     * @param controller
     */
    private static void requestRun(Run run, IModel model, IController controller, ClientId fromId) {

        Run theRun = model.getRun(run.getElementId());

        // TODO handle request if run is on another server.
        
        if (run == null) {
            
            // Run not available, perhaps on another server.
            Packet packet = PacketFactory.createRunNotAvailable(model.getClientId(), fromId, run);
            controller.sendToClient(packet);
            
        } else if (run.getStatus() == RunStates.NEW || fromId.getClientType().equals(ClientType.Type.ADMINISTRATOR)) {
            // Run available, fetch it for judge.
            
            model.updateRun(theRun, RunStates.BEING_JUDGED, fromId);

            theRun = model.getRun(run.getElementId());
            RunFiles runFiles = model.getRunFiles(run);

            Packet checkOutPacket = PacketFactory.createCheckedOutRun(model.getClientId(), fromId, theRun, runFiles, fromId);
            controller.sendToClient(checkOutPacket);
            
            sendToJudgesAndOthers(model, controller, checkOutPacket, true);

        } else {

            // run not available for judging
            Packet notAvailableRunPacket = PacketFactory.createRunNotAvailable(model.getClientId(), fromId, theRun);
            controller.sendToClient(notAvailableRunPacket);
        }

    }

    /**
     * Unpack and add list of runs to model.
     * 
     * @param packet
     * @param model
     */
    private static void addRunsToRunList (Packet packet, IModel model) {

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

        ClientId clientId = null;
        
        try {
            clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
            if (clientId != null) {
                model.setClientId(clientId);
                info("DEBUG set client to : "+model.getClientId());
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
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

        addRunsToRunList (packet, model);
        
        if (model.isLoggedIn()){
            
            // show main UI
            controller.startMainUI(model.getClientId());
 
            // Login to other sites
            loginToOtherSites (packet, model);
        }else{
            String message = "Trouble loggin in, check logs";
            model.loginDenied(packet.getDestinationId(), connectionHandlerID, message);
        }
    }

    private static void loginToOtherSites(Packet packet, IModel model) {
        try {
            ClientId [] listOfLoggedInUsers = (ClientId[]) PacketFactory.getObjectValue(packet, PacketFactory.LOGGED_IN_USERS);
            for (ClientId id : listOfLoggedInUsers){
                if (isServer (id)){
                    if ( ! model.isLoggedIn(id)){
                        loginToOtherSite (id.getSiteNumber()); 
                    }
                }
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.unclassified("Exception logged ", e);
        }
        
    }

    private static void loginToOtherSite(int siteNumber) {
        
        info("debug loginToOtherSite, would have logged into  "+siteNumber);
        
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
        StaticLog.info(s) ;
    }
}
