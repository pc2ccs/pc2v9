// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.util.IJSONTool;

/**
 * JSON for pc2 classes.
 *
 * @author Troy Boudreau <boudreat@ecs.csus.edu>
 */
public class JSONTool implements IJSONTool {

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
     * @param submission to convert
     * @param servletRequest Web request
     * @param sc Web security context
     */
    @Override
    public ObjectNode convertToJSON(Run submission, HttpServletRequest servletRequest, SecurityContext sc) {
        Set<String> exceptProps = new HashSet<String>();
        ObjectNode element = null;

        CLICSSubmission cSub = new CLICSSubmission(model, submission);
        cSub.getExceptProps(exceptProps);
        try {
            // for this file, create filter to omit unused properties (height/width in this case)
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
            mapper.setFilters(fp);
            element = mapper.convertValue(cSub, ObjectNode.class);
            mapper.setFilters(null);
        } catch (Exception e) {
            if(controller != null) {
                controller.getLog().log(Level.WARNING, "Error creating JSON for submission " + submission.getElementId().toString() + " " + e.getMessage());
            }
        }

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

    @Override
    public ObjectNode convertToJSON(Group group) {
        return(mapper.convertValue(new CLICSGroup(group), ObjectNode.class));
    }

    @Override
    public ObjectNode convertToJSON(Language language) {
        return(mapper.convertValue(new CLICSLanguage(language), ObjectNode.class));
    }

    @Override
    public ObjectNode convertToJSON(Clarification clarification, ClarificationAnswer clarAnswer) {
        return(mapper.convertValue(new CLICSClarification(model, clarification, clarAnswer), ObjectNode.class));
    }

    /**
     * This converts ContestInformation to a /state object
     *
     * @param ci
     * @return
     */
    @Override
    public ObjectNode toStateJSON(ContestInformation ci) {
        return(mapper.convertValue(new CLICSContestState(model, ci), ObjectNode.class));
    }

    /**
     * This converts ContestInformation to a /contest object
     *
     * @param ci
     * @return
     */
    @Override
    public ObjectNode convertToJSON(ContestInformation ci) {
        return(mapper.convertValue(new CLICSContestInfo(model, ci), ObjectNode.class));
    }

    @Override
    public ObjectNode convertToJSON(Judgement judgement) {
        return(mapper.convertValue(new CLICSJudgmentType(model, judgement), ObjectNode.class));
    }

    @Override
    public ObjectNode convertOrganizationsToJSON(Account account) {
        // this is a hack because we do not have organizations in the Model directly.
        // [0]:1329 [1]:New York University [2]:NYU
        // but only need [1] & [2].  We get the rest from the a typical account.
        String [] fields = new String[3];

        fields[0] = account.getInstitutionCode();   // Not used in CLICSOrganization; included for completeness
        fields[1] = account.getInstitutionName();
        fields[2] = account.getInstitutionShortName();
        return(mapper.convertValue(new CLICSOrganization(fields[0], account, fields), ObjectNode.class));
    }

    @Override
    public ObjectNode convertToJSON(Account account) {
        return(mapper.convertValue(new CLICSTeam(model, account), ObjectNode.class));
    }

    @Override
    public ObjectNode convertToJSON(Problem problem, int ordinal) {
        return(mapper.convertValue(new CLICSProblem(model, problem, ordinal), ObjectNode.class));
    }

    /**
     * Create JSON for judgement of a submission in the contest
     *
     * @param submission
     */
    @Override
    public ObjectNode convertJudgementToJSON(Run submission) {
        Set<String> exceptProps = new HashSet<String>();
        ObjectNode element = null;

        CLICSRun cRun = new CLICSRun(model, controller, submission, exceptProps);
        try {
            // for this judgment, create filter to omit unused/bad properties (max_run_time in this case)
            SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(exceptProps);
            FilterProvider fp = new SimpleFilterProvider().addFilter("rtFilter", filter).setFailOnUnknownId(false);
            mapper.setFilters(fp);
            element = mapper.convertValue(cRun, ObjectNode.class);
            mapper.setFilters(null);
        } catch (Exception e) {
            if(controller != null) {
                controller.getLog().log(Level.WARNING, "Error creating JSON for judgment " + submission.getElementId().toString() + " " + e.getMessage());
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

    @Override
    public ObjectNode convertToJSON(RunTestCase[] runTestCases, int ordinal) {
        return(mapper.convertValue(new CLICSTestCase(model, runTestCases[ordinal]), ObjectNode.class));
    }

    public Group getGroupFromNumber(String groupnum) {
        for(Group group: model.getGroups()) {
            if (IJSONTool.getGroupId(group).equals(groupnum)) {
                return(group);
            }
        }
        return(null);
    }

    private boolean notEmpty(String str) {
        return(str != null && !str.equals(""));
    }
}
