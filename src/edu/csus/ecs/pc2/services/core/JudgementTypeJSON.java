package edu.csus.ecs.pc2.services.core;

import java.util.Properties;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Judgement Type JSON.
 * 
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
//TODO CLICS change JudgementTypeJSON to use ObjectMapper
public class JudgementTypeJSON extends JSONUtilities {
    
    /**
     * Is the SE judgement penalized?
     * 
     */
    private boolean securityViolationApplyPenalty = true;

    /**
     * Is the CE judgement penalized?
     */
    private boolean compilationErrorApplyPenalty = true;
    
    public void updatePenaltySettings(IInternalContest contest) {

        Properties properties = contest.getContestInformation().getScoringProperties();

        if (properties != null) {
            securityViolationApplyPenalty = 0 != getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION);
            compilationErrorApplyPenalty = 0 != getPropIntValue(properties, DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR);
        }
    }

    public String createJSON(IInternalContest contest, Judgement judgement) {
        
        /**
         *      ID  yes     no  provided by CCS     identifier of the judgement type. Usable as a label, typically a 2-3 letter capitalized shorthand, see Problem Format
        name    string  yes     no  provided by CCS     name of the judgement
        penalty     boolean     depends     no  provided by CCS     whether this judgement causes penalty time; should be present if and only if contest:penalty_time is present
        solved 
         */

        StringBuilder stringBuilder = new StringBuilder();

        appendPair(stringBuilder, "id", judgement.getAcronym());
        stringBuilder.append(", ");

        appendPair(stringBuilder, "name", judgement.getDisplayName());
        stringBuilder.append(", ");

        boolean penalized = isPenalizedJudgement(contest, judgement);

        appendPair(stringBuilder, "penalty", penalized);
        stringBuilder.append(", ");

        boolean solved = isSolved(judgement);
        appendPair(stringBuilder, "solved", solved);

        return stringBuilder.toString();
    }
    
    /**
     * Is there a point penalty for this judgement?
     * @param contest
     * @param judgement
     * @return
     */
    private boolean isPenalizedJudgement(IInternalContest contest, Judgement judgement) {

        boolean usePenalty = true;

        if (isSolved(judgement)) {
            return false;
        }

        if (Judgement.ACRONYM_COMPILATION_ERROR.equals(judgement.getAcronym())) {
            usePenalty = isCEPenalty();
        } else if (Judgement.ACRONYM_SECURITY_VIOLATION.equals(judgement.getAcronym())) {
            usePenalty = isSEPenalty();
        } // else - no elss fall through

        return usePenalty;
    }
    


    boolean isSEPenalty() {
        return securityViolationApplyPenalty;
    }

    boolean isCEPenalty() {
        return compilationErrorApplyPenalty;
    }
    
    private boolean isSolved(Judgement judgement) {
        return Judgement.ACRONYM_ACCEPTED.equals(judgement.getAcronym());
    }
    
    
    /**
     * Return int value for key.
     * @param key property to lookup
     * @param properties 
     */
    int getPropIntValue(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value != null && value.length() > 0 && isAllDigits(value)) {
            Integer i = Integer.parseInt(value);
            return i.intValue();
        } else {
            return 0;
        }
    }


    /**
     * Does the string only contain digits?  
     * 
     * @param value
     * @return
     */
    boolean isAllDigits(String value) {
        return value != null && value.matches("[0-9]+");
    }


}
