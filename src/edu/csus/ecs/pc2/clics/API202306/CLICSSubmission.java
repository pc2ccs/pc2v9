// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.util.IJSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Submission from a contestant
 * Contains information about a contestant's submission
 *
 * @author John Buck
 *
 */

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

    /**
     * Fill in API problem score information properties (for scoreboard endpoint)
     *
     * @param probEleToShort hashmap for mapping problem elementid to shortname
     * @param versionInfo
     */
    public CLICSSubmission(IInternalContest model, Run submission) {
        this.id = IJSONTool.getSubmissionId(submission);
        this.language_id = IJSONTool.getLanguageId(model.getLanguage(submission.getLanguageId()));
        this.problem_id = IJSONTool.getProblemId(model.getProblem(submission.getProblemId()));
        this.team_id = new Integer(submission.getSubmitter().getClientNumber()).toString();
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
}
