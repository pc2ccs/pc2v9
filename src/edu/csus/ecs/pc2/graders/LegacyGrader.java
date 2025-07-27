// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.graders;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class performs the functions described in the Problem Package Format "Legacy" specification
 * for the "Default Grader"
 *
 * @author John Buck
 *
 */
public class LegacyGrader {

    public static final String IGNORE_SAMPLE_FLAG = "ignore_sample";

    public static final String ACCEPT_IF_ANY_ACCEPTED_FLAG = "accept_if_any_accepted";

    public static final int GRADER_ERROR_BAD_FORMAT = 1;
    public static final int GRADER_ERROR_BAD_SCORE = 2;
    public static final int GRADER_ERROR_BAD_JUDGMENT = 3;
    public static final int GRADER_ERROR_NO_TEST_CASES = 4;
    public static final int GRADER_ERROR_BAD_WORST_CODE = 5;
    public static final int GRADER_ERROR_BAD_FIRST_CODE = 6;
    public static final int GRADER_ERROR_BAD_VERDICT_MODE = 7;
    public static final int GRADER_ERROR_IGNORE_SAMPLE = 8;

    enum VerdictMode {
        worst_error,
        first_error,
        always_accept;
    }

    enum ScoringMode {
        sum,
        avg,
        min,
        max;
    }

    enum JudgmentCodes {
        AC, // MUST be first (index 0)
        RTE,
        TLE,
        WA;
    }

    private VerdictMode verdictMode = VerdictMode.worst_error;
    private ScoringMode scoringMode = ScoringMode.sum;
    private boolean acceptIfAnyAccepted = false;
    private boolean ignoreSample = false;
    private int graderError = 0;

    /**
     * See if the supplied string argument is a valid verdict mode.
     *
     * @param arg string to check for a verdict mode
     * @return true if the argument supplied was accepted as the verdict mode, false otherwise.
     */
    private boolean checkVerdictMode(String arg) {
        boolean result = false;

        for(VerdictMode vm : VerdictMode.values()) {
            if(arg.equals(vm.toString().toLowerCase())) {
                verdictMode = vm;
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * See if the supplied string argument is a valid scoring mode.
     *
     * @param arg string to check for a scoring mode
     * @return true if the argument supplied was accepted as the scoring mode, false otherwise.
     */
    private boolean checkScoringMode(String arg) {
        boolean result = false;

        for(ScoringMode sm : ScoringMode.values()) {
            if(arg.equals(sm.toString().toLowerCase())) {
                scoringMode = sm;
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * See if the supplied string argument is a flag.
     *
     * @param arg string to check for a flag
     * @return true if the argument supplied was accepted as a valid flag, false otherwise.
     */
    private boolean checkFlags(String arg)
    {
        boolean result = false;

        if(arg.equals(ACCEPT_IF_ANY_ACCEPTED_FLAG)) {
            acceptIfAnyAccepted = true;
            result = true;
        } else if(arg.equals(IGNORE_SAMPLE_FLAG)) {
            ignoreSample = true;
            result = true;
        }
        return(result);
    }

    /**
     * Process an array of string arguments for the grader.
     * Note: This can be called directly if this package is included in a larger application
     *
     * @param args Array of arguments (see the Legacy Problem Package Format specification for Graders)
     * @return true if there were no errors, false otherwise
     */
    public boolean parseArguments(String [] args) {
        boolean result = true;

        for(String arg : args ) {
            if(!checkVerdictMode(arg) && !checkScoringMode(arg) && !checkFlags(arg)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Read the stdin input for lines of test case judgments, create a list of them,
     * and evaluate according the specification.
     *
     * @return 0 on success (this is the exit code for the program), non-zero indicates a judging error
     */
    public int processResults() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> testCaseResults = new ArrayList<String>();

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(!line.isEmpty()) {
                testCaseResults.add(line);
            }
        }
        graderError = 0;
        String result = gradeTestCases(testCaseResults);

        if(result != null) {
            System.out.println(result);
        } else {
            System.out.println("JE 0");
        }
        return(graderError);
    }

    public String gradeTestCases(ArrayList<String> testCaseResults) {
        int nLine = 0;
        int idx;
        boolean found;
        double scoreSum = 0;
        double scoreCount = 0;
        double scoreMin = Double.POSITIVE_INFINITY;
        double scoreMax = Double.NEGATIVE_INFINITY;
        boolean [] sawJudgment = new boolean[JudgmentCodes.values().length];
        boolean anyFailures = false;
        boolean ignoreSampleGroup = ignoreSample;
        String firstError = null;
        String graderResult = null;

        graderError = 0;
        for(String line : testCaseResults) {
            nLine++;
            /*
             * A little explanation here about ignoring samples.
             * The Legacy Grader specification says:
             *     "Must only be used on the root level. The first sub-result (sample)
             *      will be ignored, the second sub-result (secret) will be used,
             *      both verdict and score.
             * If the ignore_sample command line flag was supplied, it implies the group
             * being graded is the root level, as such, there will be exactly 2 sub-groups
             * at the level, sample and secret (in that order - lexicographically).  Therefore,
             * the first group (sample) will simply be ignored (skipped).  Grading will procede
             * with the next line, which will be secret.
             */
            if(ignoreSampleGroup) {
                // Ignore sample group which is the first one.  There better be exactly one more
                // group in the list, or it's a judging error.
                if(testCaseResults.size() != 2) {
                    graderError = GRADER_ERROR_IGNORE_SAMPLE;
                    break;
                }
                ignoreSampleGroup = false;
                continue;
            }
            String [] values = line.trim().split("\\s+");
            if(values.length != 2) {
                System.err.println("LegacyGrader: invalid input test case " + nLine);
               graderError = GRADER_ERROR_BAD_FORMAT;
               break;
            }
            // be generous and accept upper or lower case for judgment
            String code = values[0].toUpperCase();
            double score = 0;
            try {
                score = Double.parseDouble(values[1]);
            } catch(NumberFormatException e) {
                System.err.println("LegacyGrader: invalid score for test case " + nLine);
                graderError = GRADER_ERROR_BAD_SCORE;
                break;
            }
            found = false;
            // We now have the possible code and the score
            for(JudgmentCodes jcode : JudgmentCodes.values()) {
                if(code.equals(jcode.toString())) {
                    idx = jcode.ordinal();
                    // Tally score - we do this even for reject cases(!)
                    scoreSum += score;
                    scoreCount += 1;

                    // keep track of min and max score
                    if(score < scoreMin) {
                        scoreMin = score;
                    }
                    if(score > scoreMax) {
                        scoreMax = score;
                    }

                    // check for first failure
                    if(idx > 0) {
                        if(firstError == null) {
                            firstError = jcode.toString();
                        }
                        anyFailures = true;
                    }
                    sawJudgment[idx] = true;
                    found = true;
                    break;
                }
            }
            if(!found) {
                System.err.println("LegacyGrader: Unknown judgment code " + code + " for test case " + nLine);
                graderError = GRADER_ERROR_BAD_JUDGMENT;
                break;
            }
            if(graderError != 0) {
                break;
            }
        }
        // Only calculate judgment if there were no errors
        if(graderError == 0) {
            // Check for no failures or always accept mode or optional flag "any" accepted case
            if(!anyFailures || verdictMode == VerdictMode.always_accept || (acceptIfAnyAccepted && scoreCount > 0)) {
                if(scoreCount == 0) {
                    // this there were no judgments in the input
                    System.err.println("LegacyGrader: No judgments in the input.");
                    graderError = GRADER_ERROR_NO_TEST_CASES;
                } else {
                    double score = 0;
                    // In this case, we return a score
                    switch(scoringMode) {
                        case sum:   // add'm up
                            score = scoreSum;
                            break;
                        case avg:   // calculate mean
                            score = scoreSum / scoreCount;
                            break;
                        case min:   // the smallest score
                            score = scoreMin;
                            break;
                        case max:   // the biggest score
                            score = scoreMax;
                            break;
                    }
                    graderResult = JudgmentCodes.AC.toString() + " " + score;
                }
            } else {
                // determine non-accepted judgment
                // All cases should either print the correct output to stdout, or print
                // an error to stderr and set graderError to a non-zero value.
                switch(verdictMode) {
                    case worst_error:
                        found = false;
                        for(JudgmentCodes jcode : JudgmentCodes.values()) {
                            idx = jcode.ordinal();
                            if(idx > 0 && sawJudgment[idx]) {
                                System.out.println(jcode.toString() + " 0");
                                found = true;
                                break;
                            }
                        }
                        if(!found) {
                            // Uhm.  This is extremely bad, Tammy, since we KNOW anyFailures must be true
                            System.err.println("LegacyGrader: FATAL error - can not find judgment code for worst_error mode.");
                            graderError = GRADER_ERROR_BAD_WORST_CODE;
                        }
                        break;

                    case first_error:
                        if(firstError == null) {
                            // Uhm.  This is extremely bad took Tammy, since we know anyFailures must be true
                            System.err.println("LegacyGrader: FATAL error - can not find judgment code for first_error mode.");
                            graderError = GRADER_ERROR_BAD_FIRST_CODE;
                        } else {
                            System.out.println(firstError + " 0");
                        }
                        break;

                    default:
                        // Very bad, Tammy.  This can't happen because alwaysAccept was handled above
                        System.err.println("LegacyGrader: FATAL error - invalid verdict mode " + verdictMode.toString());
                        graderError = GRADER_ERROR_BAD_VERDICT_MODE;
                        break;
                }
            }
        }
        // this will be null in the case of an error, in which case graderError will have the error code
        // in the case of success, this will be the "judgment_acronym <space> score", eg. "AC 50"
        return graderResult;
    }

    /**
     * Returns the last error the grader saw, or 0 if no errors.
     * This may be useful if the getTestCases() method returns null.
     *
     * @return the last grader error
     */
    int getGraderError() {
        return graderError;
    }

    /**
     * @param args - optional combination of a VerdictMode, ScoringMode and Flag(s)
     */
    public static void main(String[] args) {
       LegacyGrader grader = new LegacyGrader();
       int exitCode = 0;

       if(!grader.parseArguments(args)) {
           System.err.println("LegacyGrader: Unrecognized option supplied.");
           exitCode = 1;
       } else {
           exitCode = grader.processResults();
       }
       System.exit(exitCode);
    }

}
