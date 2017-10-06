package edu.csus.ecs.pc2.services.core;

import java.util.Calendar;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Judgement JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JudgementJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Run run) {
        //    id 
        //    submission_id 
        //    judgement_type_id 

        StringBuilder stringBuilder = new StringBuilder();

        appendPair(stringBuilder, "id", run.getNumber());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "submission_id", run.getNumber());
        stringBuilder.append(", ");

        if (run.isJudged()) {
            ElementId judgementId = run.getJudgementRecord().getJudgementId();
            Judgement judgement = contest.getJudgement(judgementId);

            appendPair(stringBuilder, "judgement_type_id", judgement.getAcronym());
        } else {

            appendPairNullValue(stringBuilder, "judgement_type_id");
        }

        //        start_time  TIME    yes     no  provided by CCS     absolute time when judgement started
        //        start_contest_time  RELTIME     yes     no  provided by CCS     contest relative time when judgement started
        //        end_time    TIME    yes     yes     provided by CCS     absolute time when judgement completed
        //        end_contest_time    RELTIME     yes     yes     provided by CCS     contest relative time when judgement completed 

        //        [{"id":"189549","submission_id":"wf2017-32163123xz3132yy","judgement_type_id":"CE","start_time":"2014-06-25T11:22:48.427+01",
        //            "start_contest_time":"1:22:48.427","end_time":"2014-06-25T11:23:32.481+01","end_contest_time":"1:23:32.481"},
        //           {"id":"189550","submission_id":"wf2017-32163123xz3133ub","judgement_type_id":null,"start_time":"2014-06-25T11:24:03.921+01",
        //            "start_contest_time":"1:24:03.921","end_time":null,"end_contest_time":null}
        //          ]

        Calendar wallElapsed = calculateElapsedWalltime(contest, run.getElapsedMS());

        stringBuilder.append(", ");
        appendPair(stringBuilder, "start_time", wallElapsed); // absolute time when judgement started ex. 2014-06-25T11:24:03.921+01

        stringBuilder.append(", ");
        appendPair(stringBuilder, "start_contest_time", XMLUtilities.formatSeconds(run.getElapsedMS())); // contest relative time when judgement started. ex. 1:24:03.921

        stringBuilder.append(", ");
        appendPairNullValue(stringBuilder, "end_time"); // TODO CLICS DATA ADD - add code to save in JudgementRecord in Executable

        stringBuilder.append(", ");
        appendPairNullValue(stringBuilder, "end_contest_time"); // TODO CLICS DATA ADD  add code to save in JudgementRecord - in Executable

        return stringBuilder.toString();
    }

}
