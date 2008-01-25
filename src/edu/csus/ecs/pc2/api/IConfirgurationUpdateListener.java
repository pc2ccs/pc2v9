package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Contest Data Update, both configuration and run-time.
 * 
 * This event is triggered when any configuration data is changed (added, removed, updated).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
 // $HeadURL$
 
public interface IConfirgurationUpdateListener {

    /**
     * All contest data has been reset or changed.
     * 
     * This event happens when a reset is done on the server, any or all of the configuration information may have changed.
     * <P>
     */
    void reset();

    /**
     * Account has been added.
     * 
     * @param clientId
     *            the Client of the new account.
     */
    void accountAdded(ClientId clientId);

    /**
     * Account has been removed.
     * 
     * @param clientId
     *            the ClientId of the new account.
     */
    void accountRemoved(ClientId clientId);

    /**
     * Account has been updated.
     * 
     * Could be triggered by a change in the client's title.
     * 
     * @param clientId
     *            the ClientId of the changed account.
     */
    void accountUpdated(ClientId clientId);

    /**
     * The contest title has been updated.
     * 
     * @see edu.csus.ecs.pc2.IContest#getContestTitle()
     * @param title
     *            new title for contest.
     */
    void titleUpdated(String title);

    /**
     * Language has been added.
     * 
     * The title, compile command line, etc has been changed.
     * 
     * @see edu.csus.ecs.pc2.IContest#getLanguageTitle(ElementId)
     * @param elementId
     *            the ElementId for the added language.
     */
    void languageAdded(ElementId elementId);

    /**
     * A language was removed.
     * 
     * @see edu.csus.ecs.pc2.IContest#getLanguageTitle(ElementId)
     * @param elementId
     *            the ElementId for the removed language.
     */
    void languageRemoved(ElementId elementId);

    void languageUpdated(ElementId elementId);

    void problemAdded(ElementId elementId);

    void problemRemoved(ElementId elementId);

    void problemUpdated(ElementId elementId);

    void judgementAdded(ElementId elementId);

    void judgementRemoved(ElementId elementId);

    void judgementUpdated(ElementId elementId);
}
