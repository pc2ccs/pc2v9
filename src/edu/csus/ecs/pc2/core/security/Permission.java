package edu.csus.ecs.pc2.core.security;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * User permissions.
 * 
 * Permission.Type is a enumeration of all the permissions that
 * a client can have.
 * 
 * @see PermissionList
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Permission implements Serializable {

    /**
     * Permissions that user can perform.
     * 
     * @see PermissionList
     * @author pc2@ecs.csus.edu
     * 
     */
    public enum Type {
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
         * 
         */
        EDIT_ACCOUNT,
        /**
         * 
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
         * 
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
         * 
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
        VIEW_SUMMARY_ATTEMPTS_GRID,
        /**
         * Ability to force logoff a client
         */
        FORCE_LOGOFF_CLIENT,
        /**
         * Ability to force logoff server
         */
        FORCE_LOGOFF_SERVER,
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

        hash.put(Type.BALLOON_EMAIL, "Allow balloon e-mail");
        hash.put(Type.BALLOON_OUTPUT_SHUTOFF, "Allowed to shutoff output");
        hash.put(Type.BALLOON_PRINT, "Allowed to do balloon print");
        hash.put(Type.CHANGE_PASSWORD, "Change password");
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
    }

    /**
     * Return a description for permission type.
     * 
     * @param permission
     * @return Description of type.
     */
    public String getDescription(Type type) {
        return hash.get(type);
    }

}
