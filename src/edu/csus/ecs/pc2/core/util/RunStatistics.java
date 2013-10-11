package edu.csus.ecs.pc2.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.csus.ecs.pc2.core.list.RunCompartorByElapsed;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Contains methods that compute various statistics.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: RunStatistics.java 336 2013-06-21 08:15:14Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/core/util/RunStatistics.java $
public class RunStatistics {

    private IInternalContest contest;

    /**
     * Map of first solved runs for given problem (elementid).
     */
    private Map<ElementId, Run> solvedMap = new HashMap<ElementId, Run>();

    public RunStatistics(IInternalContest contest) {
        setContest(contest);
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;

        recomputeStatitics();
    }

    private void recomputeStatitics() {

        /*
         * find first soleved for each problem.
         */

        Run[] runs = contest.getRuns();

        if (runs.length == 0 || contest.getProblems().length == 0) {
            return;
        }

        // sort by elapsed then by run number
        Arrays.sort(runs, new RunCompartorByElapsed());

        for (Run run : runs) {
            if (run.isSolved() && (!run.isDeleted())) {
                ElementId problemId = run.getProblemId();
                if (solvedMap.get(problemId) == null) {
                    // first solved for this problem.
                    solvedMap.put(problemId, run);
                }
            }
        }
    }

    /**
     * Get list of runs that are first solution for each problem.
     * 
     * @return returns 0 length array of Run, or list of Runs that first solved each problem.
     */
    public Run[] firstSolvedRuns() {
        Set<ElementId> keys = solvedMap.keySet();
        if (keys.size() == 0) {
            return new Run[0];
        }
        ArrayList<Run> list = new ArrayList<Run>();

        for (Problem problem : contest.getProblems()) {

            Run run = getFirstSolved(problem);
            if (run != null) {
                list.add(run);
            }
        }

        return (Run[]) list.toArray(new Run[list.size()]);

    }

    /**
     * Get first run that solves input problem.
     * 
     * @param problem
     * @return null if no run/solution for problem, else returns run.
     */
    public Run getFirstSolved(Problem problem) {
        return solvedMap.get(problem.getElementId());
    }

    /**
     * Is the input team the first to solve this problem?.
     * 
     * @see #getFirstSolved(Problem)
     * @param id
     * @param problem
     * @return false if not the first to solve.
     */
    public boolean isFirstToSolve(ClientId id, Problem problem) {
        Run run = getFirstSolved(problem);
        if (run != null) {
            return run.getSubmitter().equals(id);
        } else {
            return false;
        }
    }
}
