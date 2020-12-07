package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IRun;

/**
 * This interface describes the set of methods that any Test Run Listener must implement.
 * <p>
 * These methods are invoked when a Test Run has been added, removed (marked as deleted), testing completed, or updated in the contest. 
 * A client utilizing the PC<sup>2</sup> API can implement this interface and add
 * itself to the contest as a Test Run Listener, and therefore arrange to be notified 
 * when any Test Runs are added to, modified, or removed from the contest.
 * <P>
 * 
 * Test Run Flow is: 
 * <ol>
 * <li> {@link #testRunSubmitted(IRun)} </li>
 * <li> {@link #testRunTestingCompleted(IRun)} when test run results are available. </li>
 * </ol>
 * <br>
 * <br>
 * 
 */
public interface ITestRunListener {

    /**
     * Invoked when a new Test Run has been added to the contest.
     * <P>
     * Typically this means that a Test Run has been submitted by a team;
     * it may also may be caused by a remote server in a multi-site contest sending its run(s) to the local server.
     * <P>
     * The added Test Run may have been originally entered into the contest on either the local server (the server to which 
     * this client is connected) or on a remote server.
     * 
     * @param run
     *            the {@link IRun} that has been added to the contest
     */
    void testRunSubmitted(IRun run);

    /**
     * Invoked when an existing Test Run has been deleted from the contest (marked as deleted by the Contest Administrator).
     * 
     * @param run
     *            the deleted {@link IRun}
     */
    void testRunDeleted(IRun run);

    /**
     * Invoked when an existing Test Run has been updated (modified) in some way.
     * <P>
     * Typically a <code>runUpdated()</code> invocation occurs when either
     * <ul>
     *      <li>The Contest Administrator changes a setting (such as the elapsed time or the assigned {@link edu.csus.ecs.pc2.api.IJudgement}) in the test run.</li>
     * </ul>
     * 
     * @param run
     *            the {@link IRun} which has been changed
     * @param isFinal true if this is a action for a final Judgement (not applicable for Test Runs)
     */
    void testRunUpdated(IRun run, boolean isFinal);

    /**
     * Invoked when the Test Run done and results are available.
     * 
     * @see IRun#getTestRunResults()
     * @param run the {@link IRun} which has been tested that contains resting results.
     */
    void testRunTestingCompleted(IRun run);

}
