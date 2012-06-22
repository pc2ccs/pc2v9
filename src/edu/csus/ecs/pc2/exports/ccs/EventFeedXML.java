package edu.csus.ecs.pc2.exports.ccs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.BalloonDeliveryComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.ProblemComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ClientType;
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
import edu.csus.ecs.pc2.core.model.Notification;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Event Feed XML.
 * 
 * Class used <li>to CCS Standard Event Feed output XML based on contest data.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedXML {

    // TODO move EventFeedXML to package pc2.core ?

    public static final String CONTEST_TAG = "contest";

    public static final String INFO_TAG = "info";
    
    public static final String REGION_TAG = "region";

    public static final String PROBLEM_TAG = "problem";

    public static final String LANGUAGE_TAG = "language";

    public static final String TEAM_TAG = "team";

    public static final String CLARIFICATION_TAG = "clarification";

    public static final String RUN_TAG = "run";

    public static final String JUDGEMENT_TAG = "judgement";

    public static final String FINALIZE_TAG = "finalize";

    public static final String JUDGEMENT_RECORD_TAG = "judgement_record";
    
    public static final String BALLOON_TAG = "balloon";

    public static final String BALLOON_LIST_TAG = "balloons";
    
    public static final String NOTIFICATION_TAG = "notification";

    private RunComparator runComparator = new RunComparator();

    public String toXML(IInternalContest contest) throws IOException {
        return toXML(contest, new Filter());
    }

    public String toXML(IInternalContest contest, Filter filter) throws IOException {

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
        for (Judgement judgement : judgements) {
            memento = mementoRoot.createChild(JUDGEMENT_TAG);
            addMemento(memento, contest, judgement);
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
            if (filter.matches(account)) {
                memento = mementoRoot.createChild(TEAM_TAG);
                addMemento(memento, contest, account);
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            if (filter.matches(run)) {
                memento = mementoRoot.createChild(RUN_TAG);
                addMemento(memento, contest, run);
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
        
        // Create notifications
        
        BalloonDeliveryInfo [] deliveries = getBalloonDeliveries ( contest);
        Arrays.sort (deliveries, new BalloonDeliveryComparator(contest));
        int notificationSequenceNumber = 1;
        
        for (BalloonDeliveryInfo balloonDeliveryInfo : deliveries) {
            Run run = getFirstSolvedRun (contest, balloonDeliveryInfo.getClientId(), balloonDeliveryInfo.getProblemId());
            
            memento = mementoRoot.createChild(NOTIFICATION_TAG);
            addMemento( memento,  contest, run,  notificationSequenceNumber);
            notificationSequenceNumber ++;
        }
        
        String finalizeXML = "";
        
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null) {
            if (finalizeData.isCertified()) {
                finalizeXML = createFinalizeXML(contest, finalizeData);
            }
        }

        return mementoRoot.saveToString() + finalizeXML;
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
     * Returns deliveries
     * @param contest
     * @return if no deliveries returns an empty array
     */
    public BalloonDeliveryInfo[] getBalloonDeliveries(IInternalContest contest) {
        
        BalloonDeliveryInfo[] deliveries = new BalloonDeliveryInfo[0];

        BalloonSettings balloonSettings = contest.getBalloonSettings(contest.getSiteNumber());
        
        if (balloonSettings == null) {
            return deliveries;
        } else {

            ClientSettings settings = new ClientSettings(balloonSettings.getBalloonClient());
            Hashtable<String, BalloonDeliveryInfo> deliveryHash = settings.getBalloonList();

            ArrayList<BalloonDeliveryInfo> balloonDeliveryArray = Collections.list(deliveryHash.elements());
            BalloonDeliveryInfo[] balloonDeliveryInfos = (BalloonDeliveryInfo[]) balloonDeliveryArray.toArray(new BalloonDeliveryInfo[balloonDeliveryArray.size()]);
            
            return balloonDeliveryInfos;
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
    public XMLMemento createInfoElement(IInternalContest contest, Filter filter) throws IOException {
        XMLMemento memento = XMLMemento.createWriteRoot(INFO_TAG);
        addInfoMemento(memento, contest, contest.getContestInformation());
        return memento;
    }

    public IMemento addInfoMemento(IMemento memento, IInternalContest contest, ContestInformation info)  {
        
        
//        <info>
//        <title>The 2010 World Finals of the ACM International Collegiate Programming Contest</title>
//        <length>05:00:00</length>
//        <penalty>20</penalty>
//        <started>false</started>
//        <starttime>1265335138.26</starttime>
//        </info>

        
        ContestTime time = contest.getContestTime();

        XMLUtilities.addChild(memento, "title", info.getContestTitle());
        
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
        XMLUtilities.addChild(memento, "started", running);
        XMLUtilities.addChild(memento, "starttime", formattedSeconds);
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

    public IMemento addMemento(IMemento memento, IInternalContest contest, Language language, int id) {

        // <language>
        // <name>C++</name>
        // </language>

        memento.putInteger("id", id);
        XMLUtilities.addChild(memento, "name", language.toString());
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Problem problem, int id) {
        XMLMemento memento = XMLMemento.createWriteRoot(PROBLEM_TAG);
        addMemento(memento, contest, problem, id);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Problem problem, int id) {

        // <problem id="1" state="enabled">
        // <label>A</label>
        // <name>APL Lives!</name>
        // <balloon-color rgb="#ffff00">yellow</balloon-color>
        // </problem>

        memento.putInteger("id", id);
        memento.putBoolean("enabled", problem.isActive());

        String problemLetter = getProblemLetter(id);
        memento.createChildNode("label", problemLetter);
        memento.createChildNode("name", problem.toString());
        
        BalloonSettings settings = contest.getBalloonSettings(contest.getSiteNumber());
        if (settings != null){
            String color = settings.getColor(problem);
            IMemento balloonColor = memento.createChildNode("balloon-color", ""+color);
            String rgbColor = settings.getColorRGB(problem);
            balloonColor.putString("rgb", ""+rgbColor);
        }
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

    public IMemento addBalloonMemento(IMemento memento, IInternalContest contest, Problem problem) {

        // <balloon problem-id="2">
        // <label>B</label>
        // <name>Bulls and bears</name>
        // <color rgb="ff0000">red</color>
        // </balloon>
        
        int problemIndex = getProblemIndex(contest, problem);
        memento.putInteger("problem-id", problemIndex);

        XMLUtilities.addChild(memento, "label", getProblemLetter(problemIndex));
        XMLUtilities.addChild(memento, "name", contest.getProblem(problem.getElementId()).getDisplayName());
        
        IMemento colorMemento = XMLUtilities.addChild(memento, "color", getProblemBalloonColor(contest, problem));
        colorMemento.putString("rgb", getProblemRGB(contest, problem));
        return memento;
    }

    private String getProblemRGB(IInternalContest contest, Problem problem) {
        BalloonSettings settings = contest.getBalloonSettings(contest.getSiteNumber());
        return settings.getColorRGB(problem);
    }

    private String getProblemBalloonColor(IInternalContest contest, Problem problem) {
        BalloonSettings settings = contest.getBalloonSettings(contest.getSiteNumber());
        return settings.getColor(problem);
    }
    
    public XMLMemento createBalloonElement(IInternalContest contest, Problem problem) {
        XMLMemento memento = XMLMemento.createWriteRoot(BALLOON_TAG);
        addBalloonMemento(memento, contest, problem);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Notification notification) {
        // <notification id="214" team-id="34">
        // <team>U Waterloo</team>
        // <contest-time>132.04</contest-time>
        // <timestamp>1298733213.10</timestamp>

        // <balloon problem-id="2">
        // <label>B</label>
        // <name>Bulls and bears</name>
        // <color rgb="ff0000">red</color>
        // </balloon>

        // <first-by-team>true</first-by-team>
        
        ClientId clientId = notification.getSubmitter();

        memento.putInteger("id", notification.getNumber());
        memento.putInteger("team-id", clientId.getClientNumber());

        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(notification.getElapsedMS()));
        XMLUtilities.addChild(memento, "timestamp", notification.getTimeSent());
        XMLUtilities.addChild(memento, "team", contest.getAccount(clientId).getDisplayName());

        Problem[] problems = getSolvedRuns(contest, clientId);

        boolean firstSolvedByteam = problems.length == 1;
        XMLUtilities.addChild(memento, "first-by-team", firstSolvedByteam);
        
        Problem solvedProblem = contest.getProblem(notification.getProblemId());
        
        IMemento singleBalloon = memento.createChild(BALLOON_TAG);
        addBalloonMemento(singleBalloon, contest, solvedProblem);

        // <balloons>
        // <balloon problem-id="4">
        // <label>D</label>
        // <name>Down the hill</name>
        // <color rgb="33cc00">green</color>
        // </balloon>
        // <balloon problem-id="6">
        // <label>F</label>
        // <name>Failing to make the grade</name>
        // <color rgb="ffff00">yellow</color>
        // </balloon>
        // </balloons>
        // </notification>

        IMemento balloonsRoot = memento.createChild(BALLOON_LIST_TAG);

        for (Problem problem : problems) {
            memento = balloonsRoot.createChild(BALLOON_TAG);
            addBalloonMemento(memento, contest, problem);
        }
        
        return memento; 
        
    }
    
    public IMemento addMemento(IMemento memento, IInternalContest contest, Run run, int notificationSequenceNumber) {

        // <notification id="214" team-id="34">
        // <team>U Waterloo</team>
        // <contest-time>132.04</contest-time>
        // <timestamp>1298733213.10</timestamp>

        // <balloon problem-id="2">
        // <label>B</label>
        // <name>Bulls and bears</name>
        // <color rgb="ff0000">red</color>
        // </balloon>

        // <first-by-team>true</first-by-team>
        
        ClientId clientId = run.getSubmitter();

        memento.putInteger("id", notificationSequenceNumber);
        memento.putInteger("team-id", clientId.getClientNumber());

        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMins() * 1000));
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());
        XMLUtilities.addChild(memento, "team", contest.getAccount(clientId).getDisplayName());

        Problem[] problems = getSolvedRuns(contest, clientId);

        boolean firstSolvedByteam = problems.length == 1;
        XMLUtilities.addChild(memento, "first-by-team", firstSolvedByteam);
        
        Problem solvedProblem = contest.getProblem(run.getProblemId());
        
        IMemento singleBalloon = memento.createChild(BALLOON_TAG);
        addBalloonMemento(singleBalloon, contest, solvedProblem);

        // <balloons>
        // <balloon problem-id="4">
        // <label>D</label>
        // <name>Down the hill</name>
        // <color rgb="33cc00">green</color>
        // </balloon>
        // <balloon problem-id="6">
        // <label>F</label>
        // <name>Failing to make the grade</name>
        // <color rgb="ffff00">yellow</color>
        // </balloon>
        // </balloons>
        // </notification>

        IMemento balloonsRoot = memento.createChild(BALLOON_LIST_TAG);

        for (Problem problem : problems) {
            memento = balloonsRoot.createChild(BALLOON_TAG);
            addBalloonMemento(memento, contest, problem);

        }
        
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, Account account) {
        XMLMemento memento = XMLMemento.createWriteRoot(TEAM_TAG);
        addMemento(memento, contest, account);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Account account) {

        // <team id="1" external-id="23412">
        // <name>American University of Beirut</name>
        // <nationality>LBN</nationality>
        // <university>American University of Beirut</university>
        // <region>Europe</region>
        // </team>

        memento.putInteger("id", account.getClientId().getClientNumber());
        memento.putString("external-id", account.getExternalId());

        XMLUtilities.addChild(memento, "name", account.getDisplayName());
        XMLUtilities.addChild(memento, "nationality", account.getCountryCode());
        XMLUtilities.addChild(memento, "university", account.getLongSchoolName());

        String regionName = "";
        if (account.getGroupId() != null) {
            Group group = contest.getGroup(account.getGroupId());
            regionName = group.getDisplayName();
        }

        XMLUtilities.addChild(memento, "region", regionName);
        return memento;
    }
    
//  TODO CCS add TestCaseResults class
//  TODO CCS add TestCaseResults [] JudgementRecord.getTestCaseResults();
    
//    
//    public XMLMemento createElement(IInternalContest contest, Testcase testcase) {
//        XMLMemento memento = XMLMemento.createWriteRoot(CLARIFICATION_TAG);
//        addMemento(memento, contest, testcase);
//        return memento;
//    }

    public XMLMemento createElement(IInternalContest contest, Clarification clarification) {
        XMLMemento memento = XMLMemento.createWriteRoot(CLARIFICATION_TAG);
        addMemento(memento, contest, clarification);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Clarification clarification) {

        // <clar id="1" team-id="0" problem-id="1">
        // <answer>The number of pieces will fit in a signed 32-bit integer.
        // </answer>
        // <question>What is the upper limit on the number of pieces of chocolate
        // requested by the friends?</question>
        // <to-all>true</to-all>
        // <contest-time>118.48</contest-time>
        // <timestamp>1265335256.74</timestamp>
        // </clar>

        memento.putInteger("id", clarification.getNumber());
        memento.putInteger("team-id", clarification.getNumber());

        Problem problem = contest.getProblem(clarification.getProblemId());
        memento.putInteger("problem-id", getProblemIndex(contest, problem));

        String answer = clarification.getAnswer();
        if (answer == null) {
            answer = "";
        }
        XMLUtilities.addChild(memento, "answer", answer);
        XMLUtilities.addChild(memento, "question", clarification.getQuestion());
        XMLUtilities.addChild(memento, "to-all", clarification.isSendToAll());
        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(clarification.getElapsedMins() * 1000));
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());
        return memento;
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

    public XMLMemento createElement(IInternalContest contest, Run run) {
        XMLMemento memento = XMLMemento.createWriteRoot(RUN_TAG);
        addMemento(memento, contest, run);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Run run) {

        // OLD
        // <run time="1265353100290">
        // <id>1410</id>
        // <judged>True</judged>
        // <language>C++</language>
        // <penalty>True</penalty>
        // <problem>4</problem>
        // <result>WA</result>
        // <solved>False</solved>
        // <team>74</team>
        // <time>17960.749403</time>
        // <timestamp>1265353100.29</timestamp>
        // </run>

        /**
         * In a newer version of the Event Feed wiki page the run element was simplified and lost a number of useful tags: solved and judged.
         */
        
        // TODO CCS add solved ?
        // TODO CCS add judged ?
        
        // <run id="1410" team-id="74" problem-id="4">
        // <language>C++</language>
        // <judgement>WA</judgement>
        // <penalty>true</penalty>
        // <contest-time>17960.74</contest-time>
        // <timestamp>1265353100.29</timestamp>
        // </run>

        memento.putInteger("id", run.getNumber());
        memento.putInteger("team-id", run.getSubmitter().getClientNumber());
        Problem problem = contest.getProblem(run.getProblemId());
        int problemIndex = getProblemIndex(contest, problem);
        memento.putInteger("problem-id", problemIndex);

        XMLUtilities.addChild(memento, "judged", run.isJudged());

        Language language = contest.getLanguage(run.getLanguageId());
        XMLUtilities.addChild(memento, "language", language.getDisplayName());

        XMLUtilities.addChild(memento, "penalty", "TODO"); // TODO CCS What ispenalty ??
        
        if (run.isJudged()){
            ElementId judgementId = run.getJudgementRecord().getJudgementId();
            String acronym = contest.getJudgement(judgementId).getAcronym();
            if (acronym == null) {
                acronym = "?";
            }
            XMLUtilities.addChild(memento, "judgement", acronym);

            // old XML name/values result and solved.
            // XMLUtilities.addChild(memento, "result", judgement.toUpperCase().substring(0, 2));
            // XMLUtilities.addChild(memento, "solved", run.isSolved());
        }

        XMLUtilities.addChild(memento, "team", run.getSubmitter().getClientNumber());
        XMLUtilities.addChild(memento, "elapsed-Mins", run.getElapsedMins());
        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMS()));
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());

        return memento;
    }

    /**
     * @throws IOException
     */
    private String toXML(XMLMemento mementoRoot) throws IOException {
        return mementoRoot.saveToString(true);
    }

    /**
     * Starts contest XML and adds all configuration data/values.
     * 
     * @param contest
     * @param judgement
     * @return
     */
    public String createStartupXML(IInternalContest contest) {

        // TODO CCS write handle exception code
        
        StringBuffer sb = new StringBuffer("<" + CONTEST_TAG + ">");

        /**
         * A general implementation for logging errors needs to be established. Perhaps something with log4j ? The goal should be to standardized logging in such a way that Exceptions are not lost.
         */

        try {
            sb.append(toXML(createInfoElement(contest, contest.getContestInformation())));
        } catch (IOException e) {
            // TODO CCS Auto-generated catch block
            e.printStackTrace();
        }

        int idx;

        idx = 1;
        for (Language language : contest.getLanguages()) {
            try {
                sb.append(toXML(createElement(contest, language, idx)));
            } catch (IOException e) {
                // TODO CCS Auto-generated catch block
                e.printStackTrace();
            }
            idx++;
        }

        idx = 0;
        for (Problem problem : contest.getProblems()) {
            try {
                sb.append(toXML(createElement(contest, problem, idx)));
            } catch (IOException e) {
                // TODO CCS Auto-generated catch block
                e.printStackTrace();
            }
            idx++;
        }

        for (Judgement judgement : contest.getJudgements()) {
            try {
                sb.append(toXML(createElement(contest, judgement)));
            } catch (IOException e) {
                // TODO CCS Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        // TODO ACCOUNT huh
        
        Account [] teamAccounts = getTeamAccounts(contest);
        for (Account account : teamAccounts){
            
            try {
                sb.append(toXML(createElement(contest, account)));
            } catch (IOException e) {
                // TODO CCS Auto-generated catch block
                e.printStackTrace();
            }
            
        }

        Clarification[] clarifications = contest.getClarifications();
        for (Clarification clarification : clarifications) {
            try {
                sb.append(toXML(createElement(contest, clarification)));
            } catch (IOException e) {
                // TODO CCS Auto-generated catch block
                e.printStackTrace();
            }
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            try {
                sb.append(toXML(createElement(contest, run)));
            } catch (IOException e) {
                // TODO CCS Auto-generated catch block
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * Get all sites' teams sorted by site then team number.
     * 
     * @param contest
     * @return
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

        try {
            sb.append(toXML(memento));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("</");
        sb.append(CONTEST_TAG);
        sb.append(">");
        return sb.toString();
    }

    private void addMemento(XMLMemento memento, IInternalContest contest, FinalizeData data) {

        // <finalized>
        // <last-gold>4</last-gold>
        // <last-silver>8</last-silver>
        // <last-bronze>12</last-bronze>
        // <comment>Finalized by John Doe and Jane Doe</comment>
        // <timestamp>1265336078.01</timestamp>
        // </finalized>

        XMLUtilities.addChild(memento, "last-gold", data.getGoldRank());
        XMLUtilities.addChild(memento, "last-silver", data.getSilverRank());
        XMLUtilities.addChild(memento, "last-bronze", data.getBronzeRank());
        XMLUtilities.addChild(memento, "comment", data.getComment());
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());
    }

    public XMLMemento createElement(IInternalContest contest, Group group) {
        XMLMemento memento = XMLMemento.createWriteRoot(REGION_TAG);
        addMemento(memento, contest, group);
        return memento;
    }
    
    public void addMemento(IMemento memento, IInternalContest contest, Group group) {
        // <region external-id="3012">
        //   <name>Europe</name>
        // </region>
        
        memento.putInteger("id", group.getGroupId());
        XMLUtilities.addChild(memento, "name", group.getDisplayName());
    }

    public XMLMemento createElement(IInternalContest contest, Judgement judgement) {
        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_TAG);
        addMemento(memento, contest, judgement);
        return memento;
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Judgement judgement) {
        // <judgement>
        // <acronym>CE</acronym>
        // <name>Compile Error</name>
        // </judgement>
        String name = judgement.getDisplayName();

        String acronym = judgement.getAcronym();
        if (acronym == null) {
            acronym = "?";
        }
        XMLUtilities.addChild(memento, "acronym", acronym); 
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

        XMLUtilities.addChild(memento, "acronym", judgement.getAcronym());
        XMLUtilities.addChild(memento, "name", judgement.getDisplayName());
        return memento;
    }
    
    public XMLMemento createElement(IInternalContest contest, Notification notification) {

        XMLMemento memento = XMLMemento.createWriteRoot(NOTIFICATION_TAG);
        addMemento(memento, contest, notification);
        return memento;
    }

    public XMLMemento createElement(IInternalContest contest, BalloonDeliveryInfo balloonDeliveryInfo, int notificationSequenceNumber) {

        XMLMemento memento = XMLMemento.createWriteRoot(NOTIFICATION_TAG);
        Run run = getFirstSolvedRun (contest, balloonDeliveryInfo.getClientId(), balloonDeliveryInfo.getProblemId());
        
        addMemento(memento, contest, run, notificationSequenceNumber);
        return memento;
    }
}
