// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
/**
 * Implementation of the CLICS 2020-03 API Specification (sort of) for the event feed
 * @author John Buck
 */
package edu.csus.ecs.pc2.clics.API202003;

import java.util.Date;
import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.services.eventFeed.WebServerPropertyUtils;
import edu.csus.ecs.pc2.services.web.ICLICSResourceConfig;

public class ResourceConfig202003 implements ICLICSResourceConfig {

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

        // add each of the enabled services to the config:

        if (wsPropUtilities.getBooleanProperty(WebServerPropertyUtils.STARTTIME_SERVICE_ENABLED_KEY, false)) {
            resConfig.register(new StarttimeService(getContest(), getController()));
            showMessage("Starting /starttime web service");
        }

        if (wsPropUtilities.getBooleanProperty(WebServerPropertyUtils.FETCH_RUN_SERVICE_ENABLED_KEY, false)) {
            resConfig.register(new FetchRunService(getContest(), getController()));
            showMessage("Starting /fetchRun web service");
        }

        // CLICS Contest API services are collective -- either all enabled or all disabled (and default to enabled if unspecified)
        if (wsPropUtilities.getBooleanProperty(WebServerPropertyUtils.CLICS_CONTEST_API_SERVICES_ENABLED_KEY, true)) {

            resConfig.register(new ContestService(getContest(), getController()));
            showMessage("Starting /contest web service");
            resConfig.register(new ScoreboardService(getContest(), getController()));
            showMessage("Starting /contest/scoreboard web service");
            resConfig.register(new LanguageService(getContest(), getController()));
            showMessage("Starting /contest/languages web service");
            resConfig.register(new TeamService(getContest(), getController()));
            showMessage("Starting /contest/teams web service");
            resConfig.register(new GroupService(getContest(), getController()));
            showMessage("Starting /contest/groups web service");
            resConfig.register(new OrganizationService(getContest(), getController()));
            showMessage("Starting /contest/organizations web service");
            resConfig.register(new JudgementTypeService(getContest(), getController()));
            showMessage("Starting /contest/judgement-types web service");
            resConfig.register(new ClarificationService(getContest(), getController()));
            showMessage("Starting /contest/clarifications web service");
            resConfig.register(new SubmissionService(getContest(), getController()));
            showMessage("Starting /contest/submissions web service");
            resConfig.register(new ProblemService(getContest(), getController()));
            showMessage("Starting /contest/problems web service");
            resConfig.register(new JudgementService(getContest(), getController()));
            showMessage("Starting /contest/judgements web service");
            resConfig.register(new RunService(getContest(), getController()));
            showMessage("Starting /contest/runs web service");
            resConfig.register(new EventFeedService(getContest(), getController()));
            showMessage("Starting /contest/event-feed web service");
            resConfig.register(new StateService(getContest(), getController()));
            showMessage("Starting /contest/state web service");
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
