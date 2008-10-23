package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.IRunJudgement;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;

/**
 * Implementation for IRunJudgement.
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

    public RunJudgementImplemenation(JudgementRecord record, IInternalContest internalContest, IInternalController controller) {
        super();
        this.record = record;
        this.internalContest = internalContest;
        this.controller = controller;
    }

    public IJudgement getJudgement() {
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
}
