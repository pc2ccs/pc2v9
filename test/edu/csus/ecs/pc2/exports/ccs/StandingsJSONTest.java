package edu.csus.ecs.pc2.exports.ccs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.RunUnavailableException;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: StandingsJSONTest.java 342 2013-06-26 10:21:44Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/test/edu/csus/ecs/pc2/exports/ccs/StandingsJSONTest.java $
public class StandingsJSONTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();
    
    private boolean debugMode = false;

    public void testNull() throws Exception {
        StandingsJSON standings = new StandingsJSON();

        String jsonString = standings.createJSON(null);
        assertEquals("Expecting empty JSON Array", "[]", jsonString);

        jsonString = standings.createJSON(new InternalContest());
        assertEquals("Expecting empty JSON Array", "[]", jsonString);

        SampleContest sample = new SampleContest();
        jsonString = standings.createJSON(sample.createStandardContest());
        assertEquals("Expecting empty JSON Array", "[]", jsonString);

    }

    public void testWithRuns() throws Exception {

        String[] runsData = { "1,1,A,1,No,Yes", // 20 (a No before first yes)
                "2,1,A,3,Yes,Yes", // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No,Yes", // zero -- after Yes
                "4,1,A,7,Yes,Yes", // zero -- after Yes
                "5,1,A,9,No,Yes", // zero -- after Yes
                "6,1,B,11,No,Yes", // zero -- not solved
                "7,1,B,13,No,Yes", // zero -- not solved
                "8,2,A,30,Yes,Yes", // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No,Yes", // zero -- not solved
                "10,2,B,40,No,Yes", // zero -- not solved
                "11,2,B,45,No,Yes", // zero -- not solved
                "12,2,B,50,No,Yes", // zero -- not solved
                "13,2,B,55,No,Yes", // zero -- not solved

                "14,9,A,55,Yes,Yes", //
                "15,9,B,155,Yes,Yes", //
                "16,9,C,255,Yes,Yes", //
                "17,9,D,355,Yes,Yes", //
                "18,9,E,455,Yes,Yes", //

        };

        IInternalContest contest = sampleContest.createStandardContest();

        for (String runInfoLine : runsData) {
            sampleContest.addARun((InternalContest) contest, runInfoLine);
        }

        StandingsJSON standings = new StandingsJSON();
        String jsonString = standings.createJSON(contest);

        // TODO use assertCount("Expected number of commas in JSON Array", 265, ',', jsonString);
        
        int commaCount = countCharacters(jsonString, ',');
        assertEquals("Expected number of commas in JSON Array", 265, commaCount);

        int colonCount = countCharacters(jsonString, ':');
        assertEquals("Expected number of colons in JSON Array", 394, colonCount);

        assertEquals("Expecting JSON Array of length", 4724, jsonString.length());

        // String filename = "/tmp/StandingsJSON.out.txt";
        // PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false), true);
        // writer.println(jsonString);
        // writer.close();
        // writer = null;
        // editFile(filename);

        String expected9E = "\"E\":{\"a\":1,\"t\":455,\"s\":\"first\"}";

        assertEquals("Expecting string " + expected9E + " in jason", 165, jsonString.indexOf(expected9E));

    }
    
    /**
     * Judge all runs.
     * 
     * @throws Exception
     */
    public void testNumber2() throws Exception {

        IInternalContest contest = sampleContest.createStandardContest();

        StandingsJSON standings = new StandingsJSON();
        createRunPerTeam(contest);
        String jsonString = standings.createJSON(contest);

        int commaCount = countCharacters(jsonString, ',');
        assertEquals("Expected number of commas in JSON Array", 560, commaCount);

        int colonCount = countCharacters(jsonString, ':');
        assertEquals("Expected number of colons in JSON Array", 800, colonCount);

        assertEquals("Expecting JSON Array of length", 8162, jsonString.length());

        if (debugMode){
            prettyPrintJason(jsonString);
        }

    }

    private void prettyPrintJason(String jsonString) {

        String[] f = jsonString.split("}");
        for (String string : f) {
            System.out.print(string + "}");
            if (string.length() == 0) {
                System.out.println();
            }

        }
        System.out.println("");
    }

    private void createRunPerTeam(IInternalContest contest) throws IOException, ClassNotFoundException, FileSecurityException, RunUnavailableException {
        Vector<Account> vector = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts, new AccountComparator());
        int runId = contest.getRuns().length + 1;
        int time = 82;
        for (Account account : accounts) {
            int teamId = account.getClientId().getClientNumber();
            time += 5;
            String solved = "Yes";
            if (runId % 3 == 0){
                solved = "No";
            }
            String runInfoLine = runId + "," + teamId + ",A," + time + "," + solved + ",Yes"; // zero -- after Yes
            sampleContest.addARun((InternalContest) contest, runInfoLine);
            runId++;
        }
    }
    
}
