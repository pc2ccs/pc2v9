// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.execute;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import edu.csus.ecs.pc2.core.list.ProblemList;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.LogUtilities;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunTestCase;

/**
 * Judgement utilities.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class JudgementUtilites {

    /**
     * 
     */
    private JudgementUtilites() {
        super();
    }

    /**
     * Create judgementRecord, create record based on execution results.
     * 
     * @param contest
     * @param run
     * @param executionData
     * @param validationResults
     * @return
     */
    public static JudgementRecord createJudgementRecord(IInternalContest contest, Run run, ExecutionData executionData, String validationResults) {

        JudgementRecord judgementRecord = null;

        if (executionData.getExecutionException() != null) {

            // Some sort of JE or execution error.

            // Default to a "wrong answer" judgment that cannot have its scoring properties changed 
            // (i.e., always applies penalty)

            ElementId elementId = contest.getJudgements()[2].getElementId();
            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);

            //see if we can find a "Judging Error" judgment, if so use that
            //TODO: NOTE: this code block seems useless, since variable "judgment" is never used
            Judgement judgement = findJudgementByAcronym(contest, "JE");
            if (judgement == null) {
                //we didn't find a JE; choose a non-variable-penalty "no" judgment
                judgement = contest.getJudgements()[2];
            }

            judgementRecord.setValidatorResultString("Execption during execution " + executionData.getExecutionException().getMessage());

        } else if (!executionData.isCompileSuccess()) {
            // Compile failed, darn!

            Judgement judgement = JudgementUtilites.findJudgementByAcronym(contest, "CE");
            String judgementString = "No - Compilation Error"; // default
            ElementId elementId = null;
            if (judgement != null) {
                judgementString = judgement.getDisplayName();
                elementId = judgement.getElementId();
            } else {
                // TODO: find judgement string by name (from somewhere other than the judgements list)
                elementId = contest.getJudgements()[1].getElementId();
            }

            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);

            judgementRecord.setValidatorResultString(judgementString);

        } else if (executionData.isValidationSuccess()) {

            // We got stuff from validator!!
            String results = validationResults;

            if (results == null) {
                results = "Undetermined";
            } else {
                results = results.trim();
            }

            if (results.length() == 0) {
                results = "Undetermined";
            }

            boolean solved = false;

            // Try to find result text in judgement list
            //  (default to a non-variable-penalty "no" judgment to start)
            ElementId elementId = contest.getJudgements()[2].getElementId();
            for (Judgement judgement : contest.getJudgements()) {
                if (judgement.getDisplayName().trim().equalsIgnoreCase(results)) {
                    //found a matching judgment; use that instead of the default
                    elementId = judgement.getElementId();
                }
            }

            // Or perhaps it is a yes? yes?
            Judgement yesJudgement = contest.getJudgements()[0];
            // bug 280 ICPC Validator Interface Standard calls for "accepted" in any case.
            if (results.equalsIgnoreCase("accepted")) {
                results = yesJudgement.getDisplayName();
            }
            if (yesJudgement.getDisplayName().equalsIgnoreCase(results)) {
                elementId = yesJudgement.getElementId();
                solved = true;
            }

            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), solved, true, true);
            judgementRecord.setValidatorResultString(results);

        } else {
            // Something went wrong either during validation or execution
            // Unable to validate result: Undetermined

            //default to a non-variable-scoring "no" judgment
            ElementId elementId = contest.getJudgements()[2].getElementId();
            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
            judgementRecord.setValidatorResultString("Undetermined");

        }

        return judgementRecord;
    }

    public static Judgement findJudgementByAcronym(IInternalContest contest, String acronym) {

        Judgement[] judgements = contest.getJudgements();
        for (Judgement judgement : judgements) {
            if (judgement.getAcronym().equals(acronym)) {
                return judgement;
            }
        }

        return null;
    }

    /**
     * 
     * @param log
     * @param judgeId
     * @param run
     * @param executeDirctoryName
     * @param problem
     * @param testCaseJudgements list of test case judgements for last execute run/test
     * @param executionData
     * @param prefixString
     * @param properties
     */
    public static void dumpJudgementResultsToLog(Log log, ClientId judgeId, Run run, String executeDirctoryName, Problem problem, List<Judgement> testCaseJudgements, ExecutionData executionData,
            String prefixString, Properties properties) {

        // TODO handle max number of AC/Yes test cases to dump

        int maximumLinesToOutput = 20; // SOMEDAY assign max lines to dump from properties

        int maxYesJudgementsToOutput = 5; // SOMEDAY assign max lines to dump from properties

        // For reference, Sample file list from multiple test case problem
        //    cstderr.pc2
        //    cstdout.pc2
        //    estderr.pc2
        //    estdout.pc2
        //    
        //    teamoutput.0.txt
        //    teamoutput.1.txt
        //    teamoutput.2.txt
        //    
        //    NO stderr
        //    
        //    valerr.0.txt
        //    valerr.1.txt
        //    valerr.2.txt

        try {

            int numberOfTestCases = problem.getNumberTestCases();
            log.info(prefixString + " Dumping for " + run + " judge id=" + judgeId);
            log.info(prefixString + " There are " + numberOfTestCases + " for problem " + problem.getDisplayName() + " '" + problem.getShortName() + "'");

            String[] otherFiles = { "cstderr.pc2", "cstdout.pc2", "estderr.pc2", "estdout.pc2" };

            /**
             * Other Files
             */

            for (String otherName : otherFiles) {
                String fullName = executeDirctoryName + File.separator + otherName;
                if (new File(fullName).isFile())
                {
                    LogUtilities.addFileToLog(log, fullName, maximumLinesToOutput, prefixString);
                }
                else
                {
                    log.info(prefixString + " File does not exist " + fullName);
                }
            }

            int totalTestCaseJudgements = testCaseJudgements.size();

            int yesJudgementCount = 0;

            /*
             * Test case files.
             */

            for (int dataSetNumber = 0; dataSetNumber < numberOfTestCases; dataSetNumber++) {

                boolean yesJudgement = false;

                if (dataSetNumber < totalTestCaseJudgements) {
                    Judgement judgement = testCaseJudgements.get(dataSetNumber);
                    String judgementAcronym = judgement.getAcronym();

                    yesJudgement = Judgement.ACRONYM_ACCEPTED.equals(judgementAcronym);
                    if (yesJudgement) {
                        yesJudgementCount++;
                    }
                }

                if (yesJudgement && yesJudgementCount >= maxYesJudgementsToOutput) {

                    // Skip dumping any yes judgements if beyond max amount of them to output
                    continue;
                }

                String teamOutputFilename = "teamoutput." + dataSetNumber + ".txt";
                String fullName = executeDirctoryName + File.separator + teamOutputFilename;

                if (new File(fullName).isFile())
                {
                    LogUtilities.addFileToLog(log, fullName, maximumLinesToOutput, prefixString);
                }
                else
                {
                    log.info(prefixString + " File does not exist " + fullName);
                }
            }

            if (executionData != null)
            {
                Exception ex = executionData.getExecutionException();
                if (ex != null)
                {
                    log.log(Level.INFO, "Exception " + ex.getMessage(), ex);
                }
            }

            log.info(prefixString + "end of dump, run id=" + run.getNumber());

        } catch (Exception e) {
            log.log(Log.WARNING, prefixString + "Warning - while dumping result files " + e.getMessage(), e);
        }
    }

    public static String getExecuteDirectoryName(ClientId id) {
        return "executesite" + id.getSiteNumber() + id.getName();
    }

    /**
     * Get judgements for last set of test cases.
     * 
     * @param contest
     * @param run
     * @return empty list if no test cases judgement in run, else the list of judgements
     */
    public static List<Judgement> getLastTestCaseJudgementList(IInternalContest contest, Run run) {

        List<Judgement> list = new ArrayList<Judgement>();
        
        try {


            if (null != run.getRunTestCases() && run.getRunTestCases().length > 0) {
                RunTestCase[] testCases = run.getRunTestCases();
                for (RunTestCase runTestCase : testCases) {

                    if (runTestCase.getTestNumber() == 0) {
                        // if new set of test cases, start list all over again.
                        list = new ArrayList<Judgement>();
                    }

                    ElementId judgementId = runTestCase.getJudgementId();
                    list.add(contest.getJudgement(judgementId));
                }
            }

        } catch (Exception e) {
            list = new ArrayList<Judgement>();
            System.err.println("ERROR in getLastTestCaseJudgementList "+e.getMessage());
            e.printStackTrace();
        }

        return list;
    }
    
    /**
     * Return a single set of judgements.
     * 
     * If there is more than one site connected, all judgements from all other sites are in the model. 
     * If more than one site is defined, returns the site 1 judgements.
     * 
     * @return a list of judgements for the judges
     */
    public static List<Judgement> getSingleListofJudgements(IInternalContest contest) {
        List<Judgement> list = new ArrayList<Judgement>();

        Judgement[] judgements = contest.getJudgements();

        /**
         * Is a multi site contest?
         */
        boolean multiSiteContest = contest.getSites().length > 1;

        for (Judgement judgement : judgements) {

            if (multiSiteContest) {
                if (judgement.getSiteNumber() == 1) {
                    // only add judgements from site 1 if there is more than one site.
                    list.add(judgement);
                }
            } else {
                // add judgements from model (if not a multiSiteContest
                list.add(judgement);
            }
        }

        if (list.size() == 0) {
            /**
             * A catch all condition. If a rare condition occurs where there are no judgements added to the list, all all model's judgements.
             * 
             * This should never happen.
             */
            list.addAll(Arrays.asList(judgements));
            StaticLog.getLog().log(Log.WARNING, "getSingleListofJudgements, note in multi site did not find judgements from site 1");
        }

        return list;
    }
    
    /**
     * Can Problem be auto judged.?
     * 
     * Problem must:
     * <li> computer judged
     * <li> has a validator
     * 
     * @param problem
     * @return
     */
    public static boolean canBeAutoJudged(Problem problem) {
        return problem.isComputerJudged() && problem.isValidatedProblem();
    }

    /**
     * Returns the list of problems that match filter.
     * 
     * @param contest
     * @param judgesAJProblemsFilter - judge client id
     * @return
     */
    public static ProblemList getAutoJudgedProblemList(IInternalContest contest, Filter judgesAJProblemsFilter) {
        ProblemList list = new ProblemList();
        
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (JudgementUtilites.canBeAutoJudged(problem)) {
               if (judgesAJProblemsFilter.matches(problem)) {
                   list.add(problem);
               }
            }
        }
        
        return list;
    }

    /**
     * Is run queued?
     * @param run
     * @return true if queued for computer judgement, else false.
     */
    public static boolean isQueuedForComputerJudging(Run run) {
        return RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT.equals(run.getStatus());
    }

    
    
    
}
