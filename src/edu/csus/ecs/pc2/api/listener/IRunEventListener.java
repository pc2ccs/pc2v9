package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IRun;

/**
 * Set of methods that any Run Listener must implement.
 *  
 * These are the events/methods invoked when a run has been added, removed
 * or updated.
 * <P>
 * See {@link IContest#getRuns()} for an example of use.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public interface IRunEventListener {

    /**
     * A run has been added to the system.
     * <P>
     * Typically this means that a run has been submitted by a team. Though it also may be caused by a remote server sending its runs to the local server.
     * <P>
     * The run may be added on the local server or a remote server.
     * 
     * @param run
     *            the run that has changed
     */
    void runAdded(IRun run);

    /**
     * Run has been removed (marked as deleted).
     * <P>
     * Typically this happens when an Admin marks a run as deleted.
     * 
     * @param run
     *            deleted run
     */
    void runRemoved(IRun run);

    /**
     * Run judged.
     * 
     * Typically this happens when a judge judges a run.
     * 
     * @param run
     *            the judged run
     */
    void runJudged(IRun run);

    /**
     * Run updated or rejudged.
     * <P>
     * Triggered when a judge re-judges a run.
     * Triggered when the admin changes a run (like changes the elapsed time) or changes a judgement.
     * 
     * @param run
     *            the changed run
     */
    void runUpdated(IRun run);

}
