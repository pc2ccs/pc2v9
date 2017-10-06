package edu.csus.ecs.pc2.services.core;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;

/**
 * Run JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class RunJSON extends JSONUtilities {

    public String createJSON(IInternalContest contest, Run run, RunResultFiles files) {

        //      id  ID  yes     no  provided by CCS     identifier of the run
        //      judgement_id    ID  yes     no  provided by CCS     identifier of the judgement this is part of
        //      ordinal     integer     yes     no  provided by CCS     ordering of runs in the judgement (implicit from the test cases)

        StringBuilder stringBuilder = new StringBuilder();

        appendPair(stringBuilder, "id", run.getNumber());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "contest_time", XMLUtilities.formatSeconds(run.getElapsedMS()));
        

        //      judgement_type_id   ID  yes     no  provided by CCS     the verdict of this judgement (i.e. a judgement type)
        //      time    TIME    yes     no  provided by CCS     absolute time when run completed
        //      contest_time    RELTIME     yes     no  provided by CCS     contest relative time when run completed 

        if (run.isJudged()) {
            
            stringBuilder.append(", ");
            
            ElementId judgementId = run.getJudgementRecord().getJudgementId();
            Judgement judgement = contest.getJudgement(judgementId);
            appendPair(stringBuilder, "judgement_type_id", judgement.getAcronym());
        }

        return stringBuilder.toString();

    }
}
