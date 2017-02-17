package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;
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
     * Gets all the teams in the contest. Returns an array of {@link ITeam} objects, where each {@link ITeam} element describes one Team account in the contest. Note that the returned array contains
     * one entry for every Team account which is currently defined, whether or not that team is currently logged in to the contest via PC<sup>2</sup>. Note also that in a multi-site contest the
     * returned array contains an entry for every account <I>at every site</i>, not just for the site for the server to which the client is currently connected.
     * <P>
     * <A NAME="printTeamsSample"></A> The following code snippet shows typical usage for obtaining and printing a list of all teams currently in the contest. It assumes variable <code>contest</code>
     * represents a valid {@link IContest} obtained from a server to which this client is connected.
     * 
     * <pre>
     * for (ITeam team : contest.getTeams()) {
     *     String teamName = team.getDisplayName();
     *     int siteNumber = team.getSiteNumber();
     *     String groupName = team.getGroup().getName();
     *     System.out.println(teamName + &quot; Site: &quot; + siteNumber + &quot; Group: &quot; + groupName);
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
     * The languages in the returned array are always ordered in the order in which the Contest Administrator
     * entered them into the contest:  element [0] is the first language, etc.
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
     * 
     * Returns an array containing one {@link IProblem} for each currently defined/active contest problem.
     * The problems in the returned array are always ordered in the order in which the Contest Administrator
     * entered them into the contest:  element [0] is the first problem, etc.
     * <P>
     * <A NAME="printProblemsSample"></A>  
     * The following code snippet shows typical usage for obtaining and printing the names of all problems
     * currently defined in the contest. It assumes variable <code>contest</code> represents a valid
     * {@link IContest} obtained from a server to which this client is connected.
     * 
     * <pre>
     * for (IProblem problem : contest.getProblems()) {
     *     System.out.println(problem.getName());
     * }
     * </pre>
     * 
     * 
     * 
     * @return An array containing one {@link IProblem} for each currently defined (non-hidden) contest problem.
     */
    IProblem[] getProblems();
    
    /**
     * Get an ordered list of all contest problems (hidden or non-hidden).
     * 
     * 
     * @see #getProblems()
     * @return An array containing one {@link IProblem} for each currently defined contest problem.
     */
    IProblem[] getAllProblems();
    
    
    /**
     * Get an ordered list of all currently defined clarification categories.
     * 
     * <pre>
     * // list all clarification categories and problems.
     * </pre>
     * for (IProblem problem : contest.getClarificationCategories()) {
     *     System.out.println(problem.getName());
     * }
     * for (IProblem problem : contest.getProblems()) {
     *     System.out.println(problem.getName());
     * }
     * @return An array containing one {@link IProblem} for each defined clarification category.
     */
    IProblem[] getClarificationCategories();
    
    /**
     * Get an ordered list of all currently defined contest sites.
     * Returns an array containing one {@link ISite} for each currently defined contest site.
     * The sites in the returned array are always ordered in the order in which the Contest Administrator
     * defined them in the contest:  element [0] is the first site, etc.
     * 
     * @return An array containing one {@link ISite} for each currently defined contest site.
     */
    ISite [] getSites();

    /**
     * Get a list of all currently-defined (i.e., possible) judgements.  Note that this refers to the Judgement values which
     * the Contest Administrator has configured into the contest settings (i.e., the list of judgement results
     * from which a Judge may choose when assigning a result to any particular submitted run); 
     * it is not related to the specific judgements which may have been assigned to any particular run.
     * <P>
     * Returns an array containing one {@link IJudgement} for each currently defined allowable Judge's response
     * to a submitted run. The returned {@link IJudgement}s are given in the array in the order in which they
     * were defined by the Contest Administrator.
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
     * @return An ordered array containing one {@link IJudgement} for each currently defined allowable Judge's response
     * to a submitted run.
     * 
     */
    IJudgement[] getJudgements();

    /**
     * Get a list of all the runs in the contest.
     * 
     * Returns an array of {@link IRun}s, where each element of the array holds a single contest {@link IRun}. In a multi-site contest the returned array will contain the runs from all connected
     * sites, not just the site for the server to which this client is connected.
     * 
     * <P>
     * <A NAME="getRunsSample"></A> The following code snippet shows typical usage for obtaining and printing a list of all runs currently in the contest. It assumes variable <code>contest</code>
     * represents a valid {@link IContest} obtained from a server to which this client is connected.
     * 
     * <pre>
     * for (IRun run : contest.getRuns()) {
     * 
     *     System.out.println(&quot;Run &quot; + run.getNumber() + &quot; from site &quot; + run.getSiteNumber());
     *     System.out.println(&quot;    submitted at &quot; + run.getSubmissionTime() + &quot; minutes by &quot; + run.getTeam().getDisplayName());
     *     System.out.println(&quot;    For problem &quot; + run.getProblem().getName());
     *     System.out.println(&quot;    Written in &quot; + run.getLanguage().getName());
     * 
     *     if (run.isFinalJudged()) {
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
     * Get single run by runNumber from the current site.
     * 
     * @param runNumber the number of the run to be fetched
     * @return the IRun or null if no such run exists.
     */
    IRun getRun(int runNumber);
    
    /**
     * Get a list of all the clarifications in the contest.
     * 
     * @return An unordered list of Clarifications for all sites  connected to the contest.
     */
    IClarification [] getClarifications();

    /**
     * Add a Run Event listener to the contest. 
     * 
     * A run event listener (object of type {@link IRunEventListener}) will be
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
     * Add a Connection Event listener to the contest.
     * 
     * A connection event listener (object of type {@link IConnectionEventListener}) will be 
     * invoked every time a connection to the server has been dropped, this client is no longer logged in.
     * 
     * @param connectionEventListener the IConnectionEventListener to be added
     */
    void addConnectionListener(IConnectionEventListener connectionEventListener);

    /**
     * Remove the specified connection event listener from the contest.
     * 
     * @param connectionEventListener
     *            The {@link IConnectionEventListener} listener to be removed.
     */
    void removeConnectionListener(IConnectionEventListener connectionEventListener);

    /**
     * Remove the specified clarification event listener from the contest.
     * 
     * @param clarificationEventListener the IClarificationEventListener to be added
     */
    void removeClarificationListener(IClarificationEventListener clarificationEventListener);

    /**
     * Add a Clarification Event listener to contest.
     * A clarification event listener (object of type {@link IClarificationEventListener}) will be
     * invoked every time a clarification is added to the contest, modified (e.g. Answered), or marked as deleted from the contest.
     * Custom clients using the PC<sup>2</sup> API can therefore arrange to be notified when any of these conditions occurs.
     * 
     * @see IClarificationEventListener
     * @see IClarification
     * @param clarificationEventListener an {@link IClarificationEventListener} listener for Clarification events.
     */
    void addClarificationListener(IClarificationEventListener clarificationEventListener);

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
     * <P>
     * Note that the {@link IContestClock} object returned by the current implementation of 
     * this method is static; it does not dynamically update.  In other words, obtaining the
     * current contest time requires obtaining a new {@link IContestClock} object each 
     * time the current time information is needed.
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
     * 
     * @return the ICLient object for the currently logged-in client
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
     * Returns true if in CCS Test Mode.
     * 
     * CCS Test mode allows the override of the elapsed (contest) time, the test mode
     * is set in the Contest Information Tab on the Admin.
     * 
     * @return true if in CCS test mode, false otherwise.
     */
    boolean isCCSTestMode();
    
    
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
     * Returns an array of {@link IStanding}s describing the current standing of every team in the contest as determined by the currently active plugin implementation of the PC<sup>2</sup> scoring
     * algorithm.
     * <P>
     * Note that the determination of the data in an {@link IStanding} is up to the scoring algorithm, which can be dynamically changed by the Contest Administrator during a contest. Note also that
     * scoring details such as how to rank teams that are tied is also a function of the scoring algorithm.
     * <P>
     * In addition, note also that the order in which the {@link IStanding}s are contained in the returned array is a function of the scoring algorithm. In particular, while the default PC<sup>2</sup>
     * scoring algorithm provides {@link IStanding}s in ranked order, scoring algorithm implementations are not required to do so.
     * 
     * <pre>
     * for (IStanding standingRank : contest.getStandings()) {
     *     String displayName = standingRank.getClient().getDisplayName();
     *     System.out.printf(&quot; %3d %-35s %2d %4d&quot;, standingRank.getRank(), displayName, standingRank.getNumProblemsSolved(), standingRank.getPenaltyPoints());
     * }
     * </pre>
     * 
     * @return An array of {@link IStanding}s, one element for each contest team.
     */
    IStanding [] getStandings();
    
    /**
     * Returns name of the host (server) this application is using.
     * 
     * @return name of server host computer.
     */
    String getServerHostName();
    
    /**
     * Returns the port number for the host (server) this application is using.
     * 
     * @return port number for port. 
     */
    int getServerPort();
    
    /**
     * Get all the problem details.
     * 
     * @return problem details for all teams sorted by site, team and then problem.
     */
    IProblemDetails[] getProblemDetails();

    /**
     * Get all clients from all sites.
     * 
     * @return a list of clients from all sites.
     */
    IClient[] getClientsAllSites();

    /**
     * Get all this site's clients.
     * 
     * @return a list of clients from this site.
     */
    IClient[] getClients();

    /**
     * Get run state for a run (judge or admin only).
     * 
     * @param run the IRun whose state is to be fetched
     * @return run state
     */
    RunStates getRunState(IRun run);
    
    /**
     * Get PC^2 Major version.
     * 
     * For "9.3Beta" would be "9"
     * 
     * @return the "Major version number" of this version of PC^2
     */
    String getMajorVersion();

    /**
     * Get PC^2 Minor version number.
     * 
     * For "9.3Beta" would be "3"
     * @return the "minor version number" of this version of PC^2
     */
    String getMinorVersion();
    
    /**
     * Get PC^2 Build number.
     * @return build number.
     */
    String getBuildNumber();
    
    /**
     * Get Full PC^2 version string.
     * <P>
     * Example:
     * <pre>Version 9.3 20150205 (Thursday, February 5th 2015 15:55 UTC) build 2920</pre>
     * 
     * @return a String containing the full PC^2 version string for this version of PC^2
     */
    String getFullVersionString();

    /**
     * Get extra version info.
     * 
     * Get anything after the minor version number.
     * <br><br>
     *  For ""9.3Beta"" would be "Beta"
     * @return extra version info
     */
    String getOtherVersionInfo();

    /**
     * Get a run from a site.
     * 
     * @param siteNumber the site from which the run should be fetched
     * @param runNumber the ID number of the Run to be fetched
     * @return an IRun object describing the requested run
     */
    IRun getRun(int siteNumber, int runNumber);

}
