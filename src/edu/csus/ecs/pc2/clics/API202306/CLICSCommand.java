// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CLICS Command information
 * Contains information about a command, such as a compiler or runner
 * 
 * @author John Buck
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CLICSCommand {

    @JsonProperty
    private String command;

    @JsonProperty
    private String args;

    @JsonProperty
    private String version;

    @JsonProperty
    private String version_command;

    /**
     * Fill in command information properties
     * 
     * @param command The command to execute
     * @param args Arguments to command, null if none
     */
    public CLICSCommand(String command, String args) {
        this.command = command;
        this.args = args;
    }
    
    /**
     * Fill in command and extract args from supplied string (after first space)
     * 
     * @param command
     */
    public CLICSCommand(String command) {
        int indexSep = command.indexOf(' ');
        if(indexSep > 0) {
            this.command = command.substring(0, indexSep);
            this.args = command.substring(indexSep+1);
        } else {
            this.command = command;
        }
    }
}
