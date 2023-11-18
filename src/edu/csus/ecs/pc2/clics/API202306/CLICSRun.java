// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.JSONTool;

/**
 * CLICS Judgment Run
 * Contains information about a Problem
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonFilter("rtFilter")
public class CLICSRun {

    @JsonProperty
    private String id;

    @JsonProperty
    private String submission_id;

    @JsonProperty
    private String judgement_type_id;

//    Only for "score" type contests, N/A for ICPC pass/fail
//    @JsonProperty
//    private String score;

    @JsonProperty
    private String start_time;

    @JsonProperty
    private String start_contest_time;

    @JsonProperty
    private String end_time;

    @JsonProperty
    private String end_contest_time;

    @JsonProperty
    private double max_run_time;

    /**
     * Fill in properties for a Problem description.
     * 
     * @param model The contest
     * @param problem The problem 
     */
    public CLICSRun(IInternalContest model, Run submission, Set<String> exceptProps) {
        // {"id":"189549","submission_id":"wf2017-32163123xz3132yy","judgement_type_id":"CE","start_time":"2014-06-25T11:22:48.427+01",
        // "start_contest_time":"1:22:48.427","end_time":"2014-06-25T11:23:32.481+01","end_contest_time":"1:23:32.481"}
        id = submission.getElementId().toString();
        submission_id = JSONTool.getSubmissionId(submission);

        Date startJudgeDate = submission.getJudgeStartDate();
        if(startJudgeDate != null) {
            start_time = Utilities.getIso8601formatterWithMS().format(startJudgeDate);
            start_contest_time = ContestTime.formatTimeMS(startJudgeDate.getTime() - model.getContestTime().getContestStartTime().getTime().getTime());             
        }

        if (submission.isJudged()) {

            JudgementRecord judgementRecord = submission.getJudgementRecord();
            
            // only output its judgment and end times if this is the final judgment
            if (!judgementRecord.isPreliminaryJudgement()) {

                // Fetch judgement_type_id from judgement acronym
                judgement_type_id = model.getJudgement(judgementRecord.getJudgementId()).getAcronym();
                Calendar wallElapsed = JSONTool.calculateElapsedWalltime(model, judgementRecord.getWhenJudgedTime() * 60000);
                if (wallElapsed != null) {
                    end_time = Utilities.getIso8601formatter().format(wallElapsed.getTime());
                }
                // is null if there are no elapsedMinutes in the contest
                // when judged is in minutes convert to milliseconds
                end_contest_time = ContestTime.formatTimeMS(judgementRecord.getWhenJudgedTime() * 60000);
                max_run_time = ((double)judgementRecord.getExecuteMS())/1000.;
            } else {
                // Filter out max_run_time from serialization
                exceptProps.add("max_run_time");
            }
        }
        
    }
}
