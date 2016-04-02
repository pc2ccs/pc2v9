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
            
            //contruct the JSON data for the current problem, using the format defined in the 2106 JSON Scoreboard spec, which is as follows:
            /*
             * {"id":1,"label":"A","short_name":"asteroids","name":"Asteroid Rangers","rgb":"#00f","color":"blue"}
             */
            String problemLetter = problem.getLetter();
            if (problemLetter==null || problemLetter.trim().equals("")) {
                problemLetter = "null";
            }
            String problemShortName = problem.getShortName();
            if (problemShortName==null || problemShortName.trim().equals("")) {
                problemShortName = "null";
            }
            String problemName = problem.getDisplayName();
            if (problemName==null || problemName.trim().equals("")) {
                problemName = "null";
            }
            String problemRGB = problem.getColorRGB();
            if (problemRGB==null || problemRGB.trim().equals("")) {
                problemRGB = "null";
            }
            String problemColorName = problem.getColorName();
            if (problemColorName==null || problemColorName.trim().equals("")) {
                problemColorName = "null";
            }
            //add the current problem data to the output buffer
            buffer.append(pair("id", rowCount) + "," + pair("label", problemLetter) + "," + pair("short_name", problemShortName) + "," 
                        + pair("name", problemName) + "," + pair("rgb", problemRGB) + "," + pair("color", problemColorName));
            // close the entry for the current problem
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
