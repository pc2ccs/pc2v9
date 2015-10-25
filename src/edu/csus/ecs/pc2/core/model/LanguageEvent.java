package edu.csus.ecs.pc2.core.model;

/**
 * A language and a event state {@link edu.csus.ecs.pc2.core.model.LanguageEvent.Action}.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

//$HeadURL$
public class LanguageEvent {


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
        /**
         * More then 1 language was added
         */
        ADDED_LANGUAGES,
        /**
         * More then 1 language was updated
         */
        CHANGED_LANGUAGES,
        /**
         * Refresh all languages
         */
        REFRESH_ALL,

    }

    private Action action;

    private Language language;

    private Language[] languages;

    public LanguageEvent(Action problemAction, Language language) {
        super();
        this.action = problemAction;
        this.language = language;
    }

    public LanguageEvent(Action action, Language[] languages) {
        super();
        this.action = action;
        this.setLanguages(languages);
    }

    public Action getAction() {
        return action;
    }

    public Language getLanguage() {
        return language;
    }

    /**
     * @return the languages
     */
    public Language[] getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(Language[] languages) {
        this.languages = languages;
    }

}
