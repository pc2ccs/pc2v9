// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.graders;

import java.util.Scanner;

/**
 * This class performs the functions described in the Problem Package Format "Legacy" specification
 * for the "Default Grader"
 *
 * @author John Buck
 *
 */
public class LegacyGrader {

    // I'm not sure why this is a flag to the grader -- JB
    public static final String IGNORE_SAMPLE_FLAG = "ignore_sample";

    public static final String ACCEPT_IF_ANY_ACCEPTED_FLAG = "accept_if_any_accepted";


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
     * Read the stdin input for lines of test case judgments and evaluate according the specification.
     *
     * @return 0 on success (this is the exit code for the program), non-zero indicates a judging error
     */
    public int processResults() {
        int result = 0;
        Scanner scanner = new Scanner(System.in);
        int nLine = 0;
        int idx;
        boolean found;
        double scoreSum = 0;
        double scoreCount = 0;
        double scoreMin = Double.POSITIVE_INFINITY;
        double scoreMax = Double.NEGATIVE_INFINITY;
        boolean [] sawJudgment = new boolean[JudgmentCodes.values().length];
        boolean anyFailures = false;
        String firstError = null;

        while(scanner.hasNextLine()) {
            nLine++;
            String line = scanner.nextLine().trim();
            String [] values = line.split("\\s+");
            if(values.length != 2) {
                System.err.println("LegacyGrader: invalid input on line " + nLine);
               result = 1;
               break;
            }
            // be generous and accept upper or lower case for judgment
            String code = values[0].toUpperCase();
            double score = 0;
            try {
                score = Double.parseDouble(values[1]);
            } catch(NumberFormatException e) {
                System.err.println("LegacyGrader: invalid score value on line " + nLine);
                result = 2;
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
                System.err.println("LegacyGrader: Unknown judgment code " + code + " at line " + nLine);
                result = 3;
                break;
            }
            if(result != 0) {
                break;
            }
        }
        // Only calculate judgment if there were no errors
        if(result == 0) {
            // Check for no failures or always accept mode or optional flag "any" accepted case
            if(!anyFailures || verdictMode == VerdictMode.always_accept || (acceptIfAnyAccepted && scoreCount > 0)) {
                if(scoreCount == 0) {
                    // this there were no judgments in the input
                    System.err.println("LegacyGrader: No judgments in the input.");
                    result = 4;
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
                    System.out.println(JudgmentCodes.AC.toString() + " " + score);
                }
            } else {
                // determine non-accepted judgment
                // All cases should either print the correct output to stdout, or print
                // an error to stderr and set result to a non-zero value.
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
                            result = 5;
                        }
                        break;

                    case first_error:
                        if(firstError == null) {
                            // Uhm.  This is extremely bad took Tammy, since we know anyFailures must be true
                            System.err.println("LegacyGrader: FATAL error - can not find judgment code for worst_error mode.");
                            result = 6;
                        } else {
                            System.out.println(firstError + " 0");
                        }
                        break;

                    default:
                        // Very bad, Tammy.  This can't happen because alwaysAccept was handled above
                        System.err.println("LegacyGrader: FATAL error - invalid verdict mode " + verdictMode.toString());
                        result = 7;
                        break;
                }
            }
        }
        return result;
    }

    /**
     * @param args - optional combination of a VerdictMode, ScoringMode and Flag(s)
     */
    public static void main(String[] args) {
       LegacyGrader grader = new LegacyGrader();

       if(!grader.parseArguments(args)) {
           System.err.println("LegacyGrader: Unrecognized option supplied.");
           System.exit(1);
       }

       System.exit(grader.processResults());
    }

}
