package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JudgementRecord;

/**
 * Implementation for IRun.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunImplementation implements IRun {

    private boolean judged;

    private boolean solved;

    private boolean deleted;

    private String judgementTitle = "";

    private ITeam submitterTeam;

    private IProblem problem;

    private ILanguage language;

    private int number;

    private int siteNumber;

    private long elapsedMins;

    private ElementId elementId;

    /**
     * 
     * @param run
     * @param internalContest
     */
    public RunImplementation(edu.csus.ecs.pc2.core.model.Run run, IInternalContest internalContest) {

        judged = run.isJudged();
        solved = run.isSolved();
        deleted = run.isDeleted();

        JudgementRecord judgementRecord = run.getJudgementRecord();
        if (judgementRecord != null) {
            String judgementText = internalContest.getJudgement(judgementRecord.getJudgementId()).toString();
            String validatorJudgementName = judgementRecord.getValidatorResultString();
            if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
                if (validatorJudgementName.trim().length() == 0) {
                    validatorJudgementName = "undetermined";
                }
                judgementText = validatorJudgementName;
            }
            judgementTitle = judgementText;
        }

        submitterTeam = new TeamImplementation(run.getSubmitter(), internalContest);

        problem = new ProblemImplementation(run.getProblemId(), internalContest);

        language = new LanguageImplementation(run.getLanguageId(), internalContest);
        
        number = run.getNumber();
        
        siteNumber = run.getSiteNumber();
        
        elapsedMins = run.getElapsedMins();
        
        elementId = run.getElementId();

    }

    public boolean isJudged() {
        return judged;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public ITeam getTeam() {
        return submitterTeam;
    }

    public String getJudgementName() {
        return judgementTitle;
    }

    public IProblem getProblem() {
        return problem;
    }

    public ILanguage getLanguage() {
        return language;
    }

    public int getNumber() {
        return number;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public long getSubmissionTime() {
        return elapsedMins;
    }

    public String[] getSourceCodeFileNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public byte[][] getSourceCodeFileContents() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof RunImplementation) {
            RunImplementation runImplementation = (RunImplementation) obj;
            return (runImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }
    
    

}
