package edu.csus.ecs.pc2.exports.ccs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.scoring.StandingsRecord;
import edu.csus.ecs.pc2.core.scoring.SummaryRow;
import edu.csus.ecs.pc2.core.util.RunStatistics;

/**
 * Standings information in JSON format.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: StandingsJSON.java 341 2013-06-21 10:53:25Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/exports/ccs/StandingsJSON.java $
public class StandingsJSON {

    public String createJSON(IInternalContest contest) throws IllegalContestState {

        if (contest == null || contest.getRuns().length == 0) {
            return "[]";
        }

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        scoringAlgorithm.setContest(contest);
        
        ContestInformation info = contest.getContestInformation();
        Properties properties = new Properties();
        if (info != null) {
            if (info.getScoringProperties() != null) {
                properties = info.getScoringProperties();
            }
        }

        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, properties);

        RunStatistics runStatistics = new RunStatistics(contest);

        StringBuffer buffer = new StringBuffer();

        for (StandingsRecord sr : standingsRecords) {
            buffer.append('{');
            ClientId clientId = sr.getClientId();
            Account account = contest.getAccount(clientId);
            String universityName = account.getDisplayName();
            String groupName = "";
            if (account.getGroupId() != null) {
                groupName = contest.getGroup(account.getGroupId()).toString();
            }

            // {"id":"<Rank>","name":"<University_name>","group":"<Group_name>"
            buffer.append(pair("id", "" + sr.getRankNumber()) + "," + pair("name", universityName) + "," + pair("group", groupName));

            SummaryRow row = sr.getSummaryRow();
            for (int i = 0; i < contest.getProblems().length; i++) {
                int problemIndex = i + 1;
                ProblemSummaryInfo summaryInfo = row.get(problemIndex);
                if (summaryInfo != null) {
                    int numberSubmitted = summaryInfo.getNumberSubmitted();
                    // int penalty = summaryInfo.getPenaltyPoints();
                    long solveTime = summaryInfo.getSolutionTime();
                    String results = null;
                    if (numberSubmitted > 0) {
                        results = "tried";
                        if (solveTime > 0) {
                            results = "solved";
                            Problem problem = contest.getProblems()[i];
                            if (runStatistics.isFirstToSolve(clientId, problem)) {
                                results = "first";
                            }
                        }
                    }
                    // ,"<Problem_letter>":{"a":<hAttempts uh>,"t":<Elapsed_time>,"s":"<Results_phase>"}},
                    if (results != null) {
                        ArrayList<String> list = new ArrayList<String>();
                        if (numberSubmitted > 0) {
                            list.add(pair("a", numberSubmitted));
                        }
                        if (solveTime > 0) {
                            list.add(pair("t", solveTime));
                        }
                        list.add(pair("s", "" + results));
                        String probInfo = join(",", list);

                        buffer.append(",\"" + getProblemLetter(problemIndex) + "\":{" + probInfo + "}");
                    }
                }
            }

            buffer.append('}');

        }
        return "[" + buffer.toString() + "]";
    }

    public static String join(String delimit, List<String> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
            if (i < list.size() - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }

    private String pair(String name, long value) {
        return "\"" + name + "\":" + value;
    }

    /**
     * return a letter for a number.
     * 
     * 
     * @return 0 = A, 1 = B, etc.
     */
    public static String getProblemLetter(int id) {
        char let = 'A';
        let += (id - 1);
        return Character.toString(let);
    }

    private String pair(String name, String value) {
        return "\"" + name + "\":\"" + value + "\"";
    }
}
