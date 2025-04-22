// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
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
    private String [] to_team_ids;

    @JsonProperty
    private String [] to_group_ids;

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
     * In the CLICS API questions and answers are two separate things completely.
     * The only tenuous connection between an answer and question is that the
     * answer has a property called "reply_to_id".
     * This CLICSClarification class is used for both questions and answers.
     * If the clarAns argument is null, it means it's a question and "clar"
     * contains that question (see the comment for @param clarAns).  To complicate matters,
     * a Clarification may also be a annoucement, in which case, there is no separate answer that
     * would result in a clarification change event, rather the
     * announcement text is included when the clarification is created.
     *
     * So, ClarificationService creates a separate CLICSClarification object
     * for the question (clarAns == null) and the answer to that question (clarAns != null).
     *
     * @param model The contest
     * @param clar The clarification
     * @param clarAns non-null if this is an answer
     */
    public CLICSClarification(IInternalContest model, Clarification clar, ClarificationAnswer clarAns) {

        // pc2 specific number (really, ordinal or original question - for announcement's too!)
        number = clar.getNumber();

        // SOMEDAY change id to a ordinal?
        id = clar.getElementId().toString();
        if (clarAns != null && clarAns.getElementId() != null) {
            id = clarAns.getElementId().toString();
        }
        if (clar.getSubmitter().getClientType().equals(ClientType.Type.TEAM) && clarAns == null) {
            from_team_id = "" + clar.getSubmitter().getClientNumber();
        }
        // Announcements do not have a reply_to_id or a question, but they do have an answer
        if(clar.isAnnounced()) {
            reply_to_id = null;
            ClarificationAnswer[] clarAnswers = clar.getClarificationAnswers();
            if(clarAnswers != null && clarAnswers.length > 0) {
                clarAns = clarAnswers[clarAnswers.length - 1];
            }
        } else {
            reply_to_id = clar.getElementId().toString();
        }
        if (clarAns != null) {
            // does the answer go to a team (as opposed to everyone)?
            if (!clarAns.isSendToAll()){
                // The CLICS 2023-06 model does not fit in with the PC2 concept of directed responses to Groups
                // and a list of teams.  As such, we'll always return the team that submitted the request, in to_team_id
                // since they will get the response (and possibly others).
                // We also fill in to_team_ids and to_group_ids  because a client should check these first and if non-null
                // use the arrays and ignore to_team_id.
                if(clar.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                    to_team_id = "" + clar.getSubmitter().getClientNumber();
                } else {
                    ClientId [] destTeams = clarAns.getAllDestinationsTeam();
                    ElementId [] destGroups = clarAns.getAllDestinationsGroup();

                    // first let's look at destination teams, if any
                    if(destTeams != null && destTeams.length > 0){
                        // Use first team in the list for backward compatibilty of only supporting a single dest team
                        // CLICS does not (yet) allow for more than one
                        to_team_id = "" + destTeams[0].getClientNumber();
                        // Implement PC2 extension of proposed CLICS change - array of teams.
                        ArrayList<String> teamIdList = new ArrayList<String>();
                        for(ClientId destClient : destTeams) {
                            teamIdList.add("" + destClient.getClientNumber());
                        }
                        to_team_ids = teamIdList.toArray(new String[teamIdList.size()]);
                    }
                    // next, let's look at destination groups, if any
                    // it should be noted that if to_team_id is null at this point, it will remain null, implying
                    // the response to the clar was sent to all teams.  It's a reasonable compromise since the 2023-06 spec
                    // does not have a notion of group replies.
                    if(destGroups != null && destGroups.length > 0) {
                        ArrayList<String> groupIdList = new ArrayList<String>();

                        for(ElementId groupEle : destGroups) {
                            Group group = model.getGroup(groupEle);
                            if(group != null) {
                                groupIdList.add("" + group.getGroupId());
                            }
                            to_group_ids = groupIdList.toArray(new String[groupIdList.size()]);
                        }
                    }
                }
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
        if (!clar.getProblemId().equals(model.getGeneralProblem().getElementId()) && model.getCategory(clar.getProblemId()) == null) {
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
            return "Error creating JSON for clarification " + e.getMessage();
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
