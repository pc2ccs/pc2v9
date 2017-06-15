package edu.csus.ecs.pc2.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.list.RunComparator;
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
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.ui.judge.JudgeView;

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

    private AutoJudgeStatusFrame autoJudgeStatusFrame = null;
    
    private AutoJudgeNotifyMessages notifyMessager = null;

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

    private GregorianCalendar startTimeCalendar;
    
    private boolean usingGui = true;

    private boolean judgingRun;
    
    private Runnable controlLoop = null;

    // private edu.csus.ecs.pc2.ui.AutoJudgingMonitor.FetchRunListenerImplemenation fetchRunListenerImplemenation;

    /**
     * This class gets started when autoJudging is started.
     * It handles calling attemptToFetchNextRun().
     */
    private class ControlLoop implements Runnable {

        private boolean running = false;
        public void run() {
            running = true;
            while(isAutoJudgingEnabled() && !isAutoJudgeDisabledLocally()) {
                attemptToFetchNextRun();
            }            
            running = false;
        }
        public boolean isRunning() {
            return running;
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 2774495762012789107L;
    
    /**
     * @wbp.parser.constructor
     * @wbp.parser.entryPoint
     */

    public AutoJudgingMonitor() {
        this(false);
    }

    public AutoJudgingMonitor(boolean useGUI) {
        usingGui = useGUI;
        
        if (usingGui){
            autoJudgeStatusFrame = new AutoJudgeStatusFrame();
            notifyMessager = autoJudgeStatusFrame;
        } else {
            notifyMessager = new AutoJudgeNotifyMesssageImpl();
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
        
        notifyMessager.setContestAndController(inContest, inController);

        log = controller.getLog();

        if (usingGui) {
            autoJudgeStatusFrame.setAutoJudgeMonitor(this);
        }

        contest.addRunListener(new RunListenerImplementation());
        contest.addClientSettingsListener(new ClientSettingsListenerImplementation());

        if (usingGui) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    autoJudgeStatusFrame.setTitle("Auto Judge Status " + contest.getClientId().getName());
                }
            });
        }
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

        Filter filter = getAutoJudgeFilter();

        if (filter == null) {
            info(contest.getClientId() + " has no problems selected to auto judge (filter is null)");
            return null;
        }

        if (!isAutoJudgingEnabled()) {
            info(contest.getClientId() + " does not have auto judging turned on");
            return null;
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs,new RunComparator());

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
     * get filter which contains problems to be auto judged.
     * 
     * @return
     */
    private Filter getAutoJudgeFilter() {

        Filter filter = null;
        
        ClientSettings clientSettings = contest.getClientSettings();
        if (clientSettings != null && clientSettings.isAutoJudging()) {
            filter = clientSettings.getAutoJudgeFilter();
        }
        
        return filter;
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
            // just let the ControlLoop grab it
        }
        
        public void refreshRuns(RunEvent event) {
            stopAutoJudging();
            startAutoJudging();
        }

        public void runChanged(RunEvent event) {

            // bug XXX added verification the run is directed to us
            if (runBeingAutoJudged != null && event.getRun().getElementId().equals(runBeingAutoJudged.getElementId())
                    && (event.getSentToClientId() != null && event.getSentToClientId().equals(contest.getClientId()))) {
                // found the run we requested

                if (fetchedRun == null) {
                    // start the time to judge
                    TimeZone tz = TimeZone.getTimeZone("GMT");
                    startTimeCalendar = new GregorianCalendar(tz);
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
            } else {
                if (event.getAction().equals(Action.RUN_NOT_AVAILABLE)) {
                    // we are fetching a run 
                    if (runBeingAutoJudged != null && fetchedRun == null) { // but do not have it yet
                        // and we received a not available for the run we were requesting
                        if (event.getRun().getNumber() ==  runBeingAutoJudged.getNumber() 
                                && event.getRun().getSiteNumber() == runBeingAutoJudged.getSiteNumber()) {
                            // XXX should we log this?
                            // claim to have received it, so fetchRun can exit
                            synchronized (listening) {
                                try {
                                    answerReceived = true;
                                    listening.notify();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            // now cleanup
                            cleanupLastAutoJudge();
                        }
                    }                    
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
        notifyMessager.updateStatusLabel("Waiting for runs");
        notifyMessager.updateMessage("(Still waiting)");
        // we must release this before we get to the next attemptToFetchNewRun()
        setAlreadyJudgingRun(false);
        
        // and this is what allows us to get into that next attemptToFetchNewRun()
        setCurrentlyAutoJudging(false);
    }

    private void setAlreadyJudgingRun(boolean b) {
        
        if (usingGui){
            JudgeView.setAlreadyJudgingRun(b);
        } else {
            judgingRun = b;
        }
        
    }

    /**
     * Judge checked out run.
     * 
     * @param run
     * @param runFiles
     */
    private void executeAndAutoJudgeRun() {
        
        long executeTimeMS = 0;

        setCurrentlyAutoJudging(true);

        if (usingGui){
            autoJudgeStatusFrame.setVisible(true);
        }
        
        notifyMessager.updateStatusLabel("Received run");
        notifyMessager.updateMessage(getRunDescription(fetchedRun));

        try {

            notifyMessager.updateStatusLabel("Judging run");

        } catch (Exception e) {
            warn("Exception logged ", e);
        }

        System.gc();

        executable = new Executable(contest, controller, fetchedRun, fetchedRunFiles);

        // Suppress pop up messages on errors
        executable.setShowMessageToUser(false);
        executable.setUsingGUI(usingGui);

        notifyMessager.updateMessage(getRunDescription(fetchedRun));

        executable.execute();

        ExecutionData executionData = executable.getExecutionData();
        
        executeTimeMS = executionData.getExecuteTimeMS();

        RunResultFiles runResultFiles = null;

        JudgementRecord judgementRecord = null;

        try {

            if (executionData.getExecutionException() != null) {
                notifyMessager.updateStatusLabel("ERROR - " + executionData.getExecutionException().getMessage());
                log.log(Log.WARNING, "ERROR - " + executionData.getExecutionException().getMessage(), "ERROR - " + executionData.getExecutionException());
                // judgementRecord stays null

            } else if (!executionData.isCompileSuccess()) {
                // Compile failed, darn!

                notifyMessager.updateStatusLabel("Run failed to compile");

                ElementId elementId = contest.getJudgements()[1].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
                // TODO this needs to be flexible
                judgementRecord.setValidatorResultString("No - Compilation Error");

            } else if (executionData.isValidationSuccess()) {

                // We got stuff from validator!!
                String results = executable.getValidationResults();
                if (results == null) {
                    results = "Undetermined";
                } else {
                    results = results.trim();
                }
                if (results.length() == 0) {
                    results = "Undetermined";
                }

                boolean solved = false;

                // Try to find result text in judgement list
                //  (start with a default of a non-variable-scoring "no" judgment)
                ElementId elementId = contest.getJudgements()[2].getElementId();
                
                for (Judgement judgement : contest.getJudgements()) {
                    if (judgement.getDisplayName().trim().equalsIgnoreCase(results)) {
                        elementId = judgement.getElementId();
                    }
                }

                // Or perhaps it is a yes? yes?
                Judgement yesJudgement = contest.getJudgements()[0];
                // bug 280 ICPC Validator Interface Standard calls for "accepted" in any case.
                if (results.equalsIgnoreCase("accepted")) {
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

                warn("Run compiled but failed to validate " + fetchedRun);

                //  default to a non-variable-scoring "no" judgment
                ElementId elementId = contest.getJudgements()[2].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
                judgementRecord.setValidatorResultString("Undetermined");

            }

        } catch (Exception e) {
            warn("Exception during execute/validating run " + fetchedRun, e);
        }

        if (judgementRecord == null) {

            warn("Problem judging run " + fetchedRun + " unable to create judgement record");
            notifyMessager.updateStatusLabel("Problem judging run");
            // Cancel the run, hope for better luck.

            notifyMessager.updateStatusLabel("Returning run to server");
            controller.cancelRun(runBeingAutoJudged);

            cleanupLastAutoJudge();

        } else {

            info("Sending judgement to server " + fetchedRun);
            notifyMessager.updateStatusLabel("Sending judgement to server");

            TimeZone tz = TimeZone.getTimeZone("GMT");
            GregorianCalendar cal = new GregorianCalendar(tz);

            long milliDiff = cal.getTime().getTime() - startTimeCalendar.getTime().getTime();
            long totalSeconds = milliDiff / 1000;
            judgementRecord.setHowLongToJudgeInSeconds(totalSeconds);
            judgementRecord.setExecuteMS(executeTimeMS);

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
        if (nextRun == null) {
            // not ready yet
            try {
                Thread.sleep(1300); // 1.3 seconds
            } catch (InterruptedException e) {
                log.finest("attemptToFetchNextRun InterruptedException");
            }
            return;
        }

        if (!isAutoJudgingEnabled()) {
            // Auto judging is turned OFF no need to fetch a new run
            return;
        }

        if (isCurrentlyAutoJudging()) {
            // Already judging run, no need to fetch a new run
            return;
        }

        // need to wait for JudgeView (eg human) too
        // WARNING: must release the JudgeView.alreadyJudgingRun, prior to getting into this
        
        Boolean judgingRunStatus = isAlreadyJudgingRun();
        
        synchronized (judgingRunStatus) {
            notifyMessager.updateMessage("(Waiting 2)");
            while(isAlreadyJudgingRun()) {
                try {
                    judgingRunStatus.wait();
                } catch (InterruptedException e) {
                    log.throwing("AutoJudgingMonitor", "attempttoFetchNextRun()", e);
                }
            }
            setAlreadyJudgingRun(true);
            notifyMessager.updateMessage("(Waiting)");
        }
        if (isRunToBeAutoJudged(nextRun)) {

            runBeingAutoJudged = nextRun;
            fetchRun(nextRun);

            if (fetchedRun != null) {
                info("Fetched run " + fetchedRun);
                executeAndAutoJudgeRun();
            } else {
                info("Unable to fetch run " + nextRun);
                setAlreadyJudgingRun(false);
            }

        } else {
            notifyMessager.updateStatusLabel("Waiting for runs");
            notifyMessager.updateMessage("(Still waiting)");
            setAlreadyJudgingRun(false);
        }
    }

    private boolean isAlreadyJudgingRun() {
        if (usingGui){
            return JudgeView.isAlreadyJudgingRun(); 
        } else {
            return judgingRun;
        }
    }

    private void fetchRun(Run run) {

        setCurrentlyAutoJudging(true);
        runBeingAutoJudged = run;
        notifyMessager.updateStatusLabel("Fetching Run " + run.getNumber() + " (Site " + run.getSiteNumber() + ")");
        notifyMessager.updateMessage(getRunDescription(run));

        startTimeCalendar = null;
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

        public void clientSettingsRefreshAll(ClientSettingsEvent clientSettingsEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (usingGui) {

                        autoJudgeStatusFrame.setTitle("Auto Judge Status " + contest.getClientId().getName());
                    }
                    updateClientSettings(contest.getClientSettings());
                }
            });
        }

    }

    /**
     * If settings are updated then attempt to start autojuding.
     * 
     * @param clientSettings
     */
    public void updateClientSettings(ClientSettings clientSettings) {
        if (clientSettings.getClientId().equals(contest.getClientId())) {
            // These are my settings
            
            if (clientSettings.isAutoJudging()){
                startAutoJudging();
            } else {
                stopAutoJudging();
            }
        }
    }

    /**
     * Start Auto Judging, if enabled,, update status message.
     */
    public void startAutoJudging() {

        if (isAutoJudgingEnabled()) {
            if (usingGui){
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        notifyMessager.updateStatusLabel("Waiting for runs");
                        notifyMessager.updateMessage("(Still waiting)");
                    }
                });
            } else {
                notifyMessager.updateStatusLabel("Auto-judging is ON");
                
                printSelectedProblems();
                
                notifyMessager.updateMessage("Waiting for runs");
            }

            if (controlLoop == null) {
                controlLoop = new ControlLoop();
            }
            
            if (usingGui){

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        autoJudgeStatusFrame.setVisible(true);
                    }
                });
            }
            
            if (!((ControlLoop) controlLoop).isRunning()) {
                controlLoop.run();
            }
            
        } else {

            if (usingGui){
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        notifyMessager.updateStatusLabel("Auto-judging is OFF");
                        notifyMessager.updateMessage("");
                    }
                });
            } else {
                notifyMessager.updateStatusLabel("Auto-judging is OFF");
            }
        }
        
   

    }

    /**
     * Print list of problems that are to be auto judged.
     */
    private void printSelectedProblems() {
        Filter filter = getAutoJudgeFilter();

        ArrayList<String> list = new ArrayList<String>();

        for (Problem problem : contest.getProblems()) {
            if (filter.matches(problem)) {
                list.add(problem.getDisplayName());
            }
        }

        if (list.size() < 1) {
            System.out.println("No problems selected, will not auto judge any runs");
        } else {
            System.out.println("Will auto judge " + list.size() + " problems ");
            for (Iterator<String> i = list.listIterator(); i.hasNext();) {
                System.out.println("  " + i.next());
            }
        }
    }

    public void stopAutoJudging() {
        if (isCurrentlyAutoJudging()) {

            // Cancel the run
            if (runBeingAutoJudged != null) {
                controller.cancelRun(runBeingAutoJudged);
            }
            cleanupLastAutoJudge();
        }

        // TODO handle locally disabled judging
//        setAutoJudgeDisabledLocally(true);

        notifyMessager.updateStatusLabel("Auto-judging is OFF");
        notifyMessager.updateMessage("");
    }

    public void info(String s) {
        controller.getLog().info(s);
        if (! usingGui){
            System.out.println(s);
        }
      
    }

    public void warn(String s) {
        controller.getLog().warning(s);
        if (! usingGui){
            System.err.println(s);
        }
      
    }

    public void warn(String s, Exception exception) {
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
