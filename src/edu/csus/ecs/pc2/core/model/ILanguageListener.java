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
     * Language has been removed.
     * @param event
     */
    void languageRemoved(LanguageEvent event);
    
    /**
     * Languages were added
     * @param event
     */
    void languagesAdded(LanguageEvent event);
    
    /**
     * Languages were changed
     * @param event
     */
    void languagesChanged(LanguageEvent event);

    /**
     * Languages need to be reloaded/refreshed
     * @param event
     */
    void languageRefreshAll(LanguageEvent event);
    
}
