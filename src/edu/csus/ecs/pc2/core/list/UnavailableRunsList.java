package edu.csus.ecs.pc2.core.list;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.UnavailableRun;
import edu.csus.ecs.pc2.ui.AutoJudgingMonitor;

/**
 * This class maintains a list of unavailable runs -- that is, runs which an AutoJudge has previously requested to check out (because it 
 * found the run in the QUEUED_FOR_COMPUTER_JUDGEMENT state), but for which it received back a RUN_NOTAVAILABLE response (packet).
 * 
 * The list of unavailable runs is managed as a {@link PriorityBlockingQueue}.  Note: class {@link PriorityBlockingQueue} 
 * is used rather than {@link PriorityQueue} because the former is thread-safe whereas the latter is not.
 * 
 * The priority of a queue element (unavailable run) is based on an "expiration time" -- the contest elapsed time (in seconds) at which the run should 
 * no longer be considered "unavailable".  Runs with earlier expiration times appear closer to the head of the queue.  When a run is inserted into 
 * the queue (by calling method {@link #addRun(Run run)}), that method computes an "expiration time" (the contest time at which the run becomes a 
 * candidate for removal from the list), which then becomes the "priority" of that unavailable run.  
 * Runs whose "expiration time" has arrived (or passed) become candidates for removal from the list. 
 * 
 * The purpose of the "expiration time" of an unavailable run is to ensure that at some point an AJ will go back and
 * consider runs which were PREVIOUSLY unavailable but which might now be legitimately available.  This could happen for example if some
 * other judge, or an Admin, grabbed the run while a given AJ was requesting it, but then the other judge/Admin returned the run unjudged.
 * It could also happen due to an unknown bug in the system.
 * The "expiration timeout" is thus in effect a "fail-safe" mechanism to ensure that if a run somehow was marked "unavailable" but then
 * somehow later got legitimately returned to the QUEUED_FOR_COMPUTER_JUDGEMENT state, the AJ will (eventually) attempt to judge it once again.
 * 
 * This class tracks the <I>number of times</i> a given run gets inserted into the UnavailableRunsList; the more times a given
 * Run gets inserted, the longer (further in the future) its computed "expiration time" will be (see method {@link #addRun(Run)} for details).
 * 
 * Note that the notion of a "list of unavailable runs" is strictly associated with AutoJudging; "human" or "manual" judging should never
 * be making any use of this UnavailableRunsList class.  The purpose of the class is to deal with situations
 * where, in the current PC2 distribution, an AJ can (for currently unknown/unexplained reasons) find a run in the QUEUED_FOR_COMPUTER_JUDGEMENT
 * state but when it sends a RUN_CHECKOUT request it gets back a RUN_NOTAVAILABLE response.  Note that this actually SHOULDN'T EVER HAPPEN;
 * however, it DOES in fact happen under some (currently unknown) condition in the system.
 * The list managed by this class was created as a (hopefully, temporary) workaround for the issue of AJ's repeatedly 
 * requesting the same unavailable run ad infinitum -- something which has been seen in various contests.  
 * See https://github.com/pc2ccs/pc2v9/issues/480 for details.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class UnavailableRunsList {
    
    public static long INITIAL_EXPIRATION_TIME_OFFSET_SECS = 60;
    
    //a Priority Queue holding the unavailable runs, ordered by increasing "expiration time".
    //the priority queue has an initial capacity of 11 (the default) and uses an UnavailableRunComparatorByExpirationTime for determining priority.
    private PriorityBlockingQueue<UnavailableRun> unavailableRuns = new PriorityBlockingQueue<UnavailableRun>(11, new UnavailableRunComparatorByExpirationTime());
    
    //a hashtable to keep track of how many times any given run has ever been added to the UnavailableRunsList.
    // This allows computing increased expiration times the more times a given run is found "unavailable".
    private Hashtable<ElementId,Integer> allAddedRuns = new Hashtable<ElementId,Integer>();
    
    private IInternalContest contest;
    private Log log;
    
    /**
     * Constructs an (initially-empty) UnavailableRunsList for the specified IInternalContest.
     * 
     * @param contest the IInternalContest on which this UnavailableRunsList is based.
     * @param controller the IInternalController for the contest, used to obtain the Log to be used by this class.
     * 
     */
    public UnavailableRunsList (IInternalContest contest, IInternalController controller) {
        this.contest = contest;
        if (controller != null) {
            this.log = controller.getLog();
        }
    }
    
    /**
     * Adds the specified Run to the list of unavailable runs.
     * 
     * Each run which is added to the list is given an "expiration time" -- the contest elapsed time (in seconds) when the run 
     * is no longer to be treated as "unavailable".  The expiration time of a run on the list is a function of both
     * the time it is put on the list and how many times (if any) it has PREVIOUSLY been put on the list; runs which 
     * have been added to the list previously are given increasingly longer (further in the future) expiration times.
     * 
     * Note that runs are not "automatically" removed from the list when their "expiration time" has arrived; a call to
     * method {@link #removeExpiredRuns()} must be made to cause expired runs to be removed
     * (see method {@link AutoJudgingMonitor#findNextAutoJudgeRun()}). 
     * 
     * @param run the Run to be added to the list.
     */
    public void addRun(Run run) {
        
        if (contest==null) {
            throw new RuntimeException("UnavailableRunsList contains null contest");
        }
        
        if (run==null) {
            if (log!=null) {
                log.warning("Attempted to add a null run to the UnavailableRunsList!");
            } else {
                throw new RuntimeException("Attempted to add a null run to the UnavailableRunsList, and no log is available for logging!!");
            }
        }
        
        //find the number of times the run has previously been added to the UnavailableRunsList (if any).
        //  (Note that we're checking the hashtable of ALL runs which have EVER been added to the UnavailableRunsList, 
        //  not the current UnavailableRunsList.) The "allAddedRuns" hashtable maps Runs (via their ElementId) to a count 
        //  of the number of times that run has ever been added to the UnavailableRunsList.
        int timesAddedToRunList;
        if (allAddedRuns.containsKey(run.getElementId())) {
            
            //the run has previously been added; get the count of number of times it has been added previously
            timesAddedToRunList = allAddedRuns.get(run.getElementId());
            
            //indicate another addition is occurring
            timesAddedToRunList++ ;
            allAddedRuns.put(run.getElementId(), timesAddedToRunList);
            
        } else {
            
            //this is the first time the run has been added
            allAddedRuns.put(run.getElementId(),  1);
            timesAddedToRunList = 1;
        }
        
        //compute how long past "now", as a function of number of times already added, the run should "expire" on the UnavailableRunsList.
        // (Note that the current calculation is a LINEAR increase in time; it would probably be better to have something like an EXPONENTIAL
        //   increase.  However, since this entire class is (hopefully) temporary, I didn't spend any more time on that... jlc)
        long contestTimeNowInSecs = contest.getContestTime().getElapsedSecs();
        long expirationTimeInSecs = contestTimeNowInSecs + (INITIAL_EXPIRATION_TIME_OFFSET_SECS * timesAddedToRunList) ;
        long expirationLengthInSecs = expirationTimeInSecs - contestTimeNowInSecs;
        
        //add the run to the priority queue (note that method add(run) adds the run into the queue in priority order based on the expiration time in the run)
        UnavailableRun runToAdd = new UnavailableRun(run, expirationTimeInSecs);
        unavailableRuns.add(runToAdd);
        
        if (log!=null) { 
            log.info("Added run " + run.getNumber() + " from site " + run.getSiteNumber() + " to UnavailableRuns list (times added = " + timesAddedToRunList 
                            + "; time until expiration = " + expirationLengthInSecs + " secs); list size = " + unavailableRuns.size());
        } else {
            throw new RuntimeException("Added run " + run.getNumber() + " from site " + run.getSiteNumber() + " to UnavailableRuns list (times added = " + timesAddedToRunList 
                            + "; time until expiration = " + expirationLengthInSecs + " secs; list size = " + unavailableRuns.size()+ ") but no log was available for logging!!");            
        }
    }
    
    /**
     * This method returns an indication of whether the specified run is currently contained in the list of "unavailable runs".
     * 
     * @param run the run to be looked for in the UnavailableRuns list.
     * @return true if the run is in the unavailable runs list; false otherwise.
     */
    public boolean contains(Run run) {
        
        //get an iterator over all the Unavailable Runs, without actually removing any of them from the priority queue        
        Iterator<UnavailableRun> iterator = unavailableRuns.iterator();
        
        //check each UnavailableRun to see if it matches the received Run
        UnavailableRun nextRun;
        while (iterator.hasNext()) {
            nextRun = iterator.next();
            if (nextRun != null  &&  nextRun.getRun().getElementId().equals(run.getElementId())) {
                //we found the specified Run in the list
                return true;
            }
        }
        
        //after checking all the runs in the queue (iterator) we didn't find the specified run
        return false;
    }
    
    /**
     * This method goes through the list of unavailable runs and removes any runs whose "expiration time" has passed.
     * Unavailable Runs are ordered in the queue based on their expiration time in contest elapsed seconds; what this
     * method does is remove from the UnavailableRuns list all runs at the head of the queue with an expiration time
     * less that the current contest elapsed time. 
     */
    public void removeExpiredRuns() {
        
        if (contest==null) {
            throw new RuntimeException("UnavailableRunsList contains null contest");
        }
        
        long timeNow = contest.getContestTime().getElapsedSecs();
        
        //repeatedly look at the run at the head of the queue to see if its expiration time has passed
        while ( (unavailableRuns.peek()!=null) && (unavailableRuns.peek().getExpirationTimeInSecs() < timeNow) ) {
            
            //the run at the head of the queue has an expiration time which has passed; remove it from the queue
            // (note that method "poll()" is the PriorityBlockingQueue method to remove the head of the queue without blocking if the queue is empty)
            UnavailableRun removedRun = unavailableRuns.poll();
            
            //log the removal
            if (log!=null) {
                log.info("Removed run " + removedRun.getRun().getNumber() + " from UnavailableRuns list");
            }
        }
    }
}
