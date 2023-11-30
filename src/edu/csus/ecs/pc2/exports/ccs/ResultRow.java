// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.exports.ccs;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;

public class ResultRow {

    int cmsId;

    int rank;

    String award;

    int numberSolved;

    int totalTime;

    int lastSubmissinTime;

//    results 1
//    825274  6   Bronze Medal    9   1336    277
//    825149  5   Bronze Medal    9   1232    272
//    825070  4   Bronze Medal    9   1045    263
//    825118  3   Silver Medal    9   1036    251

//    
//    825061      Honorable   3   823 297
//    825106      Honorable   3   451 162
//    825117      Honorable   5   1133    272
//    825155      Honorable   4   880 290
//    825276      Honorable   4   704 219

    public ResultRow(String tsvLine) {

        String[] fields = tsvLine.split(Constants.TAB);

        if (fields.length < 6) {
            throw new IllegalArgumentException("Too few fields " + fields.length + " " + tsvLine);
        }
        
        cmsId = Integer.parseInt(fields[0]);
        rank = Utilities.nullSafeToInt(fields[1], -1);
        award = fields[2];
        numberSolved = Utilities.nullSafeToInt(fields[3], -1);
        totalTime = Utilities.nullSafeToInt(fields[4], -1);
        lastSubmissinTime = Utilities.nullSafeToInt(fields[5], -1);

    }

    public int getCmsId() {
        return cmsId;
    }
    
    public int getRank() {
        return rank;
    }

    public String getAward() {
        return award;
    }

    public int getNumberSolved() {
        return numberSolved;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getLastSubmissinTime() {
        return lastSubmissinTime;
    }
    
    

}
