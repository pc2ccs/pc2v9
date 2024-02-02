// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeam;
import edu.csus.ecs.pc2.core.list.RunCompartorByElapsed;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunUtilities;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilities;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;
import edu.csus.ecs.pc2.util.ScoreboardVariableReplacer;

/**
 * "New" Scoring Algorithm implementation.
 * 
 * Uses same SA as the {@link DefaultScoringAlgorithm}
 * 
 * @author pc2@ecs.csus.edu
 */
public class NewScoringAlgorithm extends Plugin implements INewScoringAlgorithm {

    /**
     * 
     */
    private static final long serialVersionUID = -7815725774105747895L;

    /**
     * @see #setBlockRanking(boolean)
     */
    private boolean blockRanking = true;

    private boolean respectEOC = false;

    private DefaultStandingsRecordComparator comparator = new DefaultStandingsRecordComparator();

    private PermissionList permissionList = new PermissionList();

    /**
     * Return a list of regional winners.
     * 
     * <br>
     * If there is more than one winner (tie) in a region will all winners (rank 1).
     * 
     * @param contest
     * @param properties
     * @return
     * @throws IllegalContestState
     */
    // TODO SA SOMEDAY Move this to a SA Utility Class
    public StandingsRecord[] getRegionalWinners(IInternalContest contest, Properties properties) throws IllegalContestState {

        StandingsRecord[] records = getStandingsRecords(contest, properties);

        Vector<StandingsRecord> outVector = new Vector<StandingsRecord>();

        for (StandingsRecord record : records) {
            if (record.getGroupRankNumber() == 1) {
                outVector.addElement(record);
            }
        }

        return (StandingsRecord[]) outVector.toArray(new StandingsRecord[outVector.size()]);
    }

    /**
     * Get Regional Winner.
     * 
     * Will return StandingsRecord for a single regional winner. If there is a tie for first place this method will return null.
     * 
     * @param contest
     * @param properties
     * @param group
     * @return null if no regional winner or if more than one regional winner.
     * @throws IllegalContestState
     */
    // TODO SA SOMEDAY Move this to a SA Utility Class
    public StandingsRecord getRegionalWinner(IInternalContest contest, Properties properties, Group group) throws IllegalContestState {

        StandingsRecord[] records = getStandingsRecords(contest, properties);

        StandingsRecord outRecord = null;

        for (StandingsRecord record : records) {
            if (record.getGroupRankNumber() == 1) {

                Account account = contest.getAccount(record.getClientId());
                if (account == null) {
                    // TODO throw exception/indicate an error.
                    continue;
                }
                Group teamGroup = contest.getGroup(account.getGroupId());
                if (teamGroup == null) {
                    // TODO throw exception/indicate an error.
                    continue;
                }

                if (teamGroup.equals(group)) {
                    if (outRecord != null) {
                        // More than one winner
                        return null;
                    }
                    outRecord = record;
                }
            }
        }

        return outRecord;
    }


    @Override
    public StandingsRecord[] getStandingsRecords(IInternalContest contest, Properties properties) throws IllegalContestState {
        return getStandingsRecords(contest, null, null, properties, false, null);
    }
    
    private StandingsRecord[] getStandingsRecords(IInternalContest contest, Integer divisionNumber, Group group, Properties properties) throws IllegalContestState {
        return getStandingsRecords(contest, divisionNumber, group, properties, false, null);
    }
    
    /**
     * Returns sorted and ranked StandingsRecord for optional divisionNumber, if honorScoreboadFreeze is true then run results
     * from the freeze period will be hidden,  unless the contest is unfrozen.
     * 
     * NB. This should probably be in INewScoringAlgorithm
     * 
     * @param contest
     * @param divisionNumber
     * @param properties
     * @param honorScoreboardFreeze
     * @param runs
     * @return
     * @throws IllegalContestState
     */
    // This should probably be in INewScoringAlgorithm
    public StandingsRecord[] getStandingsRecords(IInternalContest contest, Integer divisionNumber, Properties properties, boolean honorScoreboardFreeze, Run [] runs) throws IllegalContestState {
        return getStandingsRecords(contest, divisionNumber, null, properties, true, runs);
    }

    /**
     * Returns sorted and ranked StandingsRecord for optional divisionNumber and/or Group, if honorScoreboadFreeze is true then run results
     * from the freeze period will be hidden,  unless the contest is unfrozen.
     * 
     * NB. This should probably be in INewScoringAlgorithm
     * 
     * @param contest
     * @param divisionNumber get standings for this division, null for all
     * @param group get standings for this group, null for all
     * @param properties
     * @param honorScoreboardFreeze
     * @param runs 
     * @return ranked StandingsRecords.
     * @throws IllegalContestState
     */
    public StandingsRecord[] getStandingsRecords(IInternalContest contest, Integer divisionNumber, Group group, Properties properties, boolean honorScoreboardFreeze, Run [] runs) throws IllegalContestState {
        
        if (contest == null){
            throw new IllegalArgumentException("contest is null");
        }
        
        setContest(contest);

        /*
         * Get all the teams, then create a new vector of only those teams shown on the
         * scoreboard.  The new vector is used for the standings computations.
         */
        Vector<Account> allAccountVector = getContest().getAccounts(Type.TEAM);
        Vector<Account> accountVector = new Vector<Account>();
        for(Account av : allAccountVector) {
            if(av.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                if (group != null) {
                    // if this client is not a member of the desired group, skip it
                    if(group != ScoreboardUtilities.getGroup(contest, av.getClientId())){
                        continue;
                    }
                }
                if (divisionNumber != null) {
                    String div = ScoreboardUtilities.getDivision(contest, av.getClientId());
                    if (! divisionNumber.toString().trim().equals(div.trim())){
                        /**
                         * If this account is NOT in the same division as the divisionNumber, then do not account to list of accounts on scoreboard, skip to next account.
                         */
                        continue;
                    }
                }
                accountVector.add(av);
            }
        }
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);

        // Kludge for DefaultStandingsRecordComparator
        AccountList accountList = new AccountList();
        for (Account account : accounts) {
            accountList.add(account);
        }
        comparator.setCachedAccountList(accountList);

        if (runs == null) {
            runs = getContest().getRuns();
        }

        respectEOC = isAllowed(getContest(), getContest().getClientId(), Permission.Type.RESPECT_EOC_SUPPRESSION);

        if (respectEOC) {
            runs = filterRunsbyEOC(getContest(), runs);
        }
        if(honorScoreboardFreeze) {
            runs = filterRunsByScoreboardFreeze(getContest(), runs);
        }

        StandingsRecord[] standings = computeStandingStandingsRecords(runs, accounts, properties, getContest().getProblems());

        Arrays.sort(standings, comparator);

        if (blockRanking) {
            assignRanksBlock(standings);
        } else {
            assignRanks(standings);
        }

        assignGroupRanks(getContest(), standings);

        return standings;
    }

    private Run[] filterRunsbyEOC(IInternalContest contest, Run[] runs) {

        Vector<Run> vector = new Vector<Run>();
        // now check EOC settings
        JudgementNotificationsList judgementNotificationsList = contest.getContestInformation().getJudgementNotificationsList();
        ContestTime contestTime = contest.getContestTime();

        for (Run run : runs) {
            Run runToAdd = run;

            if (respectEOC && RunUtilities.supppressJudgement(judgementNotificationsList, run, contestTime)) {
                /**
                 * If we are suppose to suppress this judgement, then change the run to a NEW run.
                 */
                runToAdd = RunUtilities.createNewRun(run, contest);
            }
            vector.add(runToAdd);
        }

        return (Run[]) vector.toArray(new Run[vector.size()]);
    }

    private Run[] filterRunsByScoreboardFreeze(IInternalContest contest, Run[] runs) {

        Vector<Run> vector = new Vector<Run>();
        // now check EOC settings
        long freezeTime = Utilities.getFreezeTime(contest);
        boolean unfrozen = contest.getContestInformation().isUnfrozen();

        for (Run run : runs) {
            Run runToAdd = run;

            if (!unfrozen && RunUtilities.supppressJudgement(run, freezeTime)) {
                /**
                 * If we are suppose to suppress this judgement, then change the run to a NEW run.
                 */
                runToAdd = RunUtilities.createNewRun(run, contest);
            }
            vector.add(runToAdd);
        }

        return (Run[]) vector.toArray(new Run[vector.size()]);
    }
    
    @Override
    public String getStandings(IInternalContest contest, Properties properties, Log inputLog) throws IllegalContestState {
        return getStandings(contest, null, null, null, properties, inputLog);
    }
    
    @Override
    public String getStandings(IInternalContest contest, Run[] runs, Integer divisionNumber, Properties properties, Log inputLog) throws IllegalContestState {
        return getStandings(contest, null, null, null, properties, inputLog);
    }
    
    @Override
    // TODO SA SOMEDAY Move this to a SA Utility Class
    // returns XML String for standings.
    public String getStandings(IInternalContest contest, Run[] runs, Integer divisionNumber, Group group, Properties properties, Log inputLog) throws IllegalContestState {

        StandingsRecord[] standings = getStandingsRecords(contest, divisionNumber, group, properties);

        XMLMemento mementoRoot = XMLMemento.createWriteRoot("contestStandings");
        IMemento summaryMememento = createSummaryMomento(contest.getContestInformation(), mementoRoot);

        dumpGroupList(contest.getGroups(), mementoRoot);

        Problem[] problems = contest.getProblems();
        summaryMememento.putLong("problemCount", problems.length);
        Site[] sites = contest.getSites();
        summaryMememento.putInteger("siteCount", sites.length);
        Group[] groups = contest.getGroups();
        if (groups != null) {
            dumpGroupList(groups, summaryMememento);
        }

        int indexNumber = 0;
        for (StandingsRecord standingsRecord : standings) {
            addTeamMemento(mementoRoot, contest, standingsRecord, indexNumber);
            indexNumber++;
        }

        GrandTotals grandTotals = addProblemSummaryMememento(summaryMememento, standings, contest, contest.getProblems());

        addGrandTotals(summaryMememento, grandTotals);

        String xmlString = null;

        try {
            xmlString = mementoRoot.saveToString();
        } catch (IOException e) {
            IllegalContestState state = new IllegalContestState(e.getLocalizedMessage());
            state.setStackTrace(e.getStackTrace());
            throw state;
        }

        return xmlString;
    }

    /**
     * 
     * @param standingsRecord
     * @param standingsRecord2
     * @return true if records are tied.
     */
    boolean isTied(StandingsRecord standingsRecord, StandingsRecord standingsRecord2) {

        if (standingsRecord2.getNumberSolved() != standingsRecord.getNumberSolved()) {
            return false;
        }
        if (standingsRecord2.getPenaltyPoints() != standingsRecord.getPenaltyPoints()) {
            return false;
        }
        if (standingsRecord2.getLastSolved() != standingsRecord.getLastSolved()) {
            return false;
        }
        return true;
    }

    // TODO SA SOMEDAY Move this to a SA Utility Class
    private void assignRanksBlock(StandingsRecord[] standings) {

        int rank = 1;

        if (standings.length > 0) {
            standings[0].setRankNumber(rank);
        }

        int numInBlock = 0;

        if (standings.length > 1) {

            for (int i = 1; i < standings.length; i++) {
                if (!isTied(standings[i], standings[i - 1])) {
                    rank++;
                    rank += numInBlock;
                    numInBlock = 0;
                } else {
                    numInBlock++;
                }
                // TODO handle other tie breakers ?

                standings[i].setRankNumber(rank);
            }
        }
    }

    // TODO SA SOMEDAY Move this to a SA Utility Class
    // TODO SA figure out how to make isTied abstract or an interface
    private void assignRanks(StandingsRecord[] standings) {

        int rank = 1;
        standings[0].setRankNumber(rank);

        if (standings.length > 1) {

            for (int i = 1; i < standings.length; i++) {
                if (!isTied(standings[i], standings[i - 1])) {
                    rank++;
                }
                // TODO handle other tie breakers ?

                standings[i].setRankNumber(rank);
            }
        }

    }

    // TODO SA SOMEDAY Move this to a SA Utility Class
    private void assignGroupRanks(IInternalContest contest, StandingsRecord[] standings) {

        Group[] groups = contest.getGroups();

        if (groups == null || groups.length == 0) {
            /**
             * Nothing to rank
             */
            return;
        }

        for (Group group : groups) {

            int groupRank = 0;
            int lastRank = 0;
            int groupId = group.getGroupId();

            for (StandingsRecord standingsRecord : standings) {

                Account account = contest.getAccount(standingsRecord.getClientId());
                if (account == null) {
                    // TODO throw exception/indicate an error.
                    continue;
                }

                ElementId groupElementId = account.getGroupId();
                if (groupElementId == null) {
                    // TODO throw exception/indicate an error.
                    continue;
                }

                Group teamGroup = contest.getGroup(groupElementId);
                if (teamGroup == null) {
                    // TODO throw exception/indicate an error.
                    continue;
                }

                int teamGroupId = teamGroup.getGroupId();

                if (groupId == teamGroupId) {
                    if (lastRank != standingsRecord.getRankNumber()) {
                        lastRank = standingsRecord.getRankNumber();
                        groupRank++;
                    }
                    standingsRecord.setGroupRankNumber(groupRank);
                }
            }
        }
    }

    /**
     * Create XML problem (for all problems).
     * 
     * @param summaryMememento
     * @param standings
     * @param contest
     * @param problems
     * @return
     */
    // TODO SA SOMEDAY Move this to a SA Utility Class
    private GrandTotals addProblemSummaryMememento(IMemento summaryMememento, StandingsRecord[] standings,
            IInternalContest contest, Problem[] problems) {

        GrandTotals grandTotals = new GrandTotals();

        long[] fastestSolved = new long[problems.length];
        long[] lastSolutionTime = new long[problems.length];
        int[] numberSolved = new int[problems.length];
        int[] numberAttempts = new int[problems.length];

        for (StandingsRecord standingsRecord : standings) {

            SummaryRow summaryRow = standingsRecord.getSummaryRow();
            for (int i = 0; i < problems.length; i++) {
                ProblemSummaryInfo problemSummaryInfo = summaryRow.get(i + 1);

                if (problemSummaryInfo != null) {
                    long solveTime = problemSummaryInfo.getSolutionTime();
                    if (fastestSolved[i] == 0 || solveTime < fastestSolved[i]) {
                        fastestSolved[i] = solveTime;
                    }
                    if (solveTime > lastSolutionTime[i]) {
                        lastSolutionTime[i] = solveTime;
                    }
                    if (problemSummaryInfo.isSolved()) {
                        numberSolved[i]++;
                    }
                    numberAttempts[i] += problemSummaryInfo.getNumberSubmitted();
                }
            }
        }

        for (int i = 0; i < problems.length; i++) {
            int id = i + 1;

            IMemento problemMemento = summaryMememento.createChild("problem");
            problemMemento.putInteger("id", id);
            problemMemento.putString("title", problems[i].getDisplayName());

            problemMemento.putLong("attempts", numberAttempts[i]);
            grandTotals.incrementTotalAttempts(numberAttempts[i]);

            problemMemento.putLong("numberSolved", numberSolved[i]);
            grandTotals.incrementTotalSolutions(numberSolved[i]);

            if (numberSolved[i] > 0) {
                problemMemento.putLong("bestSolutionTime", fastestSolved[i]);
                problemMemento.putLong("lastSolutionTime", lastSolutionTime[i]);
            }
        }

        return grandTotals;
    }

    /**
     * Create an XML teamStanding element for a team.
     * 
     * @param mementoRoot
     * @param contest
     * @param standingsRecord
     * @param indexNumber
     * @return teamStanding XML element for team (StandingsRecord.getClientId()).
     */
    // TODO SA SOMEDAY Move this to a SA Utility Class
    private IMemento addTeamMemento(IMemento mementoRoot, IInternalContest contest, StandingsRecord standingsRecord, int indexNumber) {

        IMemento standingsRecordMemento = mementoRoot.createChild("teamStanding");
        
        String teamVarDisplayString = contest.getContestInformation().getTeamScoreboardDisplayFormat();
        ElementId groupId = contest.getAccount(standingsRecord.getClientId()).getGroupId();
        Group group = null;
        if (groupId != null) {
            group = contest.getGroup(groupId);    
        }

        // if (standingsRecord.getNumberSolved() > 0){
        standingsRecordMemento.putLong("firstSolved", standingsRecord.getFirstSolved());
        standingsRecordMemento.putLong("lastSolved", standingsRecord.getLastSolved());

        standingsRecordMemento.putLong("points", standingsRecord.getPenaltyPoints());
        standingsRecordMemento.putInteger("solved", standingsRecord.getNumberSolved());
        standingsRecordMemento.putInteger("rank", standingsRecord.getRankNumber());
        standingsRecordMemento.putInteger("index", indexNumber);
        Account account = contest.getAccount(standingsRecord.getClientId());
 
        standingsRecordMemento.putString("teamName", ScoreboardVariableReplacer.substituteDisplayNameVariables(teamVarDisplayString, account, group));
        
        standingsRecordMemento.putInteger("teamId", account.getClientId().getClientNumber());
        standingsRecordMemento.putInteger("teamSiteId", account.getClientId().getSiteNumber());
        standingsRecordMemento.putString("teamKey", account.getClientId().getTripletKey());
        standingsRecordMemento.putString("teamExternalId", account.getExternalId());
        if (account.getAliasName().trim().equals("")) {
            standingsRecordMemento.putString("teamAlias", account.getDisplayName() + " (not aliasesd)");
        } else {
            standingsRecordMemento.putString("teamAlias", account.getAliasName().trim());
        }

        ElementId elementId = account.getGroupId();
        if (elementId != null && contest.getGroup(elementId) != null) {
            standingsRecordMemento.putInteger("groupRank", standingsRecord.getGroupRankNumber());
            standingsRecordMemento.putString("teamGroupName", group.getDisplayName());
            // TODO dal CRITICAL
            // standingsRecordMemento.putInteger("teamGroupId", group.get()+1);
            standingsRecordMemento.putInteger("teamGroupExternalId", group.getGroupId());
        }
        Problem[] problems = contest.getProblems();

        for (int i = 0; i < problems.length; i++) {
            ProblemSummaryInfo summaryInfo = standingsRecord.getSummaryRow().get(i + 1);
            addProblemSummaryRow(standingsRecordMemento, i + 1, summaryInfo);
        }

        return standingsRecordMemento;
    }

    /**
     * Creates all standings records for all teams, unsorted/unranked.
     * 
     * @param runs
     * @param accounts
     * @param properties
     * @param problems
     * @return
     */
    private StandingsRecord[] computeStandingStandingsRecords(Run[] runs, Account[] accounts, Properties properties, Problem[] problems) {

        Arrays.sort(runs, new RunComparatorByTeam());

        if (runs.length == 0) {
            // NO runs special case
            return generateStandingsRecords(accounts, problems);
        }

        StandingsRecord[] standingsRecords = new StandingsRecord[accounts.length];

        int standRecCount = 0;

        for (Account account : accounts) {
            StandingsRecord standingsRecord = new StandingsRecord();
            standingsRecord.setClientId(account.getClientId());

            int problemNumber = 1;
            for (Problem problem : problems) {

                Run[] teamProblemRuns = getRuns(runs, account.getClientId(), problem);

                if (teamProblemRuns.length > 0) {

                    // old ProblemScoreRecord problemScoreRecord = new ProblemScoreRecord(teamProblemRuns, problem, properties);
                    ProblemScoreRecord problemScoreRecord = createProblemScoreRecord(teamProblemRuns, problem, properties);

                    standingsRecord.setPenaltyPoints(standingsRecord.getPenaltyPoints() + problemScoreRecord.getPoints());

                    if (problemScoreRecord.getSolutionTime() > standingsRecord.getLastSolved()) {
                        standingsRecord.setLastSolved(problemScoreRecord.getSolutionTime());
                    }

                    if (standingsRecord.getFirstSolved() == -1 || standingsRecord.getFirstSolved() > problemScoreRecord.getSolutionTime()) {
                        standingsRecord.setFirstSolved(problemScoreRecord.getSolutionTime());

                    }
                    if (problemScoreRecord.isSolved()) {
                        standingsRecord.setNumberSolved(standingsRecord.getNumberSolved() + 1);
                    }

                    ProblemSummaryInfo summaryInfo = createProblemSummaryInfo(teamProblemRuns, problem, problemNumber, problemScoreRecord);
                    standingsRecord.getSummaryRow().put(problemNumber, summaryInfo);
                } else {
                    ProblemSummaryInfo summaryInfo = new ProblemSummaryInfo();
                    summaryInfo.setUnJudgedRuns(false);
                    summaryInfo.setSolved(false);
                    summaryInfo.setProblemId(problem.getElementId());

                    standingsRecord.getSummaryRow().put(problemNumber, summaryInfo);
                }
                problemNumber++;
            }
            long penaltyPoints = standingsRecord.getPenaltyPoints();
            int scoreAdjustment = account.getScoringAdjustment();
            if (penaltyPoints > 0 && scoreAdjustment != 0) {
                standingsRecord.setPenaltyPoints(Math.max(penaltyPoints+scoreAdjustment,0));
            }
            standingsRecords[standRecCount] = standingsRecord;
            standRecCount++;
        }

        return standingsRecords;
    }

    private long getYesPenalty(Properties properties) {
        return getPropIntValue(properties, DefaultScoringAlgorithm.BASE_POINTS_PER_YES, "0");
    }

    /**
     * @param key
     *            property to lookup
     * @param defaultValue
     * @return
     */
    // TODO SA SOMEDAY Move this to a SA Utility Class
    private int getPropIntValue(Properties inProperties, String key, String defaultValue) {
        String s = inProperties.getProperty(key, defaultValue);
        Integer i = Integer.parseInt(s);
        return (i.intValue());
    }

    private int getNoPenalty(Properties properties) {
        // private static String[][] propList = { { POINTS_PER_NO, "20:Integer" }, { POINTS_PER_YES_MINUTE, "1:Integer" }, {
        // BASE_POINTS_PER_YES, "0:Integer" } };
        return getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO, "20");
    }

    private int getMinutePenalty(Properties properties) {
        return getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_YES_MINUTE, "1");
    }

    @Override
    public ProblemScoreRecord createProblemScoreRecord(Run[] runs, Problem problem, Properties properties) {

        boolean solved = false;

        long points = 0;

        long solutionTime = 0;

        int numberSubmissions = 0;

        Run solvingRun = null;

        int submissionsBeforeYes = 0;
        int compilationErrorsBeforeYes = 0;
        int securityViolationBeforeYes = 0;
        
        int numberPending = 0;

        int numberJudged = 0;

        Arrays.sort(runs, new RunCompartorByElapsed());

        for (Run run : runs) {
            if (run.isDeleted()) {
                continue;
            }

            numberSubmissions++;
            
//            System.out.println(numberSubmissions + " "+run.getElapsedMins()+ " "+run);
            
            if (run.isJudged()) {
                numberJudged++;
            } else {
                numberPending++;
            }
            
            if (run.isSolved() && solutionTime == 0) {
                // set to solved, set solution time
                solved = true;
                solutionTime = run.getElapsedMins();
                solvingRun = run;
            }
            
            if (run.isJudged() && (!solved)) {
                
                // before first yes.
                
                ElementId elementId = run.getJudgementRecord().getJudgementId();
                Judgement judgment = getContest().getJudgement(elementId);
                
                if (Judgement.ACRONYM_COMPILATION_ERROR.equals(judgment.getAcronym())) {
                    compilationErrorsBeforeYes++;
                } else if (Judgement.ACRONYM_SECURITY_VIOLATION.equals(judgment.getAcronym())) {
                    securityViolationBeforeYes++;
                } else {
                    submissionsBeforeYes++;     
                }
            }
        }

        if (solved) {
            points = (solutionTime * getMinutePenalty(properties) + getYesPenalty(properties)) + // 
                    (submissionsBeforeYes * getNoPenalty(properties)) + //
                    (compilationErrorsBeforeYes * getCEPenalty(properties)) + //
                    (securityViolationBeforeYes * getSVPenalty(properties));
        }
        
        return new ProblemScoreRecord(solved, solvingRun, problem, points, solutionTime, numberSubmissions, submissionsBeforeYes, numberPending, numberJudged);

    }

    private int getCEPenalty(Properties properties) {
        return getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR, "0");
    }

    private int getSVPenalty(Properties properties) {
        return getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION, "0");
    }

    /**
     * Add XML problemSummaryInfo.
     * 
     * @param mementoRoot
     * @param index
     * @param summaryInfo
     * @return
     */
    private IMemento addProblemSummaryRow(IMemento mementoRoot, int index, ProblemSummaryInfo summaryInfo) {
        IMemento summaryInfoMemento = mementoRoot.createChild("problemSummaryInfo");
        summaryInfoMemento.putInteger("index", index);
        summaryInfoMemento.putString("problemId", summaryInfo.getProblemId().toString());
        summaryInfoMemento.putInteger("attempts", summaryInfo.getNumberSubmitted());
        summaryInfoMemento.putInteger("points", summaryInfo.getPenaltyPoints());
        summaryInfoMemento.putLong("solutionTime", summaryInfo.getSolutionTime());
        summaryInfoMemento.putBoolean("isSolved", summaryInfo.isSolved());
        summaryInfoMemento.putBoolean("isPending", summaryInfo.isUnJudgedRuns());
        return summaryInfoMemento;
    }

    private ProblemSummaryInfo createProblemSummaryInfo(Run[] runs, Problem problem, int problemNumber, ProblemScoreRecord problemScoreRecord) {
        ProblemSummaryInfo summaryInfo = new ProblemSummaryInfo();

        summaryInfo.setNumberSubmitted(problemScoreRecord.getNumberSubmissions());
        summaryInfo.setJudgedRunCount(problemScoreRecord.getNumberJudgedSubmissions());
        summaryInfo.setPendingRunCount(problemScoreRecord.getNumberPendingSubmissions());
        summaryInfo.setPenaltyPoints((int) problemScoreRecord.getPoints());
        summaryInfo.setSolutionTime(problemScoreRecord.getSolutionTime());
        summaryInfo.setUnJudgedRuns(false);
        summaryInfo.setSolved(problemScoreRecord.isSolved());
        summaryInfo.setProblemId(problem.getElementId());

        return summaryInfo;
    }

    /**
     * Get all runs for a team/clientid and problem.
     * 
     * @param runs
     * @param clientId
     * @param problem
     * @return
     */
    public Run[] getRuns(Run[] runs, ClientId clientId, Problem problem) {
        Vector<Run> vector = new Vector<Run>();

        for (Run run : runs) {
            if (clientId.equals(run.getSubmitter())) {
                if (run.getProblemId().equals(problem.getElementId())) {
                    vector.add(run);
                }
            }
        }
        return (Run[]) vector.toArray(new Run[vector.size()]);

    }

    private StandingsRecord[] generateStandingsRecords(Account[] accounts, Problem[] problems) {

        Vector<StandingsRecord> vector = new Vector<StandingsRecord>();

        for (Account account : accounts) {
            StandingsRecord standingsRecord = new StandingsRecord();
            standingsRecord.setClientId(account.getClientId());
            standingsRecord.setSummaryRow(getBlankSummaryRow(problems));
            vector.add(standingsRecord);
        }

        return (StandingsRecord[]) vector.toArray(new StandingsRecord[vector.size()]);
    }

    private SummaryRow getBlankSummaryRow(Problem[] problems) {
        SummaryRow summaryRow = new SummaryRow();

        int problemNumber = 0;

        for (Problem problem : problems) {
            if (problem.isActive()) {
                problemNumber++;
                ProblemSummaryInfo problemSummaryInfo = new ProblemSummaryInfo();
                problemSummaryInfo.setProblemId(problem.getElementId());
                summaryRow.put(problemNumber, problemSummaryInfo);
            }
        }

        return summaryRow;
    }

    /**
     * use block ranking algorithm.
     * 
     * See {@link #setBlockRanking(boolean)} for more info.
     * 
     * @return
     */
    public boolean useBlockRanking() {
        return blockRanking;
    }

    /**
     * Set block ranking when teams have tied rankings.
     * 
     * This only applies if there are tied teams. IF there are tied teams there are two ways to rank teams after the tied teams.
     * <P>
     * For example, blocked ranks are:
     * 
     * <pre>
     * 1.  Team 7
     * 1.  Team 2
     * 3.  Team 4
     * 4.  Team 8
     * </pre>
     * 
     * Non-blocked ranks are:
     * 
     * <pre>
     * 1.  Team 7
     * 1.  Team 2
     * 2.  Team 4
     * 3.  Team 8
     * </pre>
     * 
     * @param blockRanking
     */
    public void setBlockRanking(boolean blockRanking) {
        this.blockRanking = blockRanking;
    }

    private void initializePermissions(IInternalContest contest, ClientId clientId) {
        Account account = contest.getAccount(clientId);
        if (account != null) {
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        } else {
            // Set default conditions
            permissionList.clearAndLoadPermissions(new PermissionGroup().getPermissionList(clientId.getClientType()));
        }
    }

    /**
     * Is Client allowed to do permission type
     * 
     * @param contest
     * @param clientId
     * @param type
     * @param respect_notify_team_setting
     * @return true if permission/type.
     */
    private boolean isAllowed(IInternalContest contest, ClientId clientId, Permission.Type type) {
        initializePermissions(contest, clientId);
        return permissionList.isAllowed(type);
    }

    private void addGrandTotals(IMemento summaryMememento, GrandTotals grandTotals) {
        summaryMememento.putInteger("totalAttempts", grandTotals.getTotalAttempts());
        summaryMememento.putInteger("totalSolved", grandTotals.getTotalSolutions());
        summaryMememento.putInteger("problemsAttempted", grandTotals.getTotalProblemAttempts());
    }

    /**
     * Create XML standingsHeader.
     * 
     * This creates the standingsHeader block. Later other methods add problem summaries ("problem" blocks) to this block.
     * 
     * @param mementoRoot
     */
    // TODO SA SOMEDAY Move this to a SA Utility Class
    private IMemento createSummaryMomento(ContestInformation contestInformation, XMLMemento mementoRoot) {
        IMemento memento = mementoRoot.createChild("standingsHeader");
        String title = contestInformation.getContestTitle();
        if (title == null || title.length() == 0) {
            title = "Contest";
        }
        memento.putString("title", title);
        VersionInfo versionInfo = new VersionInfo();
        memento.putString("systemName", versionInfo.getSystemName());
        memento.putString("systemVersion", versionInfo.getVersionNumber() + " build " + versionInfo.getBuildNumber());
        memento.putString("systemURL", versionInfo.getSystemURL());
        memento.putString("currentDate", new Date().toString());
        memento.putString("generatorId", "$Id$");

        return memento;
    }

    // private ProblemSummaryInfo calcProblemScoreData(Run[] runs, Problem problem) throws IllegalContestState {
    // ProblemSummaryInfo problemSummaryInfo = new ProblemSummaryInfo();
    //
    // int score = 0;
    // int attempts = 0;
    // long solutionTime = -1;
    // boolean solved = false;
    // boolean unJudgedRun = false;
    //
    // problemSummaryInfo.setSolved(solved);
    // problemSummaryInfo.setSolutionTime(solutionTime);
    // problemSummaryInfo.setProblemId(problem.getElementId());
    // problemSummaryInfo.setNumberSubmitted(attempts);
    // problemSummaryInfo.setPenaltyPoints(score);
    // problemSummaryInfo.setUnJudgedRuns(unJudgedRun);
    // return problemSummaryInfo;
    // }

    /**
     * Create XML groupList.
     * 
     * @param groups
     * @param memento
     */
    private void dumpGroupList(Group[] groups, IMemento memento) {
        memento.putInteger("groupCount", groups.length + 1);
        IMemento groupsMemento = memento.createChild("groupList");
        int id = 0;
        for (int i = 0; i < groups.length; i++) {
            if (!groups[i].isDisplayOnScoreboard()) {
                continue;
            }
            id = id + 1;
            IMemento groupMemento = groupsMemento.createChild("group");
            groupMemento.putInteger("id", id);
            groupMemento.putString("title", groups[i].getDisplayName());
            groupMemento.putInteger("externalId", groups[i].getGroupId());
            if (groups[i].getSite() != null) {
                groupMemento.putInteger("pc2Site", groups[i].getSite().getSiteNumber());
            }
        }
    }

    /**
     * Grand totals per team.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    private class GrandTotals {
        private int totalAttempts = 0;

        private int totalSolutions = 0;

        private int totalProblemAttempts = 0;

        public int getTotalAttempts() {
            return totalAttempts;
        }

        public void incrementTotalSolutions(int num) {
            totalSolutions += num;
        }

        public void incrementTotalAttempts(int num) {
            totalAttempts += num;
        }

        public int getTotalSolutions() {
            return totalSolutions;
        }

        public int getTotalProblemAttempts() {
            return totalProblemAttempts;
        }
    }

    @Override
    public String getPluginTitle() {
        return "Scoring Algorithm";
    }

    @Override
    public void dispose() {

        // nothing to dispose of.

    }
}
