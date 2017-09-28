package edu.csus.ecs.pc2.services.core;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Event feed information in the CLICS JSON format.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
// TODO reformat code
// TODO for all sections pass in Key rather than hard coded inside method
public class EventFeedJSON implements IEventSequencer {

    public static final String TEAM_MEMBERS_KEY = "team-members";

    public static final String CLARIFICATIONS_KEY = "clarifications";

    public static final String GROUPS_KEY = "groups";

    public static final String JUDGEMENT_TYPE_KEY = "judgement-types";

    public static final String TEAM_KEY = "teams";

    public static final String SUBMISSION_KEY = "submissions";

    public static final String RUN_KEY = "runs";

    public static final String CONTEST_KEY = "contests";

    public static final String LANGUAGE_KEY = "languages";

    public static final String PROBLEM_KEY = "problems";

    public static final String JUDGEMENT_KEY = "judgements";

    /**
     * Event Id Sequence.
     * 
     * @see #getNextEventId()
     */
    private long eventIdSequence = 0;

    /**
     * ISO 8601 Date format for SimpleDateFormat.
     */
    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss z";

    /**
     * New Line (EOLN).
     */
    private static final String NL = System.getProperty("line.separator");
    
    private static final String JSON_EOLN = "," + NL;


    private SimpleDateFormat iso8601formatter = new SimpleDateFormat(ISO_8601_DATE_FORMAT);

    /**
     * Is the SE judgement penalized?
     * 
     */
    private boolean securityViolationApplyPenalty = true;

    /**
     * Is the CE judgement penalized?
     */
    private boolean compilationErrorApplyPenalty = true;

    public String getContestJSON(IInternalContest contest) {
        
        updatePenaltySettings(contest);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", CONTEST_KEY);
        stringBuilder.append(", ");

        ContestInformation info = contest.getContestInformation();
        ContestTime time = contest.getContestTime();

        String path = contest.getProfile().getProfilePath();
        String contestUUID = path;
        if (path.startsWith("profiles")){
            contestUUID = path.substring(9);
        }
        
        appendPair(stringBuilder, "id", contestUUID);
        stringBuilder.append(", ");

        String s = info.getContestShortName();
        if (s == null) {
            s = info.getContestTitle();
        }

        appendPair(stringBuilder, "name", s);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "formal_name", info.getContestTitle());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "start_time", info.getScheduledStartTime());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "duration", time.getContestLengthStr());
        stringBuilder.append(", ");

        String freezeTime = info.getFreezeTime();
        if (freezeTime != null) {
            appendPair(stringBuilder, "scoreboard_freeze_duration", info.getFreezeTime());
            stringBuilder.append(", ");
        }

        String penalty = DefaultScoringAlgorithm.getDefaultProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO);
        
        if (penalty != null && penalty.matches("[0-9]+")){
            appendPair(stringBuilder, "penalty_time", Integer.parseInt(penalty));
            stringBuilder.append(", ");
        }
        
        // Start states array

        stringBuilder.append("\"state\":{");

        appendPair(stringBuilder, "state.running", time.isContestRunning());
        stringBuilder.append(", ");

        boolean finalized = false;
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null && finalizeData.isCertified()) {
            finalized = true;
        }

        appendPair(stringBuilder, "state.frozen", isContestFrozen(contest));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "state.final", finalized);
        
        stringBuilder.append("}"); // end states array

        stringBuilder.append("},"+NL);

        return stringBuilder.toString();
    }

    private boolean isContestFrozen(IInternalContest contest) {

        // TODO technical deficit - code isContestFrozen.
        return false;
    }

    /**
     * List of judgements.
     * 
     */
    public String getJudgementTypeJSON(IInternalContest contest) {
        
        StringBuilder stringBuilder = new StringBuilder();

        Judgement[] judgements = contest.getJudgements();
        for (Judgement judgement : judgements) {
            stringBuilder.append(getJudgementTypeJSON(contest, judgement));
            stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();
    }

    private Object getJudgementTypeJSON(IInternalContest contest, Judgement judgement) {
        
        /**
         *      ID  yes     no  provided by CCS     identifier of the judgement type. Usable as a label, typically a 2-3 letter capitalized shorthand, see Problem Format
name    string  yes     no  provided by CCS     name of the judgement
penalty     boolean     depends     no  provided by CCS     whether this judgement causes penalty time; should be present if and only if contest:penalty_time is present
solved 
         */
        
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", JUDGEMENT_TYPE_KEY);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", judgement.getAcronym()); 
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", judgement.getDisplayName());
        stringBuilder.append(", ");
        
        boolean penalized = isPenalizedJudgement(contest, judgement);

        appendPair(stringBuilder, "penalty", penalized);
        stringBuilder.append(", ");
        
        boolean solved = isSolved(judgement);
        appendPair(stringBuilder, "solved", solved);

        stringBuilder.append("} ");

        return stringBuilder.toString();
    }

    private boolean isSolved(Judgement judgement) {
        return Judgement.ACRONYM_ACCEPTED.equals(judgement.getAcronym());
    }

    /**
     * Return int value for key.
     * @param key property to lookup
     * @param properties 
     */
    private int getPropIntValue(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value != null && value.length() > 0 && isAllDigits(value)){
            Integer i = Integer.parseInt(value);
            return i.intValue();
        } else {
            return 0;
        }
    }

    /**
     * Does the string only contain digits?  
     * 
     * @param value
     * @return
     */
    private boolean isAllDigits(String value) {
        return value != null && value.matches("[0-9]+");
    }

    /**
     * Is there a point penalty for this judgement?
     * @param contest
     * @param judgement
     * @return
     */
    private boolean isPenalizedJudgement(IInternalContest contest, Judgement judgement) {
        
        boolean usePenalty = true;
        
        if (isSolved(judgement)){
            return false;
        }
        
        if(Judgement.ACRONYM_COMPILATION_ERROR.equals(judgement.getAcronym())) {
            usePenalty = isCEPenalty();
        } else   if(Judgement.ACRONYM_SECURITY_VIOLATION.equals(judgement.getAcronym())) {
            usePenalty =  isSEPenalty();
        } // else - no elss fall through

        return usePenalty;
    }

    private boolean isSEPenalty() {
        return securityViolationApplyPenalty;
    }

    private boolean isCEPenalty() {
        return compilationErrorApplyPenalty;
    }
    
    private void updatePenaltySettings(IInternalContest contest) {

        Properties properties = contest.getContestInformation().getScoringProperties();

        if (properties != null) {
            securityViolationApplyPenalty = 0 != getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION);
            compilationErrorApplyPenalty = 0 != getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR);
        }
    }

    /**
     * Get all languages JSON.
     * 
     * @param contest
     * @return
     */
    public String getLanguageJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Language[] languages = contest.getLanguages();
        int id = 1;
        for (Language language : languages) {
            stringBuilder.append(getLanguageJSON(contest, language, id));
            stringBuilder.append(JSON_EOLN);
            id++;
        }

        return stringBuilder.toString();

    }

    /**
     * get JSON for a language.
     * 
     * @param contest
     * @param language
     * @param languageNumber sequence number
     * @return
     */
    public String getLanguageJSON(IInternalContest contest, Language language, int languageNumber) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", LANGUAGE_KEY);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", languageNumber);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", language.getDisplayName());

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public String getProblemJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Problem[] problems = contest.getProblems();
        int id = 1;
        for (Problem problem : problems) {
            stringBuilder.append(getProblemJSON(contest, problem, id));
            stringBuilder.append(JSON_EOLN);
            id++;
        }

        return stringBuilder.toString();
    }

    public String getProblemJSON(IInternalContest contest, Problem problem, int problemNumber) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", PROBLEM_KEY);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", problemNumber); 
        stringBuilder.append(", ");

        appendPair(stringBuilder, "label", problem.getLetter()); // letter
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", problem.getDisplayName());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "ordinal", problemNumber);
        stringBuilder.append(", ");
        
        String s = problem.getColorRGB();
        if ( s != null )
        {
            appendPair(stringBuilder, "rgb", s);
            stringBuilder.append(", ");
        }
        
        s = problem.getColorName();
        if ( s != null )
        {
            appendPair(stringBuilder, "color", s);
            stringBuilder.append(", ");
        }

        appendPair(stringBuilder, "test_data_coun", problem.getNumberTestCases());

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public String getGroupJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        Group[] groups = contest.getGroups();
        
        Arrays.sort(groups, new GroupComparator());
        for (Group group : groups) {
            stringBuilder.append(getGroupJSON(contest, group));
            stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();
    }

    private Object getGroupJSON(IInternalContest contest, Group group) {

        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ ");
        
        
        //    id 
        //    icpc_id 
        //    name 
        //    organization_id 
       
        appendPair(stringBuilder, "event", GROUPS_KEY);
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "id", getNextEventId()); 
        stringBuilder.append(", ");
       
//        id  ID  yes     no  provided by CCS     identifier of the group
//        icpc_id     string  no  yes     provided by CCS     external identifier from ICPC CMS
//        name    string  yes     no  provided by CCS     name of the group 
        
        
        appendPair(stringBuilder, "icpc_id", group.getGroupId());
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "name", group.getDisplayName());
        


        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public String getOrganizationJSON(IInternalContest contest) {
        
        //    Name    Type    Required?   Nullable?   @WF     Description
        //    id  ID  yes     no  provided by CCS     identifier of the organization
        //    icpc_id     string  no  yes     provided by CCS     external identifier from ICPC CMS
        //    name    string  yes     no  provided by CCS     display name of the organization
        //    formal_name     string  no  yes     provided by CCS     full organization name if too long for normal display purposes.
        //    country     string  no  yes     not used    ISO 3-letter code of the organization's country
        //    url     string  no  yes     provided by CDS     URL to organization's website
        //    twitter_hashtag     string  no  yes     provided by CDS     organization hashtag
        //    location    object  no  yes     provided by CDS     JSON object as specified below
        //    location.latitude   float   depends     no  provided by CDS     latitude. Required iff location is present.
        //    location.longitude  float   depends     no  provided by CDS     longitude. Required iff location is present. 

        return null; // TODO CLICS technical deficit code getOrganizationJSON
    }
    
    /**
     * Get all sites' teams.
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

    public String getTeamJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Account[] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            stringBuilder.append(getTeamJSON(contest, account));
            stringBuilder.append(JSON_EOLN);
        }
        
        return stringBuilder.toString();
    }
    
    public String getTeamJSON(IInternalContest contest, Account account) {
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ ");
        
        ClientId clientId = account.getClientId();
        
        //    id 
        //    icpc_id 
        //    name 
        //    organization_id 
       
        appendPair(stringBuilder, "event", TEAM_KEY);
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "id", clientId.getClientNumber()); 
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "icpc_id", account.getExternalId());
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "name", account.getDisplayName());
        stringBuilder.append(", ");
        
        appendPairNullValue(stringBuilder, "organization_id"); // TODO CLICS DATA ADD  technical deficit - add organizational id into account/model
        
        //    group_id 
        
        ElementId elementId = account.getGroupId();
        if (elementId != null){
            Group group = contest.getGroup(elementId);
            if (group != null) {
                stringBuilder.append(", ");
                appendPair(stringBuilder, "group_id", group.getGroupId());
            }
        }

        //    location   //     JSON object as specified below. 
        //    location.x 
        //    location.y 
        //    location.rotation 
        
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    /**
     * Get team member info.
     * 
     */
    public String getTeamMemberJSON(IInternalContest contest) {
        
        StringBuilder stringBuilder = new StringBuilder();

        Account[] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            String [] names = account.getMemberNames();
            
            if (names.length > 0){
                for (String teamMemberName : names) {
                    stringBuilder.append(getTeamMemberJSON(contest, account, teamMemberName));
                    stringBuilder.append(JSON_EOLN);    
                }
            }
        }
        
        return stringBuilder.toString();
    }

    private String getTeamMemberJSON(IInternalContest contest, Account account, String teamMemberName) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ ");

        //      Id   ID  yes     no  provided by CDS     identifier of the team-member.
        //      icpc_id     string  no  yes     provided by CDS     external identifier from ICPC CMS
        //      name    string  yes     no  provided by CDS     full name of team member.

        appendPair(stringBuilder, "event", TEAM_MEMBERS_KEY);
        stringBuilder.append(", ");

        appendPairNullValue(stringBuilder, "id"); // TODO CLICS DATA ADD  id needs to be added to new Account Member class and model
        stringBuilder.append(", ");

        appendPairNullValue(stringBuilder, "icpc_id"); // TODO CLICS DATA ADD  icpc_id needs to be added to new Account Member class
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", teamMemberName);
        stringBuilder.append(", ");

        //      sex     string  no  no  provided by CDS     one of male, female or other
        //      team_id     ID  yes     no  provided by CDS     team of team member.
        //      role    string  yes     no  provided by CDS     one of contestant or coach.

        appendPairNullValue(stringBuilder, "sex");
        stringBuilder.append(", ");

        appendPairNullValue(stringBuilder, "team_id");
        stringBuilder.append(", ");

        appendPairNullValue(stringBuilder, "role");

        stringBuilder.append("}");

        return stringBuilder.toString();

    }

    /**
     * Get run submission.
     * 
     * @param contest
     * @return
     */
    public String getSubmissionJSON(IInternalContest contest) {
        
        StringBuilder stringBuilder = new StringBuilder();
        Run[] runs = contest.getRuns();
        
        Arrays.sort(runs, new RunComparator());
        for (Run run : runs) {
            stringBuilder.append(getSubmissionJSON(contest, run));
               stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();   
        
    }
    

    private String getSubmissionJSON(IInternalContest contest, Run run) {
        
        //    id 
        //    language_id 
        //    problem_id 
        //    team_id 

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("{ ");

            appendPair(stringBuilder, "event", SUBMISSION_KEY);
            stringBuilder.append(", ");

            appendPair(stringBuilder, "id", getNextEventId()); 
            stringBuilder.append(", ");
            
            appendPair(stringBuilder, "language_id", getLanguageIndex(contest,  run.getLanguageId()));
            stringBuilder.append(", ");
            
            appendPair(stringBuilder, "problem_id", getProblemIndex(contest, run.getProblemId()));
            stringBuilder.append(", ");
            
            appendPair(stringBuilder, "team_id", run.getSubmitter().getClientNumber());
            stringBuilder.append(", ");
            
            //    time 
            //    contest_time 
            //    entry_point 
            
            Calendar wallElapsed = calculateElapsedWalltime (contest, run.getElapsedMS());
            
            appendPair(stringBuilder, "time", wallElapsed); 

            stringBuilder.append(", ");
            appendPair(stringBuilder, "contest_time",  XMLUtilities.formatSeconds(run.getElapsedMS()));
            
            stringBuilder.append(", ");
            appendPair(stringBuilder, "entry_point", "Main"); // TODO CLICS DATA ADD ADD  the team's submitted executable name is not available at this time.
            
            stringBuilder.append("}");

            return stringBuilder.toString();
    }

    /**
     * List of all runs' judgements..
     * 
     */
    public String getJudgementJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        Run[] runs = contest.getRuns();
        
        Arrays.sort(runs, new RunComparator());
        
        for (Run run : runs) {
            stringBuilder.append(getJudgementJSON(contest, run));
            stringBuilder.append(JSON_EOLN);
        }
        
        return stringBuilder.toString();
    }

    private String getJudgementJSON(IInternalContest contest, Run run) {
        
    //    id 
    //    submission_id 
    //    judgement_type_id 

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", JUDGEMENT_KEY);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", getNextEventId()); 
        stringBuilder.append(", ");

        appendPair(stringBuilder, "submission_id", run.getNumber());
        stringBuilder.append(", ");
        
        if (run.isJudged()){
            ElementId judgementId = run.getJudgementRecord().getJudgementId();
            Judgement judgement = contest.getJudgement(judgementId);
            
            appendPair(stringBuilder, "judgement_type_id", judgement.getAcronym());
        } else {
            
            appendPairNullValue(stringBuilder, "judgement_type_id");
        }

        
//        start_time  TIME    yes     no  provided by CCS     absolute time when judgement started
//        start_contest_time  RELTIME     yes     no  provided by CCS     contest relative time when judgement started
//        end_time    TIME    yes     yes     provided by CCS     absolute time when judgement completed
//        end_contest_time    RELTIME     yes     yes     provided by CCS     contest relative time when judgement completed 
        
//        [{"id":"189549","submission_id":"wf2017-32163123xz3132yy","judgement_type_id":"CE","start_time":"2014-06-25T11:22:48.427+01",
//            "start_contest_time":"1:22:48.427","end_time":"2014-06-25T11:23:32.481+01","end_contest_time":"1:23:32.481"},
//           {"id":"189550","submission_id":"wf2017-32163123xz3133ub","judgement_type_id":null,"start_time":"2014-06-25T11:24:03.921+01",
//            "start_contest_time":"1:24:03.921","end_time":null,"end_contest_time":null}
//          ]
        
        
        Calendar wallElapsed = calculateElapsedWalltime (contest, run.getElapsedMS());
        
        stringBuilder.append(", ");
        appendPair(stringBuilder, "start_time", wallElapsed); // absolute time when judgement started ex. 2014-06-25T11:24:03.921+01

        stringBuilder.append(", ");
        appendPair(stringBuilder, "start_contest_time",  XMLUtilities.formatSeconds(run.getElapsedMS())); // contest relative time when judgement started. ex. 1:24:03.921
        
        stringBuilder.append(", ");
        appendPairNullValue(stringBuilder, "end_time");  // TODO CLICS DATA ADD - add code to save in JudgementRecord in Executable
        
        stringBuilder.append(", ");
        appendPairNullValue(stringBuilder, "end_contest_time"); // TODO CLICS DATA ADD  add code to save in JudgementRecord - in Executable

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

 
//    private Calendar calculateElapsedWalltime(IInternalContest contest, Run run) {
//        
//        ContestTime time = contest.getContestTime();
//        if (time.getElapsedMins() > 0){
//            
//        Calendar contestStart = time.getContestStartTime();
//        
//        long ms = contestStart.getTimeInMillis();
//        
//        ms += run.getElapsedMS(); // add elapsed time
//        
//        // create wall time.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(ms);
//        return calendar;
//        
//        } else {
//            return null;
//        }
//        
//    }
    
    /**
     * Return wall time for input elapsed time in ms.
     * 
     * Calculates based on elapsed time plus contest start time
     * 
     * @param contest
     * @param elapsedMS - elapsed ms when submission submitted
     * @return wall time for run.
     */
    private Calendar calculateElapsedWalltime(IInternalContest contest, long elapsedMS) {
        
        ContestTime time = contest.getContestTime();
        if (time.getElapsedMins() > 0){
            
        Calendar contestStart = time.getContestStartTime();
        
        long ms = contestStart.getTimeInMillis();
        
        ms += elapsedMS; // add elapsed time
        
        // create wall time.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        return calendar;
        
        } else {
            return null;
        }
        
    }

    /**
     * Return test cases.
     * 
     * @param contest
     */
    public String getRunJSON(IInternalContest contest) {
        
        StringBuilder stringBuilder = new StringBuilder();
        Run[] runs = contest.getRuns();
        
        Arrays.sort(runs, new RunComparator());
        for (Run run : runs) {
            RunResultFiles files = null; // TODO CLICS must fetch files to get main file name, or put mainfile name into Run
            stringBuilder.append(getRunJSON(contest, run, files));
            stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();
    }
    

    private String getRunJSON(IInternalContest contest, Run run, RunResultFiles files) {
        
        //      id  ID  yes     no  provided by CCS     identifier of the run
        //      judgement_id    ID  yes     no  provided by CCS     identifier of the judgement this is part of
        //      ordinal     integer     yes     no  provided by CCS     ordering of runs in the judgement (implicit from the test cases)


        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", RUN_KEY);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", run.getNumber());
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "contest_time",  XMLUtilities.formatSeconds(run.getElapsedMS()));
        stringBuilder.append(", ");


        //      judgement_type_id   ID  yes     no  provided by CCS     the verdict of this judgement (i.e. a judgement type)
        //      time    TIME    yes     no  provided by CCS     absolute time when run completed
        //      contest_time    RELTIME     yes     no  provided by CCS     contest relative time when run completed 

        ElementId judgementId = run.getJudgementRecord().getJudgementId();
        Judgement judgement = contest.getJudgement(judgementId);
        
        appendPair(stringBuilder, "judgement_type_id", judgement.getAcronym());
        stringBuilder.append("}");

        return stringBuilder.toString();
    
        
    }

    /**
     * Clarification Answer.
     * @param contest
     * @return
     */
    public String getClarificationJSON(IInternalContest contest) {
        StringBuilder stringBuilder = new StringBuilder();
        Clarification[] clarifications = contest.getClarifications();
        
        Arrays.sort(clarifications, new ClarificationComparator());
        for (Clarification clarification : clarifications) {
            stringBuilder.append(getClarificationJSON(contest, clarification));
            stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();
    }

    private String getClarificationJSON(IInternalContest contest, Clarification clarification) {
        
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ ");
        
        
//        Id   ID  yes     no  provided by CCS     identifier of the clarification
//        from_team_id    ID  yes     yes     provided by CCS     identifier of team sending this clarification request, null if a clarification sent by jury
//        to_team_id  ID  yes     yes     provided by CCS     identifier of the team receiving this reply, null if a reply to all teams or a request sent by a team
//        reply_to_id     ID  yes     yes     provided by CCS     identifier of clarification this is in response to, otherwise null
   
       
        appendPair(stringBuilder, "event", CLARIFICATIONS_KEY);
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "id", clarification.getNumber()); 
        stringBuilder.append(", ");

        appendPair(stringBuilder, "from_team_id", clarification.getSubmitter().getClientNumber()); 
        stringBuilder.append(", ");

        if (clarification.isSendToAll()){
            appendPairNullValue(stringBuilder, "to_team_id");
        } else {
            appendPair(stringBuilder, "to_team_id", clarification. getSubmitter().getClientNumber());
        }
        
        stringBuilder.append(", ");
        
        if (clarification.isAnswered()){
            appendPair(stringBuilder, "reply_to_id", clarification.getNumber()); // this answer is in reply to
        } else {
            appendPairNullValue(stringBuilder, "reply_to_id");
        }
        stringBuilder.append(", ");

//        problem_id  ID  yes     yes     provided by CCS     identifier of associated problem, null if not associated to a problem
//        text    string  yes     no  provided by CCS     question or reply text
//        time    TIME    yes     no  provided by CCS     time of the question/reply
//        contest_time    RELTIME     yes     no  provided by CCS     contest time of the question/reply 

        stringBuilder.append("}");
        
        appendPair(stringBuilder, "problem_id", getProblemIndex(contest, clarification.getElementId()));
        stringBuilder.append(", ");
        
        // Due to a design mistake in the CCS team the clarification JSON element has either
        // an answer or a question.   There were two mistakes, first that there is on
        // JSoN element clarifications for both the question and the answer, there should
        // be two elements.   The second mistake is that an answer should have both
        // question and answer to avoid a bug by the consumer where they mismatch the
        // question and answer because those would be married on reply_to_id
        
        if (clarification.isAnswered()){
            // text is 
        }

        appendPair(stringBuilder, "text", clarification.getQuestion()); // TODO CLICS need to quote this string 
        stringBuilder.append(", ");

        Calendar wallElapsed = calculateElapsedWalltime (contest, clarification.getElapsedMS());
        
        stringBuilder.append(", ");
        appendPair(stringBuilder, "start_time", wallElapsed); // absolute time when judgement started ex. 2014-06-25T11:24:03.921+01

        stringBuilder.append(", ");
        appendPair(stringBuilder, "start_contest_time",  XMLUtilities.formatSeconds(clarification.getElapsedMS())); // contest relative time when judgement started. ex. 1:24:03.921
        
        stringBuilder.append("}");
        
        return stringBuilder.toString();
    }

    public String getAwardJSON(IInternalContest contest) {

//        [{"id":"gold-medal","citation":"Gold medal winner","team_ids":["54","23","1","45"]},
//         {"id":"first-to-solve-a","citation":"First to solve problem A","team_ids":["45"]},
//         {"id":"first-to-solve-b","citation":"First to solve problem B","team_ids":[]}
//        ]
                
        return null; // TODO CLICS - technical deficit code getAwardJSON
    }

    /**
     * Returns a JSON string listing the current contest event feed
     * 
     * @param contest - the current contest
     * @return a JSON string giving event feed in JSON
     * @throws IllegalContestState
     */
    public String createJSON(IInternalContest contest) throws IllegalContestState {

        if (contest == null) {
            return "[]";
        }

        //        Vector<Account> accountlist = contest.getAccounts(Type.TEAM);
        //        if (accountlist.size() == 0) {
        //            return "[]";
        //        }
        //        Account[] accounts = (Account[]) accountlist.toArray(new Account[accountlist.size()]);
        //
        //        Group[] groups = contest.getGroups();
        //        final Map<ElementId, String> groupMap = new HashMap<ElementId, String>();
        //        for (Group group : groups) {
        //            groupMap.put(group.getElementId(), group.getDisplayName());
        //        }
        StringBuffer buffer = new StringBuffer();

//        contest = new SampleContest().createStandardContest();

        String json = getContestJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getJudgementTypeJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getLanguageJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getProblemJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getGroupJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getOrganizationJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getTeamJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getTeamMemberJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getSubmissionJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getJudgementJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getRunJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getClarificationJSON(contest);
        if (json != null) {
            buffer.append(json);
        }
        json = getAwardJSON(contest);
        if (json != null) {
            buffer.append(json);
        }

        // At this point there is a trailing , and new line, if so remove it.

        if (buffer.length() > 6) {
            buffer.delete(buffer.length() - JSON_EOLN.length(), buffer.length());
        }
        
        // return the collected standings as elements of a JSON array
        return "[" + buffer.toString() + "]";
    }

    public static String join(String delimit, List<String> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    @Override
    public String getNextEventId() {
        eventIdSequence++;
        return "pc2-" + eventIdSequence;
    }

    // TODO technical deficit - move these methods
    // TODO move pair methods into JsonUtilities

    private void appendPair(StringBuilder stringBuilder, String name, boolean booleanValue) {
        
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");
        stringBuilder.append(":");

        stringBuilder.append(booleanValue);
    }

    private void appendPair(StringBuilder stringBuilder, String name, long value) {
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");
        stringBuilder.append(":");

        stringBuilder.append(value);
    }

    private void appendPair(StringBuilder stringBuilder, String name, String value) {
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");

        stringBuilder.append(":");

        stringBuilder.append("\"");
        stringBuilder.append(value);
        stringBuilder.append("\"");
    }

    private void appendPair(StringBuilder stringBuilder, String name, Calendar calendar) {

        if (calendar != null) {
            appendPair(stringBuilder, name, iso8601formatter.format(calendar.getTime()));
        } else {
            appendPairNullValue(stringBuilder, name);
        }
    }

    private void appendPairNullValue(StringBuilder stringBuilder, String name) {
        stringBuilder.append("\"");
        stringBuilder.append(name);
        stringBuilder.append("\"");

        stringBuilder.append(": null");
        
    }
    
    /**
     * Return the language index (starting as base one).
     * 
     * @param contest
     * @param elementId
     * @return -1 if language not found or inactive, else 1 or greater rank for language.
     */
    public int getLanguageIndex(IInternalContest contest, ElementId elementId) {
        int idx = 0;
        for (Language language : contest.getLanguages()) {
            if (language.isActive()) {
                if (language.getElementId().equals(elementId)) {
                    return idx + 1;
                }
                idx++;
            }
        }

        return -1;
    }
    
    /**
     * Return the problem index (starting at/base one)).
     * 
     * @param contest
     * @param elementId
     * @return -1 if problem not found or inactive, else 1 or greater as rank for problem.
     */
    public int getProblemIndex(IInternalContest contest, ElementId elementId) {
        int idx = 0;
        for (Problem problem : contest.getProblems()) {
                if (problem.getElementId().equals(elementId)) {
                    return idx + 1;
                }
                idx++;
        }

        return -1;
    }

}
