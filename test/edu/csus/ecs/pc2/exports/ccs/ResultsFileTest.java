package edu.csus.ecs.pc2.exports.ccs;

import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;

/**
 * Test ResultsFile class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ResultsFileTest.java 193 2011-05-14 05:02:16Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/exports/ccs/ResultsFileTest.java $
public class ResultsFileTest extends TestCase {

    private final boolean debugMode = false;

    /**
     * Sample Finalize Data.
     * 
     * @param rank
     * @return
     */
    private FinalizeData createSampFinalData(int rank) {
        FinalizeData data = new FinalizeData();

        // 3, 6, 10
        // data.setGoldRank(rank += 2);
        // data.setSilverRank(rank += 3);
        // data.setBronzeRank(rank += 4);

        data.setGoldRank(4);
        data.setSilverRank(8);
        data.setBronzeRank(12);

        data.setComment("Finalized by Director of Operations");

        // if (debugMode) {
        // System.out.println("  gold rank   : " + data.getGoldRank());
        // System.out.println("  silver rank : " + data.getSilverRank());
        // System.out.println("  bronze rank : " + data.getBronzeRank());
        // System.out.println("  comment     : " + data.getComment());
        // }

        return data;
    }

    public Account[] getTeamAccounts(IInternalContest contest) {
        Vector<Account> accounts = contest.getAccounts(Type.TEAM);
        Account[] list = (Account[]) accounts.toArray(new Account[accounts.size()]);
        Arrays.sort(list, new AccountComparator());
        return list;
    }



    /**
     * Add runs to contest.
     * 
     * @param contest
     * @param runsData
     * @throws Exception 
     */
    private void addRuns(IInternalContest contest, String[] runsData) throws Exception {

        for (String runInfoLine : runsData) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }
    }

    /**
     * Test for results file with no runs.
     * 
     * @throws Exception
     */
    public void testcreateTSVFileLinesEmpty() throws Exception {

        String[] expectedResults = { //
        "2020;1;gold;0", // results
                "2021;1;gold;0", // results
                "2022;1;gold;0", // results
                "2023;1;gold;0", // results
                "2024;1;gold;0", // results
                "2025;1;gold;0", // results
                "2026;1;gold;0", // results
                "2027;1;gold;0", // results
                "2028;1;gold;0", // results
                "2029;1;gold;0", // results
                "2030;1;gold;0", // results
                "2031;1;gold;0", // results
                "2032;1;gold;0", // results
                "2033;1;gold;0", // results
                "2034;1;gold;0", // results
                "2035;1;gold;0", // results
                "2036;1;gold;0", // results
                "2037;1;gold;0", // results
                "2038;1;gold;0", // results
                "2039;1;gold;0", // results
        };

        ResultsFile resultsFile = new ResultsFile();

        FinalizeData finalizeData = createSampFinalData(1);
        resultsFile.setFinalizeData(finalizeData);

        int numTeams = 20;
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, numTeams, 12, true);

        SampleContest.assignReservationIds(contest, 2020);
        
        contest.setFinalizeData(finalizeData);

         String[] results = resultsFile.createTSVFileLines(contest); // using getStandingsRecords
//        String[] results = resultsFile.createTSVFileLinesTwo(contest); // using XML

        assertEquals("Number results file lines ", numTeams + 1, results.length);

        compareResults(results, expectedResults);

    }

    /**
     * Test with 27 or more runs.
     * 
     * @throws Exception
     */
    public void testcreateTSVFileLinesComplex() throws Exception {

        String[] runsData = { //
        "1,1,A,1,No", // 20 (a No before first yes)
                "2,1,A,3,Yes", // 3 (first yes counts Minute points but never
                               // Run Penalty points)
                "3,1,A,5,No", // zero -- after Yes
                "4,1,A,7,Yes", // zero -- after Yes
                "5,1,A,9,No", // zero -- after Yes
                "6,1,B,11,No", // zero -- not solved
                "7,1,B,13,No", // zero -- not solved
                "8,2,A,30,Yes", // 30 (minute points; no Run points on first
                                // Yes)
                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
                "14,12,B,55,Yes", // solved
                "15,12,A,55,Yes", // solved
                "16,5,B,53,Yes", // solved
                "17,5,A,35,Yes", // solved
                "18,7,B,55,Yes", // solved
                "19,7,A,55,Yes", // solved
                "20,10,B,15,Yes", // solved
                "21,11,A,25,Yes", // solved
                "22,12,B,35,Yes", // solved
                "23,13,A,45,Yes", // solved
                "24,14,B,55,Yes", // solved
                "25,15,A,65,Yes", // solved
                "26,19,B,75,Yes", // solved
                "27,19,A,85,Yes", // solved
        };

        // Reservation Id; rank ; medal; solved

        // for medal ranks 4, 8, 12
        String[] expectedResults = { //
        "2024;1;gold;2", // results
                "2031;2;gold;2", // results
                "2026;3;gold;2", // results
                "2038;4;gold;2", // results
                "2029;5;silver;1", // results
                "2020;6;silver;1", // results
                "2030;7;silver;1", // results
                "2021;8;silver;1", // results
                "2032;9;bronze;1", // results
                "2033;10;bronze;1", // results
                "2034;11;bronze;1", // results
                "2022;12;bronze;0", // results
                "2023;12;bronze;0", // results
                "2025;12;bronze;0", // results
                "2027;12;bronze;0", // results
                "2028;12;bronze;0", // results
                "2035;12;bronze;0", // results
                "2036;12;bronze;0", // results
                "2037;12;bronze;0", // results
                "2039;12;bronze;0", // results
        };

        // for medal ranks 3, 6, 10
        @SuppressWarnings("unused")
        String[] expectedResults3610 = { //
        "2024;1;gold;2", // results
                "2031;2;gold;2", // results
                "2026;3;silver;2", // results
                "2038;4;silver;2", // results
                "2029;5;silver;1", // results
                "2020;6;bronze;1", // results
                "2030;7;bronze;1", // results
                "2021;8;bronze;1", // results
                "2032;9;bronze;1", // results
                "2033;10;honorable;1", // results
                "2034;11;honorable;1", // results
                "2022;12;honorable;0", // results
                "2023;12;honorable;0", // results
                "2025;12;honorable;0", // results
                "2027;12;honorable;0", // results
                "2028;12;honorable;0", // results
                "2035;12;honorable;0", // results
                "2036;12;honorable;0", // results
                "2037;12;honorable;0", // results
                "2039;12;honorable;0", // results
        };

        ResultsFile resultsFile = new ResultsFile();

        FinalizeData finalizeData = createSampFinalData(1);
        resultsFile.setFinalizeData(finalizeData);

        int numTeams = 20;
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, numTeams, 12, true);

        SampleContest.assignReservationIds(contest, 2020);

        addRuns(contest, runsData);
        
        contest.setFinalizeData(finalizeData);

        String[] results = resultsFile.createTSVFileLines(contest); // using getStandingsRecords
        // String[] results = resultsFile.createTSVFileLinesTwo(contest); // using XML

        if (debugMode) {
            dumpStringArray(results);

            // printExpectedTestData(results);
        }

        // TODO CCS
        compareResults(results, expectedResults);
    }

    @SuppressWarnings("unused")
    private void printExpectedTestData(String[] expected) {

        // form
        // "2024;1;gold;2", // results

        for (String s : expected) {
            String[] fields = s.split("\t");
            if (fields.length > 4) {
                System.out.println("\"" + fields[0] + ";" + fields[1] + ";" + fields[2] + ";" + fields[3] + "\", // results ");
            }
        }
    }

    private void dumpStringArray(String[] sa) {
        int count = 1;
        for (String s : sa) {
            System.out.println(count + ": " + s);
            count++;
        }
    }

    /**
     * Compare results file info to expected info.
     * 
     * @param results
     * @param expectedResults
     */
    private void compareResults(String[] results, String[] expectedResults) {

        assertEquals("Number results file lines ", expectedResults.length, results.length - 1);

        for (int row = 0; row < expectedResults.length; row++) {

            String[] fields1 = results[row + 1].split("\t");
            String[] fields2 = expectedResults[row].split(";");

            // Reservation Id; rank ; medal; solved

            assertEquals("Reservation Id", fields1[0], fields2[0]);
            assertEquals("Rank for " + fields1[0], fields1[1], fields2[1]);
            assertEquals("Medal for " + fields1[0], fields1[2], fields2[2]);
            assertEquals("Solved for " + fields1[0], fields1[3], fields2[3]);
        }
    }
}
