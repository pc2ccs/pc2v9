package edu.csus.ecs.pc2.services.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Clarification JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ClarificationJSON extends JSONUtilities {
    
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode element = mapper.createObjectNode();
    private ArrayNode childNode = mapper.createArrayNode();

    public String createJSON(IInternalContest contest, Clarification clarification) {

        // get last answer
        ClarificationAnswer clarificationAnswer = null;
        ClarificationAnswer[] answers = null;
        answers = clarification.getClarificationAnswers();
        if (clarification.isAnswered()) {
            clarificationAnswer = answers[answers.length - 1];
        }
        
      //        Id   ID  yes     no  provided by CCS     identifier of the clarification
      //        from_team_id    ID  yes     yes     provided by CCS     identifier of team sending this clarification request, null if a clarification sent by jury
      //        to_team_id  ID  yes     yes     provided by CCS     identifier of the team receiving this reply, null if a reply to all teams or a request sent by a team
      //        reply_to_id     ID  yes     yes     provided by CCS     identifier of clarification this is in response to, otherwise null

//        String id = clarification.getElementId().toString();
//        if (clarificationAnswer != null) {
//            id = clarificationAnswer.getElementId().toString();
//        }
//        element.put("id", id);
        
        element.put("id", Integer.toString(clarification.getNumber()));
        
        if (clarificationAnswer == null &&ClientType.Type.TEAM.equals(clarification.getSubmitter().getClientType())) {
            element.put("from_team_id", new Integer(clarification.getSubmitter().getClientNumber()).toString());
        } else {
            element.set("from_team_id", null);
        }
        
        if (clarificationAnswer == null) {
            // the request goes to a judge not a team
            element.set("to_team_id", null);
            element.set("reply_to_id", null);
        } else {
            if (clarification.isSendToAll() || !ClientType.Type.TEAM.equals(clarification.getSubmitter().getClientType())) {
                element.set("to_team_id", null);
            } else {
                element.put("to_team_id", new Integer(clarification.getSubmitter().getClientNumber()).toString());
            }
            element.put("reply_to_id", clarification.getElementId().toString());
        }
        
        //        problem_id  ID  yes     yes     provided by CCS     identifier of associated problem, null if not associated to a problem
        //        text    string  yes     no  provided by CCS     question or reply text
        //        time    TIME    yes     no  provided by CCS     time of the question/reply
        //        contest_time    RELTIME     yes     no  provided by CCS     contest time of the question/reply 

        if (contest.getGeneralProblem().getElementId().equals(clarification.getProblemId()) || contest.getCategory(clarification.getProblemId()) != null) {
            element.set("problem_id", null);
        } else {
            element.put("problem_id", Integer.toString(getProblemIndex(contest, clarification.getProblemId())));
        }

        if (clarificationAnswer != null) {
            element.put("text", clarificationAnswer.getAnswer());
            String time = Utilities.getIso8601formatterWithMS().format(clarificationAnswer.getDate());
            element.put("time", time);
            element.put("contest_time", ContestTime.formatTimeMS(clarificationAnswer.getElapsedMS()));
        } else {
            element.put("text", clarification.getQuestion());
            String time = Utilities.getIso8601formatterWithMS().format(clarification.getCreateDate());
            element.put("time", time);
            element.put("contest_time", ContestTime.formatTimeMS(clarification.getElapsedMS()));
        }
        childNode.add(element);
        
        return stripOuterJSON(childNode.toString());

        // TODO remove old code
        
//        StringBuilder stringBuilder = new StringBuilder();
//
//        //        Id   ID  yes     no  provided by CCS     identifier of the clarification
//        //        from_team_id    ID  yes     yes     provided by CCS     identifier of team sending this clarification request, null if a clarification sent by jury
//        //        to_team_id  ID  yes     yes     provided by CCS     identifier of the team receiving this reply, null if a reply to all teams or a request sent by a team
//        //        reply_to_id     ID  yes     yes     provided by CCS     identifier of clarification this is in response to, otherwise null
//
//        appendPair(stringBuilder, "id", Integer.toString(clarification.getNumber()));
//        stringBuilder.append(", ");
//
//        appendPair(stringBuilder, "from_team_id", Integer.toString(clarification.getSubmitter().getClientNumber()));
//        stringBuilder.append(", ");
//
//        if (clarification.isSendToAll()) {
//            appendPairNullValue(stringBuilder, "to_team_id");
//        } else {
//            appendPair(stringBuilder, "to_team_id", Integer.toString(clarification.getSubmitter().getClientNumber()));
//        }
//
//        stringBuilder.append(", ");
//
//        if (clarification.isAnswered()) {
//            appendPair(stringBuilder, "reply_to_id", Integer.toString(clarification.getNumber())); // this answer is in reply to
//        } else {
//            appendPairNullValue(stringBuilder, "reply_to_id");
//        }
//        stringBuilder.append(", ");
//
//        //        problem_id  ID  yes     yes     provided by CCS     identifier of associated problem, null if not associated to a problem
//        //        text    string  yes     no  provided by CCS     question or reply text
//        //        time    TIME    yes     no  provided by CCS     time of the question/reply
//        //        contest_time    RELTIME     yes     no  provided by CCS     contest time of the question/reply 
//
//        appendPair(stringBuilder, "problem_id", Integer.toString(getProblemIndex(contest, clarification.getProblemId())));
//        stringBuilder.append(", ");
//
//        // Due to a design mistake in the CCS team the clarification JSON element has either
//        // an answer or a question.   There were two mistakes, first that there is on
//        // JSoN element clarifications for both the question and the answer, there should
//        // be two elements.   The second mistake is that an answer should have both
//        // question and answer to avoid a bug by the consumer where they mismatch the
//        // question and answer because those would be married on reply_to_id
//
//        if (clarification.isAnswered()) {
//            // text is 
//        }
//
//        appendPair(stringBuilder, "text", clarification.getQuestion()); // TODO CLICS need to quote this string 
//        stringBuilder.append(", ");
//
//        Calendar wallElapsed = calculateElapsedWalltime(contest, clarification.getElapsedMS());
//
//        appendPair(stringBuilder, "start_time", wallElapsed); // absolute time when judgement started ex. 2014-06-25T11:24:03.921+01
//        stringBuilder.append(", ");
//
//        appendPair(stringBuilder, "start_contest_time", XMLUtilities.formatSeconds(clarification.getElapsedMS())); // contest relative time when judgement started. ex. 1:24:03.921
//
//        return stringBuilder.toString();
    }
}