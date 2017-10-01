package edu.csus.ecs.pc2.exports.ccs;

import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.scoring.StandingsRecord;
import edu.csus.ecs.pc2.core.scoring.SummaryRow;

/**
 * Standings information in CLI 2016 JSON format.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: StandingsJSON.java 341 2013-06-21 10:53:25Z laned $
 */

public class ContestAPIStandingsJSON {

    private IInternalContest model;
    private IInternalController controller;
    
    /**
     * Returns a JSON string describing the current contest standings in the format defined by the 2016 CLI JSON Scoreboard.
     * The format follows this JSON example, from the CLI Wiki JSON Scoreboard page:
     * 
     * <pre>
     *  [ {
     *     "rank":1,"team_id":42,"score":{"num_solved":3,"total_time":340},
     *     "problems":[
     *       {"problem_id": "1","num_judged":3,"num_pending":1,"solved":false},
     *       {"problem_id":"2","num_judged":1,"num_pending":0,"solved":true,"time":20},
     *       {"problem_id":"3","num_judged":2,"num_pending":0,"solved":true,"time":55},
     *       {"problem_id":"4","num_judged":0,"num_pending":0,"solved":false},
     *       {"problem_id":"5","num_judged":3,"num_pending":0,"solved":true,"time":205}
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
    public String createJSON(IInternalContest contest, IInternalController inController) throws IllegalContestState {

        model = contest;
        controller = inController;

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();

        if (contest != null) {
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

            for (StandingsRecord sr : standingsRecords) {
                dumpStandingRecord(mapper, childNode, sr);
            }
        }
        // return the collected standings as elements of a JSON array
        return childNode.toString();
    }

    private void dumpStandingRecord(ObjectMapper mapper, ArrayNode childNode, StandingsRecord sr) {
        ClientId clientId = sr.getClientId();
        Account account = model.getAccount(clientId);
        int teamNum = account.getClientId().getClientNumber();
        ObjectNode element = mapper.createObjectNode();
        
        element.put("rank", sr.getRankNumber());
        element.put("team_id", new Integer(teamNum).toString());

        //add the 'score' components (num_solved and total_time) to the buffer
        long numSolved = sr.getNumberSolved();
        long totalTime = sr.getPenaltyPoints();
        ObjectNode scoreElement = mapper.createObjectNode();
        scoreElement.put("num_solved",  numSolved);
        scoreElement.put("total_time", totalTime);
        element.set("score", scoreElement);
        
        ArrayNode problemsArray = mapper.createArrayNode();
        SummaryRow row = sr.getSummaryRow();
        
        //for each problem
        for (int i = 0; i < model.getProblems().length; i++) {
            ObjectNode problemsNode = mapper.createObjectNode();
            int problemIndex = i + 1;
            ProblemSummaryInfo summaryInfo = row.get(problemIndex);

            if (summaryInfo != null) {

                problemsNode.put("problem_id", summaryInfo.getProblemId().toString());

                //get data on submitted runs
                int numSubmitted = summaryInfo.getNumberSubmitted();
                int numPending = summaryInfo.getPendingRunCount();
                int numJudged = summaryInfo.getJudgedRunCount();
                
                //verify data makes sense
                if ((numPending+numJudged) != numSubmitted) {
                    System.err.println ("StandingsJSON2016: mismatch: numPendingRuns+numJudgedRuns!=numSubmittedRuns ("
                            + "(" + numPending + "+" + numJudged + ")!=" + numSubmitted + ")");
                    controller.getLog().warning("StandingsJSON2016: mismatch: numPendingRuns+numJudgedRuns!=numSubmittedRuns ("
                            + "(" + numPending + "+" + numJudged + ")!=" + numSubmitted + ")");
                }

                // add the number of judging-completed runs to the buffer
                problemsNode.put("num_judged", numJudged);
                
                //add the number of pending runs to the buffer
                problemsNode.put("num_pending",numPending);
                
                //add the field indicating whether the problem has been solved
                problemsNode.put("solved", summaryInfo.isSolved());
                
                //if the problem was solved, add the fields showing solution time and whether the solution was the first-to-solve
                if (summaryInfo.isSolved()) {
                    long solutionTime = summaryInfo.getSolutionTime();
                    problemsNode.put("time",solutionTime);
                }
            }
            problemsArray.add(problemsNode);
        }
        element.set("problems", problemsArray);
        childNode.add(element);
    }
}
