package edu.csus.ecs.pc2.core.scoring;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.BalloonSettingsComparatorbySite;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeam;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
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
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;
import edu.csus.ecs.pc2.core.security.Permission.Type;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Default Scoring Algorithm, implementation of the IScoringAlgorithm.
 * 
 * This class implements the standard (default) scoring algorithm, which ranks all teams according to number of problems solved, then according to "penalty points" computed by multiplying the number
 * of "NO" runs on solved problems by the PenaltyPoints value specified in the contest configuration, then finally according to earliest time of last solution (with ties at that level broken
 * alphabetically). This is the "standard" algorithm used in many ICPC Regional Contests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
// $HeadURL$
public class DefaultScoringAlgorithm implements IScoringAlgorithm {

    public static final String POINTS_PER_NO = "Points per No";

    public static final String POINTS_PER_YES_MINUTE = "Points per Minute (for 1st yes)";

    public static final String BASE_POINTS_PER_YES = "Base Points per Yes";
    
    public static final String POINTS_PER_NO_COMPILATION_ERROR = "Points per Compilation Error";
    
    public static final String POINTS_PER_NO_SECURITY_VIOLATION = "Points per Security Violation";
    
    /**
     * properties.
     * 
     * key=name, value=default_value, type, min, max (colon delimited)
     */
    private static String[][] propList = { { POINTS_PER_NO, "20:Integer" }, { POINTS_PER_YES_MINUTE, "1:Integer" }, { BASE_POINTS_PER_YES, "0:Integer" }, { POINTS_PER_NO_COMPILATION_ERROR, "0:Integer" }, { POINTS_PER_NO_SECURITY_VIOLATION, "0:Integer" } };
    
    private Properties props = new Properties();

    private Object mutex = new Object();
    
    private int grandTotalAttempts;

    private int grandTotalSolutions;

    private int grandTotalProblemAttempts;

    private int[] problemBestTime = null;

    private int[] problemLastTime = null;

    private int[] problemSolutions = null;

    private int[] problemAttempts = null;

    private Log log;
    
    private boolean countPreliminaryJudgements = false;

    private PermissionList permissionList = new PermissionList();
    
    /**
     * Respect Send to Team Permission.
     * 
     * true means if {@link edu.csus.ecs.pc2.core.model.JudgementRecord#isSendToTeam()} is true then process run as a NEW run.
     * <br>
     * false means process all records per usual. 
     */
    private boolean respectSendToTeam = false;
    private boolean respectEOC = false;
    

    public DefaultScoringAlgorithm() {
        super();
        for (int i = 0; i < propList.length; i++) {
            String key = propList[i][0];
            String value = propList[i][1];
            int colon = value.indexOf(":");
            String defaultValue = value.substring(0, colon);
            props.put(key, defaultValue);
        }
        // props.put(POINTS_PER_NO, "20");
        // props.put(POINTS_PER_YES_MINUTE, "1");
        // props.put(BASE_POINTS_PER_YES, "0");
        // TODO populate default properties
    }

    AccountList getAccountList(IInternalContest theContest) {
        Vector<Account> accountVect = theContest.getAccounts(ClientType.Type.ALL);
        AccountList accountList = new AccountList();
        Enumeration<Account> accountEnum = accountVect.elements();
        while(accountEnum.hasMoreElements()) {
            Account a = (Account)accountEnum.nextElement();
            accountList.add(a);
        }
        return accountList;
    }
    /**
     * Get the Score and Statistics information for one problem.
     * 
     * @return pc2.ex.ProblemScoreData
     * @param treeMap
     *            java.util.TreeMap
     */
    private ProblemSummaryInfo calcProblemScoreData(TreeMap<Run,Run> treeMap, IInternalContest theContest) throws IllegalContestState {
        ProblemSummaryInfo problemSummaryInfo = new ProblemSummaryInfo();
        int score = 0;
        int attempts = 0;
        ElementId problemId = null;
        long solutionTime = -1;
        boolean solved = false;
        boolean unJudgedRun = false;

        if (treeMap.isEmpty()) {
            problemSummaryInfo = null; // ProblemScoreData must have ProblemId to be valid
        } else {
            Collection<Run> coll = treeMap.values();
            Object[] o;
            Run run;
            o = coll.toArray();
            for (int i = 0; i < o.length; i++) {
                run = (Run) o[i];
                // this should not have made it into the incoming treeMap
                if (run.isDeleted()) {
                    continue;
                }
                attempts++;
                problemId = run.getProblemId();
                // added isValidJudgement to check and obey preliminary results
                if (run.isSolved() && isValidJudgement(run)) {
                    // TODO: we might want some differing logic here if all
                    // yes's are counted
                    // and/or no's after yes's are counted
                    solved = true;
                    solutionTime = run.getElapsedMins();
                    score += solutionTime * getPenaltyPointsPerYesMinute() + getBasePointsPerYes();
                    break;
                } else {
                    // we should really only do this if it's been judged
                    if (isValidJudgement(run)) {
                    	String response = theContest.getJudgement(run.getJudgementRecord().getJudgementId()).getAcronym();
                        if(response.equals(Judgement.ACRONYM_COMPILATION_ERROR))
                    		score += getPenaltyPointsPerNoCompilationError();
                    	else if(response.equals(Judgement.ACRONYM_SECURITY_VIOLATION))
                    		score += getPenaltyPointsPerNoSecurityViolation();
                    	else
                    		score += getPenaltyPointsPerNo();
                    } else {
                        unJudgedRun = true;
                    }
                }
            }
        }
        // TODO put another if around this if there was a setting to include all
        // no's before yes
        if (!solved) {
            score = 0;
        }
        problemSummaryInfo.setSolved(solved);
        problemSummaryInfo.setSolutionTime(solutionTime);
        problemSummaryInfo.setProblemId(problemId);
        problemSummaryInfo.setNumberSubmitted(attempts);
        problemSummaryInfo.setPenaltyPoints(score);
        problemSummaryInfo.setUnJudgedRuns(unJudgedRun);
        return problemSummaryInfo;
    }

    /**
     * @param key
     *            property to lookup
     * @return
     */
    private int getPropIntValue(String key) {
        String s = props.getProperty(key);
        Integer i = Integer.parseInt(s);
        return (i.intValue());
    }

    /**
     * @return number of points to assign per yes
     */
    private int getBasePointsPerYes() {
        return (getPropIntValue(BASE_POINTS_PER_YES));
    }

    /**
     * @return number of points to assign per no
     */
    private int getPenaltyPointsPerNo() {
        return (getPropIntValue(POINTS_PER_NO));
    }
    
    private int getPenaltyPointsPerNoCompilationError() {
        return (getPropIntValue(POINTS_PER_NO_COMPILATION_ERROR));
    }
    
    private int getPenaltyPointsPerNoSecurityViolation() {
        return (getPropIntValue(POINTS_PER_NO_SECURITY_VIOLATION));
    }

    /**
     * @return additional points to assign per yes
     */
    private int getPenaltyPointsPerYesMinute() {
        return (getPropIntValue(POINTS_PER_YES_MINUTE));
    }

    public Properties getProperties() {
        return props;
    }

    public void setProperties(Properties properties) {
        this.props = properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.core.scoring.ScoringAlgorithm#getStandings(edu.csus.ecs.pc2.core.Run[], edu.csus.ecs.pc2.core.AccountList, edu.csus.ecs.pc2.core.ProblemDisplayList, java.util.Properties)
     */
    public String getStandings(IInternalContest theContest, Properties properties, Log inputLog) throws IllegalContestState {
        if (theContest == null) {
            throw new InvalidParameterException("Invalid model (null)");
        }
        
        if (properties == null || properties.isEmpty()) {
            properties = getProperties();
        }
        
        this.log = inputLog;
        
        // TODO properties should be validated here
        props = properties;
        
        /**
         * Settings 
         */
        
 
        respectSendToTeam = isAllowed (theContest, theContest.getClientId(), Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        respectEOC = isAllowed (theContest, theContest.getClientId(), Permission.Type.RESPECT_EOC_SUPPRESSION);

        countPreliminaryJudgements = theContest.getContestInformation().isPreliminaryJudgementsUsedByBoard();
        
        XMLMemento mementoRoot = XMLMemento.createWriteRoot("contestStandings");
        IMemento summaryMememento = createSummaryMomento (theContest.getContestInformation(), mementoRoot);
        
        AccountList accountList = getAccountList(theContest);
        Problem[] allProblems = theContest.getProblems();
        Hashtable <ElementId, Integer> problemsIndexHash = new Hashtable<ElementId, Integer>();
        int p2 = 0;
        for (int p=1; p <= allProblems.length ; p++) {
            Problem prob = allProblems[p-1];
            if (prob.isActive()) {
                p2++;
                problemsIndexHash.put(prob.getElementId(), new Integer(p2));
            }
        }
        Problem[] problems = new Problem[p2];
        Set<ElementId> keys = problemsIndexHash.keySet();
        for (Iterator<ElementId> iterator = keys.iterator(); iterator.hasNext();) {
            ElementId type = (ElementId) iterator.next();
            int p = Integer.valueOf(problemsIndexHash.get(type));
            problems[p-1] = theContest.getProblem(type);
        }

        summaryMememento.putLong("problemCount", problems.length);
        Site[] sites = theContest.getSites();
        summaryMememento.putInteger("siteCount", sites.length);
        Group[] groups = theContest.getGroups();
        if (groups != null) {
            dumpGroupList(groups, summaryMememento);
        }
        BalloonSettings[] balloonSettings = theContest.getBalloonSettings();
        if (balloonSettings != null) {
            Arrays.sort(balloonSettings, new BalloonSettingsComparatorbySite());
            IMemento listMemento = summaryMememento.createChild("colorList");
            for (int i = 0; i < balloonSettings.length; i++) {
                int id = i + 1;
                IMemento balloonSettingsMemento = listMemento.createChild("colors");
                balloonSettingsMemento.putInteger("id", id);
                dumpBalloonSettings(balloonSettings[i], problems, balloonSettingsMemento);
            }
        }
        Run[] runs = theContest.getRuns();
        synchronized (mutex) {
            Account[] accounts = accountList.getList();
            
            /**
             * This contains the standings records, key is ClientId.toString() value is StandingsRecord
             */
            Hashtable<String, StandingsRecord> standingsRecordHash = new Hashtable<String, StandingsRecord>();
            
            RunComparatorByTeam runComparatorByTeam = new RunComparatorByTeam();
            TreeMap<Run, Run> runTreeMap = new TreeMap<Run, Run>(runComparatorByTeam);
            
            Hashtable<String, Problem> problemHash = new Hashtable<String, Problem>();
            for (int i = 0; i < problems.length; i++) {
                Problem problem = problems[i];
                if (problem.isActive()) {
                    problemHash.put(problem.getElementId().toString(), problem);
                }
            }
            
           initializeStandingsRecordHash (accountList, accounts, problems, standingsRecordHash);
            
            for (int i = 0; i < runs.length; i++) {
                // skip runs that are deleted and
                // skip runs whose submitter is no longer active and
                // skip runs whose problem are no longer active
                Account account = accountList.getAccount(runs[i].getSubmitter());
                if (account == null) {
                    log.info("account could not be located for " + runs[i].getSubmitter());
                    continue;
                }
                if (!runs[i].isDeleted() && account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) 
                        && problemHash.containsKey(runs[i].getProblemId().toString())) {
                    
                    Run runToAdd = runs[i];
                    if ( respectSendToTeam && runToAdd.getAllJudgementRecords().length > 0 ){
                        /**
                         * There are judgements and we need to check the send to team (notify team) flag
                         */
                        if (! runToAdd.getJudgementRecord().isSendToTeam()){
                            /**
                             * If not send to team, then change run to a new run with status NEW
                             */
                            runToAdd = RunUtilities.createNewRun(runs[i], theContest);
                        }
                    }
                    // now check EOC settings
                    JudgementNotificationsList judgementNotificationsList = theContest.getContestInformation().getJudgementNotificationsList();
                    ContestTime contestTime = theContest.getContestTime();
                    if (respectEOC && RunUtilities.supppressJudgement(judgementNotificationsList, runs[i], contestTime)) {
                        /**
                         * If we are suppose to suppress this judgement, then change the run to a NEW run.
                         */
                        runToAdd = RunUtilities.createNewRun(runs[i], theContest);
                    }
                    runTreeMap.put(runToAdd, runToAdd);
                    
                }
            }
            
            if (!runTreeMap.isEmpty()) {
                
                generateStandingsValues (runTreeMap, standingsRecordHash, problemsIndexHash, theContest);
                    
            } // else no runs

            // use TreeMap to sort
            DefaultStandingsRecordComparator src = new DefaultStandingsRecordComparator();
            src.setCachedAccountList(accountList);
            TreeMap<StandingsRecord, StandingsRecord> treeMap = new TreeMap<StandingsRecord, StandingsRecord>(src);
            Collection<StandingsRecord> enumeration = standingsRecordHash.values();
            for (StandingsRecord record : enumeration) {
                treeMap.put(record, record);
            }
            
            createStandingXML(treeMap, mementoRoot, accountList, problems, problemsIndexHash, groups, summaryMememento);
            
        } // mutex
 
        String xmlString;
        try {
            xmlString = mementoRoot.saveToString();
        } catch (IOException e) {
            log.log(Log.WARNING,"Trouble saving momentoRoot to String ", e);
            xmlString = "";
        }
//        System.out.println(xmlString);
        return xmlString;
    }
    
    private void initializePermissions(IInternalContest theContest, ClientId clientId) {
        permissionList.clearAndLoadPermissions(getPermissionList(theContest));
    }

    private PermissionList getPermissionList(IInternalContest theContest) {
        ClientId id = theContest.getClientId();

        Account account = theContest.getAccount(id);

        PermissionList list = null;
        if (account == null) {
            list = new PermissionGroup().getPermissionList(id.getClientType());
        } else {
            list = account.getPermissionList();
        }
        return list;
    }

    /**
     * Is Client allowed to do permission type
     * 
     * @param theContest
     * @param clientId
     * @param respect_notify_team_setting
     * @return true if permission/type.
     */
    private boolean isAllowed(IInternalContest theContest, ClientId clientId, Type type) {
        initializePermissions(theContest, clientId);
        return permissionList.isAllowed(type);
    }

    private void dumpBalloonSettings(BalloonSettings balloonSettings, Problem[] problems, IMemento memento) {
        memento.putInteger("siteNum", balloonSettings.getSiteNumber());
        if (problems != null) {
            for (int i = 0; i < problems.length; i++) {
                int id = i + 1;
                IMemento problemMemento = memento.createChild("problem");
                problemMemento.putInteger("id", id);
                problemMemento.putString("color", balloonSettings.getColor(problems[i]));
            }
        }
    }

    private void dumpGroupList(Group[] groups, IMemento memento) {
        memento.putInteger("groupCount", groups.length+1);
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
     * Ranks standings records and add standings XML to mementoRoot.
     * 
     * Loops through the standings records and problem summary information
     * creating XML blocks: teamStanding and problemSummaryInfo.
     * 
     * @param treeMap
     * @param mementoRoot
     * @param accountList
     * @param problems
     * @param problemsIndexHash
     * @param summaryMememento
     */
    private void createStandingXML (TreeMap<StandingsRecord, StandingsRecord> treeMap, XMLMemento mementoRoot, 
            AccountList accountList, Problem[] problems, Hashtable<ElementId, Integer> problemsIndexHash, Group[] groups, IMemento summaryMememento) {
   
        // easy access
        Hashtable<ElementId, Group> groupHash = new Hashtable<ElementId, Group>();
        Hashtable<Group, Integer> groupIndexHash = new Hashtable<Group, Integer>();
        int groupCount = 0;
        for (Group group : groups) {
            // no refence to groups that should not be displayed on scoreboard
            if (!group.isDisplayOnScoreboard()) {
                continue;
            }
            groupHash.put(group.getElementId(), group);
            groupIndexHash.put(group, Integer.valueOf(groupCount));
            groupCount++;
        }
        StandingsRecord[] srArray = new StandingsRecord[treeMap.size()];
        
        Collection<StandingsRecord> coll = treeMap.values();
        Iterator<StandingsRecord> iterator = coll.iterator();
        
        problemBestTime = new int[problems.length + 1];
        problemLastTime = new int[problems.length + 1];
        problemSolutions = new int[problems.length + 1];
        problemAttempts = new int[problems.length + 1];
        for (int p = 1; p <= problems.length; p++) {
            problemBestTime[p] = -1;
        }
        
        grandTotalAttempts = 0;
        grandTotalSolutions = 0;
        grandTotalProblemAttempts = 0;
         
        // assign the ranks
        long numSolved = -1, score = 0, lastSolved = 0;
        int rank = 0, indexRank = 0;
        int index = 0;
        // these are indexed by groupIndex
        long[] groupNumSolved = new long[groupCount];
        for (int i = 0; i < groupNumSolved.length; i++) {
            groupNumSolved[i] = -1;
        }
        long[] groupScore = new long[groupCount];
        long[] groupLastSolved = new long[groupCount];
        int[] groupRank = new int[groupCount];
        int[] groupIndexRank = new int[groupCount];
        for (int i = 0; i < groupIndexRank.length; i++) {
            groupScore[i] = 0;
            groupLastSolved[i] = 0;
            groupRank[i] = 0;
            groupIndexRank[i] = 0;
        }
        while (iterator.hasNext()) {
            Object o = iterator.next();
            StandingsRecord standingsRecord = (StandingsRecord) o;
            indexRank++;
            if (!isTeamTied(standingsRecord, numSolved, score, lastSolved)) {
                numSolved = standingsRecord.getNumberSolved();
                score = standingsRecord.getPenaltyPoints();
                lastSolved = standingsRecord.getLastSolved();
                rank = indexRank;
                standingsRecord.setRankNumber(rank);
            } else {
                // current user tied with last user, so same rank
                standingsRecord.setRankNumber(rank);
            }
//            mementoRoot.putMemento(standingsRecord.toMemento());
            long totalAttempts = 0;
            long problemsAttempted = 0;
            IMemento standingsRecordMemento = mementoRoot.createChild("teamStanding");
            standingsRecordMemento.putLong("firstSolved", standingsRecord.getFirstSolved());
            standingsRecordMemento.putLong("lastSolved", standingsRecord.getLastSolved());
            standingsRecordMemento.putLong("points", standingsRecord.getPenaltyPoints());
            standingsRecordMemento.putInteger("solved", standingsRecord.getNumberSolved());
            standingsRecordMemento.putInteger("rank", standingsRecord.getRankNumber());
            standingsRecordMemento.putInteger("index", index);
            Account account = accountList.getAccount(standingsRecord.getClientId());
            standingsRecordMemento.putString("teamName", account.getDisplayName()); 
            standingsRecordMemento.putInteger("teamId", account.getClientId().getClientNumber());
            standingsRecordMemento.putInteger("teamSiteId", account.getClientId().getSiteNumber());
            standingsRecordMemento.putString("teamKey", account.getClientId().getTripletKey());
            standingsRecordMemento.putString("teamExternalId", account.getExternalId());
            if (account.getAliasName().trim().equals("")) {
                standingsRecordMemento.putString("teamAlias", account.getDisplayName()+" (not aliasesd)");
            } else {
                standingsRecordMemento.putString("teamAlias", account.getAliasName().trim());
            }
            Group group = null;
            if (account.getGroupId() != null) {
                group = groupHash.get(account.getGroupId());
            }
            if (group != null ) {
                // the group was in groupHash, so must be in groupIndexHash
                int groupIndex = groupIndexHash.get(group).intValue();
                // do the same thing as above, now for the group
                groupIndexRank[groupIndex]++;
                if (!isTeamTied(standingsRecord,groupNumSolved[groupIndex], groupScore[groupIndex],groupLastSolved[groupIndex])) {
                    groupNumSolved[groupIndex] = standingsRecord.getNumberSolved();
                    groupScore[groupIndex] = standingsRecord.getPenaltyPoints();
                    groupLastSolved[groupIndex] = standingsRecord.getLastSolved();
                    groupRank[groupIndex] = groupIndexRank[groupIndex];
                    standingsRecord.setGroupRankNumber(groupRank[groupIndex]);
                } else {
                    // current user tied with last user, so same rank
                    standingsRecord.setGroupRankNumber(groupRank[groupIndex]);
                }
                standingsRecordMemento.putInteger("groupRank", standingsRecord.getGroupRankNumber());
                standingsRecordMemento.putString("teamGroupName", group.getDisplayName());
                standingsRecordMemento.putInteger("teamGroupId", groupIndex+1);
                standingsRecordMemento.putInteger("teamGroupExternalId", group.getGroupId());
            }
            SummaryRow summaryRow = standingsRecord.getSummaryRow();
            for (int i = 0; i < problems.length; i++) {
                int id = i + 1;
                ProblemSummaryInfo psi = summaryRow.get(id);
                if (psi == null) {
                    // TODO change to Log, cleanup message (leaning towards error)
                    log.log(Log.WARNING, "ProblemSummaryInfo not generated/found for problem "+id+" "+problems[i]);
                    System.out.println("error or normal? ProblemSummaryInfo not found for problem "+ id);
                } else {
                    IMemento psiMemento = standingsRecordMemento.createChild("problemSummaryInfo");
                    psiMemento.putInteger("index", problemsIndexHash.get(psi.getProblemId()));
                    psiMemento.putString("problemId", psi.getProblemId().toString());
                    psiMemento.putInteger("attempts", psi.getNumberSubmitted());
                    psiMemento.putInteger("points", psi.getPenaltyPoints());
                    psiMemento.putLong("solutionTime", psi.getSolutionTime());
                    psiMemento.putBoolean("isSolved", psi.isSolved());
                    psiMemento.putBoolean("isPending", psi.isUnJudgedRuns());
                    problemAttempts[id] += psi.getNumberSubmitted();
                    totalAttempts += psi.getNumberSubmitted();
                    grandTotalAttempts += psi.getNumberSubmitted();
                    if (psi.getNumberSubmitted() > 0) {
                        problemsAttempted++;
                    }
                    if (psi.isSolved()) {
                        problemSolutions[id]++;
                        grandTotalSolutions++;
                        if (psi.getSolutionTime() > problemLastTime[id]) {
                            problemLastTime[id] = new Long(psi.getSolutionTime()).intValue();
                        }
                        if (problemBestTime[id] < 0 || psi.getSolutionTime() < problemBestTime[id]) {
                            problemBestTime[id] = new Long(psi.getSolutionTime()).intValue();                       
                        }
                    }
                }
            }
            standingsRecordMemento.putLong("totalAttempts",totalAttempts);
            standingsRecordMemento.putLong("problemsAttempted",problemsAttempted);

            srArray[index++] = standingsRecord;
        }
     
        summaryMememento.putInteger("medianProblemsSolved", getMedian(srArray));
        generateSummaryTotalsForProblem (problems, problemsIndexHash, summaryMememento);
        
    }
    
    /**
     * Input is a sorted ranking list.  What is the median number of problems solved.
     * 
     * @param srArray
     * @return median number of problems solved
     */
    private int getMedian(StandingsRecord[] srArray) {
        int median;
        if (srArray == null || srArray.length == 0) {
            median = 0;
        } else {
            if (srArray.length == 1) {
                median = srArray[0].getNumberSolved();
            } else {
                if (srArray.length % 2 == 0) {
                    // even number of entries
                    int high, low;
                    low = srArray[srArray.length/2-1].getNumberSolved();
                    high = srArray[(srArray.length+1)/2].getNumberSolved();
                    median = (low + high) /2;
                } else {
                    // odd number
                    median = srArray[(srArray.length+1)/2-1].getNumberSolved();
                }
            }
        }
        return median;
    }

    /**
     * This routine checks and obeys the preliminary judgement rules.
     * 
     * @param run
     * @return true if run is judged and the state is valid
     */
    boolean isValidJudgement(Run run) {
        boolean result=false;
        if (run.getStatus().equals(RunStates.JUDGED)) {
            // done it's good & simple
            result = true;
        } else {
            // now the ugly stuff, handle being rejudged/preliminary judgements/...
            if (run.getJudgementRecord() != null) {
                // good... but why is the state not JUDGED
                if (run.getJudgementRecord().isPreliminaryJudgement()){
                    if (countPreliminaryJudgements) {
                        result = true;
                    }
                } else {
                    result = true;
                }
            }
        }
        return result;
    }
    
    /**
     * Do these long parameters match the values in the StandingsRecord?
     * 
     * @param standingsRecord
     * @param numSolved
     * @param score
     * @param lastSolved
     * @return True if the long parameters match the corresponding numbers in the StandingsRecord
     */
    boolean isTeamTied(StandingsRecord standingsRecord, long numSolved, long score, long lastSolved) {
        if (numSolved != standingsRecord.getNumberSolved()) {
            return false;
        }
        if (score != standingsRecord.getPenaltyPoints()) {
            return false;
        }
        if (lastSolved != standingsRecord.getLastSolved()) {
            return false;
        }
        return true;
    }

    /**
     * Add Problem Summary totals/info for each problem.
     * 
     * Generate all "problem" blocks in "standingsHeader" block (summaryMemento) 
     * 
     * @param problems
     * @param problemsIndexHash
     * @param summaryMememento
     */
    
    private void generateSummaryTotalsForProblem(Problem[] problems, Hashtable<ElementId, Integer> problemsIndexHash, IMemento summaryMememento) {
        
        for (int i = 0; i < problems.length; i++) {
            int id = i + 1;
            problemsIndexHash.put(problems[i].getElementId(), new Integer(id));
            IMemento problemMemento = summaryMememento.createChild("problem");
            problemMemento.putInteger("id", id);
            problemMemento.putString("title", problems[i].getDisplayName());
            // problemMemento.putString("color", problems[i].get);
            problemMemento.putLong("attempts", problemAttempts[id]);
            if (problemAttempts[id] > 0) {
                grandTotalProblemAttempts++;
            }
            problemMemento.putLong("numberSolved", problemSolutions[id]);
            if (problemSolutions[id] > 0) {
                problemMemento.putLong("bestSolutionTime",problemBestTime[id]);
                problemMemento.putLong("lastSolutionTime",problemLastTime[id]);
            }
        }
        summaryMememento.putInteger("totalAttempts", grandTotalAttempts);
        summaryMememento.putInteger("totalSolved", grandTotalSolutions);
        summaryMememento.putInteger("problemsAttempted", grandTotalProblemAttempts);
        
        
    }

    /**
     * Calculate standings raw data, set values into standingsRecordHash.
     * 
     * Loops through runTreeMap and puts calculated values into standingsRecords in hash.
     * 
     * @param runTreeMap
     * @param standingsRecordHash
     * @param problemsIndexHash
     */
    private void generateStandingsValues (final TreeMap<Run, Run> runTreeMap, Hashtable<String, StandingsRecord> standingsHash, Hashtable<ElementId, Integer> problemsHash, IInternalContest theContest) throws IllegalContestState {

        long oldTime = 0;
        long youngTime = -1;

        RunComparatorByTeam runComparatorByTeam = new RunComparatorByTeam();
        TreeMap<Run, Run> problemTreeMap = new TreeMap<Run, Run>(runComparatorByTeam);

        Collection<Run> runColl = runTreeMap.values();
        Iterator<Run> runIterator = runColl.iterator();
        // cannot be null for 1st run
        String lastUser = "";
        String lastProblem = "";

        while (runIterator.hasNext()) {
            Object o = runIterator.next();
            Run run = (Run) o;
            if (!lastUser.equals(run.getSubmitter().toString()) || !lastProblem.equals(run.getProblemId().toString())) {
                if (!problemTreeMap.isEmpty()) {
                    ProblemSummaryInfo problemSummaryInfo = calcProblemScoreData(problemTreeMap, theContest);
                    StandingsRecord standingsRecord = (StandingsRecord) standingsHash.get(lastUser);
                    SummaryRow summaryRow = standingsRecord.getSummaryRow();
                    summaryRow.put(problemsHash.get(problemSummaryInfo.getProblemId()), problemSummaryInfo);
                    standingsRecord.setSummaryRow(summaryRow);
                    standingsRecord.setPenaltyPoints(standingsRecord.getPenaltyPoints() + problemSummaryInfo.getPenaltyPoints());
                    if (problemSummaryInfo.isSolved()) {
                        standingsRecord.setNumberSolved(standingsRecord.getNumberSolved() + 1);
                        oldTime = standingsRecord.getLastSolved();
                        youngTime = standingsRecord.getFirstSolved();
                        if (problemSummaryInfo.getSolutionTime() > oldTime) {
                            standingsRecord.setLastSolved(problemSummaryInfo.getSolutionTime());
                        }
                        if (youngTime < 0 || problemSummaryInfo.getSolutionTime() < youngTime) {
                            standingsRecord.setFirstSolved(problemSummaryInfo.getSolutionTime());
                        }
                    }
                    standingsHash.put(lastUser, standingsRecord);
                    // now clear the TreeMap
                    problemTreeMap.clear();
                }
                lastUser = run.getSubmitter().toString();
                lastProblem = run.getProblemId().toString();
            }
            problemTreeMap.put(run, run);
        }

        // handle last run
        if (!problemTreeMap.isEmpty()) {
            ProblemSummaryInfo problemSummaryInfo = calcProblemScoreData(problemTreeMap, theContest);
            StandingsRecord standingsRecord = (StandingsRecord) standingsHash.get(lastUser);
            SummaryRow summaryRow = standingsRecord.getSummaryRow();
            summaryRow.put(problemsHash.get(problemSummaryInfo.getProblemId()), problemSummaryInfo);
            standingsRecord.setSummaryRow(summaryRow);
            standingsRecord.setPenaltyPoints(standingsRecord.getPenaltyPoints() + problemSummaryInfo.getPenaltyPoints());
            if (problemSummaryInfo.isSolved()) {
                standingsRecord.setNumberSolved(standingsRecord.getNumberSolved() + 1);
                oldTime = standingsRecord.getLastSolved();
                youngTime = standingsRecord.getFirstSolved();
                if (problemSummaryInfo.getSolutionTime() > oldTime) {
                    standingsRecord.setLastSolved(problemSummaryInfo.getSolutionTime());
                }
                if (youngTime < 0 || problemSummaryInfo.getSolutionTime() < youngTime) {
                    standingsRecord.setFirstSolved(problemSummaryInfo.getSolutionTime());
                }
            }
            standingsHash.put(lastUser, standingsRecord);
        }

        problemTreeMap.clear();
        problemTreeMap = null;

    }

    /**
     * Initialize the standingsRecordHash.
     * 
     * @param accountList
     * @param accounts
     * @param problems
     * @param standingsRecordHash
     */
    private void initializeStandingsRecordHash(AccountList accountList, Account[] accounts, Problem[] problems, Hashtable<String, StandingsRecord> standingsRecordHash) {

        for (int i = 0; i < accountList.size(); i++) {
            Account account = accounts[i];
            if (account.getClientId().getClientType() == ClientType.Type.TEAM && account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {

                StandingsRecord standingsRecord = new StandingsRecord();
                SummaryRow summaryRow = standingsRecord.getSummaryRow();
                // populate summaryRow with problems

                for (int j = 0; j < problems.length; j++) {
                    ProblemSummaryInfo problemSummaryInfo = new ProblemSummaryInfo();
                    problemSummaryInfo.setProblemId(problems[j].getElementId());
                    problemSummaryInfo.setPenaltyPoints(0);
                    summaryRow.put(j + 1, problemSummaryInfo);
                }
                standingsRecord.setSummaryRow(summaryRow);
                standingsRecord.setClientId(account.getClientId());
                standingsRecordHash.put(account.getClientId().toString(), standingsRecord);
            }
        }

    }

    /**
     * Create Summary Momento.
     * 
     * This creates the standingsHeader block.  Later other
     * methods add problem summaries ("problem" blocks) to this block.
     * 
     * @param mementoRoot
     */
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
    
    /**
     * 
     * @return a list of name/value pairs for default scoring properties.
     */
    public static Properties getDefaultProperties() {
        Properties properties = new Properties();
        for (int i = 0; i < propList.length; i++) {
            String key = propList[i][0];
            String value = propList[i][1];
            int colon = value.indexOf(":");
            String defaultValue = value.substring(0, colon);
            properties.put(key, defaultValue);
        }
        return properties;
    }

}
