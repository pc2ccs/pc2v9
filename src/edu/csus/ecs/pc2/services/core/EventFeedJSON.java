package edu.csus.ecs.pc2.services.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Event feed information in the CLICS JSON format.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedJSON implements IEventSequencer {

    private long id = 0;

    /**
     * ISO 8601 Date format for SimpleDateFormat.
     */
    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss z";

    /**
     * New Line (EOLN).
     */
    private static final String NL = System.getProperty("line.separator");
    
    private static final String JSON_EOLN = ", " + NL;


    private SimpleDateFormat iso8601formatter = new SimpleDateFormat(ISO_8601_DATE_FORMAT);

    public String getContestJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", "contest");
        stringBuilder.append(", ");

        ContestInformation info = contest.getContestInformation();
        ContestTime time = contest.getContestTime();

        appendPair(stringBuilder, "id", getNextEventId());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", info.getContestShortName());
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

        appendPair(stringBuilder, "penalty_time", DefaultScoringAlgorithm.getDefaultProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "state", ""); // TODO what is this value ?

        stringBuilder.append(", ");
        appendPair(stringBuilder, "state.running", time.isContestRunning());
        stringBuilder.append(", ");

        boolean finalized = false;
        FinalizeData finalizeData = contest.getFinalizeData();
        if (finalizeData != null && finalizeData.isCertified()) {
            finalized = true;
        }

        appendPair(stringBuilder, "state.frozen", isContestFrozen(contest));
        stringBuilder.append(", ");

        appendPair(stringBuilder, "state.final", Boolean.toString(finalized));

        stringBuilder.append("} ");

        return stringBuilder.toString();
    }

    private boolean isContestFrozen(IInternalContest contest) {

        // TODO technical deficit - code this.
        return false;
    }

    public String getJudgementTypeJSON(IInternalContest contest) {

        return null; // TODO technical deficit code this
    }

    public String getLanguageJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Language[] languages = contest.getLanguages();
        int id = 1;
        for (Language language : languages) {
            stringBuilder.append(getLanguageJSON(contest, language, id));
            stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();

    }

    public String getLanguageJSON(IInternalContest contest, Language language, int languageNumber) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", "language");
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", languageNumber);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", language.getDisplayName());

        stringBuilder.append("} ");

        return stringBuilder.toString();
    }

    public String getProblemJSON(IInternalContest contest) {

        StringBuilder stringBuilder = new StringBuilder();

        Problem[] problems = contest.getProblems();
        int id = 1;
        for (Problem problem : problems) {
            stringBuilder.append(getProblemJSON(contest, problem, id));
            stringBuilder.append(JSON_EOLN);
        }

        return stringBuilder.toString();
    }

    public String getProblemJSON(IInternalContest contest, Problem problem, int problemNumber) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");

        appendPair(stringBuilder, "event", "problem");
        stringBuilder.append(", ");

        appendPair(stringBuilder, "id", problemNumber); 
        stringBuilder.append(", ");

        appendPair(stringBuilder, "label", problem.getLetter()); // letter
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", problem.getDisplayName());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "ordinal", problemNumber);
        stringBuilder.append(", ");

        appendPair(stringBuilder, "rgb", problem.getColorRGB());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "color", problem.getColorName());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "test_data_coun", problem.getNumberTestCases());

        stringBuilder.append("} ");

        return stringBuilder.toString();
    }

    public String getGroupJSON(IInternalContest contest) {

        return null; // TODO technical deficit code this

    }

    public String getOrganizationJSON(IInternalContest contest) {

        return null; // TODO technical deficit code this
    }

    public String getTeamJSON(IInternalContest contest) {
        
        return null; // TODO technical deficit code this
    }
    
    public String getTeamJSON(IInternalContest contest, Account account) {
        
        /**
         * 
         * iid 
name 
organization_id 
group_id 

         */
        
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ ");
        
        ClientId clientId = account.getClientId();

        appendPair(stringBuilder, "event", "team");
        stringBuilder.append(", ");

        appendPair(stringBuilder, "team_id", clientId.getClientNumber());
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "organization_id", "// TODO");
        stringBuilder.append(", ");
        
        appendPair(stringBuilder, "organization_id", "// TODO");
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", account.getDisplayName());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "icpc_id", "// TODO");
        stringBuilder.append(", ");


        stringBuilder.append("} ");

        return stringBuilder.toString();
    
        
        
        
        
    }

    public String getTeamMemberJSON(IInternalContest contest) {

        return null; // TODO technical deficit code this
    }

    public String getSubmissionJSON(IInternalContest contest) {
        return null; // TODO technical deficit code this
    }

    public String getJudgementJSON(IInternalContest contest) {
        return null; // TODO technical deficit code this
    }

    public String getRunJSON(IInternalContest contest) {
        return null; // TODO technical deficit code this
    }

    public String getClarificationJSON(IInternalContest contest) {

        return null; // TODO technical deficit code this
    }

    public String getAwardJSON(IInternalContest contest) {

        return null; // TODO technical deficit code this
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
            buffer.append(JSON_EOLN);
        }
        json = getJudgementTypeJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
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
            buffer.append(JSON_EOLN);
        }
        json = getOrganizationJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getTeamJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getTeamMemberJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getSubmissionJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getJudgementJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getRunJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getClarificationJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }
        json = getAwardJSON(contest);
        if (json != null) {
            buffer.append(json);
            buffer.append(JSON_EOLN);
        }

        // At this point there is a trailing , and new line, if so remove it.

        if (buffer.length() > 6) {
            System.out.println("debug 22 len = " + buffer.length());
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
        id++;
        return "pc2-" + id;
    }

    // TODO technical deficit - move these methods
    // TODO move pair methods into JsonUtilities

    private void appendPair(StringBuilder stringBuilder, String name, boolean booleanValue) {
        appendPair(stringBuilder, name, Boolean.toString(booleanValue));
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
            appendPair(stringBuilder, name, "null");
        }
    }

}
