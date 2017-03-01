package edu.csus.ecs.pc2.convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Methods to load event feed runs into the contest.
 * 
 * @author Douglas A. Lane &lt;laned@ecs.csus.edu&gt;
 */
public class LoadRuns {

    // private boolean debugMode = true;
    private boolean debugMode = false;

    private Language[] allLanguages;

    private Problem[] allProblems;

    private Account[] allTeams;

    private Judgement[] allJudgements;

    private Account[] allJudges;

    /**
     * 
     * @param contest
     * @param judgementId
     * @return true if judgement is a Yes/Accepted
     */
    private boolean isSolved(IInternalContest contest, ElementId judgementId) {
        return contest.getJudgements()[0].getElementId().equals(judgementId);
    }

    /**
     * Checkout a run to a judge.
     * 
     * @param contest
     * @param run
     * @param judgeId
     * @throws RunUnavailableException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     */
    private void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) throws RunUnavailableException, IOException, ClassNotFoundException, FileSecurityException {

        if (run == null) {
            throw new IllegalArgumentException("run is null");
        }

        if (judgeId == null) {
            throw new IllegalArgumentException("judge id is null");
        }

        contest.checkoutRun(run, judgeId, false, false);
    }

    /**
     * Add judgement to run, as if judge had judged it.
     * 
     * @param contest
     * @param run
     * @param judgement
     * @param judgeId
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileSecurityException
     * @throws RunUnavailableException
     */
    public Run addJudgement(IInternalContest contest, Run run, Judgement judgement, ClientId judgeId) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {

        ElementId judgementId = judgement.getElementId();
        boolean solved = isSolved(contest, judgementId);

        JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);

        checkOutRun(contest, run, judgeId);
        contest.addRunJudgement(run, judgementRecord, null, judgeId);

        return contest.getRun(run.getElementId());
    }

    /**
     * Add EF runs to contest.
     * 
     * @see #loadRunsFromEventFeed(String)
     * 
     * @param contest
     * @param runs
     * @param cdpBasePath
     * @param addJudgements
     * @return
     * @throws IllegalContestState
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FileSecurityException
     * @throws RunUnavailableException
     */
    public IInternalContest updateContestFromEFRuns(IInternalContest contest, List<EventFeedRun> runs, String cdpBasePath, boolean addJudgements) throws IllegalContestState, ClassNotFoundException,
            IOException, FileSecurityException, RunUnavailableException {

        if (contest == null) {
            throw new IllegalArgumentException("contest must not be null");
        }

        validateEventFeedRuns(contest, runs);

        Collections.sort(runs, new CompareByRunId());

        String submissionDir = cdpBasePath + File.separator + IContestLoader.SUBMISSIONS_DIRNAME + File.separator;

        ClientId firstJudge = getFirstJudge(contest).getClientId();

        for (EventFeedRun evRun : runs) {

            Account teamAccount = teamLookup(contest, Integer.parseInt(evRun.getTeam()));

            Language language = languageLookup(contest, evRun.getLanguage(), "Java");

            Problem problem = problemLookup(contest, Integer.parseInt(evRun.getProblem()));

            Judgement judgement = problemJudgementAcroLookup(contest, evRun.getResult());

            long ms = evRun.getElapsedMS();

            if (debugMode) {

                System.out.print(" Run " + evRun.getId());
                System.out.print(" teamAccount = " + teamAccount);
                System.out.print(" language = " + language);
                System.out.print(" problem = " + problem);
                System.out.print(" result  = " + evRun.getResult());
                System.out.print(" time = " + evRun.getResult());
                System.out.print(" elap = " + ms);
                System.out.print(" judgement = " + judgement);
                System.out.println();

            }

            notFound("Lanaguage for " + evRun.getLanguage(), language);
            notFound("Problem for " + evRun.getProblem(), problem);
            notFound("Team for " + evRun.getTeam(), teamAccount);

            if (addJudgements) {
                notFound("Judgement  for " + evRun.getResult(), judgement);
            }

            Run run = new Run(teamAccount.getClientId(), language, problem);

            run.setElapsedMS(ms);
            run.setOverRideNumber(Integer.parseInt(evRun.getId()));

            List<String> files = EventFeedUtilities.fetchRunFileNames(submissionDir, evRun.getId());
            String sourceFilename = files.get(0);

            RunFiles runFiles = new RunFiles(run, sourceFilename);

            contest.addRun(run, runFiles);

            if (addJudgements) {
                addJudgement(contest, run, judgement, firstJudge);
            }
        }

        return contest;

    }

    private Account getFirstJudge(IInternalContest contest) {
        Account[] judges = getJudges(contest);
        return judges[0];
    }

    private Judgement problemJudgementAcroLookup(IInternalContest contest, String acronym) {

        Judgement[] judgements = getJudgements(contest);
        for (Judgement judgement : judgements) {
            if (judgement.getAcronym().equalsIgnoreCase(acronym)) {
                return judgement;
            }
        }

        return null;

    }

    private Judgement[] getJudgements(IInternalContest contest) {

        if (allJudgements == null) {
            allJudgements = contest.getJudgements();
        }
        return allJudgements;
    }

    private void notFound(String message, Object object) {

        if (object == null) {
            System.err.println("Not found " + message);
            throw new RuntimeException(message);
        }
    }

    /**
     * 
     * @param contest
     * @param problemNumber
     *            base 1 problem number
     * @return
     */
    private Problem problemLookup(IInternalContest contest, int problemNumber) {
        Problem[] problems = getProblems(contest);
        return problems[problemNumber - 1];
    }

    private Problem[] getProblems(IInternalContest contest) {

        if (allProblems == null) {
            allProblems = contest.getProblems();
        }

        return allProblems;
    }

    private Language languageLookup(IInternalContest contest, String language, String defaultLangName) {

        Language[] languages = getLanguages(contest);
        for (Language language2 : languages) {
            if (language2.getDisplayName().equalsIgnoreCase(language)) {
                return language2;
            }
        }

        for (Language language2 : languages) {
            if (language2.getDisplayName().equalsIgnoreCase(defaultLangName)) {
                return language2;
            }
        }

        return null;
    }

    private Language[] getLanguages(IInternalContest contest) {

        if (allLanguages == null) {
            allLanguages = contest.getLanguages();
        }
        return allLanguages;
    }

    private Account teamLookup(IInternalContest contest, int teamNum) {

        Account[] teams = getTeams(contest);
        for (Account account : teams) {
            if (account.getClientId().getClientNumber() == teamNum) {
                return account;
            }
        }

        return null;
    }

    private Account[] getTeams(IInternalContest contest) {
        if (allTeams == null) {
            Vector<Account> teamAccounts = contest.getAccounts(Type.TEAM);
            allTeams = (Account[]) teamAccounts.toArray(new Account[teamAccounts.size()]);
        }
        return allTeams;
    }

    private Account[] getJudges(IInternalContest contest) {
        if (allJudges == null) {
            Vector<Account> teamAccounts = contest.getAccounts(Type.JUDGE);
            allJudges = (Account[]) teamAccounts.toArray(new Account[teamAccounts.size()]);
        }
        return allJudges;
    }

    /**
     * 
     * @see #loadRunsFromEventFeed(String).
     * 
     * @param contest
     * @param runs
     * @throws IllegalContestState
     */
    public void validateEventFeedRuns(IInternalContest contest, List<EventFeedRun> runs) throws IllegalContestState {

        int zeroElapsedCount = 0;
        for (EventFeedRun eventFeedRun : runs) {
            if (eventFeedRun.getElapsedMS() == 0) {
                zeroElapsedCount++;
            }
        }
        if (zeroElapsedCount > 0) {
            System.out.println("Warning in " + runs.size() + " runs there were " + zeroElapsedCount + " runs with 0 ms submission/elapsed time");
        }

        // Teams

        int teamCount = EventFeedUtilities.getMaxTeam(runs);
        int curteamCount = contest.getAccounts(Type.TEAM).size();

        int missingTeams = teamCount - curteamCount;
        if (missingTeams > 0) {
            throw new IllegalContestState("EF has " + teamCount + " teams, contest has " + curteamCount + " teams");
        }

        // Problems

        int pCount = EventFeedUtilities.getMaxProblem(runs);
        int numProblems = contest.getProblems().length;
        int probDiff = numProblems - pCount;

        if (probDiff < 0) {
            // need to add problem

            throw new IllegalContestState("EF has " + pCount + " problems, contest has " + numProblems + " problems");

            // for (int i = probDiff; i != 0; i++)
            // {
            // System.out.println("Add new Problem ");
            // }
        }

        // Languages

        String[] langs = EventFeedUtilities.getAllLanguages(runs);
        int numberLanguaes = contest.getLanguages().length;
        int langDiff = numberLanguaes - langs.length;

        if (langDiff < 0) {
            throw new IllegalContestState("EF has " + langs.length + " languages, contest has " + numberLanguaes + " languages");
        }

        Judgement[] judgements = contest.getJudgements();
        if (judgements.length < 5) {
            throw new IllegalContestState("Not enough judgements found in contest model");
        }

        // check if all judgments acro in EF are present in contest.

        String[] efjudgements = getAllJudgements(runs);
        for (String efJudge : efjudgements) {

            boolean foundId = false;
            for (Judgement judgement : judgements) {
                if (efJudge.equals(judgement.getAcronym())) {
                    foundId = true;
                }
            }
            if (!foundId) {
                throw new IllegalContestState("EF result/acronym " + efJudge + " not found in jugements ");
            }
        }

    }

    /**
     * Get list of all results/judgement acronyms in a list of runs.
     * 
     * @param runs
     * @return
     */
    private String[] getAllJudgements(List<EventFeedRun> runs) {
        Map<String, String> map = new HashMap<>();
        for (EventFeedRun eventFeedRun : runs) {
            String res = eventFeedRun.getResult();
            if (!isEmpty(res)) {
                map.put(res, res);
            }
        }
        Set<String> keys = map.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    private boolean isEmpty(String res) {
        if (res != null && res.trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Reads event feed creates EventFeedRuns.
     * 
     * @param eventFeedFileName
     * @return
     * @throws Exception
     */
    public List<EventFeedRun> loadRunsFromEventFeed(String eventFeedFileName) throws Exception {

        List<EventFeedRun> runs = new ArrayList<EventFeedRun>();

        try {

            XMLDomParse1 parse1 = new XMLDomParse1();
            Document document = parse1.create(eventFeedFileName);

            // find nodes for path
            String path = "/contest/run/*";
            NodeList nodes = parse1.getNodes(document, path);

            Properties[] runPropertyList = parse1.create(nodes, EventFeedRun.ID_TAG_NAME);
            runs = EventFeedRun.toRuns(runPropertyList, true);

        } catch (Exception e) {
            throw new Exception("Problem parsing " + eventFeedFileName, e.getCause());
        }

        return runs;
    }

    /**
     * Load Event Feed runs, adds run judgements.
     * 
     * @param contest
     * @param runs
     * @param cdpBasePath
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalContestState
     * @throws IOException
     * @throws FileSecurityException
     * @throws RunUnavailableException
     */
    public IInternalContest updateContestFromEFRuns(IInternalContest contest, List<EventFeedRun> runs, String cdpBasePath) throws ClassNotFoundException, IllegalContestState, IOException,
            FileSecurityException, RunUnavailableException {
        return updateContestFromEFRuns(contest, runs, cdpBasePath, true);
    }

}
