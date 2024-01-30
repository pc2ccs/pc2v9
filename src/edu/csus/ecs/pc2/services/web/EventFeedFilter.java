// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;

/**
 * Event Feed filter.
 *
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedFilter {

    private static final String EF_TYPE_STRING = "\"type\":";
    private static final String EF_SEQ_STRING = "\"id\":";
    private static final String DATA_OBJECT_STRING = "\"data\":";
    private static final String PROBLEM_ID_STRING = "\"id\":";
    private static final String CMS_GROUP_ID_STRING = "\"icpc_id\":";
    private static final String TEAM_GROUP_IDS_STRING = "\"group_ids\":";
    private static final String TEAM_ID_STRING = "\"id\":";
    private static final String SUB_TEAM_ID_STRING = "\"team_id\":";
    private static final String SUB_ID_STRING = "\"id\":";
    private static final String JUDGEMENT_ID_STRING = "\"id\":";
    private static final String JUDGEMENT_SUB_ID_STRING = "\"submission_id\":";
    private static final String RUN_JUDGEMENT_ID_STRING = "\"judgement_id\":";
    private static final String CLAR_FROM_TEAM_ID_STRING = "\"from_team_id\":";
    private static final String CLAR_TO_TEAM_ID_STRING = "\"to_team_id\":";
    private static final String CLAR_PROBLEM_ID_STRING = "\"problem_id\":";

    private String eventTypeList = null;

    private String startingEventId = null;

    // List of groups to match events for
    private List<Group> wantedGroups = null;
    private HashSet<String> wantedGroupsSet = null;
    private HashSet<String> cmsAffiliatedGroupIds = null;
    // Problems that are visible to this filter based on group
    private HashSet<String> problemFilter = null;

    // Teams that we are not interested in
    private HashSet<String> teamIgnore = new HashSet<String>();

    // Submissions we are not interested in (so we can filter out judgements for them)
    private HashSet<String> subIgnore = new HashSet<String>();

    // Judgements we are not interseted in (so we can filter out runs (test cases))
    private HashSet<String> judgementIgnore = new HashSet<String>();

    private String clientInfo;

    public EventFeedFilter(){
        this(null, null);
    }


    /**
     * Create filter.
     *
     * eventTypeList events are:  contests, judgement-types, languages, problems, groups, organizations,
     * teams, team-members, submissions, judgements, runs, clarifications, awards.
     *
     * <br>
     * The complete list of events are at: {@link EventFeedType}
     *
     * @param startingEventId start after event id, null allowed to indicate to not filter
     * @param eventTypeList eventtype list, null allowed to indicate to not filter
     */
    public EventFeedFilter(String startintEventId, String eventTypeList) {
        super();
        this.startingEventId = startintEventId;
        if(eventTypeList != null) {
            this.eventTypeList = eventTypeList.toUpperCase();
        } else {
            this.eventTypeList = null;
        }
    }


    public void addEventTypeList(String addEventTypeList) {
        // user may specify lower case events, eg. "languages,problems"
        // we should accept those
        this.eventTypeList = addEventTypeList.toUpperCase();
    }

    public void addStartintEventId(String addStartingEventId) {
        this.startingEventId = addStartingEventId;
    }

    /**
     * Add a list of groups to the event feed filter.  Only events appropriate for these groups are sent.
     *
     * TODO: does not take into account if an account or problem adds a group after the event
     *      feed has started.  This can affect which problems are sent on the event feed.
     * @param contest
     * @param wantedGroupIds Comma-separated list of group ids
     * @return true if the groups were valid, false if any are bad
     */
    public boolean addGroups(IInternalContest contest, String wantedGroupIds) {
        Account [] accounts = contest.getAccounts();
        HashMap<String, Group> groupToGroupId = new HashMap<String, Group>();

        // Populate hashmap with for group id to group mapping
        for(Group g : contest.getGroups()) {
            groupToGroupId.put(Integer.toString(g.getGroupId()), g);
        }
        for(String groupId : wantedGroupIds.split(",")) {
            Group group = groupToGroupId.get(groupId);
            if(group == null) {
                // Bad group id, abort request
                return false;
            }
            if(wantedGroups == null) {
                wantedGroups = new ArrayList<Group>();
                wantedGroupsSet = new HashSet<String>();
                cmsAffiliatedGroupIds = new HashSet<String>();
                problemFilter = new HashSet<String>();
            }
            wantedGroups.add(group);
            wantedGroupsSet.add(Integer.toString(group.getGroupId()));

            ElementId groupElementId = group.getElementId();

            // now, add all groups for each account that is a member of 'group'
            // this is somewhat inefficient
            for(Account account : accounts) {
                if(account.isGroupMember(groupElementId)) {
                    // add all groups for this account to the affiliatedGroups
                    for(ElementId gElementId : account.getGroupIds()) {
                        cmsAffiliatedGroupIds.add(Integer.toString(contest.getGroup(gElementId).getGroupId()));
                    }
                }
            }

            // now create a hashset of all wanted problem short names (id's) for quick look up later
            for(Problem problem : contest.getProblems()) {
                if(problem.canView(wantedGroups)) {
                    String id = problem.getShortName();
                    // if we don't have a problem shortNamee use the internal id
                    if (id == null || id.length() == 0) {
                        id = problem.getElementId().toString();
                    }
                    problemFilter.add(id);
                }
            }
        }
        // get rid of empty list - shouldn't happen, but can't hurt to check.
        if(wantedGroups != null && wantedGroups.isEmpty()) {
            wantedGroups = null;
            wantedGroupsSet = null;
            cmsAffiliatedGroupIds = null;
            problemFilter = null;
        }
        return(true);
    }

    private boolean matchesFilter(String eventId, EventFeedType type) {

        boolean matched = true;

        if (startingEventId != null) {
            long startId = EventFeedJSON.extractSequence(startingEventId);
            long actual = EventFeedJSON.extractSequence(eventId);
            matched &= actual > startId;
        }

        if (eventTypeList != null) {
            //note: at some point someone added specific (lower/mixed? case) names for the EventFeedType enum
            // hence we have to convert it to uppercase since the eventTypeList has been converted to uppercase
            matched &= eventTypeList.indexOf(type.toString().toUpperCase()) > -1;
        }

        return matched;
    }

    /**
     * match JSON line.
     *
     * @param string JSON string, ex.
     * @return
     */
    public boolean matchesFilter(String string) {

        boolean matches = true;

        EventFeedType recType = EventFeedType.UNDEFINED;

        // always check groups first so we can populate our filtering maps for things like teams' submissions
        if(wantedGroups != null) {
            // Isolate "data":{} portion
            int dataIndex = string.indexOf(DATA_OBJECT_STRING);

            if(dataIndex > 0) {
                String rec = getCleanValue(string, EF_TYPE_STRING);
                if(rec != null) {
                    recType = parseEventFeedType(rec);
                    String dataObject = string.substring(dataIndex + DATA_OBJECT_STRING.length());

                    // The only type of events that require filtering by group are:
                    // problems, groups, teams, submissions, judgements, runs, clarifications
                    // a case COULD be made for organizations but, apparently sending extra orgs is ok, ask Fredrik.
                    switch(recType) {
                        case PROBLEMS:
                            matches &= matchesProblem(dataObject);
                            break;

                        case GROUPS:
                            matches &= matchesGroup(dataObject);
                            break;

                        case TEAMS:
                            matches &= matchesTeam(dataObject);
                            break;

                        case SUBMISSIONS:
                            matches &= matchesSubmission(dataObject);
                            break;

                        case JUDGEMENTS:
                            matches &= matchesJudgement(dataObject);
                            break;

                        case RUNS:
                            matches &= matchesRun(dataObject);
                            break;

                        case CLARIFICATIONS:
                            matches &= matchesClar(dataObject);
                            break;
                    }
                }
            }
        }

        if (matches && (startingEventId != null || eventTypeList != null)) {
            // we know that this event would pass through (passed group test), so we now check for event type and token
            if(recType == EventFeedType.UNDEFINED) {
                String rec = getCleanValue(string, EF_TYPE_STRING);
                if(rec != null) {
                    recType = parseEventFeedType(rec);
                }
            }
            String seqField = getCleanValue(string, EF_SEQ_STRING);
            if(seqField != null) {
                matches &= matchesFilter(seqField, recType);
            }
        }
        return matches;
    }

    /**
     * Extract event feed type.
     * @param string JSON string, {"type":"languages", "id":"pc2-11", "op":"create", "data": {"id":"1","name":"Java"}}
     * @return type for event, ex. "languages" as EventFeedType.LANGUAGES.
     */
    protected EventFeedType getEventFeedType(String string) {
        // {"type":"languages", "id":"pc2-11", "op":"create", "data": {"id":"1","name":"Java"}}

        String typeValue = getCleanValue(string, EF_TYPE_STRING);
        return(parseEventFeedType(typeValue));
    }

    /**
     * Convert string to enum for event type
     *
     * @param typeValue string type of event, eg. teams, problems, submissions, etc.
     * @return EventFeedType enumeration
     */
    private EventFeedType parseEventFeedType(String typeValue) {
        if(typeValue == null) {
            return(EventFeedType.UNDEFINED);
        }
        return EventFeedType.valueOf(typeValue.toUpperCase().replace("-", "_"));

    }

    /**
     * Extract value for id from JSON string
     * @param string ex. {"event":"languages", "id":"pc2-11", "op":"create", "data": {"id":"1","name":"Java"}}
     * @return value for id, ex pc2-11
     */
    protected String getEventFeedSequence(String string) {
        // {"event":"languages", "id":"pc2-11", "op":"create", "data": {"id":"1","name":"Java"}}
        String seqVal = getCleanValue(string, EF_SEQ_STRING);
        if(seqVal == null) {
            seqVal = "";
        }
        return(seqVal);
    }

    /**
     * Filter JSON lines.
     *
     * @param jsonLines
     * @return list of lines matching filter.
     */
    public List<String> filterJson(String [] jsonLines){
        return filterJson(jsonLines, this);
    }

    /**
     * Filter JSON lines.
     *
     * @param jsonLines
     * @param filter
     * @return list of lines matching filter.
     */
    public static List<String> filterJson(String [] jsonLines, EventFeedFilter filter){
        List<String> filteredLines = new ArrayList<String>();
        for (String string : jsonLines) {
            if (filter.matchesFilter(string)){
                filteredLines.add(string);
            }
        }
        return filteredLines;
    }


    @Override
    public String toString() {

        String strStartingEventId = startingEventId;
        if (strStartingEventId == null) {
            strStartingEventId = "<none set>";
        }
        String strEventTypeList = eventTypeList;
        if (strEventTypeList == null) {
            strEventTypeList = "<none set>";
        }
        String strGroupList = "<none set>";
        if (wantedGroupsSet != null) {
            strGroupList = wantedGroupsSet.toString();
        }
        return "startid = " + strStartingEventId + ", event types = " + strEventTypeList + ", groupids = " + strGroupList;
    }


    /**
     * Set identifying information for the client using this filter
     *
     * @param string
     */
    public void setClient(String string) {
        clientInfo = string;
    }

    /**
     * Return the identifying information for this user of this filter.
     *
     * @return
     */
    public String getClient() {
        return clientInfo;
    }

    /**
     * Checks if the decorated problem id supplied matches the group filter
     *
     * @param string json decorated problem id, eg: "{"id":"cornhole-1", "....
     * @return true if the problem matches, false otherwise
     */
    private boolean matchesProblem(String string) {
        boolean matches = true;

        if(problemFilter != null && string != null) {

            string = getCleanValue(string, PROBLEM_ID_STRING);
            if(string != null) {
                matches &= problemFilter.contains(string);
            }
        }
        return(matches);
    }

    /**
     * Checks if the decorated cms group id supplied matches the group filter
     * @param string data object from event
     * @return true if the group matches, false otherwise
     */
    private boolean matchesGroup(String string) {
        boolean matches = true;

        String cmsGroupId = getCleanValue(string, CMS_GROUP_ID_STRING);
        if(cmsGroupId != null) {
            matches &= cmsAffiliatedGroupIds.contains(cmsGroupId);
        }
        return(matches);
    }

    /**
     * Checks if the decorated team's group ids supplied matches any in the group filter
     * @param string - entire json line
     * @return true if the team is a member of any wanted group, false otherwise
     */
    private boolean matchesTeam(String string) {
        boolean matches = true;

        if(string != null) {
            String [] groupIds = getCleanValueArray(string, TEAM_GROUP_IDS_STRING);

            if(groupIds != null && groupIds.length > 0) {
                // go through this teams groups, and if any are in the wanted list, accept it and end the loop
                matches = false;
                for(String cmsGroupId : groupIds) {
                    if(wantedGroupsSet.contains(cmsGroupId)) {
                        matches = true;
                        break;
                    }
                }
            } else {
                // If no groups on this team, then do not send on feed (we know there is a group filter in place)
                matches = false;
            }
            if(!matches) {
                // we are not interested in anything from this team, so keep track of that for submissions and clars
                String teamId = getCleanValue(string, TEAM_ID_STRING);
                if(teamId != null) {
                    if(teamId.length() > 0) {
                        teamIgnore.add(teamId);
                    }
                }
            }
        }
        return(matches);
    }

    /**
     * Checks if a submission should be shown on EF.  This presupposes that accounts (teams) were sent prior to this
     * so that the teamIgnore hashset is populated.
     *
     * @param string containing data portion of event
     * @return true if the submission should be sent on EF, false if not
     */
    private boolean matchesSubmission(String string) {
        boolean matches = true;

        String submitterId = getCleanValue(string, SUB_TEAM_ID_STRING);
        if(submitterId != null) {
            if(teamIgnore.contains(submitterId)) {
                matches = false;

                string = getCleanValue(string, SUB_ID_STRING);
                if(string != null) {
                    subIgnore.add(string);
                }
            }
        }
        return(matches);
    }

    /**
     * Checks if a judgement is visible based on whether we ignored the submission or not
     *
     * @param string
     * @return true if the judgement should be shown, false otherwise
     */
    private boolean matchesJudgement(String string) {
        boolean matches = true;

        String subId = getCleanValue(string, JUDGEMENT_SUB_ID_STRING);
        if(subId != null) {
            if(subIgnore.contains(subId)) {
                matches = false;

                string = getCleanValue(string, JUDGEMENT_ID_STRING);
                if(string != null) {
                    judgementIgnore.add(string);
                }
            }
        }
        return(matches);
    }

    /**
     * Checks if a run testcase is visible based on whether we ignored the judgement it is part of or not
     *
     * @param string
     * @return true if the run should be shown, false otherwise
     */
    private boolean matchesRun(String string) {
        boolean matches = true;

        String judgementId = getCleanValue(string, RUN_JUDGEMENT_ID_STRING);
        if(judgementId != null) {
            if(judgementIgnore.contains(judgementId)) {
                matches = false;
            }
        }
        return(matches);
    }

    /**
     * Checks if a clarification should be visible on the EF.  This is harder to check.  We show the clar if:
     * 1) If either the from or to team is visible on the feed
     * 2) If both from and to teams are null and the problem is visible on the feed
     *
     * @param string
     * @return true if the clar should be sent on the feed, false otherwise.
     */
    private boolean matchesClar(String string) {
        boolean matches = true;

        String fromTeamId = getCleanValue(string, CLAR_FROM_TEAM_ID_STRING);
        if(fromTeamId != null && !fromTeamId.equals("null")) {
            // from team is not null, so if we are ignoring this team, then we don't show this clar
            if(teamIgnore.contains(fromTeamId)) {
               matches = false;
            }
            // else we will always show this clar since the author is visible on the feed
        } else  {
            String toTeamId = getCleanValue(string, CLAR_TO_TEAM_ID_STRING);
            if(toTeamId == null || toTeamId.equals("null")) {
                // Both from and to teams are null, only show if the problem visible
                String problemId = getCleanValue(string, CLAR_PROBLEM_ID_STRING);
                if(problemId != null) {
                    // a 'null' problem id is always allowed (general clar)
                    if(!problemId.equals("null")) {
                        matches &= problemFilter.contains(problemId);
                    }
                }
            } else if(teamIgnore.contains(toTeamId)){
                matches = false;
            }
        }
        return(matches);
    }

    /**
     * Finds the supplied json property and isolates the value and cleans it up (removes quotes, braces, lead/trailing spaces)
     *
     * @param string to find property in
     * @return cleaned up property value string otherwise null if not found (no property)
     */
    private String getCleanValue(String string, String jsonProperty) {
        if(string != null) {
            int idx = string.indexOf(jsonProperty);

            if(idx >= 0) {
                idx += jsonProperty.length();
                // Find end of this property.  None of the properties we care about have commas in them, so this is safe.
                int idxEnd = string.indexOf(',', idx);
                if(idxEnd > 0) {
                    string = string.substring(idx, idxEnd);
                } else {
                    string = string.substring(idx);
                }
                // this is probably ridiculously expensive, what with the RE and all.
                // TODO: write optimized routine to remove a set of characters, eg. removeAll(String, String charset)
                string = string.replaceAll("[\"{}]", "").trim();
            }
        } else {
            string = null;
        }
        return string;
    }

    /**
     * Finds the supplied json property array and isolates the value and cleans it up (removes quotes, bracket, braces, spaces)
     * eg. "group_ids":["35851","1201","1206"]}}  -> String [] "35851","1201","1206"
     * @param string
     * @return cleaned up property array value as string [] otherwise null if not found (no property)
     */
    private String [] getCleanValueArray(String string, String jsonProperty) {
        String [] groupArray = null;

        if(string != null) {
            int idx = string.indexOf(jsonProperty);

            if(idx >= 0) {
                idx += jsonProperty.length();
                idx = string.indexOf('[', idx);
                // has to be a [ to start the array, if not, we don't care about this
                if(idx >= 0) {
                    idx++;
                    int idxEnd = string.indexOf(']', idx);
                    if(idxEnd > 0) {
                        // this is probably ridiculously expensive, what with the RE and all.
                        // TODO: write optimized routine to remove a set of characters, eg. removeAll(String, String charset)
                        groupArray = string.substring(idx, idxEnd).replaceAll("[\"\\s]", "").split(",");
                    }
                }
            }
        }
        return groupArray;
    }
}
