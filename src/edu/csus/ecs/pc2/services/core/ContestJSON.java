package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * JSON for contest.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ContestJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        ContestInformation info = contest.getContestInformation();
        ContestTime time = contest.getContestTime();

        String path = contest.getProfile().getProfilePath();
        String contestUUID = path;
        if (path.startsWith("profiles")) {
            contestUUID = path.substring(9);
        }

        appendPair(stringBuilder, "id", contestUUID);
        stringBuilder.append(", ");

        String s = info.getContestShortName();
        if (s == null) {
            s = info.getContestTitle();
        }

        appendPair(stringBuilder, "name", s);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "formal_name", info.getContestTitle());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "start_time", info.getScheduledStartTime());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "duration", time.getContestLengthStr());
        stringBuilder.append(", ");

        String freezeTime = info.getFreezeTime();
        if (freezeTime != null) {
            appendPair(stringBuilder, "scoreboard_freeze_duration", info.getFreezeTime());
            stringBuilder.append(", ");
        }

        String penalty = DefaultScoringAlgorithm.getDefaultProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO);

        if (penalty != null && penalty.matches("[0-9]+")) {
            appendPair(stringBuilder, "penalty_time", Integer.parseInt(penalty));
            stringBuilder.append(", ");
        }

        // Start states array

        stringBuilder.append("\"state\":{");

        appendPair(stringBuilder, "state.running", time.isContestRunning());
        stringBuilder.append(", ");

        boolean finalized = false;
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null && finalizeData.isCertified()) {
            finalized = true;
        }

        appendPair(stringBuilder, "state.frozen", isContestFrozen(info, time));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "state.final", finalized);

        stringBuilder.append("}"); // end states array

        return stringBuilder.toString();
    }
    
    /**
     * Past frozen time?
     */
    private boolean isContestFrozen(ContestInformation info, ContestTime time) {

        String freeze = info.getFreezeTime();

        if (freeze != null) {
            long ms = convertToMs(freeze);
            return time.getElapsedMS() > ms;
        }

        return false;
    }
    



} 
