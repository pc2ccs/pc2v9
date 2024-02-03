// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.LanguageUtilities;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * CLICS Language
 * Contains information about a language
 *
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSLanguage {

    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private boolean entry_point_required;

    @JsonProperty
    private String entry_point_name;

    @JsonProperty
    private String [] extensions;

    @JsonProperty
    private CLICSCommand compiler;

    @JsonProperty
    private CLICSCommand runner;

    /**
     * Fills in the test case properties
     *
     * @param language The language being serialized
     */
    public CLICSLanguage(Language language) {
        id = JSONTool.getLanguageId(language);
        name = language.getDisplayName();
        entry_point_required = false;
        compiler = new CLICSCommand(language.getCompileCommandLine());
        if(language.isUsingJudgeProgramExecuteCommandLine()) {
            runner = new CLICSCommand(language.getJudgeProgramExecuteCommandLine());
        } else {
            runner = new CLICSCommand(language.getProgramExecuteCommandLine());
        }
        extensions = LanguageUtilities.getExtensionsForLanguage(id, name);
    }

    public String toJSON() {

        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return "Error creating JSON for language info " + e.getMessage();
        }
    }
}
