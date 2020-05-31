// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Run Utilities.
 * 
 * @author pc2@ecs.csus.edu
 */

public final class RunUtilities {

    private RunUtilities() {

    }
    
    public static Run createNewRun (Run run, IInternalContest contest){
        Run newRun = new Run(run.getSubmitter(), contest.getLanguage(run.getLanguageId()), contest.getProblem(run.getProblemId()));
        newRun.setElementId(run.getElementId());
        newRun.setElapsedMS(run.getElapsedMS());
        newRun.setDeleted(run.isDeleted());
        newRun.setNumber(run.getNumber());
        newRun.setStatus(RunStates.NEW);
        return newRun;
    }

    /**
     * Determine whether to suppress run notification based on NotificationSettings.
     * 
     * Check the last judgement for this run against the notification settings. <br>
     * If there are no notifications for the run's problem, returns false. (aka do not suppress) <br>
     * If there are no NotificationSettings for the problem, returns false.
     * 
     * @param judgementNotificationsList
     *            - list of notifications
     * @param run
     *            - the run judgement
     * @param contestTime
     * @return true if run has judgement to be suppressed
     */
    public static boolean supppressJudgement(JudgementNotificationsList judgementNotificationsList, Run run, ContestTime contestTime) {

        JudgementRecord judgementRecord = run.getJudgementRecord();
        return supppressJudgement(judgementNotificationsList, run, judgementRecord, contestTime);
    }

    /**
     * Determine whether judgementRecord for Run should be suppressed.
     * 
     * @see {@link #supppressJudgement(JudgementNotificationsList, Run, ContestTime)}
     * @param judgementNotificationsList
     * @param run
     * @param judgementRecord
     * @param contestTime
     * @return
     */
    public static boolean supppressJudgement(JudgementNotificationsList judgementNotificationsList, Run run, JudgementRecord judgementRecord, ContestTime contestTime) {

        // No judgements
        if (judgementRecord == null) {
            return false;
        }

        if (judgementNotificationsList == null || judgementNotificationsList.getList().length == 0) {
            /**
             * No notifications at all, do not suppress
             */
            return false;
        }

        NotificationSetting notificationSetting = (NotificationSetting) judgementNotificationsList.get(run.getProblemId());

        if (notificationSetting == null) {
            /**
             * No notification for problem, do not suppress
             */
            return false;
        }

        boolean solvedProblem = judgementRecord.isSolved();

        /**
         * This is the remaining time in the contest when the run was submitted.
         */
        long runRemainingTime = contestTime.getContestLengthMins() - run.getElapsedMins();

        if (judgementRecord.isPreliminaryJudgement()) {

            if (solvedProblem) {
                // prelim Yes
                if (notificationSetting.getPreliminaryNotificationYes().isNotificationSupressed()) {

                    return runRemainingTime <= notificationSetting.getPreliminaryNotificationYes().getCuttoffMinutes();

                } // else - fall through
            } else {
                if (notificationSetting.getPreliminaryNotificationNo().isNotificationSupressed()) {

                    return runRemainingTime <= notificationSetting.getPreliminaryNotificationNo().getCuttoffMinutes();

                } // else - fall through
            }

        } else {
            // Is final judgement
            if (solvedProblem) {
                // prelim Yes
                if (notificationSetting.getFinalNotificationYes().isNotificationSupressed()) {

                    return runRemainingTime <= notificationSetting.getFinalNotificationYes().getCuttoffMinutes();

                } // else - fall through
            } else {
                if (notificationSetting.getFinalNotificationNo().isNotificationSupressed()) {

                    return runRemainingTime <= notificationSetting.getFinalNotificationNo().getCuttoffMinutes();

                } // else - fall through
            }
        }

        // Fall through condition, do not subpress
        return false;
    }

    /**
     * Determine whether judgement for Run should be suppressed.
     * 
     * @param run
     * @param scoreboardFreeze in seconds
     * @return
     */
    public static boolean supppressJudgement(Run run, long scoreboardFreeze) {
        if (run.getElapsedMS()/1000 > scoreboardFreeze) {
            // run came in after the scoreboardFreeze
            return true;
        }
        // Fall through condition, do not suppress
        return false;
    }
    
    
    /**
     * Has run been submitted in a shadow contest?
     * 
     * @param contest
     * @param runId
     * @return true if override run number matches runId
     */
    public static boolean isAlreadySubmitted(IInternalContest contest, String runId) {

        if (isDigits(runId)) {

            long runNumber = Long.parseLong(runId.trim());
            Run[] runs = contest.getRuns();
            for (Run run : runs) {
                if (run.getOverrideNumber() == runNumber) {
                    return true;
                }
            }
        } else {
            System.err.println("Warning not code to match non-digit run is '" + runId + "'");
        }

        return false;
    }

    /**
     * is string a set of digits
     * 
     * @param s
     * @return true if digits, false otherwise
     */
    public static boolean isDigits(String s) {

        try {
            Long.parseLong(s.trim());
            return true;
        } catch (Exception e) {
            return false;

        }
    }

}
