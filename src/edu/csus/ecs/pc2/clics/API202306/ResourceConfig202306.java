// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * Implementation of the CLICS 2020-03 API Specification (sort of) for the event feed
 * @author John Buck
 */
package edu.csus.ecs.pc2.clics.API202306;

import java.util.Date;
import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.eventFeed.WebServerPropertyUtils;
import edu.csus.ecs.pc2.services.web.ICLICSResourceConfig;

public class ResourceConfig202306 implements ICLICSResourceConfig {

    public static final String CLICS_API_VERSION = "2023-06";
    public static final String CLICS_API_VERSION_URL = "https://ccs-specs.icpc.io/2023-06/contest_api";
    
    private IInternalContest contest;
    
    private IInternalController controller;
    
    private WebServerPropertyUtils wsPropUtilities;

    private Log log = null;
   
    /**
     * {@inheritDoc}
     */
    public ResourceConfig getResourceConfig(IInternalContest aContest, IInternalController aController, WebServerPropertyUtils wsPropUtil) {

        setContestAndController(aContest, aController);
        wsPropUtilities = wsPropUtil;
        
        // create and (empty) ResourceConfig
        ResourceConfig resConfig = new ResourceConfig();
        resConfig.register(RolesAllowedDynamicFeature.class);

        // CLICS Contest API services are collective -- either all enabled or all disabled (and default to enabled if unspecified)
        if (wsPropUtilities.getBooleanProperty(WebServerPropertyUtils.CLICS_CONTEST_API_SERVICES_ENABLED_KEY, true)) {

            resConfig.register(new ContestService(getContest(), getController()));
            showMessage("Starting /contests web service");
            resConfig.register(new AccessService(getContest(), getController()));
            showStartingMessage("access web service");
            resConfig.register(new ScoreboardService(getContest(), getController()));
            showStartingMessage("scoreboard web service");
            resConfig.register(new LanguageService(getContest(), getController()));
            showStartingMessage("languages web service");
            resConfig.register(new TeamService(getContest(), getController()));
            showStartingMessage("teams web service");
            resConfig.register(new GroupService(getContest(), getController()));
            showStartingMessage("groups web service");
            resConfig.register(new OrganizationService(getContest(), getController()));
            showStartingMessage("organizations web service");
            resConfig.register(new JudgementTypeService(getContest(), getController()));
            showStartingMessage("judgement-types web service");
            resConfig.register(new ClarificationService(getContest(), getController()));
            showStartingMessage("clarifications web service");
            resConfig.register(new SubmissionService(getContest(), getController()));
            showStartingMessage("submissions web service");
            resConfig.register(new ProblemService(getContest(), getController()));
            showStartingMessage("problems web service");
            resConfig.register(new JudgementService(getContest(), getController()));
            showStartingMessage("judgements web service");
            resConfig.register(new RunService(getContest(), getController()));
            showStartingMessage("runs web service");
            resConfig.register(new EventFeedService(getContest(), getController()));
            showStartingMessage("event-feed web service");
            resConfig.register(new StateService(getContest(), getController()));
            showStartingMessage("state web service");
            resConfig.register(new VersionService(getContest(), getController()));
            showMessage("Starting / endpoint for version web service");

        }

        return resConfig;
    }

    // Maybe these should be in a base class that all API's inherit?
    private void setContestAndController(IInternalContest inContest, IInternalController inController) {

        this.contest = inContest;
        this.controller = inController;
        this.log = controller.getLog();
    }

    private void showMessage(final String message, Exception ex) {
        getLog().log(Log.INFO, message, ex);
        System.out.println(new Date() + " " + message);
        ex.printStackTrace();
    }

    private void showMessage(String message) {
        System.out.println(new Date() + " " + message);
        getLog().info(message);
    }

    private void showStartingMessage(String message) {
        String startingMessage = "Starting /contests/" + contest.getContestIdentifier() + "/" + message;
        System.out.println(new Date() + " " + startingMessage);
        getLog().info(startingMessage);
    }

    private Logger getLog() {
        return log;
    }

    public IInternalContest getContest() {
        return contest;
    }

    public IInternalController getController() {
        return controller;
    }

}
