package edu.csus.ecs.pc2.services.core;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.web.EventFeedFilter;

/**
 * Event feed information in the CLICS JSON format.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
// TODO for all sections pass in Key rather than hard coded inside method
public class EventFeedJSON extends JSONUtilities {
    /**
     * 
     */
    public EventFeedJSON(IInternalContest contest) {
        super();
        jsonTool = new JSONTool(contest, null);
    }

    private AwardJSON awardJSON = new AwardJSON();

    private TeamMemberJSON teamMemberJSON = new TeamMemberJSON();

    /**
     * Event Id Sequence.
     * 
     * @see #nextEventId()
     */
    protected long eventIdSequence = 0;

    /**
     * Start event id.
     * 
     * /event-feed?type=<event_list>
     */
    private String startEventId = null;

    /**
     * List of events to output.
     * 
     */
    private String eventTypeList = null;

    private JSONTool jsonTool;

    public String getContestJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        appendJSONEvent(stringBuilder, CONTEST_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getContestJSONFields(contest));
        stringBuilder.append(NL);
        return stringBuilder.toString();

    }

    public String getContestJSONFields(IInternalContest contest) {
        return jsonTool.convertToJSON(contest.getContestInformation()).toString();
    }

    /**
     * List of judgements.
     * 
     */
    public String getJudgementTypeJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Judgement[] judgements = contest.getJudgements();
        for (Judgement judgement : judgements) {
            appendJSONEvent(stringBuilder, JUDGEMENT_TYPE_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getJudgementTypeJSON(contest, judgement));
            stringBuilder.append(NL);
        }

        return stringBuilder.toString();
    }

    String getJudgementTypeJSON(IInternalContest contest, Judgement judgement) {
        return jsonTool.convertToJSON(judgement).toString();
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
        for (Language language : languages) {

            if (language.isActive()) {
                appendJSONEvent(stringBuilder, LANGUAGE_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getLanguageJSON(contest, language));
                stringBuilder.append(NL);
            }
        }

        return stringBuilder.toString();

    }

    /**
     * get JSON for a language.
     * 
     * @param contest
     * @param language
     * @param languageNumber
     *            sequence number
     * @return
     */
    public String getLanguageJSON(IInternalContest contest, Language language) {
        return jsonTool.convertToJSON(language).toString();
    }

    public String getProblemJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Problem[] problems = contest.getProblems();
        int id = 1;
        for (Problem problem : problems) {
            if (problem.isActive()) {
                appendJSONEvent(stringBuilder, PROBLEM_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getProblemJSON(contest, problem, id));
                stringBuilder.append(NL);
                id++;
            }
        }

        return stringBuilder.toString();
    }

    public String getProblemJSON(IInternalContest contest, Problem problem, int problemNumber) {
        return jsonTool.convertToJSON(problem, problemNumber).toString();
    }

    public String getGroupJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        Group[] groups = contest.getGroups();

        Arrays.sort(groups, new GroupComparator());
        for (Group group : groups) {

            if (group.isDisplayOnScoreboard()) {
                appendJSONEvent(stringBuilder, GROUPS_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getGroupJSON(contest, group));
                stringBuilder.append(NL);
            }
        }

        return stringBuilder.toString();
    }

    protected String getGroupJSON(IInternalContest contest, Group group) {
        return jsonTool.convertToJSON(group).toString();

    }

    public String getOrganizationJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        // get the team accounts from the model
        Account[] accounts = contest.getAccounts();
        // keep track of which ones we have dumped
        Hashtable<String, Account> organizations = new Hashtable<String, Account>();

        for (Account account : accounts) {
            if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && !account.getInstitutionCode().equals("undefined")) {
                if (!organizations.containsKey(account.getInstitutionCode())) {
                    organizations.put(account.getInstitutionCode(), account);
                    appendJSONEvent(stringBuilder, ORGANIZATION_KEY, ++eventIdSequence, EventFeedOperation.CREATE, jsonTool.convertOrganizationsToJSON(account).toString());
                    stringBuilder.append(NL);
                }
            }
        }

        return stringBuilder.toString();
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

            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                appendJSONEvent(stringBuilder, TEAM_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getTeamJSON(contest, account));
                stringBuilder.append(NL);
            }
        }

        return stringBuilder.toString();
    }

    public String getTeamJSON(IInternalContest contest, Account account) {
        return jsonTool.convertToJSON(account).toString();
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
            String[] names = account.getMemberNames();

            if (names.length > 0) {
                for (String teamMemberName : names) {
                    appendJSONEvent(stringBuilder, TEAM_MEMBERS_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getTeamMemberJSON(contest, account, teamMemberName));
                    stringBuilder.append(NL);
                }
            }
        }

        return stringBuilder.toString();
    }

    protected String getTeamMemberJSON(IInternalContest contest, Account account, String teamMemberName) {
        return teamMemberJSON.createJSON(contest, account, teamMemberName);
    }

    /**
     * Get run submission.
     * 
     * @param contest
     * @return
     */
    public String getSubmissionJSON(IInternalContest contest, HttpServletRequest servletRequest, SecurityContext sc ) {

        StringBuilder stringBuilder = new StringBuilder();
        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunComparator());
        for (Run run : runs) {
            if (!run.isDeleted()) {
                appendJSONEvent(stringBuilder, SUBMISSION_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getSubmissionJSON(contest, run, servletRequest, sc));
                stringBuilder.append(NL);
            }
        }

        return stringBuilder.toString();

    }

    public String getSubmissionJSON(IInternalContest contest, Run run, HttpServletRequest servletRequest, SecurityContext sc) {
        return jsonTool.convertToJSON(run, servletRequest, sc).toString();
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

            if (run.isJudged()) {

                appendJSONEvent(stringBuilder, JUDGEMENT_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getJudgementJSON(contest, run));
                stringBuilder.append(NL);
            }
        }

        return stringBuilder.toString();
    }

    private String getJudgementJSON(IInternalContest contest, Run run) {
        return jsonTool.convertJudgementToJSON(run).toString();
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
            JudgementRecord judgementRecord = run.getJudgementRecord();
            if (run.isJudged() && !judgementRecord.isPreliminaryJudgement()) {
                RunTestCase[] testCases = run.getRunTestCases();
                for (int j = 0; j < testCases.length; j++) {
                    appendJSONEvent(stringBuilder, RUN_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getRunJSON(contest, testCases, j));
                    stringBuilder.append(NL);
                }
            }
        }

        return stringBuilder.toString();
    }

    private String getRunJSON(IInternalContest contest, RunTestCase[] runTestCases, int ordinal) {
        return jsonTool.convertToJSON(runTestCases, ordinal).toString();
    }

    /**
     * Clarification Answer.
     * 
     * @param contest
     * @return
     */
    public String getClarificationJSON(IInternalContest contest) {
        StringBuilder stringBuilder = new StringBuilder();
        Clarification[] clarifications = contest.getClarifications();

        Arrays.sort(clarifications, new ClarificationComparator());
        for (Clarification clarification : clarifications) {
            appendJSONEvent(stringBuilder, CLARIFICATIONS_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getClarificationJSON(contest, clarification, null));
            stringBuilder.append(NL);
            if (clarification.isAnswered()) {
                ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
                appendJSONEvent(stringBuilder, CLARIFICATIONS_KEY, ++eventIdSequence, EventFeedOperation.CREATE, getClarificationJSON(contest, clarification, clarAnswers[clarAnswers.length - 1]));
                stringBuilder.append(NL);
            }
        }

        return stringBuilder.toString();
    }

    String getClarificationJSON(IInternalContest contest, Clarification clarification, ClarificationAnswer clarAnswer) {
        return jsonTool.convertToJSON(clarification, clarAnswer).toString();

    }

    public String getAwardJSON(IInternalContest contest) {
        return awardJSON.createJSON(contest);
    }

    public String createJSON(IInternalContest contest, EventFeedFilter filter, HttpServletRequest servletRequest, SecurityContext sc) throws IllegalContestState {

        if (contest == null) {
            return "[]";
        }

        // fetch lines
        String json = createJSON(contest, servletRequest, sc);
        String[] lines = json.split(NL);

        // filter
        List<String> list = EventFeedFilter.filterJson(lines, filter);

        // SOMEDAY in Java 8 return String.join(NL, list) + NL;

        String[] sa = (String[]) list.toArray(new String[list.size()]);
        return StringUtilities.join(NL, sa) + NL;
    }

    /**
     * Returns a JSON string listing the current contest event feed
     * 
     * @param contest
     *            - the current contest
     * @return a JSON string giving event feed in JSON
     * @throws IllegalContestState
     */
    public String createJSON(IInternalContest contest, HttpServletRequest servletRequest, SecurityContext sc) throws IllegalContestState {

        if (contest == null) {
            return "[]";
        }

        // Vector<Account> accountlist = contest.getAccounts(Type.TEAM);
        // if (accountlist.size() == 0) {
        // return "[]";
        // }
        // Account[] accounts = (Account[]) accountlist.toArray(new Account[accountlist.size()]);
        //
        // Group[] groups = contest.getGroups();
        // final Map<ElementId, String> groupMap = new HashMap<ElementId, String>();
        // for (Group group : groups) {
        // groupMap.put(group.getElementId(), group.getDisplayName());
        // }
        StringBuffer buffer = new StringBuffer();

        // contest = new SampleContest().createStandardContest();

        if (eventTypeList != null) {

            appendAllJSONEvents(contest, buffer, eventTypeList, servletRequest);

        } else {

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
            json = getSubmissionJSON(contest, servletRequest, sc);
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
        }
        return buffer.toString();
    }

    /**
     * Appends named event types onto a buffer.
     * 
     * valid events are: awards, clarifications, contests, groups, judgement-types, judgements, languages, organizations, problems, runs, submissions, team-members, teams
     * 
     * @param contest
     * @param buffer
     * @param inEventTypeList
     *            list of events types, comma delimited
     * @throws IllegalArgumentException
     *             if any event in eventTypeList is not valid
     */
    private void appendAllJSONEvents(IInternalContest contest, StringBuffer buffer, String inEventTypeList, HttpServletRequest servletRequest) throws IllegalArgumentException {

        String[] events = inEventTypeList.split(",");

        for (String name : events) {
            name = name.trim();

            switch (name) {
                case CONTEST_KEY:
                    appendNotNull(buffer, getContestJSON(contest));
                    break;
                case JUDGEMENT_TYPE_KEY:
                    appendNotNull(buffer, getJudgementTypeJSON(contest));
                    break;
                case LANGUAGE_KEY:
                    appendNotNull(buffer, getLanguageJSON(contest));
                    break;
                case PROBLEM_KEY:
                    appendNotNull(buffer, getProblemJSON(contest));
                    break;
                case GROUPS_KEY:
                    appendNotNull(buffer, getGroupJSON(contest));
                    break;
                case ORGANIZATION_KEY:
                    appendNotNull(buffer, getOrganizationJSON(contest));
                    break;
                case TEAM_KEY:
                    appendNotNull(buffer, getTeamJSON(contest));
                    break;
                case TEAM_MEMBERS_KEY:
                    appendNotNull(buffer, getTeamMemberJSON(contest));
                    break;
                case SUBMISSION_KEY:
                    appendNotNull(buffer, getSubmissionJSON(contest, servletRequest, null));
                    break;
                case JUDGEMENT_KEY:
                    appendNotNull(buffer, getJudgementJSON(contest));
                    break;
                case RUN_KEY:
                    appendNotNull(buffer, getRunJSON(contest));
                    break;
                case CLARIFICATIONS_KEY:
                    appendNotNull(buffer, getClarificationJSON(contest));
                    break;
                case AWARD_KEY:
                    appendNotNull(buffer, getAwardJSON(contest));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown event type '" + name + "' in list " + inEventTypeList);
            }
        }
    }

    /**
     * Get next event id.
     */
    public String nextEventId() {
        eventIdSequence++;
        return getEventId(eventIdSequence);
    }

    /**
     * Get next event sequence id.
     */
    public long nextEventIdSequence() {
        return(++eventIdSequence);
    }

    /**
     * get event id.
     * 
     * @param sequenceNumber
     *            ascending number
     * @return event Id
     */
    public static String getEventId(long sequenceNumber) {
        return "pc2-" + sequenceNumber;
    }

    public static long extractSequence(String eventId) {
        return Long.parseLong(eventId.substring(4));
    }

    // TODO technical deficit - move these methods
    // TODO move pair methods into JsonUtilities

    public String getStartEventId() {
        return startEventId;
    }

    public void setStartEventId(String startEventId) {
        this.startEventId = startEventId;
    }

    public void setEventTypeList(String eventTypeList) {
        this.eventTypeList = eventTypeList;
    }

    public String getEventTypeList() {
        return eventTypeList;
    }

    public long getEventIdSequence() {
        return eventIdSequence;
    }

    public void setEventIdSequence(long eventIdSequence) {
        this.eventIdSequence = eventIdSequence;
    }
}
