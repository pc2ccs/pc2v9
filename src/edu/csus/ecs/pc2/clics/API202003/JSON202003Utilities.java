// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202003;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 *
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JSON202003Utilities extends JSONUtilities {
    public static final String TEAM_MEMBERS_KEY = "team-members";

    public static final String CLARIFICATIONS_KEY = "clarifications";

    public static final String GROUPS_KEY = "groups";

    public static final String JUDGEMENT_TYPE_KEY = "judgement-types";

    public static final String TEAM_KEY = "teams";

    public static final String SUBMISSION_KEY = "submissions";

    public static final String RUN_KEY = "runs";

    public static final String CONTEST_KEY = "contests";

    public static final String STATE_KEY = "state";

    public static final String LANGUAGE_KEY = "languages";

    public static final String PROBLEM_KEY = "problems";

    public static final String JUDGEMENT_KEY = "judgements";

    public static final String AWARD_KEY = "awards";

    public static final String ORGANIZATION_KEY = "organizations";

    public static final String JSON_ANNOTATION_INTERFACE = ".JsonProperty";

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
     * Add an event prefix to the buffer.
     *
     * Adds event, (event) id, and data keyword to string.
     *
     * @param stringBuilder
     * @param eventType
     * @param op
     */
    public String getJSONEvent(String eventName, long eventSequence, EventFeedOperation operation, String data) {
        StringBuilder stringBuilder = new StringBuilder();
        appendJSONEvent(stringBuilder, eventName, eventSequence, operation, data);
        return stringBuilder.toString();
    }

    public void appendJSONEvent(StringBuilder stringBuilder, String eventName, long eventSequence, EventFeedOperation operation, String data) {

        // {"type": "<event type>", "id": "<id>", "op": "<type of operation>", "data": <JSON data for element> }

        stringBuilder.append("{");
        appendPair(stringBuilder, "type", eventName);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", EventFeedJSON.getEventId(eventSequence));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "op", operation.toString());
        stringBuilder.append(", ");

        stringBuilder.append("\"data\": ");

        stringBuilder.append(data);

        stringBuilder.append("}");

    }

}
