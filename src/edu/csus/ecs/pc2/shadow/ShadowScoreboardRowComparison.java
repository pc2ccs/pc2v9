package edu.csus.ecs.pc2.shadow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;

/**
 * This class encapsulates the "matching" status between two {@link TeamScoreRow} objects.
 * It provides a method {@link #isMatch()} indicating whether the two contained TeamScoreRows
 * match.  
 * 
 * Note that the definition of "match" is determined by first requiring that both contained
 * TeamScoreRows be non-null, and then by the value returned by {@link TeamScoreRow#matches(TeamScoreRow)}.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class ShadowScoreboardRowComparison {
    
    private TeamScoreRow sb1Row ;
    private TeamScoreRow sb2Row ;
    private boolean match ;
    
    
    public TeamScoreRow getSb1Row() {
        return sb1Row;
    }
    
    public void setSb1Row(TeamScoreRow sb1Row) {
        this.sb1Row = sb1Row;
        updateMatch();
    }
    
    public TeamScoreRow getSb2Row() {
        return sb2Row;
    }
    
    public void setSb2Row(TeamScoreRow sb2Row) {
        this.sb2Row = sb2Row;
        updateMatch();
    }
    
    public boolean isMatch() {
        return match;
    }
    
    /**
     * Updates the status of the "match" variable in this ScoreboardRowComparison object indicating whether 
     * the two scoreboard rows in this ScoreboardRowComparison object match each other. 
     * 
     * Note that the definition of mathching is that both scoreboard row objects are non-null
     * and then that {@link TeamScoreRow#matches(TeamScoreRow)} is true.
     */
    private void updateMatch() {
        if (sb1Row!=null && sb2Row!=null) {
            this.match = sb1Row.matches(sb2Row);
        } else {
            //one or both rows are null; define that as non-matching
            this.match = false;
        }
    }
    
    /**
     * Returns a JSON string representation of this ShadowScoreboardRowComparison object.
     */
    @Override
    public String toString() {
        
        ObjectMapper mapper = new ObjectMapper();
        
        String retStr = "";
        try {
            retStr = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            // TODO pass a Log object (or a Controller which provides access to a Log) into this class
            // so we can log exceptions.
            e.printStackTrace();
        }
        
        return retStr ;
        
    }
    
}
