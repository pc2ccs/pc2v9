// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

    public static final String JSON_ANNOTATION_INTERFACE = ".JsonProperty";

    public static ObjectMapper mapper = null;

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
     * Return the language index (starting as base one).
     *
     * @param contest
     * @param elementId
     * @return -1 if language not found or inactive, else 1 or greater rank for language.
     */
    public static int getLanguageIndex(IInternalContest contest, ElementId elementId) {
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
    public static int getProblemIndex(IInternalContest contest, ElementId elementId) {
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


    /**
     * Return wall time for input elapsed time in ms.
     *
     * Calculates based on elapsed time plus contest start time
     *
     * @param contest
     * @param elapsedMS - elapsed ms when submission submitted
     * @return wall time for run.
     */
    public static Calendar calculateElapsedWalltime(IInternalContest contest, long elapsedMS) {

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

    public static String getLanguageIndexString(IInternalContest contest, ElementId elementId) {
        return Integer.toString(getLanguageIndex(contest, elementId));
    }

    /**
     * Returns a Map containing the key/value elements in the specified JSON string.
     * This method uses the Jackson {@link ObjectMapper} to perform the conversion from the JSON
     * string to a Map.  Note that the ObjectMapper recurses for nested JSON elements, returning
     * a appropriate Object in the Map under the corresponding key string.
     *
     * @param jsonString a JSON string to be converted to a Map
     * @return a Map mapping the keys in the JSON string to corresponding values, or null if the input
     *          String is null or if an exception occurs while converting the JSON to a Map.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(String jsonString) {

        if (jsonString == null){
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Get an object mapper.
     *
     * SerializationFeature.WRAP_ROOT_VALUE is false which means that the classname will
     * not preceed the rest of the object values.
     *
     * @return oibject mapper with no FAIL_ON_UNKNOWN_PROPERTIES and no WRAP_ROOT_VALUE
     */
    public final static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            mapper.disableDefaultTyping();

        }
        return mapper;
    }

    /**
     * Pretty print json object.
     *
     * @param JSONObject
     * @return formatted listing for input json object.
     * @throws JsonProcessingException
     */
    public static String prettyPrint(Object JSONObject) throws JsonProcessingException {
        String json = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(JSONObject);
        return json;
    }

    /**
     * Get list of @jsonProperty's for a class.  This list is used to support the Access endpoint.
     * @param c The class to look through for jsonproperty's
     * @return array of Stings where each one is a json property, null if none.
     */
    public static String [] getJsonProperties(Class c) {
        Field [] fields = c.getDeclaredFields();
        Field f;
        int i, n = fields.length;
        int ia, na;
        Annotation [] alist = null;
        Annotation a;
        ArrayList<String> aResult = new ArrayList<String>();
        for(i = 0; i < n; i++) {
            f = fields[i];
            alist = f.getAnnotations();
            na = alist.length;
            for(ia = 0; ia < na; ia++) {
                a = alist[ia];
                Class<? extends Annotation> ca = a.annotationType();
                String cn = ca.getName();
                // if the property was annotated with the jsonproperty interface from Jackson, we want it.
                if(cn.endsWith(JSON_ANNOTATION_INTERFACE)) {
                    String pn = f.getName();
                    aResult.add(pn);
                    break;
                }
            }
        }
        if(aResult.size() == 0) {
            return(null);
        }
        return(aResult.toArray(new String [0]));
    }

}
