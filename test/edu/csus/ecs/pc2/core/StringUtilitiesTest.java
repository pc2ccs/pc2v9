// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilities;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;


/**
 * Unit test.
 *
 * @author pc2@ecs.csus.edu
 */
public class StringUtilitiesTest extends AbstractTestCase {

    public void testAppendString() throws Exception {

        String[] data = {
                //
                "a,b", //
                "a,b,c,d,e,f", //
        };

        for (String line : data) {

//            System.out.println("line " + line);
            String[] f = line.split(",");

            String[] results = new String[0];

            for (int i = 0; i < f.length; i++) {
                results = StringUtilities.appendString(results, f[i]);
                compareArrayParts(f, i + 1, results);
            }
        }
    }

    private void compareArrayParts(String[] source, int count, String[] actual) {
        for (int i = 0; i < actual.length; i++) {
            assertEquals(source[i], actual[i]);
        }
    }

    public void testTrunc() throws Exception {
        String[] data = { //
                "a;5;a", //
                "abc;5;abc", //
                "abcdefg;5;ab...", //
                "abcdefghijklmopqrstuv;5;ab...", //
                "abcdefghijklm;13;abcdefghijklm", //
        };

        for (String line : data) {

            String[] f = line.split(";");

            String source = f[0];
            int maxlen = Integer.parseInt(f[1]);
            String expected = f[2];

            String actual = StringUtilities.trunc(source, maxlen);
            assertEquals("trunc method ", expected, actual);
        }


    }

    /**
     * Test getNumberList
     * @throws Exception
     */
    public void testgetNumberList() throws Exception {

        String [] data = { //
                "1;[1]", //
                "1,2,3,6-12;[1, 2, 3, 6, 7, 8, 9, 10, 11, 12]", //
                "5-5;[5]", //
                "  5  -  5   ;[5]", //
                "1,3,20-26;[1, 3, 20, 21, 22, 23, 24, 25, 26]", //
                "1, 2 ,3;[1, 2, 3]", //
                "4-18;[4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18]", //
                "4-12,1,5,6;[4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 5, 6]", //
                "100-102,123, 321 ;[100, 101, 102, 123, 321]", //
                "1,4,12-15,22;[1, 4, 12, 13, 14, 15, 22]", //

        };

        for (String string : data) {
            String [] values =string.split(";");
            String numberString =values[0];
            String expected =values[1].trim();

            int[] out = StringUtilities.getNumberList(numberString);
//            System.out.println("new data \""+numberString+";"+Arrays.toString(out)+"\", // ");
            assertEquals("Expected range for "+numberString, expected, Arrays.toString(out));
        }
    }

    /**
     * Test getNumberList for invalid input number strings.
     *
     * @throws Exception
     */
    public void testgetNumberListNegative() throws Exception {

        String[] data = { //
                "-", //
                "2,-4", // missing start number
                "1,2,3,55-,6", // missing end number
                "20-12", // start number greater than end range number
                "1,2,4-5-6,20", // too many dashes
                "", // no list at all
                "            ", // lots of space
                "1,2,,4,5", // missing number
                "1,2,3,     ,7,8", // missing number
                "0-5;[5]", //
        };

        for (String inString : data) {
            try {
                String numberString = inString;
                StringUtilities.getNumberList(numberString);

                fail("Expecting failure for string: " + inString);
            } catch (Exception e) {
                ; // Passes unit test
            }
        }

    }


    protected Run [] add90Runs(IInternalContest contest) throws Exception {

        String [] runsDataList = { //
                "1,16,B,1,No", //
                "2,8,C,1,No", //
                "3,5,B,1,No", //
                "4,4,C,1,No", //
                "5,4,D,1,No", //
                "6,3,A,1,No", //
                "7,1,A,1,No", //
                "8,6,B,1,New", //
                "9,18,A,1,No", //
                "10,6,A,1,No", //
                "11,21,D,1,Yes", //
                "12,6,D,1,No", //
                "13,12,A,1,Yes", //
                "14,13,A,1,No", //
                "15,8,D,1,Yes", //
                "16,3,B,1,No", //
                "17,3,A,1,No", //
                "18,16,C,1,No", //
                "19,20,D,1,Yes", //
                "20,12,B,1,No", //
                "21,14,A,1,No", //
                "22,15,C,1,No", //
                "23,8,D,1,No", //
                "24,13,D,1,No", //
                "25,21,A,1,No", //
                "26,18,D,1,Yes", //
                "27,6,C,1,No", //
                "28,20,B,1,Yes", //
                "29,8,D,1,No", //
                "30,19,B,1,No", //
                "31,22,C,1,No", //
                "32,7,A,1,No", //
                "33,7,A,1,No", //
                "34,4,D,1,New", //
                "35,18,B,1,No", //
                "36,4,D,1,Yes", //
                "37,19,C,1,No", //
                "38,2,B,1,No", //
                "39,15,C,1,No", //
                "40,12,B,1,No", //
                "41,10,D,1,Yes", //
                "42,22,A,1,No", //
                "43,8,C,1,No", //
                "44,8,A,1,Yes", //
                "45,18,D,1,No", //
                "46,13,C,1,No", //
                "47,7,D,1,No", //
                "48,7,C,1,New", //
                "49,5,C,1,No", //
                "50,7,B,1,New", //
                "51,21,B,1,No", //
                "52,8,D,1,Yes", //
                "53,16,A,1,No", //
                "54,10,A,1,No", //
                "55,22,B,1,No", //
                "56,18,C,1,No", //
                "57,5,D,2,Yes", //
                "58,10,C,2,No", //
                "59,9,C,2,Yes", //
                "60,5,D,2,Yes", //
                "61,12,D,2,No", //
                "62,10,C,2,No", //
                "63,3,B,2,Yes", //
                "64,21,C,2,No", //
                "65,8,B,2,Yes", //
                "66,19,B,2,Yes", //
                "67,18,A,2,No", //
                "68,12,D,2,Yes", //
                "69,5,B,2,Yes", //
                "70,2,A,2,No", //
                "71,51,D,2,No", //
                "72,52,D,2,No", //
                "73,58,C,2,No", //
                "74,54,D,2,Yes", //
                "75,52,A,2,No", //
                "76,60,D,2,No", //
                "77,57,C,2,No", //
                "78,54,D,2,No", //
                "79,55,A,2,No", //
                "80,56,B,2,No", //
                "81,52,C,2,No", //
                "82,52,C,2,No", //
                "83,52,A,2,No", //
                "84,51,D,2,Yes", //
                "85,52,C,2,No", //
                "86,60,C,2,No", //
                "87,57,C,2,No", //
                "88,57,A,2,New", //
                "89,60,B,2,No", //
                "90,52,C,2,No" //

        };

        for (String runInfoLine : runsDataList) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }

        return contest.getRuns();
    }

    protected Run [] addTc1Runs(IInternalContest contest) throws Exception {

        String [] runsDataList = { //
                "1,101,B,1,Yes", //
                "2,151,C,1,Yes", //
                "3,201,B,1,Yes", //
                "4,251,C,1,Yes", //
                "5,301,D,1,Yes", //
                "6,351,A,1,Yes", //
                "7,401,A,1,Yes", //
                "8,451,B,1,No", //
                "9,501,A,1,Yes", //
                "10,551,A,1,Yes", //
                "11,551,D,1,Yes", //
                "12,551,D,1,No", //
                "13,602,A,1,Yes", //
                "14,801,A,1,No", //
                "15,801,D,1,Yes", //
                "16,801,B,1,No", //
                "17,801,A,1,No", //
                "18,901,C,1,No", //
                "19,901,D,1,Yes", //
                "20,901,B,1,No", //
                "90,901,C,2,No" //
        };

        for (String runInfoLine : runsDataList) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }

        return contest.getRuns();
    }



    private void initializeStaticLog(String name) {
        StaticLog.setLog(new Log("logs", name + ".log"));
    }

    public void testgetRunsForGroupFilter() throws Exception {

        initializeStaticLog(getName());
        InternalContest contest = new InternalContest();
        String cdpDir = getTestSampleContestDirectory("tc1");

        IContestLoader loader = new ContestSnakeYAMLLoader();
        loader.initializeContest(contest, new File(cdpDir));

        Account[] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());

        assertEquals("Expecting # jugements", 10, contest.getJudgements().length);

        addTc1Runs(contest);

        Group group5 = contest.getGroup(contest.getAccount(accounts[5].getClientId()).getPrimaryGroupId());
        assertNotNull(group5);
        assertEquals("getGroupFilteredRuns for " + group5.getDisplayName(),
                countRunsWhoseSubmitterIsInGroup(contest, group5), getRunsForGroup(contest, group5).length);

        Account account12 = accounts[12];
        Group group12 = contest.getGroup(account12.getPrimaryGroupId());
        assertNotNull(group12);
        int runsInGroup12Before = getRunsForGroup(contest, group12).length;
        assertEquals("getGroupFilteredRuns for " + group12.getDisplayName(),
                countRunsWhoseSubmitterIsInGroup(contest, group12), runsInGroup12Before);
        assertTrue("Expecting at least one run from a team in " + group12.getDisplayName(), runsInGroup12Before > 0);

        account12.clearGroups();
        contest.updateAccounts(accounts);
        for (Run run : getRunsForGroup(contest, group12)) {
            assertFalse("Team removed from group should not appear in group-filtered runs",
                    run.getSubmitter().equals(account12.getClientId()));
        }

    }

    private Run[] getRunsForGroup(IInternalContest contest, Group group) {
        List<Group> wantedGroups = new ArrayList<Group>();
        wantedGroups.add(group);
        return ScoreboardUtilities.getGroupFilteredRuns(contest, wantedGroups);
    }

    private int countRunsWhoseSubmitterIsInGroup(IInternalContest contest, Group group) {
        int count = 0;
        for (Run run : contest.getRuns()) {
            Account account = contest.getAccount(run.getSubmitter());
            if (account != null && account.isGroupMember(group.getElementId())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Load 9 judgements, including AC into contest
     * @param contest
     */
    public void loadJudgement(IInternalContest contest) {

        String[] judgements = {"Stupid programming error", "Misread problem statement", "Almost there", "You have no clue", "Give up and go home", "Consider switching to another major",
                "How did you get into this place ?", "Contact Staff - you have no hope" };
        String[] acronymns = {"CE", "WA", "TLE", "WA2", "RTE", "OFE", "WA3", "JE" };

        Judgement judgementYes = new Judgement("Yes.", "AC");
        contest.addJudgement(judgementYes);

        int i = 0;
        for (String judgementName : judgements) {
            Judgement judgement = new Judgement(judgementName,acronymns[i]);
            contest.addJudgement(judgement);
            i++;
        }
    }


    // TODO REFACTOR move to AbstractTestcase
    @Override
    public String getSampleContestsDirectory() {
        return "samps" + File.separator + "contests";
    }


    // TODO REFACTOR move to AbstractTestcase
    @Override
    public String getTestSampleContestDirectory(String dirname) {
        return getSampleContestsDirectory() + File.separator + dirname;
    }

    public void testgetTeamNumber() throws Exception {

//        016.991|INFO|qtp49752459-26|log|Standings requested by team team101

        String user = "team101".toLowerCase();
        Integer num = StringUtilities.getTeamNumber(user);
        assertEquals("Expecting number for "+user, 101, num.intValue());

        user = "team1".toLowerCase();
        num = StringUtilities.getTeamNumber(user);
        assertEquals("Expecting number for "+user, 1, num.intValue());

        user = "foo33".toLowerCase();
        num = StringUtilities.getTeamNumber(user);
        System.out.println("debug 22 num num "+num);
        assertNull("Expecting null for "+user, num);

    }


}
