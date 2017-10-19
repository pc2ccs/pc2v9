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
    private ObjectNode element = null;
    private ArrayNode childNode = null;

    public String createJSON(IInternalContest contest, Clarification clarification) {

        element = mapper.createObjectNode();
        childNode = mapper.createArrayNode();
        
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
            element.put("reply_to_id", clarification.getNumber());
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
    }
}
