package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Set of methods that any Configuration Update Listener must implement.
 *
 * These are the events/methods invoked when contest data has changed.<br>
 * For Account changes the ClientId for the client is supplied, for other
 * contest data the ElementId is supplied.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
 // $HeadURL$
 
public interface IConfigurationUpdateListener {

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
     * @see edu.csus.ecs.pc2.api.IContest#getContestTitle()
     * @param title
     *            new title for contest.
     */
    void titleUpdated(String title);

    /**
     * Language has been added.
     * 
     * The title, compile command line, etc has been changed.
     * 
     * @see edu.csus.ecs.pc2.api.IContest#getLanguageTitle(ElementId)
     * @param elementId
     *            the ElementId for the added language.
     */
    void languageAdded(ElementId elementId);

    /**
     * A language was removed.
     * 
     * @see edu.csus.ecs.pc2.api.IContest#getLanguageTitle(ElementId)
     * @param elementId
     *            the ElementId for the removed language.
     */
    void languageRemoved(ElementId elementId);

    /**
     * Language updated.
     * 
     * @param elementId         the ElementId for the changed language.
     */
    void languageUpdated(ElementId elementId);

    /**
     * Problem added.
     * 
     * @param elementId the ElementId for the added problem.
     */
    void problemAdded(ElementId elementId);

    /**
     * Problem removed.
     * 
     * @param elementId the ElementId for the removed problem.
     */
    void problemRemoved(ElementId elementId);

    /**
     * Problem updated
     * @param elementId the ElementId for the updated problem.
     */
    void problemUpdated(ElementId elementId);

    /**
     * Judgement added.
     * @param elementId the ElementId for the added judgement.
     */
    void judgementAdded(ElementId elementId);

    /**
     * Judgement removed.
     * @param elementId the ElementId for the added judgement.
     */
    void judgementRemoved(ElementId elementId);

    /**
     * Judgement Updated.
     * @param elementId the ElementId for the added judgement.
     */
    void judgementUpdated(ElementId elementId);
}
