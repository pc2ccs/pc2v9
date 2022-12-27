package edu.csus.ecs.pc2.ui;

import java.util.Vector;

import edu.csus.ecs.pc2.core.log.NullController;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author laned
 *
 */
public class AutoJudgeAvailablePaneTest extends AbstractTestCase {
    /**
     * Update account to auto judge all problems, update model with new settings.
     * 
     * @param contest
     * @param account
     * @return 
     */
    private static ClientSettings updateAsAutoJudge(IInternalContest contest, Account account) {

        ClientSettings settings = contest.getClientSettings(account.getClientId());
        if (settings == null) {
            settings = new ClientSettings(account.getClientId());
        }
        settings.setAutoJudging(true);

        // auto judge all problems
        Filter autoJudgeFilter = new Filter();
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            autoJudgeFilter.addProblem(problem);
        }
        settings.setAutoJudgeFilter(autoJudgeFilter);
        contest.updateClientSettings(settings);
        return contest.getClientSettings(account.getClientId());
    }

    public static IInternalContest create16RunContest() throws Exception {

        SampleContest sampleContest = new SampleContest();

        String [] runsData = {

                "1,1,A,1,No",  //20
                "2,1,A,3,Yes",  //3 (first yes counts Minutes only)
                "3,1,A,5,No",  //20
                "4,1,A,7,Yes",  //20  
                "5,1,A,9,No",  //20

                "6,1,B,11,No",  //20  (all runs count)
                "7,1,B,13,No",  //20  (all runs count)

                "8,2,A,30,Yes",  //30

                "9,2,B,35,No",  //20 (all runs count)
                "10,2,B,40,No",  //20 (all runs count)
                "11,2,B,45,No",  //20 (all runs count)
                "12,2,B,50,No",  //20 (all runs count)
                "13,2,B,55,No",  //20 (all runs count)

                "14,2,A,30, ", // doesn't count, no after yes
                "15,2,A,25, ", // doesn't count, no after yes

                "16,2,A,330, ",  // doesn't count, yes after yes
        };

        IInternalContest contest = new SampleContest().createContest(1, 3, 12, 12, true);

        for (String runInfoLine : runsData) {
            sampleContest.addARun(contest, runInfoLine);      
        }
        return contest;

    }

    public static void main(String[] args) throws Exception {

        try {
            IInternalContest contest = create16RunContest();
            //        IInternalController controller = sample.createController(contest, false, false);
            NullController controller = new NullController("AutoJudgeAvailablePaneTest.log");
            StaticLog.setLog(controller.getLog());

            ClientId adminClient = contest.getAccounts(Type.ADMINISTRATOR).firstElement().getClientId();
            
            Problem[] problems = contest.getProblems();
            for (Problem problem : problems) {
                problem.setComputerJudged(true);
                problem.setValidatorType(VALIDATOR_TYPE.CLICSVALIDATOR);
                contest.updateProblem(problem);
            }
            
            // update all runs to QUEUED_FOR_COMPUTER_JUDGEMENT

            Run[] runs = contest.getRuns();
            for (Run run : runs) {
                run.setStatus(RunStates.QUEUED_FOR_COMPUTER_JUDGEMENT);
                contest.updateRun(run, adminClient);
            }

            Vector<Account> judges = contest.getAccounts(Type.JUDGE);
            for (Account account : judges) {
                setToUpdateAllAutoJudge(contest, account);
                contest.addAvailableAutoJudge(account.getClientId());
            }
            
            for (Run run : runs) {
                contest.addAvailableAutoJudgeRun(run);
            }
            
            AutoJudgeAvailablePane pane = new AutoJudgeAvailablePane();
            pane.setContestAndController(contest, controller);
            TestingFrame frame = new TestingFrame(pane);

            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void setToUpdateAllAutoJudge(IInternalContest contest, Account account) {
        
        ClientSettings settings = contest.getClientSettings(account.getClientId());
        if (settings == null) {
            settings = new ClientSettings(account.getClientId());
        }
        settings.setAutoJudging(true);
        
        Filter filter = new Filter();
        
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            filter.addProblem(problem);
        }
        
        settings.setAutoJudgeFilter(filter);
        contest.updateClientSettings(settings);
        
    }


}
