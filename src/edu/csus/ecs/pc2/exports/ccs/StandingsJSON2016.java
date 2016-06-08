package edu.csus.ecs.pc2.exports.ccs;

import java.util.List;
import java.util.Properties;

import edu.csus.ecs.pc2.core.IInternalController;
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
 * Standings information in CLI 2016 JSON format.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: StandingsJSON.java 341 2013-06-21 10:53:25Z laned $
 */

public class StandingsJSON2016 {

    /**
     * Returns a JSON string describing the current contest standings in the format defined by the 2016 CLI JSON Scoreboard.
     * The format follows this JSON example, from the CLI Wiki JSON Scoreboard page:
     * 
     * <pre>
     *  [ {
     *     "rank":1,"team":42,"score":{"num_solved":3,"total_time":340},
     *     "problems":[
     *       {"label":"A","num_judged":3,"num_pending":1,"solved":false},
     *       {"label":"B","num_judged":1,"num_pending":0,"solved":true,"time":20,"first_to_solve":true},
     *       {"label":"C","num_judged":2,"num_pending":0,"solved":true,"time":55,"first_to_solve":false},
     *       {"label":"D","num_judged":0,"num_pending":0,"solved":false},
     *       {"label":"E","num_judged":3,"num_pending":0,"solved":true,"time":205,"first_to_solve":false}
     *      ]
     *    },
     *    ...
     *  ]
     * </pre>
     * 
     * @param contest - the current contest
     * @param controller - the current contest controller
     * @return a JSON string giving contest standings in 2016 format
     * @throws IllegalContestState
     */
    public String createJSON(IInternalContest contest, IInternalController controller) throws IllegalContestState {

        if (contest == null) {
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

        int rank = 1;
        for (StandingsRecord sr : standingsRecords) {
            
            //start a new JSON element for the current standings record
            //if it's not the first rank being output, add a comma separator
            if (rank!=1) {
                buffer.append(",");
            }
            rank++ ;
            buffer.append('{');
            
            ClientId clientId = sr.getClientId();
            Account account = contest.getAccount(clientId);
            int teamNum = account.getClientId().getClientNumber();

            //add the rank and team number to the buffer
            buffer.append( pair("rank", sr.getRankNumber()) + "," + pair("team", teamNum) + "," );

            //add the 'score' components (num_solved and total_time) to the buffer
            long numSolved = sr.getNumberSolved();
            long totalTime = sr.getPenaltyPoints();
            buffer.append( "\"score\":{" + pair("num_solved",numSolved) + "," + pair("total_time",totalTime) + "},");
            
            //add the "problems" array to the buffer
            buffer.append("\"problems\":[");

            SummaryRow row = sr.getSummaryRow();
            
            //for each problem
            for (int i = 0; i < contest.getProblems().length; i++) {
                int problemIndex = i + 1;
                ProblemSummaryInfo summaryInfo = row.get(problemIndex);

                if (summaryInfo != null) {

                    // start the problem description in the buffer
                    if (i>0) {
                        buffer.append(",");
                    }
                    buffer.append("{");

                    // add the problem label (letter) to the buffer
                    String letter = contest.getProblem(summaryInfo.getProblemId()).getLetter();
                    buffer.append(pair("label", letter) + ",");

                    //get data on submitted runs
                    int numSubmitted = summaryInfo.getNumberSubmitted();
                    int numPending = summaryInfo.getPendingRunCount();
                    int numJudged = summaryInfo.getJudgedRunCount();
                    
//                    //debug:
//                    System.out.println ("StandingsJSON2016: " 
//                            + "Team: "+ teamNum + "  problem: '" + letter + "'" 
//                            + "  numSubmitted: " + numSubmitted
//                            + "  numPending: " + numPending 
//                            + "  numJudged: " + numJudged);
                    
                    //verify data makes sense
                    if ((numPending+numJudged) != numSubmitted) {
                        System.err.println ("StandingsJSON2016: mismatch: numPendingRuns+numJudgedRuns!=numSubmittedRuns ("
                                + "(" + numPending + "+" + numJudged + ")!=" + numSubmitted + ")");
                        controller.getLog().warning("StandingsJSON2016: mismatch: numPendingRuns+numJudgedRuns!=numSubmittedRuns ("
                                + "(" + numPending + "+" + numJudged + ")!=" + numSubmitted + ")");
                    }

                    // add the number of judging-completed runs to the buffer
                    buffer.append(pair("num_judged", numJudged) + ",");
                    
                    //add the number of pending runs to the buffer
                    buffer.append(pair("num_pending",numPending) + ",");
                    
                    //add the field indicating whether the problem has been solved
                    String isSolved = "false";
                    if (summaryInfo.isSolved()) {
                        isSolved = "true";
                    }
                    buffer.append("\"solved\":" + isSolved);
                    
                    //if the problem was solved, add the fields showing solution time and whether the solution was the first-to-solve
                    if (summaryInfo.isSolved()) {
                        long solutionTime = summaryInfo.getSolutionTime();
                        buffer.append("," + pair("time",solutionTime));
                        Problem prob = contest.getProblem(summaryInfo.getProblemId());
                        String fts = "false";
                        if (runStatistics.isFirstToSolve(clientId, prob)) {
                            fts = "true";
                        }
                        buffer.append("," + "\"first_to_solve\":" + fts);
                    }
                    
                    //close the problem description
                    buffer.append("}");

                } else {
                    controller.getLog().info("StandingsJSON2016: missing problem summary info for problem " + problemIndex);
                }
            }
            //close the "problems" array
            buffer.append("]");
            
            //close the entry for the current StandingsRecord (team)
            buffer.append('}');

        }
        
        //return the collected standings as elements of a JSON array
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
