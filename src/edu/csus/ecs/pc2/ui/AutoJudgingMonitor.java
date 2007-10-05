package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Auto Judge Monitor.
 * 
 * This class will auto judge runs. It will update the status frame as the state of judging runs changes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgingMonitor implements UIPlugin {

    private IContest contest;

    private IController controller;

    private AutoJudgeStatusFrame autoJudgeStatusFrame = new AutoJudgeStatusFrame();

    private boolean alreadyJudgingRun = false;

    private Log log;

    private Run runToCheckout = null;

    /**
     * 
     */
    private static final long serialVersionUID = 2774495762012789107L;

    public void setContestAndController(IContest inContest, IController inController) {
        contest = inContest;
        controller = inController;

        log = controller.getLog();

        autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
        autoJudgeStatusFrame.updateMessage("(Still waiting)");
        attemptToFetchNextRun(); // START trying to auto judge now
    }

    public String getPluginTitle() {
        return "Auto Judging Monitor";
    }

    /**
     * Searches run database for run to auto judge.
     * 
     * @return null if nothing to auto judge, otherwise Run
     */
    public Run findNextAutoJudgeRun() {

        Filter filter = null;

        ClientSettings clientSettings = contest.getClientSettings();
        if (clientSettings != null && clientSettings.isAutoJudging()) {
            filter = clientSettings.getAutoJudgeFilter();
        }

        if (filter == null) {
            log.log(Log.WARNING, contest.getClientId() + " has no problems selected to auto judge (filter is null)");
            return null;
        }

        if (!isAutoJudgeOn()) {
            log.log(Log.WARNING, contest.getClientId() + " does not have auto judging turned on");
            return null;
        }

        Run[] runs = contest.getRuns();

        for (Run run : runs) {
            if (run.getStatus() == RunStates.NEW) {
                if (filter.matches(run)) {
                    return run;
                }
            }
        }

        return null;
    }

    /**
     * Is Auto Judging turned On for this judge ?
     * 
     * @return
     */
    private boolean isAutoJudgeOn() {
        ClientSettings clientSettings = contest.getClientSettings();
        if (clientSettings != null && clientSettings.isAutoJudging()) {
            return true;
        }

        return false;
    }

    /**
     * Is this run to be auto judged ?
     * 
     * @param run
     * @return
     */
    private boolean isRunToBeAutoJudged(Run run) {
        
        if (run == null){
            return false;
        }

        if (!isAutoJudgeOn()) {
            return false;
        }

        // Check for validator defined

        Problem problem = contest.getProblem(run.getProblemId());

        if (problem == null || (!problem.isValidatedProblem())) {
            log.log(Log.INFO, "Problem has no validator - can't auto judge " + run);
            return false;
        }

        // Check for whether this problem is selected to be auto judged

        ClientSettings clientSettings = contest.getClientSettings();
        if (clientSettings != null && clientSettings.isAutoJudging()) {
            if (clientSettings.getAutoJudgeFilter() != null) {
                return clientSettings.getAutoJudgeFilter().matches(run);
            }
        }
        return false;
    }

    /**
     * 
     * @param run
     */
    private void checkoutNextRun(Run run) {
        setAlreadyJudgingRun(true);
        runToCheckout = run;
        autoJudgeStatusFrame.updateStatusLabel("Fetching Run " + run.getNumber() + " (Site " + run.getSiteNumber() + ")");
        autoJudgeStatusFrame.updateMessage(getRunDescription(run));
        
        // TODO send run request to server
    }

    private String getRunDescription(Run runToCheckOut) {
        // ## - Problem Title (Run NN, Site YY)
        return contest.getProblem(runToCheckOut.getProblemId()).getDisplayName() + " (Run " + runToCheckOut.getNumber() + " Site " + runToCheckOut.getSiteNumber();
    }

    protected boolean isAlreadyJudgingRun() {
        return alreadyJudgingRun;
    }

    protected void setAlreadyJudgingRun(boolean alreadyJudgingRun) {
        this.alreadyJudgingRun = alreadyJudgingRun;
    }

    /**
     * Class listens for runs and auto judges.
     * 
     * Auto judges run if run is to be auto judged.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            attemptToFetchNextRun(event.getRun());
        }

        public void runChanged(RunEvent event) {
            checkCheckedOutRun(event);
        }

        public void runRemoved(RunEvent event) {
            // ignored
        }
    }

    /**
     * Check out run from the server.
     * 
     * @param event
     */
    public void checkCheckedOutRun(RunEvent event) {

        if (event.getAction().equals(RunEvent.Action.CHECKEDOUT_RUN)) {
            // checked out run
            if (event.getSentToClientId().equals(contest.getClientId())) {
                // we checked out the run, let's try to judge it.

                if (runToCheckout != null) {
                    if (event.getRun().getElementId().equals(runToCheckout.getElementId())) {
                        executeAndAutoJudgeRun(event.getRun(), event.getRunFiles());
                    }
                }

            }
        } else if (event.getAction().equals(RunEvent.Action.RUN_NOT_AVIALABLE)) {
            if (runToCheckout.getElementId().equals(event.getRun().getElementId())) {
                // Darn we weren't fast enough
                setAlreadyJudgingRun(false);
                runToCheckout = null;

                attemptToFetchNextRun();
            }

        }
    }

    /**
     * Judge checked out run.
     * 
     * @param run
     * @param runFiles
     */
    private void executeAndAutoJudgeRun(Run run, RunFiles runFiles) {

        setAlreadyJudgingRun(true);

        autoJudgeStatusFrame.updateMessage(getRunDescription(run));
        autoJudgeStatusFrame.updateStatusLabel("Received run");

        try {

            autoJudgeStatusFrame.updateStatusLabel("Judging run");

        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
        }

        // TODO send judgement back to server.

        // JudgementRecord judgementRecord = pendingJudgements.get(run.getElementId());
        // if (judgementRecord != null) {
        // getController().submitRunJudgement(run, judgementRecord, null);
        // pendingJudgements.remove(run.getElementId());
        // }
        autoJudgeStatusFrame.updateStatusLabel("Sent judgement to server");

        setAlreadyJudgingRun(false);

        attemptToFetchNextRun();

    }

    /**
     * Find next auto judge run and fetch it.
     */
    private void attemptToFetchNextRun() {
        attemptToFetchNextRun (findNextAutoJudgeRun());
    }
    
    /**
     * Attempt to fetch next run to be judged.
     * @param nextRun
     */
    private void attemptToFetchNextRun(Run nextRun) {
            
        if (isRunToBeAutoJudged(nextRun)) {
            // There is ANOTHER run to judge!! Yes!

            try {
                // TODO get this setting from the contest property
                Thread.sleep(2500);
            } catch (Exception e) {
                log.log(Log.WARNING, "Exception logged ", e);
            }

            checkoutNextRun(nextRun);

        } else {
            autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
            autoJudgeStatusFrame.updateMessage("(Still waiting)");
        }
    }
}
