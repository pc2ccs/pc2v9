// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.model.JSONObjectMapper;

/**
 * A message
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class MessageRecord {

    @JsonProperty
    private String message;

    @JsonProperty
    private Exception exception;

    @JsonProperty
    private Level level = Level.INFO;

    @JsonProperty
    private MessageScope scope = MessageScope.NONE;

    public MessageRecord(String message, MessageScope scope, Exception exception) {
        super();
        this.message = message;
        this.scope = scope;
        this.exception = exception;
    }

    public MessageRecord(String message, Exception exception) {
        super();
        this.message = message;
        this.exception = exception;
    }

    public MessageRecord(String message, MessageScope scope, Level level, Exception exception) {
        super();
        this.message = message;
        this.scope = scope;
        this.level = level;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }

    public Level getLevel() {
        return level;
    }

    public MessageScope getScope() {
        return scope;
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper om = JSONObjectMapper.getObjectMapper();
        return om.writeValueAsString(this);
    }

    @Override
    public String toString() {
        try {
            return toJSON();
        } catch (Exception e) {
            return "<invalid " + e.getMessage() + ">";
        }
    }

}
