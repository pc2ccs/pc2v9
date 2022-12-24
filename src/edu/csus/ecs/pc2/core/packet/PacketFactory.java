// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.packet;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import edu.csus.ecs.pc2.core.exception.ContestSecurityException;
import edu.csus.ecs.pc2.core.list.ClarificationList;
import edu.csus.ecs.pc2.core.list.LanguageDisplayList;
import edu.csus.ecs.pc2.core.list.ProblemDisplayList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Category;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestLoginSuccessData;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.MessageEvent.Area;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileChangeStatus.Status;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunExecutionStatus;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.packet.PacketType.Type;
import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Creates {@link Packet}s.
 * 
 * Each packet can be created by using a method in this class. There is a "create" method for each {@link Type}. <br>
 * There are also some methods to extract fields/classes from packets.
 * <P>
 * Typically the contents of a packet is a {@link java.util.Properties}.
 * <P>
 * Constants are present in this class that are used to extract individual contents from a packet. <br>
 * Example of extracting individual contents from a packet {@link edu.csus.ecs.pc2.core.packet.Packet}.
 * 
 * <pre>
 * Clarification clarification = (Clarification) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION);
 * 
 * ClientId whoCheckedOut = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
 * </pre>
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class PacketFactory {

    public static final String LOGIN = "LOGIN";

    /**
     * A single {@link Run}.
     */
    public static final String RUN = "RUN";

    public static final String PASSWORD = "PASSWORD";

    /**
     * A single {@link JudgementRecord}.
     */
    public static final String JUDGEMENT_RECORD = "JUDGEMENT_RECORD";

    /**
     * Array of {@link Run}.
     */
    public static final String RUN_LIST = "RUN_LIST";

    public static final String RUN_FILES = "RUN_FILES";

    public static final String LANGUAGE = "LANGUAGE";

    public static final String PROBLEM = "PROBLEM";

    public static final String GENERAL_PROBLEM = "GENERAL_PROBLEM";
    
    public static final String GROUP = "GROUP";

    public static final String CLARIFICATION_ANSWER = "CLARIFICATION_ANSWER";

    /**
     * A single {@link ElementId} for a requested {@link Run}.
     */

    public static final String REQUESTED_RUN_ELEMENT_ID = "REQUESTED_RUN_ELEMENT_ID";

    public static final String CLIENT_ID = "CLIENT_ID";

    public static final String CONTEST_PASSWORD = "CONTEST_PASSWORD";
    
    /**
     * Array of ClientIds of logged in users.
     */
    public static final String LOCAL_LOGGED_IN_USERS = "LOCAL_LOGGED_IN_USERS";
    
    /**
     * Array of ClientIds of logged in users.
     */
    public static final String REMOTE_LOGGED_IN_USERS = "REMOTE_LOGGED_IN_USERS";

    /**
     * Site Number
     */
    public static final String SITE_NUMBER = "SITE_NUMBER";

    public static final String JUDGEMENT = "JUDGEMENT";

    /**
     * Site class.
     */
    public static final String SITE = "SITE";

    /**
     * A single {@link ContestTime}.
     */
    public static final String CONTEST_TIME = "CONTEST_TIME";

    /**
     * A single {@link Clarification}.
     */
    public static final String CLARIFICATION = "CLARIFICATION";

    /**
     * A single {@link ElementId} for a requested {@link Clarification}.
     */
    public static final String REQUESTED_CLARIFICATION_ELEMENT_ID = "REQUESTED_CLARIFICATION_ELEMENT_ID";

    /**
     * A single {@link Account}.
     */
    public static final String ACCOUNT = "ACCOUNT";

    /**
     * Array of {@link Account}.
     */
    public static final String ACCOUNT_ARRAY = "ACCOUNT_ARRAY";

    /**
     * A single ClientType.Type.
     * 
     * @see #createGenerateAccounts(ClientId, ClientId, int, ClientType.Type, int, int, boolean)
     */
    public static final String CLIENT_TYPE = "CLIENT_TYPE";

    public static final String COUNT = "COUNT";

    /**
     * Used as a start count (id) for generating accounts.
     * 
     * If start count is 0, will add accounts after max account number. <br>
     * If start count is 100, will add accounts after max account number is greater than 100.
     */
    public static final String START_COUNT = "START_COUNT";

    public static final String CREATE_ACCOUNT_ACTIVE = "CREATE_ACCOUNT_ACTIVE";

    public static final String ELAPSED_TIME = "ELAPSED_TIME";

    public static final String CONTEST_LENGTH_TIME = "CONTEST_LENGTH_TIME";

    public static final String REMAINING_TIME = "REMAINING_TIME";

    public static final String CONNECTION_HANDLE_ID = "CONNECTION_HANDLE_ID";

    public static final String PROBLEM_DATA_FILES = "PROBLEM_DATA_FILES";

    /**
     * Array of {@link LanguageDisplayList}.
     */

    public static final String LANGUAGE_DISPLAY_LIST = "LANGUAGE_DISPLAY_LIST";

    /**
     * Array of {@link ProblemDisplayList}.
     */
    public static final String PROBLEM_DISPLAY_LIST = "PROBLEM_DISPLAY_LIST";

    public static final String DEFAULT_CLARIFICATION_ANSWER = "DEFAULT_CLARIFICATION_ANSWER";

    public static final String CONTEST_SETTINGS = "CONTEST_SETTINGS";

    /**
     * Array of {@link Site}.
     */
    public static final String SITE_LIST = "SITE_LIST";

    public static final String MESSAGE_STRING = "MESSAGE_STRING";
    
    public static final String CONTEST_INFORMATION = "CONTEST_INFORMATION";
    
    /**
     * Array of {@link ClientSettings}.
     */
    public static final String CLIENT_SETTINGS_LIST = "CLIENT_SETTINGS_LIST";
    
    public static final String CLIENT_SETTINGS = "CLIENT_SETTINGS";
    
    /**
     * Array of {@link BalloonSettings}.
     */
    public static final String BALLOON_SETTINGS_LIST = "BALLOON_SETTINGS_LIST";

    public static final String PACKET = "PACKET";

    /**
     * Change password, new password
     */
    public static final String NEW_PASSWORD = "NEW_PASSWORD";

    /**
     * Boolean value.
     */
    public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
    

    /**
     * On login, send settings to server.
     * 
     * Usually set to false.s
     */
    public static final String SEND_SETTINGS = "SEND_SETTINGS";

    public static final String RUN_RESULTS_FILE = "RUN_RESULTS_FILE";

    /**
     * Array of {@link Group}.
     */
    public static final String GROUP_LIST = "GROUP_LIST";

    /**
     * Array of {@link Problem}.
     */
    public static final String PROBLEM_LIST = "PROBLEM_LIST";

    /**
     * Array of {@link ProblemDataFiles}
     */
    public static final String PROBLEM_DATA_FILES_LIST = "PROBLEM_DATA_FILES_LIST";

    /**
     * Array of {@link Category}.
     */
    public static final String CATEGORY_LIST = "CATEGORY_LIST";

    /**
     * Array of {@link Language}.
     */
    public static final String LANGUAGE_LIST = "LANGUAGE_LIST";

    /**
     * Array of {@link Judgement}.
     */
    public static final String JUDGEMENT_LIST = "JUDGEMENT_LIST";
    
    /**
     * Array of {@link ContestTime}.
     */
    public static final String CONTEST_TIME_LIST = "CONTEST_TIME_LIST";

    /**
     * Array of {@link Clarification}.
     */
    public static final String CLARIFICATION_LIST = "CLARIFICATION_LIST";

    /**
     * Array of {@link ConnectionHandlerID}
     */
    public static final String CONNECTION_HANDLE_ID_LIST = "CONNECTION_HANDLE_ID_LIST";

    /**
     * A ClientId representing all sites (used as a destinationId).
     */
    public static final ClientId ALL_SERVERS = new ClientId(0, ClientType.Type.SERVER, 0);

    /**
     * A boolean indicating fetching a read only copy of clar or run.
     */
    public static final String READ_ONLY = "READ_ONLY";

    public static final String BALLOON_SETTINGS = "BALLOON_SETTINGS";

    public static final String EVENT_NAME = "EVENT_NAME";

    public static final String MESSAGE = "MESSAGE";

    public static final String EXCEPTION = "EXCEPTION";

    public static final String COMPUTER_JUDGE = "COMPUTER_JUDGE";

    public static final String FROM_HUMAN = "FROM_HUMAN";

    public static final String RUN_STATUS = "RUN_STATUS";

    public static final String SERVER_CLOCK_OFFSET = "SERVER_CLOCK_OFFSET";

    public static final String DELETE_PROBLEM_DEFINITIONS = "DELETE_PROBLEM_DEFINITIONS";

    public static final String DELETE_LANGUAGE_DEFINITIONS = "DELETE_LANGUAGE_DEFINITIONS";

    public static final String CONTEST_IDENTIFIER = "CONTEST_IDENTIFIER";

    public static final String PROFILE = "PROFILE";

    public static final String PROFILE_LIST = "PROFILE_LIST";

    public static final String PROFILE_CLONE_SETTINGS = "PROFILE_CLONE_SETTINGS";

    public static final String SWITCH_PROFILE = "SWITCH_PROFILE";

    public static final String NEW_PROFILE = "NEW_PROFILE";

    public static final String MESSAGE_AREA = "MESSAGE_AREA";

    public static final String RUN_ID = "RUN_ID";

    public static final String RUN_FILES_LIST = "RUN_FILES_LIST";

    public static final String PROFILE_STATUS = "PROFILE_STATUS";

    public static final String FINALIZE_DATA = "FINALIZE_DATA";

    public static final String CATEGORY = "CATEGORY";

    public static final String PLAYBACK_INFO = "PLAYBACK_INFO";

    public static final String OVERRIDE_RUN_ID = "OVERRIDE_RUN_ID";

    public static final String AUTO_REG_REQUEST_INFO = "AUTO_REG_REQUEST_INFO";
    
    /**
     * List of files submitted by team.
     */
    public static final String TEAM_RUN_SOURCE_FILES_LIST = "TEAM_RUN_SOURCE_FILES_LIST";

    public static final String REQUEST_LOGIN_AS_PROXY = "REQUEST_LOGIN_AS_PROXY";
    
    
    /**
     * Constructor is private as this is a utility class which should not be extended or invoked.
     */
    private PacketFactory() {
        super();
    }

    /**
     * Create a packet of {@link PacketType.Type#LOGIN_REQUEST}.
     * 
     * @param source -
     *            who is logging in.
     * @param password -
     *            password for login authentication.
     * @param destination -
     *            server to authenticate.
     * @return a {@link PacketType.Type#LOGIN_REQUEST} packet.
     */
    public static Packet createLoginRequest(ClientId source, String loginName, String password, ClientId destination) {
        return createLoginRequest(source, loginName, password, destination, false);
    }
    
    /**
     * 
     * @param source
     * @param loginName
     * @param password
     * @param destination
     * @param proxiedSite true means proxy my site, use the site that is logged into as my proxy.
     * @return
     */
    public static Packet createLoginRequest(ClientId source, String loginName, String password, ClientId destination, boolean proxiedSite) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(LOGIN, loginName);
        prop.put(PASSWORD, password);
        prop.put(REQUEST_LOGIN_AS_PROXY, new Boolean(proxiedSite));
        return createPacket(PacketType.Type.LOGIN_REQUEST, source, destination, prop);
    }
    
    public static Packet createPasswordChangeRequest(ClientId source,  ClientId destination, String password, String newPassword) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PASSWORD, password);
        prop.put(NEW_PASSWORD, newPassword);
        return createPacket(PacketType.Type.PASSWORD_CHANGE_REQUEST, source, destination, prop);
    }
    
    public static Packet createPasswordChangeResult(ClientId source,  ClientId destination, boolean passwordChanged, String message) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PASSWORD_CHANGED, new Boolean(passwordChanged));
        prop.put(PacketFactory.MESSAGE_STRING, message);
        return createPacket(PacketType.Type.PASSWORD_CHANGE_RESULTS, source, destination, prop);
    }
   
    /**
     * Create a packet.
     * 
     * 
     * @param type -
     *            {@link PacketType.Type}
     * @param source -
     *            who packet sent from.
     * @param destination -
     *            who to be sent to.
     * @param serializable -
     *            contents of packet.
     * @return a Packet.
     */
    protected static Packet createPacket(Type type, ClientId source, ClientId destination, Serializable serializable) {
        Packet packet = new Packet(type, source, destination, serializable);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#MESSAGE}.
     * 
     * @param source
     * @param destination
     * @param message
     */
    public static Packet createMessage(ClientId source, ClientId destination, Area area, String message) {
        Properties prop = new Properties();
        prop.put(MESSAGE_STRING, message);
        prop.put(MESSAGE_AREA, area);
        Packet packet = new Packet(Type.MESSAGE, source, destination, prop);
        return packet;
    }

    public static Packet createMessage(ClientId source, ClientId destination, Area area, String message, Exception ex) {
        Properties prop = new Properties();
        prop.put(MESSAGE_STRING, message);
        prop.put(MESSAGE_AREA, area);
        prop.put(EXCEPTION, ex);
        Packet packet = new Packet(Type.MESSAGE, source, destination, prop);
        return packet;
    }
    /**
     * Create a packet of {@link PacketType.Type#RUN_SUBMISSION}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param runFiles
     * @param overrideSubmissionTime 
     * @return submitted run packet.
     */
    public static Packet createSubmittedRun(ClientId source, ClientId destination, Run run, RunFiles runFiles, long overrideSubmissionTime, long overrideRunId) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        prop.put(RUN_FILES, runFiles);
        if (overrideSubmissionTime > 0) {
            prop.put(ELAPSED_TIME, new Long(overrideSubmissionTime));
        }
        if (overrideRunId > 0) {
            prop.put(OVERRIDE_RUN_ID, new Long(overrideRunId));
        }
        Packet packet = new Packet(Type.RUN_SUBMISSION, source, destination, prop);
        return packet;
    }
    
    public static Packet createRunSubmissionConfirmation(ClientId source, ClientId destination, Run run, RunFiles runFiles) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        prop.put(RUN_FILES, runFiles);
        Packet packet = new Packet(Type.RUN_SUBMISSION_CONFIRM_SERVER, source, destination, prop);
        return packet;
        
    }

    /**
     * Dump packet info to PrintWriter and System.err.
     * 
     * @param pw
     * @param packet
     */
    public static void dumpPacket(PrintWriter pw, Packet packet) {

        dumpPacket(pw, packet);

        pw.println("Packet " + packet.getType());
        pw.println("  From: " + packet.getSourceId());
        pw.println("    To: " + packet.getDestinationId());
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                if (element.endsWith("PASSWORD")) {
                    // this covers PASSWORD and NEW_PASSWORD
                    continue;
                }
                pw.println("   key: " + element + " is: " + prop.get(element).getClass().getName() + " " + prop.get(element));
            }
        } else {

            pw.println("  Contains: " + obj.toString() + " " + obj);
        }
        pw.println();

    }
    
    /**
     * Dump packet info to PrintWriter and System.err.
     * 
     * @param log
     * @param packet
     * @param message
     */
    public static void dumpPacket(Log log, Packet packet, String message) {

        log.info("Packet " + packet.getType() + " (Seq #" + packet.getPacketNumber() + ", o#"+packet.getOriginalPacketNumber() +" ) " + message);
        log.info("  From: " + packet.getSourceId() + ", o" + packet.getOriginalSourceId() + " (" + packet.getHostName() + " @ " + packet.getHostAddress() + ")" + " (Contest Id: "
                + packet.getContestIdentifier() + ")");
        log.info("    To: " + packet.getDestinationId());
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                if (element.endsWith("PASSWORD")) {
                    // this covers PASSWORD and NEW_PASSWORD
                    continue;
                }
                log.info("   key: " + element + " is: " + prop.get(element).getClass().getName() + " " + prop.get(element));
            }
        } else {

            log.info("  Contains: " + obj.toString() + " " + obj);
        }
    }

    /**
     * Dump packet to PrintStream.
     * 
     * @param pw
     * @param packet
     * @param message 
     */
    public static void dumpPacket(PrintStream pw, Packet packet, String message) {
        pw.println("Packet " + packet.getType() + " (Seq #" + packet.getPacketNumber() + ", o#" + packet.getOriginalPacketNumber() + " ) " + message);
        pw.println("  From: " + packet.getSourceId() + ", o" + packet.getOriginalSourceId() + " (" + packet.getHostName() + " @ " + packet.getHostAddress() + ")" + " (Contest Id: "
                + packet.getContestIdentifier() + ")");
        pw.println("    To: " + packet.getDestinationId());
        Object obj = packet.getContent();
        if (obj instanceof Properties) {
            Properties prop = (Properties) obj;
            Enumeration<?> enumeration = prop.keys();

            while (enumeration.hasMoreElements()) {
                String element = (String) enumeration.nextElement();
                if (element.endsWith("PASSWORD")) {
                    // this covers PASSWORD and NEW_PASSWORD
                    continue;
                }
                pw.println("   key: " + element + " is: " + prop.get(element).getClass().getName() + " " + prop.get(element));
            }
        } else {

            pw.println("  Contains: " + obj.toString() + " " + obj);
        }
        pw.println();

    }

    /**
     * Create a packet of {@link PacketType.Type#RUN_AVAILABLE}.
     * 
     * @param source
     * @param destination
     * @param run
     * @return run available packet.
     */
    public static Packet createRunAvailable(ClientId source, ClientId destination, Run run) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        Packet packet = new Packet(Type.RUN_AVAILABLE, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#RUN_UPDATE}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param judgementRecord optional
     * @param runResultFiles optional
     * @param whoModifiedRun
     */
    public static Packet createRunUpdated(ClientId source, ClientId destination, Run run, JudgementRecord judgementRecord, RunResultFiles runResultFiles, ClientId whoModifiedRun) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, whoModifiedRun);
        prop.put(RUN, run);
        if (judgementRecord != null) {
            prop.put(JUDGEMENT_RECORD, judgementRecord);
        }
        if (runResultFiles != null) {
            prop.put(RUN_RESULTS_FILE, runResultFiles);
        }
        Packet packet = new Packet(Type.RUN_UPDATE, source, destination, prop);
        return packet;
    }
    
    public static Packet createRunUpdateNotification(ClientId source, ClientId destination, Run run, ClientId whoModifiedRun) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, whoModifiedRun);
        prop.put(RUN, run);
        Packet packet = new Packet(Type.RUN_UPDATE_NOTIFICATION, source, destination, prop);
        return packet;
    }


    /**
     * Send checked out packet to requesting judge.
     * 
     * @param source
     * @param destination
     * @param run
     * @param runFiles
     *            run files or null (if run is not for destination client).
     * @param id
     * @param runResultFiles
     * @return packet of checked out run for judge.
     */
    public static Packet createCheckedOutRun(ClientId source, ClientId destination, Run run, RunFiles runFiles, ClientId id, RunResultFiles[] runResultFiles) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        if (runFiles != null) {
            prop.put(RUN_FILES, runFiles);
        }
        if (runResultFiles!= null) {
            prop.put(RUN_RESULTS_FILE, runResultFiles);
        }
        prop.put(CLIENT_ID, id);

        Packet packet = new Packet(Type.RUN_CHECKOUT, source, destination, prop);
        return packet;
    }

    /**
     * Create packet that notifies judges and others that run has been checked out.
     * 
     * @param source
     * @param destination
     * @param run
     * @param id
     * @return packet that contains run and who checked out run
     */
    public static Packet createCheckedOutRunNotification(ClientId source, ClientId destination, Run run, ClientId id) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        prop.put(CLIENT_ID, id);

        Packet packet = new Packet(Type.RUN_CHECKOUT_NOTIFICATION, source, destination, prop);
        return packet;
    }

    
    /**
     * Create a packet of {@link PacketType.Type#RUN_NOTAVAILABLE}.
     * 
     * @param source
     * @param destination
     * @param run
     */
    public static Packet createRunNotAvailable(ClientId source, ClientId destination, Run run) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        Packet packet = new Packet(Type.RUN_NOTAVAILABLE, source, destination, prop);
        return packet;
    }
    
    /**
     * Create a packet of {@link PacketType.Type#RUN_NOTAVAILABLE}.
     * 
     * @param source
     * @param destination
     * @param run
     */
    public static Packet createRunRevoked (ClientId source, ClientId destination, Run run, ClientId revokedFrom) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        prop.put(CLIENT_ID, revokedFrom);
        Packet packet = new Packet(Type.RUN_REVOKED, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#RUN_LIST}.
     * 
     * @param source
     * @param destination
     * @param runs
     */
    public static Packet createRunList(ClientId source, ClientId destination, Run[] runs) {
        Properties prop = new Properties();
        prop.put(RUN_LIST, runs);
        Packet packet = new Packet(Type.RUN_LIST, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#CLARIFICATION_LIST}.
     * 
     * @param source
     * @param destination
     * @param clarList
     */
    public static Packet createClarList(ClientId source, ClientId destination, ClarificationList clarList) {
        Packet packet = new Packet(Type.CLARIFICATION_LIST, source, destination, clarList);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#RUN_UNCHECKOUT}.
     * 
     * @param source
     * @param destination
     * @param beingJudgingRun
     * @param whoUncheckedOutRun - who checked out run.
     */
    public static Packet createUnCheckoutRun(ClientId source, ClientId destination, Run beingJudgingRun, ClientId whoUncheckedOutRun) {
        Properties prop = new Properties();
        prop.put(RUN, beingJudgingRun);
        prop.put(CLIENT_ID, whoUncheckedOutRun);
        Packet packet = new Packet(Type.RUN_UNCHECKOUT, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#RUN_JUDGEMENT}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param judgementRecord
     * @param runResultFiles
     */
    public static Packet createRunJudgement(ClientId source, ClientId destination, Run run, JudgementRecord judgementRecord,
            RunResultFiles runResultFiles) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(RUN, run);
        prop.put(JUDGEMENT_RECORD, judgementRecord);

        if (runResultFiles != null) {
            prop.put(RUN_RESULTS_FILE, runResultFiles);
        }
        Packet packet = new Packet(Type.RUN_JUDGEMENT, source, destination, prop);
        return packet;

    }
    
    /**
     * Return the value (Object) inside a packet.
     * 
     * If the packet contents is a {@link Properties} object, will retrieve the value for the input key from that {@link Properties}
     * object.
     * <P>
     * Examples:
     * <pre>
     * Clarification [] clarifications =(Clarification[]) PacketFactory.getObjectValue(packet, PacketFactory.CLARIFICATION_LIST);
     * 
     * Run [] runs = (Run[]) PacketFactory.getObjectValue(packet, PacketFactory.RUN_LIST);
     * 
     * ContestTime contestTime = (ContestTime) PacketFactory.getObjectValue(packet, PacketFactory.CONTEST_TIME);
     * 
     * ClientId clientId = (ClientId) PacketFactory.getObjectValue(packet, PacketFactory.CLIENT_ID);
     * 
     * </pre>
     * One can then check for whether the item is null to determine whether item
     * is in the packet.
     * 
     * @param packet packet to extract data from.
     * @param key one of the many constant Strings in PacketFactory
     * @return a Object value for a property inside a packet, or null if object is not present.
     */
    public static Object getObjectValue(Packet packet, String key) {
        try {
            Properties props = (Properties) packet.getContent();
            return props.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return the String value inside a packet.
     * 
     * @see #getObjectValue(Packet, String)
     * @param packet
     * @param key
     * @return a string value for a property inside a packet.
     */
    public static String getStringValue(Packet packet, String key) {
        return (String) getObjectValue(packet, key);
    }

    /**
     * Return the Boolean value inside a packet.
     * 
     * @see #getObjectValue(Packet, String)
     * @param packet
     * @param key
     * @return a string value for a property inside a packet.
     */
    public static Boolean getBooleanValue(Packet packet, String key) {
        return (Boolean) getObjectValue(packet, key);
    }

    /**
     * Create a packet of {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param language
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Language language) {

        Properties prop = new Properties();
        prop.put(LANGUAGE, language);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }
    
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Profile profile) {
        Properties prop = new Properties();
        prop.put(PROFILE, profile);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }
    
    public static Packet createUpdateSetting(ClientId source, ClientId destination, BalloonSettings balloonSettings) {
        Properties prop = new Properties();
        prop.put(BALLOON_SETTINGS, balloonSettings);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }


     public static Packet createUpdateSetting(ClientId source, ClientId destination, Problem problem,
            ProblemDataFiles problemDataFiles) {
        Properties prop = new Properties();
        prop.put(PROBLEM, problem);
        if (problemDataFiles != null) {
            prop.put(PROBLEM_DATA_FILES, problemDataFiles);
        }
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

     public static Packet createAddSetting(ClientId source, ClientId destination, BalloonSettings balloonSettings) {
         Properties prop = new Properties();
         prop.put(BALLOON_SETTINGS, balloonSettings);
         Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
         return packet;
     }

    /**
     * Create a packet of {@link PacketType.Type#ADD_SETTING}.
     * 
     * @param source
     * @param destination
     * @param language
     */
    public static Packet createAddSetting(ClientId source, ClientId destination, Language language) {
        Properties prop = new Properties();
        prop.put(LANGUAGE, language);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

     public static Packet createAddSetting(ClientId source, ClientId destination, Site site) {
        Properties prop = new Properties();
        prop.put(SITE, site);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

     public static Packet createAddSetting(ClientId source, ClientId destination, Group group) {
         Properties prop = new Properties();
         prop.put(GROUP, group);
         Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
         return packet;
     }

     public static Packet createAddSetting(ClientId source, ClientId destination, Profile profile) {
         Properties prop = new Properties();
         prop.put(PROFILE, profile);
         Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
         return packet;
     }

     /**
     * Create a packet of {@link PacketType.Type#ADD_SETTING}.
     * 
     * @param source
     * @param destination
     * @param judgement
     */
    public static Packet createAddSetting(ClientId source, ClientId destination, Judgement judgement) {
        Properties prop = new Properties();
        prop.put(JUDGEMENT, judgement);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

     public static Packet createAddSetting(ClientId source, ClientId destination, Problem problem, ProblemDataFiles problemDataFiles) {
        Properties prop = new Properties();
        prop.put(PROBLEM, problem);
        if (problemDataFiles != null) {
            prop.put(PROBLEM_DATA_FILES, problemDataFiles);
        }
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet for a list of problems.
     * 
     * @param source
     * @param destination
     * @param problems
     * @param problemDataFilesList
     */
    public static Packet createAddSetting(ClientId source, ClientId destination, Problem[] problems, ProblemDataFiles[] problemDataFilesList) {
        Properties prop = new Properties();
        prop.put(PROBLEM_LIST, problems);
        prop.put(PROBLEM_DATA_FILES_LIST, problemDataFilesList);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

    /** 
     * Create start contest to send to server.
     * @param source
     * @param destination
     * @param siteNumber
     * @param who 
     */
    public static Packet createStartContestClock(ClientId source, ClientId destination, int siteNumber, ClientId who) {
        Properties prop = new Properties();
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        prop.put(CLIENT_ID, who);
        Packet packet = new Packet(Type.START_CONTEST_CLOCK, source, destination, prop);
        return packet;

    }

    /**
     * Create a stop contest to send to server.
     * 
     * @param source
     * @param destination
     * @param siteNumber
     * @param who 
     */
    public static Packet createStopContestClock(ClientId source, ClientId destination, int siteNumber, ClientId who) {
        Properties prop = new Properties();
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        prop.put(CLIENT_ID, who);
        Packet packet = new Packet(Type.STOP_CONTEST_CLOCK, source, destination, prop);
        return packet;
    }
    
    /**
     * Create a stop contest to send to clients.
     * 
     * @param source
     * @param destination
     * @param inSiteNumber
     */
    public static Packet createContestStopped(ClientId source, ClientId destination, int inSiteNumber, ClientId who) {
        Properties prop = new Properties();
        prop.put(PacketType.SITE_NUMBER, new Integer(inSiteNumber));
        prop.put(CLIENT_ID, who);
        Packet packet = new Packet(Type.CLOCK_STOPPED, source, destination, prop);
        return packet;
    }

    /**
     * Create a start contest to send to clients.
     * 
     * @param source
     * @param destination
     * @param inSiteNumber
     */
    public static Packet createContestStarted(ClientId source, ClientId destination, int inSiteNumber, ClientId who) {
        Properties prop = new Properties();
        prop.put(PacketType.SITE_NUMBER, new Integer(inSiteNumber));
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        prop.put(SERVER_CLOCK_OFFSET, gregorianCalendar);
        prop.put(CLIENT_ID, who);
        Packet packet = new Packet(Type.CLOCK_STARTED, source, destination, prop);
        return packet;
    }


    /**
     * Create a packet of {@link PacketType.Type#UPDATE_CONTEST_CLOCK}.
     * 
     * @param source
     * @param destination
     * @param contestTime
     * @param siteNumber
     */
    public static Packet createUpdateContestTime(ClientId source, ClientId destination, ContestTime contestTime, int siteNumber, ClientId whoModifiedIt) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, whoModifiedIt);
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        prop.put(CONTEST_TIME, contestTime);
        Packet packet = new Packet(Type.UPDATE_CONTEST_CLOCK, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#ACCOUNT_LOGIN}.
     * 
     * @param source
     * @param destination
     * @param connectionHandlerID
     */
    public static Packet createAccountLogin(ClientId source, ClientId destination, ConnectionHandlerID connectionHandlerID) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, destination);
        prop.put(CONNECTION_HANDLE_ID, connectionHandlerID);
        Packet packet = new Packet(Type.ACCOUNT_LOGIN, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#LOGOUT}.
     * 
     * @param source
     * @param destination
     * @param userId
     */
    public static Packet createLogoff(ClientId source, ClientId destination, ClientId userId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userId);
        Packet packet = new Packet(Type.LOGOUT, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#CLARIFICATION_SUBMISSION_CONFIRM}.
     * 
     * @param source
     * @param destination
     * @param newClarification
     */
    public static Packet createClarSubmissionConfirm(ClientId source, ClientId destination, Clarification newClarification) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, newClarification);
        Packet packet = new Packet(Type.CLARIFICATION_SUBMISSION_CONFIRM, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#CLARIFICATION_SUBMISSION}.
     * 
     * @param source
     * @param destination
     * @param clarification2
     */
    public static Packet createClarificationSubmission(ClientId source, ClientId destination, Clarification clarification2) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(CLARIFICATION, clarification2);
        Packet packet = new Packet(Type.CLARIFICATION_SUBMISSION, source, destination, prop);
        return packet;
    }

    /**
     * Create a packet of {@link PacketType.Type#RUN_SUBMISSION_CONFIRM}.
     * 
     * @param source
     * @param destination
     * @param run
     */
    public static Packet createRunSubmissionConfirm(ClientId source, ClientId destination, Run run) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        Packet packet = new Packet(Type.RUN_SUBMISSION_CONFIRM, source, destination, prop);
        return packet;
    }

    /**
     * @param source
     * @param destination
     * @param connectionHandlerID
     * @param loggedInClientId
     * @param settings 
     */
    public static Packet createLogin(ClientId source, ClientId destination, ConnectionHandlerID connectionHandlerID, ClientId loggedInClientId, ClientSettings settings) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, loggedInClientId);
        prop.put(CONNECTION_HANDLE_ID, connectionHandlerID);
        prop.put(CLIENT_SETTINGS, settings);
        Packet packet = new Packet(Type.LOGIN, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#LOGIN_SUCCESS}.
     * 
     * Sent to a client or server on successful login.  This packet
     * has all contest data.
     * 
     * @param source
     * @param destination
     * @param contestTime
     * @param siteNumber
     * @param information 
     * @param data 
     */
    public static Packet createLoginSuccess(ClientId source, ClientId destination, ContestTime contestTime, int siteNumber, ContestInformation information, ContestLoginSuccessData data) {
        try {
            Properties prop = new Properties();
            
            prop.put(SITE_NUMBER, new Integer(siteNumber));
            prop.put(CONTEST_TIME, contestTime);
            prop.put(CLIENT_ID, destination);
            prop.put(CONTEST_INFORMATION, information);
            
            addContestData(prop, data);
            
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
            prop.put(SERVER_CLOCK_OFFSET, gregorianCalendar);

            Packet packet = new Packet(Type.LOGIN_SUCCESS, source, destination, prop);
            return packet;
        } catch (Exception e) {
            System.err.println("Exception creating LOGIN_SUCCESS ");
            e.printStackTrace(System.err);
            StaticLog.log("Exception in createLoginSuccess ", e);
            throw new SecurityException(e.getMessage());
        }
    }
    
    /**
     * Add Contest Data into a Properties.
     * 
     * Can be used in a Packet.
     * 
     * @param prop
     * @param data
     */
    private static void addContestData(Properties prop, ContestLoginSuccessData data) {
        
        prop.put(CONTEST_TIME_LIST, data.getContestTimes());

        prop.put(PROBLEM_LIST, data.getProblems());
        prop.put(LANGUAGE_LIST, data.getLanguages());
        prop.put(JUDGEMENT_LIST, data.getJudgements());
        prop.put(SITE_LIST, data.getSites());
        prop.put(RUN_LIST, data.getRuns());
        prop.put(CLARIFICATION_LIST, data.getClarifications());
        prop.put(LOCAL_LOGGED_IN_USERS, data.getLocalLoggedInUsers());
        prop.put(REMOTE_LOGGED_IN_USERS, data.getRemoteLoggedInUsers());
        prop.put(CONNECTION_HANDLE_ID_LIST, data.getConnectionHandlerIDs());
        prop.put(ACCOUNT_ARRAY, data.getAccounts());
        prop.put(PROBLEM_DATA_FILES, data.getProblemDataFiles());
        prop.put(CLIENT_SETTINGS_LIST, data.getClientSettings());
        prop.put(BALLOON_SETTINGS_LIST, data.getBalloonSettingsArray());
        prop.put(GROUP_LIST, data.getGroups());
        prop.put(GENERAL_PROBLEM, data.getGeneralProblem());
        prop.put(CONTEST_IDENTIFIER, data.getContestIdentifier());
        prop.put(PROFILE, data.getProfile());
        prop.put(PROFILE_LIST, data.getProfiles());
        prop.put(SITE_NUMBER, new Integer(data.getSiteNumber()));
        prop.put(CONTEST_TIME, data.getContestTime());
        prop.put(CONTEST_INFORMATION, data.getContestInformation());
        
        if (data.getFinalizeData() != null) {
            prop.put(FINALIZE_DATA, data.getFinalizeData());
        }
        
        if (data.getContestSecurityPassword() != null) {
            prop.put(CONTEST_PASSWORD, data.getContestSecurityPassword());
        }
        
        if (data.getCategories() != null){
            prop.put(CATEGORY_LIST, data.getCategories());
        }
    }

    /**
     * A contest settings packet.
     * 
     * @param source
     * @param destination
     * @param loginSuccessPacket
     * @return a contest settings packet
     */
    public static Packet createContestSettingsPacket(ClientId source, ClientId destination, Packet loginSuccessPacket) {

        try {

            if (loginSuccessPacket.getType().equals(Type.LOGIN_SUCCESS)) {
                Properties properties = (Properties) loginSuccessPacket.getContent();
                Packet contestSettingsPacket = new Packet(Type.SERVER_SETTINGS, source, destination, properties);
                return contestSettingsPacket;
            } else {
                throw new IllegalArgumentException("Packet loginSuccessPacket is not LOGIN_SUCCESS " + loginSuccessPacket);
            }

        } catch (Exception e) {
            System.err.println("Exception creating SERVER_SETTINGS ");
            e.printStackTrace(System.err);
            StaticLog.log("Exception in createContestUpdate ", e);
            throw new SecurityException(e.getMessage());
        }

    }
    
    

    /**
     * Create a packet of {@link PacketType.Type#SETTINGS}.
     * 
     * @param source
     * @param destination
     * @param props
     */
    public static Packet createSettings(ClientId source, ClientId destination, Properties props) {
        Packet packet = new Packet(Type.SETTINGS, source, destination, props);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#LOGIN_FAILED}.
     * 
     * @param source
     * @param destination
     * @param string
     */
    public static Packet createLoginDenied(ClientId source, ClientId destination, String string) {
        Properties props = new Properties();
        props.put(PacketFactory.MESSAGE_STRING, string);
        Packet packet = new Packet(Type.LOGIN_FAILED, source, destination, props);
        return packet;

    }

    /**
     * Create packet for {@link PacketType.Type#RUN_REQUEST}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param requesingId
     * @param readOnly - request a read only (fetch) of the run
     */
    public static Packet createRunRequest(ClientId source, ClientId destination, Run run, ClientId requesingId, boolean readOnly, boolean computerJudge) {
        Properties props = new Properties();
        props.put(PacketFactory.RUN, run);
        props.put(PacketFactory.CLIENT_ID, requesingId);
        props.put(PacketFactory.READ_ONLY, new Boolean(readOnly));
        props.put(PacketFactory.COMPUTER_JUDGE, new Boolean(computerJudge));
        Packet packet = new Packet(Type.RUN_REQUEST, source, destination, props);
        return packet;
    }
    
    /**
     * Create a request to fetch a run from the server.
     * @param source
     * @param destination
     * @param run
     * @param requesingId
     * @return request a fetched (read-only) run from the server.
     */
    public static Packet createFetchRun(ClientId source, ClientId destination, Run run, ClientId requesingId) {
        Properties props = new Properties();
        props.put(PacketFactory.RUN, run);
        props.put(PacketFactory.CLIENT_ID, requesingId);
        Packet packet = new Packet(Type.FETCH_RUN, source, destination, props);
        return packet;
    }
    
    /**
     * Create a run fetched (but not checked out) from server packet.
     * 
     * Response to createFetchRun.
     * 
     * @param source
     * @param destination
     * @param run
     * @param runFiles
     * @param requesingId
     * @param runResultFiles
     * @return packet containing fetched (non-checked out) run.
     */
    public static Packet createFetchedRun(ClientId source, ClientId destination, Run run, RunFiles runFiles, ClientId requesingId, RunResultFiles[] runResultFiles) {
        Properties props = new Properties();
        props.put(PacketFactory.RUN, run);
        props.put(PacketFactory.CLIENT_ID, requesingId);
        props.put(RUN_FILES, runFiles);
        if (runResultFiles != null) {
            props.put(RUN_RESULTS_FILE, runResultFiles);
        }
        Packet packet = new Packet(Type.FETCHED_REQUESTED_RUN, source, destination, props);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#CLARIFICATION_REQUEST}.
     * 
     * @param source
     * @param destination
     * @param elementId
     * @param userId
     */
    public static Packet createClarificationRequest(ClientId source, ClientId destination, ElementId elementId, ClientId userId) {
        Properties props = new Properties();
        props.put(PacketFactory.REQUESTED_CLARIFICATION_ELEMENT_ID, elementId);
        props.put(PacketFactory.CLIENT_ID, userId);
        Packet packet = new Packet(Type.CLARIFICATION_REQUEST, source, destination, props);
        return packet;
    }
    
    

    /**
     * Create packet for {@link PacketType.Type#CLARIFICATION_CHECKOUT}.
     * 
     * @param source
     * @param destination
     * @param clarification
     * @param whoCheckedOut
     */
    public static Packet createCheckedOutClarification(ClientId source, ClientId destination, Clarification clarification,
            ClientId whoCheckedOut) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        prop.put(CLIENT_ID, whoCheckedOut);
        Packet packet = new Packet(Type.CLARIFICATION_CHECKOUT, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#CLARIFICATION_UNCHECKOUT}.
     * 
     * @param source
     * @param destination
     * @param clarification
     */
    public static Packet createUnCheckoutClarification(ClientId source, ClientId destination, Clarification clarification) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.CLARIFICATION_UNCHECKOUT, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#CLARIFICATION_AVAILABLE}.
     * 
     * @param source
     * @param destination
     * @param clarification
     */
    public static Packet createClarificationAvailable(ClientId source, ClientId destination, Clarification clarification) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        Packet packet = new Packet(Type.CLARIFICATION_AVAILABLE, source, destination, prop);
        return packet;
    }
    
    public static Packet createClarificationRevoked(ClientId source, ClientId destination, Clarification clarification, ClientId revokedFrom) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        prop.put(CLIENT_ID,revokedFrom);
        Packet packet = new Packet(Type.CLARIFICATION_REVOKED, source, destination, prop);
        return packet;
    }


    /**
     * Create packet for {@link PacketType.Type#CLARIFICATION_UPDATE}.
     * 
     * @param source
     * @param destination
     * @param clarification
     */
    public static Packet createClarificationUpdate(ClientId source, ClientId destination, Clarification clarification) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        Packet packet = new Packet(Type.CLARIFICATION_UPDATE, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#CLARIFICATION_ANSWER}.
     * 
     * @param source
     * @param destination
     * @param clarification
     * @param answer
     */
    public static Packet createAnsweredClarification(ClientId source, ClientId destination, Clarification clarification,
            String answer) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        prop.put(CLARIFICATION_ANSWER, answer);
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.CLARIFICATION_ANSWER, source, destination, prop);
        return packet;
    }

     /**
         * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
         * 
         * @param source
         * @param destination
         * @param site
         */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Site site) {
        Properties prop = new Properties();
        prop.put(SITE, site);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#ADD_SETTING}.
     * 
     * @param source
     * @param destination
     * @param account
     */
    public static Packet createAddSetting(ClientId source, ClientId destination, Account account) {
        Properties prop = new Properties();
        prop.put(ACCOUNT, account);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }
    
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Account [] accounts) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(ACCOUNT_ARRAY, accounts);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, FinalizeData finalizeData) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(FINALIZE_DATA, finalizeData);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }


    /**
     * Create packet for {@link PacketType.Type#ADD_SETTING }.
     * 
     * @param source
     * @param destination
     * @param type
     * @param startNumber - a requested start client number
     * @param count - number of accounts to add
     * @param isActive
     * @return a packet
     */
    public static Packet createGenerateAccounts(ClientId source, ClientId destination, int siteNumber, ClientType.Type type, int count, int startNumber, boolean isActive) {
        Properties prop = new Properties();
        prop.put(CLIENT_TYPE, type);
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        prop.put(COUNT, new Integer(count));
        prop.put(START_COUNT, new Integer(startNumber));
        prop.put(CREATE_ACCOUNT_ACTIVE, new Boolean(isActive));
        Packet packet = new Packet(Type.GENERATE_ACCOUNTS, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#ADD_SETTING }.
     * 
     * @param source
     * @param destination
     * @param type
     * @param count
     */
    public static Packet createAddSetting(ClientId source, ClientId destination, ClientType.Type type, int count) {
        Properties prop = new Properties();
        prop.put(CLIENT_TYPE, type);
        prop.put(COUNT, new Integer(count));
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param account
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Account account) {
        Properties prop = new Properties();
        prop.put(ACCOUNT, account);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param group
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Group group) {
        Properties prop = new Properties();
        prop.put(GROUP, group);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }
    
    /**
     * Create packet for {@link PacketType.Type#RUN_REJUDGE_REQUEST}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param requesterId
     */
    public static Packet createRunRejudgeRequest(ClientId source, ClientId destination, Run run, ClientId requesterId) {
        Properties props = new Properties();
        props.put(PacketFactory.RUN, run);
        props.put(PacketFactory.CLIENT_ID, requesterId);
        Packet packet = new Packet(Type.RUN_REJUDGE_REQUEST, source, destination, props);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#RUN_REJUDGE_CHECKOUT}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param runFiles
     * @param id
     */
    public static Packet createRejudgeCheckedOut(ClientId source, ClientId destination, Run run, RunFiles runFiles, ClientId id) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        if (runFiles != null) {
            prop.put(RUN_FILES, runFiles);
        }
        prop.put(CLIENT_ID, id);

        Packet packet = new Packet(Type.RUN_REJUDGE_CHECKOUT, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param timeValue
     * @param siteNumber
     */
    public static Packet createUpdateContestLengthTime(ClientId source, ClientId destination, long timeValue, int siteNumber) {
        Properties prop = new Properties();
        prop.put(CONTEST_LENGTH_TIME, new Long(timeValue));
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param timeValue
     * @param siteNumber
     */
    public static Packet createUpdateContestRemainingTime(ClientId source, ClientId destination, long timeValue, int siteNumber) {
        Properties prop = new Properties();
        prop.put(REMAINING_TIME, new Long(timeValue));
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param timeValue
     * @param siteNumber
     */
    public static Packet createUpdateContestElapsedTime(ClientId source, ClientId destination, long timeValue, int siteNumber) {
        Properties prop = new Properties();
        prop.put(ELAPSED_TIME, new Long(timeValue));
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#DROPPED_CONNECTION}.
     * 
     * @param source
     * @param destination
     * @param connectionHandlerID
     */
    public static Packet createDroppedConnection(ClientId source, ClientId destination, ConnectionHandlerID connectionHandlerID) {
        Properties prop = new Properties();
        prop.put(CONNECTION_HANDLE_ID, connectionHandlerID);
        Packet packet = new Packet(Type.DROPPED_CONNECTION, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#ESTABLISHED_CONNECTION}.
     * 
     * @param source
     * @param destination
     * @param connectionHandlerID
     */
    public static Packet createEstablishedConnection(ClientId source, ClientId destination, ConnectionHandlerID connectionHandlerID) {
        Properties prop = new Properties();
        prop.put(CONNECTION_HANDLE_ID, connectionHandlerID);
        Packet packet = new Packet(Type.ESTABLISHED_CONNECTION, source, destination, prop);
        return packet;
    }

    // public static Packet createUpdateSetting(ClientId source, ClientId destination, Site site, ClientId id) {
    // Properties prop = new Properties();
    // prop.put(SITE, site);
    // prop.put(CLIENT_ID, id);
    //
    // Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
    // return packet;
    // }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param id
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Run run, ClientId id) {
        Properties prop = new Properties();
        prop.put(RUN, run);
        prop.put(CLIENT_ID, id);

        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param run
     * @param judgementRecord
     * @param id
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Run run, JudgementRecord judgementRecord,
            ClientId id) {

        Properties prop = new Properties();
        prop.put(RUN, run);
        if (judgementRecord != null) {
            prop.put(JUDGEMENT_RECORD, judgementRecord);
        }
        prop.put(CLIENT_ID, id);

        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;

    }

    /**
     * Create packet for {@link PacketType.Type#FORCE_DISCONNECTION}.
     * 
     * @param source
     * @param destination
     * @param userLoginId
     */
    public static Packet createForceLogoff(ClientId source, ClientId destination, ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        Packet packet = new Packet(Type.FORCE_DISCONNECTION, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#FORCE_DISCONNECTION}.
     * 
     * @param source
     * @param destination
     * @param connectionHandlerID
     */
    public static Packet createForceLogoff(ClientId source, ClientId destination, ConnectionHandlerID connectionHandlerID) {
        Properties prop = new Properties();
        prop.put(CONNECTION_HANDLE_ID, connectionHandlerID);
        Packet packet = new Packet(Type.FORCE_DISCONNECTION, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#START_ALL_CLOCKS}.
     * 
     * @param source
     * @param destination
     * @param userLoginId
     */
    public static Packet createStartAllClocks(ClientId source, ClientId destination, ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        Packet packet = new Packet(Type.START_ALL_CLOCKS, source, destination, prop);
        return packet;
    }

//    /**
//     * Create packet for {@link PacketType.Type#RESET_CONTEST}.
//     * 
//     * @param source
//     * @param destination
//     * @param siteNumber
//     * @param userLoginId
//     */
//    public static Packet createResetContest(ClientId source, ClientId destination, int siteNumber, ClientId userLoginId) {
//        Properties prop = new Properties();
//        prop.put(CLIENT_ID, userLoginId);
//        prop.put(SITE_NUMBER, new Integer(siteNumber));
//        Packet packet = new Packet(Type.RESET_CONTEST, source, destination, prop);
//        return packet;
//    }


    /**
     * Create packet for {@link PacketType.Type#RESET_ALL_CONTESTS}.
     * 
     * @param source
     * @param destination
     * @param userLoginId
     */
    public static Packet createResetAllSites(ClientId source, ClientId destination, ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        Packet packet = new Packet(Type.RESET_ALL_CONTESTS, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param languageDisplayList
     * @param userLoginId
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, LanguageDisplayList languageDisplayList,
            ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        prop.put(LANGUAGE_DISPLAY_LIST, languageDisplayList);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param problemDisplayList
     * @param userLoginId
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, ProblemDisplayList problemDisplayList,
            ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        prop.put(PROBLEM_DISPLAY_LIST, problemDisplayList);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param answer
     * @param userLoginId
     */
    public static Packet createUpdateSettingDefaultClarificationAnswer(ClientId source, ClientId destination, String answer,
            ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        prop.put(DEFAULT_CLARIFICATION_ANSWER, answer);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#STOP_ALL_CLOCKS}.
     * 
     * @param source
     * @param destination
     * @param userLoginId
     */
    public static Packet createStopAllClocks(ClientId source, ClientId destination, ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        Packet packet = new Packet(Type.STOP_ALL_CLOCKS, source, destination, prop);
        return packet;
    }

    // public static Packet createUpdateSetting(ClientId source, ClientId destination, BalloonSettings balloonSettings,
    // ClientId userLoginId) {
    // Properties prop = new Properties();
    // prop.put(CLIENT_ID, userLoginId);
    // prop.put(BALOON_SETTINGS, balloonSettings);
    // Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
    // return packet;
    // }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param clarification
     * @param userLoginId
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Clarification clarification,
            ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, userLoginId);
        prop.put(CLARIFICATION, clarification);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    // public static Packet createUpdateSetting(ClientId source, ClientId destination, ContestProperties contestProperties,
    // ClientId userLoginId) {
    // Properties prop = new Properties();
    // prop.put(CLIENT_ID, userLoginId);
    // prop.put(CONTEST_SETTINGS, contestProperties);
    // Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
    // return packet;
    // }

    /**
     * Create packet for {@link PacketType.Type#UPDATE_SETTING}.
     * 
     * @param source
     * @param destination
     * @param clarification
     * @param answer
     * @param userLoginId
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Clarification clarification, String answer,
            ClientId userLoginId) {
        Properties prop = new Properties();
        prop.put(CLARIFICATION, clarification);
        prop.put(CLIENT_ID, userLoginId);
        if (answer != null) {
            prop.put(CLARIFICATION_ANSWER, answer);
        }
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    /**
     * Create packet for {@link PacketType.Type#CONTEST_TIME}.
     * 
     * @param source
     * @param destination
     * @param inContestTime
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, ContestTime inContestTime) {
        Properties prop = new Properties();
        prop.put(PacketType.CONTEST_TIME, inContestTime);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createAddSetting(ClientId source, ClientId destination, Account[] accounts) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(ACCOUNT_ARRAY, accounts);
        return createPacket(PacketType.Type.ADD_SETTING, source, destination, prop);
    }
    
    public static Packet createAddSetting(ClientId source, ClientId destination, ClientSettings clientSettings) {
        Properties prop = new Properties();
        prop.put(CLIENT_SETTINGS, clientSettings);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }
    
    public static Packet createRunJudgmentUpdate(ClientId source, ClientId destination, Run run, ClientId whoJudgedId) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, whoJudgedId);
        prop.put(RUN, run);
        return createPacket(PacketType.Type.RUN_JUDGEMENT_UPDATE, source, destination, prop);
    }
    
    /**
     * Clone packet from existing packet.
     * 
     * Can reassign source and destination with affecting contents.
     * 
     * @param source
     * @param destination
     * @param packet
     * @return copy of the input packet
     */
    public static Packet clonePacket(ClientId source, ClientId destination, Packet packet) {
        Packet newPacket = createPacket(packet.getType(), source, destination, (Properties) packet.getContent());
        newPacket.setOriginalPacketNumber(packet.getOriginalPacketNumber());
        newPacket.setOriginalSourceId(packet.getOriginalSourceId());
        return newPacket;
    }
    
    /**
     * Clone packet and change packet type.
     * 
     * Can reassign type, source, and destination with affecting contents.
     * 
     * @param type
     * @param source
     * @param destination
     * @param packet
     * @return cloned Packet.
     */
    public static Packet clonePacket(PacketType.Type type, ClientId source, ClientId destination, Packet packet) {
        Packet newPacket = createPacket(type, source, destination, (Properties) packet.getContent());
        newPacket.setOriginalPacketNumber(packet.getOriginalPacketNumber());
        newPacket.setOriginalSourceId(packet.getOriginalSourceId());
        return newPacket;
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, Judgement judgement) {
        Properties prop = new Properties();
        prop.put(JUDGEMENT, judgement);
        return createPacket(PacketType.Type.UPDATE_SETTING, source, destination, prop);
    }

    public static Packet createAnsweredClarificationUpdate(ClientId source, ClientId destination, Clarification clarification, String answer, ClientId whoAnsweredIt) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, whoAnsweredIt);
        prop.put(CLARIFICATION, clarification);
        prop.put(CLARIFICATION_ANSWER, answer);
        Packet packet = new Packet(Type.CLARIFICATION_ANSWER_UPDATE, source, destination, prop);
        return packet;
    }

    public static Packet createClarificationNotAvailable(ClientId source, ClientId destination, Clarification clarification, ClientId requestFromId2) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, requestFromId2);
        prop.put(CLARIFICATION, clarification);
        Packet packet = new Packet(Type.CLARIFICATION_NOT_AVAILABLE, source, destination, prop);
        return packet;
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, ClientSettings clientSettings) {
        Properties prop = new Properties();
        prop.put(CLIENT_SETTINGS, clientSettings);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, ContestInformation contestInformation) {
        Properties prop = new Properties();
        prop.put(CONTEST_INFORMATION, contestInformation);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }
    
    public static Packet createUpdateSetting(ClientId source, ClientId destination, Judgement[] judgements) {
        Properties prop = new Properties();
        prop.put(JUDGEMENT_LIST, judgements);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createDeleteSetting (ClientId source, ClientId destination, Judgement judgement) {
        Properties prop = new Properties();
        prop.put(JUDGEMENT, judgement);
        Packet packet = new Packet(Type.DELETE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createDeleteSetting (ClientId source, ClientId destination, Problem problem) {
        Properties prop = new Properties();
        prop.put(PROBLEM, problem);
        Packet packet = new Packet(Type.DELETE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createDeleteSetting (ClientId source, ClientId destination, Language language) {
        Properties prop = new Properties();
        prop.put(LANGUAGE, language);
        Packet packet = new Packet(Type.DELETE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createReconnectPacket(ClientId source, ClientId destination, int inSiteNumber) {
        Properties prop = new Properties();
        prop.put(SITE_NUMBER, new Integer(inSiteNumber));
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.RECONNECT_SITE_REQUEST, source, destination, prop);
        return packet;
    }

    public static Packet createSecurityMessagePacket(ClientId source, ClientId destination, String message, ClientId whoCanceledRun, 
            ConnectionHandlerID connectionHandlerID, ContestSecurityException contestSecurityException, Packet inPacket) {
        Properties prop = new Properties();
        if (whoCanceledRun != null){
            prop.put(CLIENT_ID, whoCanceledRun);
        }
        prop.put(MESSAGE, message);
        if (connectionHandlerID != null){
            prop.put(CONNECTION_HANDLE_ID, connectionHandlerID);
        }
        if (inPacket != null){
            prop.put(PACKET, inPacket); 
        }
        if (contestSecurityException != null){
            prop.put(EXCEPTION, contestSecurityException);
        }
        
        Packet packet = new Packet(Type.SECURITY_MESSAGE, source, destination, prop);
        return packet;
    }

    public static Packet createRunStatusPacket(ClientId source, ClientId destination, Run run, ClientId judgeClient, RunExecutionStatus status) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, judgeClient);
        prop.put(RUN, run);
        prop.put(RUN_STATUS, status);
        return createPacket(PacketType.Type.RUN_EXECUTION_STATUS, source, destination, prop);
    }

    public static Packet createResetAllSitesPacket(ClientId source, ClientId destination, ClientId clientResettingContest, Profile newProfile, boolean eraseProblems, boolean eraseLanguages) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, clientResettingContest);
        prop.put(DELETE_PROBLEM_DEFINITIONS, new Boolean(eraseProblems));
        prop.put(DELETE_LANGUAGE_DEFINITIONS, new Boolean(eraseLanguages));
        prop.put(PROFILE, newProfile);
        return createPacket(PacketType.Type.RESET_ALL_CONTESTS, source, destination, prop);
      
    }
    
    public static Packet createCloneProfilePacket(ClientId source, ClientId destination, Profile profile2, ProfileCloneSettings settings, boolean switchNow) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PROFILE, profile2);
        prop.put(PROFILE_CLONE_SETTINGS, settings);
        prop.put(SWITCH_PROFILE, new Boolean (switchNow));
        return createPacket(PacketType.Type.CLONE_PROFILE, source, destination, prop);
    }

    public static Packet createSwitchProfilePacket(ClientId source, ClientId destination, Profile currentProfile, Profile switchToProfile, String contestPassword) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PROFILE, currentProfile);
        prop.put(NEW_PROFILE, switchToProfile);
        prop.put(CONTEST_PASSWORD, contestPassword);
        return createPacket(PacketType.Type.SWITCH_PROFILE, source, destination, prop);
    }
    
    // TODO fold contestTime and ContestInformation into  ContestLoginSuccessData

    public static Packet createUpdateProfileClientPacket(ClientId source, ClientId destination, Profile currentProfile, Profile switchToProfile, ContestLoginSuccessData data) {
        try {
            
            Properties prop = new Properties();
            
            prop.put(PROFILE, currentProfile);
            prop.put(NEW_PROFILE, switchToProfile);
            
            prop.put(CLIENT_ID, destination);
            prop.put(PROFILE_LIST, data.getProfiles()); 

            if (data.getContestSecurityPassword() != null) {
                prop.put(CONTEST_PASSWORD, data.getContestSecurityPassword());
            }
            
            addContestData(prop, data);

            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
            prop.put(SERVER_CLOCK_OFFSET, gregorianCalendar);

            if (data.getContestSecurityPassword() != null) {
                prop.put(CONTEST_PASSWORD, data.getContestSecurityPassword());
            }

            Packet packet = new Packet(Type.UPDATE_CLIENT_PROFILE, source, destination, prop);
            return packet;
            
        } catch (Exception e) {
            System.err.println("Exception creating UPDATE_CLIENT_PROFILE");
            e.printStackTrace(System.err);
            StaticLog.log("Exception in createUpdateProfileClientPacket ", e);
            throw new SecurityException(e.getMessage());
        }
    }

    public static Packet createFetchRunFilesPacket(ClientId source, ClientId destination, int lastRunId) {
        Properties prop = new Properties();
        prop.put(RUN_ID, new Integer(lastRunId));
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.FETCH_RUN_FILES, source, destination, prop);
        return packet;
    }
    
    public static Packet createRequestRemoteDataPacket(ClientId source, ClientId destination) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.REQUEST_REMOTE_DATA, source, destination, prop);
        return packet;
    }

    public static Packet createRunFilesPacket(ClientId source, ClientId destination, RunFiles[] files) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(RUN_FILES_LIST, files);
        Packet packet = new Packet(Type.UPDATE_RUN_FILES, source, destination, prop);
        return packet;
    }
    
    public static Packet createRequestServerStatusPacket(ClientId source, ClientId destination, Profile currentProfile) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PROFILE, currentProfile);
        prop.put(SITE_NUMBER, new Integer(destination.getSiteNumber()));
        Packet packet = new Packet(Type.REQUEST_SERVER_STATUS, source, destination, prop);
        return packet;
    }

    public static Packet createServerStatusPacket(ClientId source, ClientId destination,Profile currentProfile, Status status, Site site ) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PROFILE, currentProfile);
        prop.put(SITE, site);
        prop.put(PROFILE_STATUS, status);
        Packet packet = new Packet(Type.SERVER_STATUS, source, destination, prop);
        return packet;
    }

    public static Packet createSwitchSynchronizePacket(ClientId source, ClientId destination,Profile profile) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(PROFILE, profile);
        Packet packet = new Packet(Type.SYNCHRONIZE_REMOTE_DATA, source, destination, prop);
        return packet;
    }

    public static Packet createShutdownPacket(ClientId source, ClientId destination, int siteNumber) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, new Integer(siteNumber));
        Packet packet = new Packet(Type.SHUTDOWN, source, destination, prop);
        return packet;
    }

    public static Packet createShutdownAllServersPacket(ClientId source, ClientId destination) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.SHUTDOWN_ALL, source, destination, prop);
        return packet;
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, Category[] categories) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(CATEGORY_LIST, categories);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, Category category) {
        Properties prop = new Properties();
        prop.put(CATEGORY, category);
        return createPacket(PacketType.Type.UPDATE_SETTING, source, destination, prop);
    }

    public static Packet createAddSetting(ClientId source, ClientId destination, Category category) {
        Properties prop = new Properties();
        prop.put(CATEGORY, category);
        Packet packet = new Packet(Type.ADD_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createStartPlayback(ClientId source, ClientId destination, PlaybackInfo playbackInfo) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(PLAYBACK_INFO, playbackInfo);
        Packet packet = new Packet(Type.START_PLAYBACK, source, destination, prop);
        return packet;
    }
    
    public static Packet createUpdateSetting(ClientId source, ClientId destination, PlaybackInfo playbackInfo) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(PLAYBACK_INFO, playbackInfo);
        return createPacket(PacketType.Type.UPDATE_SETTING, source, destination, prop);
    }

    public static Packet createAutoRegisterRequest(ClientId source, ClientId destination, String autoRegistrationInfo) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(AUTO_REG_REQUEST_INFO, autoRegistrationInfo);
        return createPacket(Type.AUTO_REGISTRATION_LOGIN_REQUEST, source, destination, prop);
    }

    public static Packet createAutoRegReply(ClientId source, ClientId destination, Account account) {
        Properties prop = new Properties();
        prop.put(ACCOUNT, account);
        return createPacket(Type.AUTO_REGISTRATION_SUCCESS, source, destination, prop);
    }

    public static Packet createAddSetting(ClientId source, ClientId destination, Language[] languages) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(LANGUAGE_LIST, languages);
        return createPacket(PacketType.Type.ADD_SETTING, source, destination, prop);
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, Language[] languages) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(LANGUAGE_LIST, languages);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }

    public static Packet createAddSetting(ClientId source, ClientId destination, Group[] groups) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(GROUP_LIST, groups);
        return createPacket(PacketType.Type.ADD_SETTING, source, destination, prop);
    }

    public static Packet createUpdateSetting(ClientId source, ClientId destination, Group[] groups) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(SITE_NUMBER, source.getSiteNumber());
        prop.put(GROUP_LIST, groups);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }
    
    /**
     * List of team source files.
     * 
     * <P>
     * This is a response/reply to a {@link #createRunSourceFetchRequest(ClientId, ClientId, Run[])}.
     */
    public static Packet createUpdateSetting(ClientId source, ClientId destination, RunFiles [] runfiles) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(TEAM_RUN_SOURCE_FILES_LIST, runfiles);
        Packet packet = new Packet(Type.UPDATE_SETTING, source, destination, prop);
        return packet;
    }
    
    /**
     * Request a list of team source files.
     */
    public static Packet createRunSourceFetchRequest(ClientId source, ClientId destination, Run [] runList) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        prop.put(RUN_LIST, runList);
        Packet packet = new Packet(Type.REQUEST_FETCH_TEAMS_SUBMISSION_FILES, source, destination, prop);
        return packet;
    }
    

    /**
     * Auto judge ready to judge.
     * 
     */
    public static Packet createAvaiableToAutoJudge(ClientId source, ClientId destination) {
        Properties prop = new Properties();
        prop.put(CLIENT_ID, source);
        Packet packet = new Packet(Type.AVAILABLE_TO_AUTO_JUDGE, source, destination, prop);
        return packet;
    }

}
