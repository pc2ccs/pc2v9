package edu.csus.ecs.pc2.core;

import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Contest Loader.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestImporter {
    
    /**
     * Load contest information to the server.
     * 
     * Updates the current contest with data from the input contest.
     * 
     * @param theController
     * @param inContest
     */
    public void sendContestSettingsToServer(IInternalController theController, IInternalContest inContest) {
        // TODO CCS

        if (inContest.getContestInformation().getContestTitle() != null) {
            ContestInformation contestInformation = inContest.getContestInformation();
            contestInformation.setContestTitle(inContest.getContestInformation().getContestTitle());

            theController.updateContestInformation(contestInformation);
            // TODO CCS update contest title
        }

        // TODO CCS need to update all properties from YAML contest header
        // TODO CCS update contest length
        // TODO CCS update contest freeze time

        for (Site site : inContest.getSites()) {
            Site existingSite = inContest.getSite(site.getSiteNumber());
            if (existingSite != null) {
                System.out.println("Not overwriting site: " + site.getSiteNumber() + " " + site.getDisplayName());
            } else {
                theController.addNewSite(site);
            }
        }

        for (Language language : inContest.getLanguages()) {
            theController.addNewLanguage(language);
        }

        for (Problem problem : inContest.getProblems()) {
            ProblemDataFiles problemDataFiles = inContest.getProblemDataFile(problem);
            if (problemDataFiles != null) {
                theController.addNewProblem(problem, problemDataFiles);
            } else {
                theController.addProblem(problem);
            }
        }
        
        // TODO CCS Add categories, add updateSettings to be a more
        // generic way to update multiple settings
//        Category [] categories = inContest.getCategories();
//        theController.updateSettings(categories);

        theController.addNewAccounts(inContest.getAccounts());
    }
}
