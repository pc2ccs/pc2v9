package edu.csus.ecs.pc2.ui;

import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientSettingsEvent;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IClientSettingsListener;
import edu.csus.ecs.pc2.core.model.IContest;
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

    private IContest contest;

    private IController controller;

    private AutoJudgeStatusFrame autoJudgeStatusFrame = new AutoJudgeStatusFrame();

    private boolean currentlyAutoJudging = false;

    private Log log;

    /**
     * 
     */
    private Run runBeingAutoJudged = null;

    private Executable executable;

    /**
     * This is an entirely local value.
     */
    private boolean autoJudgeDisabledLocally = false;

    /**
     * 
     */
    private static final long serialVersionUID = 2774495762012789107L;

    public void setContestAndController(IContest inContest, IController inController) {
        contest = inContest;
        controller = inController;

        log = controller.getLog();

        autoJudgeStatusFrame.setAutoJudgeMonitor(this);

        contest.addRunListener(new RunListenerImplementation());
        contest.addClientSettingsListener(new ClientSettingsListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                autoJudgeStatusFrame.setTitle("Auto Judge Status "+contest.getClientId().getName());
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

        if (!isAutoJudgeOn()) {
            info(contest.getClientId() + " does not have auto judging turned on");
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
     * Should monitor auto judge?
     * 
     * 
     * 
     * @return
     */
    private boolean isAutoJudgeOn() {

        if (autoJudgeDisabledLocally) {
            return false;
        }
        if (isCurrentlyAutoJudging()) {
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
        setCurrentlyAutoJudging(true);
        runBeingAutoJudged = run;
        autoJudgeStatusFrame.updateStatusLabel("Fetching Run " + run.getNumber() + " (Site " + run.getSiteNumber() + ")");
        autoJudgeStatusFrame.updateMessage(getRunDescription(run));

        try {
            controller.checkOutRun(run, false);
        } catch (Exception e) {
            info("Could not check out run " + run + " waiting again... ", e);
            setCurrentlyAutoJudging(false);
            runBeingAutoJudged = null;
            autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
            autoJudgeStatusFrame.updateMessage("(Still waiting)");
        }
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
            if (contest.getClientId().equals(event.getSentToClientId())) {
                // we checked out the run, let's try to judge it.

                if (runBeingAutoJudged != null) {
                    if (event.getRun().getElementId().equals(runBeingAutoJudged.getElementId())) {
                        executeAndAutoJudgeRun(event.getRun(), event.getRunFiles());
                    } else {
                        info("Run we got was for us but the wrong run?? " + event.getRun());
                    }
                } else {
                    info("Got run for us " + event.getRun() + " but no AJ run to be checked out");
                }
            } else {
                info("Ignoring run " + event.getRun() + " was for judge " + event.getRun());
            }
        } else if (event.getAction().equals(RunEvent.Action.RUN_NOT_AVIALABLE)) {
            if (runBeingAutoJudged.getElementId().equals(event.getRun().getElementId())) {
                // Darn we weren't fast enough
                setCurrentlyAutoJudging(false);
                runBeingAutoJudged = null;

                info(event.getAction() + " for run " + event.getRun());

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

        setCurrentlyAutoJudging(true);

        autoJudgeStatusFrame.setVisible(true);

        autoJudgeStatusFrame.updateMessage(getRunDescription(run));
        autoJudgeStatusFrame.updateStatusLabel("Received run");

        try {

            autoJudgeStatusFrame.updateStatusLabel("Judging run");

        } catch (Exception e) {
            info("Exception logged ", e);
        }

        System.gc();

        executable = new Executable(contest, controller, run, runFiles);
        
        // Suppress pop up messages on errors
        executable.setShowMessageToUser(false);
        
        executable.execute();

        ExecutionData executionData = executable.getExecutionData();

        RunResultFiles runResultFiles = null;

        JudgementRecord judgementRecord = null;
        
        try {

            if (executionData.getExecutionException() != null) {
                autoJudgeStatusFrame.updateStatusLabel("ERROR - " + executionData.getExecutionException().getMessage());
                log.log(Log.WARNING, "ERROR - " + executionData.getExecutionException().getMessage(), "ERROR - " + executionData.getExecutionException());
                sleepMS(3000);
                // judgementRecord stays null

            } else if (!executionData.isCompileSuccess()) {
                // Compile failed, darn!
                
                autoJudgeStatusFrame.updateStatusLabel("Run failed to compile");

                ElementId elementId = contest.getJudgements()[1].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true);
                judgementRecord.setValidatorResultString("Source failed to compile (compilation error)");

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
                if (yesJudgement.getDisplayName().equalsIgnoreCase(results)) {
                    elementId = yesJudgement.getElementId();
                    solved = true;
                }

                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), solved, true);
                judgementRecord.setValidatorResultString(results);

            } else {
                // Something went wrong either during validation or execution
                // Unable to validate result: Undetermined

                info("Run compiled but failed to validate " + run);

                ElementId elementId = contest.getJudgements()[1].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true);
                judgementRecord.setValidatorResultString("Undetermined");

            }

        } catch (Exception e) {
            info("Exception logged ", e);
        }

        if (judgementRecord == null) {

            autoJudgeStatusFrame.updateStatusLabel("Problem judging run");
            // Cancel the run, hope for better luck.

            sleepMS(2000);
            autoJudgeStatusFrame.updateStatusLabel("Returning run to server");
            controller.cancelRun(run);
            sleepMS(10000);

        } else {

            controller.submitRunJudgement(run, judgementRecord, runResultFiles);
            autoJudgeStatusFrame.updateStatusLabel("Sent judgement to server");

        }

        sleepMS(2000);

        setCurrentlyAutoJudging(false);
        runBeingAutoJudged = run;

        attemptToFetchNextRun();

    }

    /**
     * Start auto judging by finding and fetching next run.
     */
    private void attemptToFetchNextRun() {
        attemptToFetchNextRun(findNextAutoJudgeRun());
    }

    private void sleepMS(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            info(" sleep interrupted ", e);
        }
    }

    /**
     * Attempt to fetch next run to be judged.
     * 
     * @param nextRun
     */
    private void attemptToFetchNextRun(Run nextRun) {
        
        if (isCurrentlyAutoJudging()){
            return;
        }

        if (isRunToBeAutoJudged(nextRun)) {
            // There is ANOTHER run to judge!! Yes!

            // TODO get this setting from the contest property
            sleepMS(1500);

            checkoutNextRun(nextRun);

        } else {
            autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
            autoJudgeStatusFrame.updateMessage("(Still waiting)");
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
     * Start Auto Judging (if configured to start).
     * 
     */
    public void startAutoJudging() {

        if (isAutoJudgeOn()) {
            autoJudgeStatusFrame.updateStatusLabel("Waiting for runs");
            autoJudgeStatusFrame.updateMessage("(Still waiting)");
            attemptToFetchNextRun();

        } else {

            autoJudgeStatusFrame.updateStatusLabel("Auto-judging is OFF");
            autoJudgeStatusFrame.updateMessage("");
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
            if (runBeingAutoJudged != null){
                controller.cancelRun(runBeingAutoJudged);
            }
        }
        
        setAutoJudgeDisabledLocally(true);

        autoJudgeStatusFrame.updateStatusLabel("Auto-judging is OFF");
        autoJudgeStatusFrame.updateMessage("");
    }

    public void info(String s) {
        controller.getLog().warning(s);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
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
     * @return
     */
    public boolean isAutoJudgeDisabledLocally() {
        return autoJudgeDisabledLocally;
    }

    public void setAutoJudgeDisabledLocally(boolean autoJudgeDisabledLocally) {
        this.autoJudgeDisabledLocally = autoJudgeDisabledLocally;
    }

}
