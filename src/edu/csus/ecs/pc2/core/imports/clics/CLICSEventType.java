// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

/**
 * CLICS Event Feed Event Type.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public enum CLICSEventType {

    /**
     * accounts event
     */
    ACCOUNTS("accounts"),
    /**
     * clarifications event
     */
    CLARIFICATIONS("clarifications"),
    /**
     * contest event
     */
    CONTEST("contest"),
    /**
     * groups event
     */
    GROUPS("groups"),
    /**
     * judgement-types event
     */
    JUDGEMENT_TYPES("judgement-types"),
    /**
     * judgements event
     */
    JUDGEMENTS("judgements"),
    /**
     * languages event
     */
    LANGUAGES("languages"),
    /**
     * organizations event
     */
    ORGANIZATIONS("organizations"),
    /**
     * persons event
     */
    PERSONS("persons"),
    /**
     * problems event
     */
    PROBLEMS("problems"),
    /**
     * runs event
     */
    RUNS("runs"),
    /**
     * state event
     */
    STATE("state"),
    /**
     * submissions event
     */
    SUBMISSIONS("submissions"),
    /**
     * Awards event
     */
    AWARDS("awards"),
    /**
     * teams event
     */
    TEAMS("teams");

    private String name;

    CLICSEventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    };

    @Override
    public String toString() {
        return name;
    }

}
