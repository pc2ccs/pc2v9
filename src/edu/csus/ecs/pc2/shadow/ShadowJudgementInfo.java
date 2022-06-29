// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.shadow;

import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * This class encapsulates the Shadow Judgement Information for a single submission.
 * It contains a submissionID, the TeamID, ProblemID, and LanguageID for the submission,
 * and a {@link ShadowJudgementPair} containing the Shadow and Remote CCS judgements for
 * the submission.
 * 
 * @author John Clevenger, PC2 Development Team, pc2@ecs.csus.edu
 *
 */
public class ShadowJudgementInfo {

    private String submissionID ;
    private String teamID;
    private String problemID;
    private String languageID;
    private ClientId judgerID;
    private ShadowJudgementPair shadowJudgementPair ;
    
    public ShadowJudgementInfo (String submissionID, String teamID, String problemID, String languageID, 
                                ClientId judgerID, ShadowJudgementPair judgementPair) {
        this.submissionID = submissionID;
        this.teamID = teamID;
        this.problemID = problemID;
        this.languageID = languageID;
        this.judgerID = judgerID;
        this.shadowJudgementPair = judgementPair;
        
    }

    public String getSubmissionID() {
        return submissionID;
    }

    public String getTeamID() {
        return teamID;
    }

    public String getProblemID() {
        return problemID;
    }

    public String getLanguageID() {
        return languageID;
    }

    public ClientId getJudgerID() {
        return judgerID;
    }

    public ShadowJudgementPair getShadowJudgementPair() {
        return shadowJudgementPair;
    }
}
