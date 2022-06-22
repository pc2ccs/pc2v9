// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.security;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * User permissions.
 * 
 * These are lists of user permissions/abilities.
 * 
 * @see PermissionList
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class Permission implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 530537916675047901L;

    /**
     * Permissions that user can perform.
     * 
     * @see PermissionList
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public enum Type {
        /**
         * 
         */
        BALLOON_EMAIL,
        /**
         * 
         */
        BALLOON_OUTPUT_SHUTOFF,
        /**
         * 
         */
        BALLOON_PRINT,
        /**
         * 
         */
        CHANGE_PASSWORD,
        /**
         * Add accounts.
         */
        ADD_ACCOUNT,
        /**
         * Modify and delete accounts.
         */
        EDIT_ACCOUNT,
        /**
         * Can user request and judge runs ?
         */
        JUDGE_RUN,
        /**
         * Is client allowed to login ?
         */
        LOGIN,
        /**
         * Can client be shown on scoreboard
         */
        DISPLAY_ON_SCOREBOARD,
        /**
         * Can user request and judge a already judged run ?.
         */
        REJUDGE_RUN,
        /**
         * 
         */
        TEST_RUN,
        /**
         * 
         */
        VIEW_CLARIFICATIONS,
        /**
         * 
         */
        VIEW_RUNS,
        /**
         * 
         */
        ADD_PROBLEM,
        /**
         * 
         */
        ADD_SITE,
        /**
         * 
         */
        ADD_SETTINGS,
        /**
         * 
         */
        ANSWER_CLARIFICATION,
        /**
         * 
         */
        ADD_LANGUAGE,
        /**
         * 
         */
        EDIT_LANGUAGE,
        /**
         * 
         */
        EDIT_PERMISSIONS,
        /**
         * 
         */
        EDIT_PROBLEM,
        /**
         * 
         */
        EDIT_RUN,
        /**
         * 
         */
        EDIT_SITE,
        /**
         * Read-only execute a run, but not judge run.
         */
        EXECUTE_RUN,
        /**
         * 
         */
        SUBMIT_CLARIFICATION,
        /**
         * 
         */
        EDIT_CLARIFICATION,
        /**
         * 
         */
        SUBMIT_RUN,
        /**
         * 
         */
        VIEW_ALL_JUDGEMENTS,
        /**
         * 
         */
        VIEW_STANDINGS,
        /**
         * 
         */
        VIEW_DELETED_RUNS,
        /**
         * 
         */
        VIEW_SUMMARY_ATTEMPTS_GRID,
        /**
         * Ability to force logoff a client
         */
        FORCE_LOGOFF_CLIENT,
        /**
         * Ability to force logoff server
         */
        FORCE_LOGOFF_SERVER,
        /**
         * Give run to judges.
         */
        GIVE_RUN,
        /**
         * Take run from judges
         */
        TAKE_RUN,
        /**
         * View run judgements history
         */
        VIEW_RUN_JUDGEMENT_HISTORIES,
        /**
         * 
         */
        EXTRACT_RUNS,
        /**
         * 
         */
        GIVE_CLARIFICATION,
        /**
         * 
         */
        TAKE_CLARIFICATION,
        /**
         * Generate a new clarification and answer
         */
        GENERATE_NEW_CLARIFICATION,
        /**
         * Start contest clock
         */
        START_CONTEST_CLOCK,
        /**
         * Stop contest clock
         */
        STOP_CONTEST_CLOCK,
        /**
         * 
         */
        EDIT_CONTEST_CLOCK,
        /**
         * 
         */
        ALLOWED_TO_RECONNECT_SERVER,
        /**
         * 
         */
        ALLOWED_TO_AUTO_JUDGE,
        /**
         * 
         */
        VIEW_SECURITY_ALERTS,
        /**
         * 
         */
        ALLOWED_TO_FETCH_RUN,
        /**
         * Use the End of Contest Control settings.
         */
        RESPECT_EOC_SUPPRESSION,
        /**
         * The board/API scoring respects the judge's send notify to team.
         * 
         *  The SA (Scoring Algorithm) needs to respect the 
         *  {@link edu.csus.ecs.pc2.core.model.JudgementRecord#isSendToTeam()} setting.
         */
        RESPECT_NOTIFY_TEAM_SETTING,
        /**
         * Reset Contest
         */
        RESET_CONTEST,
        /**
         * Switch Profile
         */
        SWITCH_PROFILE, 
        /**
         * Change, clone, create profile.
         */
        CLONE_PROFILE, 
        /**
         * Export Profile.
         */
        EXPORT_PROFILE,
        /**
         * 
         */
        ADD_GROUPS,
        /**
         * 
         */
        EDIT_GROUPS,
        /**
         * 
         */
        EDIT_AJ_SETTINGS,
        /**
         * 
         */
        ADD_JUDGEMENTS,
        /**
         * 
         */
        EDIT_JUDGEMENTS,
        /**
         * 
         */
        ADD_NOTIFICATIONS,
        /**
         * 
         */
        EDIT_NOTIFICATIONS,
        /**
         * 
         */
        VIEW_PASSWORDS,
        /**
         * 
         */
        SHUTDOWN_SERVER,
        /**
         * 
         */
        SHUTDOWN_ALL_SERVERS,
        /**
         * 
         */
        ADD_CATEGORY, 
        /**
         * 
         */
        EDIT_CATEGORY, 
        /**
         * Start a replay playback
         */
        START_PLAYBACK,
        /**
         * Stop a replay playback
         */
        STOP_PLAYBACK, 
        /**
         * Change settings for a replay playback
         */
        EDIT_PLAYBACK,
        /**
         * Change or add event feed
         */
        EDIT_EVENT_FEED, 
        /**
         * View event feed
         */
        VIEW_EVENT_FEED,
        /**
         * Act as proxy team in shadow mode
         */
        SHADOW_PROXY_TEAM,

        /**
         * Allow User to enable/disable shadowing
         */
        ENABLE_SHADOW_MODE,

        /**
         * Start/Stop Shadowing
         */
        START_STOP_SHADOWING,

        /**
         * View Runs Compare Scoreboard
         */
        COMPARE_RUNS_SCOREBOARD,

        /**
         * Modify Shadow settings.
         */
        MODIFY_SHADOW_SETTINGS,

    };

    private Hashtable<Type, String> hash = new Hashtable<Type, String>();

    public Permission() {
        loadDescription();
    }

    /**
     * Load descriptions into hash.
     *
     */
    private void loadDescription() {

        hash.put(Type.BALLOON_EMAIL, "E-mail balloons");
        hash.put(Type.BALLOON_OUTPUT_SHUTOFF, "Shutoff output");
        hash.put(Type.BALLOON_PRINT, "Print balloons");
        hash.put(Type.CHANGE_PASSWORD, "Change password");
        hash.put(Type.ADD_ACCOUNT, "Add Accounts");
        hash.put(Type.EDIT_ACCOUNT, "Edit Accounts");
        hash.put(Type.EDIT_PERMISSIONS, "Edit Permissions");
        hash.put(Type.EXECUTE_RUN, "Execute but not judge runs");
        hash.put(Type.JUDGE_RUN, "Judge runs");
        hash.put(Type.LOGIN, "Login");
        hash.put(Type.REJUDGE_RUN, "Re-judge runs");
        hash.put(Type.TEST_RUN, "Test run");
        hash.put(Type.VIEW_CLARIFICATIONS, "View Clarifications");
        hash.put(Type.VIEW_RUNS, "View Runs");
        hash.put(Type.ADD_PROBLEM, "Add problem");
        hash.put(Type.ADD_SITE, "Add site");
        hash.put(Type.ADD_SETTINGS, "Add general settings");
        hash.put(Type.ANSWER_CLARIFICATION, "Answer a clarification");
        hash.put(Type.EDIT_LANGUAGE, "Edit a language");
        hash.put(Type.EDIT_PROBLEM, "Edit a problem");
        hash.put(Type.EDIT_SITE, "Edit a site");
        hash.put(Type.EDIT_RUN, "Edit a run");
        hash.put(Type.EDIT_CLARIFICATION, "Edit a clarification");
        hash.put(Type.SUBMIT_CLARIFICATION, "Submit a clarification");
        hash.put(Type.SUBMIT_RUN, "Submit a run");
        hash.put(Type.VIEW_ALL_JUDGEMENTS, "Execute but not judge runs");
        hash.put(Type.VIEW_STANDINGS, "View standings");
        hash.put(Type.VIEW_SUMMARY_ATTEMPTS_GRID, "View Summary Attempts Grid");
        hash.put(Type.FORCE_LOGOFF_CLIENT, "Force client logoff");
        hash.put(Type.FORCE_LOGOFF_SERVER, "Force server logoff");
        hash.put(Type.DISPLAY_ON_SCOREBOARD, "Shown on scoreboard displays");
        
        hash.put(Type.GIVE_RUN, "Give runs to judges");
        hash.put(Type.TAKE_RUN, "Take runs from judges");
        hash.put(Type.VIEW_RUN_JUDGEMENT_HISTORIES, "View run judgement histories");
        hash.put(Type.EXTRACT_RUNS, "Extract run contents from runs database");
        
        hash.put(Type.GIVE_CLARIFICATION, "Give clarifications to judges");
        hash.put(Type.TAKE_CLARIFICATION, "Take clarifications from judges");
        hash.put(Type.GENERATE_NEW_CLARIFICATION, "Generate a clarification and answer");

        hash.put(Type.VIEW_DELETED_RUNS, "View deleted runs");
        
        hash.put(Type.START_CONTEST_CLOCK, "Start contest clock");
        hash.put(Type.STOP_CONTEST_CLOCK, "Stop contest clock");
        
        hash.put(Type.EDIT_CONTEST_CLOCK, "Edit Contest Clock/Times");
        hash.put(Type.ALLOWED_TO_RECONNECT_SERVER, "Reconnect server");

        hash.put(Type.ALLOWED_TO_AUTO_JUDGE, "Auto judge");

        hash.put(Type.VIEW_SECURITY_ALERTS, "View Security Alerts Log/View");

        hash.put(Type.ALLOWED_TO_FETCH_RUN, "Fetch run (not checkout)");
        hash.put(Type.RESPECT_EOC_SUPPRESSION, "Apply End of Contest Controls");
        
        hash.put(Type.RESPECT_NOTIFY_TEAM_SETTING, "Apply Notify Team Setting to Board/SA");
        hash.put(Type.RESET_CONTEST, "Reset Contest");
        
        hash.put(Type.CLONE_PROFILE, "Clone or switch profile");
        hash.put(Type.EXPORT_PROFILE, "Export profile");
        hash.put(Type.SWITCH_PROFILE, "Switch profile");
        hash.put(Type.RESET_CONTEST, "Reset contest/profile");
        
        hash.put(Type.ADD_LANGUAGE,"Add Language");
        hash.put(Type.EXPORT_PROFILE, "Export Profile");
        hash.put(Type.ADD_GROUPS, "Add Groups");
        hash.put(Type.EDIT_GROUPS,"Edit Groups");
        hash.put(Type.EDIT_AJ_SETTINGS,"Edit Auto Judge Settings");
        hash.put(Type.ADD_JUDGEMENTS,"Add Judgement");
        hash.put(Type.ADD_NOTIFICATIONS,"Add Notification");
        hash.put(Type.EDIT_NOTIFICATIONS,"Edit Notification");
        hash.put(Type.VIEW_PASSWORDS,"View Passwords");
        
        hash.put(Type.EDIT_JUDGEMENTS,"Edit Judgement");
        hash.put(Type.SHUTDOWN_SERVER,"Shutdown server");
        hash.put(Type.SHUTDOWN_ALL_SERVERS,"Shutdown all servers");
        
        hash.put(Type.ADD_CATEGORY,"Add Category");
        hash.put(Type.EDIT_CATEGORY,"Edit Category");
        
        hash.put(Type.START_PLAYBACK,"Start Playback");
        hash.put(Type.STOP_PLAYBACK,"Stop Playback");
        hash.put(Type.EDIT_PLAYBACK,"Edit Playback Settings");
        
        hash.put(Type.EDIT_EVENT_FEED,"Edit Event Feeds");
        hash.put(Type.VIEW_EVENT_FEED,"View Event Feeds");
        hash.put(Type.SHADOW_PROXY_TEAM,"Shadow Proxy Team");

        hash.put(Type.ENABLE_SHADOW_MODE, "Enable Shadow Mode");
        hash.put(Type.START_STOP_SHADOWING, "Start/Stop Shadowing");
        hash.put(Type.COMPARE_RUNS_SCOREBOARD, "Allow Compare Runs/Scoreboard");
        hash.put(Type.MODIFY_SHADOW_SETTINGS, "Modify Shadow Settings");

    }

    /**
     * Return a description for permission type.
     * 
     * @param type
     * @return Description of type.
     */
    public String getDescription(Type type) {
        return hash.get(type);
    }
}
