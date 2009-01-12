package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IRun;

/**
 * This interface describes the set of methods that any Run Listener must implement.
 * <p>
 * These methods are invoked when a run has been added, removed (marked as deleted), judged, or updated in the contest. A client utilizing the PC<sup>2</sup> API can implement this interface and add
 * itself to the contest as a Listener, and therefore arrange to be notified when any runs are added to, modified, or removed from the contest.
 * <P>
 * 
 * <B>There is no guarantee that these events will appear in the order described below.</B>
 * The system is a asynchronous system (Threads) and many factors can affect the
 * order which these events occur.
 * <P>
 * 
 * Run Flow - if Admin selects "Send Additional Run Status Information" to OFF 
 * <ol>
 * <li> {@link #runSubmitted(IRun)}
 * <li> {@link #runJudged(IRun)} typically, but also can be: {@link #runJudgingCanceled(IRun)} or {@link #runDeleted(IRun)} or {@link #runUpdated(IRun)}.
 * </ol>
 * 
 * Run Flow - if Admin selects "Send Additional Run Status Information" to ON.
 * <ol>
 * <li> {@link #runSubmitted(IRun)}
 * <li> {@link #runCompiling(IRun)}
 * <li> {@link #runExecuting(IRun)} 
 * <li> {@link #runValidating(IRun)}
 * <li> {@link #runJudged(IRun)} typically, but also can be: {@link #runJudgingCanceled(IRun)} or {@link #runDeleted(IRun)} or {@link #runUpdated(IRun)}.
 * </ol>
 * <br>
 * <B>Note that the events {@link #runCompiling(IRun)}, {@link #runExecuting(IRun)} and {@link #runValidating(IRun)} can
 * appear in any order</B>
 * <br>
 * <br>
 * Note 2 - {@link #runExecuting(IRun)} and {@link #runValidating(IRun)} are optional, if the preceeding
 * step fails or does not create output then these states may never be reached.
 * 
 * <p>
 * This documentation describes the current <I>draft</i> of the PC<sup>2</sup> API, which is subject to change.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface IRunEventListener {

    /**
     * Invoked when a new run has been added to the contest.
     * <P>
     * Typically this means that a run has been submitted by a team;
     * it may also may be caused by a remote server in a multi-site contest sending its run(s) to the local server.
     * <P>
     * The added run may have been originally entered into the contest on either the local server (the server to which 
     * this client is connected) or on a remote server.
     * 
     * @param run
     *            the {@link IRun} that has been added to the contest
     */
    void runSubmitted(IRun run);

    /**
     * Invoked when an existing run has been deleted from the contest (marked as deleted by the Contest Administrator).
     * 
     * @param run
     *            the deleted {@link IRun}
     */
    void runDeleted(IRun run);

    /**
     * Invoked when an existing run has been judged; that is, has had a Judgement applied to it.
     * 
     * Typically this happens when a judge assigns a Judgement, but it can also occur
     * as a result of an &quot;automated judgement&quot; (also known as a &quot;validator&quot;)
     * applying a judgement automatically.  
     * 
     * @param run
     *            the judged {@link IRun}
     */
    void runJudged(IRun run);

    /**
     * Invoked when an existing run has been updated (modified) in some way.
     * <P>
     * Typically a <code>runUpdated()</code> invocation occurs when either
     * <ul>
     *      <li>A judge re-judges a run; or </li>
     *      <li>The Contest Administrator changes a setting (such as the elapsed time or the assigned {@link edu.csus.ecs.pc2.api.IJudgement}) in the run.</li>
     * </ul>
     * 
     * @param run
     *            the {@link IRun} which has been changed
     */
    void runUpdated(IRun run);
    
    /**
     * Invoked when an existing run is being compiled.
     * 
     * @param run
     *            the {@link IRun} which is being compiled.
     */
    void runCompiling(IRun run);

    /**
     * Invoked when an existing run is being executed.
     * 
     * @param run
     *            the {@link IRun} which is being executed.
     */
    void runExecuting(IRun run);

    /**
     * Invoked when an existing run is being validated.
     * 
     * @param run
     *            the {@link IRun} which is being validated.
     */
    void runValidating(IRun run);

    /**
     * Invoked if the run is re-queued for judgement.
     * 
     * If a judge has checked out a run then cancels (returns the run to
     * the set of runs to be judged) then this method will be invoked.
     * 
     * @param run
     *            the {@link IRun} which has been returned to the judging queue.
     */
    void runJudgingCanceled(IRun run);
}
