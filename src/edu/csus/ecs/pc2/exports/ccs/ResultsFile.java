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
 * @version $Id: ResultsFile.java 193 2011-05-14 05:02:16Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/exports/ccs/ResultsFile.java $
public class ResultsFile {

    private static final String BRONZE = "bronze";

    private static final String GOLD = "gold";

    private static final String SILVER = "silver";

    private static final String HONORABLE = "honorable";

    private static final String TAB = "\t";

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

        Vector<String> lines = new Vector<String>();

        finalizeData = contest.getFinalizeData();
        
        if (finalizeData == null){
            throw new IllegalContestState("Contest not Finalized, can not create results file");
        }

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();

        Properties properties = getScoringProperties(contest);

        // Field Description Example Type
        // 1 Label results fixed string (always same value)
        // 2 Version number 1 integer

        lines.addElement("results" + TAB + "1");

        // return ranked teams
        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, properties);

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
            String award = getAwardMedal(record.getRankNumber(), finalizeData);

            lines.addElement(reservationId + TAB //
                    + record.getRankNumber() + TAB //
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

    private String getAwardMedal(int rankNumber, FinalizeData data) {

        // TODO CCS determine how to assign bronze and ranked

        if (rankNumber <= data.getGoldRank()) {
            return GOLD;
        } else if (rankNumber <= data.getSilverRank()) {
            return SILVER;
        } else if (rankNumber <= data.getBronzeRank()) {
            return BRONZE;
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
