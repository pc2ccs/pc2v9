package edu.csus.ecs.pc2.exports.ccs;

import java.util.List;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Problem information in CLI 2016 JSON format.
 * 
 * @author pc2@ecs.csus.edu
 */

public class ProblemsJSON {

    /**
     * Returns a JSON string describing the current contest problems in the format defined by the 2016 CLI JSON Scoreboard.
     * 
     * @param contest - the current contest
     * @return a JSON string giving contest problems in 2016 format
     * @throws IllegalContestState
     */
    public String createJSON(IInternalContest contest) throws IllegalContestState {

        if (contest == null) {
            return "[]";
        }

        Problem[] problems = contest.getProblems();
        if (problems.length == 0) {
            return "[]";
        }
        StringBuffer buffer = new StringBuffer();

        int rowCount = 1;
        for (Problem problem : problems) {
            /* add comma between rows */
            if (rowCount != 1) {
                buffer.append(',');
            }
            // start problem entry
            buffer.append('{');
            /*
             * {"id":1,"label":"A","short_name":"asteroids","name":"Asteroid Rangers","rgb":"#00f","color":"blue"}
             */
            buffer.append(pair("id", rowCount) + "," + pair("label", problem.getLetter()) + "," + pair("short_name", problem.getShortName()) + "," + pair("name", problem.getDisplayName()) + ","
                    + pair("rgb", problem.getColorRGB()) + "," + pair("color", problem.getColorName()));
            // close the entry for the current problem entry
            buffer.append('}');
            rowCount++;
        }

        // return the collected standings as elements of a JSON array
        return "[" + buffer.toString() + "]";
    }

    public static String join(String delimit, List<String> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    /*
     * these should be a utility class
     */
    private String pair(String name, long value) {
        return "\"" + name + "\":" + value;
    }

    private String pair(String name, String value) {
        return "\"" + name + "\":\"" + value + "\"";
    }
}
