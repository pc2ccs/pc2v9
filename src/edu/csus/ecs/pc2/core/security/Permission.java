package edu.csus.ecs.pc2.core.security;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * User permissions.
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
         * Able to edit settings in general.
         */
        EDIT_SETTINGS,
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

        hash.put(Type.BALLOON_EMAIL, "Allowed to e-mail balloons");
        hash.put(Type.BALLOON_OUTPUT_SHUTOFF, "Allowed to shutoff output");
        hash.put(Type.BALLOON_PRINT, "Allowed to print balloons");
        hash.put(Type.CHANGE_PASSWORD, "Change password");
        hash.put(Type.ADD_ACCOUNT, "Add Accounts");
        hash.put(Type.EDIT_ACCOUNT, "Edit Accounts");
        hash.put(Type.EDIT_PERMISSIONS, "Edit Permissions");
        hash.put(Type.EDIT_PERMISSIONS, "Edit Runs");
        hash.put(Type.EXECUTE_RUN, "Execute but not judge runs");
        hash.put(Type.JUDGE_RUN, "Judge runs");
        hash.put(Type.LOGIN, "Allowed to login");
        hash.put(Type.REJUDGE_RUN, "Re-judge runs");
        hash.put(Type.TEST_RUN, "can test run");
        hash.put(Type.VIEW_CLARIFICATIONS, "View Clarifications");
        hash.put(Type.VIEW_RUNS, "View Runs");
        hash.put(Type.ADD_PROBLEM, "Add a problem");
        hash.put(Type.ADD_SITE, "Add a site");
        hash.put(Type.ADD_SETTINGS, "Add general settings");
        hash.put(Type.ANSWER_CLARIFICATION, "Answer a clarification");
        hash.put(Type.EDIT_LANGUAGE, "Edit a language");
        hash.put(Type.EDIT_PROBLEM, "Edit a problem");
        hash.put(Type.EDIT_SITE, "Edit a site");
        hash.put(Type.EDIT_SETTINGS, "Edit general settings");
        hash.put(Type.EDIT_RUN, "Edit a run");
        hash.put(Type.EDIT_CLARIFICATION, "Edit a clarification");
        hash.put(Type.SUBMIT_CLARIFICATION, "Submit a clarification");
        hash.put(Type.SUBMIT_RUN, "Submit a run");
        hash.put(Type.VIEW_ALL_JUDGEMENTS, "Execute but not judge runs");
        hash.put(Type.VIEW_STANDINGS, "View standings");
        hash.put(Type.VIEW_SUMMARY_ATTEMPTS_GRID, "View Summary Attempts Grid");
        hash.put(Type.FORCE_LOGOFF_CLIENT, "Force client logoff");
        hash.put(Type.FORCE_LOGOFF_SERVER, "Force server logoff");
        hash.put(Type.DISPLAY_ON_SCOREBOARD, "Can be shown on scoreboard displays");
        
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
        hash.put(Type.ALLOWED_TO_RECONNECT_SERVER, "Allowed to reconnect to server");

        hash.put(Type.ALLOWED_TO_AUTO_JUDGE, "Allowed to auto judge");

        hash.put(Type.VIEW_SECURITY_ALERTS, "View Security Alerts Log/View");

        hash.put(Type.ALLOWED_TO_FETCH_RUN, "Allowed to fetch run (not checkout)");
        hash.put(Type.RESPECT_EOC_SUPPRESSION, "Apply End of Contest Controls");
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
