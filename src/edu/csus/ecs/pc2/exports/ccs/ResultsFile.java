// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.exports.ccs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.core.XMLUtilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.FinalsStandingsRecordComparator;
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

    private static final String HIGHEST_HONOR = "Highest Honors";

    private static final String HIGH_HONOR = "High Honors";

    private static final String HONOR = "Honors";

    private static final String HONORABLE = "Honorable";

    private static final String TAB = "\t";

    private static final String COMMA = ",";

    private static final String DEFAULT_RESULT_FIELD_NAME =  "results";

    public static final String RESULTS_FILENAME = "results.tsv";

    public static final String RESULTS_CSV_FILENAME = "results.csv";

    private static final String RANKED = "Ranked";

    private FinalizeData finalizeData = null;

    private FinalsStandingsRecordComparator comparator;

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
    public String[] createTSVFileLines(IInternalContest contest) {
        return createTSVFileLines(contest, DEFAULT_RESULT_FIELD_NAME);
    }

    public String [] createTSVFileLines(IInternalContest contest, Group group) {
        return createFileLines(contest, group, DEFAULT_RESULT_FIELD_NAME, true);
    }

    private String [] createTSVFileLines(IInternalContest contest, String resultFileTitleFieldName) {
        return createFileLines(contest, null, resultFileTitleFieldName, true);
    }

    /**
     * creates CCS results.csv file contents
     * @param contest
     * @param group (or null)
     * @return contents of results.csv (after header line)
     */
    public String[] createCSVFileLines(IInternalContest contest) {
        return createFileLines(contest, null, DEFAULT_RESULT_FIELD_NAME, false);
    }

    public String[] createCSVFileLines(IInternalContest contest, Group group) {
        return createFileLines(contest, group, DEFAULT_RESULT_FIELD_NAME, false);
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
     * Create CCS results.tsv file contents.
     *
     * @param contest
     * @param resultFileTitleFieldName override title anem {@value #DEFAULT_RESULT_FIELD_NAME}.
     * @return
     */
    public String[] createFileLines(IInternalContest contest, Group group, String resultFileTitleFieldName, boolean isTSV)  {

        Vector<String> lines = new Vector<String>();

        finalizeData = contest.getFinalizeData();

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        scoringAlgorithm.setContest(contest);

        Properties properties = getScoringProperties(contest);

        // Field Description Example Type
        // 1 Label results fixed string (always same value)
        // 2 Version number 1 integer

        if (isTSV) {
            lines.addElement(resultFileTitleFieldName + TAB + "1");
        } else {
            lines.addElement("teamId,rank,medalCitation,problemsSolved,totalTime,lastProblemTime,siteCitation,citation");
        }

        // return ranked teams
        StandingsRecord[] standingsRecords = null;
        try {
            List<Group> groupList = null;
            if(group != null) {
                groupList = new ArrayList<Group>();
                groupList.add(group);
            }
            standingsRecords = scoringAlgorithm.getStandingsRecords(contest, null,  groupList, properties, false, null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate standings ", e.getCause());
        }

        int median = getMedian(standingsRecords);

        if (finalizeData == null) {
            finalizeData = GenDefaultFinalizeData();
        }

        // TODO finalizeData really needs a B instead of getBronzeRank
        int lastMedalRank = finalizeData.getBronzeRank();
        int lastSolvedNum = 0;
        int rankNumber = 0;

        // resort standingsRecord based on lastMedalRank and median
        Vector<Account> accountVector = contest.getAccounts(Type.TEAM);
        Account[] accounts = accountVector.toArray(new Account[accountVector.size()]);
        AccountList accountList = new AccountList();
        for (Account account : accounts) {
            accountList.add(account);
        }
        comparator = new FinalsStandingsRecordComparator();
        comparator.setCachedAccountList(accountList);
        comparator.setLastRank(lastMedalRank);
        comparator.setMedian(median);
        comparator.setUseWFGroupRanking(finalizeData.isUseWFGroupRanking());
        Arrays.sort(standingsRecords, comparator);

        int realRank = 0;
        int highestHonorSolvedCount = standingsRecords[lastMedalRank - 1].getNumberSolved();
        int highHonorSolvedCount = highestHonorSolvedCount - 1;

        for (StandingsRecord record : standingsRecords) {
            realRank++;
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
            
            boolean isHighestHonor = false;
            boolean isHighHonor = false;
            boolean isHonor = false;

            if (finalizeData.isUseWFGroupRanking()) {
                if (record.getNumberSolved() >= highestHonorSolvedCount) {
                    isHighestHonor = true;
                } else if (record.getNumberSolved() >= highHonorSolvedCount) {
                    isHighHonor = true;
                } else if (record.getNumberSolved() >= median) {
                    isHonor = true;
                }
            } else if (record.getNumberSolved() >= median) {
                isHonor = true;
            }

            String award = getMedalCitation(record.getRankNumber(), finalizeData, isHighestHonor, isHighHonor, isHonor);
            if (record.getNumberSolved() == 0) {
                award = HONORABLE;
            }

            String rank = "";
            if (!HONORABLE.equalsIgnoreCase(award)) {
                if (finalizeData.isUseWFGroupRanking() && realRank > lastMedalRank) {
                    if (record.getNumberSolved() != lastSolvedNum) {
                        lastSolvedNum = record.getNumberSolved();
                        rankNumber = realRank;
                    }
                    record.setRankNumber(rankNumber);
                }
                rank = Integer.toString(record.getRankNumber());
            }

            if (isTSV) {
                lines.addElement(reservationId + TAB //
                        + rank + TAB //
                        + award + TAB  //
                        + record.getNumberSolved() + TAB //
                        + record.getPenaltyPoints() + TAB //
                        + record.getLastSolved());
            } else {
                // teamId,rank,medalCitation,problemsSolved,totalTime,lastProblemTime,siteCitation,citation
                lines.addElement(reservationId + COMMA //
                        + rank + COMMA //
                        + award + COMMA //
                        + record.getNumberSolved() + COMMA //
                        + record.getPenaltyPoints() + COMMA //
                        + record.getLastSolved() + COMMA // then siteCitation
                        + COMMA);  // then citation
            }
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Determine whether a team receives a medal or Honor citaion.
     *
     * <pre>
     * Award is a string with value "gold", "silver", "bronze", "highest honor", "high honor", "honor" if WF type rankings or "ranked"
     * or "honorable" as appropriate.
     * </pre>
     *
     * @param rankNumber,isHighestHonor,isHighHonor,isHonor
     * @param finalizeData
     * @return medalOrCitation
     */

    private String getMedalCitation(int rankNumber, FinalizeData data, boolean isHighestHonor, boolean isHighHonor, boolean isHonor) {

        String medalOrCitation = getAwardMedal(rankNumber, data);
        if (medalOrCitation == null) {
            medalOrCitation = getCitation(data, isHighestHonor, isHighHonor, isHonor);
        }
        return medalOrCitation;
    }

    /**
     * Determine and return award medal color.
     *
     * <pre>
     * Award is a string with value "gold", "silver", "bronze"
     * or null as appropriate.
     * </pre>
     *
     * @param rankNumber
     * @param finalizeData
     * @return medalColor
     */
    private String getAwardMedal(int rankNumber, FinalizeData data) {

        if (rankNumber <= data.getGoldRank()) {
            return GOLD;
        } else if (rankNumber <= data.getSilverRank()) {
            return SILVER;
        } else if (rankNumber <= data.getBronzeRank()) {
            return BRONZE;
        } else {
            return null;
        }
    }

    /**
     * Determine what citation team will receive based on new WF rules or are they Ranked if WF rules not used
     *
     * <pre>
     * Award is a string with value "highest honor", "high honor", "honor" if WF type rankings or "ranked"
     * or "honorable" as appropriate.
     * </pre>
     *
     * @param isHighestHonor,isHighHonor,isHonor
     * @param finalizeData
     * @return citation
     */

     private String getCitation(FinalizeData data, boolean isHighestHonor, boolean isHighHonor, boolean isHonor) {

        // TODO CCS determine how to assign bronze and ranked
        if (!(isHighestHonor || isHighHonor || isHonor)) {
            return HONORABLE;
        } else if (!data.isUseWFGroupRanking()) {
            return RANKED;
        } else if (isHighestHonor) {
            return HIGHEST_HONOR;
        } else if (isHighHonor) {
            return HIGH_HONOR;
        } else {
            return HONOR;
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
        String[] keys = defProperties.keySet().toArray(new String[defProperties.keySet().size()]);
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

    /**
     * Generate some default finalize data so we can make a results.tsv before the contest is finalized.
     * @return FinalizeData object
     */
    private FinalizeData GenDefaultFinalizeData()
    {
        finalizeData = new FinalizeData();
        finalizeData.setGoldRank(4);
        finalizeData.setSilverRank(8);
        finalizeData.setBronzeRank(12);
        finalizeData.setCertified(false);
        finalizeData.setComment("Preliminary Results - Contest not Finalized");
        finalizeData.setUseWFGroupRanking(true);
        return(finalizeData);
    }
}
