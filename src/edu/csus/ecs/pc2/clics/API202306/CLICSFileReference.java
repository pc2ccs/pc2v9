// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS File Reference
 * Contains information about a file used in the API
 *
 * @author John Buck
 *
 */

@JsonFilter("rtFilter")
public class CLICSFileReference {

    @JsonProperty
    private String href;

    @JsonProperty
    private String filename;

    @JsonProperty
    private String hash;

    @JsonProperty
    private String mime;

    @JsonProperty
    private int width;

    @JsonProperty
    private int height;

    /**
     * Fills in properties for a CLICS FILE reference.  Currently, we only support the required Href, Filename and Mime type.
     *
     * @param href
     * @param filename
     * @param mime
     */
    public CLICSFileReference(String href, String filename, String mime) {
        this.href = href;
        this.filename = filename;
        this.mime = mime;
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
            return "Error creating JSON for FILE reference " + e.getMessage();
        }
    }

    /**
     * Get set of properties for which we do not want to serialize into JSON.
     * This is so we don't serialize width/height if they are 0
     *
     * @param exceptProps Set to fill in with property names to omit
     */
    public void getExceptProps(Set<String> exceptProps) {
        if(width == 0){
            exceptProps.add("width");
        }
        if(height == 0) {
            exceptProps.add("height");
        }
    }
}
