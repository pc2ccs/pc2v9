package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

/**
 * This interface represents the PC<sup>2</sup> API view of the contest information available
 * to a client connected to a PC<sup>2</sup> server through the API.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IContest {

    /**
     * Gets all the teams in the contest. Returns an array of {@link ITeam} objects,
     * where each {@link ITeam} element describes one Team account in the contest.
     * Note that the returned array contains one entry for every Team account which is
     * currently defined, whether or not that team is currently logged in to the contest
     * via PC<sup>2</sup>.  Note also that in a multi-site contest the returned array contains an entry for every
     * account <I>at every site</i>, not just for the site for the server to which the client is currently connected.
     * <P>
     * <A NAME="printTeamsSample"></A>  
     * The following code snippet shows typical usage for obtaining and printing a list of all teams currently 
     * in the contest. It assumes variable <code>contest</code> represents a valid
     * {@link IContest} obtained from a server to which this client is connected.
     * <pre>
     * for (ITeam team : contest.getTeams()) {
     *      String teamName = team.getDisplayName();
     *      int siteNumber = team.getSiteNumber();
     *      String groupName = team.getGroup().getName();
     *      System.out.println(teamName + &quot; Site: &quot; + siteNumber + &quot; Group: &quot; + groupName));
     * }
     * </pre>
     * 
     * @return an unordered array of {@link ITeam}s, one for each team account defined in the contest.
     */
    ITeam[] getTeams();

    /**
     * Get the name for the specified contest site.
     * 
     * @param siteNumber The number of a site in the contest.
     * @return A String containing the name of the specified contest site.
     */
    String getSiteName(int siteNumber);

    /**
     * Get the contest title.  The contest title is configured by the Contest Administrator
     * using the PC<sup>2</sup> &quot;Admin&quot; module.
     * 
     * @return A String containing the title of the contest.
     * 
     */
    String getContestTitle();

    /**
     * Get the name of the contest site for the PC<sup>2</sup> server to which this client is currently connected.
     * 
     * @return the current server's site name.
     * 
     */
    String getSiteName();

    /**
     * Get a list of all currently defined contest languages.
     * Returns an array containing one {@link ILanguage} for each currently defined contest language.
     * <P>
     * <A NAME="printLanguagesSample"></A>  
     * The following code snippet shows typical usage for obtaining and printing the names of all languages
     * currently defined in the contest. It assumes variable <code>contest</code> represents a valid
     * {@link IContest} obtained from a server to which this client is connected.
     * <pre>
     * for (ILanguage language : contest.getLanguages()) {
     *     System.out.println(language.getName());
     * }
     * </pre>
     * 
     * @return An array of {@link ILanguage}s, one for each language defined in the contest.
     * 
     */
    ILanguage[] getLanguages();

    /**
     * Get an ordered list of all currently defined contest problems.
     * Returns an array containing one {@link IProblem} for each currently defined contest problem.
     * The problems in the returned array are always ordered in the order in which the Contest Administrator
     * entered them into the contest:  element [0] is the first problem, etc.
     * <P>
     * <A NAME="printProblemsSample"></A>  
     * The following code snippet shows typical usage for obtaining and printing the names of all problems
     * currently defined in the contest. It assumes variable <code>contest</code> represents a valid
     * {@link IContest} obtained from a server to which this client is connected.
     * <pre>
     * for (IProblem problem : contest.getProblems()) {
     *     System.out.println(problem.getName());
     * }
     * </pre>
     * 
     * @return An array containing one {@link IProblem} for each currently defined contest problem.
     * 
     */
    IProblem[] getProblems();
    
    /**
     * 
     * @return An array containing one {@link ISite} for each currently defined contest site.
     */
    // TODO document
    ISite [] getSites();

    /**
     * Get a list of all currently-defined (i.e., possible) judgements.  Note that this refers to the Judgement values which
     * the Contest Administrator has configured into the contest settings (i.e., the list of judgement results
     * from which a Judge may choose when assigning a result to any particular submitted run); 
     * it is not related to the specific judgements which may have been assigned to any particular run.
     * Returns an array containing one {@link IJudgement} for each currently defined allowable Judge's response
     * to a submitted run.
     * <P>
     * <A NAME="printJudgementsSample"></A>  
     * The following code snippet shows typical usage for obtaining and printing the names of all Judgements
     * currently defined in the contest. It assumes variable <code>contest</code> represents a valid
     * {@link IContest} obtained from a server to which this client is connected.
     * <pre>
     * for (IJudgement judgement : contest.getJudgements()) {
     *     System.out.println(judgement.getName());
     * }
     * </pre>
     * 
     * @return An array containing one {@link IJudgement} for each currently defined allowable Judge's response
     * to a submitted run.
     * 
     */
    IJudgement[] getJudgements();

    /**
     * Get a list of all the runs in the contest.  Returns an array of {@link IRun}s, where 
     * each element of the array holds a single contest {@link IRun}.  In a multi-site contest
     * the returned array will contain the runs from all connected sites, not just the site for the
     * server to which this client is connected.   
     * 
     * <P>
     * <A NAME="getRunsSample"></A>  
     * The following code snippet shows typical usage for obtaining and printing a list of all runs currently 
     * in the contest. It assumes variable <code>contest</code> represents a valid
     * {@link IContest} obtained from a server to which this client is connected.
     * <pre>
     * for (IRun run : contest.getRuns()) {
     * 
     *     System.out.println(&quot;Run &quot; + run.getNumber() + &quot; from site &quot; + run.getSiteNumber());
     *     System.out.println(&quot;    submitted at &quot; + run.getSubmissionTime() + &quot; minutes by &quot; + run.getTeam().getDisplayName());
     *     System.out.println(&quot;    For problem &quot; + run.getProblem().getName());
     *     System.out.println(&quot;    Written in &quot; + run.getLanguage().getName());
     * 
     *     if (run.isJudged()) {
     *         System.out.println(&quot;    Judgement: &quot; + run.getJudgementName());
     *     } else {
     *         System.out.println(&quot;    Judgement: not judged yet &quot;);
     *     }
     * }
     * </pre>
     * 
     * @return An unordered list of Runs for all sites currently connected to the contest.
     */
    IRun[] getRuns();

    /**
     * Add a Run Event listener to the contest.  A run event listener (object of type {@link IRunEventListener}) will be
     * invoked every time a run is added to the contest, modified (e.g. Judged), or marked as deleted from the contest.
     * Custom clients using the PC<sup>2</sup> API can therefore arrange to be notified when any of these conditions occurs.
     * 
     * @see IRunEventListener
     * @see IRun
     * @param runEventListener an {@link IRunEventListener} listener for Run events
     */
    void addRunListener(IRunEventListener runEventListener);

    /**
     * Remove the specified run event listener from the contest.
     * 
     * @param runEventListener
     *            The {@link IRunEventListener} listener to be removed.
     */
    void removeRunListener(IRunEventListener runEventListener);

    /**
     * Add a Contest Configuration Update listener to the contest.
     * A configuration update listener (object of type {@link IConfigurationUpdateListener}) will be
     * invoked every time a contest configuration item is added, modified, or removed from the contest.
     * Custom clients using the PC<sup>2</sup> API can therefore arrange to be notified when any of these conditions occurs.
     * 
     * @see IConfigurationUpdateListener
     * @see edu.csus.ecs.pc2.api.listener.ContestEvent
     * 
     * @param contestConfigurationUpdateListener
     *            an {@link IConfigurationUpdateListener} listener for configuration update events
     */
    void addContestConfigurationUpdateListener(IConfigurationUpdateListener contestConfigurationUpdateListener);

    /**
     * Remove the specified Contest Configuration Update listener from the contest.
     * 
     * @param contestConfigurationUpdateListener
     *            The {@link IConfigurationUpdateListener} listener to be removed.
     */
    void removeContestConfigurationUpdateListener(IConfigurationUpdateListener contestConfigurationUpdateListener);

    /**
     * Get an {@link IContestClock} object containing contest time-related information.
     * The {@link IContestClock} object can be queried for values such as the amount of 
     * time elasped so far in the contest, the amount of time remaining in the contest, and
     * whether the contest clock is currently &quot;paused&quot; or not. 
     * 
     * @see IContestClock
     * @return A {@link IContestClock} object containing contest time information
     */
    IContestClock getContestClock();

    /**
     * Get a list of the <b>groups</b> currently defined in the contest. 
     * Groups can be used by the Contest Administrator to associate Teams together.
     * For example, all teams from a certain geographical region, or with an equivalent background
     * (say, Undergraduate vs. Graduate) can be put together in the same group.  
     * The PC<sup>2</sup> scoring algorithm implementation can then be used to compute
     * standings on a per-group basis.
     * 
     * @see IGroup
     * @return list of groups.
     */
    IGroup[] getGroups();

    /**
     * Get the current logged in client.
     * This method can be used by a custom client making use of the PC<sup>2</sup> API to
     * obtain at runtime a PC<sup>2</sup> {@link IClient} description of its own client
     * login data.
     */
    IClient getMyClient();

    /**
     * Returns a boolean value indicating whether the contest clock is currently running.
     * If the method returns false, either the contest has not been started, or it has
     * been started and then paused by the Contest Administrator.  Method {@link IContest#getContestClock()} 
     * can be used to obtain clock information to determine which case exists (not yet started vs. paused.)
     * 
     * @return true if the contest clock is currently running; false otherwise.
     */
    boolean isContestClockRunning();
    
    
    /**
     * Returns an {@link IStanding} describing the current standing of the specified team in the contest as
     * determined by the currently active implementation of the PC<sup>2</sup> scoring algorithm.
     * <P>
     * Note that the determination of the data in an {@link IStanding} is up to the scoring algorithm, which 
     * can be dynamically changed by the Contest Administrator during a contest.  Note also that scoring
     * details such as how to rank teams that are tied is also a function of the scoring algorithm.
     * 
     * @param team The {@link ITeam} for which an {@link IStanding} is being requested.
     * @return An {@link IStanding} for the specified team.
     */
    IStanding getStanding(ITeam team);
    
    
    /**
     * Returns an array of {@link IStanding}s describing the current standing of every team in the contest as
     * determined by the currently active implementation of the PC<sup>2</sup> scoring algorithm.
     * <P>
     * Note that the determination of the data in an {@link IStanding} is up to the scoring algorithm, which 
     * can be dynamically changed by the Contest Administrator during a contest.  Note also that scoring
     * details such as how to rank teams that are tied is also a function of the scoring algorithm.
     * 
     * @return An array of {@link IStanding}s, one element for each contest team.
     */
    IStanding [] getStandings();
    
}
