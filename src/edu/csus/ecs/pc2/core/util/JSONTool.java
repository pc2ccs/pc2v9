/**
 * 
 */
package edu.csus.ecs.pc2.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * @author ICPC
 *
 */
public class JSONTool {
    private ObjectMapper mapper = new ObjectMapper();

    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    /**
     * @param model
     * @param controller
     */
    public JSONTool(IInternalContest model, IInternalController controller) {
        super();
        this.model = model;
        this.controller = controller;
    }

    public ObjectNode convertToJSON(Run submission) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", submission.getElementId().toString());
        element.put("language_id", submission.getLanguageId().toString());
        element.put("problem_id", submission.getProblemId().toString());
        element.put("team_id", new Integer(submission.getSubmitter().getClientNumber()).toString());
        element.put("time", Utilities.getIso8601formatterWithMS().format(submission.getCreateDate()));
        element.put("contest_time", ContestTime.formatTimeMS(submission.getElapsedMS()));

        return element;
    }

    public ObjectNode convertToJSON(Group group) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", group.getElementId().toString());
        if (group.getGroupId() != -1) {
            element.put("icpc_id", new Integer(group.getGroupId()).toString());
        }
        element.put("name", group.getDisplayName());
        return element;
    }

    public ObjectNode convertToJSON(Language language) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", language.getElementId().toString());
        element.put("name", language.getDisplayName());
        return element;
    }

    public ObjectNode convertToJSON(Clarification clarification, ClarificationAnswer clarAnswer) {
        ObjectNode element = mapper.createObjectNode();
        String id = clarification.getElementId().toString();
        if (clarAnswer != null) {
            id = clarAnswer.getElementId().toString();
        }
        element.put("id", id);
        if (clarification.getSubmitter().getClientType().equals(ClientType.Type.TEAM) && clarAnswer == null) {
            element.put("from_team_id", new Integer(clarification.getSubmitter().getClientNumber()).toString());
        } else {
            element.set("from_team_id", null);
        }
        if (clarAnswer == null) {
            // the request goes to a judge not a team
            element.set("to_team_id", null);
            element.set("reply_to_id", null);
        } else {
            if (clarification.isSendToAll() || !clarification.getSubmitter().getClientType().equals(ClientType.Type.TEAM)) {
                element.set("to_team_id", null);
            } else {
                element.put("to_team_id", new Integer(clarification.getSubmitter().getClientNumber()).toString());
            }
            element.put("reply_to_id", clarification.getElementId().toString());
        }
        if (clarification.getProblemId().equals(model.getGeneralProblem()) || model.getCategory(clarification.getProblemId()) != null) {
            element.set("problem_id", null);
        } else {
            element.put("problem_id", clarification.getProblemId().toString());
        }
        if (clarAnswer != null) {
            ClarificationAnswer[] clarificationAnswers = clarification.getClarificationAnswers();
            int lastAnswer = clarificationAnswers.length - 1;
            element.put("text", clarificationAnswers[lastAnswer].getAnswer());
            String time = Utilities.getIso8601formatterWithMS().format(clarificationAnswers[lastAnswer].getDate());
            element.put("time", time);
            element.put("contest_time", ContestTime.formatTimeMS(clarificationAnswers[lastAnswer].getElapsedMS()));
        } else {
            element.put("text", clarification.getQuestion());
            String time = Utilities.getIso8601formatterWithMS().format(clarification.getCreateDate());
            element.put("time", time);
            element.put("contest_time", ContestTime.formatTimeMS(clarification.getElapsedMS()));
        }
        return element;
    }

    /**
     * This converts ContestInformation to a /contest object
     * 
     * @param ci
     * @return
     */
    public ObjectNode convertToJSON(ContestInformation ci) {
        String id = model.getContestIdentifier();
        String name = ci.getContestShortName();
        String formalName = ci.getContestTitle();
        if (name == null || name.equals("")) {
            name = formalName;
        }
        String duration = model.getContestTime().getContestLengthStr();
        String scoreboardFreezeDuration = ci.getFreezeTime();
        if (scoreboardFreezeDuration.length() > 2) {
            if (!scoreboardFreezeDuration.contains(":")) {
                try {
                    long seconds = Long.parseLong(scoreboardFreezeDuration);
                    scoreboardFreezeDuration = ContestTime.formatTime(seconds);
                } catch (NumberFormatException e) {
                    System.out.println("attempting to parse "+scoreboardFreezeDuration+" failed with "+e.getMessage());
                }
            }
        }
        String startTime = "null";
        if (model.getContestTime().isContestStarted()) {
            if (model.getContestTime().isContestRunning()) {
                startTime = Utilities.getIso8601formatterWithMS().format(model.getContestTime().getContestStartTime().getTime());
            }
            // else startTime is null during a pause
        } else {
            // contest has not started, check for a scheduledStartTime
            Calendar calendar = ci.getScheduledStartTime();
            if (calendar != null) {
                Date date = calendar.getTime();
                startTime = Utilities.getIso8601formatterWithMS().format(date);
            }
        }
        String penaltyTime = ci.getScoringProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO, "20");
        boolean stateRunning = model.getContestTime().isContestRunning();
        boolean skipStateFrozen = false;
        boolean stateFrozen = false;
        if (ci.getFreezeTime() != null) {
            ci.getFreezeTime();
            long elapsed = model.getContestTime().getElapsedSecs();
            long length = model.getContestTime().getContestLengthSecs();
            long freezeTime = Utilities.convertStringToSeconds(ci.getFreezeTime());
            if ((length - elapsed) > freezeTime) {
                stateFrozen = false;
            } else {
                stateFrozen = true;
            }
        } else {
            skipStateFrozen = true;
        }
        boolean stateFinal = false;
        if (model.getFinalizeData() != null) {
            stateFinal = model.getFinalizeData().isCertified();
        }

        ObjectNode element = mapper.createObjectNode();
        element.put("id", id);
        element.put("name", name);
        if (formalName != null && !formalName.equals("")) {
            element.put("formal_name", formalName);
        }
        if (startTime.equals("null")) {
            startTime = null;
        }
        element.put("start_time", startTime);
        element.put("duration", duration);
        element.put("scoreboard_freeze_duration", scoreboardFreezeDuration);
        element.put("penalty_time", penaltyTime);
        ObjectNode state = mapper.createObjectNode();
        state.put("running", stateRunning);
        if (!skipStateFrozen) {
            state.put("frozen", stateFrozen);
        }
        state.put("final", stateFinal);
        element.set("state", state);

        return element;
    }

    public ObjectNode convertToJSON(Judgement judgement) {
        ObjectNode element = mapper.createObjectNode();
        String name = judgement.getDisplayName();
        Boolean solved = false;
        Boolean penalty = true;
        if (name.equalsIgnoreCase("yes") || name.equalsIgnoreCase("accepted") || judgement.getAcronym().equalsIgnoreCase("ac")) {
            name = "Accepted";
            solved = true;
            penalty = false;
        } else {
            name = name.substring(5, name.length());
            Properties scoringProperties = model.getContestInformation().getScoringProperties();
            if (judgement.getAcronym().equalsIgnoreCase("ce") || name.toLowerCase().contains("compilation error")) {
                Object result = scoringProperties.getProperty(DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR, "0");
                if (result.equals("0")) {
                    penalty = false;
                }
            }
            if (judgement.getAcronym().equalsIgnoreCase("sv") || name.toLowerCase().contains("security violation")) {
                String result = scoringProperties.getProperty(DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION, "0");
                if (result.equals("0")) {
                    penalty = false;
                }
            }

        }
        element.put("id", getKey(judgement));
        element.put("name", name);
        element.put("penalty", penalty);
        element.put("solved", solved);
        return element;
    }

    /**
     * returns true if the value is not null and is not the empty string
     * 
     * @param value
     * @return
     */
    private boolean notEmpty(String value) {
        if (value != null && !value.equals("")) {
            return true;
        }
        return false;
    }

    public ObjectNode convertOrganizationsToJSON(Account account) {
        // this is a hack because we do not have organizations in the Model directly.
        ObjectNode element = mapper.createObjectNode();
        element.put("id", account.getInstitutionCode());
        element.put("icpc_id", account.getInstitutionCode());
        element.put("name", account.getInstitutionShortName());
        if (notEmpty(account.getInstitutionName())) {
            element.put("formal_name", account.getInstitutionName());
        }
        if (notEmpty(account.getCountryCode()) && !account.getCountryCode().equals("XXX")) {
            element.put("country", account.getCountryCode());
        }
        return element;
    }

    public ObjectNode convertToJSON(Account account) {
        ObjectNode element = mapper.createObjectNode();
        // TODO multi-site with overlapping teamNumbers?
        element.put("id", new Integer(account.getClientId().getClientNumber()).toString());
        if (notEmpty(account.getExternalId())) {
            element.put("icpc_id", account.getExternalId());
        }
        element.put("name", account.getTeamName());
        if (notEmpty(account.getInstitutionCode()) && !account.getInstitutionCode().equals("undefined")) {
            element.put("organization_id", account.getInstitutionCode());
        }
        if (account.getGroupId() != null) {
            element.put("group_id", account.getGroupId().toString());
        }
        return element;
    }

    public ObjectNode convertToJSON(Problem problem, int ordinal) {
        ObjectNode element = mapper.createObjectNode();
        // {"id":"asteroids","label":"A","name":"Asteroid Rangers","ordinal":1,"color":"blue","rgb":"#00f","test_data_count":10}
        String id = problem.getElementId().toString();
        // if we have a problem shortName use it, otherwise default to the internal id
        if (notEmpty(problem.getShortName())) {
            id = problem.getShortName();
        }
        element.put("id", id);
        element.put("label", problem.getLetter());
        element.put("name", problem.getDisplayName());
        element.put("ordinal", ordinal);
        // optional attribute color
        if (notEmpty(problem.getColorName())) {
            element.put("color", problem.getColorName());
        }
        // optional attribute rgb
        if (notEmpty(problem.getColorRGB())) {
            element.put("rgb", problem.getColorRGB());
        }
        element.put("test_data_count", problem.getNumberTestCases());
        return element;
    }

    /**
     * Return wall time for input elapsed time in ms.
     *
     * Calculates based on elapsed time plus contest start time
     *
     * @param contest
     * @param elapsedMS
     *            - elapsed ms when submission submitted
     * @return wall time for run.
     */
    private Calendar calculateElapsedWalltime(IInternalContest contest, long elapsedMS) {

        ContestTime time = contest.getContestTime();
        if (time.getElapsedMins() > 0) {

            Calendar contestStart = time.getContestStartTime();

            long ms = contestStart.getTimeInMillis();

            ms += elapsedMS; // add elapsed time

            // create wall time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.setTimeInMillis(ms);
            return calendar;

        } else {
            return null;
        }

    }

    public ObjectNode convertJudgementToJSON(Run submission) {
        // {"id":"189549","submission_id":"wf2017-32163123xz3132yy","judgement_type_id":"CE","start_time":"2014-06-25T11:22:48.427+01",
        // "start_contest_time":"1:22:48.427","end_time":"2014-06-25T11:23:32.481+01","end_contest_time":"1:23:32.481"}
        ObjectNode element = mapper.createObjectNode();
        element.put("id", submission.getElementId().toString());
        element.put("submission_id", submission.getElementId().toString());
        // TODO I think this is suppose to be when the judge retrieves it, not the submission time.
        element.put("start_time", Utilities.getIso8601formatterWithMS().format(submission.getCreateDate()));
        element.put("start_contest_time", ContestTime.formatTimeMS(submission.getElapsedMS()));
        if (submission.isJudged()) {
            JudgementRecord judgementRecord = submission.getJudgementRecord();
            Judgement judgement = model.getJudgement(judgementRecord.getJudgementId());
            // only print it's judgement and end times if this is the final judgement
            if (!judgementRecord.isPreliminaryJudgement()) {
                element.put("judgement_type_id", getKey(judgement));
                // TODO verify these times are consistent
                Calendar wallElapsed = calculateElapsedWalltime(model, judgementRecord.getWhenJudgedTime() * 60000);
                element.put("end_time", Utilities.getIso8601formatter().format(wallElapsed.getTime()));
                // when judged is in minutes convert to milliseconds
                element.put("end_contest_time", ContestTime.formatTimeMS(judgementRecord.getWhenJudgedTime() * 60000));
            }
        }
        return element;
    }

    public String getKey(Judgement judgement) {
        return judgement.getAcronym();
    }

    public ObjectNode convertToJSON(RunTestCase[] runTestCases, int ordinal) {
        // {"id":"1312","judgement_id":"189549","ordinal":28,"judgement_type_id":"TLE",
        // "time":"2014-06-25T11:22:42.420+01","contest_time":"1:22:42.420"}
        RunTestCase run = runTestCases[ordinal];
        ObjectNode element = mapper.createObjectNode();
        element.put("id", run.getElementId().toString());
        element.put("judgement_id", run.getRunElementId().toString());
        element.put("ordinal", ordinal);
        element.put("judgement_type_id", getKey(model.getJudgement(run.getJudgementId())));
        // TODO this is the local time on the judge
        element.put("time", Utilities.getIso8601formatterWithMS().format(run.getDate().getTime()));
        // note this is the contest_time as seen on the judge
        element.put("contest_time", ContestTime.formatTimeMS(run.getConestTimeMS()));
        return element;
    }
}
