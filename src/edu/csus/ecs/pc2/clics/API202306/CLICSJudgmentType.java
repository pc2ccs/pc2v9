// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS judgement-type
 * Contains information about a judgement-type
 * 
 * @author John Buck
 *
 */
public class CLICSJudgmentType {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private boolean penalty;

    @JsonProperty
    private boolean solved;

    /**
     * Fill in properties for a judgement-type, eg. "TLE", "RTE", etc.
     * @param model The contest
     * @param judgment The judgment type
     */
    public CLICSJudgmentType(IInternalContest model, Judgement judgment) {
 
        name = judgment.getDisplayName();
        solved = false;
        penalty = true;
        id = judgment.getAcronym();
        
        // TODO: absolutely horrendous.  this has got to go and be done a better way -- JB
        if (name.equalsIgnoreCase("yes") || name.equalsIgnoreCase("accepted") || judgment.getAcronym().equalsIgnoreCase("ac")) {
            name = "Accepted";
            solved = true;
            penalty = false;
        } else {
            name = name.substring(5, name.length());
            Properties scoringProperties = model.getContestInformation().getScoringProperties();
            // TODO: omg - this too is horrendous.  How did this past muster? It's got to go. -- JB
            if (judgment.getAcronym().equalsIgnoreCase("ce") || name.toLowerCase().contains("compilation error")
                    || name.toLowerCase().contains("compile error")) {
                Object result = scoringProperties.getProperty(DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR, "0");
                if (result.equals("0")) {
                    penalty = false;
                }
            }
            // TODO: oh please, not more of this nonsense -- JB
            if (judgment.getAcronym().equalsIgnoreCase("sv") || name.toLowerCase().contains("security violation")) {
                String result = scoringProperties.getProperty(DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION, "0");
                if (result.equals("0")) {
                    penalty = false;
                }
            }
        }
    }
    
    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for judgement-type info " + e.getMessage();
        }
    }
}
