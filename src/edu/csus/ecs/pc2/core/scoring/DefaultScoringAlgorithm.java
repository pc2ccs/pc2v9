package edu.csus.ecs.pc2.core.scoring;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeam;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Default Scoring Algorithm.
 * 
 * This class implements the standard (default) scoring algorithm, which ranks all teams according to number of problems solved, then according to "penalty points" computed by multiplying the number
 * of "NO" runs on solved problems by the PenaltyPoints value specified in the contest configuration, then finally according to earliest time of last solution (with ties at that level broken
 * alphabetically). This is the "standard" algorithm used in many ICPC Regional Contests.
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class DefaultScoringAlgorithm implements IScoringAlgorithm {
    /**
     * 
     */
    private static final long serialVersionUID = -2471349413867745412L;

    public static final String SVN_ID = "$Id$";

    private static final String POINTS_PER_NO = "Points per No";

    private static final String POINTS_PER_YES_MINUTE = "Points per Minute (for 1st yes)";

    private static final String BASE_POINTS_PER_YES = "Base Points per Yes";

    /**
     * properties.
     * 
     * key=name, value=default_value, type, min, max (colon delimited)
     */
    private String[][] propList = { { POINTS_PER_NO, "20:Integer" }, { POINTS_PER_YES_MINUTE, "1:Integer" }, { BASE_POINTS_PER_YES, "0:Integer" } };

    private Properties props = new Properties();

    private Object mutex = new Object();

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

    AccountList getAccountList(IModel theContest) {
        Vector<Account> accountVect = theContest.getAccounts(ClientType.Type.ALL);
        AccountList accountList = new AccountList();
        Enumeration accountEnum = accountVect.elements();
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
    private ProblemSummaryInfo calcProblemScoreData(TreeMap treeMap) {
        ProblemSummaryInfo psd = new ProblemSummaryInfo();
        int score = 0;
        int attempts = 0;
        ElementId problemId = null;
        long solutionTime = -1;
        boolean solved = false;

        if (treeMap.isEmpty()) {
            psd = null; // ProblemScoreData must have ProblemId to be valid
        } else {
            Collection coll = treeMap.values();
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
                if (run.isSolved()) {
                    // TODO: we might want some differing logic here if all
                    // yes's are counted
                    // and/or no's after yes's are counted
                    solved = true;
                    solutionTime = run.getElapsedMins();
                    score += solutionTime * getPenaltyPointsPerYesMinute() + getBasePointsPerYes();
                    break;
                } else {
                    // we should really only do this if it's been judged
                    if (run.isJudged()) {
                        score += getPenaltyPointsPerNo();
                    }
                }
            }
        }
        // TODO put another if around this if there was a setting to include all
        // no's before yes
        if (!solved) {
            score = 0;
        }
        psd.setSolved(solved);
        psd.setSolutionTime(solutionTime);
        psd.setProblemId(problemId);
        psd.setNumberSubmitted(attempts);
        psd.setPenaltyPoints(score);
        return psd;
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

    /**
     * @return additional points to assign per yes
     */
    private int getPenaltyPointsPerYesMinute() {
        return (getPropIntValue(POINTS_PER_YES_MINUTE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.core.scoring.ScoringAlgorithm#getProperties()
     */
    public Properties getProperties() {
        return props;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.csus.ecs.pc2.core.scoring.ScoringAlgorithm#getStandings(edu.csus.ecs.pc2.core.Run[], edu.csus.ecs.pc2.core.AccountList, edu.csus.ecs.pc2.core.ProblemDisplayList, java.util.Properties)
     */
    public String getStandings(IModel theContest) {
        if (theContest == null) {
            throw new InvalidParameterException("Invalid model (null)");
        }
        Properties properties = getProperties();
        if (properties == null) {
            throw new InvalidParameterException("Invalid properties (null)");
        }
        
        // TODO properties should be validated here
        props = properties;

        XMLMemento mementoRoot = XMLMemento.createWriteRoot("contestStandings");
        IMemento summaryMememento = mementoRoot.createChild("standingsHeader");
        summaryMememento.putString("title", "this is not in the model");
//        summaryMememento.putString("title", theContest.getTitle());
//        summaryMememento.putString("version", );
        AccountList accountList = getAccountList(theContest);
        Problem[] problems = theContest.getProblems();
        Hashtable <ElementId, Integer> problemsIndexHash = new Hashtable<ElementId, Integer>();
        for (int i = 0; i < problems.length; i++) {
            int id = i + 1;
            problemsIndexHash.put(problems[i].getElementId(), new Integer(id));
            IMemento problemMemento = summaryMememento.createChild("problem");
            problemMemento.putInteger("id", id);
            problemMemento.putString("title", problems[i].getDisplayName());
            // problemMemento.putString("color", problems[i].get);
        }
        Run[] runs = theContest.getRuns();
        synchronized (mutex) {
            int numAccounts = accountList.size();
            Account[] accounts = accountList.getList();
            // used in the StandingsRank comparator
            Hashtable<String, StandingsRecord> srHash = new Hashtable<String, StandingsRecord>();
            RunComparatorByTeam runComparatorByTeam = new RunComparatorByTeam();
            TreeMap<Run, Run> runTreeMap = new TreeMap<Run, Run>(runComparatorByTeam);
            Hashtable<String, Problem> probHash = new Hashtable<String, Problem>();
            for (int i = 0; i < problems.length; i++) {
                Problem problem = problems[i];
                if (problem.isActive()) {
                    probHash.put(problem.getElementId().toString(), problem);
                }
            }
            for (int i = 0; i < numAccounts; i++) {
                Account account = accounts[i];
                if (account.getClientId().getClientType() == ClientType.Type.TEAM && account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    StandingsRecord sr = new StandingsRecord();
                    SummaryRow sumRow = sr.getSummaryRow();
                    // populate summaryRow with problems
                    for (int j = 0; j < problems.length; j++) {
                        ProblemSummaryInfo psi = new ProblemSummaryInfo();
                        psi.setProblemId(problems[j].getElementId());
                        psi.setPenaltyPoints(0);
                        sumRow.put(j + 1, psi);
                    }
                    sr.setSummaryRow(sumRow);
                    sr.setClientId(account.getClientId());
                    srHash.put(account.getClientId().toString(), sr);
                }
            }
            for (int i = 0; i < runs.length; i++) {
                // skip runs that are deleted and
                // skip runs whose submitter is no longer active and
                // skip runs whose problem are no longer active
                Account account = accountList.getAccount(runs[i].getSubmitter());
                if (account == null) {
                    // TODO change to Log
                    System.out.println("account could not be located for " + runs[i].getSubmitter());
                    continue;
                }
                if (!runs[i].isDeleted() && account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) 
                        && probHash.containsKey(runs[i].getProblemId().toString())) {
                    runTreeMap.put(runs[i], runs[i]);
                }
            }
            long oldTime = 0;
            long youngTime = -1;
            if (!runTreeMap.isEmpty()) {
                Collection runColl = runTreeMap.values();
                Iterator runIterator = runColl.iterator();
                // cannot be null for 1st run
                String lastUser = "";
                String lastProblem = "";
                TreeMap<Run, Run> problemTreeMap = new TreeMap<Run, Run>(runComparatorByTeam);
                while (runIterator.hasNext()) {
                    Object o = runIterator.next();
                    Run run = (Run) o;
                    if (!lastUser.equals(run.getSubmitter().toString()) || !lastProblem.equals(run.getProblemId().toString())) {
                        if (!problemTreeMap.isEmpty()) {
                            ProblemSummaryInfo psd = calcProblemScoreData(problemTreeMap);
                            StandingsRecord sr = (StandingsRecord) srHash.get(lastUser);
                            SummaryRow sumRow = sr.getSummaryRow();
                            sumRow.put(problemsIndexHash.get(psd.getProblemId()), psd);
                            sr.setSummaryRow(sumRow);
                            sr.setPenaltyPoints(sr.getPenaltyPoints() + psd.getPenaltyPoints());
                            if (psd.isSolved()) {
                                sr.setNumberSolved(sr.getNumberSolved() + 1);
                                oldTime = sr.getLastSolved();
                                youngTime = sr.getFirstSolved();
                                if (psd.getSolutionTime() > oldTime) {
                                    sr.setLastSolved(psd.getSolutionTime());
                                }
                                if (youngTime < 0 || psd.getSolutionTime() < youngTime) {
                                    sr.setLastSolved(psd.getSolutionTime());
                                }
                            }
                            srHash.put(lastUser, sr);
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
                    ProblemSummaryInfo psd = calcProblemScoreData(problemTreeMap);
                    StandingsRecord sr = (StandingsRecord) srHash.get(lastUser);
                    SummaryRow sumRow = sr.getSummaryRow();
                    sumRow.put(problemsIndexHash.get(psd.getProblemId()), psd);
                    sr.setSummaryRow(sumRow);
                    sr.setPenaltyPoints(sr.getPenaltyPoints() + psd.getPenaltyPoints());
                    if (psd.isSolved()) {
                        sr.setNumberSolved(sr.getNumberSolved() + 1);
                        oldTime = sr.getLastSolved();
                        youngTime = sr.getFirstSolved();
                        if (psd.getSolutionTime() > oldTime) {
                            sr.setLastSolved(psd.getSolutionTime());
                        }
                        if (youngTime < 0 || psd.getSolutionTime() < youngTime) {
                            sr.setFirstSolved(psd.getSolutionTime());
                        }
                    }
                    srHash.put(lastUser, sr);
                }
                problemTreeMap.clear();
                problemTreeMap = null;
            } // else no runs

            // use TreeMap to sort
            DefaultStandingsRecordComparator src = new DefaultStandingsRecordComparator();
            src.setCachedAccountList(accountList);
            TreeMap<StandingsRecord, StandingsRecord> treeMap = new TreeMap<StandingsRecord, StandingsRecord>(src);
            Collection<StandingsRecord> enumeration = srHash.values();
            for (StandingsRecord record : enumeration) {
                treeMap.put(record, record);
            }
            StandingsRecord[] srArray = new StandingsRecord[treeMap.size()];
            Collection<StandingsRecord> coll = treeMap.values();
            Iterator iterator = coll.iterator();

            // assign the ranks
            long numSolved = -1, score = 0, lastSolved = 0;
            int rank = 0, indexRank = 0;
            int index = 0;
            while (iterator.hasNext()) {
                Object o = iterator.next();
                StandingsRecord sr = (StandingsRecord) o;
                indexRank++;
                if (numSolved != sr.getNumberSolved() || score != sr.getPenaltyPoints() || lastSolved != sr.getLastSolved()) {
                    numSolved = sr.getNumberSolved();
                    score = sr.getPenaltyPoints();
                    lastSolved = sr.getLastSolved();
                    rank = indexRank;
                    sr.setRankNumber(rank);
                } else {
                    // current user tied with last user, so same rank
                    sr.setRankNumber(rank);
                }
//                mementoRoot.putMemento(sr.toMemento());
                IMemento standingsRecordMemento = mementoRoot.createChild("teamStanding");
                standingsRecordMemento.putLong("firstSolved", sr.getFirstSolved());
                standingsRecordMemento.putLong("lasstSolved", sr.getLastSolved());
                standingsRecordMemento.putLong("points", sr.getPenaltyPoints());
                standingsRecordMemento.putInteger("solved", sr.getNumberSolved());
                standingsRecordMemento.putInteger("rank", sr.getRankNumber());
                standingsRecordMemento.putInteger("index", index);
                Account account = accountList.getAccount(sr.getClientId());
                standingsRecordMemento.putString("teamName", account.getDisplayName()); 
                standingsRecordMemento.putString("teamkey", account.getClientId().getTripletKey());
                SummaryRow summaryRow = sr.getSummaryRow();
                for (int i = 0; i < problems.length; i++) {
                    int id = i + 1;
                    ProblemSummaryInfo psi = summaryRow.get(id);
                    if (psi == null) {
                        // TODO change to Log, cleanup message (leaning towards error)
                        System.out.println("error or normal? ProblemSummaryInfo not found for problem "+ id);
                    } else {
                        IMemento psiMemento = standingsRecordMemento.createChild("problemSummaryInfo");
                        psiMemento.putInteger("index", problemsIndexHash.get(psi.getProblemId()));
                        psiMemento.putString("problemId", psi.getProblemId().toString());
                        psiMemento.putInteger("numberSubmitted", psi.getNumberSubmitted());
                        psiMemento.putInteger("points", psi.getPenaltyPoints());
                        psiMemento.putLong("solutionTime", psi.getSolutionTime());
                        psiMemento.putBoolean("isSolved", psi.isSolved());
                    }
                }
                srArray[index++] = sr;
            }
        } // mutex
        /* XXX comment out now that we have JUnit tests
        IMemento child = mementoRoot.createChild("teamStanding");
        child.putLong("firstSolved", 13);
        child.putLong("lasstSolved", 272);
        child.putLong("points", 1405);
        child.putInteger("solved", 8);
        child.putInteger("rank", 1);
        child.putInteger("index", 1);
        child.putString("teamName", "Warsaw University");
        */ 
        String xmlString;
        try {
            xmlString = mementoRoot.saveToString();
        } catch (IOException e) {
            // TODO Change to Log
            e.printStackTrace();
            xmlString = "";
        }
        System.out.println(xmlString);
        return xmlString;
    }

}
