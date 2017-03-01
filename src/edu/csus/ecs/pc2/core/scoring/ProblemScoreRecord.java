package edu.csus.ecs.pc2.core.scoring;

import java.util.Properties;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Holds summary score information for a single Problem.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ProblemScoreRecord.java 181 2011-04-11 03:21:46Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/core/scoring/ProblemScoreRecord.java $
public class ProblemScoreRecord {

    private boolean solved = false;

    private long points = 0;

    private long solutionTime = 0;

    private int numberSubmissions = 0;

    private Run solvingRun = null;

    private Problem problem;

    private int numberPendingSubmissions = 0;

    private int numberJudgedSubmissions = 0;

    private int submissionsBeforeYes;

    /**
     * 
     * @param solved
     * @param solvingRun
     * @param problem
     * @param points
     * @param solutionTime
     * @param numberSubmissions
     * @param submissionsBeforeYes
     * @param numberPendingSubmissions
     * @param numberJudgedSubmissions
     */
    public ProblemScoreRecord(boolean solved, Run solvingRun, Problem problem, long points, long solutionTime, int numberSubmissions, int submissionsBeforeYes, int numberPendingSubmissions,
            int numberJudgedSubmissions) {
        super();
        this.solved = solved;
        this.solvingRun = solvingRun;
        this.points = points;
        this.solutionTime = solutionTime;
        this.numberSubmissions = numberSubmissions;
        this.problem = problem;
        this.submissionsBeforeYes = submissionsBeforeYes;
        this.numberPendingSubmissions = numberPendingSubmissions;
        this.numberJudgedSubmissions = numberJudgedSubmissions;
    }

    /**
     * Constructor deprecated.
     * 
     * This class is now used to store values, before it was used to both calculate and store values. <br>
     * See {@link edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm#createProblemScoreRecord(Run[], Problem, Properties)} as an example of how to compute values.
     */
    @Deprecated
    public ProblemScoreRecord(Run[] teamProblemRuns, Problem problem2, Properties properties) {
        // TODO Auto-generated constructor stub
    }

    /**
     * Has problem been solved?.
     * 
     * @return true if problem solved.
     */
    public boolean isSolved() {
        return solved;
    }

    public long getSolutionTime() {
        return solutionTime;
    }

    /**
     * All non-deleted Runs.
     * 
     * @return
     */
    public int getNumberSubmissions() {
        return numberSubmissions;
    }

    /**
     * Time/Penalty points for this problem.
     * 
     * @return number of points
     */
    public long getPoints() {
        return points;
    }

    /**
     * First run which solved this Problem.
     * 
     * @return null if run solved the problem.
     */
    public Run getSolvingRun() {
        return solvingRun;
    }

    /**
     * Number of submissions before first yes.
     * 
     * @return
     */
    public int getSubmissionsBeforeYes() {
        return submissionsBeforeYes;
    }

    @Override
    public String toString() {
        return " Problem " + problem.getDisplayName() + " Solved=" + isSolved() + " pts=" + getPoints() + " runs" + getNumberSubmissions();
    }

    public void setNumberJudgedSubmissions(int numberJudgedSubmissions) {
        this.numberJudgedSubmissions = numberJudgedSubmissions;
    }

    public int getNumberJudgedSubmissions() {
        return numberJudgedSubmissions;
    }

    public void setNumberPendingSubmissions(int numberPendingSubmissions) {
        this.numberPendingSubmissions = numberPendingSubmissions;
    }

    public int getNumberPendingSubmissions() {
        return numberPendingSubmissions;
    }

}
