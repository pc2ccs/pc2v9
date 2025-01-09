// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Clarification
 * Contains information about a clarification
 *
 * @author John Buck
 *
 */
public class CLICSClarification {

    @JsonProperty
    private String id;

    @JsonProperty
    private String from_team_id;

    @JsonProperty
    private String to_team_id;

    @JsonProperty
    private String reply_to_id;

    @JsonProperty
    private String problem_id;

    @JsonProperty
    private String text;

    @JsonProperty
    private String time;

    @JsonProperty
    private String contest_time;

    @JsonProperty
    private int number;

    public CLICSClarification() {
        // for jackson deserialize
    }

    public CLICSClarification(IInternalContest model, Clarification clar) {
        this(model, clar, null);
    }

    /**
     * Fills in the clarification properties
     *
     * @param model The contest
     * @param clar The clarification
     * @param clarAns non-null if this is an answer
     */
    public CLICSClarification(IInternalContest model, Clarification clar, ClarificationAnswer clarAns) {

//        // use last answer if we were not handed the answer
//        if(clarAns == null) {
//            if (clar.isAnswered()) {
//                // dump the answer
//                ClarificationAnswer[] clarAnswers = clar.getClarificationAnswers();
//                if(clarAnswers != null && clarAnswers.length > 0) {
//                    clarAns = clarAnswers[clarAnswers.length-1];
//                }
//            }
//        }


        // pc2 specific number (really, ordinal or original question - for announcement's too!)
        number = clar.getNumber();

        // SOMEDAY change id to a original?  WTF does that mean? ordinal would make sense. -- JB
        id = clar.getElementId().toString();
        if (clarAns != null && clarAns.getElementId() != null) {
            id = clarAns.getElementId().toString();
        }
        if (clar.getSubmitter().getClientType().equals(ClientType.Type.TEAM) && clarAns == null) {
            from_team_id = "" + clar.getSubmitter().getClientNumber();
        }
        if (clarAns != null) {
            // the request goes to a team?
            if (!clar.isSendToAll()){
                // The CLICS model does not fit in with the PC2 concept of directed responses to Groups
                // and a list of teams.  As such, we'll always return the team that submitted the request,
                // since they will get the response (and possibly others).
                if(clar.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                    to_team_id = "" + clar.getSubmitter().getClientNumber();
                } else if(clarAns.getAllDestinationsTeam() != null && clarAns.getAllDestinationsTeam().length > 0){
                    // Use first team in the list since we can only specify one team - CLICS does not allow for more than one
                    to_team_id = "" + clarAns.getAllDestinationsTeam()[0].getClientNumber();
                }
            }
            // Announcements do not have a question.
            if(clar.isAnnounced()) {
                reply_to_id = null;
            } else {
                reply_to_id = clar.getElementId().toString();
            }
            text = clarAns.getAnswer();
            time = Utilities.getIso8601formatterWithMS().format(clarAns.getDate());
            contest_time = ContestTime.formatTimeMS(clarAns.getElapsedMS());
        } else {
            // the request goes to a judge not a team, so to_team_id and reply_to_id is null
            // fill in question and time fields
            text = clar.getQuestion();
            time = Utilities.getIso8601formatterWithMS().format(clar.getCreateDate());
            contest_time = ContestTime.formatTimeMS(clar.getElapsedMS());
        }
        // if not a general clar and it's not a special category clar, then we need to supply the problem id.
        if (!clar.getProblemId().equals(model.getGeneralProblem()) && model.getCategory(clar.getProblemId()) == null) {
            problem_id = IJSONTool.getProblemId(model.getProblem(clar.getProblemId()));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom_team_id() {
        return from_team_id;
    }

    public String getTo_team_id() {
        return to_team_id;
    }

    public String getReply_to_id() {
        return reply_to_id;
    }

    public String getProblem_id() {
        return problem_id;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContest_time() {
        return contest_time;
    }

    public void setContest_time(String contest_time) {
        this.contest_time = contest_time;
    }

    public int getNumber() {
        return number;
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for version info " + e.getMessage();
        }
    }

    /**
     * Create CLICSClarification object
     *
     * @param json string to deserialize
     * @return new CLICSClarification object
     */
    public static CLICSClarification fromJSON(String json) {
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return(mapper.readValue(json, CLICSClarification.class));
            // deserialize exceptions
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize clarification string " + json, e);
        }
        return(null);
    }
}
