// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.IJSONTool;

/**
 * Event feed information in the CLICS JSON format.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
// TODO for all sections pass in Key rather than hard coded inside method
public class EventFeedJSON extends JSON202306Utilities {

    public static final String EVENT_ID_PREFIX = "pc2-";

    private boolean useCollections = true;

    /**
     *
     */
    public EventFeedJSON(IJSONTool jsonTool) {
        super();
        this.jsonTool = jsonTool;
    }

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

    private IJSONTool jsonTool;

    private Filter filter = null;

    private HashSet<ElementId> ignoreGroup = new HashSet<ElementId>();

    private HashSet<ClientId> ignoreTeam = new HashSet<ClientId>();

    public String getContestJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        appendJSONEvent(stringBuilder, CONTEST_KEY, ++eventIdSequence, null, getContestJSONFields(contest));
        stringBuilder.append(NL);
        return stringBuilder.toString();

    }

    public String getContestJSONFields(IInternalContest contest) {
        return new CLICSContestInfo(contest, null).toJSON();
    }

    public String getStateJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        appendJSONEvent(stringBuilder, STATE_KEY, ++eventIdSequence, null, new CLICSContestState(contest, null).toJSON());
        stringBuilder.append(NL);
        return stringBuilder.toString();

    }
    /**
     * List of judgements.
     *
     */
    public String getJudgementTypeJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Judgement[] judgements = contest.getJudgements();
        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Judgement judgement : judgements) {
                if(bFirst) {
                    bFirst = false;
                } else {
                    dataCollection.append(",");
                }
                dataCollection.append(getJudgementTypeJSON(contest, judgement));
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, JUDGEMENT_TYPE_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Judgement judgement : judgements) {
                appendJSONEvent(stringBuilder, JUDGEMENT_TYPE_KEY, ++eventIdSequence, judgement.getAcronym(), getJudgementTypeJSON(contest, judgement));
                stringBuilder.append(NL);
            }
        }
        return stringBuilder.toString();
    }

    public String getJudgementTypeJSON(IInternalContest contest, Judgement judgement) {
        return new CLICSJudgmentType(contest, judgement).toJSON();
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
        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Language language : languages) {
                if(language.isActive()) {
                    if(bFirst) {
                        bFirst = false;
                    } else {
                        dataCollection.append(",");
                    }
                    dataCollection.append(getLanguageJSON(contest, language));
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, LANGUAGE_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Language language : languages) {

                if (language.isActive()) {
                    appendJSONEvent(stringBuilder, LANGUAGE_KEY, ++eventIdSequence, IJSONTool.getLanguageId(language), getLanguageJSON(contest, language));
                    stringBuilder.append(NL);
                }
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
     * @return
     */
    public String getLanguageJSON(IInternalContest contest, Language language) {
        return new CLICSLanguage(language).toJSON();
    }

    public String getProblemJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Problem[] problems = contest.getProblems();
        int id = 1;
        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Problem problem : problems) {
                if(problem.isActive() && (filter == null || filter.matches(problem))) {
                    if(bFirst) {
                        bFirst = false;
                    } else {
                        dataCollection.append(",");
                    }
                    dataCollection.append(getProblemJSON(contest, problem, id));
                    id++;
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, PROBLEM_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Problem problem : problems) {
                if (problem.isActive() && (filter == null || filter.matches(problem))) {
                    appendJSONEvent(stringBuilder, PROBLEM_KEY, ++eventIdSequence, IJSONTool.getProblemId(problem), getProblemJSON(contest, problem, id));
                    stringBuilder.append(NL);
                    id++;
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getProblemJSON(IInternalContest contest, Problem problem, int problemNumber) {
        return new CLICSProblem(contest, problem, problemNumber).toJSON();
    }

    public String getGroupJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        Group[] groups = contest.getGroups();

        Arrays.sort(groups, new GroupComparator());

        HashSet<ElementId> usedGroups = getGroupsUsed(contest);

        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Group group : groups) {
                // Put this group in the event feed if teams are not members of any group or one of the teams
                // that matches the (possibly in-effect) filters used this group.
                if (usedGroups == null || usedGroups.contains(group.getElementId())) {
                    if(bFirst) {
                        bFirst = false;
                    } else {
                        dataCollection.append(",");
                    }
                    dataCollection.append(getGroupJSON(contest, group));
                } else {
                    ignoreGroup.add(group.getElementId());
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, GROUPS_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Group group : groups) {
                // Put this group in the event feed if teams are not members of any group or one of the teams
                // that matches the (possibly in-effect) filters used this group.
                if (usedGroups == null || usedGroups.contains(group.getElementId())) {
                    appendJSONEvent(stringBuilder, GROUPS_KEY, ++eventIdSequence, IJSONTool.getGroupId(group), getGroupJSON(contest, group));
                    stringBuilder.append(NL);
                } else {
                    ignoreGroup.add(group.getElementId());
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Returns a hashset that includes all groups that any (possibly filtered) teams are members of.
     * We need this so we include all "used" groups in the event feed.
     *
     * @param contest
     * @return Set of Group elementId's found in any matching or null if none found
     */
    private HashSet<ElementId> getGroupsUsed(IInternalContest contest) {

        Account[] accounts = EventFeedJSON.getTeamAccounts(contest);

        HashSet<ElementId> usedGroups = new HashSet<ElementId>();

        for (Account account : accounts) {

            if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && isDisplayAccountGroupOnScoreboard(account) && (filter == null || filter.matches(account))) {
                HashSet<ElementId> groups = account.getGroupIds();
                if(groups != null) {
                    for(ElementId groupElementId : groups) {
                        usedGroups.add(groupElementId);
                    }
                }
            }
        }
        if(usedGroups.size() == 0) {
            usedGroups = null;
        }
        return usedGroups;
    }

    public String getGroupJSON(IInternalContest contest, Group group) {
        return new CLICSGroup(group).toJSON();

    }

    public String getOrganizationJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        // get the team accounts from the model
        Account[] accounts = contest.getAccounts();
        // keep track of which ones we have dumped so we don't do them twice
        Hashtable<String, Account> organizations = new Hashtable<String, Account>();


        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Account account : accounts) {
                if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && (filter == null || filter.matches(account)) && !account.getInstitutionCode().equals("undefined")) {
                    if (!organizations.containsKey(account.getInstitutionCode())) {
                        organizations.put(account.getInstitutionCode(), account);
                        if(bFirst) {
                            bFirst = false;
                        } else {
                            dataCollection.append(",");
                        }
                        dataCollection.append(getOrganizationJSON(account));
                    }
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, ORGANIZATION_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Account account : accounts) {
                if (account.getClientId().getClientType().equals(ClientType.Type.TEAM) && (filter == null || filter.matches(account)) && !account.getInstitutionCode().equals("undefined")) {
                    if (!organizations.containsKey(account.getInstitutionCode())) {
                        organizations.put(account.getInstitutionCode(), account);
                        appendJSONEvent(stringBuilder, ORGANIZATION_KEY, ++eventIdSequence, IJSONTool.getOrganizationId(account), getOrganizationJSON(account).toString());
                        stringBuilder.append(NL);
                    }
                }
            }
        }

        return stringBuilder.toString();
    }

    public String getOrganizationJSON(Account account) {
        // this is a hack because we do not have organizations in the Model directly,
        // rather, each Account actually has the full organziation info.
        // [0]:INST-U-1329 [1]:New York University [2]:NYU
        // but only need [1] & [2].  We get the rest from the a typical account.
        String [] fields = new String[3];

        fields[0] = IJSONTool.getOrganizationId(account);   // Not used in CLICSOrganization; included for completeness
        fields[1] = account.getInstitutionName();
        fields[2] = account.getInstitutionShortName();
        return new CLICSOrganization(fields[0], account, fields).toJSON();
    }

    public String getTeamJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Account[] accounts = EventFeedJSON.getTeamAccounts(contest);

        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Account account : accounts) {
                if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && isDisplayAccountGroupOnScoreboard(account) && (filter == null || filter.matches(account))) {
                    if(bFirst) {
                        bFirst = false;
                    } else {
                        dataCollection.append(",");
                    }
                    dataCollection.append(getTeamJSON(contest, account));
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, TEAM_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Account account : accounts) {
                if (account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) && isDisplayAccountGroupOnScoreboard(account) && (filter == null || filter.matches(account))) {
                    appendJSONEvent(stringBuilder, TEAM_KEY, ++eventIdSequence, IJSONTool.getAccountId(account), getTeamJSON(contest, account));
                    stringBuilder.append(NL);
                } else {
                    ignoreTeam.add(account.getClientId());
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getTeamJSON(IInternalContest contest, Account account) {
        return new CLICSTeam(contest, account).toJSON();
    }

    /**
     * Determine if the supplied account is to be shown on the scoreboard based on the groups it belongs to
     *
     * @param account to check if it has a group that can be displayed
     * @return true if the account should be displayed, false otherwise
     */
    private boolean isDisplayAccountGroupOnScoreboard(Account account)
    {
        HashSet<ElementId> groups = account.getGroupIds();
        boolean canDisplay = false;
        if(groups != null) {
            for(ElementId groupElementId : groups) {
                if(!ignoreGroup.contains(groupElementId)) {
                    canDisplay = true;
                    break;
                }
            }
        } else {
            // If no groups for account, then it's ok to display
            canDisplay = true;
        }
        return(canDisplay);
    }

    /**
     * Get team member info.
     *
     */
    public String getTeamMemberJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        int memberId;
        Account[] accounts = EventFeedJSON.getTeamAccounts(contest);

        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Account account : accounts) {
                if (filter == null || filter.matches(account)) {
                    String[] names = account.getMemberNames();

                    if (names.length > 0) {
                        for (String teamMemberName : names) {
                            String teamMemberJson = getTeamMemberJSON(contest, account, teamMemberName);
                            if(teamMemberJson != null) {
                                if(bFirst) {
                                    bFirst = false;
                                } else {
                                    dataCollection.append(",");
                                }
                                dataCollection.append(teamMemberJson);
                            }
                        }
                    }
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, TEAM_MEMBERS_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Account account : accounts) {
                if(filter == null || filter.matches(account)) {
                    String[] names = account.getMemberNames();

                    if (names.length > 0) {
                        // JB: A bit of a TODO hack here.  create a unique ID on-the-fly for the contestant.
                        // Multiply the account id by 10 and add the member's ordinal on the team.
                        // Of course this will be fixed when we really add persons to the model and use the ICPC id.
                        // ... or something.
                        memberId = account.getClientId().getClientNumber()*10 + 1;
                        for (String teamMemberName : names) {
                            String teamMemberJson = getTeamMemberJSON(contest, account, teamMemberName);
                            if(teamMemberJson != null) {
                                appendJSONEvent(stringBuilder, TEAM_MEMBERS_KEY, ++eventIdSequence, "" + memberId, teamMemberJson);
                                // Bump faux team member id
                                memberId++;
                                stringBuilder.append(NL);
                            }
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * TODO Correctly implement this by adding persons to the model.
     * Right now, team members are simply strings in the account - they have no ID, or ICPC ID.
     * This routine is a place-holder until such a time as that code is added.
     *
     * @param contest
     * @param account
     * @param teamMemberName (probably should be the ordinal(or index) of the member
     * @return JSON string for the team member or null if none
     */
    public String getTeamMemberJSON(IInternalContest contest, Account account, String teamMemberName) {
        return(null);
        // TODO: Fix - need to add team member names to the model, not just have strings for members.  eg. read persons.json
        // The class TeamMemberJSON (since removed) which supposedly generated JSON for a team member was just plain wrong.
        // It gave all members of a team the same "id" for starters (that of the account).  Also, for some reason, it only added team
        // id's if it had an external CMS team id, even though it was adding the pc2 team's client ID.
        // Basically it was dunsel code.
        //return teamMemberJSON.createJSON(contest, account, teamMemberName);
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
        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Run run : runs) {
                if (!run.isDeleted() && !ignoreTeam.contains(run.getSubmitter())) {
                    if(bFirst) {
                        bFirst = false;
                    } else {
                        dataCollection.append(",");
                    }
                    dataCollection.append(getSubmissionJSON(contest, run, servletRequest, sc));
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, SUBMISSION_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Run run : runs) {
                if (!run.isDeleted() && !ignoreTeam.contains(run.getSubmitter())) {
                    appendJSONEvent(stringBuilder, SUBMISSION_KEY, ++eventIdSequence, IJSONTool.getSubmissionId(run), getSubmissionJSON(contest, run, servletRequest, sc));
                    stringBuilder.append(NL);
                }
            }
        }
        return stringBuilder.toString();

    }

    /**
     * Get JSON for a submission.
     * JB: Not sure why we need servletRequest and sc.  There is a comment in old API spec code (2020-03):
     *   // FIXME we need separate event feeds for public and admin/analyst
     *   // FIXME perhaps change sc to a boolean for public or not?
     * I'm not sure what that means.  I thought submissions were always visible?  it's the results that aren't.
     *
     * @param contest
     * @param run
     * @param servletRequest - Web request info
     * @param sc - security info of user
     * @return JSON string for the submission
     */
    public String getSubmissionJSON(IInternalContest contest, Run run, HttpServletRequest servletRequest, SecurityContext sc) {
        return new CLICSSubmission(contest, run).toJSON();
    }

    /**
     * List of all runs' judgements..
     *
     */
    public String getJudgementJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();
        Run[] runs = contest.getRuns();

        Arrays.sort(runs, new RunComparator());

        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Run run : runs) {
                if (run.isJudged() && !ignoreTeam.contains(run.getSubmitter())) {
                    if(bFirst) {
                        bFirst = false;
                    } else {
                        dataCollection.append(",");
                    }
                    dataCollection.append(getJudgementJSON(contest, run));
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, JUDGEMENT_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Run run : runs) {

                if (run.isJudged() && !ignoreTeam.contains(run.getSubmitter())) {

                    appendJSONEvent(stringBuilder, JUDGEMENT_KEY, ++eventIdSequence, run.getElementId().toString(), getJudgementJSON(contest, run));
                    stringBuilder.append(NL);
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getJudgementJSON(IInternalContest contest, Run run) {
        ObjectNode jNode = jsonTool.convertJudgementToJSON(run);
        if(jNode == null) {
            return("");
        }
        return jNode.toString();
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

        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Run run : runs) {
                if (ignoreTeam.contains(run.getSubmitter()) || !run.isJudged()) {
                    continue;
                }
                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (!judgementRecord.isPreliminaryJudgement()) {
                    RunTestCase[] testCases = run.getRunTestCases();
                    for (int j = 0; j < testCases.length; j++) {
                        if(bFirst) {
                            bFirst = false;
                        } else {
                            dataCollection.append(",");
                        }
                        dataCollection.append(getRunJSON(contest, testCases, j));
                    }
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, RUN_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Run run : runs) {
                if (ignoreTeam.contains(run.getSubmitter()) || !run.isJudged()) {
                    continue;
                }
                JudgementRecord judgementRecord = run.getJudgementRecord();
                if (!judgementRecord.isPreliminaryJudgement()) {
                    RunTestCase[] testCases = run.getRunTestCases();
                    for (int j = 0; j < testCases.length; j++) {
                        appendJSONEvent(stringBuilder, RUN_KEY, ++eventIdSequence, testCases[j].getElementId().toString(), getRunJSON(contest, testCases, j));
                        stringBuilder.append(NL);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getRunJSON(IInternalContest contest, RunTestCase[] runTestCases, int ordinal) {
        return new CLICSTestCase(contest, runTestCases[ordinal]).toJSON();
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
        if(isUseCollections()) {
            StringBuilder dataCollection = new StringBuilder();
            boolean bFirst = true;
            dataCollection.append("[");
            for (Clarification clarification : clarifications) {
                if (ignoreTeam.contains(clarification.getSubmitter())) {
                    continue;
                }
                if(bFirst) {
                    bFirst = false;
                } else {
                    dataCollection.append(",");
                }
                dataCollection.append(getClarificationJSON(contest, clarification, null));
                if (clarification.isAnsweredorAnnounced()) {
                    ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
                    dataCollection.append(",");
                    dataCollection.append(getClarificationJSON(contest, clarification, clarAnswers[clarAnswers.length - 1]));
                }
            }
            if(dataCollection.length() > 1) {
                dataCollection.append("]");
                appendJSONEvent(stringBuilder, CLARIFICATIONS_KEY, ++eventIdSequence, null, dataCollection.toString());
                stringBuilder.append(NL);
            }
        } else {
            for (Clarification clarification : clarifications) {
                if (ignoreTeam.contains(clarification.getSubmitter())) {
                    continue;
                }
                appendJSONEvent(stringBuilder, CLARIFICATIONS_KEY, ++eventIdSequence, IJSONTool.getClarificationId(clarification), getClarificationJSON(contest, clarification, null));
                stringBuilder.append(NL);
                if (clarification.isAnsweredorAnnounced()) {
                    ClarificationAnswer[] clarAnswers = clarification.getClarificationAnswers();
                    ClarificationAnswer clarAns = clarAnswers[clarAnswers.length - 1];
                    appendJSONEvent(stringBuilder, CLARIFICATIONS_KEY, ++eventIdSequence, IJSONTool.getClarificationAnswerId(clarAns), getClarificationJSON(contest, clarification, clarAns));
                    stringBuilder.append(NL);
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getClarificationJSON(IInternalContest contest, Clarification clarification, ClarificationAnswer clarAnswer) {
        return new CLICSClarification(contest, clarification, clarAnswer).toJSON();

    }

    public String getAwardJSON(IInternalContest contest) {
        return null;
        // TODO: Old code return null as a result of the unimplemented createJSON in the unimplemented AwardJSON class (which has
        // since been removed).  This could use the IJSONTool interface which is API specific
        //return awardJSON.createJSON(contest);
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

        return String.join(NL, list) + NL;
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
            json = getStateJSON(contest);
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
                case STATE_KEY:
                    appendNotNull(buffer, getStateJSON(contest));
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
        return EventFeedJSON.getEventId(eventIdSequence);
    }

    /**
     * Get next event sequence id.
     */
    public long nextEventIdSequence() {
        return(++eventIdSequence);
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

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean isUseCollections() {
        return useCollections;
    }

    public void setUseCollections(boolean useCollections) {
        this.useCollections = useCollections;
    }
    /**
     * Get event id property for the event feed notification
     *
     * @param sequenceNumber ascending number
     * @return event Id, eg: pc2-314
     */
    public static String getEventId(long sequenceNumber) {
        return EVENT_ID_PREFIX + sequenceNumber;
    }

    /**
     * Remove the EVENT_ID_PREFIX from an event id
     *
     * @param eventId
     * @return the sequence number of the event
     */
    public static long extractSequence(String eventId) {
        return Long.parseLong(eventId.substring(EVENT_ID_PREFIX.length()));
    }

    /**
     * Get all sites' teams in sorted order.
     *
     * @param inContest
     * @return array of sorted teams' Accounts
     */
    public static Account[] getTeamAccounts(IInternalContest inContest) {
        Vector<Account> accountVector = inContest.getAccounts(ClientType.Type.TEAM);
        Account[] accounts = accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }
}
