package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IJudgement;
import edu.csus.ecs.pc2.api.IRunJudgement;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

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
    
    /**
     * Is a preliminary judgement, by default set false.
     * 
     * In the constructor it will be determined whether to set
     * preliminary to true or not.
     * 
     */
    private boolean preliminaryJudgement = false;

    public RunJudgementImplemenation(JudgementRecord record, Run run, IInternalContest internalContest, IInternalController controller) {
        super();
        this.record = record;
        this.internalContest = internalContest;
        this.controller = controller;
        
        setPreliminaryJudgement (run);
    }

    /**
     * Set/establish whether this judgement is a preliminary judgement or not.
     * 
     * Determine whether this judgement record is a preliminary or final judgement.
     * 
     * @param run
     */
    private void setPreliminaryJudgement(Run run) {

        Problem problem = internalContest.getProblem(run.getProblemId());
        if (problem.isManualReview() && problem.isComputerJudged()) {
            /**
             * Only preliminary possible is if is manual review AND computer judged.
             */

            JudgementRecord[] records = run.getAllJudgementRecords();
            if (records != null) {
                /**
                 * If there are judgements, only the first (computer judged) will be a preliminary judged run.
                 */
                System.out.println(" debug " + records[0].getElementId());
                System.out.println(" debug " + record.getElementId());

                preliminaryJudgement = records[0].getElementId().equals(record.getElementId());
            }
        }
        // else - not possible at this time to be anything else but final judgement

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

    public boolean isPreliminaryJudgement() {
        return preliminaryJudgement;
    }
}
