package edu.csus.ecs.pc2.ccs;

import edu.csus.ecs.pc2.core.CommandVariableReplacer;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Manages sending of Run Submissions via Run Submission Interface.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunSubmitterInterfaceManager implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7193057846912064771L;

    private IInternalContest contest;

    private IInternalController controller;

    private CommandVariableReplacer commandVariableReplacer = new CommandVariableReplacer();

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        ContestInformation contestInformation = inContest.getContestInformation();

        if (contestInformation == null) {
            info("No Run Submission Interface defined");
        } else if (contestInformation.getRsiCommand() == null) {
            info("No Run Submission Interface defined");
        } else {
            info("Run Submission Interface command is: " + contestInformation.getRsiCommand());
        }
    }

    private void info(String string) {
        if (controller != null && controller.getLog() != null) {
            controller.getLog().info(string);
        } else if (Utilities.isDebugMode()) {
            System.err.println(string);
        }
    }

    public String getPluginTitle() {
        return "Run Submitter Interface Manager";
    }

    /**
     * 
     * @param run
     * @param runFiles
     */
    public void sendRun(Run run, RunFiles runFiles) {

        String command = contest.getContestInformation().getRsiCommand();

        Problem problem = contest.getProblem(run.getProblemId());
        info("RSI Command before: " + command);
        String newCommand = commandVariableReplacer.substituteAllStrings(contest, run, runFiles, command, null, contest.getProblemDataFile(problem));
        info("RSI Command  after: " + newCommand);

        // TODO construct command

        // TODO execute command
    }

}
