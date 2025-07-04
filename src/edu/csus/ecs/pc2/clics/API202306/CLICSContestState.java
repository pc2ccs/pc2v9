// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Contest State
 *
 * @author John Buck
 *
 */
// 2023-06 Spec specifically says each property must be null or contain a TIME value, so always include all properties
// But the spec also says the fields are optional, so I assume, if it's null, that means the CCS
// supports that property, which PC2 does.  If it did not support the property, then it would
// not be included in the JSON.  (At least that is my interpretation -- JohnB)
@JsonInclude(JsonInclude.Include.ALWAYS)
public class CLICSContestState {

    @JsonProperty
    private String started;

    @JsonProperty
    private String frozen;

    @JsonProperty
    private String ended;

    @JsonProperty
    private String thawed;

    @JsonProperty
    private String finalized;

    @JsonProperty
    private String end_of_updates;

    /*
     * These are non-standard PC2 specific properties to support pausing the contest
     * 'paused' is the CONTEST elapsed time at which the contest was paused.  If set, it means the contest
     * is currently paused. (HH:MM:SS.uuu).
     *
     * 'resumed' is the real (wall) time the contest was last resumed (or started, including initial contest start).
     * You can tell if the contest had been paused at some point if resumed != started (ISO time).
     */
    @JsonProperty
    private String paused;

    @JsonProperty
    private String resumed;

    /*
     * Non-standard property supplying a client with the current contest time (HH:MM:SS.uuu).
     */
    @JsonProperty
    private String contest_time;

    /**
     * Fill in properties for contest state as per 2023-06 spec
     *
     * @param model place to get contest times from
     * @param ci optional contest information.  If null, use the model's info.  This constructor
     *      is also used by ContestInformation objects that may come from an event listener, so
     *      we make it variable as it may not be from the current model.
     */
    public CLICSContestState(IInternalContest model, ContestInformation ci) {

        if (model.getContestTime().isContestStarted()) {
            if(ci == null) {
                ci = model.getContestInformation();
            }
            ContestTime ct = model.getContestTime();

            started = Utilities.getIso8601formatterWithMS().format(ct.getContestStartTime().getTime());
            if (ct.isPastEndOfContest()) {
                Calendar endedDate = IJSONTool.calculateElapsedWalltime(model, ct.getContestLengthMS());
                if (endedDate != null) {
                    ended = Utilities.getIso8601formatterWithMS().format(endedDate.getTimeInMillis());
                }
            }

            contest_time = Utilities.formatDuration(ct.getElapsedMS());
            if(ct.isContestRunning() == false) {
                if(ended == null) {
                    // This means the contest was paused - can't pause a contest if it is ended... so ended must be null
                    paused = contest_time;
                }
            }
            resumed = Utilities.getIso8601formatterWithMS().format(ct.getResumeTime().getTime());

            String scoreboardFreezeDuration = ci.getFreezeTime();
            Date thawedDate = null;

            if (scoreboardFreezeDuration != null && scoreboardFreezeDuration.trim().length() > 0) {
                // set default freeze time in seconds to be the end of the contest
                long freezeTimeSecs = ct.getContestLengthSecs();
                long freezeDurationSecs = Utilities.convertStringToSeconds(scoreboardFreezeDuration);
                // if freeze duration is valid, adjust freeze time in seconds to be when freeze starts
                if (freezeDurationSecs != -1) {
                    // convert time since start of contest
                    freezeTimeSecs = model.getContestTime().getContestLengthSecs() - freezeDurationSecs;
                }
                // FIXME this date should be stored in ContestInformation
                if (ct.getElapsedSecs() >= freezeTimeSecs) {
                    Calendar freezeCal = IJSONTool.calculateElapsedWalltime(model, freezeTimeSecs * 1000);
                    if (freezeCal != null) {
                        frozen = Utilities.getIso8601formatterWithMS().format(freezeCal.getTime());
                    }
                }
                if (ci.isUnfrozen()) {
                    thawedDate = ci.getThawed();
                    if (thawedDate != null) {
                        thawed = Utilities.getIso8601formatterWithMS().format(thawedDate);
                    }
                }
            }
            // FIXME this should only be shown if the contest is thawed for public users
            FinalizeData fData = model.getFinalizeData();
            if (fData != null) {
                Date fDate = fData.getCertificationDate();
                // end of updates can only be set if contest is finalized.
                if(fDate != null) {
                    finalized = Utilities.getIso8601formatterWithMS().format(fDate);
                    // If contest was frozen, it also must be thawed before end of updates is set
                    if(frozen != null) {
                        // the test on thawedDate is paranoia.  It must be set above for thawed to be set,
                        // but I wanted to make that clear here.
                        if(thawed != null && thawedDate != null) {
                            // if thawedDate is after finalized date, use it as end of updates, otherwise use finalized
                            if(thawedDate.after(fDate)) {
                                end_of_updates = thawed;
                            } else {
                                end_of_updates = finalized;
                            }
                        }
                    } else {
                        // the contest was never frozen, but now finalized, so end of updates is set.
                        end_of_updates = finalized;
                    }
                }
            }
        }
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for contest state info " + e.getMessage();
        }
    }
}
