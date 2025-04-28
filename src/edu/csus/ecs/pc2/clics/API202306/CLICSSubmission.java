// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Contains information about a contestant's submission.
 * This corresponds to the object returned by the CLICS Submissions endpoint.
 *
 * @author John Buck
 *
 */

@JsonFilter("rtFilter")
public class CLICSSubmission {
    // This will force the source file name to be "1.zip", "235.zip", etc.  If
    // you want something like "sub_1.zip", set this to: "sub_"
    private static final String SUBMISSION_FILE_PREFIX = "";

    @JsonProperty
    private String id;

    @JsonProperty
    private String language_id;

    @JsonProperty
    private String problem_id;

    @JsonProperty
    private String team_id;

    @JsonProperty
    private String time;

    @JsonProperty
    private String contest_time;

    @JsonProperty
    private String entry_point;

    @JsonProperty
    private CLICSFileReference [] files;

// Not included since we don't do reaction vids
//  @JsonProperty
//  private CLICSFileReference [] reaction;

    public CLICSSubmission() {
        // For Jackson deserialize
    }

    /**
     * Fill in API submission information properties (for submissions endpoint)
     *
     * @param submission The submission
     */
    public CLICSSubmission(IInternalContest model, Run submission) {
        this.id = IJSONTool.getSubmissionId(submission);
        this.language_id = IJSONTool.getLanguageId(model.getLanguage(submission.getLanguageId()));
        this.problem_id = IJSONTool.getProblemId(model.getProblem(submission.getProblemId()));
        this.team_id = Integer.valueOf(submission.getSubmitter().getClientNumber()).toString();
        this.time = Utilities.getIso8601formatterWithMS().format(submission.getCreateDate());
        this.contest_time = Utilities.formatDuration(submission.getElapsedMS());
        if (submission.getEntryPoint() != null) {
            this.entry_point = new String(submission.getEntryPoint());
        }

        String pathValue = "/contests/" + model.getContestIdentifier() + "/submissions/" + submission.getNumber() + "/files";

        // Submissions object supports exactly 1 element.
        files = new CLICSFileReference[1];
        files[0] = new CLICSFileReference(pathValue, SUBMISSION_FILE_PREFIX + this.id + ".zip", "application/zip");
    }

    /**
     * Get set of properties for which we do not want to serialize into JSON.
     * This is so we don't serialize width/height if they are 0
     *
     * @param exceptProps Set to fill in with property names to omit
     */
    public void getExceptProps(Set<String> exceptProps) {
        if(files != null && files[0] != null) {
            files[0].getExceptProps(exceptProps);
        }
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
            return "Error creating JSON for submission " + e.getMessage();
        }
    }

    /**
     * Create CLICSSubmission object
     *
     * @param json string to deserialize
     * @return new CLICSSubmission object
     */
    public static CLICSSubmission fromJSON(String json) {
        Log log = StaticLog.getLog();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return(mapper.readValue(json, CLICSSubmission.class));
            // deserialize exceptions
        } catch (Exception e) {
            log.log(Log.WARNING, "could not deserialize submission string " + json, e);
        }
        return(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getProblem_id() {
        return problem_id;
    }

    public void setProblem_id(String problem_id) {
        this.problem_id = problem_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContest_time() {
        return contest_time;
    }

    public void setContest_time(String contest_time) {
        this.contest_time = contest_time;
    }

    public String getEntry_point() {
        return entry_point;
    }

    public void setEntry_point(String entry_point) {
        this.entry_point = entry_point;
    }

    public CLICSFileReference [] getFiles() {
        return files;
    }
}
