package edu.csus.ecs.pc2.core.scoring;

import java.util.Arrays;
import java.util.Properties;

import edu.csus.ecs.pc2.core.list.RunCompartorByElapsed;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Holds summary score information for a single Problem.
 * 
 * This uses the same Properties from the {@link DefaultScoringAlgorithm}.
 * <P>
 * This class ignores deleted runs and respects the {@link Run#isSendToTeams()}
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

    private Run[] runs;

    private Problem problem;

    private Properties properties;

//    private static final String POINTS_PER_NO = "Points per No";
//
//    private static final String POINTS_PER_YES_MINUTE = "Points per Minute (for 1st yes)";
//
//    private static final String BASE_POINTS_PER_YES = "Base Points per Yes";

    public ProblemScoreRecord(Run[] runs, Problem problem, Properties properties) {
        super();
        this.runs = runs;
        this.problem = problem;
        this.properties = properties;

        calculateScore();
    }

    private void calculateScore() {

        int submissionsBeforeYes = 0;

        Arrays.sort(runs, new RunCompartorByElapsed());

        for (Run run : runs) {
            if (run.isDeleted()) {
                continue;
            }

            numberSubmissions++;
            
            if (run.isSendToTeams()){
                if (run.isSolved()  && solutionTime == 0) {
                    solved = true;
                    solutionTime = run.getElapsedMins();
                    solvingRun = run;
                }
                if (!isSolved()) {
                    // Not solved, yet

                    if (run.isJudged() && (!run.isSolved())) {
                        submissionsBeforeYes++;
                    }
                }
            }
        }

        if (isSolved()) {
            points = (solutionTime * getMinutePenalty() + getYesPenalty()) + (submissionsBeforeYes * getNoPenalty());
        }

    }

    private long getYesPenalty() {
        return getPropIntValue(properties, DefaultScoringAlgorithm.BASE_POINTS_PER_YES, "0");
    }

    /**
     * @param key property to lookup
     * @param defaultValue
     * @return
     */
    private int getPropIntValue(Properties inProperties, String key, String defaultValue) {
        String s = inProperties.getProperty(key, defaultValue);
        Integer i = Integer.parseInt(s);
        return (i.intValue());
    }

    private int getNoPenalty() {
        // private static String[][] propList = { { POINTS_PER_NO, "20:Integer" }, { POINTS_PER_YES_MINUTE, "1:Integer" }, {
        // BASE_POINTS_PER_YES, "0:Integer" } };
        return getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO, "20");
    }

    private int getMinutePenalty() {
        return getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_YES_MINUTE, "1");
    }

    /**
     * Has problem been solved?.
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
     * Get all runs from constructor.
     * 
     * @return
     */
    public Run[] getRuns() {
        return runs;
    }

    public Problem getProblem() {
        return problem;
    }

    /**
     * 
     * @return scoring properties
     */
    public Properties getProperties() {
        return properties;
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

    @Override
    public String toString() {
        return " Problem " + problem.getDisplayName() + " Solved=" + isSolved() + " pts=" + getPoints() + " runs"
                + getNumberSubmissions();
    }
}
