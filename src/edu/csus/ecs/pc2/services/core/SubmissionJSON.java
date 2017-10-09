package edu.csus.ecs.pc2.services.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Submission JSON - new Run submitted in contest.
 * 
 * "Submissions, a.k.a. attempts to solve problems in the contest." 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class SubmissionJSON extends JSONUtilities {
    
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode element = mapper.createObjectNode();

    public String createJSON(IInternalContest contest, Run run) {
        
//    id  ID  yes     no  CCS     identifier of the submission. Usable as a label, typically a low incrementing number
//    language_id     ID  yes     no  CCS     identifier of the language submitted for
//    problem_id  ID  yes     no  CCS     identifier of the problem submitted for
//    team_id     ID  yes     no  CCS     identifier of the team that made the submission
        
        element = mapper.createObjectNode();
        element.put("id", Integer.toString(run.getNumber()));
        element.put("language_id", Integer.toString(getLanguageIndex(contest, run.getLanguageId())));
        element.put("problem_id", Integer.toString(getProblemIndex(contest, run.getProblemId())));
        element.put("team_id", new Integer(run.getSubmitter().getClientNumber()).toString());
        
//    time    TIME    yes     no  CCS     timestamp of when the submission was made
//    contest_time    RELTIME     yes     no  CCS     contest relative time when the submission was made
//    entry_point     string  yes     yes     CCS     code entry point for specific languages 
        
        element.put("time", Utilities.getIso8601formatterWithMS().format(run.getCreateDate()));
        element.put("contest_time", ContestTime.formatTimeMS(run.getElapsedMS()));
        
//        element.put("entry_point", ContestTime.formatTimeMS(run.getElapsedMS())); // TODO add entry_point value
        
        return stripOuterJSON(element.toString());
        
//        
//        
//        StringBuilder stringBuilder = new StringBuilder();
//
//        appendPair(stringBuilder, "id", run.getNumber());
//        stringBuilder.append(", ");
//
//        appendPair(stringBuilder, "language_id", getLanguageIndex(contest, run.getLanguageId()));
//        stringBuilder.append(", ");
//
//        appendPair(stringBuilder, "problem_id", getProblemIndex(contest, run.getProblemId()));
//        stringBuilder.append(", ");
//
//        appendPair(stringBuilder, "team_id", run.getSubmitter().getClientNumber());
//        stringBuilder.append(", ");
//
//        //    time 
//        //    contest_time 
//        //    entry_point 
//
//        Calendar wallElapsed = calculateElapsedWalltime(contest, run.getElapsedMS());
//
//        appendPair(stringBuilder, "time", wallElapsed);
//
//        stringBuilder.append(", ");
//        appendPair(stringBuilder, "contest_time", XMLUtilities.formatSeconds(run.getElapsedMS()));
//
//        stringBuilder.append(", ");
//        appendPair(stringBuilder, "entry_point", "Main"); // TODO CLICS DATA ADD ADD  the team's submitted executable name is not available at this time.
//
//        return stringBuilder.toString();
    }
}
