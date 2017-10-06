package edu.csus.ecs.pc2.services.core;

import java.util.Calendar;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;

public class SubmissionJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Run run) {
        //    id 
        //    language_id 
        //    problem_id 
        //    team_id 

        StringBuilder stringBuilder = new StringBuilder();

        appendPair(stringBuilder, "id", run.getNumber());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "language_id", getLanguageIndex(contest, run.getLanguageId()));
        stringBuilder.append(", ");

//        Problem problem = contest.getProblem(run.getProblemId());
//        appendPair(stringBuilder, "problem_id", problem.getShortName());
        appendPair(stringBuilder, "problem_id", getProblemIndex(contest, run.getProblemId()));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "team_id", run.getSubmitter().getClientNumber());
        stringBuilder.append(", ");

        //    time 
        //    contest_time 
        //    entry_point 

        Calendar wallElapsed = calculateElapsedWalltime(contest, run.getElapsedMS());

        appendPair(stringBuilder, "time", wallElapsed);

        stringBuilder.append(", ");
        appendPair(stringBuilder, "contest_time", XMLUtilities.formatSeconds(run.getElapsedMS()));

        stringBuilder.append(", ");
        appendPair(stringBuilder, "entry_point", "Main"); // TODO CLICS DATA ADD ADD  the team's submitted executable name is not available at this time.

        return stringBuilder.toString();
    }
}
