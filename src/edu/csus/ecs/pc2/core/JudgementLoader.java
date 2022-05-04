// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
     * @deprecated use {@link #loadJudgementsFromIni(IInternalContest, String)}
     */
    public boolean loadedJudgementsFromIni(IInternalContest contest, String filename) {

        if (new File(filename).exists()) {

            String[] lines = Utilities.loadINIFile(filename);
            return loadedJudgements(contest, lines);
        }

        return false;
    }
    
    /**
     * Loads reject.ini file contents into Judgements.
     * 
     * 
     * If file contains a AC acronym judgement, then that line and its judgment name are used/loaded.
     * 
     * Returns false if cannot read reject.ini file or reject.ini file is empty (perhaps only containing comments).
     * 
     * @return true if loaded, false if could not find line or read file.
     */
    public static boolean loadJudgementsFromIni(IInternalContest contest, String filename) {

        if (new File(filename).exists()) {
            String[] lines = Utilities.loadINIFile(filename);
            return loadJudgements(contest, lines);
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
     * @deprecated Used {@link #loadJudgements(IInternalContest, String[])}
     */
    public boolean loadedJudgements(IInternalContest contest, String[] lines) {
        return loadJudgements(contest, lines);
    }
    
    /**
     * Load judgements.
     * 
     * @param contest
     * @param lines
     * @return
     */
    public static boolean loadJudgements(IInternalContest contest, String[] lines) {

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

            if (line.trim().startsWith("#") || line.trim().length() == 0) {
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
                Judgement judgement = new Judgement(judgementText, acronym);
                contest.addJudgement(judgement);
            }
        }

        return true;
    }

    protected static String getJudgementText(String line) {
        String[] fields = line.split("[|]");
        if (fields.length > 1) {
            return fields[0].trim();
        } else {
            return line.trim();
        }
    }

    protected static String getAcronym(String line) {
        String[] fields = line.split("[|]");
        if (fields.length > 1) {
            return fields[1].trim();
        } else {
            return null;
        }
    }
    
    
    /**
     * Loads default judgements.
     * 
     * This method will unconditionally add judgements to the contest model.
     * 
     * @param contest
     */
    public static void loadDefaultJudgements(IInternalContest contest) {

        String[] judgementNames = { //
                "Yes", // 
                "No - Compilation Error", // 
                "No - Run-time Error", // 
                "No - Time Limit Exceeded", // 
                "No - Wrong Answer", // 
                "No - Excessive Output", // 
                "No - Output Format Error", // 
                "No - Other - Contact Staff" //
        };
        String [] judgementAcronyms = {
                Judgement.ACRONYM_ACCEPTED, // 
                Judgement.ACRONYM_COMPILATION_ERROR, //
                Judgement.ACRONYM_RUN_TIME_ERROR, //
                Judgement.ACRONYM_TIME_LIMIT_EXCEEDED, //
                Judgement.ACRONYM_WRONG_ANSWER, //
                Judgement.ACRONYM_EXCESSIVE_OUTPUT, //
                Judgement.ACRONYM_OUTPUT_FORMAT_ERROR, //
                Judgement.ACRONYM_OTHER_CONTACT_STAFF, //
        };
        
        int i = 0;
        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName, judgementAcronyms[i]);
            contest.addJudgement(judgement);
            i++;
        }
    }
    
    /**
     * initial load judgements into contest model, will not overwrite existing judgements.
     * 
     * @param contest
     * @param contactingRemoteServer
     * @return
     */
    public static String loadJudgements(IInternalContest contest, boolean contactingRemoteServer) {
        return loadJudgements(contest, contactingRemoteServer, ".");
    }
    
    /**
     * initial load judgements into contest model, will not overwrite existing judgements.
     * 
     * @param contest
     * @param contactingRemoteServer
     * @param startupDirectory location where reject.ini might be found.
     * @return comment that describes the change made
     */
    public static String loadJudgements(IInternalContest contest, boolean contactingRemoteServer, String startupDirectory) {

        String msg = "";

        if (!contactingRemoteServer) {

            msg = "Judgements already loaded ";

            if (contest.getJudgements().length == 0) {

                String rejectFilename = startupDirectory + File.separator + Constants.JUDGEMENT_INIT_FILENAME;
                if (loadJudgementsFromIni(contest, rejectFilename)) {
                    msg = "Loaded judgements from " + rejectFilename;
                } else {
                    msg = "Judgements not loaded, no judgement file found at "+rejectFilename;
                }
            }
        } else {
            msg = "Judgements will be loaded from a remote site";
        }

        return msg;
    }
}
