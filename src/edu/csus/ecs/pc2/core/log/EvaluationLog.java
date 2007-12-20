package edu.csus.ecs.pc2.core.log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Creates and adds evaluation/judgement info line to a log file (evals.log).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EvaluationLog implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3187268249635199184L;

    private IContest contest;

    @SuppressWarnings("unused")
    private IController controller;

    /**
     * 
     */
    private PrintWriter evalLog = null;

    private boolean logOpened = false;

    public EvaluationLog(String logFileName, IContest contest, IController controller) {
        try {
            logOpened = false;
            evalLog = new PrintWriter(new FileOutputStream(logFileName, true), true);
            logOpened = true;
            setContestAndController(contest, controller);
        } catch (FileNotFoundException e) {
            StaticLog.log("Unable to open file" + logFileName, e);
            evalLog = null;
        }
    }

    /**
     * Print a evaluation line to a printWriter.
     * 
     * @param printWriter
     * @param run
     * @param inContest
     */
    public static void printEvaluationLine(PrintWriter printWriter, Run run, IContest inContest) {

        // 1 date and time
        // 2 site #
        // 3 Run #
        // 4 Team #
        printWriter.print(new Date() + "|");
        printWriter.print("Site " + run.getSiteNumber() + "|");
        printWriter.print("Run " + run.getNumber() + "|");
        printWriter.print("Team " + run.getSubmitter().getClientNumber() + "|");

        // 5 Problem #
        // 6 Was solved
        // 7 Proxy
        // 8 Run deleted
        printWriter.print(run.getProblemId() + "|");
        printWriter.print("Solved " + run.isSolved() + "|");
        printWriter.print("Proxy 0|");
        printWriter.print("Deleted " + run.isDeleted() + "|");

        // 9# Judgement
        // 10 (unknown)
        // 11 Was Accept Hit on judgement screen
        // 12 who judged it

        JudgementRecord judgementRecord = run.getJudgementRecord();

        if (run.isJudged() && judgementRecord != null) {

            ElementId elementId = judgementRecord.getJudgementId();
            printWriter.print("Judgement " + inContest.getJudgement(elementId) + "|");
            printWriter.print("ValJud " + judgementRecord.isUsedValidator() + "|");
            printWriter.print("AccHit " + judgementRecord.isAcceptButtonHit() + "|");
            printWriter.print("Judge " + judgementRecord.getJudgerClientId() + "|");
        } else {
            printWriter.print("||||");
        }
        printWriter.println();
        printWriter.flush();
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        
        contest.addRunListener(new RunListenerImplementation());
    }

    public String getPluginTitle() {
        return "Evaluation Log Writer";
    }

    protected void writeRunLine(Run run) {
        if (logOpened) {
            printEvaluationLine(evalLog, run, contest);
        } else {
            StaticLog.warning("Evaluation log not opened for write " + run);
        }
    }

    /**
     * Run Listenter for Evaluations Log.
     * 
     * This listener will write out when runs are judged and deleted.
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            runChanged(event);
        }

        public void runChanged(RunEvent event) {
            Run run = event.getRun();
            if (run.isJudged()) {
                writeRunLine(run);
            }
        }

        public void runRemoved(RunEvent event) {
            Run run = event.getRun();
            writeRunLine(run);
        }
    }

    public boolean isLogOpened() {
        return logOpened;
    }

    /**
     * A handle the the evaluation log.
     * 
     * @return
     */
    public PrintWriter getEvalLog() {
        return evalLog;
    }
}
