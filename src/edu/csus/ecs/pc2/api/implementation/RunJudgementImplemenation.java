package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.IRunJudgement;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Implementation for IRunJudgement.
 * 
 * A single judgement.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class RunJudgementImplemenation implements IRunJudgement {

    private JudgementRecord record = null;

    private IInternalContest internalContest = null;

    @SuppressWarnings("unused")
    private IInternalController controller = null;
    
    public RunJudgementImplemenation(JudgementRecord record, Run run, IInternalContest internalContest, IInternalController controller) {
        super();
        this.record = record;
        this.internalContest = internalContest;
        this.controller = controller;
    }

    public IJudgement getJudgement() {

        // TODO Handle when judgement text should be from validator results
        
//        String validatorJudgementName = judgementRecord.getValidatorResultString();
//        if (judgementRecord.isUsedValidator() && validatorJudgementName != null) {
//            if (validatorJudgementName.trim().length() == 0) {
//                validatorJudgementName = "undetermined";
//            }
//            judgementText = validatorJudgementName;
//        }
        
        Judgement judgement = internalContest.getJudgement(record.getJudgementId());
        return new JudgementImplementation(judgement);
    }

    public boolean isActive() {
        return record.isActive();
    }

    public boolean isComputerJudgement() {
        return record.isComputerJudgement();
    }

    public boolean isSendToTeam() {
        return record.isSendToTeam();
    }

    public boolean isPreliminaryJudgement() {
        return record.isPreliminaryJudgement();
    }

    public boolean isSolved() {
        return record.isSolved();
    }
    
    public IClient getJudge(){
        return new ClientImplementation(record.getJudgerClientId(), internalContest);
    }
}
