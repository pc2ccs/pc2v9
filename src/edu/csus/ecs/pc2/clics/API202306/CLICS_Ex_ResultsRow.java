// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains information about a single entry (row) in the extended CLICS results.
 *
 * @author John Buck
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICS_Ex_ResultsRow {

    // eg. "965415\t2\2Silver Medal\29\2911\t282"
    public static final int NUM_RESULTS_TSV_FIELDS = 6;
    private static final int ICPC_ID_FIELD = 0;
    private static final int RANK_FIELD = 1;
    private static final int CITATION_FIELD = 2;
    private static final int NUM_SOLVED_FIELD = 3;
    private static final int TOTAL_TIME_FIELD = 4;
    private static final int LAST_SOLVED_FIELD = 5;

    @JsonProperty
    private int rank;

    @JsonProperty
    private String icpc_id;

    @JsonProperty
    private String citation;

    @JsonProperty
    private int num_solved;

    @JsonProperty
    private int total_time;

    @JsonProperty
    private int last_solved;

    /**
     * Fill in results row information properties (for results endpoint)
     *
     * @param resultTSVLine TSV line to assign to object
     */
    public CLICS_Ex_ResultsRow(String [] resultFields) {
        String rankField = resultFields[RANK_FIELD];
        // In the case of Honorable mentions, there is no rank (empty field)
        if(rankField.isEmpty()) {
            rank = -1;
        } else {
            rank = Integer.valueOf(rankField);
        }
        icpc_id = resultFields[ICPC_ID_FIELD];
        citation = resultFields[CITATION_FIELD];
        num_solved = Integer.valueOf(resultFields[NUM_SOLVED_FIELD]);
        total_time = Integer.valueOf(resultFields[TOTAL_TIME_FIELD]);
        last_solved = Integer.valueOf(resultFields[LAST_SOLVED_FIELD]);
    }
}
