package edu.csus.ecs.pc2.core.packet;

import java.io.Serializable;

/**
 * Defines packet types and property names used to store data in packets.
 * 
 * This class defines the PC<sup>2</sup> Packet Types. The class contains a collection of public static fields of type
 * <I>PacketType</I>, each of which defines a different type of PC<sup>2</sup> <I>packet</I> (see {@link Packet}).
 * <P>
 * <P>
 * 
 * @author PC<sup>2</sup> (pc2@ecs.csus.edu)
 * @version $Id$
 */

// TODO: Settings update from admin 
// TODO: Clar update from admin 
// TODO: import 
// TODO: export 
// TODO: force logoff 
// TODO: force transport disconnection 

// $HeadURL$
public final class PacketType implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3948306726936701813L;

    public static final String CLARIFICATIONS_LIST = "CLARIFICATIONS_LIST";

    public static final String JUDGEMENT_LIST = "JUDGEMENT_LIST";

    public static final String PROBLEM_LIST = "PROBLEM_LIST";

    public static final String LANGUAGE_LIST = "LANGUAGE_LIST";

    public static final String GENERAL_CLAR_PROBLEM = "GENERAL_CLAR_PROBLEM";

    public static final String RUN_LIST = "RUN_LIST";

    public static final String SITE_LIST = "SITE_LIST";
    
    public static final String SITE_NUMBER = "SITE_NUMBER";

    public static final String CONTEST_TIME = "CONTEST_TIME";

    public static final String DATA_FILE_LIST = "DATA_FILE_LIST";

    public static final String ACCOUNT_LIST = "ACCOUNT_LIST";

    public static final String DISPLAY_LISTS = "DISPLAY_LISTS";

    public static final String BALLOON_SETTINGS_LIST = "BALLOON_SETTINGS_LIST";

    public static final String SCORING_PROPERTIES = "SCORING_PROPERTIES";

    public static final String CONTEST_PROPERTIES = "CONTEST_PROPERTIES";

    /**
     * Logged in users
     */
    public static final String LOGGED_IN_USERS = "LOGGED_IN_USERS";

    /**
     * Connections to server (user not logged in, just connected)
     */
    public static final String CONNECTIONS = "CONNECTIONS";

    public static final String CONTEST_TIME_LIST = "CONTEST_TIME_LIST";

    /**
     * All defined packet types.
     * 
     * @see PacketType
     * 
     * @author pc2@ecs.csus.edu
     */

    public enum Type {

        /**
         * Unknown or not initialized Packet.
         * 
         * Created as default state to avoid initializing a Packet with invalid type.
         */
        UNKNOWN,

        /**
         * InternalContest Settings or partial settings.
         * 
         * InternalContest Settings from server<br>
         * From server to clients <br>
         * From server to remote server <br>
         * From Admin to server, to update the settings <br>
         * Contents: Properties
         */
        SETTINGS,

        /**
         * Request Setting from server.
         * 
         * From client to server<br>
         * From remote server to server <br>
         * Contents: Settings
         */
        SETTINGS_REQUEST,

        /**
         * Run submitted by a team.
         * 
         * From team to server<br>
         * Contents: Properties: Run and RunFiles
         */
        RUN_SUBMISSION,

        /**
         * Packet confirming that run has been added.
         * <P>
         * When a server receives and adds a run to the run database, this packet is sent back to the team as a confirmation.
         * <P>
         * From server to team <br>
         * From server to judges, admins, servers<br>
         * <br>
         * Contents: {@link edu.csus.ecs.pc2.core.model.Run}
         * 
         * @see #RUN_SUBMISSION
         */
        RUN_SUBMISSION_CONFIRM,

        /**
         * Judge requests a run to be checked out.
         * <P>
         * From judge to server<br>
         * <P>
         * 
         * <P>
         * Contents: Run
         */
        RUN_REQUEST,

        /**
         * Run returned to judge corresponding to a RUN_REQUEST.
         * <P>
         * From judge to server<br>
         * Contents: Properties (Run and RunFiles)
         */
        RUN_CHECKOUT,

        /**
         * Run returned to judge cooresponding to a RUN_REJUDGED_REQUEST.
         * <P>
         * From judge to server<br>
         * Contents: Properties (Run and RunFiles)
         */
        RUN_REJUDGE_CHECKOUT,

        /**
         * Could not get run, from server to requestor.
         * <P>
         * When a judge requests a run (RUN_REQUEST) and the run is not available, this packet is sent to the judge.<br>
         * 
         * From server to judge <br>
         * Contents: Run
         */
        RUN_NOTAVAILABLE,

        /**
         * Judge un-checks out a run.
         * <P>
         * If a judge has received a run via a {@link PacketType.Type#RUN_CHECKOUT} packet, if the judge does not want to judge the
         * run and cancel the run this packet is sent to the server for that run.
         * <P>
         * From judge to server<br>
         * From server to judges (as a 'Take') <br>
         * From server to remote server (as a 'Take') <br>
         * Contents: Run
         */
        RUN_UNCHECKOUT,

        /**
         * Judgement from a judge.
         * <P>
         * A judge has rendered a judgement for the run, this packet contains the judgement to be sent to the server.
         * <P>
         * From judge to server <br>
         * From server to server <br>
         * Contents: Run and judgement
         */
        RUN_JUDGEMENT,

        /**
         * Judgement from a server to all other servers and clients.
         * 
         *  A server has updated a run with a run judgement
         *  <P>
         *  From server to server<br>
         *  From server to team (who submitted run) <br>
         *  From server to admin, judge, board <br>
         *  
         */
        RUN_JUDGEMENT_UPDATE,

        /**
         * Request run list from server.
         * <P>
         * From server to judge <br>
         * From server to server <br>
         * From server to team <br>
         * Contents: Enumeration of class Run
         */
        RUN_LIST,

        /**
         * Clar submitted by a team (or judge).
         * <P>
         * From team to server <br>
         * From judge to server <br>
         * Contents: ClarificationData
         */
        CLARIFICATION_SUBMISSION,

        // CLAR_UPDATED - there is no Admin update feature for clars, but if
        // there were...

        /**
         * Judge requests a clar to be checked out.
         * <P>
         * From judge to server<br>
         * <P>
         * CLAR_REQUEST is a request to check out a clar, this can be for a specific clar (site:runId), or for the next clar, or for
         * the next clar that matches a particular filter.
         * <P>
         * Contents: SubmissionInfo
         */
        CLARIFICATION_REQUEST,

        /**
         * Confirmation of a clar processed by server.
         * <P>
         * Packet contains clarification info about a submitted clar.
         * <P>
         * From server to team<br>
         * From server to judge<br>
         * From server to remote server<br>
         * Contents: ClarificationData
         */

        CLARIFICATION_SUBMISSION_CONFIRM,

        /**
         * Judge checks out a clar.
         * <P>
         * From judge to server<br>
         * <u>After processed by the server </u> <br>
         * From server to judges<br>
         * From server to Admin<br>
         * From server to remote server<br>
         * Contents: ClarificationData
         */
        CLARIFICATION_CHECKOUT,

        /**
         * Clar Request denied, clar not available for checkout.
         * <P>
         * From server to judge <br>
         * Contents: TODO code
         */
        CLARIFICATION_NOT_AVAILABLE,
        /**
         * Judge un-checksout a clar.
         * <P>
         * From judge to server<br>
         * <u>After processed by the server </u> <br>
         * From server to remote server<br>
         * From server to judge<br>
         * From server to Admin<br>
         * Contents: ClarificationData
         */
        CLARIFICATION_UNCHECKOUT,

        /**
         * A list of clarifications.
         * 
         * Contains: Enumeration of Clarifications
         * 
         * @see PacketFactory
         */
        CLARIFICATION_LIST,

        /**
         * Password change request to server.
         * <P>
         * From client to server <br>
         * Contents: previous password, new password
         */
        PASSWORD_CHANGE_REQUEST,

        /**
         * Confirmation of password change (or not :).
         * <P>
         * Package indicates whether password change was successful or not. From server to client <br>
         * Contents: boolean (true - changed, false - not changed) and a message.
         */
        PASSWORD_CHANGE_RESULTS,

        /**
         * Export data used to manually send/receive data.
         * 
         * From Admin to Admin (external media transfer) <br>
         * Contents: Hashtable of various settings
         */
        EXPORT_DATA,

        /**
         * Request login into system.
         * <P>
         * All clients and servers use this to login to a contest.
         * <P>
         * From client to server<br>
         * From server to remote server<br>
         * Contents: Login information SessionId (partially filled in)
         */
        LOGIN_REQUEST,

        /**
         * Logoff of a client or server.
         * <P>
         * Request logoff by a client or server.
         * <P>
         * From client to server <br>
         * From remote server to server <br>
         * <u>After processed by the server </u> <br>
         * From server to Admin <br>
         * From server to remote server<br>
         * Contents: Login information SessionId (partially filled in)
         */
        LOGOUT,
        
        /**
         * User has loggged on.
         * 
         * From server to server<br>
         * from server to admin<br>
         */
        LOGIN,

        /**
         * Will logoff or disconnect (clear) configuration.
         * 
         */
        FORCE_DISCONNECTION,

        /**
         * Request Run from server (read-only, not checked out).
         * <P>
         * 
         * From client to server <br>
         * From server to server<br>
         * Contents: site and runid
         */
        RUN_STATUS_REQUEST,

        /**
         * Request List of Run from server.
         * <P>
         * From scoreboard to server <br>
         * From server to server<br>
         * From server to scoreboard<br>
         * 
         * Contents: who requested run list TODO code
         */
        RUN_LIST_REQUEST,

        /**
         * Notify judges that a clar has been submitted.
         * 
         * From server to judge<br>
         * From server to server<br>
         * Contents: ClarificationData
         */
        CLAR_AVAILABLE,

        /**
         * Reset Client when contest/site is reset.
         * 
         * From server to all clients<br>
         * Contents: String - message about reset
         */
        CONTEST_RESET,

        /**
         * Failure to Login.  
         * 
         * This could be caused by a number of causes which include
         * but are not limited to: no such account, invalid login,
         * invalid password, inactive account.
         * 
         * From server to client<br>
         * From server to server <br>
         * Contents: SessionId/status
         */
        LOGIN_FAILED,

        /**
         * Successful Login.
         * 
         * In all cases this contains the contest settings. <P>
         * 
         * From client to server<br>
         * From server to server <br>
         * Note: server login as well.
         * <P>
         * Contents: InternalContest Settings.
         */
        LOGIN_SUCCESS,
        
        /**
         * Notify judges that a run has been submitted.
         * 
         * From server to judges, admins, servers<br>
         * Contents: Run
         */
        RUN_AVAILABLE,
        /**
         * Changed judgement.
         * <P>
         * Admin updates via edit run.<br>
         * Judge re-judge.<br>
         * <br>
         * From server to individual team<br>
         * From server to judges, admins and servers<br>
         * Contents: Run and judgement
         */
        RUN_REJUDGED,
        /**
         * Balloon Delivered.
         * <P>
         * From balloon client to server <br>
         * Contents: Run
         */
        BALLOON_DELIVERED,
        /**
         * Clar has been checked out/selected to be judged/answered.
         * 
         * From server to judge<br>
         * From server to server<br>
         * From server to Admin<br>
         * Contents: Clarification
         */
        CLAR_NOT_AVAILABLE,
        /**
         * An update of a run from Admin.
         * <P>
         * An Admin has changed/updated a run and that information needs to be updated on the server (db). This is information
         * beyond a rejudgement (like delete, change submission time, etc). It may also include a run re-judgement.
         */
        RUN_UPDATE,
        
        /**
         * Run has been updated.
         * 
         * From server to server<br>
         * From server to clients<br>
         */
        RUN_UPDATE_NOTIFICATION,

        /**
         * An update of a clarification from Admin.
         * 
         */
        CLARIFICATION_UPDATE,
        /**
         * Shutdown request server or client.
         * <P>
         * A orderly shutdown of a client or server.
         * 
         * From client to server <br>
         * From server to client <br>
         */
        SHUTDOWN,

        /**
         * Message for user.
         * 
         * Contains: String
         */
        MESSAGE,
        /**
         * Update one or more settings <br>
         * from Admin to server<br>
         * from server to server<br>
         * Contains: Properties
         */
        UPDATE_SETTING,
        /**
         * Add a new setting. <br>
         * from Admin to server<br>
         * from server to server<br>
         * Contains: Properties (one or more properties to add to config).
         */
        ADD_SETTING,
        
        /**
         * Remove setting.
         * 
         * This packet contains a setting or list of settings to
         * remove from the settings ({@link edu.csus.ecs.pc2.core.model.InternalContest}
         * <P>
         * from Admin to server<br>
         * from server to server<br>
         * Contains: Properties (one or more properties to add to config).
         */
        DELETE_SETTING,
        /**
         * Generate Accounts. 
         */
        GENERATE_ACCOUNTS,
        /**
         * Stop Context Clock. Admin to Server. Contains: SITE_NUMBER, CLIENT_ID
         */
        START_CONTEST_CLOCK,
        /**
         * Stop InternalContest Clock. Admin to Server. Contains: SITE_NUMBER, CLIENT_ID
         */
        STOP_CONTEST_CLOCK,
        
        /**
         * Clock has been stopped, from Server. Contents: CONTEST_TIME, CLIENT_ID
         */
        CLOCK_STOPPED,
        
        /**
         * Clock has been started, from Server. Contents: CONTEST_TIME, CLIENT_ID
         */
        CLOCK_STARTED,

        /**
         * Update InternalContest Clock.
         * 
         * Update elapsed, remaining, contest length, running or not. <br>
         * Contains: Properties (ContestTime)
         */
        UPDATE_CONTEST_CLOCK,
        /**
         * User Login.
         * 
         * Admin and Server. Contains: Properties (ClientId)
         */
        ACCOUNT_LOGIN,

        /**
         * Clarification canceled by judge, no available to all judges.
         * 
         * Contains: Properties (Clarification)
         */
        CLARIFICATION_AVAILABLE,
        /**
         * The answer to a clarification
         */
        CLARIFICATION_ANSWER,
        /**
         * A request to rejudge a run.
         * 
         */
        RUN_REJUDGE_REQUEST,
        /**
         * A connection is dropped.
         */
        DROPPED_CONNECTION,
        /**
         * A connection is established.
         */
        ESTABLISHED_CONNECTION,
        /**
         * Start all sites' clocks.
         */
        START_ALL_CLOCKS,
        /**
         * Reset all site's clars/runs/clock.
         */
        RESET_ALL_CONTESTS,
        /**
         * Stop clocks on all sites.
         */
        STOP_ALL_CLOCKS,
        /**
         * 
         */
        SCORING_PROPERTIES,
        /**
         * 
         */
        CONTEST_PROPERTIES,
        /**
         * 
         */
        CLARIFICATION_ANSWER_UPDATE,
        /**
         * Server settings.
         */
        SERVER_SETTINGS,
        /**
         * Reconnect Site Request.
         * 
         */
        RECONNECT_SITE_REQUEST,
        /**
         * 
         */
        SECURITY_MESSAGE,
        /**
         * Run taken from judge
         */
        RUN_REVOKED,
        /**
         * Clarification taken from judge.
         */
        CLARIFICATION_REVOKED,
        /**
         * Fetch a run (do not checkout or change run state).
         */
        FETCH_RUN,
        /**
         * Fetched run.
         */
        FETCHED_REQUESTED_RUN,
        /**
         * Run Execution Status.
         * 
         * @see RunExecutionStatus
         */
        RUN_EXECUTION_STATUS,
        /**
         * A notification that a run has been checked out 
         */
        RUN_CHECKOUT_NOTIFICATION,
        /**
         * Switch to a new profile.
         * <br>
         * contains: old profile, new profile. <br>
         * Sent from Admin to Server. <br>  
         * Profile flow is:
         * <ol>
         * <li>{@link #SWITCH_PROFILE} from Admin
         * <li>{@link #UPDATE_CLIENT_PROFILE} or {@link #UPDATE_SERVER_PROFILE}
         * </ol>
         * 
         */
        SWITCH_PROFILE, 
        /**
         * Create/clone a new profile, possibly switch profile too.
         * 
         */
        CLONE_PROFILE,
        /**
         * Update/change a server profile.
         */
        UPDATE_SERVER_PROFILE,
        /**
         * Causes client to reset/change profile (409 packet).
         */
        UPDATE_CLIENT_PROFILE,
        /**
         * This is a run confirmation with RunFiles.
         * 
         * After a {@link #RUN_SUBMISSION} this packet will be
         * send to all servers then each server will send out
         * a {@link #RUN_SUBMISSION_CONFIRM} to the clients.
         * 
         * @see #RUN_SUBMISSION
         */
        RUN_SUBMISSION_CONFIRM_SERVER
    }

    /**
     * Constructor is private as this is a utility class which should not be extended or invoked.
     */
    private PacketType() {
        super();
    }

}
