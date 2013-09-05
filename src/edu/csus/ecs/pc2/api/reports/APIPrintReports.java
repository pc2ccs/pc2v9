package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IClient;

/**
 * List of API Reports.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class APIPrintReports {

    private APIAbstractTest[] reportsList = { //
            new PrintRuns(), //
            new PrintRun(), //
            new PrintMyClient(), //
            new PrintSites(), //
            new PrintClockInfo(), //
            new PrintJudgements(), //
            new PrintContestTitle(), //
            new PrintClarification(), //
            new PrintTeams(), //
            new PrintClarificationCategories(), //
            new PrintProblems(), //
            new PrintLanguages(), //
            new PrintStandings(), //
            new PrintAllProblemDetails(), //
            new PrintStandingForUser(), //
            new PrintClarifications(), //
            new PrintContestRunning(), //
            new PrintSiteName(), //
            new PrintGroups(), //
            new PrintLocalHostName(), //
            new PrintLocalPortNumber(), //
            new PrintMyClientSC(), //
            new PrintGetContestSC(), //
            new PrintLoggedInSC(), //
    };

    public static void printClient(APIAbstractTest abstractTest, String title, IClient client) {
        abstractTest.print(title + " login=" + client.getLoginName());
        abstractTest.print(", name=" + client.getDisplayName());
        abstractTest.print(", type=" + client.getType());
        abstractTest.print(", account#=" + client.getAccountNumber());
        abstractTest.print(", site=" + client.getSiteNumber());
        abstractTest.println();
    }

    public static String trueFalseString(boolean value, String trueString, String falseString) {
        if (value) {
            return trueString;
        } else {
            return falseString;
        }
    }

    public APIAbstractTest[] getReportsList() {
        return reportsList;
    }
}
