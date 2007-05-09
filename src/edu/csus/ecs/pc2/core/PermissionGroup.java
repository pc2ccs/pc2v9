package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Holds Default Permissions for users.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class PermissionGroup {

    private PermissionList teamPermissionList = new PermissionList();

    private PermissionList judgePermissionList = new PermissionList();

    private PermissionList scoreboardPermissionList = new PermissionList();

    private PermissionList administratorPermissionList = new PermissionList();

    public PermissionGroup() {
        initialize();
    }

    private void initialize() {

        // Give admin all permissions

        for (Type type : Permission.Type.values()) {
            administratorPermissionList.addPermission(type);
        }
        
        administratorPermissionList.removePermission(Type.DISPLAY_ON_SCOREBOARD);


        /**
         * Team permissions
         */

        teamPermissionList.addPermission(Type.LOGIN);
        teamPermissionList.addPermission(Type.DISPLAY_ON_SCOREBOARD);
        teamPermissionList.addPermission(Type.TEST_RUN);
        teamPermissionList.addPermission(Type.VIEW_CLARIFICATIONS);
        teamPermissionList.addPermission(Type.VIEW_RUNS);
        teamPermissionList.addPermission(Type.SUBMIT_CLARIFICATION);
        teamPermissionList.addPermission(Type.SUBMIT_RUN);

        /**
         * Judge permissions
         */

        judgePermissionList.addPermission(Type.BALLOON_EMAIL);
        judgePermissionList.addPermission(Type.CHANGE_PASSWORD);
        judgePermissionList.addPermission(Type.JUDGE_RUN);
        judgePermissionList.addPermission(Type.LOGIN);
        judgePermissionList.addPermission(Type.REJUDGE_RUN);
        judgePermissionList.addPermission(Type.TEST_RUN);
        judgePermissionList.addPermission(Type.VIEW_CLARIFICATIONS);
        judgePermissionList.addPermission(Type.VIEW_RUNS);
        judgePermissionList.addPermission(Type.ANSWER_CLARIFICATION);
        judgePermissionList.addPermission(Type.SUBMIT_CLARIFICATION);
        judgePermissionList.addPermission(Type.SUBMIT_RUN);
        judgePermissionList.addPermission(Type.VIEW_ALL_JUDGEMENTS);
        judgePermissionList.addPermission(Type.VIEW_STANDINGS);
        judgePermissionList.addPermission(Type.VIEW_SUMMARY_ATTEMPTS_GRID);
        judgePermissionList.addPermission(Type.VIEW_RUN_JUDGEMENT_HISTORIES);
        judgePermissionList.addPermission(Type.GENERATE_NEW_CLARIFICATION);
        
        /**
         * Board Permissions
         */

        scoreboardPermissionList.addPermission(Type.BALLOON_EMAIL);
        scoreboardPermissionList.addPermission(Type.BALLOON_OUTPUT_SHUTOFF);
        scoreboardPermissionList.addPermission(Type.BALLOON_PRINT);
        scoreboardPermissionList.addPermission(Type.CHANGE_PASSWORD);
        scoreboardPermissionList.addPermission(Type.LOGIN);
        scoreboardPermissionList.addPermission(Type.VIEW_STANDINGS);
        scoreboardPermissionList.addPermission(Type.VIEW_SUMMARY_ATTEMPTS_GRID);

    }

    /**
     * Get default permissions for users.
     * 
     * @param type
     *            client type.
     * @return list of permissions.
     */
    public PermissionList getPermissionList(ClientType.Type type) {

        if (type.equals(ClientType.Type.TEAM)) {
            return teamPermissionList;
        } else if (type.equals(ClientType.Type.ADMINISTRATOR)) {
            return administratorPermissionList;
        } else if (type.equals(ClientType.Type.JUDGE)) {
            return judgePermissionList;
        } else if (type.equals(ClientType.Type.SCOREBOARD)) {
            return scoreboardPermissionList;
        } else {
            // default scoreboard permissions.
            return scoreboardPermissionList;
        }
    }

    // for (Type type : Permission.Type.values()) {
    // System.out.println("foo.addPermission(Type." + type + ");");
    // }
}
