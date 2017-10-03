package edu.csus.ecs.pc2.core;

import java.io.File;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;

/**
 * Provides a way to load reject.ini file.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class JudgementLoader {

    /**
     * Loads reject.ini file contents into Judgements.
     * 
     * If finds reject.ini file, reads file. Adds Yes judgement, then prepends "No - " onto each entry from the reject.ini file and returns true.
     * 
     * If file contains a AC acronym judgement, then that line and its judgment name are used/loaded.
     * 
     * Returns false if cannot read reject.ini file or reject.ini file is empty (perhaps only containing comments).
     * 
     * @return true if loaded, false if could not find line or read file.
     */
    public boolean loadedJudgementsFromIni(IInternalContest contest, String filename) {

        if (new File(filename).exists()) {

            String[] lines = Utilities.loadINIFile(filename);
            return loadedJudgements(contest, lines);
        }

        return false;
    }

    
    /**
     * Load judgements.
     * 
     * @see #loadedJudgementsFromIni(IInternalContest, String)
     * 
     * @param contest
     * @param lines
     * @return true if loaded, false if no lines
     */
    public boolean loadedJudgements(IInternalContest contest, String[] lines) {

        if (lines == null || lines.length == 0) {
            return false;
        }

        Judgement yesJudgement = new Judgement("Yes", Judgement.ACRONYM_ACCEPTED);

        for (String line : lines) {
            if (Judgement.ACRONYM_ACCEPTED.equals(getAcronym(line))) {
                yesJudgement = new Judgement(getJudgementText(line), Judgement.ACRONYM_ACCEPTED);
            }
        }

        contest.addJudgement(yesJudgement);
        int offset = contest.getJudgements().length;
        
        for (String line : lines) {
            
            if (line.trim().startsWith("#") || line.trim().length() == 0){
                continue;
            }

            String judgementText = getJudgementText(line);
            String acronym = getAcronym(line);

            if (acronym == null) {
                String waNumber = String.format("%03d", offset++);
                acronym = Judgement.ACRONYM_WRONG_ANSWER + waNumber;
            }

            // Already loaded Yes 
            if (!Judgement.ACRONYM_ACCEPTED.equals(getAcronym(line))) {
                Judgement judgement = new Judgement("No - " + judgementText, acronym);
                contest.addJudgement(judgement);
            }
        }

        return true;
    }

    protected String getJudgementText(String line) {
        String[] fields = line.split("[|]");
        if (fields.length > 1) {
            return fields[0].trim();
        } else {
            return line.trim();
        }
    }

    protected String getAcronym(String line) {
        String[] fields = line.split("[|]");
        if (fields.length > 1) {
            return fields[1].trim();
        } else {
            return null;
        }
    }

}
