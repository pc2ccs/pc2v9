package edu.csus.ecs.pc2.shadow;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A submission from a remote CCS via the REST event-feed API.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ShadowRunSubmission {
    
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
    private List< Map<String,String>> files;
    @JsonProperty
    private String mime;
    
    public String getId() {
        return id;
    }
    public String getLanguage_id() {
        return language_id;
    }
    public String getProblem_id() {
        return problem_id;
    }
    public String getTeam_id() {
        return team_id;
    }
    public String getTime() {
        return time;
    }
    public String getContest_time() {
        return contest_time;
    }
    public String getEntry_point() {
        return entry_point;
    }
    
    public List<Map<String, String>> getFiles() {
        return files;
    }
    
    public String getMime() {
        return mime;
    }
}
