package edu.csus.ecs.pc2.services.core;

import java.util.Calendar;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Clarification JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ClarificationJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Clarification clarification) {

        StringBuilder stringBuilder = new StringBuilder();

        //        Id   ID  yes     no  provided by CCS     identifier of the clarification
        //        from_team_id    ID  yes     yes     provided by CCS     identifier of team sending this clarification request, null if a clarification sent by jury
        //        to_team_id  ID  yes     yes     provided by CCS     identifier of the team receiving this reply, null if a reply to all teams or a request sent by a team
        //        reply_to_id     ID  yes     yes     provided by CCS     identifier of clarification this is in response to, otherwise null

        appendPair(stringBuilder, "id", Integer.toString(clarification.getNumber()));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "from_team_id", Integer.toString(clarification.getSubmitter().getClientNumber()));
        stringBuilder.append(", ");

        if (clarification.isSendToAll()) {
            appendPairNullValue(stringBuilder, "to_team_id");
        } else {
            appendPair(stringBuilder, "to_team_id", Integer.toString(clarification.getSubmitter().getClientNumber()));
        }

        stringBuilder.append(", ");

        if (clarification.isAnswered()) {
            appendPair(stringBuilder, "reply_to_id", Integer.toString(clarification.getNumber())); // this answer is in reply to
        } else {
            appendPairNullValue(stringBuilder, "reply_to_id");
        }
        stringBuilder.append(", ");

        //        problem_id  ID  yes     yes     provided by CCS     identifier of associated problem, null if not associated to a problem
        //        text    string  yes     no  provided by CCS     question or reply text
        //        time    TIME    yes     no  provided by CCS     time of the question/reply
        //        contest_time    RELTIME     yes     no  provided by CCS     contest time of the question/reply 

        appendPair(stringBuilder, "problem_id", Integer.toString(getProblemIndex(contest, clarification.getProblemId())));
        stringBuilder.append(", ");

        // Due to a design mistake in the CCS team the clarification JSON element has either
        // an answer or a question.   There were two mistakes, first that there is on
        // JSoN element clarifications for both the question and the answer, there should
        // be two elements.   The second mistake is that an answer should have both
        // question and answer to avoid a bug by the consumer where they mismatch the
        // question and answer because those would be married on reply_to_id

        if (clarification.isAnswered()) {
            // text is 
        }

        appendPair(stringBuilder, "text", clarification.getQuestion()); // TODO CLICS need to quote this string 
        stringBuilder.append(", ");

        Calendar wallElapsed = calculateElapsedWalltime(contest, clarification.getElapsedMS());

        appendPair(stringBuilder, "start_time", wallElapsed); // absolute time when judgement started ex. 2014-06-25T11:24:03.921+01
        stringBuilder.append(", ");

        appendPair(stringBuilder, "start_contest_time", XMLUtilities.formatSeconds(clarification.getElapsedMS())); // contest relative time when judgement started. ex. 1:24:03.921

        return stringBuilder.toString();
    }
}
