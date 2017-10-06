package edu.csus.ecs.pc2.services.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JSONUtilities {

    /**
     * ISO 8601 Date format for SimpleDateFormat.
     */
    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss z";

    /**
     * New Line (EOLN).
     */
    public static final String NL = System.getProperty("line.separator");

    private SimpleDateFormat iso8601formatter = new SimpleDateFormat(ISO_8601_DATE_FORMAT);

    /**
     * Append to string buffer if not null.
     * @param buffer
     * @param awardJSON
     */
    void appendNotNull(StringBuffer buffer, String string) {
        if (string != null) {
            buffer.append(string);
        }
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

    /**
     * Add JSON pair to stringBuilder.
     */
    protected void appendPair(StringBuilder stringBuilder, String name, boolean booleanValue) {

        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");
        stringBuilder.append(":");

        stringBuilder.append(booleanValue);
    }

    /**
     * Add JSON pair to stringBuilder.
     */

    void appendPair(StringBuilder stringBuilder, String name, long value) {
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");
        stringBuilder.append(":");

        stringBuilder.append(value);
    }

    /**
     * Add JSON pair to stringBuilder.
     */

    void appendPair(StringBuilder stringBuilder, String name, String value) {
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");

        stringBuilder.append(":");

        stringBuilder.append("\"");
        stringBuilder.append(value);
        stringBuilder.append("\"");
    }

    /**
     * Add JSON pair to stringBuilder.
     */

    void appendPair(StringBuilder stringBuilder, String name, Calendar calendar) {

        if (calendar != null) {
            appendPair(stringBuilder, name, iso8601formatter.format(calendar.getTime()));
        } else {
            appendPairNullValue(stringBuilder, name);
        }
    }

    /**
     * Add JSON pair with null value to stringBuilder.
     */
    void appendPairNullValue(StringBuilder stringBuilder, String name) {
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");

        stringBuilder.append(": null");

    }

    /**
     * Return the language index (starting as base one).
     * 
     * @param contest
     * @param elementId
     * @return -1 if language not found or inactive, else 1 or greater rank for language.
     */
    public int getLanguageIndex(IInternalContest contest, ElementId elementId) {
        int idx = 0;
        for (Language language : contest.getLanguages()) {
            if (language.isActive()) {
                if (language.getElementId().equals(elementId)) {
                    return idx + 1;
                }
                idx++;
            }
        }

        return -1;
    }

    /**
     * Return the problem index (starting at/base one)).
     * 
     * @param contest
     * @param elementId
     * @return -1 if problem not found or inactive, else 1 or greater as rank for problem.
     */
    public int getProblemIndex(IInternalContest contest, ElementId elementId) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
            if (problem.getElementId().equals(elementId)) {
                return idx + 1;
            }
            idx++;
        }

        return -1;
    }

    /**
     * Convert HH:MM:SS to ms.
     * 
     * @param freeze
     * @return
     */
    public static long convertToMs(String hhmmss) {
        long seconds = Utilities.convertStringToSeconds(hhmmss);
        return seconds * Constants.MS_PER_SECOND;
    }
    
    

    // TODO old code cull
    //    private Calendar calculateElapsedWalltime(IInternalContest contest, Run run) {
    //        
    //        ContestTime time = contest.getContestTime();
    //        if (time.getElapsedMins() > 0){
    //            
    //        Calendar contestStart = time.getContestStartTime();
    //        
    //        long ms = contestStart.getTimeInMillis();
    //        
    //        ms += run.getElapsedMS(); // add elapsed time
    //        
    //        // create wall time.
    //        Calendar calendar = Calendar.getInstance();
    //        calendar.setTimeInMillis(ms);
    //        return calendar;
    //        
    //        } else {
    //            return null;
    //        }
    //        
    //    }

    

    /**
     * Return wall time for input elapsed time in ms.
     * 
     * Calculates based on elapsed time plus contest start time
     * 
     * @param contest
     * @param elapsedMS - elapsed ms when submission submitted
     * @return wall time for run.
     */
    public Calendar calculateElapsedWalltime(IInternalContest contest, long elapsedMS) {

        ContestTime time = contest.getContestTime();
        if (time.getElapsedMins() > 0) {

            Calendar contestStart = time.getContestStartTime();

            long ms = contestStart.getTimeInMillis();

            ms += elapsedMS; // add elapsed time

            // create wall time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ms);
            return calendar;

        } else {
            return null;
        }

    }

    /**
     * Strip JSON of [{  }].
     * 
     * @param string
     * @return
     */
    public String stripOuterJSON(String string) {
        string = stringOuterChars(string, '[', ']');
        string = stringOuterChars(string, '{', '}');
        return string;
    }

    public String stringOuterChars(String string, char start, char end ) {
        String out = string.trim();
        if (out.charAt(0) == start) {
            out = out.substring(1, out.length());
        }
        if (out.charAt(out.length() - 1) == end) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }

    
    /**
     * returns true if the value is not null and is not the empty string
     * @param value
     * @return true if not empty
     */
    public boolean notEmpty(String value) {
        if (value != null && !value.equals("")) {
            return true;
        }
        return false;
    }
}
