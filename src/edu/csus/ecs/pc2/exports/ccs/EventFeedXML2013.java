package edu.csus.ecs.pc2.exports.ccs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.ProblemComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.list.RunTestCaseComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * 2013 Finals Event Feed XML.
 *
 * The CCS Event Feed is implemented in the {@link EventFeedXML}.
 * 
 * Mementos are the internal tags for a element, Elements are the XML Elements with
 * surrounding tag.
 * <P>
 * For example {@link #createElement(IInternalContest, Language, int)} will create
 * an element with a {@link #LANGUAGE_TAG} whereas the contents of the memento
 * {@link #addMemento(IMemento, IInternalContest, Language, int)}.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: EventFeedXML.java 2685 2013-06-11 13:14:16Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/exports/ccs/EventFeedXML.java $
public class EventFeedXML2013 {

    public static final String CONTEST_TAG = "contest";

    public static final String INFO_TAG = "info";

    public static final String REGION_TAG = "region";

    public static final String PROBLEM_TAG = "problem";

    public static final String LANGUAGE_TAG = "language";

    public static final String TEAM_TAG = "team";

    public static final String CLARIFICATION_TAG = "clar";

    public static final String TESTCASE_TAG = "testcase";

    public static final String RUN_TAG = "run";

    public static final String JUDGEMENT_TAG = "judgement";

    public static final String FINALIZE_TAG = "finalized";

    public static final String JUDGEMENT_RECORD_TAG = "judgement_record";

    private RunComparator runComparator = new RunComparator();
    
    private VersionInfo versionInfo = new VersionInfo();
    
    private Log log = null;
    
    public String toXML(IInternalContest contest) {
        return toXML(contest, new Filter());
    }
    
    /**
     * Return freeze XML.
     * @param contest
     * @param minutesFromEnd minutes from the end of the contest.
     * @return
     */
    public String toXMLFreeze(IInternalContest contest, long minutesFromEnd) {
        
        long mins = contest.getContestTime().getConestLengthMins() - minutesFromEnd;
        
        Filter filter = new Filter();
        filter.setFilteringDeleted(true);
        // only get XML elements for events before mins
        filter.setEndElapsedTime(mins);
        
        return toXML(contest, filter);
    }


    public String toXML(IInternalContest contest, Filter filter) {

        filter.setFilteringDeleted(true);
        
        XMLMemento mementoRoot = XMLMemento.createWriteRoot(CONTEST_TAG);

        IMemento memento = mementoRoot.createChild(INFO_TAG);
        addInfoMemento(memento, contest, contest.getContestInformation());

        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());
        for (Group group : groups) {
            memento = mementoRoot.createChild(REGION_TAG);
            addMemento(memento, contest, group);
        }

        Judgement [] judgements = contest.getJudgements();
        int sequenceNumber = 1;
        for (Judgement judgement : judgements) {
            memento = mementoRoot.createChild(JUDGEMENT_TAG);
            addMemento(memento, contest, judgement, sequenceNumber);
            sequenceNumber++;
        }    

        Language[] languages = contest.getLanguages();
        int num = 1;
        for (Language language : languages) {
            if (filter.matches(language)) {
                memento = mementoRoot.createChild(LANGUAGE_TAG);
                addMemento(memento, contest, language, num);
            }
            num++;
        }

        num = 1;
        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            if (filter.matches(problem)) {
                memento = mementoRoot.createChild(PROBLEM_TAG);
                addMemento(memento, contest, problem, num);
            }
            num++;
        }

        Vector<Account> teams = contest.getAccounts(Type.TEAM);

        Account[] accounts = (Account[]) teams.toArray(new Account[teams.size()]);
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            if (filter.matches(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                memento = mementoRoot.createChild(TEAM_TAG);
                addMemento(memento, contest, account);
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                memento = mementoRoot.createChild(RUN_TAG);
                addMemento(memento, contest, run); // add RUN
                
                RunTestCase[] runTestCases = getLastJudgementTestCases(run);
                Arrays.sort(runTestCases, new RunTestCaseComparator());
                for (RunTestCase runTestCase : runTestCases) {
                    if (filter.matchesElapsedTime(runTestCase)){
                        memento = mementoRoot.createChild(TESTCASE_TAG);
                        addMemento(memento, contest, runTestCase, run); // add TESTCASE
                    }
                }
            } else if ( ! run.isDeleted() && ! filter.matchesElapsedTime(run)) {
                /**
                 * This is for a frozen event feed.  We will send out
                 * run information without judgement information, essentially
                 * we sent out that the run is 'NEW'.
                 */
                addMemento(memento, contest, run); // add RUN
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            if (filter.matches(clarification)) {
                memento = mementoRoot.createChild(CLARIFICATION_TAG);
                addMemento(memento, contest, clarification);
            }
        }
        
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null) {
            if (finalizeData.isCertified()) {
                memento = mementoRoot.createChild(FINALIZE_TAG);
                addMemento(memento, contest, finalizeData);
            }
        }

        return toXML(mementoRoot);
    }

    private String getXMLFooterComment() {
        return "<!-- Created by "+versionInfo.getSystemName()+" build "+versionInfo.getBuildNumber()+" -->";
    }

    /**
     * return first run for client and problem.
     * 
     * @param contest
     * @param clientId
     * @param problemId
     * @return null if none found, else the first run solved.
     */
    public Run getFirstSolvedRun(IInternalContest contest, ClientId clientId, ElementId problemId) {
        Filter filter = new Filter();
        filter.setFilteringDeleted(true);
        filter.addAccount(clientId);
        filter.addProblem(contest.getProblem(problemId));
        Run[] runs = filter.getRuns(contest.getRuns());
        Arrays.sort(runs, runComparator);

        if (runs.length > 0) {
            return runs[0];
        } else {
            return null;
        }
    }

    /**
     * create info XML element
     * 
     * @param contest
     * @param filter
     * @return
     * @throws IOException
     */
    public XMLMemento createInfoElement(IInternalContest contest, Filter filter) {
        XMLMemento memento = XMLMemento.createWriteRoot(INFO_TAG);
        addInfoMemento(memento, contest, contest.getContestInformation());
        return memento;
    }

    public IMemento addInfoMemento(IMemento memento, IInternalContest contest, ContestInformation info)  {

//        <info>
//        <length>05:00:00</length>
//        <penalty>20</penalty>
//        <started>True</started>
//        <starttime>1372831200.0</starttime>
//        <title>37th Annual World Finals of the ACM International Collegiate Programming Contest</title>
//       </info>

        ContestTime time = contest.getContestTime();

        String contestLengthString = "0:0:0";
        boolean running = false;
        String formattedSeconds = "0.0";

        if (time != null) {
            contestLengthString = time.getContestLengthStr();
            running = time.isContestRunning();
            if (time.getContestStartTime() != null) {
                formattedSeconds = XMLUtilities.formatSeconds(time.getContestStartTime().getTimeInMillis());
            }
        }

        XMLUtilities.addChild(memento, "length", contestLengthString); 
        XMLUtilities.addChild(memento, "penalty", DefaultScoringAlgorithm.getDefaultProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO));
        XMLUtilities.addChild(memento, "started", titleCaseBoolean(running));
        XMLUtilities.addChild(memento, "starttime", formattedSeconds);
        XMLUtilities.addChild(memento, "title", info.getContestTitle());
        return memento;
    }

    public XMLMemento createInfoElement(IInternalContest contest, ContestInformation info)  {
        XMLMemento memento = XMLMemento.createWriteRoot(INFO_TAG);
        addInfoMemento(memento, contest, info);
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Language language, int id) {
        XMLMemento memento = XMLMemento.createWriteRoot(LANGUAGE_TAG);
        addMemento(memento, contest, language, id);
        return memento;
    }

    /**
     * Add Language fields.
     * 
     * <pre>
     * <language>
     * <name>C++</name>
     * </language>
     * </pre>
     * @param memento
     * @param contest
     * @param language
     * @param id
     * @return
     */
    public IMemento addMemento(IMemento memento, IInternalContest contest, Language language, int id) {

//        <language>
//        <id>1</id>
//        <name>C++</name>
//       </language>

        XMLUtilities.addChild(memento, "id", id);
        XMLUtilities.addChild(memento, "name", language.toString());
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Problem problem, int id) {
        XMLMemento memento = XMLMemento.createWriteRoot(PROBLEM_TAG);
        addMemento(memento, contest, problem, id);
        return memento;
    }

    /**
     * Add problem fields.
     * 
     * <pre>
     * <problem id="1" state="enabled">
     * <label>A</label>
     * <name>APL Lives!</name>
     * <balloon-color rgb="#ffff00">yellow</balloon-color>
     * </problem>
     * </pre>
     * 
     * @param memento
     * @param contest
     * @param problem
     * @param id zero based problem number
     * @return
     */
    public IMemento addMemento(IMemento memento, IInternalContest contest, Problem problem, int id) {

//        <problem>
//        <id>1</id>
//        <name>Self-Assembly</name>
//       </problem>
        
        memento.createChildNode("id", Integer.toString(id));
        memento.createChildNode("name", problem.toString());
        return memento;
    }



    /**
     * For the input number, returns an uppercase letter.
     * 
     * 1 = A, 2 = B, etc.
     * 
     * @param id problem number, based at one.
     * @return single upper case letter.
     */
    protected String getProblemLetter(int id) {
        char let = 'A';
        let += (id - 1);
        return Character.toString(let);
    }

    /**
     * This routine checks and obeys the preliminary judgement rules.
     * 
     * @param run
     * @param sendNotificationsForPreliminary 
     * @return true if run is judged and the state is valid
     */
    public boolean isValidJudgement(Run run, boolean sendNotificationsForPreliminary) {
        boolean result=false;
        if (run.getStatus().equals(RunStates.JUDGED)) {
            // done it's good & simple
            result = true;
        } else {
            // now the ugly stuff, handle being rejudged/preliminary judgements/...
            if (run.isJudged()) {
                // good... but why is the state not JUDGED
                if (run.getStatus().equals(RunStates.MANUAL_REVIEW)) {
                    if (sendNotificationsForPreliminary) {
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
     * Should a balloon be issued for this run?
     * 
     * @param run
     * @param contest 
     * @return true if valid
     */
    public boolean isValidRun(IInternalContest contest, Run run) {

        ContestInformation contestInformation = contest.getContestInformation();
        boolean sendNotification = contestInformation.isPreliminaryJudgementsTriggerNotifications();

        if ((run.isJudged() && run.isSolved()) && (!run.isDeleted() && isValidJudgement(run, sendNotification))) {

            if (!run.isSendToTeams()) {
                if (contest.isAllowed(Permission.Type.RESPECT_NOTIFY_TEAM_SETTING)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Return list all problems that team has solved.
     * 
     * @param contest
     * @param id
     * @return
     */
    protected Problem [] getSolvedRuns (IInternalContest contest, ClientId id) {

        Filter filter = new Filter();
        filter.setFilteringDeleted(true);
        filter.addAccount(id);

        Run [] runs = filter.getRuns(contest.getRuns());

        Vector<Problem> probVector = new Vector<Problem>();
        for (int i = 0; i < runs.length; i++) {
            Run run = runs[i];
            if (! run.isDeleted()) {
                Problem problem = contest.getProblem(run.getProblemId());
                if (isValidRun(contest, run) && !probVector.contains(problem)) {
                    probVector.add(problem);
                }
            }
        }

        Problem [] problems = (Problem[]) probVector.toArray(new Problem[probVector.size()]);
        Arrays.sort(problems, new ProblemComparator(contest));
        return problems;
    }

    public XMLMemento createElement(IInternalContest contest, Account account) {
        XMLMemento memento = XMLMemento.createWriteRoot(TEAM_TAG);
        addMemento(memento, contest, account);
        return memento;
    }

    /**
     * Create account memento.
     * 
     * <pre>
     * <team id="1" external-id="23412">
     * <name>American University of Beirut</name>
     * <nationality>LBN</nationality>
     * <university>American University of Beirut</university>
     * <region>Europe</region>
     * </team>
     * </pre>
     * @param memento
     * @param contest
     * @param account
     * @return
     */
    public IMemento addMemento(IMemento memento, IInternalContest contest, Account account) {
        
//        <team>
//        <external-id>171936</external-id>
//        <id>107</id>
//        <name>University of New South Wales</name>
//        <nationality>AUS</nationality>
//        <region>South Pacific</region>
//        <university>University of New South Wales</university>
//       </team>


        int teamId =  account.getClientId().getClientNumber();
        
        XMLUtilities.addChild(memento, "external-id", useDefaultIfEmpty (account.getExternalId(),"836577"+teamId));
        XMLUtilities.addChild(memento, "id", teamId);
        XMLUtilities.addChild(memento, "name", account.getDisplayName());
        
        XMLUtilities.addChild(memento, "nationality", account.getCountryCode());

        String regionName = getRegionName(contest, account);
        XMLUtilities.addChild(memento, "region", regionName);
        
        XMLUtilities.addChild(memento, "university", account.getDisplayName());
        
        return memento;
    }

    /**
     * If string null or empty return defaultString.
     * @param value
     * @param defaultString
     * @return
     */
    private String useDefaultIfEmpty(String value, String defaultString) {
        if (value == null || "".equals(value.trim())) {
            return defaultString;
        } else {
            return value;
        }
    }

    private String getRegionName(IInternalContest contest, Account account) {
        
        /**
         * This code is in place because sometimes groupId is null.
         */
        
        String regionName = "";

        try {
            if (account.getGroupId() != null) {
                Group group = contest.getGroup(account.getGroupId());
                regionName = group.getDisplayName();
            }
        } catch (Exception e) {
            System.out.println("Failed to lookup group for "+account+" group id = "+account.getGroupId());
            e.printStackTrace();
        }

        return regionName;
    }

    public XMLMemento createElement(IInternalContest contest, RunTestCase testCase, Run run) {
        XMLMemento memento = XMLMemento.createWriteRoot(TESTCASE_TAG);
        addMemento(memento, contest, testCase, run);
        return memento;
    }

    /**
     * RunTestCase/TESTCASE memento.
     * @param memento
     * @param contest
     * @param testCase
     * @param run
     * @param problem
     */
    public IMemento addMemento(IMemento memento, IInternalContest contest, RunTestCase testCase, Run run) {

//        <testcase>
//        <i>3</i>
//        <judged>True</judged>
//        <judgement_id>78</judgement_id>
//        <n>57</n>
        
        Problem problem = contest.getProblem(run.getProblemId());
        
        XMLUtilities.addChild(memento, "i", testCase.getTestNumber());
        XMLUtilities.addChild(memento, "judged", titleCaseBoolean(run.isJudged()));
        XMLUtilities.addChild(memento, "judgement_id", run.getNumber());
        XMLUtilities.addChild(memento, "n", problem.getNumberTestCases());
        
//        <result>AC</result>
//        <run-id>78</run-id>
//        <solved>True</solved>
        
        String result = Judgement.ACRONYM_JUDGING_ERROR;
        if (testCase.isSolved()){
            result = Judgement.ACRONYM_ACCEPTED;
        }
        XMLUtilities.addChild(memento, "result", result);
        XMLUtilities.addChild(memento, "run-id", run.getNumber());
        XMLUtilities.addChild(memento, "solved", titleCaseBoolean (testCase.isSolved()));
        
//        <time>637.307141</time>
//        <timestamp>1372831837.31</timestamp>
//       </testcase>
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(run.getElapsedMS()));
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());

        return memento;
    }  

    public XMLMemento createElement(IInternalContest contest, Clarification clarification) {
        XMLMemento memento = XMLMemento.createWriteRoot(CLARIFICATION_TAG);
        addMemento(memento, contest, clarification);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Clarification clarification) {

//        <clar>
//        <answer></answer>
//        <answered>False</answered>
//        <id>1</id>
//        <question>Are the calculators provided?</question>

        String answer = clarification.getAnswer();
        if (answer == null) {
            answer = "";
        }
        XMLUtilities.addChild(memento, "answer", answer);
        XMLUtilities.addChild(memento, "id", clarification.getNumber());
        XMLUtilities.addChild(memento, "answered", titleCaseBoolean(clarification.isAnswered()));
        XMLUtilities.addChild(memento, "question", clarification.getQuestion());
        
//        <status>fresh</status>
//        <team>53</team>
//        <time>1194.626055</time>

        String status = getStatus(clarification.isAnswered());
        XMLUtilities.addChild(memento, "status", status);
        XMLUtilities.addChild(memento, "team", clarification.getSubmitter().getClientNumber());
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(clarification.getElapsedMS()));
        
//        <timestamp>1372832394.63</timestamp>
//        <to-all>False</to-all>
//       </clar>
//       <clar>
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());
        XMLUtilities.addChild(memento, "to-all", titleCaseBoolean(clarification.isSendToAll()));
        
        return memento;
    }

    private String getStatus(boolean value) {
        if (value){
            return "done";
        } else {
            return "fresh";
        }
    }

    /**
     * Return the problem index (starting at/base one)).
     * @param contest
     * @param inProblem
     * @return one based number for problem.
     */
    private int getProblemIndex(IInternalContest contest, Problem inProblem) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
            if (problem.getElementId().equals(inProblem.getElementId())) {
                return idx + 1;
            }
            idx++;
        }

        return -1;
    }

    /**
     * Create RUN XML element.
     * 
     * @param contest
     * @param run
     * @param suppressJudgement if true, do not output judgement information.
     * @return
     */
    public XMLMemento createElement(IInternalContest contest, Run run) {
        XMLMemento memento = XMLMemento.createWriteRoot(RUN_TAG);
        addMemento(memento, contest, run);
        return memento;
    }

    /**
     * Add RUN element.
     * 
     * @param memento
     * @param contest
     * @param run
     * @return
     */
    public IMemento addMemento(IMemento memento, IInternalContest contest, Run run) {

//        <run>
//        <id>1698</id>
//        <judged>True</judged>
//        <language>C++</language>
//        <penalty>True</penalty>
//        <problem>9</problem>
        
        XMLUtilities.addChild(memento, "id", run.getNumber());
        XMLUtilities.addChild(memento, "judged", titleCaseBoolean(run.isJudged()));

        Language language = contest.getLanguage(run.getLanguageId());
        XMLUtilities.addChild(memento, "language", language.getDisplayName());
        
        if ((!run.isSolved()) && isYoungerThanFirstYes(contest, run)) {
            // If this a "no" run and run is younger than first yes.
            XMLUtilities.addChild(memento, "penalty", "True");
        } else {
            XMLUtilities.addChild(memento, "penalty", "False");
        }
        
        Problem problem = contest.getProblem(run.getProblemId());
        int problemIndex = getProblemIndex(contest, problem);
        XMLUtilities.addChild(memento, "problem", problemIndex);
        
//        <result>TLE</result>
//        <solved>False</solved>
//        <status>done</status>
        
        Judgement judgement = contest.getJudgement(run.getJudgementRecord().getJudgementId());
        String acronym = getAcronym(judgement);
        XMLUtilities.addChild(memento, "result", acronym);
        
        XMLUtilities.addChild(memento, "solved", run.isSolved());
        XMLUtilities.addChild(memento, "status", getStatus(run.isJudged()));
        
//        <team>50</team>
//        <time>17938.976757</time>
//        <timestamp>1372849248.21</timestamp>
//       </run>

        XMLUtilities.addChild(memento, "team",  run.getSubmitter().getClientNumber());
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(run.getElapsedMS()));
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());

        return memento;
    }

    /**
     * Is this run younger than first Yes?
     * @param contest
     * @param run
     * @return true if run is younger than first yes, false if no solution or run is actually younger than input solved run.
     */
    protected boolean isYoungerThanFirstYes(IInternalContest contest, Run run) {
        
        Run firstYes = findFistYes (contest, run.getSubmitter(), run.getProblemId());
        
        if (firstYes == null){
            // no solution found
            return false;
        } else {
            return run.getElapsedMS() < firstYes.getElapsedMS(); 
        }
    }

    /**
     * Finds first (earliest) run.
     * @return null if no such run, else the first/youngest run from submitter and problem.
     */
    protected Run findFistYes(IInternalContest contest, ClientId submitter, ElementId problemId) {
        Run[] runs = contest.getRuns();
        
        Run lastYesRun = null;
        
        for (Run run : runs) {
            if (run.isSolved()){
                if (run.getProblemId().equals(problemId)){
                    if (run.getSubmitter().equals(submitter)){
                        // same team
                        if (lastYesRun == null){
                            lastYesRun = run;
                        } else {
                            if (lastYesRun.getElapsedMS() > run.getElapsedMS()){
                                lastYesRun = run;
                            }
                        }
                    }
                }
            }
        }
        
        return lastYesRun;
        
    }

    /**
     * @throws IOException
     */
    protected String toXML(XMLMemento mementoRoot)  {
        try {
            return mementoRoot.saveToString(true) + getXMLFooterComment();
        } catch (IOException e) {
            logWarning("Error in creating XML", e);
            return "";
        }
    }
    
    public String createStartupXML(IInternalContest contest) {
        Filter filter = new Filter();
        filter.setFilteringDeleted(true);
        return createStartupXML(contest, filter);
    }

    /**
     * Starts contest XML and adds all configuration data/values.
     * 
     * @param contest
     * @param judgement
     * @return
     */
    public String createStartupXML(IInternalContest contest, Filter filter) {

        StringBuffer sb = new StringBuffer("<" + CONTEST_TAG + ">");

        sb.append(toXML(createInfoElement(contest, contest.getContestInformation())));

        int idx;

        idx = 1;
        for (Language language : contest.getLanguages()) {
            sb.append(toXML(createElement(contest, language, idx)));
            idx++;
        }

        idx = 1;
        for (Problem problem : contest.getProblems()) {
            if (filter.matches(problem)){
                sb.append(toXML(createElement(contest, problem, idx)));
            }
            idx++;
        }
        
        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());
        for (Group group : groups) {
            sb.append(toXML(createElement(contest, group, idx)));
        }

        int sequenceNumber = 1;
        for (Judgement judgement : contest.getJudgements()) {
            sb.append(toXML(createElement(contest, judgement, sequenceNumber)));
            sequenceNumber ++;
        }

        Account [] teamAccounts = getTeamAccounts(contest);
        for (Account account : teamAccounts){
            if (filter.matches(account) && contest.isAllowed(account.getClientId(), Permission.Type.DISPLAY_ON_SCOREBOARD)){
                sb.append(toXML(createElement(contest, account)));
            }
        }

        Clarification[] clarifications = contest.getClarifications();
        for (Clarification clarification : clarifications) {
            sb.append(toXML(createElement(contest, clarification)));
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {

            if (filter.matches(run)) {
                sb.append(toXML(createElement(contest, run))); // add RUN
                
                RunTestCase[] runTestCases = getLastJudgementTestCases(run);
                Arrays.sort(runTestCases, new RunTestCaseComparator());
                for (RunTestCase runTestCase : runTestCases) {
                    if (filter.matchesElapsedTime(runTestCase)){
                        sb.append(toXML(createElement(contest, runTestCase, run))); // add TESTCASE
                    }
                }
            } else if ( ! filter.matchesElapsedTime(run)) {
                /**
                 * This is for a frozen event feed.  We will send out
                 * run information without judgement information, essentially
                 * we sent out that the run is 'NEW'.
                 */
                sb.append(toXML(createElement(contest, run))); // add RUN
            }
        }
        return sb.toString();
    }

    public XMLMemento createElement(IInternalContest contest, Group group, int idx) {
        XMLMemento memento = XMLMemento.createWriteRoot(REGION_TAG);
        addMemento(memento, contest, group);
        return memento;
    }

    /**
     * Get the list of run cases for the last judgement.
     * @param run
     */
    protected RunTestCase[] getLastJudgementTestCases(Run run) {
        
        ArrayList <RunTestCase> cases = new ArrayList<RunTestCase>();
        
        if (run.isJudged()){
            RunTestCase[] runTestCases = run.getRunTestCases();
            JudgementRecord judgementRecord = run.getJudgementRecord();
            
            for (RunTestCase runTestCase : runTestCases) {
                if (runTestCase.matchesJudgement(judgementRecord)){
                    cases.add(runTestCase);
                }
            }
        }
        
        return (RunTestCase[]) cases.toArray(new RunTestCase[cases.size()]);
    }

    /**
     * @param contest
     * @return team accounts sorted by site, team number
     */
    public Account[] getTeamAccounts(IInternalContest inContest) {
        Vector<Account> accountVector = inContest.getAccounts(ClientType.Type.TEAM);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    public String createFinalizeXML(IInternalContest contest, FinalizeData data) {

        StringBuffer sb = new StringBuffer();

        XMLMemento memento = XMLMemento.createWriteRoot(FINALIZE_TAG);

        addMemento (memento, contest, data); 

        sb.append(toXML(memento));

        sb.append("</");
        sb.append(CONTEST_TAG);
        sb.append(">");
        return sb.toString();
    }

    private void addMemento(IMemento memento, IInternalContest contest, FinalizeData data) {

//        <finalized>
//        <comment>Certified by: Gunnar</comment>
//        <last-bronze>13</last-bronze>
//        <last-gold>4</last-gold>
//        <last-silver>8</last-silver>
//        <time>0</time>
//        <timestamp>1372850293.24</timestamp>
//       </finalized>

        XMLUtilities.addChild(memento, "comment", data.getComment());
        XMLUtilities.addChild(memento, "last-bronze", data.getBronzeRank());
        XMLUtilities.addChild(memento, "last-gold", data.getGoldRank());
        XMLUtilities.addChild(memento, "last-silver", data.getSilverRank());
        XMLUtilities.addChild(memento, "time", "0"); // TODO CCS is this hard coded/unused unset ??
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());
    }

    public XMLMemento createElement(IInternalContest contest, Group group) {
        XMLMemento memento = XMLMemento.createWriteRoot(REGION_TAG);
        addMemento(memento, contest, group);
        return memento;
    }

    public void addMemento(IMemento memento, IInternalContest contest, Group group) {
        
//        <region>
//        <external-id>5752</external-id>
//        <name>South Pacific</name>
//       </region>

        XMLUtilities.addChild(memento, "external-id", group.getGroupId());
        XMLUtilities.addChild(memento, "name", group.getDisplayName());
    }

    public XMLMemento createElement(IInternalContest contest, Judgement judgement, int sequenceNumber) {
        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_TAG);
        addMemento(memento, contest, judgement, sequenceNumber);
        return memento;
    }
    
    private String getAcronym(Judgement judgement) {

        if (Judgement.ACRONYM_OTHER_CONTACT_STAFF.equals(judgement.getAcronym())) {
            return Judgement.ACRONYM_JUDGING_ERROR;
        }

        if (judgement.getAcronym() == null || judgement.getAcronym().length() == 0) {
            return Judgement.ACRONYM_JUDGING_ERROR;
        } else {
            return judgement.getAcronym();
        }
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Judgement judgement, int judgementSequence) {
        
//        <judgement>
//        <acronym>CE</acronym>
//        <id>8</id>
//        <name>Compile Error</name>
//       </judgement>

        String name = judgement.getDisplayName();
        String acronym = getAcronym(judgement);
        
        XMLUtilities.addChild(memento, "acronym", acronym); 
        XMLUtilities.addChild(memento, "id", judgementSequence); 
        XMLUtilities.addChild(memento, "name", name);
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, JudgementRecord judgementRecord) {
        // Judgement Record 
        // <judgement>
        // <acronym>CE</acronym>
        // <name>Compile Error</name>
        // </judgement>

        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_RECORD_TAG);
        Judgement judgement = contest.getJudgement(judgementRecord.getJudgementId());

        XMLUtilities.addChild(memento, "acronym", getAcronym(judgement));
        XMLUtilities.addChild(memento, "name", judgement.getDisplayName());
        return memento;
    }

    private String titleCaseBoolean(boolean value) {
        if (value) {
            return "True";
        } else {
            return "False";
        }
    }

    private void logWarning(String message, Exception e) {
        log.log(Log.WARNING, message, e);
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
