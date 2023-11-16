// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.JSONTool;
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

    public CLICSClarification(IInternalContest model, Clarification clar) {
        this(model, clar, null);
    }

    public CLICSClarification(IInternalContest model, Clarification clar, ClarificationAnswer clarAns) {
        
        // use last answer if we were not handed the answer
        if(clarAns == null) {
            if (clar.isAnswered()) {
                // dump the answer
                ClarificationAnswer[] clarAnswers = clar.getClarificationAnswers();
                if(clarAnswers != null && clarAnswers.length > 0) {
                    clarAns = clarAnswers[clarAnswers.length-1];
                }
            }            
        }
        ObjectMapper mapper = new ObjectMapper();
        
        // SOMEDAY change id to a original?  WTF does that mean? -- JB
        id = clar.getElementId().toString();
        if (clarAns != null && clarAns.getElementId() != null) {
            id = clarAns.getElementId().toString();
        }
        if (clar.getSubmitter().getClientType().equals(ClientType.Type.TEAM) && clarAns == null) {
            from_team_id = "" + clar.getSubmitter().getClientNumber();
        }
        if (clarAns != null) {
            // the request goes to a team
            if (!clar.isSendToAll() && clar.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                to_team_id = "" + clar.getSubmitter().getClientNumber();
            }
            reply_to_id = clar.getElementId().toString();
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
            problem_id = JSONTool.getProblemId(model.getProblem(clar.getProblemId()));
        }
    }
    
    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for version info " + e.getMessage();
        }
    }
}
