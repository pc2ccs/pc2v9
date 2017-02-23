package edu.csus.ecs.pc2.core.report;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author pc2@ecs.csus.edu
 */

public class ContestSummaryReportsTest extends AbstractTestCase {

    /**
     * Test report names
     * @throws Exception
     */
    public void testReportNames() throws Exception {

        String[] expectedNames = {
                //
                "Fastest Solutions Summary", //
                "Fastest Solutions Per Problem", //
                "Balloons Summary", //
                "Contest XML", //
                "Contest Analysis", //
                "Solutions By Problem", //
                "Runs grouped by team", //
                "Submissions by Language", //
                "Standings XML ", //
                "Runs", //
                "Clarifications", //
                "Problems", //
                "Languages", //
                "Standings Web Pages", //
                "Evaluations", //
                "Runs (Version 8 content and format)", //
                "Contest Settings", //
                "JSON Standings", //
                "groups.tsv", //
                "scoreboard.tsv", //
                "submissions.tsv", //
                "ICPC Tools Event Feed", //
        };

        ContestSummaryReports summaryReports = new ContestSummaryReports();

        IReport[] list = summaryReports.getReportList();

        assertEquals("Expecting count of reports ", 22, list.length);

        int i = 0;
        for (IReport iReport : list) {

            String actual = iReport.getReportTitle();
            assertEquals("Expecting report name ", expectedNames[i], actual);
//            System.out.println(quote(actual)+", //");  // output new expected data
            i++;
        }
    }
    
    /**
     * Surround by a double quote
     */
    public String quote(String string) {
        return "\"" + string + "\"";
//        return "'" + string + "'";
    }

}
