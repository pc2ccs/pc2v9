package edu.csus.ecs.pc2.core.security;

import java.util.Hashtable;

/**
 * User permissions.
 * 
 * Contains list of all tasks.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class Permission {

    /**
     * List of tasks to give permission to perform..
     * 
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
         * 
         */
        LOGIN,
        /**
         * 
         */
        REJUDGE,
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

        // TODO replace entries that show description of todo
        hash.put(Type.BALLOON_EMAIL, "Allow balloon e-mail");
        hash.put(Type.BALLOON_OUTPUT_SHUTOFF, "Allowed to shutoff output");
        hash.put(Type.BALLOON_PRINT, "Allowed to do balloon print");
        hash.put(Type.CHANGE_PASSWORD, "Change password");
        hash.put(Type.EDIT_ACCOUNT, "Edit Accounts");
        hash.put(Type.EDIT_PERMISSIONS, "Edit Permissions");
        hash.put(Type.EDIT_PERMISSIONS, "Edit Runs");
        hash.put(Type.EXECUTE_RUN, "Execute but not judge runs");
        hash.put(Type.JUDGE_RUN, "Judge runs");
        hash.put(Type.LOGIN, "Login");
        hash.put(Type.REJUDGE, "Re-judge runs");
        hash.put(Type.TEST_RUN, "test run");
        hash.put(Type.VIEW_CLARIFICATIONS, "todo");
        hash.put(Type.VIEW_RUNS, "todo");
        hash.put(Type.ADD_PROBLEM, "todo");
        hash.put(Type.ADD_SITE, "todo");
        hash.put(Type.ADD_SETTINGS, "todo");
        hash.put(Type.ANSWER_CLARIFICATION, "todo");
        hash.put(Type.EDIT_LANGUAGE, "todo");
        hash.put(Type.EDIT_PROBLEM, "todo");
        hash.put(Type.EDIT_SITE, "todo");
        hash.put(Type.EDIT_SETTINGS, "todo");
        hash.put(Type.SUBMIT_CLARIFICATION, "todo");
        hash.put(Type.SUBMIT_RUN, "todo");
        hash.put(Type.VIEW_ALL_JUDGEMENTS, "Execute but not judge runs");
        hash.put(Type.VIEW_STANDINGS, "todo");
        hash.put(Type.VIEW_SUMMARY_ATTEMPTS_GRID, "todo");
    }

    public static void main(String[] args) {
        Permission permissionCl = new Permission();
        for (Type permission : Type.values()) {
            System.out.println(permission + " is " + permissionCl.getDescription(permission));
        }
    }

    /**
     * Return description for permission.
     * 
     * @param permission
     * @return
     */
    public String getDescription(Type permission) {
        return hash.get(permission);
    }

}
