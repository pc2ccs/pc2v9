package edu.csus.ecs.pc2.ui;

import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientSettingsEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IClientSettingsListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Auto Judge Monitor.
 * 
 * This class will auto judge runs. It will update the status frame as the state of judging runs changes.
 * <P>
 * The auto judging monitor starts when the {@link #startAutoJudging()} is invoked. Auto judging will only occur if autojudging is turned on in ClientSettings.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgingMonitor implements UIPlugin {

    private IInternalContest contest;

    private IInternalController controller;

    private AutoJudgeStatusFrame autoJudgeStatusFrame = new AutoJudgeStatusFrame();

    private boolean currentlyAutoJudging = false;

    private Log log;

    /**
     * 
     */
    private Run runBeingAutoJudged = null;

    private RunFiles fetchedRunFiles = null;

    private Run fetchedRun = null;

    private Boolean listening = new Boolean(true);

    private Executable executable;

    /**
     * This is an entirely local value.
     */
    private boolean autoJudgeDisabledLocally = false;

    private boolean answerReceived = false;

    // private edu.csus.ecs.pc2.ui.AutoJudgingMonitor.FetchRunListenerImplemenation fetchRunListenerImplemenation;

    /**
     * 
     */
    private static final long serialVersionUID = 2774495762012789107L;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;

        log = controller.getLog();

        autoJudgeStatusFrame.setAutoJudgeMonitor(this);

        contest.addRunListener(new RunListenerImplementation());
        contest.addClientSettingsListener(new ClientSettingsListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                autoJudgeStatusFrame.setTitle("Auto Judge Status " + contest.getClientId().getName());
            }
        });

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
            info(contest.getClientId() + " has no problems selected to auto judge (filter is null)");
            return null;
        }

        if (!isAutoJudgingEnabled()) {
            info(contest.getClientId() + " does not have auto judging turned on");
            return null;
        }

        Run[] runs = contest.getRuns();

        for (Run run : runs) {
            if (run.getStatus() == RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT) {
                if (filter.matches(run)) {
                    return run;
                }
            }
        }

        return null;
    }

    /**
     * Is the auto judge configuration turned On?.
     * 
     * @return true if not locally disabled or disabled via admin.
     */
    private boolean isAutoJudgingEnabled() {

        if (autoJudgeDisabledLocally) {
            return false;
        }

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

        if (run == null) {
            return false;
        }

        // Check for validator defined

        Problem problem = contest.getProblem(run.getProblemId());

        if (problem == null) {
            log.log(Log.WARNING, "Problem null on for " + run);
            return false;
        }

        if (!problem.isValidatedProblem()) {
            log.log(Log.WARNING, "Problem has no validator defined, can't be auto judged, run: " + run);
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

    private String getRunDescription(Run runToCheckOut) {
        // ## - Problem Title (Run NN, Site YY)
        return " Run " + runToCheckOut.getNumber() + " Site " + runToCheckOut.getSiteNumber() + " - " + contest.getProblem(runToCheckOut.getProblemId()).getDisplayName();
    }

    protected boolean isCurrentlyAutoJudging() {
        return currentlyAutoJudging;
    }

    protected void setCurrentlyAutoJudging(boolean alreadyJudgingRun) {
        this.currentlyAutoJudging = alreadyJudgingRun;
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
            if (event.getRun().getStatus().equals(RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT)) {
                attemptToFetchNextRun(event.getRun());
            }
        }

        public void runChanged(RunEvent event) {

            if (runBeingAutoJudged != null && event.getRun().getElementId().equals(runBeingAutoJudged.getElementId())) {
                // found the run we requested

                if (fetchedRun == null) {
                    fetchedRunFiles = event.getRunFiles();
                    fetchedRun = event.getRun();

                    synchronized (listening) {
                        try {
                            answerReceived = true;
                            listening.notify();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    log.info("Currently judging run " + fetchedRun);
                }

            }
        }

        public void runRemoved(RunEvent event) {
            // ignored
        }
    }

    /**
     * Reset from last judged run and fetch next run.
     */
    private void cleanupLastAutoJudge() {

        runBeingAutoJudged = null;
        fetchedRun = null;
        fetchedRunFiles = null;
        autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
        autoJudgeStatusFrame.updateMessage("(Still waiting)");
        setCurrentlyAutoJudging(false);

        attemptToFetchNextRun();
    }

    /**
     * Judge checked out run.
     * 
     * @param run
     * @param runFiles
     */
    private void executeAndAutoJudgeRun() {

        setCurrentlyAutoJudging(true);

        autoJudgeStatusFrame.setVisible(true);

        autoJudgeStatusFrame.updateMessage(getRunDescription(fetchedRun));
        autoJudgeStatusFrame.updateStatusLabel("Received run");

        try {

            autoJudgeStatusFrame.updateStatusLabel("Judging run");

        } catch (Exception e) {
            info("Exception logged ", e);
        }

        System.gc();

        executable = new Executable(contest, controller, fetchedRun, fetchedRunFiles);

        // Suppress pop up messages on errors
        executable.setShowMessageToUser(false);

        autoJudgeStatusFrame.updateMessage(getRunDescription(fetchedRun));

        executable.execute();

        ExecutionData executionData = executable.getExecutionData();

        RunResultFiles runResultFiles = null;

        JudgementRecord judgementRecord = null;

        try {

            if (executionData.getExecutionException() != null) {
                autoJudgeStatusFrame.updateStatusLabel("ERROR - " + executionData.getExecutionException().getMessage());
                log.log(Log.WARNING, "ERROR - " + executionData.getExecutionException().getMessage(), "ERROR - " + executionData.getExecutionException());
                // judgementRecord stays null

            } else if (!executionData.isCompileSuccess()) {
                // Compile failed, darn!

                autoJudgeStatusFrame.updateStatusLabel("Run failed to compile");

                ElementId elementId = contest.getJudgements()[1].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
                // TODO this needs to be flexible
                judgementRecord.setValidatorResultString("No - Compilation Error");

            } else if (executionData.isValidationSuccess()) {

                // We got stuff from validator!!
                String results = executable.getValidationResults();
                if (results == null) {
                    results = "Undetermined";
                }
                if (results.trim().length() == 0) {
                    results = "Undetermined";
                }

                boolean solved = false;

                // Try to find result text in judgement list
                ElementId elementId = contest.getJudgements()[1].getElementId();
                for (Judgement judgement : contest.getJudgements()) {
                    if (judgement.getDisplayName().equals(results)) {
                        elementId = judgement.getElementId();
                    }
                }

                // Or perhaps it is a yes? yes?
                Judgement yesJudgement = contest.getJudgements()[0];
                // bug 280 ICPC Validator Interface Standard calls for "accepted" in any case.
                if (results.trim().equalsIgnoreCase("accepted")) {
                    results = yesJudgement.getDisplayName();
                }
                if (yesJudgement.getDisplayName().equalsIgnoreCase(results)) {
                    elementId = yesJudgement.getElementId();
                    solved = true;
                }

                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), solved, true, true);
                judgementRecord.setValidatorResultString(results);

            } else {
                // Something went wrong either during validation or execution
                // Unable to validate result: Undetermined

                info("Run compiled but failed to validate " + fetchedRun);

                ElementId elementId = contest.getJudgements()[1].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
                judgementRecord.setValidatorResultString("Undetermined");

            }

        } catch (Exception e) {
            info("Exception during execute/validating run " + fetchedRun, e);
        }

        if (judgementRecord == null) {

            info("Problem judging run " + fetchedRun + " unable to create judgement record");
            autoJudgeStatusFrame.updateStatusLabel("Problem judging run");
            // Cancel the run, hope for better luck.

            autoJudgeStatusFrame.updateStatusLabel("Returning run to server");
            controller.cancelRun(runBeingAutoJudged);

            cleanupLastAutoJudge();

        } else {

            info("Sending judgement to server " + fetchedRun);
            autoJudgeStatusFrame.updateStatusLabel("Sending judgement to server");

            runResultFiles = new RunResultFiles(fetchedRun, fetchedRun.getProblemId(), judgementRecord, executable.getExecutionData());

            controller.submitRunJudgement(fetchedRun, judgementRecord, runResultFiles);

            cleanupLastAutoJudge();
        }

    }

    /**
     * Find next run to auto judge.
     */
    private void attemptToFetchNextRun() {
        attemptToFetchNextRun(findNextAutoJudgeRun());
    }

    /**
     * Attempt to fetch a new run.
     * 
     * Will not fetch a new run if auto judging is turned off, or if already auto judging.
     * 
     * @param nextRun
     */
    private void attemptToFetchNextRun(Run nextRun) {

        if (!isAutoJudgingEnabled()) {
            // Auto judging is turned OFF no need to fetch a new run
            return;
        }

        if (isCurrentlyAutoJudging()) {
            // Already judging run, no need to fetch a new run
            return;
        }

        if (isRunToBeAutoJudged(nextRun)) {

            runBeingAutoJudged = nextRun;
            fetchRun(nextRun);

            if (fetchedRun != null) {
                info("Fetched run " + fetchedRun);
                executeAndAutoJudgeRun();
            } else {
                info("Unable to fetch run " + nextRun);
            }

        } else {
            autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
            autoJudgeStatusFrame.updateMessage("(Still waiting)");
        }
    }

    private void fetchRun(Run run) {

        setCurrentlyAutoJudging(true);
        runBeingAutoJudged = run;
        autoJudgeStatusFrame.updateStatusLabel("Fetching Run " + run.getNumber() + " (Site " + run.getSiteNumber() + ")");
        autoJudgeStatusFrame.updateMessage(getRunDescription(run));

        controller.checkOutRun(run, false, true);

        answerReceived = false;

        synchronized (listening) {
            while (!answerReceived) {
                try {
                    listening.wait();
                } catch (InterruptedException e) {
                    // ok, just loop again
                    listening.booleanValue(); // terrible kludge because empty block not allowed.
                }
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ClientSettingsListenerImplementation implements IClientSettingsListener {

        public void clientSettingsAdded(ClientSettingsEvent event) {
            clientSettingsChanged(event);
        }

        public void clientSettingsChanged(ClientSettingsEvent event) {
            updateClientSettings(event.getClientSettings());
        }

        public void clientSettingsRemoved(ClientSettingsEvent event) {
            // TODO Auto-generated method stub
        }

    }

    /**
     * If settings are updated then attempt to start autojuding.
     * 
     * @param clientSettings
     */
    public void updateClientSettings(ClientSettings clientSettings) {
        if (clientSettings.getClientId().equals(contest.getClientId())) {
            startAutoJudging();
        }
    }

    /**
     * Start Auto Judging, if enabled,, update status message.
     */
    public void startAutoJudging() {

        if (isAutoJudgingEnabled()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
                    autoJudgeStatusFrame.updateMessage("(Still waiting)");
                }
            });

            attemptToFetchNextRun();

        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    autoJudgeStatusFrame.updateStatusLabel("Auto-judging is OFF");
                    autoJudgeStatusFrame.updateMessage("");
                }
            });
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                autoJudgeStatusFrame.setVisible(true);
            }
        });
    }

    public void stopAutoJudging() {
        if (isCurrentlyAutoJudging()) {

            // Cancel the run
            if (runBeingAutoJudged != null) {
                controller.cancelRun(runBeingAutoJudged);
            }
            cleanupLastAutoJudge();
        }

        setAutoJudgeDisabledLocally(true);

        autoJudgeStatusFrame.updateStatusLabel("Auto-judging is OFF");
        autoJudgeStatusFrame.updateMessage("");
    }

    public void info(String s) {
        controller.getLog().warning(s);
        // System.err.println(Thread.currentThread().getName() + " " + s);
        // System.err.flush();
    }

    public void info(String s, Exception exception) {
        controller.getLog().log(Log.WARNING, s, exception);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
        exception.printStackTrace(System.err);
    }

    /**
     * Has the user turned auto judging off locally?.
     * 
     * @return true if the auto judge has been turned off locally, else false
     */
    public boolean isAutoJudgeDisabledLocally() {
        return autoJudgeDisabledLocally;
    }

    public void setAutoJudgeDisabledLocally(boolean autoJudgeDisabledLocally) {
        this.autoJudgeDisabledLocally = autoJudgeDisabledLocally;
    }
}
