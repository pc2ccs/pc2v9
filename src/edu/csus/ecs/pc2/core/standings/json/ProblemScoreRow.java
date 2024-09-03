// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */

//The annotation one after @JsonSerialize tells the Jackson ObjectMapper not to include "type information" when it serializes
//objects of this class (the default is to include the fully-qualified class name as an additional JSON element).
//(Note that exclusion of such type information means that generated JSON cannot subsequently be accurately DESERIALIZED...)
@JsonSerialize(using = ProblemScoreRowSerializer.class)
@JsonTypeInfo(use=Id.NONE)
public class ProblemScoreRow {
    
//    
//        "solved": true, 
//        "num_judged": 1, 
//        "time": 12, 
//        "problem_id": "candlebox", 
//        "num_pending": 0
    
     //    "problems":[{"problem_id":"candlebox","num_judged":1,"num_pending":0,"solved":true,"time":12},


    @JsonProperty
    private boolean solved;
    @JsonProperty
    private int num_judged;
    @JsonProperty
    private int time;
    @JsonProperty
    private String problem_id;
    @JsonProperty
    private int num_pending;
    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    public int getNum_judged() {
        return num_judged;
    }
    public void setNum_judged(int num_judged) {
        this.num_judged = num_judged;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public String getProblem_id() {
        return problem_id;
    }
    public void setProblem_id(String problem_id) {
        this.problem_id = problem_id;
    }
    public int getNum_pending() {
        return num_pending;
    }
    public void setNum_pending(int num_pending) {
        this.num_pending = num_pending;
    }
    
}

/**
 * This custom JsonSerialer for ProblemScoreRow class adds time field to Json based on the condition of whether the problem is marked as solved or not. 
 * If it is the boolean for solved is marked true Json will have time field. If not Json will not have the time field.
 *
 */
class ProblemScoreRowSerializer extends JsonSerializer<ProblemScoreRow> {
    @Override
    public void serialize(ProblemScoreRow problemScoreRow, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeBooleanField("solved", problemScoreRow.isSolved());
        gen.writeNumberField("num_judged", problemScoreRow.getNum_judged());

        if (problemScoreRow.isSolved()) {
            gen.writeNumberField("time", problemScoreRow.getTime());
        } 
        gen.writeStringField("problem_id", problemScoreRow.getProblem_id());
        gen.writeNumberField("num_pending", problemScoreRow.getNum_pending());

        gen.writeEndObject();
    }
}

