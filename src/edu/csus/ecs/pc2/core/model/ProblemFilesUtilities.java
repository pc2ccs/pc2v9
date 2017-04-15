package edu.csus.ecs.pc2.core.model;

import java.io.File;

/**
 * Problem Files Utilities.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public final class ProblemFilesUtilities {

    private ProblemFilesUtilities(){
        
    }
    /**
     * Verifies that all judge's data and answer files are present.
     * 
     * Verifies both internal (stored in pc2) and external (stored on the file system).
     * 
     * The {@link Problem#isUsingExternalDataFiles()} method determines which will be checked.
     * 
     * @param contest
     * @param problem
     * @return true if all files available else returns false, returns false if no data files defined.
     */
    public static boolean verifyProblemFiles(IInternalContest contest, Problem problem) {
        
        if (problem.getNumberTestCases() == 0){
            return false;
        }
        
        if (problem.isUsingExternalDataFiles()) {
            return verifyExternalProblemFiles(contest, problem);
        } else {
            return verifyInternalProblemFiles(contest, problem);
        }
    }

    private static boolean verifyExternalProblemFiles(IInternalContest contest, Problem problem) {

        for (int i = 1; i <= problem.getNumberTestCases(); i++) {

            if (!new File(problem.getDataFileName(i)).isFile()) {
                return false;
            }
            if (!new File(problem.getAnswerFileName(i)).isFile()) {
                return false;
            }

        }

        return true;
    }

    /**
     * 
     * @param contest
     * @param problem
     * @return true if all internal files are present
     */
    private static boolean verifyInternalProblemFiles(IInternalContest contest, Problem problem) {

        ProblemDataFiles files = contest.getProblemDataFile(problem);
        
        if (files == null){
            return false;
        }

        if (problem.getNumberTestCases() != files.getJudgesAnswerFiles().length || problem.getNumberTestCases() != files.getJudgesDataFiles().length) {
            return false;
        }

        return false;
    }

    /**
     * Verifies that a validator is available.
     * 
     * @param contest
     * @param problem
     * @return true if validator is available.
     */
    public static boolean verifyValidator(IInternalContest contest, Problem problem) {

        ProblemDataFiles files = contest.getProblemDataFile(problem);

        if (files.getOutputValidatorFile() == null) {
            return true;
        } else if (problem.getValidatorProgramName() == null) {
            // no validator name defined
            return false;
        } else if (new File(problem.getValidatorProgramName()).isFile()) {
            // external validator name
            return true;
        }

        return false;
    }
}
