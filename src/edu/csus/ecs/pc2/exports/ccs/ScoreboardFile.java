package edu.csus.ecs.pc2.exports.ccs;

import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.ProblemSummaryInfo;
import edu.csus.ecs.pc2.core.scoring.StandingsRecord;
import edu.csus.ecs.pc2.core.scoring.SummaryRow;

/**
 * Create scoreboard.tsv file.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: ScoreboardFile.java 180 2011-04-11 00:36:50Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/exports/ccs/ScoreboardFile.java $
public class ScoreboardFile {

    private static final String TAB = "\t";

    /**
     * Create CCS scoreboard.tsv file contents.
     * 
     * @param contest
     * @return
     * @throws IllegalContestState
     */
    public String[] createTSVFileLines(IInternalContest contest) throws IllegalContestState {

        Vector<String> lines = new Vector<String>();

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();

        Properties properties = getScoringProperties(contest);

        // Field Description Example Type
        // 1 Label scoreboard fixed string (always same value)
        // 2 Version number 1 integer

        lines.addElement("scoreboard" + TAB + "1");

        // return ranked teams
        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, properties);

        for (StandingsRecord record : standingsRecords) {

            Account account = contest.getAccount(record.getClientId());

            String reservationId = account.getExternalId();

            // Then follow several lines with the following format (one per team).
            // Field Description Example Type
            // 1 Institution name University of Virginia string
            // 2 Reservation ID 24314 integer
            // 3 Position in contest 1 integer
            // 4 Number of problems the team has solved 4 integer
            // 5 Total Time 534 integer
            // 6 Time of the last accepted submission 233 integer

            String line = account.getShortSchoolName() + TAB //
                    + reservationId + TAB //
                    + record.getRankNumber() + TAB //
                    + record.getNumberSolved() + TAB //
                    + record.getPenaltyPoints() + TAB //
                    + record.getLastSolved();

            /**
             * Loop through problems
             */

            SummaryRow summaryRow = record.getSummaryRow();

            int problemNumber = 1;

            for (Problem problem : contest.getProblems()) {

                if (problem.isActive()) {

                    // 6 + 2i - 1 Number of submissions for problem i 2 integer
                    // 6 + 2i Time when problem i was solved 233 integer

                    ProblemSummaryInfo problemSummaryInfo = summaryRow.get(problemNumber);

                    if (problemSummaryInfo != null) {

                        line += TAB + problemSummaryInfo.getNumberSubmitted() //
                                + TAB + problemSummaryInfo.getSolutionTime();

                    } else {
                        line += TAB + "0" + TAB + "-1";
                    }
                }

                problemNumber++;
            }
            lines.addElement(line);
        }

        return (String[]) lines.toArray(new String[lines.size()]);

    }

    protected Properties getScoringProperties(IInternalContest contest) {

        Properties properties = contest.getContestInformation().getScoringProperties();
        if (properties == null) {
            properties = new Properties();
        }

        Properties defProperties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Fill in with default properties if not using them.
         */
        String[] keys = (String[]) defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
        for (String key : keys) {
            if (!properties.containsKey(key)) {
                properties.put(key, defProperties.get(key));
            }
        }

        return properties;
    }

}
