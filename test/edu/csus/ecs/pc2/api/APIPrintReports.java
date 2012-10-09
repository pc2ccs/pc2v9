package edu.csus.ecs.pc2.api;

import edu.csus.ecs.pc2.api.apireports.PrintAllProblemDetails;
import edu.csus.ecs.pc2.api.apireports.PrintClarification;
import edu.csus.ecs.pc2.api.apireports.PrintClarifications;
import edu.csus.ecs.pc2.api.apireports.PrintClockInfo;
import edu.csus.ecs.pc2.api.apireports.PrintContestRunning;
import edu.csus.ecs.pc2.api.apireports.PrintContestTitle;
import edu.csus.ecs.pc2.api.apireports.PrintGetContestSC;
import edu.csus.ecs.pc2.api.apireports.PrintGroups;
import edu.csus.ecs.pc2.api.apireports.PrintJudgements;
import edu.csus.ecs.pc2.api.apireports.PrintLanguages;
import edu.csus.ecs.pc2.api.apireports.PrintLocalHostName;
import edu.csus.ecs.pc2.api.apireports.PrintLocalPortNumber;
import edu.csus.ecs.pc2.api.apireports.PrintLoggedInSC;
import edu.csus.ecs.pc2.api.apireports.PrintMyClient;
import edu.csus.ecs.pc2.api.apireports.PrintMyClientSC;
import edu.csus.ecs.pc2.api.apireports.PrintProblems;
import edu.csus.ecs.pc2.api.apireports.PrintRun;
import edu.csus.ecs.pc2.api.apireports.PrintRuns;
import edu.csus.ecs.pc2.api.apireports.PrintSiteName;
import edu.csus.ecs.pc2.api.apireports.PrintSites;
import edu.csus.ecs.pc2.api.apireports.PrintStandingForUser;
import edu.csus.ecs.pc2.api.apireports.PrintStandings;
import edu.csus.ecs.pc2.api.apireports.PrintTeams;

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
