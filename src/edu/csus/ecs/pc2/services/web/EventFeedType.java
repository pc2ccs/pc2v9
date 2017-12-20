package edu.csus.ecs.pc2.services.web;

/**
 * Event Feed Types.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public enum EventFeedType {

    UNDEFINED("Undefined"),
    /**
     *
     */
    AWARDS("awards"),
    /**
     *
     */
    CLARIFICATIONS("clarifications"),
    /**
     *
     */
    CONTESTS("contests"),
    /**
     *
     */
    STATE("state"),
    /**
     *
     */
    GROUPS("groups"),
    /**
     *
     */
    JUDGEMENTS("judgements"),
    /**
     *
     */
    JUDGEMENT_TYPES("judgement-types"),
    /**
     *
     */
    LANGUAGES("languages"),
    /**
     *
     */
    ORGANIZATIONS("organizations"),
    /**
     *
     */
    PROBLEMS("problems"),
    /**
     *
     */
    RUNS("runs"),
    /**
     *
     */
    SUBMISSIONS("submissions"),
    /**
     *
     */
    TEAMS("teams"),
    /**
     *
     */
    TEAM_MEMBERS("team-members");

    private final String name;

    EventFeedType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
}
