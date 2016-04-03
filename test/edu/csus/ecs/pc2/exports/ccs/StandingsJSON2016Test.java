package edu.csus.ecs.pc2.exports.ccs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author DougOlas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class StandingsJSON2016Test extends AbstractTestCase {

    private boolean debugMode = false;

    public void test12Runs() throws Exception {

        InternalContest contest = new InternalContest();

        int numTeams = 22;
        int numProblems = 5;
        initData(contest, numTeams, numProblems);

        String[] runsDataList = { //
        //
                "1,16,B,1,No", "2,8,C,1,No", "3,5,B,1,No", //
                "4,4,C,1,No", "5,4,D,1,No", "6,3,A,1,No", "7,1,A,1,No", //
                "8,6,B,1,New", "9,18,A,1,No", "10,6,A,1,No", "11,21,D,1,Yes", //
                "12,6,D,1,No", //
        };

        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        StandingsJSON2016 standingsJSON2016 = new StandingsJSON2016();
        String json = standingsJSON2016.createJSON(contest);
        // System.out.println("JSON="+json);

        assertEquals("Expecting JSON length ", 8250, json.length());

        // ensureDirectory(getDataDirectory());
        // startExplorer(getDataDirectory());

        /**
         * File containing one line of the expected JSON output
         */
        String expectedJSONFilename = getTestFilename("test12Runs.json.txt");

        // This write file will overwrite the expected output,
        // if the createJSON method changes the output, uncomment the writeFormattedJson
        // to write the newer, better expected json.
        // writeFormattedJson(expectedJSONFilename, json);

        // System.out.println("Expected file at  "+expectedJSONFilename);

        assertFileExists(expectedJSONFilename);

        String[] lines = edu.csus.ecs.pc2.core.Utilities.loadFile(expectedJSONFilename);
        String expectedJSON = StringUtilities.join("", lines);
        assertJSONEquals(json, expectedJSON);

    }

    /**
     * Write crudely formatted JSON.
     * 
     * @param fileName
     * @param oneLine
     * @throws IOException
     */
    // SOMEDAY produce a nicer formatter output
    protected void writeFormattedJson(String fileName, String oneLine) throws IOException {
        String newString = oneLine.replaceAll("},", "},\n");
        createFile(fileName, newString);
    }

    /**
     * Create a single line file.
     * 
     * @param fileName
     * @param oneLine
     * @throws IOException
     */
    private void createFile(String fileName, String oneLine) throws IOException {

        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(fileName);
        byte[] buffer = oneLine.getBytes();
        outputStream.write(buffer, 0, buffer.length);
        outputStream.close();

    }

    public void test90Runs() throws Exception {

        InternalContest contest = new InternalContest();

        int numTeams = 22;
        int numProblems = 5;
        initData(contest, numTeams, numProblems);

        String[] runsDataList = { //
        "1,16,B,1,No", "2,8,C,1,No", "3,5,B,1,No", //
                "4,4,C,1,No", "5,4,D,1,No", "6,3,A,1,No", "7,1,A,1,No", //
                "8,6,B,1,New", "9,18,A,1,No", "10,6,A,1,No", "11,21,D,1,Yes", //
                "12,6,D,1,No", "13,12,A,1,Yes", "14,13,A,1,No", //
                "15,8,D,1,Yes", "16,3,B,1,No", "17,3,A,1,No", //
                "18,16,C,1,No", "19,20,D,1,Yes", "20,12,B,1,No", //
                "21,14,A,1,No", "22,15,C,1,No", "23,8,D,1,No", //
                "24,13,D,1,No", "25,21,A,1,No", "26,18,D,1,Yes", //
                "27,6,C,1,No", "28,20,B,1,Yes", "29,8,D,1,No", //
                "30,19,B,1,No", "31,22,C,1,No", "32,7,A,1,No", //
                "33,7,A,1,No", "34,4,D,1,New", "35,18,B,1,No", //
                "36,4,D,1,Yes", "37,19,C,1,No", "38,2,B,1,No", //
                "39,15,C,1,No", "40,12,B,1,No", "41,10,D,1,Yes", //
                "42,22,A,1,No", "43,8,C,1,No", "44,8,A,1,Yes", //
                "45,18,D,1,No", "46,13,C,1,No", "47,7,D,1,No", //
                "48,7,C,1,New", "49,5,C,1,No", "50,7,B,1,New", //
                "51,21,B,1,No", "52,8,D,1,Yes", "53,16,A,1,No", //
                "54,10,A,1,No", "55,22,B,1,No", "56,18,C,1,No", //
                "57,5,D,2,Yes", "58,10,C,2,No", "59,9,C,2,Yes", //
                "60,5,D,2,Yes", "61,12,D,2,No", "62,10,C,2,No", //
                "63,3,B,2,Yes", "64,21,C,2,No", "65,8,B,2,Yes", //
                "66,19,B,2,Yes", "67,18,A,2,No", "68,12,D,2,Yes", //
                "69,5,B,2,Yes", "70,2,A,2,No", "71,21,D,2,No", //
                "72,12,D,2,No", "73,18,C,2,No", "74,14,D,2,Yes", //
                "75,2,A,2,No", "76,20,D,2,No", "77,7,C,2,No", //
                "78,14,D,2,No", "79,15,A,2,No", "80,16,B,2,No", //
                "81,2,C,2,No", "82,2,C,2,No", "83,22,A,2,No", //
                "84,21,D,2,Yes", "85,2,C,2,No", "86,10,C,2,No", //
                "87,17,C,2,No", "88,7,A,2,New", "89,20,B,2,No", //
                "90,12,C,2,No" //
        };

        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        StandingsJSON2016 standingsJSON2016 = new StandingsJSON2016();
        String json = standingsJSON2016.createJSON(contest);

        // System.out.println("JSON="+json);

        assertEquals("Expecting JSON length ", 8761, json.length());

        /**
         * File containing one line of the expected JSON output
         */
        String expectedJSONFilename = getTestFilename("test90Runs.json.txt");

        // This write file will overwrite the expected output,
        // if the createJSON method changes the output, uncomment the writeFormattedJson
        // to write the newer, better expected json.
        // writeFormattedJson(expectedJSONFilename, json);

        // System.out.println("Expected file at  "+expectedJSONFilename);

        assertFileExists(expectedJSONFilename);

        String[] lines = edu.csus.ecs.pc2.core.Utilities.loadFile(expectedJSONFilename);
        String expectedJSON = StringUtilities.join("", lines);
        assertJSONEquals(json, expectedJSON);

    }

    private void assertJSONEquals(String expectedJSON, String actualJson) {

        // SOMEDAY create a more sophisticated/granular comparison.
        assertEquals(expectedJSON, actualJson);
    }

    /**
     * Initialize contest with teams, problems, languages, judgements.
     * 
     * @param contest
     * @param numTeams
     * @param numProblems
     */
    private void initData(IInternalContest contest, int numTeams, int numProblems) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), numTeams, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 6, true);

        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount(contest));

        // Add Problem
        for (int i = 0; i < numProblems; i++) {
            char letter = 'A';
            letter += i;
            Problem problem = new Problem("" + letter);
            problem.setLetter("" + letter);
            contest.addProblem(problem);
        }

        // Add Language
        Language language = new Language("Java");
        contest.addLanguage(language);

        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "Contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }
    }

    /**
     * add run to list of runs in a contest.
     * 
     * Files found in runInfoLine, comma delmited
     * 
     * <pre>
     * 0 - run id, int
     * 1 - team id, int
     * 2 - problem letter, char
     * 3 - elapsed, int
     * 4 - solved, String &quot;Yes&quot; or No
     * 5 - send to teams, Yes or No
     * 
     * Example:
     * &quot;6,5,A,12,Yes&quot;
     * &quot;6,5,A,12,Yes,Yes&quot;
     * 
     * </pre>
     * 
     * @param contest
     * @param runInfoLine
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void addTheRun(InternalContest contest, String runInfoLine) {

        // get 5th judge
        ClientId judgeId = contest.getAccounts(Type.JUDGE).elementAt(4).getClientId();

        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement yesJudgement = contest.getJudgements()[0];
        Judgement noJudgement = contest.getJudgements()[1];

        String[] data = runInfoLine.split(",");

        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]

        int runId = getIntegerValue(data[0]);
        int teamId = getIntegerValue(data[1]);
        String probLet = data[2];
        int elapsed = getIntegerValue(data[3]);
        boolean solved = "Yes".equals(data[4]);

        boolean sendToTeams = true;
        if (data.length > 5) {
            sendToTeams = "Yes".equals(data[5]);
        }

        String judgement = data[4];
        assertTrue("Expecting valid judgement, found " + judgement, "Yes".equals(judgement) || //
                "New".equals(judgement) || "No".equals(judgement));

        /**
         * Pending, aka not judged.
         */
        boolean pending = "New".equals(data[4]);

        int problemIndex = probLet.charAt(0) - 'A';
        Problem problem = problemList[problemIndex];
        ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamId);

        Run run = new Run(clientId, languageId, problem);
        run.setNumber(runId);
        run.setElapsedMins(elapsed);

        ElementId judgementId = noJudgement.getElementId();
        if (solved) {
            judgementId = yesJudgement.getElementId();
        }
        JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);
        judgementRecord.setSendToTeam(sendToTeams);

        try {
            contest.addRun(run);

            if (!pending) {
                checkOutRun(contest, run, judgeId);
                contest.addRunJudgement(run, judgementRecord, null, judgeId);
            }

        } catch (IOException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: " + run, false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: " + run, false);
        } catch (FileSecurityException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: " + run, false);
        }

        if (debugMode) {
            if (run.getJudgementRecord() != null) {
                System.out.print("Send to teams " + run.getJudgementRecord().isSendToTeam() + " ");
            } else {
                System.out.print("Send to teams false(not judged) ");
                System.out.println("Added run " + run);
            }
        }

    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) {
        try {
            contest.checkoutRun(run, judgeId, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and return a new scoreboard client.
     * 
     * @param contest
     * @return a ClientId for newly created scoreboard account.
     */
    private ClientId createBoardAccount(IInternalContest contest) {
        Vector<Account> scoreboards = contest.generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), 1, true);
        return scoreboards.firstElement().getClientId();
    }

}
