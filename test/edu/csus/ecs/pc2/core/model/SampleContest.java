package edu.csus.ecs.pc2.core.model;

import java.util.Properties;

import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Create Sample Contest and Controller.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SampleContest {

    /**
     * Create a new Site class instance.
     * 
     * @param siteNumber
     *            site number
     * @param siteName
     *            title for site
     * @param hostName
     *            if null, assigned to localhost
     * @param portNumber
     *            if 0 assigned 50002 + (siteNumber-1)* 1000
     * @return
     */
    public Site createSite(int siteNumber, String siteName, String hostName, int portNumber) {
        Site site = new Site(siteName, siteNumber);

        Properties props = new Properties();
        if (hostName == null) {
            props.put(Site.IP_KEY, "localhost");
        }

        if (portNumber == 0) {
            portNumber = 50002 + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
    }

    /**
     * Create an instance of contest with languages, problems, teams and judges.
     * 
     * @param numSites
     *            number of sites to create
     * @param numTeams
     *            number of teams to create
     * @param numJudges
     *            number of judges to create
     * @return
     */
    public IContest createContest (int siteNumber, int numSites, int numTeams, int numJudges) {

        String[] languages = { "Java", "C++", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing", "Faulty Towers", "London Bridge", "Finnigans Bluff" };

        Contest contest = new Contest();
        for (int i = 0; i < numSites; i++) {
            Site site = createSite(i + 1, "Site " + (i + 1), null, 0);
            contest.addSite(site);
        }

        for (String langName : languages) {
            Language language = new Language(langName);
            contest.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            contest.addProblem(problem);
        }

        if (numTeams > 0) {
            contest.generateNewAccounts(Type.TEAM.toString(), numTeams, true);
        }

        if (numJudges > 0) {
            contest.generateNewAccounts(Type.JUDGE.toString(), numJudges, true);
        }
        
        ContestTime contestTime = new ContestTime(siteNumber);
        contest.addContestTime(contestTime);
        
        return contest;
    }

    public IController createController(IContest contest, boolean isServer, boolean isRemote) {

        // Start site 1
        Controller controller = new Controller(contest);
        controller.setUsingMainUI(false);

        if (isServer) {
            controller.setContactingRemoteServer(isRemote);
            String[] argsSiteOne = { "--server" };
            controller.start(argsSiteOne);
        } else {
            controller.start(null);

        }

        return controller;

    }

}
