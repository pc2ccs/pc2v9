package edu.csus.ecs.pc2.core.execute;

import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Judgement utilities.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class JudgementUtilites {

    /**
     * Create judgementRecord, create record based on execution results.
     * 
     * @param contest
     * @param run
     * @param executionData
     * @param validationResults
     * @return
     * @throws Exception 
     */
    public static JudgementRecord createJudgementRecord(IInternalContest contest, Run run, ExecutionData executionData, String validationResults) {

        JudgementRecord judgementRecord = null;

        if (executionData.getExecutionException() != null) {
            
            // Some sort of JE or execution error.
            
            ElementId elementId = contest.getJudgements()[1].getElementId();
            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
            
            Judgement judgement = findJudgementByAcronym(contest, "JE");
            if (judgement == null){
                judgement = contest.getJudgements()[1]; // has to be a "no" judgement
            }
            
            judgementRecord.setValidatorResultString("Execption during execution "+executionData.getExecutionException().getMessage());

        } else if (!executionData.isCompileSuccess()) {
            // Compile failed, darn!

            ElementId elementId = contest.getJudgements()[1].getElementId();
            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
            // TODO this needs to be flexible
            judgementRecord.setValidatorResultString("No - Compilation Error");

        } else if (executionData.isValidationSuccess()) {

            // We got stuff from validator!!
            String results = validationResults;

            if (results == null) {
                results = "Undetermined";
            }
            if (results.trim().length() == 0) {
                results = "Undetermined";
            }

            boolean solved = false;

            // Try to find result text in judgement list
            ElementId elementId = contest.getJudgements()[1].getElementId();
            for (Judgement judgement : contest.getJudgements()) {
                if (judgement.getDisplayName().equals(results)) {
                    elementId = judgement.getElementId();
                }
            }

            // Or perhaps it is a yes? yes?
            Judgement yesJudgement = contest.getJudgements()[0];
            // bug 280 ICPC Validator Interface Standard calls for "accepted" in any case.
            if (results.trim().equalsIgnoreCase("accepted")) {
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

            ElementId elementId = contest.getJudgements()[1].getElementId();
            judgementRecord = new JudgementRecord(elementId, contest.getClientId(), false, true, true);
            judgementRecord.setValidatorResultString("Undetermined");

        }

        return judgementRecord;
    }

    private static Judgement findJudgementByAcronym(IInternalContest contest, String acronym) {
        
        Judgement[] judgements = contest.getJudgements();
        for (Judgement judgement : judgements) {
            if (judgement.getAcronym().equals(acronym)){
                
            }
        }
        
        return null;
    }

}
