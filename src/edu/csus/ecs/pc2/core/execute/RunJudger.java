// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;

/**
 * Run judger.
 * 
 * @author pc2@ecs.csus.edu
 */
public class RunJudger {

    private IInternalContest contest;

    private IInternalController controller;

    private Notifier notifyMessager = new Notifier();

    private boolean usingGui = false;

    /**
     * Run currently being judged.
     */
    private Run runBeingJudged = null;

    private RunJudger() {
        // no default constructor
        ;
    }

    public RunJudger(IInternalContest contest, IInternalController controller) {
        this.contest = contest;
        this.controller = controller;
    }

    /**
     * Judge Run.
     * 
     * @param run
     * @param runFiles
     * @return excution data
     * @throws Exception
     */
    public ExecutionData executeAndAutoJudgeRun(Run run, RunFiles runFiles) throws Exception {

        long executeTimeMS = 0;

        if (runBeingJudged != null) {
            throw new Exception("Cannot judge " + run + " already judging run " + runBeingJudged);
        }

        runBeingJudged = run;

        notifyMessager.updateStatusLabel("Received run");
        notifyMessager.updateMessage(getRunDescription(run));

        try {

            notifyMessager.updateStatusLabel("Judging run");

        } catch (Exception e) {
            warn("Exception logged ", e);
        }

        System.gc();

        Executable executable = new Executable(contest, controller, run, runFiles);

        // Suppress pop up messages on errors
        executable.setShowMessageToUser(false);
        executable.setUsingGUI(usingGui);

        notifyMessager.updateMessage(getRunDescription(run));

        executable.execute();

        // Dump execution results files to log
        String executeDirctoryName = JudgementUtilites.getExecuteDirectoryName(getContest().getClientId());
        Problem problem = getContest().getProblem(run.getProblemId());
        ClientId clientId = getContest().getClientId();
        List<Judgement> judgements = JudgementUtilites.getLastTestCaseJudgementList(contest, run);
        JudgementUtilites.dumpJudgementResultsToLog(getLog(), clientId, run, executeDirctoryName, problem, judgements, executable.getExecutionData(), "", new Properties());

        ExecutionData executionData = executable.getExecutionData();

        executeTimeMS = executionData.getExecuteTimeMS();

        RunResultFiles runResultFiles = null;

        JudgementRecord judgementRecord = null;

        TimeZone tz = TimeZone.getTimeZone("GMT");
        GregorianCalendar startTimeCalendar = new GregorianCalendar(tz);

        try {

            if (executionData.getExecutionException() != null) {
                notifyMessager.updateStatusLabel("ERROR - " + executionData.getExecutionException().getMessage());
                getLog().log(Log.WARNING, "ERROR - " + executionData.getExecutionException().getMessage(), "ERROR - " + executionData.getExecutionException());

                // judgementRecord stays null

            } else if (!executionData.isCompileSuccess()) {
                // Compile failed, darn!

                notifyMessager.updateStatusLabel("Run failed to compile");

                Judgement judgement = JudgementUtilites.findJudgementByAcronym(contest, "CE");
                String judgementString = "No - Compilation Error"; // default
                ElementId elementId = null;
                if (judgement != null) {
                    judgementString = judgement.getDisplayName();
                    elementId = judgement.getElementId();
                } else {
                    // TODO: find judgement string by name (from somewhere other than the judgements list)
                    elementId = contest.getJudgements()[1].getElementId();
                }

                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
                judgementRecord.setValidatorResultString(judgementString);

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
                // (start with a default of a non-variable-scoring "no" judgment)
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

                Judgement judgement = contest.getJudgement(elementId);
                notifyMessager.updateMessage("Judged run " + run + " results " + results + " Judgement " + judgement.getAcronym() + " : " + judgement.getDisplayName());
                info("Judged run " + run + " results " + results + " Judgement " + judgement.getAcronym() + " : " + judgement.getDisplayName());

                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), solved, true, true);
                judgementRecord.setValidatorResultString(results);

            } else {
                // Something went wrong either during validation or execution
                // Unable to validate result: Undetermined

                warn("Run compiled but failed to validate " + run);

                // default to a non-variable-scoring "no" judgment
                ElementId elementId = contest.getJudgements()[2].getElementId();
                judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
                judgementRecord.setValidatorResultString("Undetermined");

            }

        } catch (Exception e) {
            warn("Exception during execute/validating run " + run, e);
        }

        try {
            if (judgementRecord == null) {

                warn("Problem judging run " + run + " unable to create judgement record");
                notifyMessager.updateStatusLabel("Problem judging run");
                // Cancel the run, hope for better luck.

                notifyMessager.updateStatusLabel("Returning run to server");
                controller.cancelRun(run);

            } else {

                info("Sending judgement to server " + run);
                notifyMessager.updateStatusLabel("Sending judgement to server ");

                tz = TimeZone.getTimeZone("GMT");
                GregorianCalendar cal = new GregorianCalendar(tz);

                long milliDiff = cal.getTime().getTime() - startTimeCalendar.getTime().getTime();
                long totalSeconds = milliDiff / 1000;
                judgementRecord.setHowLongToJudgeInSeconds(totalSeconds);
                judgementRecord.setExecuteMS(executeTimeMS);

                runResultFiles = new RunResultFiles(run, run.getProblemId(), judgementRecord, executable.getExecutionData());
                controller.submitRunJudgement(run, judgementRecord, runResultFiles);
            }

        } catch (Exception e) {
            warn("Problem updating judgement files or sending packet for run " + run, e);
            System.out.println("Problem updating judgement files or sending packet for run " + run + " " + e.getMessage());
        }

        info("Sending AVAILABLE_TO_AUTO_JUDGE to server.");
        controller.sendAvailableToAutoJudge(contest.getClientId());
        runBeingJudged = null;

        return executable.getExecutionData();
    }

    private Log getLog() {
        return controller.getLog();
    }

    public IInternalContest getContest() {
        return contest;
    }

    private String getRunDescription(Run runToCheckOut) {
        // ## - Problem Title (Run NN, Site YY)
        return " Run " + runToCheckOut.getNumber() + " Site " + runToCheckOut.getSiteNumber() + " - " + contest.getProblem(runToCheckOut.getProblemId()).getDisplayName();
    }

    public IInternalController getController() {
        return controller;

    }

    public void info(String s) {
        controller.getLog().info(s);
        if (!usingGui) {
            System.out.println(s);
        }

    }

    public void warn(String s) {
        controller.getLog().warning(s);
        if (!usingGui) {
            System.err.println(s);
        }

    }

    public void warn(String s, Exception exception) {
        controller.getLog().log(Log.WARNING, s, exception);
        System.err.println(Thread.currentThread().getName() + " " + s);
        System.err.flush();
        exception.printStackTrace(System.err);
    }

    public class Notifier {

        public void updateStatusLabel(String string) {
            // TODO 496 write to log and to stdout?

            // TODO Auto-generated method stub

        }

        public void updateMessage(String runDescription) {
            // TODO Auto-generated method stub

        }

    }
    
    /**
     * get run being judged.
     * 
     * @return null if no run being judged, else the run
     */
    public Run getRunBeingJudged() {
        return runBeingJudged;
    }

    /**
     * Is judger already judging ?
     * @return true if judging, false if not
     */
    public boolean isJudging() {
        return runBeingJudged == null;
    }

}
