// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.imports.ccs;

/**
 * Scoring parameters for a Test Data group
 *
 * @author John Buck, PC^2 Team, pc2@ecs.csus.edu
 */
public class TestDataGroupScoringInfo {
    public enum AggregationType {
        PASSFAIL,
        SUM,
        MIN
    };
    
    private boolean unbounded = false;
    private int score = 0;
    private AggregationType aggregation = AggregationType.SUM;
    private String [] require_pass = new String[0];

    TestDataGroupScoringInfo() {
        
    }
    
    /**
     * @return the aggregation
     */
    public AggregationType getAggregation() {
        return aggregation;
    }

    /**
     * @param aggregation the aggregation to set
     */
    public void setAggregation(AggregationType aggregation) {
        this.aggregation = aggregation;
    }

    /**
     * @return the unbounded
     */
    public boolean isUnbounded() {
        return unbounded;
    }

    /**
     * @param unbounded the unbounded to set
     */
    public void setUnbounded(boolean unbounded) {
        this.unbounded = unbounded;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the require_pass
     */
    public String [] getRequire_pass() {
        return require_pass;
    }

    /**
     * @param require_pass the require_pass to set
     */
    public void setRequire_pass(String [] require_pass) {
        this.require_pass = require_pass;
    }
    
}
