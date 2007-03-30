package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Language Events.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface ILanguageListener {

    /**
     * New Language.
     * @param event
     */
    void problemAdded(LanguageEvent event);

    /**
     * Language information has changed.
     * @param event
     */
    void problemChanged(LanguageEvent event);

    /**
     * Run has been removed.
     * @param event
     */
    void problemRemoved(LanguageEvent event);
}
