package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;

/**
 * Sample Code for API.
 * 
 * This class is not intended as a JUnit test, it is a syntax check for the API samples in the Java doc in the API classes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class APISampleCode {

    /**
     * Sample code for ServerConnection
     */
    @SuppressWarnings("unused")
    public void serverConnectionSample() {

        String login = "team4";
        String password = "team4";
        try {
            ServerConnection serverConnection = new ServerConnection();
            IContest contest = serverConnection.login(login, password);
            // ... code here to invoke methods in "contest";
            serverConnection.logoff();
        } catch (LoginFailureException e) {
            System.out.println("Could not login because " + e.getMessage());
        } catch (NotLoggedInException e) {
            // TODO Auto-generated catch block
            System.out.println("Unable to execute API method");
            e.printStackTrace();
        }

    }

    // IContest Samples

    /**
     * getTeams() sample.
     * 
     * @param contest
     */
    public void getTeamsSample(IContest contest) {

        for (ITeam team : contest.getTeams()) {
            String teamName = team.getDisplayName();
            int siteNumber = team.getSiteNumber();
            String groupName = team.getGroup().getName();
            System.out.println(teamName + " Site: " + siteNumber + " Group: " + groupName);
        }

    }

    /**
     * getLanguages() sample.
     * 
     * @param contest
     */
    public void getLanguagesSample(IContest contest) {

        for (ILanguage language : contest.getLanguages()) {
            System.out.println(language.getName());
        }

    }

    /**
     * getProblems() sample.
     * 
     * @param contest
     */
    public void getProblemSample(IContest contest) {

        for (IProblem problem : contest.getProblems()) {
            System.out.println(problem.getName());
        }

    }

    /**
     * getJudgements() sample.
     * 
     * @param contest
     */
    public void getJudgmentsSample(IContest contest) {

        for (IJudgement judgement : contest.getJudgements()) {
            System.out.println(judgement.getName());
        }

    }

    /**
     * getRuns() sample.
     * 
     * @param contest
     */
    public void getRunsSample(IContest contest) {

        for (IRun run : contest.getRuns()) {

            System.out.println("Run " + run.getNumber() + " from site " + run.getSiteNumber());
            System.out.println("    submitted at " + run.getSubmissionTime() + " minutes by " + run.getTeam().getDisplayName());
            System.out.println("    For problem " + run.getProblem().getName());
            System.out.println("    Written in " + run.getLanguage().getName());

            if (run.isFinalJudged()) {
                System.out.println("    Judgement: " + run.getJudgementName());
            } else {
                System.out.println("    Judgement: not judged yet ");
            }
        }
    }

    /**
     * getStandings() samples.
     * 
     * @param contest
     */
    public void getStandingsSample(IContest contest) {

        for (IStanding standingRank : contest.getStandings()) {
            String displayName = standingRank.getClient().getDisplayName();
            System.out.printf(" %3d %-35s %2d %4d", standingRank.getRank(), displayName, standingRank.getNumProblemsSolved(), standingRank.getPenaltyPoints());
        }
    }
}
