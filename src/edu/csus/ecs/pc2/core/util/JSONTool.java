// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
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
 * JSON for pc2 classes.
 * 
 * @author Troy Boudreau <boudreat@ecs.csus.edu>
 */
public class JSONTool {
    
    /**
     * A default localhost location.
     */
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

    /**
     * Create JSON for submissions.
     * 
     * @param submission
     */
    public ObjectNode convertToJSON(Run submission, HttpServletRequest servletRequest, SecurityContext sc) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", getSubmissionId(submission));
        element.put("language_id", getLanguageId(model.getLanguage(submission.getLanguageId())));
        element.put("problem_id", getProblemId(model.getProblem(submission.getProblemId())));
        element.put("team_id", new Integer(submission.getSubmitter().getClientNumber()).toString());
        element.put("time", Utilities.getIso8601formatterWithMS().format(submission.getCreateDate()));
        element.put("contest_time", Utilities.formatDuration(submission.getElapsedMS()));
        if (submission.getEntryPoint() != null) {
            element.put("entry_point", new String(submission.getEntryPoint()));
        }
        
        
        
        // FIXME we need separate event feeds for public and admin/analyst
        // FIXME perhaps change sc to a boolean for public or not?
        // if (servletRequest != null && (sc != null && sc.isUserInRole("admin") || sc.isUserInRole("analyst"))) {
        
        
        // TODO shadow add time and mime elements to submission
//        element.put("mime","application/zip");
        
        String pathValue = "/submissions/" + submission.getNumber() + "/files";
        
        ObjectMapper mymapper = new ObjectMapper();
        ArrayNode arrayNode = mymapper.createArrayNode();
        ObjectNode objectNode = mymapper.createObjectNode();
        objectNode.put("href", pathValue);
        arrayNode.add(objectNode);
        element.set("files", arrayNode);

        return element;
    }

    /**
     * Return Primary CCS URL pc2 setting.
     * @return empty string if settings is null or empty string, otherwise API base url
     */
    private String getAPIURL() {
        
        String url = "";
        ContestInformation contestInformation = model.getContestInformation();
        String primaryCCS_URL = contestInformation.getPrimaryCCS_URL();
        if (! StringUtilities.isEmpty(primaryCCS_URL)){
            url = primaryCCS_URL.trim();
        }
        
        return url;
    }

    private void logWarn(String string, Exception e) {
        
        System.err.println(string);
        e.printStackTrace(System.err);
        
        Log log = controller.getLog();
        log.log(Level.WARNING, string, e);
    }

    public ObjectNode convertToJSON(Group group) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", getGroupId(group));
        if (group.getGroupId() != -1) {
            element.put("icpc_id", Integer.toString(group.getGroupId()));
        }
        element.put("name", group.getDisplayName());
        return element;
    }

    public String getGroupId(Group group) {
        String id = group.getElementId().toString();
        if (group.getGroupId() != -1) {
            id = Integer.toString(group.getGroupId());
        }
        return id;
    }

    public ObjectNode convertToJSON(Language language) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", getLanguageId(language));
        element.put("name", language.getDisplayName());
        return element;
    }

    public ObjectNode convertToJSON(Clarification clarification, ClarificationAnswer clarAnswer) {
        ObjectNode element = mapper.createObjectNode();

        // SOMEDAY change id to a orginal
        String id = clarification.getElementId().toString();
        if (clarAnswer != null && clarAnswer.getElementId() != null) {
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
            element.put("problem_id", getProblemId(model.getProblem(clarification.getProblemId())));
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
     * This converts ContestInformation to a /state object
     * 
     * @param ci
     * @return
     */
    public ObjectNode toStateJSON(ContestInformation ci) {
        ObjectNode element = mapper.createObjectNode();
        String startTime = null;
        if (model.getContestTime().isContestStarted()) {
            startTime = Utilities.getIso8601formatterWithMS().format(model.getContestTime().getContestStartTime().getTime());
            element.put("started", startTime);
            if (model.getContestTime().isPastEndOfContest()) {
                Calendar endedDate = calculateElapsedWalltime(model, model.getContestTime().getContestLengthMS());
                if (endedDate != null) {
                    element.put("ended", Utilities.getIso8601formatterWithMS().format(endedDate.getTimeInMillis()));
                }
            }
            String scoreboardFreezeDuration = ci.getFreezeTime();
            if (scoreboardFreezeDuration != null && scoreboardFreezeDuration.trim().length() > 0) {
                long elapsed = model.getContestTime().getElapsedSecs();
                long freezeTime = Utilities.getFreezeTime(model);
                // FIXME this date should be stored in ContestInformation
                if (elapsed >= freezeTime) {
                    Calendar freezeDate = calculateElapsedWalltime(model, freezeTime * 1000);
                    if (freezeDate != null) {
                        element.put("frozen", Utilities.getIso8601formatterWithMS().format(freezeDate.getTime()));
                    }
                }
                if (ci.isUnfrozen()) {
                    Date thawedDate = model.getContestInformation().getThawed();
                    if (thawedDate != null) {
                        element.put("thawed", Utilities.getIso8601formatterWithMS().format(thawedDate));
                    }
                }
            }
            // FIXME this should only be showed if the contest is thawed for public users
            String finalizedDate = null;
            if (model.getFinalizeData() != null) {
                finalizedDate = Utilities.getIso8601formatterWithMS().format(model.getFinalizeData().getCertificationDate());
            }
            if (finalizedDate != null) {
                element.put("finalized", finalizedDate);
            }
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
                    System.out.println("attempting to parse " + scoreboardFreezeDuration + " failed with " + e.getMessage());
                }
            }
        }
        String startTime = "null";
        if (model.getContestTime().isContestStarted()) {
            startTime = Utilities.getIso8601formatterWithMS().format(model.getContestTime().getContestStartTime().getTime());
        } else {
            // contest has not started, check for a scheduledStartTime
            Calendar calendar = ci.getScheduledStartTime();
            if (calendar != null) {
                Date date = calendar.getTime();
                startTime = Utilities.getIso8601formatterWithMS().format(date);
            }
        }
        int penaltyTime = Integer.valueOf(ci.getScoringProperties().getProperty(DefaultScoringAlgorithm.POINTS_PER_NO, "20"));

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
            if (judgement.getAcronym().equalsIgnoreCase("ce") || name.toLowerCase().contains("compilation error")
                    || name.toLowerCase().contains("compile error")) {
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
        element.put("id", getJudgementType(judgement));
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
        String id = getOrganizationId(account);
        element.put("id", id);
        element.put("icpc_id", id);
        element.put("name", account.getInstitutionShortName());
        if (notEmpty(account.getInstitutionName())) {
            element.put("formal_name", account.getInstitutionName());
        }
        if (notEmpty(account.getCountryCode()) && !account.getCountryCode().equals("XXX")) {
            element.put("country", account.getCountryCode());
        }
        return element;
    }

    public String getOrganizationId(Account account) {
        String id = account.getInstitutionCode();
        if (id.startsWith("INST-U-")) {
            id = id.substring(7);
        }
        if (id.startsWith("INST-")) {
            id = id.substring(5);
        }
        return id;
    }

    public ObjectNode convertToJSON(Account account) {
        ObjectNode element = mapper.createObjectNode();
        // SOMEDAY spec should be updated for overlapping multi-site team Ids, this will need to be updated at that time
        element.put("id", new Integer(account.getClientId().getClientNumber()).toString());
        if (notEmpty(account.getExternalId())) {
            element.put("icpc_id", account.getExternalId());
        }
        element.put("name", account.getDisplayName());
        if (notEmpty(account.getInstitutionCode()) && !account.getInstitutionCode().equals("undefined")) {
            element.put("organization_id", getOrganizationId(account));
        }
        if (account.getGroupId() != null) {
            ArrayNode groupIds = mapper.createArrayNode();
            // FIXME eventually accounts should have more then 1 groupId, make sure add them
            groupIds.add(getGroupId(model.getGroup(account.getGroupId())));
            element.set("group_ids", groupIds);
        }
        return element;
    }

    public ObjectNode convertToJSON(Problem problem, int ordinal) {
        ObjectNode element = mapper.createObjectNode();
        // {"id":"asteroids","label":"A","name":"Asteroid Rangers","ordinal":1,"color":"blue","rgb":"#00f","test_data_count":10}
        String id = getProblemId(problem);
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

    public String getProblemId(Problem problem) {
        String id = problem.getElementId().toString();
        // if we have a problem shortName use it, otherwise default to the internal id
        if (notEmpty(problem.getShortName())) {
            id = problem.getShortName();
        }
        return id;
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

    /**
     * Create JSON for judgement.
     * 
     * @param submission
     */
    public ObjectNode convertJudgementToJSON(Run submission) {
        // {"id":"189549","submission_id":"wf2017-32163123xz3132yy","judgement_type_id":"CE","start_time":"2014-06-25T11:22:48.427+01",
        // "start_contest_time":"1:22:48.427","end_time":"2014-06-25T11:23:32.481+01","end_contest_time":"1:23:32.481"}
        ObjectNode element = mapper.createObjectNode();
        element.put("id", submission.getElementId().toString());
        element.put("submission_id", getSubmissionId(submission));
        // SOMEDAY this is suppose to be when the judge retrieves it, not the submission time.
        element.put("start_time", Utilities.getIso8601formatterWithMS().format(submission.getCreateDate()));
        element.put("start_contest_time", ContestTime.formatTimeMS(submission.getElapsedMS()));
        if (submission.isJudged()) {

            JudgementRecord judgementRecord = submission.getJudgementRecord();
            
            // only output its judgement and end times if this is the final judgement
            if (!judgementRecord.isPreliminaryJudgement()) {

                // Fetch judgement_type_id from judgement acronym
                String judgmentAcronym = getJudgementAcronymn(judgementRecord);
                element.put("judgement_type_id", judgmentAcronym);
                
                Calendar wallElapsed = calculateElapsedWalltime(model, judgementRecord.getWhenJudgedTime() * 60000);
                if (wallElapsed != null) {
                    element.put("end_time", Utilities.getIso8601formatter().format(wallElapsed.getTime()));
                } // is null if there are no elapsedMinutes in the contest
                  // when judged is in minutes convert to milliseconds
                element.put("end_contest_time", ContestTime.formatTimeMS(judgementRecord.getWhenJudgedTime() * 60000));
            }
        }
        
        return element;
    }

    /**
     * Fetch Judgement Acronym for run judgement.
     * 
     * @param judgementRecord
     * @return judgement acronym.
     */
    private String getJudgementAcronymn(JudgementRecord judgementRecord) {
        
        ElementId judgementId = judgementRecord.getJudgementId();
        Judgement judgement = model.getJudgement(judgementId);
        return judgement.getAcronym();
    }

    public String getSubmissionId(Run submission) {
        return Integer.toString(submission.getNumber());
    }

    /**
     * Get judgement type (acronym).
     */
    public String getJudgementType(Judgement judgement) {
        return judgement.getAcronym();
    }

    public String getLanguageId(Language language) {
        String key = language.getID();
        if (key == null || key.trim().equals("")) {
            key = language.getElementId().toString();
        }
        return key;
    }

    public ObjectNode convertToJSON(RunTestCase[] runTestCases, int ordinal) {
        // {"id":"1312","judgement_id":"189549","ordinal":28,"judgement_type_id":"TLE",
        // "time":"2014-06-25T11:22:42.420+01","contest_time":"1:22:42.420"}
        RunTestCase run = runTestCases[ordinal];
        ObjectNode element = mapper.createObjectNode();
        element.put("id", run.getElementId().toString());
        element.put("judgement_id", run.getRunElementId().toString());
        // CLICS spec says this has to start at 1 not 0 (ACPC 2022 DJ Shadow)
        // The RunTestCase already has a 1-based test case number, we can use
        // that since it really exactly what we want here.  It is always in the
        // range 1 through #_of_test_cases.
        element.put("ordinal", run.getTestNumber());
        element.put("judgement_type_id", getJudgementType(model.getJudgement(run.getJudgementId())));
        // SOMEDAY get the time from the server instead of the judge
        element.put("time", Utilities.getIso8601formatterWithMS().format(run.getDate().getTime()));
        // note this is the contest_time as seen on the judge
        element.put("contest_time", ContestTime.formatTimeMS(run.getConestTimeMS()));
        return element;
    }
}
