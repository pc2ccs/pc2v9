// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSContests;
import edu.csus.ecs.pc2.core.imports.clics.CLICSEventType;
import edu.csus.ecs.pc2.core.imports.clics.CLICSLanguage;
import edu.csus.ecs.pc2.core.imports.clics.CLICSProblem;
import edu.csus.ecs.pc2.core.imports.clics.EventFeed;
import edu.csus.ecs.pc2.core.imports.clics.JudgementType;
import edu.csus.ecs.pc2.core.imports.clics.TeamAccount;
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
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.shadow.IRemoteContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.MockContestAPIAdapter;
import edu.csus.ecs.pc2.shadow.RemoteContestAPIAdapter;

/**
 * Model that compares two contest models.
 *
 */
public class ContestCompareModel {

    private IRemoteContestAPIAdapter remoteContestAPIAdapter = null;

    private ObjectMapper mapperField = new ObjectMapper();

    /**
     * Event Feed Judgements
     */
    private List<JudgementType> feedJudgements = new ArrayList<JudgementType>();

    private List<CLICSProblem> feedProblems = new ArrayList<CLICSProblem>();

    private List<CLICSLanguage> feedLanguages = new ArrayList<CLICSLanguage>();

    private List<TeamAccount> feedTeams = new ArrayList<TeamAccount>();

    private IInternalContest contest = new InternalContest();

    /**
     * Event Feed Contests
     */
    private CLICSContests clicsContests = new CLICSContests();

    /**
     * List of comparsisong records (comparing contest model to event feed model)
     */
    List<ContestCompareRecord> compRecs = new ArrayList<ContestCompareRecord>();

    @SuppressWarnings("unused")
    private ContestCompareModel() {
        // must use parameterized constructor
    }

    /**
     * 
     * @param contest
     *            pc2 model
     * @param eventFeedJSON
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public ContestCompareModel(IInternalContest contest, String nldjson) throws JsonParseException, JsonMappingException, IOException {
        this.contest = contest;
        loadRemoteContest(nldjson);
    }

    public ContestCompareModel(IInternalContest contest, String[] eventFeedJSONLines) throws JsonParseException, JsonMappingException, IOException {
        this.contest = contest;
        loadRemoteContest(eventFeedJSONLines);
    }

    /**
     * Load contest info.
     * 
     * @param eventFeedJSON
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    private void loadRemoteContest(String ndjson) throws JsonParseException, JsonMappingException, IOException {
        String[] lines = ndjson.split("\\n");
        loadRemoteContest(lines);

    }

    /**
     * Load event feed lines into local classes
     * 
     * @param lines
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    protected void loadRemoteContest(String[] lines) throws JsonParseException, JsonMappingException, IOException {

        for (String event : lines) {

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

                if (CLICSEventType.LANGUAGES.toString().equals(eventType)) {
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

                    TeamAccount team = mapper.convertValue(eventFeedEntry.getData(), TeamAccount.class);
                    feedTeams.add(team);

                } else if (CLICSEventType.CONTESTS.toString().equals(eventType)) {
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
     * Load ContestCompareRecord list.
     */
    private void loadComparisons() {

        loadJudgementComparisons();

        loadLanguageComparsions();

        loadProblemComparisons();

        loadTeamComparisons();

    }

    public static Account[] getAccounts(IInternalContest contest, ClientType.Type type) {
        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    private void loadTeamComparisons() {

        Account[] accounts = getAccounts(contest, Type.TEAM);
        String eventType = CLICSEventType.TEAMS.toString();

        for (Account account : accounts) {

            int id = account.getClientId().getClientNumber();
            String idStr = Integer.toString(id);

            if (isActiveTeamAccount(account)) {

                boolean found = false;
                for (TeamAccount teamAccount : feedTeams) {

                    if (id == teamAccount.getId()) {

                        ContestCompareRecord rec = new ContestCompareRecord(eventType, idStr, "name", account.getDisplayName(), teamAccount.getName());
                        compRecs.add(rec);

                        rec = new ContestCompareRecord(eventType, idStr, "icpc_id", account.getExternalId(), teamAccount.getIcpc_id());
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

        for (TeamAccount teamAccount : feedTeams) {

            int id = teamAccount.getId();
            String idStr = Integer.toString(id);
            boolean found = false;
            for (Account account : accounts) {

                if (account.getClientId().getClientNumber() == id) {
                    found = true;
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, idStr, "name", teamAccount.getName(), null);
                compRecs.add(rec);

                rec = new ContestCompareRecord(eventType, idStr, "icpc_id", teamAccount.getIcpc_id(), null);
                compRecs.add(rec);
            }
        }

    }

    private void loadProblemComparisons() {

        String eventType = CLICSEventType.PROBLEMS.toString();

        Problem[] problems = contest.getProblems();

        for (Problem problem : problems) {

            String id = problem.getShortName();

            boolean found = false;

            for (CLICSProblem clicsProblem : feedProblems) {

                if (problem.getDisplayName().equals(clicsProblem.getName())) {

                    ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", problem.getDisplayName(), clicsProblem.getName());
                    compRecs.add(rec);
                    found = true;
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", problem.getDisplayName(), null);
                compRecs.add(rec);
            }
        }

        for (CLICSProblem clicsProblem : feedProblems) {
            String id = clicsProblem.getId();

            boolean found = false;
            for (Problem problem : problems) {
                if (problem.getDisplayName().equals(clicsProblem.getName())) {
                    found = true;
                }
            }

            if (!found) {
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", null, clicsProblem.getName());
                compRecs.add(rec);
            }
        }

    }

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
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", judgement.getDisplayName(), null);
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
                ContestCompareRecord rec = new ContestCompareRecord(eventType, id, "name", null, judgementType.getName());
                compRecs.add(rec);
            }
        }
    }

    public ObjectMapper getMapper() {
        if (mapperField != null) {
            return mapperField;
        }

        mapperField = new ObjectMapper();
        mapperField.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapperField;
    }

    /**
     * Return true if after configuration events for event feed.
     * 
     * @param eventType
     * @return true if past configuration events
     */
    private boolean isAfterConfig(String eventType) {
        return CLICSEventType.SUBMISSIONS.toString().equals(eventType);
    }

    /**
     * Get Static log instance.
     * 
     * @return
     */
    private Log getLog() {
        return StaticLog.getLog();
    }

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
        InputStream inputStream = remoteContestAPIAdapter.getRemoteEventFeedInputStream();
        String eventFeedJSON = fetchEVentFeed(inputStream);
        loadRemoteContest(eventFeedJSON);
    }

    /**
     * Load/fetch event feed string from input stream.
     * 
     * @param inputStream
     * @return
     */
    private String fetchEVentFeed(InputStream inputStream) {

        return null;
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
     * Total number of different values comararing contest/model to event feed model.
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

        if (!getContestTitle().equals(getEventFedContestTitle())) {
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