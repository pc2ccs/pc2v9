// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.DateDifferizer;
import edu.csus.ecs.pc2.core.util.DateDifferizer.DateFormat;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains information about a commentary entry.
 * This corresponds to the object returned by the CLICS commentary endpoint.
 *
 * @author John Buck
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonFilter("rtFilter")
public class CLICSCommentary {

    @JsonProperty
    private String id;

    @JsonProperty
    private String time;

    @JsonProperty
    private String contest_time;

    @JsonProperty
    private String entry_point;

    @JsonProperty
    private String message;

    @JsonProperty
    private String [] tags;

    @JsonProperty
    private String source_id;

    @JsonProperty
    private String [] team_ids;

    @JsonProperty
    private String [] problem_ids;

    @JsonProperty
    private String [] submission_ids;

    public CLICSCommentary() {
        // For Jackson deserialize
    }

    /**
     * Fill in API commentary information properties (for commentary endpoint)
     *
     * @param model The contest
     * @param id
     * @param message the comment
     * @param tags
     */
    public CLICSCommentary(IInternalContest model, int id, String message, String [] tags) {
        this.id = Integer.valueOf(id).toString();
        this.message = message;
        this.tags = tags;
        setTimes(model);
    }

    public void setTimes(IInternalContest model) {
        ContestInformation ci = model.getContestInformation();
        ContestTime ct = model.getContestTime();
        if (ct.isContestStarted()) {
            contest_time = ContestTime.formatTimeMS(ct.getElapsedMS());
        } else {
            contest_time = getScheduledTimeClockText(ci);
        }
        time = Utilities.getIso8601formatterWithMS().format(GregorianCalendar.getInstance().getTime());
    }

    /**
     * Get set of properties for which we do not want to serialize into JSON.
     * This is so we don't serialize width/height if they are 0
     *
     * @param exceptProps Set to fill in with property names to omit
     */
    public void getExceptProps(Set<String> exceptProps) {
//        if(files != null && files[0] != null) {
//            files[0].getExceptProps(exceptProps);
//        }
    }

    public String toJSON() {
        Set<String> exceptProps = new HashSet<String>();

        getExceptProps(exceptProps);
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            // for this file, create filter to omit unused properties (height/width in this case)
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
           mapper.setFilters(fp);
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for commentary " + e.getMessage();
        }
    }

    /**
     * Create CLICSCommentary object
     *
     * @param json string to deserialize
     * @return new CLICSSubmission object
     */
    public static CLICSCommentary fromJSON(String json) {
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return(mapper.readValue(json, CLICSCommentary.class));
            // deserialize exceptions
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize commentary string " + json, e);
        }
        return(null);
    }

    private String getScheduledTimeClockText(ContestInformation ci) {
        GregorianCalendar startTime = ci.getScheduledStartTime();
        String text;

        // If we have a start time, figure out how long to start
        if(startTime != null) {
            Date now = GregorianCalendar.getInstance().getTime();
            DateDifferizer differizer = new DateDifferizer(now, startTime.getTime());
            differizer.setFormat(DateFormat.COUNT_DOWN);
            text = differizer.toString();
        } else {
            // no start time, just use beginning of contest.
            text = "00:00:00";
        }
        return text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContest_time() {
        return contest_time;
    }

    public void setContest_time(String contest_time) {
        this.contest_time = contest_time;
    }

    public void setTags(String [] tags) {
        this.tags = tags;
    }

    public String [] getTags() {
        return tags;
    }

    public void setSource_id(String id) {
        source_id = id;
    }

    public String getSource_id() {
        return source_id;
    }
}
