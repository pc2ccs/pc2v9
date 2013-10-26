package edu.csus.ecs.pc2.exports.ccs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.BalloonDeliveryComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.ProblemComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.list.RunTestCaseComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
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
import edu.csus.ecs.pc2.core.model.Notification;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IMemento;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Event Feed XML.
 * 
 * Class used <li>to CCS Standard Event Feed output XML based on contest data.
 * 
 * Mementos are the internal tags for a element, Elements are the XML Elements with
 * surrounding tag.
 * <P>
 * For example {@link #createElement(IInternalContest, Language, int)} will create
 * an element with a {@link #LANGUAGE_TAG} whereas the contents of the memento
 * {@link #addMemento(IMemento, IInternalContest, Language, int)}.
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

    public static final String TESTCASE_TAG = "testcase";

    public static final String RUN_TAG = "run";

    public static final String JUDGEMENT_TAG = "judgement";

    public static final String FINALIZE_TAG = "finalized";

    public static final String JUDGEMENT_RECORD_TAG = "judgement_record";

    public static final String BALLOON_TAG = "balloon";

    public static final String BALLOON_LIST_TAG = "balloons";

    public static final String NOTIFICATION_TAG = "notification";

    private RunComparator runComparator = new RunComparator();
    
    private  BalloonSettings colorSettings = null;

    private Log log = null;
    
    private static final String DEFAULT_ACRONYM = "??";

    private static final String DEFAULT_COLORS_FILENAME = "colors.txt";;
    
    private String[] acronymList = { //
            "No - Compilation Error;CE", //
            "No - Security Violation;SV", //
            "No - Time Limit Exceeded;TLE", //
            "No - Wrong Output;WA", //
            "Yes;AC", //
            "Accepted;AC", //
            "Wrong Answer;WA", //
    };

    private String colorsFilename = DEFAULT_COLORS_FILENAME;

    private String guessAcronym(String judgementText) {

        if (judgementText == null) {
            return DEFAULT_ACRONYM;
        }

        for (String line : acronymList) {

            String[] fields = line.split(";");
            String name = fields[0];
            String acro = fields[1];

            if (name.trim().toLowerCase().equals(judgementText.trim().toLowerCase())) {
                return acro;
            }
        }

        return DEFAULT_ACRONYM;
    }

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
        // only get XML elements for events before mins
        filter.setEndElapsedTime(mins);
        
        return toXML(contest, filter);
    }


    public String toXML(IInternalContest contest, Filter filter) {

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
                addMemento(memento, contest, run, false); // add RUN
                
                RunTestCase[] runTestCases = getLastJudgementTestCases(run);
                Arrays.sort(runTestCases, new RunTestCaseComparator());
                for (RunTestCase runTestCase : runTestCases) {
                    if (filter.matchesElapsedTime(runTestCase)){
                        memento = mementoRoot.createChild(TESTCASE_TAG);
                        addMemento(memento, contest, runTestCase, run); // add TESTCASE
                    }
                }
            } else if ( ! filter.matchesElapsedTime(run)) {
                /**
                 * This is for a frozen event feed.  We will send out
                 * run information without judgement information, essentially
                 * we sent out that the run is 'NEW'.
                 */
                addMemento(memento, contest, run, false); // add RUN
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
        
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null) {
            if (finalizeData.isCertified()) {
                memento = mementoRoot.createChild(FINALIZE_TAG);
                addMemento(memento, contest, finalizeData);
            }
        }

        return toXML(mementoRoot);
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
        filter.setFilteringDeleted(true);
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

        // TODO CCS code frozen restriction
        
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
    public XMLMemento createInfoElement(IInternalContest contest, Filter filter) {
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

        // <language>
        // <name>C++</name>
        // </language>

        memento.putInteger("id", id);
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

        // memento.putInteger("id", id);
        XMLUtilities.addChild(memento, "id", id);
        memento.putBoolean("enabled", problem.isActive());

        String problemLetter = getProblemLetter(id);
        memento.createChildNode("label", problemLetter);
        memento.createChildNode("name", problem.toString());

        String color =  getColor(contest, problem);
        if (color != null){
            IMemento balloonColor = memento.createChildNode("balloon-color", ""+color);
            String rgbColor = getColorRGB(contest, problem);
            balloonColor.putString("rgb", ""+rgbColor);
        }

        return memento;
    }

    
    private String getColorRGB(IInternalContest contest, Problem problem) {

        BalloonSettings settings = getColorSettings(contest);
        if (settings != null) {
            return settings.getColorRGB(problem);
        }
        return null;
    }

    protected BalloonSettings getColorSettings(IInternalContest contest) {

        try {

            if (colorSettings == null) {
                BalloonSettings[] list = contest.getBalloonSettings();
                if (list != null && list.length > 0) {
                    colorSettings = list[0];
                }
                if (colorSettings == null) {
                    BalloonSettings balloonSettings = readBalloonSettings(contest, getColorsFilename());
                    if (balloonSettings != null) {
                        colorSettings = balloonSettings;
                    }
                }
            }
            return colorSettings;
        } catch (Exception e) {
            // SOMEDAY log this to a static log
            e.printStackTrace();
        }

        return null;
    }

    public String getColorsFilename() {
        return colorsFilename;
    }
    
    public void setColorsFilename(String colorsFilename) {
        this.colorsFilename = colorsFilename;
    }

    private BalloonSettings readBalloonSettings(IInternalContest contest, String filename) {
        if (new File(filename).exists()){
            try {
                String [] lines = Utilities.loadFile(filename);
                return toBalloonSettings (contest, lines);
            } catch (Exception e) {
                if (Utilities.isDebugMode()){
                    e.printStackTrace(System.err);
                }
                return null;
            }
        }
        return null;
    }

    private BalloonSettings toBalloonSettings(IInternalContest contest, String[] lines) throws Exception {
        int site = 1;

        try {

            int found = 0;
            BalloonSettings settings = new BalloonSettings("BalloonSettings One", site);

            for (String line : lines) {
                // probname<tab>color<tab>RGB
                String[] fields = TabSeparatedValueParser.parseLine(line);

                String shortName = fields[0];
                Problem problem = getProblem(contest, shortName);

                if (problem != null && fields != null && fields.length > 1) {
                    // Found problem name and color
                    String colorName = fields[1];
                    String rgbColor = "";
                    if (fields.length > 2) {
                        // found rgb
                        rgbColor = fields[2];
                    }

                    settings.addColor(problem, colorName, rgbColor);
                    found++;
                }
            }
            if (found == 0) {
                return null;
            } else {
                return settings;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Problem getProblem(IInternalContest contest, String shortName) {
        for (Problem problem : contest.getProblems()) {
            if (shortName.equalsIgnoreCase(problem.getShortName())){
                return problem;
            }
        }
        return null;
    }

    private String getColor(IInternalContest contest, Problem problem) {
        BalloonSettings settings = getColorSettings(contest);
        if (settings != null) {
            return settings.getColor(problem);
        }
        return null;
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
        filter.setFilteringDeleted(true);

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
        XMLUtilities.addChild(memento, "id", notification.getNumber());

        memento.putInteger("team-id", clientId.getClientNumber());
        XMLUtilities.addChild(memento, "team-id", clientId.getClientNumber());
        

        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(notification.getElapsedMS()));
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(notification.getElapsedMS()));
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
//        memento.putInteger("team-id", clientId.getClientNumber());
        XMLUtilities.addChild(memento, "team-id", clientId.getClientNumber());

        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMins() * 1000));
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(run.getElapsedMins() * 1000));
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

//        memento.putInteger("id", account.getClientId().getClientNumber());
        XMLUtilities.addChild(memento, "id", account.getClientId().getClientNumber());
        memento.putInteger("id", account.getClientId().getClientNumber());
        memento.putString("external-id", account.getExternalId());

        XMLUtilities.addChild(memento, "name", account.getDisplayName());
        XMLUtilities.addChild(memento, "nationality", account.getCountryCode());
        XMLUtilities.addChild(memento, "university", account.getDisplayName());

        try {
            String regionName = "";
            if (account.getGroupId() != null) {
                Group group = contest.getGroup(account.getGroupId());
                regionName = group.getDisplayName();
            }
            
            XMLUtilities.addChild(memento, "region", regionName);
        } catch (Exception e) {
            System.out.println("Failed to lookup group for "+account+" group id = "+account.getGroupId());
            e.printStackTrace();
        }
        return memento;
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
//        <i>4</i>
//        <judged>True</judged>
//        <judgement_id>2</judgement_id>
//        <n>51</n>
        
        Problem problem = contest.getProblem(run.getProblemId());
        
        XMLUtilities.addChild(memento, "i", testCase.getTestNumber());
        XMLUtilities.addChild(memento, "judged", run.isJudged());
        XMLUtilities.addChild(memento, "judgement_id", testCase.getTestNumber());
        XMLUtilities.addChild(memento, "n", problem.getNumberTestCases());
        
//        <result>AC</result>
//        <run-id>2</run-id>
//        <solved>True</solved>
        
        XMLUtilities.addChild(memento, "solved", run.isSolved());
        
        // TODO CCS is result really the judgement acronym?
        String result = Judgement.ACRONYM_OTHER_CONTACT_STAFF;
        if (testCase.isSolved()){
            result = Judgement.ACRONYM_ACCEPTED;
        }
        XMLUtilities.addChild(memento, "result", result);
        XMLUtilities.addChild(memento, "run-id", run.getNumber());
        
        // TODO CCS is solve whether the run or the test case was solved?
        /**
         * There may be an implication that if there is no testcase output then
         * the test failed and all remaining test cases failed.
         */
  
//        <time>157.614985</time>
//        <timestamp>1337173290.16</timestamp>
//       </testcase>
//        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMins() * 1000));
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(run.getElapsedMS()));
        
        /**
         * Real time time stamp.
         * TODO is this the time stamp for the end of the judgement or the time stamp
         * for the wall clock?
         */
        XMLUtilities.addChild(memento, "timestamp", XMLUtilities.getTimeStamp());

        return memento;
    }  

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
        XMLUtilities.addChild(memento, "id", clarification.getNumber());

        memento.putInteger("team-id", clarification.getSubmitter().getClientNumber());

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
        XMLUtilities.addChild(memento, "time", XMLUtilities.formatSeconds(clarification.getElapsedMins() * 1000));
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

    /**
     * Create RUN XML element.
     * 
     * @param contest
     * @param run
     * @param suppressJudgement if true, do not output judgement information.
     * @return
     */
    public XMLMemento createElement(IInternalContest contest, Run run, boolean suppressJudgement) {
        XMLMemento memento = XMLMemento.createWriteRoot(RUN_TAG);
        addMemento(memento, contest, run, suppressJudgement);
        return memento;
    }

    /**
     * Add RUN element.
     * 
     * @param memento
     * @param contest
     * @param run
     * @param suppressJudgement if true, do not output judgement information.
     * @return
     */
    public IMemento addMemento(IMemento memento, IInternalContest contest, Run run, boolean suppressJudgement) {

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

        // 2013 "standard"
//        <run>
//        <id>1410</id>
//        <judged>True</judged>
//        <language>C++</language>
//        <penalty>True</penalty>
//        <problem>4</problem>
        
//        <result>WA</result>
//        <solved>False</solved>
//        <team>74</team>
//        <time>17960.749403</time>
//        <timestamp>1265353100.29</timestamp>
//        </run>
        
        
//        memento.putInteger("id", run.getNumber());
        XMLUtilities.addChild(memento, "id", run.getNumber());
        if (suppressJudgement) {
            XMLUtilities.addChild(memento, "judged", false);
        } else {
            XMLUtilities.addChild(memento, "judged", run.isJudged());
        }
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
//        memento.putInteger("problem", problemIndex);
        XMLUtilities.addChild(memento, "problem", problemIndex);
        
        XMLUtilities.addChild(memento, "team-id",  run.getSubmitter().getClientNumber());
        XMLUtilities.addChild(memento, "team",  run.getSubmitter().getClientNumber());
        
        memento.putInteger("team-id", run.getSubmitter().getClientNumber());

        if ((!suppressJudgement) && run.isJudged()) {
            ElementId judgementId = run.getJudgementRecord().getJudgementId();
            String acronym = contest.getJudgement(judgementId).getAcronym();
            if (acronym == null) {
                acronym = "?";
            }
            XMLUtilities.addChild(memento, "result", acronym);
        }
        
        XMLUtilities.addChild(memento, "solved", run.isSolved());

        XMLUtilities.addChild(memento, "team", run.getSubmitter().getClientNumber());
        XMLUtilities.addChild(memento, "elapsed-Mins", run.getElapsedMins());
//        XMLUtilities.addChild(memento, "contest-time", XMLUtilities.formatSeconds(run.getElapsedMS()));
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
            return mementoRoot.saveToString(true);
        } catch (IOException e) {
            logWarning("Error in creating XML", e);
            return "";
        }
    }
    
    public String createStartupXML(IInternalContest contest) {
        return createStartupXML(contest, new Filter());
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

        // currently only applies to runs
        filter.setFilteringDeleted(true);

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

        for (Judgement judgement : contest.getJudgements()) {
            sb.append(toXML(createElement(contest, judgement)));
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
                sb.append(toXML(createElement(contest, run, false))); // add RUN
                
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
                sb.append(toXML(createElement(contest, run, false))); // add RUN
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
        XMLUtilities.addChild(memento, "external-did", group.getGroupId());

        XMLUtilities.addChild(memento, "name", group.getDisplayName());
    }

    public XMLMemento createElement(IInternalContest contest, Judgement judgement) {
        XMLMemento memento = XMLMemento.createWriteRoot(JUDGEMENT_TAG);
        addMemento(memento, contest, judgement);
        return memento;
    }
    
    private String guestAcronym(Judgement judgement) {
        if (judgement.getAcronym() == null || judgement.getAcronym().length() == 0) {
            return guessAcronym(judgement.getDisplayName());
        } else {
            return judgement.getAcronym();
        }
    }

    public IMemento addMemento(IMemento memento, IInternalContest contest, Judgement judgement) {
        // <judgement>
        // <acronym>CE</acronym>
        // <name>Compile Error</name>
        // </judgement>
        String name = judgement.getDisplayName();

        String acronym = guestAcronym(judgement);
        
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

        XMLUtilities.addChild(memento, "acronym", guestAcronym(judgement));
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

    private void logWarning(String message, Exception e) {
        log.log(Log.WARNING, message, e);
    }

    public void setLog(Log log) {
        this.log = log;
    }
}
