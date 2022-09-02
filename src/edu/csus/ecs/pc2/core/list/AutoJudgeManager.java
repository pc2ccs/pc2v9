// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.MockController;
import edu.csus.ecs.pc2.core.execute.JudgementUtilites;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.AvailableAJRun;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.report.AutojudgeListReport;

/**
 * A class that contains AJ lists.
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class AutoJudgeManager  {

    /**
     * List of currently available AutoJudge clients (that is, Judge clients which have registered as being available to AutoJudge a set of problems).
     */
    private AvailableAJList availableAJList = new AvailableAJList();

    /**
     * List of submitted runs currently awaiting dispatching to an available AutoJudge.
     */
    private AvailableAJRunList availableAJRunList = new AvailableAJRunList();

    /**
     * Locking object for synchronizing access to the lists of available AutoJudges and runs waiting to be AutoJudged
     */
    Object ajLock = new Object();


    public void clear() {
        availableAJList = new AvailableAJList();
        availableAJRunList = new AvailableAJRunList();
    }

    /**
     * 
     * @param contest
     * @param judgeClientId
     * @return null if auto judge not added
     */
    public AvailableAJ addAvailableAutoJudge(IInternalContest contest, ClientId judgeClientId) {

        // Auto judge settings are in ClientSettings
        ClientSettings settings = contest.getClientSettings(judgeClientId);

        if (settings == null || !settings.isAutoJudging()) {
            // Cannot add - judge not autojudging
            return null;
        }

        Filter judgesAJProblemsFilter = settings.getAutoJudgeFilter();
        ProblemList problemList = JudgementUtilites.getAutoJudgedProblemList(contest, judgesAJProblemsFilter);

        if (problemList.getList().length == 0) {
            // Judge has no problems assigned.
            return null;
        }

        synchronized (ajLock) {
            AvailableAJ availableAJ = new AvailableAJ(judgeClientId, problemList);
            availableAJList.add(availableAJ);
            return availableAJ;
        }
    }

    public void removeAvailableAutoJudge(ClientId judgeClientId) {
        synchronized (ajLock) {
            ProblemList problemList = new ProblemList();
            AvailableAJ availableAJ = new AvailableAJ(judgeClientId, problemList);
            availableAJList.remove(availableAJ);
        }
    }
    
    /**
     * find Auto Judge for input run.
     * 
     * @param run
     * @return null if no judge found to auto judge, else the clientid for the judge
     */
    public ClientId findAutoJudgeForRun(Run run) {
        for (AvailableAJRun currentAJRun : availableAJRunList) {

            for (AvailableAJ currentAJ : availableAJList) {

                try {
                    // check if the problem for the current run can be judged by the current AJ
                    if (currentAJ.canJudge(currentAJRun.getProblemId())) {
                        return currentAJ.getClientId();
                    }
                } catch (Exception e) {
                    log(Level.WARNING, "Problem finding auto judge for run " + run);
                }
            }
        }
        return null;
    }

    /**
     * find a run in the available runs list for the input judge.
     * 
     * @param judgeClientId
     * @return null if no run found to autojudge for judge
     */
    public Run findRunToAutoJudge(IInternalContest contest, ClientId judgeClientId) {

        synchronized (ajLock) {

            Run run = null;

            //check each run which is awaiting an AJ
            for (AvailableAJRun currentAJRun : availableAJRunList) {

                for (AvailableAJ currentAJ : availableAJList) {
                    
                    try {
                        //check if the problem for the current run can be judged by the current AJ
                        if (currentAJ.canJudge(currentAJRun.getProblemId())) {
                            
                            // remove AJRun
                            availableAJRunList.remove(currentAJRun);
                            
                            // remove Judge 
                            availableAJList.remove(currentAJ);
                            
                            return contest.getRun(currentAJRun.getRunId());
                        }
                    } catch (Exception e) {
                        log(Level.WARNING, "Problem assigning run to judge "+currentAJ.getClientId()+" "+getRun(contest, currentAJRun.getRunId()));
                    }
                }
            }

            return run;
        }
    }

    /**
     * "Safe" logging method using StaticLog
     * 
     * @param warning
     * @param string
     */
    private void log(Level level, String string) {

        try {
            StaticLog.getLog().log(level, string);
        } catch (Exception e) {
            System.err.println("Unable to write message to StaticLog " + e.getMessage());
            System.err.println(string);
        }

    }

    private String getRun(IInternalContest contest, ElementId runId) {
        
        try {
            return contest.getRun(runId).toString();
        } catch (Exception e) {
            // if there is a problem, output the run elementId
            return "Run.elementid = "+runId;
        }
        
    }

    public AvailableAJRun addRunToAutoJudge(Run run) {
        synchronized (ajLock) {
            AvailableAJRun ajRun = new AvailableAJRun(run.getElementId(), run.getElapsedMS(), run.getProblemId());
            availableAJRunList.add(ajRun);
            return ajRun;
        }
    }

    public void removeAutoJudgeRun(Run run) {
        synchronized (ajLock) {
            AvailableAJRun ajRun = new AvailableAJRun(run.getElementId(), run.getElapsedMS(), run.getProblemId());
            availableAJRunList.remove(ajRun);
        }
    }

    public List<AvailableAJRun> getAvailableAJRuns(){
        
        // TODO i 496 getAvailableAJRuns synchronize this ?
        
        List<AvailableAJRun> list = new ArrayList<AvailableAJRun>();
        for (AvailableAJRun availableAJRun : availableAJRunList) {
            list.add(availableAJRun);
        }
        return list;
    }
    
    
    public List<AvailableAJ> getAvailableAJList() {
        
        // TODO i 496 getAvailableAJList synchronize this ?
        
        List<AvailableAJ> list = new ArrayList<AvailableAJ>();
        for (AvailableAJ availableAJ : availableAJList) {
            list.add(availableAJ);
        }
        return list;
    }

    public static void dump(String comment, PrintWriter printWriter, IInternalContest contest) {

        try {
            if (comment != null) {
                printWriter.println(comment);
            }

            AutojudgeListReport report = new AutojudgeListReport();
            IInternalController controller = new MockController();
            report.setContestAndController(contest, controller);
            report.writeReport(printWriter);
            printWriter.flush();
            report = null;

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }

    public static void dump(String comment, PrintStream out, IInternalContest contest) {
        dump(comment, new PrintWriter(out), contest);
    }

}
