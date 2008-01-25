package edu.csus.ecs.pc2;

import edu.csus.ecs.pc2.api.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.IRunEventListener;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;

/**
 * Contest data/information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO must javadoc ClientId
// TODO must javadoc ElementId
// $HeadURL$
public interface IContest {

    /**
     * Has this client logged in ?.
     * 
     * If the client has used the {@link IController#login(String, String)} successfully then this method will return true.
     * <P>
     * If the client has logged off ({@link IController#logoff()}) or never logged in then returns false.
     * 
     * @see IController#login(String, String)
     * @return true if logged in, false if not.
     */
    boolean isLoggedIn();

    /**
     * Returns the display name for the logged in client.
     * 
     * For each login there is a display name, like a school name.
     * <P>
     * Use {@link #getContestTitle()} to get the contest title.
     */
    String getTitle();

    /**
     * Gets the ClientId for the logged in client.
     * 
     * @return ClientId (site, client type and client number) @
     */
    ClientId getClientId();

    /**
     * Get the title for the specified site.
     * 
     * @param siteNumber
     * @return title for this site. @
     */
    String getSiteTitle(int siteNumber);

    /**
     * Get the title for a judgement.
     * 
     * @see #getJudgementIds()
     * @param elementId
     *            an id that uniquely identifies a judgement
     * @return String
     * 
     */
    String getJudgementTitle(ElementId elementId);

    /**
     * Get the title for a problem.
     * 
     * @param elementId
     *            an id that uniquely identifies a judgement
     * @return the title for the problem.
     * 
     */
    String getProblemTitle(ElementId elementId);

    /**
     * Get the title for a language.
     * 
     * @param elementId
     *            an id that uniquely identifies a judgement
     * @return the title for the language.
     * 
     */
    String getLanguageTitle(ElementId elementId);

    /**
     * Get the contest title.
     * 
     * @return the title of the contest.
     * 
     */
    String getContestTitle();

    /**
     * Get the current server's site title.
     * 
     * Gets the title for the server this client is logged into.
     * 
     * @return the current server's site title.
     * 
     */
    String getSiteTitle();

    /**
     * Get a list of languages (ElementIds)
     * 
     * Returns a list of languages, to get the title for the languages use {@link #getLanguageTitle(ElementId)}
     * <P>
     * Code snippet to print all language names.
     * 
     * <pre>
     * for (ElementId elementId : contest.getLanguageIds()) {
     *     System.out.println(contest.getLanguageTitle(elementId));
     * }
     * </pre>
     * 
     * @return list of language ids in proper order
     * 
     */
    ElementId[] getLanguageIds();

    /**
     * Get a list of problems (ElementIds).
     * 
     * Returns a list of problems, to get the title for the problems use {@link #getLanguageTitle(ElementId)}
     * <P>
     * Code snippet to print all language titles/names.
     * 
     * <pre>
     * for (ElementId elementId : contest.getProblemIds()) {
     *     System.out.println(contest.getProblemTitle(elementId));
     * }
     * </pre>
     * 
     * @return list of problem ids in proper order
     * 
     */
    ElementId[] getProblemIds();

    /**
     * Get a list of judgement (ElementIds).
     * 
     * Code snippet to print all judgement titles/names.
     * 
     * <pre>
     * for (ElementId elementId : contest.getJudgementIds()) {
     *     System.out.println(contest.getJudgementTitle(elementId));
     * }
     * </pre>
     * 
     * @return list of judgements in proper order.
     * 
     */
    ElementId[] getJudgementIds();

    /**
     * Add run event listener.
     * 
     * @see edu.csus.ecs.pc2.api.RunUpdateEvent
     * @param runEventListener
     *            listener for Run events
     */
    void addRunListener(IRunEventListener runEventListener);

    /**
     * Remove run event listener.
     * 
     * @see edu.csus.ecs.pc2.api.RunUpdateEvent
     * @param runEventListener
     *            listener for Run events
     */
    void removeRunListener(IRunEventListener runEventListener);

    /**
     * Add Contest Update listener.
     * 
     * @see edu.csus.ecs.pc2.api.ConfigurationUpdateEvent
     * @param contestUpdateConfigurationListener
     *            listener for Configuration Update events
     */
    void addContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener);

    /**
     * Remove Contest Update listener.
     * 
     * @see edu.csus.ecs.pc2.api.ConfigurationUpdateEvent
     * @param contestUpdateConfigurationListener
     *            listener for Configuration Update events
     */
    void removeContestUpdateConfigurationListener(IConfigurationUpdateListener contestUpdateConfigurationListener);

}
