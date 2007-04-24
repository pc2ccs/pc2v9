package edu.csus.ecs.pc2.core.scoring;

import edu.csus.ecs.pc2.core.model.IModel;

/**
 * Default Scoring Algorithm.
 * 
 * This class implements the standard (default) scoring algorithm,
 * which ranks all teams according to number of problems solved,
 * then according to "penalty points" computed by multiplying the
 * number of "NO" runs on solved problems by the PenaltyPoints value
 * specified in the contest configuration, then finally according to
 * earliest time of last solution (with ties at that level broken
 * alphabetically).  This is the "standard" algorithm used in many ICPC
 * Regional Contests.
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$
public class DefaultScoringAlgorithm implements IScoringAlgorithm {

    public String getStandings(IModel theContest) {
        // TODO replace hard-coded XML return string with computed one
        return getHardCodedResults();
    }

    private String getHardCodedResults() {
        String result = 
            "<?xml version='1.0' encoding='utf-8'?>"
            + "<contestStandings>"
            + "<standingsHeader>"
                + "<contestTitle> Sample Contest Title </contestTitle>"
                + "<contestDate>   16 July 2008  </contestDate>"
                + "<contestElapsedMinutes>   150   </contestElapsedMinutes>"
                + "<contestRemainingMinutes>  30   </contestRemainingMinutes>"
                + "<contestScoreboardUpdateState>  NO_MORE_UPDATES  </contestScoreboardUpdateState>"
                + "<problemList>"
                    + "<problem><problemTitle>\"Prob A\"</problemTitle><balloonColor>\"red\"</balloonColor></problem>"
                    + "<problem><problemTitle>\"Prob B\"</problemTitle><balloonColor>\"green\"</balloonColor></problem>"
                    + "<problem><problemTitle>\"Prob C\"</problemTitle></problem>"
                + "</problemList>"
            + "</standingsHeader>"
        
            + "<teamStanding>"
                + "<teamName> Sparkles </teamName><teamRank> 1 </teamRank><teamNumber> 100 </teamNumber><teamPoints> 600 </teamPoints><teamGroup> 1 </teamGroup>"
                + "<runList>"
                    + "<run><submitTime>\"35\"</submitTime><language>\"Java\"</language><problem>\"1\"</problem><result>\"NO\"</result></run>"
                    + "<run><submitTime>\"40\"</submitTime><language>\"Java\"</language><problem>\"1\"</problem><result>\"YES\"</result></run>"
                    + "<run><submitTime>\"45\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"NO\"</result></run>"
                    + "<run><submitTime>\"50\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"BEING_JUDGED\"</result></run>"
                    + "<run><submitTime>\"55\"</submitTime><language>\"Java\"</language><problem>\"3\"</problem><result>\"PENDING\"</result></run>"
                + "</runList>"
            + "</teamStanding>"
        
            + "<teamStanding>"
            + "<teamName> Diamonds </teamName><teamRank> 2 </teamRank><teamNumber> 200 </teamNumber><teamPoints> 700 </teamPoints><teamGroup> 1 </teamGroup>"
            + "<runList>"
                + "<run><submitTime>\"28\"</submitTime><language>\"Java\"</language><problem>\"1\"</problem><result>\"YES\"</result></run>"
                + "<run><submitTime>\"38\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"NO\"</result></run>"
            + "</runList>"
            + "</teamStanding>"

            + "<teamStanding>"
            + "<teamName> Rubies </teamName><teamRank> 1 </teamRank><teamNumber> 200 </teamNumber><teamPoints> 400 </teamPoints><teamGroup> 2 </teamGroup>"
            + "<runList>"
                + "<run><submitTime>\"20\"</submitTime><language>\"Java\"</language><problem>\"1\"</problem><result>\"YES\"</result></run>"
                + "<run><submitTime>\"30\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"NO\"</result></run>"
                + "<run><submitTime>\"40\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"BEING_JUDGED\"</result></run>"
            + "</runList>"
            + "</teamStanding>"

            + "<teamStanding>"
            + "<teamName> Garnets </teamName><teamRank> 1 </teamRank><teamNumber> 250 </teamNumber><teamPoints> 475 </teamPoints><teamGroup> 1 </teamGroup>"
            + "<runList>"
                + "<run><submitTime>\"33\"</submitTime><language>\"Java\"</language><problem>\"1\"</problem><result>\"NO\"</result></run>"
                + "<run><submitTime>\"44\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"NO\"</result></run>"
                + "<run><submitTime>\"55\"</submitTime><language>\"C++\"</language><problem>\"2\"</problem><result>\"YES\"</result></run>"
            + "</runList>"
            + "</teamStanding>" ;
        return result ;
    }
}
