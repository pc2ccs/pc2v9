package edu.csus.ecs.pc2.exports.ccs;

import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.StandingsRecord;

/**
 * Create results.tsv file.
 * 
 * @author pc2@ecs.csus.edu
 */
public class ResultsFile {

    private static final String BRONZE = "Bronze Medal";

    private static final String GOLD = "Gold Medal";

    private static final String SILVER = "Silver Medal";

    private static final String HONORABLE = "Honorable" ;

    private static final String TAB = "\t";

    private static final String DEFAULT_RESULT_FIELD_NAME =  "results";

    public static final String RESULTS_FILENAME = "results.tsv";

    private static final String RANKED = "Ranked";

    private FinalizeData finalizeData = null;

    public void setFinalizeData(FinalizeData finalizeData) {
        this.finalizeData = finalizeData;
    }

    /**
     * Create CCS restuls.tsv file contents.
     * 
     * @param contest
     * @return
     * @throws IllegalContestState
     */
    public String[] createTSVFileLines(IInternalContest contest) throws IllegalContestState {
        return createTSVFileLines(contest,DEFAULT_RESULT_FIELD_NAME);
    }
    
    /**
     * Input is a sorted ranking list.  What is the median number of problems solved.
     * copied from DefaultScoringAlgorithm, maybe it should be a common location?
     * @param srArray
     * @return median number of problems solved
     */
    private int getMedian(StandingsRecord[] srArray) {
        int median;
        if (srArray == null || srArray.length == 0) {
            median = 0;
        } else {
            if (srArray.length == 1) {
                median = srArray[0].getNumberSolved();
            } else {
                if (srArray.length % 2 == 0) {
                    // even number of entries
                    int high, low;
                    low = srArray[srArray.length/2-1].getNumberSolved();
                    high = srArray[(srArray.length+1)/2].getNumberSolved();
                    median = (low + high) /2;
                } else {
                    // odd number
                    median = srArray[(srArray.length+1)/2-1].getNumberSolved();
                }
            }
        }
        return median;
    }

    /**
     * Create CCS restuls.tsv file contents.
     * 
     * @param contest
     * @param resultFileTitleFieldName override title anem {@value #DEFAULT_RESULT_FIELD_NAME}. 
     * @return
     * @throws IllegalContestState
     */
    public String[] createTSVFileLines(IInternalContest contest, String resultFileTitleFieldName) throws IllegalContestState {

        Vector<String> lines = new Vector<String>();

        finalizeData = contest.getFinalizeData();
        
        if (finalizeData == null){
            throw new IllegalContestState("Contest not Finalized, can not create results file");
        }

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        scoringAlgorithm.setContest(contest);

        Properties properties = getScoringProperties(contest);

        // Field Description Example Type
        // 1 Label results fixed string (always same value)
        // 2 Version number 1 integer

        lines.addElement(resultFileTitleFieldName + TAB + "1");

        // return ranked teams
        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, properties);
        int median = getMedian(standingsRecords);
        // TODO finalizeData really needs a B instead of getBronzeRank
        int last_medal_rank = finalizeData.getBronzeRank();
        int last_solved_num = 0;
        int rankNumber = 0;
        for (StandingsRecord record : standingsRecords) {

            Account account = contest.getAccount(record.getClientId());

            // Then follow several lines with the following format (one per team).
            // Field Description Example Type
            // 1 Reservation ID 24314 integer
            // 2 Rank in contest 1 integer
            // 3 Award gold string
            // 4 Number of problems the team has solved 4 integer
            // 5 Total Time 534 integer
            // 6 Time of the last submission 233 integer

            String reservationId = account.getExternalId();
            boolean ranked = false;
            if (record.getNumberSolved() >= median) {
                ranked = true;
            }
            String award = getAwardMedal(record.getRankNumber(), finalizeData, ranked);
            String rank = "";
            if (!"honorable".equalsIgnoreCase(award)) {
                if (record.getRankNumber() > last_medal_rank && (last_solved_num == 0 ||  last_solved_num  < record.getNumberSolved())) {
                    last_solved_num = record.getNumberSolved();
                    rankNumber = record.getRankNumber();
                } else if (record.getRankNumber() > last_medal_rank && last_solved_num == record.getNumberSolved()) {
                    record.setRankNumber(rankNumber);
                }
                rank = Integer.toString(record.getRankNumber());
            }
            lines.addElement(reservationId + TAB //
                    + rank + TAB //
                    + award + TAB  // 
                    + record.getNumberSolved() //
                    + TAB + record.getPenaltyPoints() + TAB //
                    + record.getLastSolved());
        }

        return (String[]) lines.toArray(new String[lines.size()]);
    }

    /**
     * Determine and return award medal color.
     * 
     * <pre>
     * Award is a string with value "gold", "silver", "bronze", "ranked" 
     * or "honorable" as appropriate.
     * </pre>
     * 
     * @param rankNumber
     * @param finalizeData
     * @return
     */

    private String getAwardMedal(int rankNumber, FinalizeData data, boolean ranked) {

        // TODO CCS determine how to assign bronze and ranked

        if (rankNumber <= data.getGoldRank()) {
            return GOLD;
        } else if (rankNumber <= data.getSilverRank()) {
            return SILVER;
        } else if (rankNumber <= data.getBronzeRank()) {
            return BRONZE;
        } else if (ranked) {
            return RANKED;
        } else {
            return HONORABLE;
        }
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
    
    public String[] createTSVFileLinesTwo(IInternalContest contest) throws Exception {

        finalizeData = contest.getFinalizeData(); 

        DefaultScoringAlgorithm scoringAlgorithm = new DefaultScoringAlgorithm();

        String xmlString = scoringAlgorithm.getStandings(contest, new Properties(), null);

        String xsltFileName = "results.tsv.xsl";
        
        return XMLUtilities.transformToArray(xmlString, xsltFileName);
    }
 
}
