// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains an extension to the CLICS spec. consisting of ICPC specific results.
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICS_Ex_Results {

    @JsonProperty
    private String time;

    @JsonProperty
    private String contest_time;

    @JsonProperty
    private CLICSContestState state;

    @JsonProperty
    private CLICS_Ex_ResultsRow [] rows;

    /**
     * Fill in the results information
     *
     */
    public CLICS_Ex_Results(IInternalContest model, String [] tsvResults)  throws IllegalContestState, JAXBException, IOException {

        // Given an array of strings that looks like this (\t = tab):
        //    results\t1
        //    965431\t1\tGold Medal\t10\t1087\t263
        //    965415\t2\tSilver Medal\t9\t911\t282
        //    965351\t3\tSilver Medal\t9\t1024\t255
        //    965444\t4\tBronze Medal\t8\t736\t217
        //    965438\t5\tBronze Medal\t8\t766\t233
        //    965407\t6\tBronze Medal\t8\t799\t214
        //    965445\t7\tRanked\t8\t887\t262
        //    965423\t8\tRanked\t8\t1099\t295
        //
        // This is what we want to return:
        //        {
        //            "time": "2014-06-25T14:13:07.832+01",
        //            "contest_time": "4:13:07.832",
        //            "state": {
        //              "started": "2014-06-25T10:00:00+01",
        //              "ended": null,
        //              "frozen": "2014-06-25T14:00:00+01",
        //              "thawed": null,
        //              "finalized": null,
        //              "end_of_updates": null
        //            },
        //            "rows": [
        //                  { "rank":1,"icpc_id":"965431","citation":"Gold Medal","num_solved":10,"total_time":1087,"last_solved":263 },
        //                  { "rank":2,"icpc_id":"965415","citation":"Silver Medal","num_solved":9,"total_time":911,"last_solved":282 },
        //                  { "rank":3,"icpc_id":"965351","citation":"Silver Medal","num_solved":9,"total_time":1024,"last_solved":255 },
        //                  { "rank":4,"icpc_id":"965444","citation":"Bronze Medal","num_solved":8,"total_time":736,"last_solved":217 },
        //                  { "rank":5,"icpc_id":"965438","citation":"Bronze Medal","num_solved":8,"total_time":766,"last_solved":233 },
        //                  { "rank":6,"icpc_id":"965407","citation":"Bronze Medal","num_solved":8,"total_time":799,"last_solved":214 },
        //                  { "rank":7,"icpc_id":"965445","citation":"Ranked","num_solved":8,"total_time":887,"last_solved":262 },
        //                  { "rank":8,"icpc_id":"965423","citation":"Ranked","num_solved":8,"total_time":1099,"last_solved":295 }
        //            ]
        //          }
        time = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT);
        contest_time = model.getContestTime().getElapsedTimeStr();
        state = new CLICSContestState(model, null);

        if(tsvResults != null) {
            ArrayList<CLICS_Ex_ResultsRow>rowsArray = new ArrayList<CLICS_Ex_ResultsRow>();

            for (String result : tsvResults) {
                String[] resultFields;
                try {
                    resultFields = TabSeparatedValueParser.parseLine(result);
                    if(resultFields.length == CLICS_Ex_ResultsRow.NUM_RESULTS_TSV_FIELDS) {
                        rowsArray.add(new CLICS_Ex_ResultsRow(resultFields));
                    }
                } catch (Exception e) {
                    // We don't care if a line is bad, in fact, looking at TabSeparatedValueParser,
                    // it seems there is a question of whether or not an exception is ever thrown! jb-4/25
                }
            }
            rows = rowsArray.toArray(new CLICS_Ex_ResultsRow[0]);
        } else {
            rows = new CLICS_Ex_ResultsRow[0];
        }
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for CLICS Ex results info " + e.getMessage();
        }
    }
}
