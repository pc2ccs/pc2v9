// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.util;

import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;

/**
 * JSON helper interface used for creating json objects of CLICS objects
 *
 * @author John Buck
 */
public interface IJSONTool {

    /**
     * Create JSON object for submissions.
     *
     * @param submission
     * @return Json object for the submission
     */
    public ObjectNode convertToJSON(Run submission, HttpServletRequest servletRequest, SecurityContext sc);

    /**
     * Create JSON object for a group.
     *
     * @param group
     * @return Json object for the group
     */
    public ObjectNode convertToJSON(Group group);

    /**
     * Create JSON object for a language.
     *
     * @param language
     * @return Json object for the language
     */
    public ObjectNode convertToJSON(Language language);

    /**
     * Create JSON object for a clarification.
     *
     * @param clarification
     * @return Json object for the clarification
     */
    public ObjectNode convertToJSON(Clarification clarification, ClarificationAnswer clarAnswer);

    /**
     * This converts ContestInformation to a contest state json object
     *
     * @param ci - the contest information
     * @return Json object representing the current state
     */
    public ObjectNode toStateJSON(ContestInformation ci);

    /**
     * This converts ContestInformation to a json object
     *
     * @param ci - the contest information
     * @return Json object for the contest information
     */
    public ObjectNode convertToJSON(ContestInformation ci);

    /**
     * This converts a judgement to a json object
     *
     * @param judgement - the judgement
     * @return Json object for the judgement
     */
    public ObjectNode convertToJSON(Judgement judgement);

    /**
     * This converts an account to an organization json object
     *
     * @param account - the account to use to get the organization information
     * @return Json object for the organization
     */
    public ObjectNode convertOrganizationsToJSON(Account account);

    /**
     * This converts an account to a json object
     *
     * @param account - the account
     * @return Json object for the account
     */
    public ObjectNode convertToJSON(Account account);

    /**
     * This converts a problem to a json object
     *
     * @param problem - the problem
     * @param ordinal - the problem index
     * @return Json object for the problem
     */
    public ObjectNode convertToJSON(Problem problem, int ordinal);

    /**
     * This converts a Run Test Case to a json object
     *
     * @param runTestCases - all the run test cases
     * @param ordinal - index of desired run test case
     * @return Json object for the run test case
     */
    public ObjectNode convertToJSON(RunTestCase[] runTestCases, int ordinal);

    /**
     * Create JSON for judgment.
     *
     * @param submission/Run to fetch judgment from
     * @return Json object for the judgement
     */
    public ObjectNode convertJudgementToJSON(Run submission);

    /**
     * Returns the unique group id for a group.  This could be the assigned group id, or
     * the elementid if no group id is assigned.
     *
     * @param group
     * @return string representation of the group id
     */
    public static String getGroupId(Group group) {
        String id = group.getElementId().toString();
        if (group.getGroupId() != -1) {
            id = Integer.toString(group.getGroupId());
        }
        return id;
    }

    /**
     * Returns organization (institution) number as a string
     * @param account to use to fetch organization (institution) from
     * @return string representation of the organization ID (which is a number)
     */
    public static String getOrganizationId(Account account) {
        String id = account.getInstitutionCode();
        if (id.startsWith("INST-U-")) {
            id = id.substring(7);
        }
        if (id.startsWith("INST-")) {
            id = id.substring(5);
        }
        return id;
    }

    /**
     * Returns account ID as a string
     * @param account to use to the client id from
     * @return string representation of the ID (which is a number)
     */
    public static String getAccountId(Account account) {
        String id = "" + account.getClientId().getClientNumber();
        return id;
    }

    /**
     * Returns the problem shortname, if present, otherwise the elementid
     *
     * @param problem
     * @return string which is the shortname or elementid
     */
    public static String getProblemId(Problem problem) {
        String id = problem.getElementId().toString();
        // if we have a problem shortName use it, otherwise default to the internal id
        String shortName = problem.getShortName();
        if (shortName != null && !shortName.equals("")) {
            id = shortName;
        }
        return id;
    }

    /**
     * Returns the clarification id which is currently the elementid
     * Someday change to an ordinal?
     *
     * @param clarification
     * @return string which is the id to use for this clarification
     */
    public static String getClarificationId(Clarification clar) {
        String id = clar.getElementId().toString();
        return id;
    }

    /**
     * Returns the clarification answer id which is currently the elementid
     * Someday change to an ordinal?
     *
     * @param clarAns
     * @return string which is the id to use for this clarification answer
     */
    public static String getClarificationAnswerId(ClarificationAnswer clarAns) {
        String id = clarAns.getElementId().toString();
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
    public static Calendar calculateElapsedWalltime(IInternalContest contest, long elapsedMS) {

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
     * Get submission unique ID
     *
     * @param the submission's Run object
     * @return string representation of the run number
     */
    public static String getSubmissionId(Run submission) {
        return Integer.toString(submission.getNumber());
    }

    /**
     * Get judgement type (acronym).
     */
    public static String getJudgementType(Judgement judgement) {
        return judgement.getAcronym();
    }

    /**
     * Get languague unique ID
     * @param language
     * @return string with the id (eg. 'java', 'cpp', etc.)
     */
    public static String getLanguageId(Language language) {
        String key = language.getID();
        if (key == null || key.trim().equals("")) {
            key = language.getElementId().toString();
        }
        return key;
    }
}
