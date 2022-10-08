// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSContests;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.imports.clics.CLICSLanguage;
import edu.csus.ecs.pc2.core.imports.clics.CLICSProblem;
import edu.csus.ecs.pc2.core.imports.clics.EventFeed;
import edu.csus.ecs.pc2.core.imports.clics.JudgementType;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.standings.json.Team;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.MockContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.RemoteContestAPIAdapter;

/**
 * Creates comparison and creates comparison lists for two contest models.
 *
 */
public class ContestCompareModel {

    private static final int EVENT_FEED_TIME_OUT_SECONDS = 10;
    
    private static int feedTimeout = EVENT_FEED_TIME_OUT_SECONDS;

    private IRemoteContestAPIAdapter remoteContestAPIAdapter = null;

    private ObjectMapper mapperField = null;

    /**
     * Event Feed Judgements
     */
    private List<JudgementType> feedJudgements = new ArrayList<JudgementType>();

    private List<CLICSProblem> feedProblems = new ArrayList<CLICSProblem>();

    private List<CLICSLanguage> feedLanguages = new ArrayList<CLICSLanguage>();

    private List<Team> feedTeams = new ArrayList<Team>();

    private IInternalContest contest = new InternalContest();

    /**
     * Event Feed Contests
     */
    private CLICSContests clicsContests = new CLICSContests();

    /**
     * List of comparison records (comparing contest model to event feed model)
     */
    List<ContestCompareRecord> compRecs = new ArrayList<ContestCompareRecord>();

    @SuppressWarnings("unused")
    private ContestCompareModel() {
        // must use parameterized constructor
    }

    /**
     * Create comparison based on JSON String
     * 
     * @param contest
     * @param nldjson  new-line delimited event feed JSON 
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public ContestCompareModel(IInternalContest contest, String nldjson) throws JsonParseException, JsonMappingException, IOException {
        this.contest = contest;
        createComparison(nldjson);
    }

    /**
     * Create comparison based on JSON lines.
     * 
     * @param contest
     * @param eventFeedJSONLines event feed JSON lines.
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public ContestCompareModel(IInternalContest contest, String[] eventFeedJSONLines) throws JsonParseException, JsonMappingException, IOException {
        this.contest = contest;
        createComparison(eventFeedJSONLines);
    }

    /**
     * Load contest info.
     * 
     * @param eventFeedJSON
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private void createComparison(String ndjson) throws JsonParseException, JsonMappingException, IOException {
        String[] lines = ndjson.split("\\n");
        createComparison(lines);

    }

    /**
     * Load event feed lines into local classes
     * 
     * @param lines
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    protected void createComparison(String[] lines) throws JsonParseException, JsonMappingException, IOException {

        
//        String filename = "stuf-event-feed."+new Date().getTime()+".json";
//        FileUtilities.writeFileContents(filename, lines);
//        System.out.println("write event feed to "+filename);
        
        int eventCount = 0;
        
        for (String event : lines) {
            
            eventCount ++;

            if (event.length() == 0) {
                // EOF
                break;
            }

            Map<String, Object> eventMap = JSONUtilities.getMap(event);
            ObjectMapper mapper = getMapper();

            if (eventMap == null) {
                // could not parse event
                getLog().log(Level.WARNING, "Could not parse event: " + event);
                if (Utilities.isDebugMode()) {
                    System.out.println("Could not parse event: " + event);
                }

            } else {

                EventFeed eventFeedEntry = (EventFeed) mapper.readValue(event, EventFeed.class);
                // find event type
                String eventType = eventFeedEntry.getType();

//                if (isAfterConfig(eventType)) {
//                    break;
//                }

//                System.out.println(" event "+eventType+" "+eventFeedEntry.getData());

                if (eventFeedEntry.getData() == null) {
                    
                    // SOMEDAY handle delete or whatver op has null data
                    // log for now.
                    getLog().log(Level.FINE, "Line: "+eventCount+ " data:null in event line '"+event+"'");
                    
                }
                else if (CLICSEventType.LANGUAGES.toString().equals(eventType)) {
                    // languages

                    // languages data = {name=C, id=c}

                    CLICSLanguage language = mapper.convertValue(eventFeedEntry.getData(), CLICSLanguage.class);

                    feedLanguages.add(language);

                } else if (CLICSEventType.JUDGEMENT_TYPES.toString().equals(eventType)) {
                    // judgement-types

                    // {"type": "judgement-types", "id": "a17b7d9d-43fc-43ed-bfd1-ef7fccbc6feb", "op": "create", "data": {"id": "RTE", "name": "Run Time Error", "solved": false, "penalty": true}}

                    JudgementType judgementType = mapper.convertValue(eventFeedEntry.getData(), JudgementType.class);
                    feedJudgements.add(judgementType);

                } else if (CLICSEventType.PROBLEMS.toString().equals(eventType)) {
                    // problems

                    // {time_limit=3, color=null, name=Particle Swapping, id=particles, label=F, test_data_count=9, rgb=null, ordinal=6}

                    CLICSProblem problem = mapper.convertValue(eventFeedEntry.getData(), CLICSProblem.class);

                    // System.out.println("type " + eventType + " data = " + eventFeedEntry.getData().toString());

                    feedProblems.add(problem);
                } else if (CLICSEventType.TEAMS.toString().equals(eventType)) {
                    // teams

                    // {"type": "teams", "id": "289de609-b1a0-40d8-a9e0-54a30e752e69", "op": "create", "data": {"id": "169347", "name": "Colin Peppler", "icpc_id": null, "group_ids": [],
                    // "display_name": "Virginia Tech", "organization_id": "vt_edu"}}

                    Team team = mapper.convertValue(eventFeedEntry.getData(), Team.class);
                    feedTeams.add(team);

                } else if (CLICSEventType.CONTEST.toString().equals(eventType)) {
                    // contests

                    // type contests data = {duration=5:00:00.000, start_time=2022-05-21T18:00:00.000+00, scoreboard_freeze_duration=1:00:00.000, name=nac22practice5, id=nac22practice5,
                    // formal_name=null, penalty_time=20}

                    clicsContests = mapper.convertValue(eventFeedEntry.getData(), CLICSContests.class);
                    

//                     System.out.println("type " + eventType + " data = " + eventFeedEntry.getData().toString());

                }
            }
        }

        loadComparisons();

    }

    /**
     * Compare model and event feed/remote contest model, load comparison records.
     */
    private void loadComparisons() {
        
        loadContestComparisions();

        loadJudgementComparisons();

        loadLanguageComparsions();

        loadProblemComparisons();

        loadTeamComparisons();

    }

    /**
     * Load Contest information comparison.
     */
    private void loadContestComparisions() {
        
        String eventType = CLICSEventType.CONTEST.toString();
        
        String contestTitle = contest.getContestInformation().getContestTitle();
        
        if (StringUtilities.isEmpty(clicsContests.getFormal_name())) {
            // no EF contests event
            ContestCompareRecord rec = new ContestCompareRecord(eventType, clicsContests.getId(), "formal_name", contestTitle, null);
            compRecs.add(rec);
        } else {
            ContestCompareRecord rec = new ContestCompareRecord(eventType, clicsContests.getId(), "formal_name", contestTitle, clicsContests.getFormal_name());
            compRecs.add(rec);
        }
    }

    public static Account[] getAccounts(IInternalContest contest, ClientType.Type type) {
        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Load comparison records for team accounts.
     */
    private void loadTeamComparisons() {

        Account[] accounts = getAccounts(contest, Type.TEAM);
        String eventType = CLICSEventType.TEAMS.toString();

        for (Account account : accounts) {
            
            int id = account.getClientId().getClientNumber();
            String idStr = Integer.toString(id);

            if (isActiveTeamAccount(account)) {

                boolean found = false;
                for (Team team: feedTeams) {

                    if (idStr.equals(team.getId())) {

                        ContestCompareRecord rec = new ContestCompareRecord(eventType, idStr, "display_name", account.getDisplayName(), team.getName());
                        compRecs.add(rec);

                        rec = new ContestCompareRecord(eventType, idStr, "icpc_id", account.getExternalId(), team.getIcpc_id());
                        compRecs.add(rec);

                        found = true;
                    }
                }

                if (!found) {
                    ContestCompareRecord rec = new ContestCompareRecord(eventType, idStr, "name", account.getDisplayName(), null);
                    compRecs.add(rec);
                }

            }
        }

        for (Team team : feedTeams) {

            String idStr = team.getId();
            boolean found = false;
            for (Account account : accounts) {

                if (isActiveTeamAccount(account)) {
                    String accountIdString = Integer.toString(account.getClientId().getClientNumber());
                    if (idStr.equals(accountIdString)) {
                        found = true;
                    }
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, idStr, "name", team.getName(), null);
                compRecs.add(rec);

                rec = new ContestCompareRecord(eventType, idStr, "icpc_id", team.getIcpc_id(), null);
                compRecs.add(rec);
            }
        }
    }

    /**
     * Load comparison records for problems.
     */
    private void loadProblemComparisons() {

        String eventType = CLICSEventType.PROBLEMS.toString();

        Problem[] problems = contest.getProblems();

        for (Problem problem : problems) {

            String id = problem.getShortName();
            boolean found = false;

            for (CLICSProblem clicsProblem : feedProblems) {

                if (id.contentEquals(clicsProblem.getId())) {
                    ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", problem.getDisplayName(), clicsProblem.getName());
                    compRecs.add(rec);
                    found = true;
                }
            }

            if (!found) {

                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "id", id, null);
                compRecs.add(rec);
                rec = new ContestCompareRecord(eventType, id, "name", problem.getDisplayName(), null);
                compRecs.add(rec);
            }
        }

        for (CLICSProblem clicsProblem : feedProblems) {
            String id = clicsProblem.getId();

            boolean found = false;
            for (Problem problem : problems) {
                if (id.equals(problem.getShortName())) {
                    found = true;
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "id", null, clicsProblem.getId());
                compRecs.add(rec);
                rec = new ContestCompareRecord(eventType, id, "name", null, clicsProblem.getName());
                compRecs.add(rec);
            }
        }

    }

    /**
     * Load comparison records for languages
     */
    private void loadLanguageComparsions() {

        Language[] languages = contest.getLanguages();

        String eventType = CLICSEventType.LANGUAGES.toString();

        for (Language language : languages) {

            String id = language.getID();

            boolean found = false;
            
            for (CLICSLanguage clicsLanguage : feedLanguages) {

                if (language.getDisplayName().equals(clicsLanguage.getName())) {

                    ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", language.getDisplayName(), clicsLanguage.getName());
                    compRecs.add(rec);
                    found = true;
                    
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", language.getDisplayName(), null);
                compRecs.add(rec);
            }
        }

        for (CLICSLanguage clicsLanguage : feedLanguages) {
            String id = clicsLanguage.getId();
            
            boolean found = false;
            for (Language language : languages) {
                if (language.getDisplayName().equals(clicsLanguage.getName())) {
                    found = true;
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", null, clicsLanguage.getName());
                compRecs.add(rec);
            }
        }

    }

    /**
     * Load comparison records for judgemnts.
     */
    private void loadJudgementComparisons() {

        Judgement[] judgemnts = contest.getJudgements();

        String eventType = CLICSEventType.JUDGEMENT_TYPES.toString();

        for (Judgement judgement : judgemnts) {

            String id = judgement.getAcronym();

            boolean found = false;
            for (JudgementType judgementType : feedJudgements) {
                if (judgement.getAcronym().equals(judgementType.getId())) {

                    ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", judgement.getDisplayName(), judgementType.getName());
                    compRecs.add(rec);
                    found = true;
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "id", judgement.getAcronym(), null);
                compRecs.add(rec);
                 rec = new ContestCompareRecord(eventType, id, "name", judgement.getDisplayName(), null);
                compRecs.add(rec);
            }
        }

        // Compare event feed data
        for (JudgementType judgementType : feedJudgements) {
            String id = judgementType.getId();

            boolean found = false;
            for (Judgement judgement : judgemnts) {
                if (judgement.getAcronym().equals(judgementType.getId())) {
                    found = true; // this comparison done above
                }

            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "id", null, judgementType.getId());
                compRecs.add(rec);
                 rec = new ContestCompareRecord(eventType, id, "name", null, judgementType.getName());
                compRecs.add(rec);
            }
        }
    }

    /**
     * Get an object mapper that ignores unknown properties.
     * 
     * @return an object mapper that ignores unknown properties
     */
    public ObjectMapper getMapper() {
        if (mapperField != null) {
            return mapperField;
        }

        mapperField = new ObjectMapper();
        mapperField.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapperField;
    }

    /**
     * Get Static log instance.
     * 
     * This provides a log instance without requiring Log to be passed to each method.
     * 
     * @return
     */
    private static Log getLog() {
        return StaticLog.getLog();
    }

    /**
     * Load model from primary server, using model's shadow properties.
     * 
     * @param contest
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public ContestCompareModel(IInternalContest contest) throws JsonParseException, JsonMappingException, IOException {
        this(contest, new URL(contest.getContestInformation().getPrimaryCCS_URL()), contest.getContestInformation().getPrimaryCCS_user_login(), //
                contest.getContestInformation().getPrimaryCCS_user_pw());
    }

    /**
     * Load model from primary server.
     * 
     * @param contest
     * @param url
     *            CCS API URL
     * @param login
     *            CCS API Login
     * @param password
     *            CCS API URL password
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public ContestCompareModel(IInternalContest contest, URL url, String login, String password) throws JsonParseException, JsonMappingException, IOException {

        if (url == null) {
            throw new IllegalArgumentException("URL is null/missing");
        }
        if (login == null || login.trim().length() == 0) {
            throw new IllegalArgumentException("login is null/missing");
        }
        if (password == null || password.trim().length() == 0) {
            throw new IllegalArgumentException("password is null/missing");
        }

        remoteContestAPIAdapter = createRemoteContestAPIAdapter(url, login, password);
        
        getLog().log(Level.INFO, "Opening event feed for '"+url+" login="+login);
        InputStream inputStream = remoteContestAPIAdapter.getRemoteEventFeedInputStream();
        String eventFeedJSON = fetchEVentFeed(inputStream);
        createComparison(eventFeedJSON);
    }

    /**
     * Load/fetch event feed string from event feed input stream.
     * 
     * @param inputStream
     * @return
     */
    public static List<String> fetchEVentFeedAsList(InputStream remoteInputStream) {

        List<String> list = new ArrayList<String>();

        String event = "null event";
        
        /**
         * Event line number.
         */
        int eventCount = 0;
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(remoteInputStream));
            
            eventCount ++;
            event = reader.readLine();
            
            long timeoutSeconds = new Date().getTime() + (Constants.MS_PER_SECOND * feedTimeout);
            
            // process the next event
            while ((event != null) && timeoutSeconds > new Date().getTime()) {
//                System.out.println("debug 22 #" + eventCount + " " + new Date() + " event '" + event+"'");
                list.add(event);
                eventCount ++;
                event = reader.readLine();
                
            }
        } catch (Exception e) {
            System.out.println("Error while reading event feed API, event line number "+eventCount);
            e.printStackTrace();
            getLog().log(Level.WARNING, "Error while reading event feed API, event line number ", e);
        }
        
        System.out.println("debug 22 Laat Event read #" +eventCount+" "+event);
        getLog().log(Level.INFO, "Laat Event read #" +eventCount+" "+event);

        return list;
    }
    
    public static String fetchEVentFeed(InputStream remoteInputStream) {
        List<String> lines = fetchEVentFeedAsList(remoteInputStream);
        return String.join("\n", lines);
    }

    /**
     * create API Adapter.
     * 
     * @param url
     *            CCS API URL
     * @param login
     *            CCS API Login
     * @param password
     *            CCS API URL password
     */
    protected IRemoteContestAPIAdapter createRemoteContestAPIAdapter(URL url, String login, String password) {

        boolean useMockAdapter = StringUtilities.getBooleanValue(IniFile.getValue("shadow.usemockcontestadapter"), false);
        if (useMockAdapter) {
            return new MockContestAPIAdapter(url, login, password);
        } else {
            return new RemoteContestAPIAdapter(url, login, password);
        }
    }

    /**
     * Total number of different values comparing contest/model to event feed model.
     * @return
     */
    int numberDifferences() {
        return getNonMatchingComparisonRecords().size();
    }

    /**
     * Do models match ?
     * 
     * @return true if models match
     */
    public boolean isMatch() {

        if (feedTeams.size() != getActiveCount(contest.getAccounts(Type.TEAM)) || //
                feedJudgements.size() != contest.getJudgements().length || //
                feedLanguages.size() != contest.getLanguages().length || //
                feedProblems.size() != contest.getProblems().length) {
            return false;
        }

        if (0 != getNonMatchingComparisonRecords().size()) {
            return false;
        }

        return true;
    }

    /**
     * Return number of active, display on scoreboard, accounts.
     * 
     * @param accounts
     * @return
     */
    private int getActiveCount(Vector<Account> accounts) {

        int count = 0;
        for (Account account : accounts) {
            if (isActiveTeamAccount(account)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Is this account "active" shown on the scoreboard?
     * 
     * @param account
     * @return
     */
    private boolean isActiveTeamAccount(Account account) {
        return account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD);
    }

    public String getContestTitle() {
        return contest.getContestInformation().getContestTitle();
    }

    public String getEventFedContestTitle() {
        return clicsContests.getName();
//        return clicsContests.getId(); // // TODO i 536 is this the full title  
    }

    public List<ContestCompareRecord> getContestCompareRecords() {
        return compRecs;
    }

    public List<ContestCompareRecord> getComparisonRecords() {
        return compRecs;
    }

    /**
     * Get list of records that match event type
     * 
     * @param type
     *            event type
     * @return list of records for event type
     */
    public List<ContestCompareRecord> getComparisonRecords(CLICSEventType type) {
        List<ContestCompareRecord> list = new ArrayList<ContestCompareRecord>();
        for (ContestCompareRecord contestCompareRecord : compRecs) {
            if (contestCompareRecord.getEventType().contentEquals(type.toString())) {
                list.add(contestCompareRecord);
            }
        }
        return list;
    }

    public String compareSummary(String prefixString, CLICSEventType type, List<ContestCompareRecord> records) {

        if (records.size() == 0) {
            return "NO " + prefixString + " found to compare";
        }

        int identicalCount = 0;
        int missTarget = 0;
        int missSource = 0;
        int diffValueCount = 0;

        for (ContestCompareRecord contestCompareRecord : records) {

            switch (contestCompareRecord.getState()) {
                case SAME:
                    identicalCount++;
                    break;
                case NOT_SAME:
                    diffValueCount++;
                    break;
                case MISSING_SOURCE:
                    missSource++;
                    break;
                case MISSING_TARGET:
                    missTarget++;
                    break;
                default:
                    // ignored
                    break;
            }
        }

        String message;

        if (identicalCount == records.size()) {
            message = prefixString + ": " + "All (" + identicalCount + ") " + prefixString + " records match";
        } else {

            List<ContestCompareRecord> nonMatchingList = getNonMatchingComparisonRecords(type);
            int totalDifferences = nonMatchingList.size();
            
            message = prefixString + ": " + totalDifferences + //
                    " total diffs, " + identicalCount + " values match, ";
            if (diffValueCount > 0) {
                message = message + diffValueCount + " values do not match, ";
            }
            if (missSource > 0) {
                message = message + missSource + " contest values missing ";
            }
            if (missTarget > 0) {
                message = message + missTarget + " event feed values missing ";
            }
        }

        return message;
    }

    /**
     * Get records for rows that are not identical/SAME for event type
     * 
     * @param type
     *            event feed type, null means match all.
     */
    public List<ContestCompareRecord> getNonMatchingComparisonRecords(CLICSEventType type) {
        List<ContestCompareRecord> list = new ArrayList<ContestCompareRecord>();

        for (ContestCompareRecord contestCompareRecord : compRecs) {
            if (type == null || contestCompareRecord.getEventType().equals(type.toString())) {
                if (!contestCompareRecord.getState().equals(ComparisonState.SAME)) {
                    list.add(contestCompareRecord);
                }
            }
        }
        return list;
    }

    /**
     * Get records for rows that are not identical/SAME for all event types
     */
    public List<ContestCompareRecord> getNonMatchingComparisonRecords() {
        return getNonMatchingComparisonRecords(null);
    }


}