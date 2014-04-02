package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.security.Permission.Type;

/**
 * Holds Default Permissions for users.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PermissionGroup {

    private PermissionList teamPermissionList = new PermissionList();

    private PermissionList judgePermissionList = new PermissionList();

    private PermissionList scoreboardPermissionList = new PermissionList();

    private PermissionList administratorPermissionList = new PermissionList();

    private PermissionList serverPermissionList = new PermissionList();

    private PermissionList spectatorPermissionList = new PermissionList();

    private PermissionList feederPermissionList = new PermissionList();

    
    public PermissionGroup() {
        initialize();
    }

    private void initialize() {

        // Give admin all permissions
        // Give server all permissions

        for (Type type : Permission.Type.values()) {
            administratorPermissionList.addPermission(type);
            serverPermissionList.addPermission(type);
        }
        
        /**
         * Then remove certain permissions
         */
        
        Type[] removeAdmin = { Type.BALLOON_EMAIL, Type.BALLOON_PRINT, Type.DISPLAY_ON_SCOREBOARD, Type.RESPECT_EOC_SUPPRESSION, //
                Type.RESPECT_NOTIFY_TEAM_SETTING };
        
   
        for (Type type : removeAdmin) {
            administratorPermissionList.removePermission(type);
        }
        
        Type[] removeServer = { Type.BALLOON_EMAIL, Type.BALLOON_PRINT, Type.DISPLAY_ON_SCOREBOARD, Type.RESET_CONTEST, //
                Type.CLONE_PROFILE, Type.EXPORT_PROFILE, Type.SWITCH_PROFILE, Type.RESET_CONTEST };
        
        
        for (Type type : removeServer) {
            serverPermissionList.removePermission(type);
        }
        
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

        judgePermissionList.addPermission(Type.CHANGE_PASSWORD);
        judgePermissionList.addPermission(Type.JUDGE_RUN);
        judgePermissionList.addPermission(Type.LOGIN);
        judgePermissionList.addPermission(Type.REJUDGE_RUN);
        judgePermissionList.addPermission(Type.TEST_RUN);
        judgePermissionList.addPermission(Type.VIEW_CLARIFICATIONS);
        judgePermissionList.addPermission(Type.VIEW_RUNS);
        judgePermissionList.addPermission(Type.ANSWER_CLARIFICATION);
        judgePermissionList.addPermission(Type.SUBMIT_CLARIFICATION);
        judgePermissionList.addPermission(Type.VIEW_ALL_JUDGEMENTS);
        judgePermissionList.addPermission(Type.VIEW_STANDINGS);
        judgePermissionList.addPermission(Type.VIEW_SUMMARY_ATTEMPTS_GRID);
        judgePermissionList.addPermission(Type.VIEW_RUN_JUDGEMENT_HISTORIES);
        judgePermissionList.addPermission(Type.GENERATE_NEW_CLARIFICATION);
        judgePermissionList.addPermission(Type.ALLOWED_TO_AUTO_JUDGE);

        /**
         * Spectator Permissions
         */

        spectatorPermissionList.addPermission(Type.CHANGE_PASSWORD);
//        spectatorPermissionList.addPermission(Type.JUDGE_RUN);
        spectatorPermissionList.addPermission(Type.LOGIN);
//        spectatorPermissionList.addPermission(Type.REJUDGE_RUN);
        spectatorPermissionList.addPermission(Type.TEST_RUN);
        spectatorPermissionList.addPermission(Type.VIEW_CLARIFICATIONS);
        spectatorPermissionList.addPermission(Type.VIEW_RUNS);
//        spectatorPermissionList.addPermission(Type.ANSWER_CLARIFICATION);
//        spectatorPermissionList.addPermission(Type.SUBMIT_CLARIFICATION);
        spectatorPermissionList.addPermission(Type.VIEW_ALL_JUDGEMENTS);
        spectatorPermissionList.addPermission(Type.VIEW_STANDINGS);
        spectatorPermissionList.addPermission(Type.VIEW_SUMMARY_ATTEMPTS_GRID);
        spectatorPermissionList.addPermission(Type.VIEW_RUN_JUDGEMENT_HISTORIES);
//        spectatorPermissionList.addPermission(Type.GENERATE_NEW_CLARIFICATION);
//        spectatorPermissionList.addPermission(Type.ALLOWED_TO_AUTO_JUDGE);
        spectatorPermissionList.addPermission(Type.ALLOWED_TO_FETCH_RUN);
        
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
        

        /**
         * Event Feeder and Feeder list.
         */
        feederPermissionList.addPermission(Type.CHANGE_PASSWORD);
        feederPermissionList.addPermission(Type.JUDGE_RUN);
        feederPermissionList.addPermission(Type.LOGIN);
        feederPermissionList.addPermission(Type.REJUDGE_RUN);
        feederPermissionList.addPermission(Type.TEST_RUN);
        feederPermissionList.addPermission(Type.VIEW_CLARIFICATIONS);
        feederPermissionList.addPermission(Type.VIEW_RUNS);
        feederPermissionList.addPermission(Type.ANSWER_CLARIFICATION);
        feederPermissionList.addPermission(Type.SUBMIT_CLARIFICATION);
        feederPermissionList.addPermission(Type.VIEW_ALL_JUDGEMENTS);
        feederPermissionList.addPermission(Type.VIEW_STANDINGS);
        feederPermissionList.addPermission(Type.VIEW_SUMMARY_ATTEMPTS_GRID);
        feederPermissionList.addPermission(Type.VIEW_RUN_JUDGEMENT_HISTORIES);


    }

    /**
     * Get default permissions for users.
     * 
     * @param type
     *            client type.
     * @return list of permissions.
     */
    public PermissionList getPermissionList(ClientType.Type type) {

        switch (type) {
            case ADMINISTRATOR:
                return administratorPermissionList;
            case SERVER:
                return serverPermissionList;
            case JUDGE:
            case EXECUTOR:
                return judgePermissionList;
            case SPECTATOR:
                return spectatorPermissionList;
            case FEEDER:
                 return feederPermissionList;
            case SCOREBOARD:
                return scoreboardPermissionList;
            // case TEAM:
            default:
                return teamPermissionList;
        }
    }

    // for (Type type : Permission.Type.values()) {
    // System.out.println("foo.addPermission(Type." + type + ");");
    // }
}
