package edu.csus.ecs.pc2.shadow;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.standings.json.StandingScore;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;

/**
 * This class encapsulates the "matching" status between two {@link TeamScoreRow} objects.
 * It provides a method {@link #isMatch()} indicating whether the two contained TeamScoreRows
 * match.  
 * 
 * The class also supports obtaining a List of the fields which do not match (see {@link #getMismatchedFields(TeamScoreRow, TeamScoreRow)}.
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
    private ArrayList<Integer> mismatchedFieldList = new ArrayList<Integer>();
    
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
     * Note that the definition of matching is that both scoreboard row objects are non-null
     * and then that {@link TeamScoreRow#matches(TeamScoreRow)} is true.
     */
    private void updateMatch() {
        if (sb1Row!=null && sb2Row!=null) {
            this.match = sb1Row.matches(sb2Row);
            setMismatchedFieldList(getMismatchedFields(sb1Row, sb2Row));
        } else {
            //one or both rows are null; define that as non-matching
            this.match = false;
        }
    }
    
    /**
     * Returns a List of Integers giving the ordinal numbers of the fields which do not match between the two specified TeamScoreRows.
     * 
     * @param sb1Row the first TeamScoreRow to be compared.
     * @param sb2Row the second TeamScoreRow to be compared.
     * 
     * @return an ArrayList containing the ordinal numbers of any fields which do not match between the specified {@link TeamScoreRow}s.
     *          If either of the supplied TeamScoreRows is null, an empty (but not null) List is returned.
     */
    private ArrayList<Integer> getMismatchedFields(TeamScoreRow sb1Row, TeamScoreRow sb2Row) {
        
        //start with an empty list (no mismatched fields)
        ArrayList<Integer> mismatchedFields = new ArrayList<Integer>();

        if (sb2Row!=null && sb2Row!=null) {
            
            if (sb1Row.getRank() != sb2Row.getRank()) {
                mismatchedFields.add(TeamScoreRow.TeamScoreRowFields.RANK.ordinal());
            }
            if (!(sb1Row.getTeamName().contentEquals(sb2Row.getTeamName()))) {
                mismatchedFields.add(TeamScoreRow.TeamScoreRowFields.NAME.ordinal());
            }
            StandingScore sb1RowScore = sb1Row.getScore();
            StandingScore sb2RowScore = sb2Row.getScore();
            if (sb1RowScore.getNum_solved() != sb2RowScore.getNum_solved()) {
                mismatchedFields.add(TeamScoreRow.TeamScoreRowFields.SOLVED.ordinal());
            }
            if (sb1RowScore.getTotal_time() != sb2RowScore.getTotal_time()) {
                mismatchedFields.add(TeamScoreRow.TeamScoreRowFields.TIME.ordinal());
            } 
        }
        
        return mismatchedFields;

    }

    /**
     * Returns a List of the fields in this ShadowScoreboardRowComparison which do not match.
     * The returned list may be empty, meaning either there are no mismatched fields or else
     * one or the other (or both) of the {@link TeamScoreRow}s in this {@link ShadowScoreboardRowComparison}
     * are null, but the returned list will never be null.
     * 
     * @return an ArrayList<Integer> of the ordinal numbers of mismatched fields between the two {@link TeamScoreRow}s in this object.
     */
    public ArrayList<Integer> getMismatchedFieldList() {
        return mismatchedFieldList;
    }

    private void setMismatchedFieldList(ArrayList<Integer> mismatchedFieldList) {
        this.mismatchedFieldList = mismatchedFieldList;
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
