package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;

/**
 * Run Utilities.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class RunUtilities {

    private RunUtilities(){
        
    }

    /**
     * Determine whether to suppress run notification based on NotificationSettings.
     * 
     * Check the last judgement for this run against the notification settings.
     * <br>
     * If there are no notifications for the run's problem, returns false. (aka do not suppress)
     * <br>
     * If there are no NotificationSettings for the problem, returns false.
     * 
     * @param judgementNotificationsList - list of notifications
     * @param run - the run judgement
     * @param contestTime
     * @return true if run has judgement to be suppressed
     */
    public static boolean supppressJudgement (JudgementNotificationsList judgementNotificationsList, Run run, ContestTime contestTime){

        if (judgementNotificationsList == null || judgementNotificationsList.getList().length == 0){
            /**
             * No notifications at all, do not suppress
             */
            return false;
        }
        
        NotificationSetting notificationSetting = (NotificationSetting) judgementNotificationsList.get(run.getProblemId());
     
        if (notificationSetting == null){
            /**
             * No notification for problem, do not suppress
             */
            return false;
        }
        
        JudgementRecord judgementRecord = run.getJudgementRecord();
        
        // No judgements
        if (judgementRecord == null){
            return false;
        }
        
        boolean solvedProblem = judgementRecord.isSolved();
        
        /**
         * This is the remaining time in the contest when the run was submitted.
         */
        long runRemainingTime = contestTime.getConestLengthMins() - run.getElapsedMins(); 
        
        if (judgementRecord.isPreliminaryJudgement()){
            
            if (solvedProblem){
                // prelim Yes
                if (notificationSetting.getPreliminaryNotificationYes().isNotificationSupressed()){
                    
                    return runRemainingTime <= notificationSetting.getPreliminaryNotificationYes().getCuttoffMinutes();
                    
                } // else - fall through
            } else {
                if (notificationSetting.getPreliminaryNotificationNo().isNotificationSupressed()){
                    
                    return runRemainingTime <= notificationSetting.getPreliminaryNotificationNo().getCuttoffMinutes();
                    
                } // else - fall through
            }
            
        } else { 
            // Is final judgement
            if (solvedProblem){
                // prelim Yes
                if (notificationSetting.getFinalNotificationYes().isNotificationSupressed()){
                    
                    return runRemainingTime <= notificationSetting.getFinalNotificationYes().getCuttoffMinutes();
                    
                } // else - fall through
            } else {
                if (notificationSetting.getFinalNotificationNo().isNotificationSupressed()){
                    
                    return runRemainingTime <= notificationSetting.getFinalNotificationNo().getCuttoffMinutes();
                    
                } // else - fall through
            }
        }
        
        // Fall through condition, do not subpress
        return false;
    }
}
