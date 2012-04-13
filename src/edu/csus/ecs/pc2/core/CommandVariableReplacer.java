package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Replace command line variables.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class CommandVariableReplacer {

    /**
     * Replace all instances of beforeString with afterString.
     * 
     * If before string is not found, then returns original string.
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterString
     *            string to replace beforeString
     * @return original string with all beforeString instances replaced with afterString
     */
    protected String replaceString(String origString, String beforeString, String afterString) {

        if (origString == null) {
            return origString;
        }

        int startIdx = origString.lastIndexOf(beforeString);

        if (startIdx == -1) {
            return origString;
        }

        StringBuffer buf = new StringBuffer(origString);

        while (startIdx != -1) {
            buf.replace(startIdx, startIdx + beforeString.length(), afterString);
            startIdx = origString.lastIndexOf(beforeString, startIdx - 1);
        }

        return buf.toString();
    }

    /**
     * Replace beforeString with int.
     * 
     * For details see {@link #replaceString(String, String, String)}
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterInt
     *            integer to replace beforeString
     * @return string after replacement.
     */
    protected String replaceString(String origString, String beforeString, int afterInt) {
        String afterString = new Integer(afterInt).toString();
        return replaceString(origString, beforeString, afterString);
    }

    /**
     * return string with all field variables filled with values.
     * 
     * Each variable will be filled in with values.
     * 
     * <pre>
     *             valid fields are:
     *              {:mainfile} - submitted file (hello.java)
     *              {:basename} - mainfile without extension (hello)
     *              {:validator} - validator program name
     *              {:language}
     *              {:problem}
     *              {:teamid}
     *              {:siteid}
     *              {:infile}
     *              {:outfile}
     *              {:ansfile}
     *              {:pc2home}
     * </pre>
     * 
     * @param run
     *            submitted by team
     * @param origString
     *            - original string to be substituted.
     * @param executionData
     * @param problemDataFiles
     * @return string with values
     */
    public String substituteAllStrings(IInternalContest contest, Run run, RunFiles runFiles, String origString, ExecutionData executionData, ProblemDataFiles problemDataFiles) {
        String newString = "";
        String nullArgument = "-"; /* this needs to change */

        if (run == null) {
            throw new IllegalArgumentException("Run is null");
        }

        if (runFiles == null) {
            throw new IllegalArgumentException("RunFiles is null");
        }

        if (runFiles.getMainFile() == null) {
            throw new IllegalArgumentException("RunFiles.getMainFile is null");
        }

        newString = replaceString(origString, "{:mainfile}", runFiles.getMainFile().getName());
        newString = replaceString(newString, "{:basename}", removeExtension(runFiles.getMainFile().getName()));

        String validatorCommand = null;

        Problem problem = contest.getProblem(run.getProblemId());

        if (problem.getValidatorProgramName() != null) {
            validatorCommand = problem.getValidatorProgramName();
        }

        if (problemDataFiles != null) {
            SerializedFile validatorFile = problemDataFiles.getValidatorFile();
            if (validatorFile != null) {
                validatorCommand = validatorFile.getName(); // validator
            }
        }

        if (validatorCommand != null) {
            newString = replaceString(newString, "{:validator}", validatorCommand);
        }

        if (run.getLanguageId() != null) {
            Language language = contest.getLanguage(run.getLanguageId());
            int index = getLanguageIndex(contest, language);
            if (index > 0) {
                newString = replaceString(newString, "{:language}", index);
                newString = replaceString(newString, "{:languageletter}", Utilities.convertNumber(index));
            }
        }
        if (run.getProblemId() != null) {
            int index = getProblemIndex(contest, problem);
            if (index > 0) {
                newString = replaceString(newString, "{:problem}", index);
                newString = replaceString(newString, "{:problemletter}", Utilities.convertNumber(index));
            }
        }
        if (run.getSubmitter() != null) {
            newString = replaceString(newString, "{:teamid}", run.getSubmitter().getClientNumber());
            newString = replaceString(newString, "{:siteid}", run.getSubmitter().getSiteNumber());
        }

        if (problem != null) {
            if (problem.getDataFileName() != null && !problem.getDataFileName().equals("")) {
                newString = replaceString(newString, "{:infile}", problem.getDataFileName());
            } else {
                newString = replaceString(newString, "{:infile}", nullArgument);
            }
            if (problem.getAnswerFileName() != null && !problem.getAnswerFileName().equals("")) {
                newString = replaceString(newString, "{:ansfile}", problem.getAnswerFileName());
            } else {
                newString = replaceString(newString, "{:ansfile}", nullArgument);
            }
            newString = replaceString(newString, "{:timelimit}", Long.toString(problem.getTimeOutInSeconds()));
        }

        if (executionData != null) {
            if (executionData.getExecuteProgramOutput() != null) {
                if (executionData.getExecuteProgramOutput().getName() != null) {
                    newString = replaceString(newString, "{:outfile}", executionData.getExecuteProgramOutput().getName());
                } else {
                    newString = replaceString(newString, "{:outfile}", nullArgument);
                }
            }
            newString = replaceString(newString, "{:exitvalue}", Integer.toString(executionData.getExecuteExitValue()));
            newString = replaceString(newString, "{:executetime}", Long.toString(executionData.getExecuteTimeMS()));
        }
        String pc2home = new VersionInfo().locateHome();
        if (pc2home != null && pc2home.length() > 0) {
            newString = replaceString(newString, "{:pc2home}", pc2home);
        }
        return newString;
    }

    /**
     * Return the problem index (starting at/base one)).
     * 
     * Does not count problems that are not active.
     * 
     * @param contest
     * @param inProblem
     * @return -1 if problem not found or inactive, else 1 or greater as rank for problem.
     */
    public int getProblemIndex(IInternalContest contest, Problem inProblem) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
            if (problem.isActive()) {
                if (problem.getElementId().equals(inProblem.getElementId())) {
                    return idx + 1;
                }
                idx++;
            }
        }

        return -1;
    }

    /**
     * Return the language index (starting at base one).
     * 
     * @param contest
     * @param inLanguage
     * @return -2 if language not found or inactive, else 1 or greater rank for language.
     */
    public int getLanguageIndex(IInternalContest contest, Language inLanguage) {
        int idx = 0;
        for (Language language : contest.getLanguages()) {
            if (language.isActive()) {
                if (language.getElementId().equals(inLanguage.getElementId())) {
                    return idx + 1;
                }
                idx++;
            }
        }

        return -1;
    }

    /**
     * Return string minus last extension.
     * 
     * Finds last . (period) in input string, strips that period and all other characters after that last period. If no period is found in string, will return a copy of the original string. <br>
     * Unlike the Unix basename program, no extension is supplied.
     * 
     * @param original
     *            the input string
     * @return a string with all text after last . removed
     */
    public String removeExtension(String original) {
        String outString = new String(original);

        // Strip off all text after and including final dot

        int dotIndex = outString.lastIndexOf('.', outString.length() - 1);
        if (dotIndex != -1) {
            outString = outString.substring(0, dotIndex);
        }

        return outString;
    }
}
