package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Language Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface ILanguageListener {

    /**
     * New Language.
     * @param event
     */
    void languageAdded(LanguageEvent event);

    /**
     * Language information has changed.
     * @param event
     */
    void languageChanged(LanguageEvent event);

    /**
     * Run has been removed.
     * @param event
     */
    void languageRemoved(LanguageEvent event);
}
