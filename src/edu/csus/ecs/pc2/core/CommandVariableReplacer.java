// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;

import java.io.File;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Replace command line variables.
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class CommandVariableReplacer {

    /**
     * CCS RSI command line options.
     */
    public static final String OPTIONS = "{:options}";

    /**
     * All files, main file and additional files.
     *
     */
    public static final String FILELIST = "{:filelist}";

    /**
     * Main file name.
     */
    public static final String MAINFILE = "{:mainfile}";

    /**
     * Main file name without extension.
     */
    public static final String BASENAME = "{:basename}";

    /**
     * Run Id (or number).
     */
    public static final String RUNID = "{:runid}";
    public static final String RUNNUMBER = "{:runnumber}";

    /**
     * Internal run id.
     *
     * If run comes from another CCS this is the internal
     * pc2 submission number for this run.
     */
    public static final String INTERNAL_RUNID = "{:irunid}";

    /**
     * CCS short problem name.
     */
    public static final String PROBLEMSHORTNAME = "{:problemshort}";

    /**
     * CCS problem letter.
     */
    public static final String PROBLEMLETTER = "{:problemletter}";

    /**
     * CCS problem  index (1 based).
     */
    public static final String PROBLEMINDEX = "{:problem}";

    /**
     * Language name.
     */
    public static final String LANGUAGENAME = "{:languagename}";

    /**
     * Language letter.
     */
    public static final String LANGUAGELETTER = "{:languageletter}";

    /**
     * CLICS Language id.
     */
    public static final String LANGUAGEID = "{:languageid}";

    /**
     * Client Id.
     */
    public static final String CLIENTID = "{:clientid}";

    /**
     * Client full name, eg team2
     */
    public static final String CLIENTNAME = "{:clientname}";

    /**
     * Client's site id.
     */
    public static final String CLIENTSITE = "{:clientsite}";

    /**
     * Team Id.
     *
     */
    public static final String TEAMID = "{:teamid}";

    /**
     * Elapsed time in MS.
     */
    public static final String ELAPSEDMS = "{:elapsedms}";

    /**
     * Elapsed time in minutes.
     */
    public static final String ELAPSEDMINUTES = "{:elapsedmins}";

    /**
     * Elapsed time in seconds.
     */
    public static final String ELAPSEDSECONDS = "{:elapsedsecs}";

    /**
     * Client type.
     *
     */
    public static final String CLIENTTYPE = "{:clienttype}";

    /**
     * Language number.
     */
    public static final String LANGUAGE = "{:language}";

    /**
     * Site
     */
    public static final String SITEID = "{:siteid}";

    public static final String[] VARIABLE_NAMES = {//
            BASENAME, //
            CLIENTID, //
            CLIENTTYPE, //
            CLIENTNAME, //
            CLIENTSITE,
            ELAPSEDMINUTES, //
            ELAPSEDMS, //
            ELAPSEDSECONDS, //
            FILELIST, //
            INTERNAL_RUNID, //
            LANGUAGE, //
            LANGUAGENAME, //
            MAINFILE, //
            OPTIONS, //
            PROBLEMSHORTNAME, //
            RUNID, //
            TEAMID, //
            SITEID, //

            "{:ansfile}", //
            "{:executetime}", //
            "{:exitvalue}", //
            "{:infile}", //
            LANGUAGELETTER, //
            "{:outfile}", //
            "{:pc2home}", //
            PROBLEMLETTER, //
            PROBLEMINDEX, //
            "{:timelimit}", //
            "{:validator}", //

    };


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
    public static String replaceString(String origString, String beforeString, String afterString) {

        // SOMEDAY replace this with Java String replace method.

        if (origString == null || afterString == null) {
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
    public String substituteExecutableVariables(IInternalContest contest, Run run, RunFiles runFiles, String origString, //
            ExecutionData executionData, ProblemDataFiles problemDataFiles) {
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

        newString = replaceString(origString, MAINFILE, runFiles.getMainFile().getName());
        newString = replaceString(newString, BASENAME, removeExtension(runFiles.getMainFile().getName()));

        String validatorCommand = null;

        Problem problem = contest.getProblem(run.getProblemId());

        if (problem.getOutputValidatorProgramName() != null) {
            validatorCommand = problem.getOutputValidatorProgramName();
        }

        if (problemDataFiles != null) {
            SerializedFile validatorFile = problemDataFiles.getOutputValidatorFile();
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

                newString = replaceString(newString, LANGUAGE, index);
                newString = replaceString(newString, LANGUAGELETTER, Utilities.convertNumber(index));
            }
        }
        if (run.getProblemId() != null) {
            int index = getProblemIndex(contest, problem);
            if (index > 0) {
                newString = replaceString(newString, PROBLEMINDEX, index);
                newString = replaceString(newString, PROBLEMLETTER, Utilities.convertNumber(index));
            }
        }
        if (run.getSubmitter() != null) {
            newString = replaceString(newString, TEAMID, run.getSubmitter().getClientNumber());
            newString = replaceString(newString, SITEID, run.getSubmitter().getSiteNumber());
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

    /**
     * Replace beforeString with integer.
     * @param origString
     * @param beforeString
     * @param integer
     * @return
     */
    public static String replaceInteger(String origString, String beforeString, int integer) {
        return replaceString(origString, beforeString, Integer.toString(integer));
    }

    /**
     * Replace beforeString with longValue.
     * @param origString
     * @param beforeString
     * @param elapsedMS3
     * @return
     */
    public static String replaceLong(String origString, String beforeString, long longValue) {
        return replaceString(origString, beforeString, Long.toString(longValue));
    }

    /**
     * Create command after substituting various variables.
     *
     * See substitution constants. {@value CommandVariableReplacer#OPTIONS} will substitute:
     *
     * <pre>
     * -p &lt;problem short-name&gt;, string
     * -l &lt;language name&gt;, string
     * -u &lt;team id&gt;, integer
     * -m &lt;main source filename&gt;, string
     * -t &lt;contest-time for submission&gt;, integer
     * -i &lt;run id&gt; unique key for the run, integer
     * -w &lt;team password&gt;, string
     * </pre>
     *
     * @param command
     * @param run
     * @param runFiles
     * @param runDir
     * @param contest
     * @return a file with value substituted for variables.
     * @throws Exception
     */
    public String substituteVariables(String command, IInternalContest contest, Run run, RunFiles runFiles, String runDir, //
            ExecutionData executionData, ProblemDataFiles problemDataFiles) throws Exception {

        String mainfileName = getMainFileName(runDir, runFiles);
        String fileList = mainfileName;

        if (runFiles.getOtherFiles() != null) {
            for (SerializedFile file : runFiles.getOtherFiles()) {
                String outfilename = runDir + File.separator + file.getName();
                fileList += " " + outfilename;
            }
        }

        Problem problem = contest.getProblem(run.getProblemId());
        if (problem == null) {
            throw new Exception("Could not find problem for id=" + run.getProblemId() + " " + run);
        }

        Language language = contest.getLanguage(run.getLanguageId());
        if (language == null) {
            throw new Exception("Could not find language for id=" + run.getLanguageId() + " " + run);
        }

        Account account = contest.getAccount(run.getSubmitter());
        if (account == null) {
            throw new Exception("Could not find account for id=" + run.getSubmitter() + " " + run);
        }

        String newCommand = command;

        newCommand = CommandVariableReplacer.replaceString(newCommand, CommandVariableReplacer.FILELIST, fileList);

        StringBuffer buffer = new StringBuffer();

        // -p <problem short-name>, string
        // -l <language name>, string
        // -u <team id>, integer
        // -m <main source filename>, string
        // -t <contest-time for submission>, integer
        // -i <run id> unique key for the run, integer
        // -w <team password>, string

        buffer.append(" -p ") //
                .append(problem.getShortName()) //
                .append(" -l ") //
                .append(language.getDisplayName()) //
                .append(" -u ") //
                .append(run.getSubmitter().getClientNumber()) //
                .append(" -m ") //
                .append(mainfileName) //
                .append(" -i ") //
                .append(run.getNumber()) //
                .append(" -t ") //
                .append(run.getElapsedMS()); //
        // .append(" -w ") //
        // .append(account.getPassword());

        newCommand = replaceString(newCommand, MAINFILE, mainfileName);

        newCommand = replaceString(newCommand, BASENAME, removeExtension(mainfileName));

        newCommand = replaceString(newCommand, OPTIONS, buffer.toString());

        newCommand = replaceInteger(newCommand, RUNID, run.getNumber());

        newCommand = replaceInteger(newCommand, INTERNAL_RUNID, run.internalRunId());

        newCommand = replaceString(newCommand, PROBLEMSHORTNAME, problem.getShortName());

        newCommand = replaceString(newCommand, LANGUAGENAME, language.getDisplayName());

        ClientId submitter = run.getSubmitter();

        newCommand = replaceInteger(newCommand, CLIENTID, submitter.getClientNumber());

        newCommand = replaceString(newCommand, CLIENTTYPE, submitter.getClientType().toString());

        newCommand = replaceLong(newCommand, ELAPSEDMS, run.getElapsedMS());

        newCommand = replaceLong(newCommand, ELAPSEDMINUTES, run.getElapsedMins());

        newCommand = replaceLong(newCommand, ELAPSEDSECONDS, run.getElapsedMS() / 1000);

        newCommand = substituteExecutableVariables(contest, run, runFiles, newCommand, executionData, problemDataFiles);

        return newCommand;
    }

    /**
     * Returns full path for main file.
     * @param runDir if null just returns mainfilename
     * @param runFiles
     * @return name of main file, if runDir is not null returns rundir + FS + mainfilename.
     */
    public String getMainFileName(String runDir, RunFiles runFiles) {
        if (runDir != null) {
            return runDir + File.separator + runFiles.getMainFile().getName();
        } else {
            return runFiles.getMainFile().getName();
        }
    }

    /**
     * Replaces substitute variables in the execute folder string.
     * This needs a special version (and can't use substituteAllStrings) because some substitute
     * variables do not make sense or are not available at the time the execute folder is needed,
     * such as the test case, package, mainfile, infile, outfile, etc.
     *
     * return string with execute folder relevent field variables filled with values.
     *
     * Each variable will be filled in with values.
     *
     * <pre>
     *             valid fields are:
     *              {:language} - index into languages (1 based)
     *              {:languageletter} - index converted to letter, eg 1=A, 2=B
     *              {:languagename} - Display name of language (spaces converted to _)
     *              {:languageid} - CLICS language id, eg cpp
     *              {:problem} - Index into problem table
     *              {:problemletter} - A,B,C...
     *              {:problemshort} - problem short name
     *              {:teamid} - team's id number
     *              {:siteid} - team's site
     *              {:clientname} - this client's name, eg judge1
     *              {:clientid} - this client's id number, eg. 1
     *              {:clientsite} - this client's site
     *              {:runnumber} - the run number
     * </pre>
     *
     * @param inRun
     *            submitted by team
     * @param origString
     *            - original string to be substituted.
     * @return string with values
     */
    public String substituteExecuteFolderVariables(IInternalContest contest, Log log, Run inRun, String origString) {
        // Make a new copy to start with to avoid issues in the future.
        String newString = origString;

        try {
            if (inRun == null) {
                throw new IllegalArgumentException("Run is null");
            }

            // SOMEDAY LanguageId and ProblemId are now a long string not an int,
            // what should we do?

            if (inRun.getLanguageId() != null) {
                Language language = contest.getLanguage(inRun.getLanguageId());
                int index = getLanguageIndex(contest, language);
                if (index > 0) {
                    newString = replaceString(newString, LANGUAGE, index);
                    newString = replaceString(newString, LANGUAGELETTER, Utilities.convertNumber(index));
                    newString = replaceString(newString, LANGUAGENAME, language.getDisplayName().toLowerCase().replaceAll(" ", "_"));
                    newString = replaceString(newString, LANGUAGEID, language.getID());
                }
            }
            if (inRun.getProblemId() != null) {
                Problem problem = contest.getProblem(inRun.getProblemId());
                if(problem != null) {
                    int index = getProblemIndex(contest, problem);
                    newString = replaceString(newString, PROBLEMINDEX, index);
                    newString = replaceString(newString, PROBLEMLETTER, problem.getLetter());
                    newString = replaceString(newString, PROBLEMSHORTNAME, problem.getShortName());
                }
            }
            if (inRun.getSubmitter() != null) {
                newString = replaceString(newString, TEAMID, inRun.getSubmitter().getClientNumber());
                newString = replaceString(newString, SITEID, inRun.getSubmitter().getSiteNumber());
            }
            newString = replaceString(newString, CLIENTNAME, contest.getClientId().getName());
            newString = replaceString(newString, CLIENTID, contest.getClientId().getClientNumber());
            newString = replaceString(newString, CLIENTSITE, contest.getClientId().getSiteNumber());
            newString = replaceString(newString, RUNNUMBER, Integer.toString(inRun.getNumber()));

        } catch (Exception e) {
            if(log == null) {
                log = StaticLog.getLog();
            }
            if(log != null) {
                log.log(Log.CONFIG, "Exception substituting execute folder variables ", e);
            }
            // carrying on not required to save exception
        }

        return newString;
    }

}
