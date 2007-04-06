package edu.csus.ecs.pc2.core.model;

/**
 * A language and a event state {@link edu.csus.ecs.pc2.core.model.LanguageEvent.Action}.
 * 
 * @author pc2@ecs.csus.edu
 */

//$HeadURL$
public class LanguageEvent {

    public static final String SVN_ID = "$Id$";

    /**
     * Run Event States(s).
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum Action {

        /**
         * Run set to be deleted.
         */
        DELETED,
        /**
         * A new language.
         */
        ADDED,
        /**
         * Modified Language.
         */
        CHANGED,

    }

    private Action action;

    private Language language;

    public LanguageEvent(Action problemAction, Language language) {
        super();
        this.action = problemAction;
        this.language = language;
    }

    public Action getAction() {
        return action;
    }

    public Language getLanguage() {
        return language;
    }

}
