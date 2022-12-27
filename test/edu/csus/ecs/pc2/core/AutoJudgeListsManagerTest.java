// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.list.AutoJudgeListsManager;
import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane
 */
public class AutoJudgeListsManagerTest extends AbstractTestCase {

    SampleContest sample = new SampleContest();

    /**
     * Test add and remove judge from AJ list.
     * 
     * @throws Exception
     */
    public void testaddAvailableAutoJudge() throws Exception {

        AutoJudgeListsManager handler = new AutoJudgeListsManager();

        assertEquals("AJs in list ", 0, handler.getAvailableAJList().size());
        assertEquals("Runs in list ", 0, handler.getAvailableAJRuns().size());

        IInternalContest contest = sample.createStandardContest();
        List<Run> listRuns = addSomeRuns(contest);
        assertEquals("Runs added ", 16, listRuns.size());

        Run[] runs = contest.getRuns();

        handler.addRunToAutoJudge(runs[0]);
        handler.addRunToAutoJudge(runs[1]);
        handler.addRunToAutoJudge(runs[2]);

        assertEquals("Runs in list ", 3, handler.getAvailableAJRuns().size());

        VALIDATOR_TYPE type = VALIDATOR_TYPE.CUSTOMVALIDATOR;

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            // set as an auto judged problem
            problem.setComputerJudged(true);
            problem.setValidatorType(type);
            contest.updateProblem(problem);
        }

        // create 12 judges
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 12, true);

        ClientId autojudge = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

//        Vector<Account> judges = contest.getAccounts(Type.JUDGE);
//        for (Account judgeaccount : judges) {
//            addAutoJudge(contest, judgeaccount, problems);
//        }

        addAutoJudge(contest, autojudge, problems);
        AvailableAJ availaj = handler.addAvailableAutoJudge(contest, autojudge);
        assertNotNull("Expecting to add aj " + autojudge, availaj);

        assertEquals("AJs in list ", 1, handler.getAvailableAJList().size());
        assertEquals("Runs in list ", 3, handler.getAvailableAJRuns().size());

        // findRunToAutoJudge will remove items from list if run found
        Run run = handler.findRunToAutoJudge(contest, autojudge);
        assertNotNull(run);

        assertEquals("AJs in list ", 0, handler.getAvailableAJList().size());
        assertEquals("Runs in list ", 2, handler.getAvailableAJRuns().size());

    }

    /**
     * Test addAvailableAutoJudgeRun and findRunToAutoJudge using IInternalContest methods.
     * 
     * @throws Exception
     */
    public void ttestaddAvailableAutoJudgeRunUsingContest() throws Exception {

        IInternalContest contest = sample.createStandardContest();
        assertEquals("AJs in list ", 0, contest.getAvailableAutoJudges().size());
        assertEquals("Runs in list ", 0, contest.getAvailableAutoJudgeRuns().size());

        List<Run> listRuns = addSomeRuns(contest);
        assertEquals("Runs added ", 16, listRuns.size());

        Run[] runs = contest.getRuns();

        contest.addAvailableAutoJudgeRun(runs[0]);
        contest.addAvailableAutoJudgeRun(runs[1]);
        contest.addAvailableAutoJudgeRun(runs[2]);

        assertEquals("Runs in list ", 3, contest.getAvailableAutoJudgeRuns().size());

        VALIDATOR_TYPE type = VALIDATOR_TYPE.CUSTOMVALIDATOR;

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            // set as an auto judged problem
            problem.setComputerJudged(true);
            problem.setValidatorType(type);
            contest.updateProblem(problem);
        }

        // create 12 judges
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 12, true);

        ClientId autojudge = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        addAutoJudge(contest, autojudge, problems);
        AvailableAJ availaj = contest.addAvailableAutoJudge(autojudge);
        assertNotNull("Expecting to add aj " + autojudge, availaj);

        assertEquals("AJs in list ", 1, contest.getAvailableAutoJudges().size());
        assertEquals("Runs in list ", 3, contest.getAvailableAutoJudgeRuns().size());

//        AutoJudgeListsManager.dump("debug junit", System.out, contest);

        Run run = contest.findRunToAutoJudge(autojudge);
        assertNotNull(run);

        assertEquals("AJs in list ", 0, contest.getAvailableAutoJudges().size());
        assertEquals("Runs in list ", 2, contest.getAvailableAutoJudgeRuns().size());

    }

    /**
     * Test findAutoJudgeForRun.
     * 
     * @throws Exception
     */
    public void testfindAutoJudgeForRun() throws Exception {
        IInternalContest contest = sample.createStandardContest();
        assertEquals("AJs in list ", 0, contest.getAvailableAutoJudges().size());
        assertEquals("Runs in list ", 0, contest.getAvailableAutoJudgeRuns().size());

        List<Run> listRuns = addSomeRuns(contest);
        assertEquals("Runs added ", 16, listRuns.size());

        Run[] runs = contest.getRuns();
        contest.addAvailableAutoJudgeRun(runs[0]);
        contest.addAvailableAutoJudgeRun(runs[1]);
        contest.addAvailableAutoJudgeRun(runs[2]);

        assertEquals("Runs in list ", 3, contest.getAvailableAutoJudgeRuns().size());

        VALIDATOR_TYPE type = VALIDATOR_TYPE.CUSTOMVALIDATOR;

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            // set as an auto judged problem
            problem.setComputerJudged(true);
            problem.setValidatorType(type);
            contest.updateProblem(problem);
        }

        // create 12 judges
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 12, true);

        ClientId autojudge = contest.getAccounts(Type.JUDGE).firstElement().getClientId();

        addAutoJudge(contest, autojudge, problems);
        AvailableAJ availaj = contest.addAvailableAutoJudge(autojudge);
        assertNotNull("Expecting to add aj " + autojudge, availaj);

        assertEquals("AJs in list ", 1, contest.getAvailableAutoJudges().size());
        assertEquals("Runs in list ", 3, contest.getAvailableAutoJudgeRuns().size());

        Run run = contest.getRuns()[0];
        ClientId aj = contest.findAutoJudgeForRun(run);
        assertNotNull(aj);

        assertEquals("AJs in list ", 0, contest.getAvailableAutoJudges().size());
        assertEquals("Runs in list ", 2, contest.getAvailableAutoJudgeRuns().size());

    }

    /**
     * update judge to become an auto judge input problems.
     * 
     * @param contest
     * @param autojudge
     * @param problems
     * @return
     */
    private ClientSettings addAutoJudge(IInternalContest contest, ClientId autojudge, Problem[] problems) {

        ClientSettings clientSettings = contest.getClientSettings(autojudge);

        Filter autoJudgeFilter = new Filter();

        for (Problem problem : problems) {
            autoJudgeFilter.addProblem(problem);
        }

        if (clientSettings == null) {
            clientSettings = new ClientSettings(autojudge);
        }

        clientSettings.setAutoJudgeFilter(autoJudgeFilter);
        clientSettings.setAutoJudging(true);

        contest.updateClientSettings(clientSettings);

        return clientSettings;
    }

    /**
     * Add some runs.
     * 
     * @param contest
     * @return list of added runs.
     * @throws RunUnavailableException
     * @throws FileSecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private List<Run> addSomeRuns(IInternalContest contest) throws ClassNotFoundException, IOException, FileSecurityException, RunUnavailableException {

        List<Run> list = new ArrayList<Run>();

        String[] runsData = {

                "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (first yes counts Minutes only)
                "3,1,A,5,No", // 20
                "4,1,A,7,Yes", // 20
                "5,1,A,9,No", // 20

                "6,1,B,11,No", // 20 (all runs count)
                "7,1,B,13,No", // 20 (all runs count)

                "8,2,A,30,Yes", // 30

                "9,2,B,35,No", // 20 (all runs count)
                "10,2,B,40,No", // 20 (all runs count)
                "11,2,B,45,No", // 20 (all runs count)
                "12,2,B,50,No", // 20 (all runs count)
                "13,2,B,55,No", // 20 (all runs count)

                "14,2,A,30,No", // doesn't count, no after yes
                "15,2,A,25,No", // doesn't count, no after yes

                "16,2,A,330,Yes", // doesn't count, yes after yes

        };

        for (String runInfoLine : runsData) {
            Run newRun = sample.addARun(contest, runInfoLine);
            list.add(newRun);
        }

        return list;
    }

}
