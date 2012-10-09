package edu.csus.ecs.pc2.ui;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditAutoJudgeSettingFrameTest extends TestCase {

//    private static boolean canBeAutoJudged(Problem problem) {
//        return problem.isComputerJudged() && problem.isValidatedProblem();
//    }

    public static void main(String[] args) {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(4, 4, 12, 12, true);
        IInternalController controller = sample.createController(contest, true, false);

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {

            problem.setComputerJudged(true);
            problem.setValidatedProblem(true);
            contest.updateProblem(problem);
        }

        EditAutoJudgeSettingFrame frame = new EditAutoJudgeSettingFrame();
        frame.setContestAndController(contest, controller);
        ClientId clientId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        ClientSettings clientSettings = new ClientSettings(clientId);
        frame.setClientSetting(clientId, clientSettings);
        frame.setVisible(true);
    }
}
