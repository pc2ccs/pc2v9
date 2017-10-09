package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Problem JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
//TODO CLICS change ProblemJSON to use ObjectMapper
public class ProblemJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Problem problem, int problemNumber) {
        StringBuilder stringBuilder = new StringBuilder();

        appendPair(stringBuilder, "id", problem.getShortName());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "label", problem.getLetter()); // letter
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", problem.getDisplayName());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "ordinal", problemNumber);
        stringBuilder.append(", ");

        String s = problem.getColorRGB();
        if (s != null)
        {
            appendPair(stringBuilder, "rgb", s);
            stringBuilder.append(", ");
        }

        s = problem.getColorName();
        if (s != null)
        {
            appendPair(stringBuilder, "color", s);
            stringBuilder.append(", ");
        }

        appendPair(stringBuilder, "test_data_coun", problem.getNumberTestCases());

        return stringBuilder.toString();
    }

}
