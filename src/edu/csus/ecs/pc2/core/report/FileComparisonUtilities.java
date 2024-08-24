// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.CLICSScoreboard;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.standings.json.ProblemScoreRow;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Utilities for file comparison
 *
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilities {


    private static final String CONTEST_NOT_FINALIZED_MESSAGE = "Contest not finalized cannot create awards";

    // Until pc2 fixes the "time" field in the exported scoreboard, set this to true or you will see a lot
    // of differences in the comparison report.
    private static final boolean IGNORE_ZERO_SOLVE_TIMES = true;

    // CDS omits unsolved problems from the problems list for each ranked team, this flag tells the routine to ignore
    // that situtation.
    private static final boolean IGNORE_UNSOLVED_PROBLEMS = true;

    public static FileComparison createScoreboardJSONFileComparison(String jsonFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + jsonFilename;
        String secondFilename = targetDir + File.separator + jsonFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);
        int idx;

        long numberRows = 0;

        // as long as both files are present, continue with the full compare
        if(checkFilesExist(fileComparison, firstFilename, secondFilename)) {
            try {

                List<TeamScoreRow> firstTeamScoreRows = null;
                try {
                    firstTeamScoreRows = FileComparisonUtilities.loadTeamRows(fileComparison.getFirstFilename());
                } catch (JsonMappingException e) {
                    Exception rte = new RuntimeException("Error reading/parsing file " + firstFilename, e);
                    ExecuteUtilities.rethrow(rte);
                }

                List<TeamScoreRow> secondTeamScoreRows = null;
                try {
                    secondTeamScoreRows = FileComparisonUtilities.loadTeamRows(fileComparison.getSecondFilename());
                } catch (JsonMappingException e) {
                    Exception rte = new RuntimeException("Error reading/parsing file " + firstFilename, e);
                    ExecuteUtilities.rethrow(rte);
                }

                int minLines = firstTeamScoreRows.size();
                if (secondTeamScoreRows.size() < minLines) {
                    minLines = secondTeamScoreRows.size();
                }

                numberRows += minLines;

                for (int rowNum = 0; rowNum < minLines; rowNum++) {
                    TeamScoreRow firstRow = firstTeamScoreRows.get(rowNum);
                    TeamScoreRow secondScoreRow = secondTeamScoreRows.get(rowNum);
                    String rowString = "[" + rowNum + "]";

                    String fieldName = "rank";
                    String valueOne = Integer.toString(firstRow.getRank());
                    String valueTwo = Integer.toString(secondScoreRow.getRank());
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "num_solved";
                    valueOne = Long.toString(firstRow.getScore().getNum_solved());
                    valueTwo = Long.toString(secondScoreRow.getScore().getNum_solved());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "total_time";
                    valueOne = Long.toString(firstRow.getScore().getTotal_time());
                    valueTwo = Long.toString(secondScoreRow.getScore().getTotal_time());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "team_id";
                    valueOne = Long.toString(firstRow.getTeam_id());
                    valueTwo = Long.toString(secondScoreRow.getTeam_id());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    // compare problems
                    List<ProblemScoreRow> probs1 = firstRow.getProblems();
                    List<ProblemScoreRow> probs2 = secondScoreRow.getProblems();
                    if(probs1 != null && probs2 != null) {
                        ProblemScoreRow p1, p2;
                        int ptime;
                        String probString, probId;
                        boolean foundProblem;

                        int nProb1 = probs1.size();
                        int nProb2 = probs2.size();

                        for(int iProb = 0; iProb < nProb1; iProb++) {
                            probString = rowString + "problem[" + iProb + "]";

                            p1 = probs1.get(iProb);

                            // Do problem_id first, because we look up the problem in the primary by id
                            fieldName = "problem_id";
                            probId = p1.getProblem_id();
                            if(probId == null) {
                                fieldCompareRecord = new FieldCompareRecord(fieldName, null, null, "Missing main key in PC2 file", probString);
                                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                                continue;
                            }
                            valueOne = cleanupProblemId(probId);

                            // Find problem in p2
                            p2 = null;
                            foundProblem = false;
                            for(int iProb2 = 0; iProb2 < nProb2; iProb2++) {
                                p2 = probs2.get(iProb2);
                                probId = p2.getProblem_id();
                                if(probId == null) {
                                    fieldCompareRecord = new FieldCompareRecord(fieldName, null, null, "Missing main key in Primary file", probString);
                                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                                    continue;
                                }
                                valueTwo = cleanupProblemId(probId);
                                if(valueOne.equals(valueTwo)) {
                                    foundProblem = true;
                                    break;
                                }
                            }

                            if(!foundProblem) {
                                // problem not found in primary
                                if(!IGNORE_UNSOLVED_PROBLEMS) {
                                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, null, null, probString);
                                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                                }
                                continue;
                            }

                            fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, probString);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);

                            // label? (CLICS?)

                            fieldName = "num_judged";
                            valueOne = Integer.toString(p1.getNum_judged());
                            valueTwo = Integer.toString(p2.getNum_judged());
                            fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, probString);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);

                            fieldName = "num_pending";
                            valueOne = Integer.toString(p1.getNum_pending());
                            valueTwo = Integer.toString(p2.getNum_pending());
                            fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, probString);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);

                            fieldName = "solved";
                            valueOne = Boolean.toString(p1.isSolved());
                            valueTwo = Boolean.toString(p2.isSolved());
                            fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, probString);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);

                            // only matters, really, if solved is true, however, we will compare it to catch errors in implementations
                            ptime = p1.getTime();
                            if(ptime != 0 || !IGNORE_ZERO_SOLVE_TIMES) {
                                fieldName = "time";
                                valueOne = Integer.toString(ptime);
                                valueTwo = Integer.toString(p2.getTime());
                                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, probString);
                                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                            }

                            // first_to_solve ?

                            // Flag to indicate that we processed this record.
                            p2.setTime(-1);
                        }
                        for(int iProb2 = 0; iProb2 < nProb2; iProb2++) {
                            p2 = probs2.get(iProb2);
                            if(p2.getTime() == -1) {
                                continue;
                            }
                            if(p2.isSolved() || !IGNORE_UNSOLVED_PROBLEMS) {
                                probString = rowString + "problem[" + iProb2 + "]";
                                fieldName = "problem_id";
                                valueTwo = p2.getProblem_id();
                                // problem not found in primary
                                fieldCompareRecord = new FieldCompareRecord(fieldName, null, valueTwo, null, probString);
                                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                            }
                        }

                    }
                    // TODO 760 compare team names
                    //                fieldName = "team name";
                    //                valueOne = firstRow.getTeamName();
                    //                valueTwo = teamScoreRow.getTeamName();
                    //                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                    //                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                }

                for (int rowNum = minLines; rowNum < firstTeamScoreRows.size(); rowNum++) {
                    TeamScoreRow firstRow = firstTeamScoreRows.get(rowNum);
                    String rowString = "[" + rowNum + "]";

                    numberRows ++;
                    String valueTwo = null;

                    String fieldName = "rank";
                    String valueOne = Integer.toString(firstRow.getRank());
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    // TODO 760 Do we need to compare problems??  firstRowfirstRow.getProblems() ?
                    fieldName = "num solved";
                    valueOne = Long.toString(firstRow.getScore().getNum_solved());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "total_time";
                    valueOne = Long.toString(firstRow.getScore().getTotal_time());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "team_id";
                    valueOne = Long.toString(firstRow.getTeam_id());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    // TODO 760 compare team names
                    //                fieldName = "team name";
                    //                valueOne = firstRow.getTeamName();
                    //                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    //                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                }

                for (int rowNum = minLines; rowNum < secondTeamScoreRows.size(); rowNum++) {
                    String rowString = "[" + rowNum + "]";

                    numberRows ++;

                    TeamScoreRow teamScoreRow = secondTeamScoreRows.get(rowNum);

                    String valueOne = null;

                    String fieldName = "rank";
                    String valueTwo = Integer.toString(teamScoreRow.getRank());
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    // TODO 760 Do we need to compare problems??  firstRowfirstRow.getProblems() ?
                    fieldName = "num solved";
                    valueTwo = Long.toString(teamScoreRow.getScore().getNum_solved());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "total_time";
                    valueTwo = Long.toString(teamScoreRow.getScore().getTotal_time());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "team_id";
                    valueTwo = Long.toString(teamScoreRow.getTeam_id());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, rowString);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                }
            } catch (Exception e) {
                StaticLog.getLog().log(Level.WARNING, "Problem comparing scoreboard data/info", e);
                Utilities.printStackTrace(System.out, e);
            }
        }
        fileComparison.setNumberRows(numberRows);

        return fileComparison;

    }

    /**
     * Clean up problem ID. If it's a PC2 element id it will have "Id = " at the start.
     * In this case, strip that off, and also the element number
     *
     * there may be a hyphen in the id for real, so we get the right most one,
     * eg. doingthecontainershuffle-1--485644771414016799  -> doingthecontainershuffle-1
     * @param probId
     * @return
     */
    public static String cleanupProblemId(String probId) {
        int idx;

        if(probId.startsWith("Id = ")) {
            // skip "Id = " - we are stripping off the pc2 Elementid stuff
            probId = probId.substring(5);
            idx = probId.lastIndexOf('-');
            if(idx > 1) {
                // Now, see if the element number is negative, meaning 2 hyphens in a row
                if(probId.charAt(idx-1) == '-') {
                    idx--;
                }
                probId = probId.substring(0, idx);
            }
        }
        return(probId);
    }

    /**
     * Generates a FileComparison records comparing two award files.
     *
     * @param jsonFilename - name of the file to compare in each folder
     * @param sourceDir - PC2 (shadow) folder
     * @param targetDir - Primary or CDS folder
     * @param fileComparisonKey - used to get the compare key
     * @param ignoreEmptyAwards - if an award has no members (teams), ignore it if it's not in the other file.
     * @return
     */
    public static FileComparison createAwardJSONFileComparison(String jsonFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey, boolean ignoreEmptyAwards) {

        String firstFilename = sourceDir + File.separator + jsonFilename;
        String secondFilename = targetDir + File.separator + jsonFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        long numberRows = 0;

        // as long as both files are present, continue with the full compare
        if(checkFilesExist(fileComparison, firstFilename, secondFilename)) {
            try {

                List<CLICSAward> firstAwardRows = new ArrayList<CLICSAward>();
                try {
                    if (new File(firstFilename).exists()) {
                        firstAwardRows = loadAwardRows(fileComparison.getFirstFilename());
                    }
                } catch (JsonMappingException e) {
                    Exception rte = new RuntimeException("Error reading/parsing JSON file " + Constants.NL + firstFilename, e);
                    ExecuteUtilities.rethrow(rte);
                }

                List<CLICSAward> secondAwardRows = new ArrayList<CLICSAward>();
                try {
                    if (new File(secondFilename).exists()) {
                        secondAwardRows = FileComparisonUtilities.loadAwardRows(fileComparison.getSecondFilename());
                    }
                } catch (JsonMappingException e) {
                    Exception rte = new RuntimeException("Error reading/parsing JSON file " + Constants.NL + secondFilename, e);
                    ExecuteUtilities.rethrow(rte);
                }

                /**
                 * Map first awards (PC2) key to awards class
                 */
                Map<String, CLICSAward> firstFileMap = new HashMap<String, CLICSAward>();
                for (CLICSAward clicsAwardOne : firstAwardRows) {
                    String key = fileComparisonKey.getKey(clicsAwardOne);
                    firstFileMap.put(key, clicsAwardOne);
                }

                /**
                 * Map second awards (primary, cds, etc) key to awards class
                 */
                for (CLICSAward clicsAward : secondAwardRows) {
                    String key = fileComparisonKey.getKey(clicsAward);
                    CLICSAward firstAward = firstFileMap.get(key);

                    // key exists in both PC2 (first) and primary/cds (second) files, so compare each field
                    if (firstAward != null) {

                        String fieldName = "citation";
                        String valueOne = firstAward.getCitation();
                        String valueTwo = clicsAward.getCitation();
                        FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                        fileComparison.addfieldCompareRecord(fieldCompareRecord);

                        fieldName = "id";
                        valueOne = firstAward.getId();
                        valueTwo = clicsAward.getId();
                        fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                        fileComparison.addfieldCompareRecord(fieldCompareRecord);

                        fieldName = "team_ids";
                        valueOne = formatTeamList(firstAward.getTeam_ids());
                        valueTwo = formatTeamList(clicsAward.getTeam_ids());
                        fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                        fileComparison.addfieldCompareRecord(fieldCompareRecord);

                        // remove key since we found it
                        firstFileMap.remove(key);
                        numberRows++;
                    } else {
                        // Only deal with this record if it has teams for the award or we want to see empty awards
                        if(!ignoreEmptyAwards || clicsAward.getTeam_ids().length > 0) {
                            // No key in PC2 awards file, generate missing key error
                            FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(key, null, key, "No record with that key was found in the PC2 awards file", key);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);
                            numberRows++;
                       }
                    }
                }

                /**
                 * Any things left in the firstFileMap (PC2) are keys that PC2 has but the primary/cds does not
                 */
                Set<String> awardKeySet = firstFileMap.keySet();
                String[] awardkeys = awardKeySet.toArray(new String[awardKeySet.size()]);
                Arrays.sort(awardkeys);

                for (String key : awardkeys) {
                    CLICSAward firstAward = firstFileMap.get(key);
                    // Only deal with this record if it's valid and has teams for the award or we want to see empty awards
                    if(firstAward != null && (!ignoreEmptyAwards || firstAward.getTeam_ids().length > 0)) {
                        numberRows++;

                        FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(key, key, null, "A Record with that key was only found in the PC2 awards file", key);
                        fileComparison.addfieldCompareRecord(fieldCompareRecord);
                    }
                }

            } catch (Exception e) {
                StaticLog.getLog().log(Level.WARNING, "Problem comparing scoreboard data/info", e);
                ExecuteUtilities.rethrow(e);
            }
        }
        fileComparison.setNumberRows(numberRows);
        return fileComparison;
    }

    private static String formatTeamList(String[] teamIds) {
        Arrays.sort(teamIds);
        return String.join(", ", teamIds);
    }

    /**
     * Check if files exist.  If one or both don't, then add a record to the FileComparison object
     *
     * @param firstFilename - the source file (PC2)
     * @param secondFilename = the target file (Primary)
     *
     * return true if both files exist (no record created), false if one or both do not exist.
     */
    public static boolean checkFilesExist(FileComparison fileComparison, String firstFilename, String secondFilename) {
        // check if files exist, we do not want to cause an exception for nonexistant files
        boolean firstExists = new File(firstFilename).exists();
        boolean secondExists = new File(secondFilename).exists();
        FieldCompareRecord fieldCompareRecord = null;

        if(!firstExists) {
            if(!secondExists) {
                fieldCompareRecord = new FieldCompareRecord("FILE", firstFilename, secondFilename, "Can not find files", "FILE");
                fieldCompareRecord.createComparisonState(null, null);
            } else {
                fieldCompareRecord = new FieldCompareRecord("FILE", null, firstFilename, "Can not find file", "FILE");
                fieldCompareRecord.createComparisonState(null, secondFilename);
            }
        } else if(!new File(secondFilename).exists()) {
            fieldCompareRecord = new FieldCompareRecord("FILE", secondFilename, null, "Can not find file", "FILE");
            fieldCompareRecord.createComparisonState(firstFilename, null);
        }
        if(fieldCompareRecord != null) {
            fileComparison.addfieldCompareRecord(fieldCompareRecord);
            return(false);
        }
        return(true);
    }

    public static FileComparison createTSVFileComparison(String tsvFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + tsvFilename;
        String secondFilename = targetDir + File.separator + tsvFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        String[] columnList = { "CMS Id", "rank", "medal", "solved", "penalty", "last solved time" };

        long numberRows = 0;


        // as long as both files are present, continue with the full compare
        if(checkFilesExist(fileComparison, firstFilename, secondFilename)) {
            try {

                /**
                 * Map first results lines to cms id
                 */
                Map<String, String> firstFileMap = new HashMap<String, String>();

                String[] lines = Utilities.loadFile(firstFilename);

                validateResultsTSVFileContents(firstFilename, lines);

                for (String line : lines) {

                    if (!line.trim().startsWith("result")) {
                        String key = fileComparisonKey.getKey(line);
                        firstFileMap.put(key, line);
                    }
                }

                String[] secondFilelines = Utilities.loadFile(secondFilename);

                validateResultsTSVFileContents(secondFilename, secondFilelines);

                for (String secondLine : secondFilelines) {

                    String[] secondLineFields = secondLine.split(Constants.TAB);

                    if (!secondLine.trim().startsWith("result")) {
                        String key = fileComparisonKey.getKey(secondLine);
                        String firstLine = firstFileMap.get(key);

                        numberRows++;

                        if (firstLine == null) {
                            // found in second file but not in first
                            for (int i = 0; i < columnList.length; i++) {
                                String fieldName = columnList[i];
                                String valueTwo = secondLineFields[i];
                                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, null, valueTwo, null, key);
                                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                            }
                        } else {
                            // found matching cms id in both files
                            String fieldName = secondLineFields[0];

                            String[] firstLineFields = firstLine.split(Constants.TAB);

                            for (int i = 1; i < columnList.length; i++) {
                                String valueOne = firstLineFields[i];
                                String valueTwo = secondLineFields[i];
                                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                            }

                            firstFileMap.remove(key);
                        }
                    }
                }

                /**
                 * List of lines found in first file only.
                 */
                Set<String> remainingLinesFromFirstFileKeys = firstFileMap.keySet();
                String[] remainingKeys = remainingLinesFromFirstFileKeys.toArray(new String[remainingLinesFromFirstFileKeys.size()]);
                Arrays.sort(remainingKeys);

                for (String keyName : remainingKeys) {
                    String firstLine = firstFileMap.get(keyName);
                    String[] firstLineFields = firstLine.split(Constants.TAB);

                    numberRows++;

                    for (int i = 0; i < columnList.length; i++) {
                        String fieldName = columnList[i];
                        String valueOne = firstLineFields[i];
                        FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, null, null, keyName);
                        fileComparison.addfieldCompareRecord(fieldCompareRecord);
                    }
                }
            } catch (Exception e) {
                StaticLog.getLog().log(Level.WARNING, "Problem comparing results tsv data/info", e);
                Utilities.printStackTrace(System.out, e);
                ExecuteUtilities.rethrow(e);
            }
        }

        fileComparison.setNumberRows(numberRows);

        return fileComparison;
    }


    public static void validateResultsTSVFileContents(String filename) throws IOException {
        String[] lines = Utilities.loadFile(filename);
        validateResultsTSVFileContents(filename, lines);
    }

    /**
     * Validate results tsv file contents, if invalid throws exception
     * @param filename
     * @param fileLines
     */
    public static void validateResultsTSVFileContents(String filename, String[] fileLines) {

        if (fileLines == null || fileLines.length == 0) {
            throw new RuntimeException("Invalid results tsv file. "+Constants.NL+"No lines in file "+filename);
        }

        int linenum = 1;
        for (String line : fileLines) {

            if (! line.startsWith("results")) {
                String[] fields = line.split(Constants.TAB);

                if (fields.length < 6) {
                    if (CONTEST_NOT_FINALIZED_MESSAGE.contentEquals(line)){
                        throw new RuntimeException("Invalid results TSV file contents. " + Constants.NL + " in file " + filename + " line " + linenum);
                    } else {
                        throw new RuntimeException("Invalid results TSV file contents.  too few fields " + fields.length + Constants.NL + //
                                " line: '" + line + "'"+Constants.NL + " in file " + filename + " line " + linenum);
                    }
                }
            }
            linenum ++;
        }
    }
    public static String prettyPrint(Object JSONObject) throws JsonProcessingException {
        String json = JSONUtilities.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(JSONObject);
        return json;
    }

//    public static ObjectMapper getObjectMapper() {
//
//        // TODO REFACTOR move this into a utility class
//        if (objectMapper == null) {
//            objectMapper = new ObjectMapper();
//            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        }
//
//        return objectMapper;
//    }

    /**
     * Fetch cms id as key.
     *
     * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
     */
    public static class ResultTSVKey implements IFileComparisonKey{

        @Override
        public String getKey(Object object) {
            String line = (String) object;
            if (StringUtilities.isEmpty(line)) {
                return null;
            } else {
                String [] fields = line.split(Constants.TAB);
                return fields[0];
            }
        }
    }

    public static class AwardKey implements IFileComparisonKey{

        // [{"citation":"Contest winner","id":"winner","team_ids":["48"]},

        @Override
        public String getKey(Object object) {
            CLICSAward clicsAward = (CLICSAward) object;
            return clicsAward.getId();
        }
    }

    public static class ScoreboardJSONKey implements IFileComparisonKey{
//        {"rank":1,"team":48,"score":{"num_solved":11,"total_time":1443},"problems":[{"label":"A","num_judged":1,"num_pending":0,"solved":true,"time":25,"first_to_solve":false},{"label":"B","num_judged":1,"num_pending":0,"solved":true,"time":165,"first_to_solve":false},{"label":"C","num_judged":1,"num_pending":0,"solved":true,"time":69,"first_to_solve":false},{"label":"D","num_judged":1,"num_pending":0,"solved":false},{"label":"E","num_judged":1,"num_pending":0,"solved":true,"time":159,"first_to_solve":false},{"label":"F","num_judged":1,"num_pending":0,"solved":true,"time":94,"first_to_solve":false},{"label":"G","num_judged":1,"num_pending":0,"solved":true,"time":54,"first_to_solve":false},{"label":"H","num_judged":1,"num_pending":0,"solved":false},{"label":"I","num_judged":1,"num_pending":0,"solved":true,"time":16,"first_to_solve":false},{"label":"J","num_judged":1,"num_pending":0,"solved":true,"time":218,"first_to_solve":false},{"label":"K","num_judged":2,"num_pending":0,"solved":true,"time":88,"first_to_solve":false},{"label":"L","num_judged":1,"num_pending":0,"solved":true,"time":199,"first_to_solve":false},{"label":"M","num_judged":3,"num_pending":0,"solved":true,"time":296,"first_to_solve":false}]},

        @Override
        public String getKey(Object object) {
            TeamScoreRow row = (TeamScoreRow) object;
            return String.format("%04d", row.getRank()) + ":" + row.getTeam_id();
        }
    }

    public static List<CLICSScoreboard> getCLICSScoreboardList(String filename) throws JsonParseException, JsonMappingException, IOException {
        String [] lines = Utilities.loadFile(filename);
        String jsonString = String.join(" ", lines);

        List<CLICSScoreboard> scoreboardList = JSONUtilities.getObjectMapper().readValue(jsonString, new TypeReference<List<CLICSScoreboard>>() {});
//        for (CLICSScoreboard clicsScoreboard : scoreboardList) {
//            System.out.println("debug sb2 scoreboards "+clicsScoreboard);
//        }
        return scoreboardList;
    }

    public static List<CLICSScoreboard> getScoreboardJSON(String filename) {

        try {
            List<CLICSScoreboard> list = getCLICSScoreboardList(filename);
            return list;
        } catch (Exception e) {
            ExecuteUtilities.rethrow(e); // throws a RunTimeException, so no more code after this
            // compiler requires this dead code.
            return new ArrayList<CLICSScoreboard>();

        }
    }

    public static  List<TeamScoreRow> loadTeamRows(String scoreboardJSONFilename) throws IOException {

        List<TeamScoreRow> rows = new ArrayList<TeamScoreRow>();

        String[] lines = Utilities.loadFile(scoreboardJSONFilename);
        String firstLineString = String.join(" ", lines);

        CLICSScoreboard clicsScoreboard = JSONUtilities.getObjectMapper().readValue(firstLineString, CLICSScoreboard.class);

        if (clicsScoreboard != null)
        {
            rows = clicsScoreboard.getRows();
        }

        return rows;
    }

    public static List<CLICSAward> loadAwardRows(String awardsJSONFilename) throws IOException {

        List<CLICSAward> awardsList = new ArrayList<CLICSAward>();

        String[] lines = Utilities.loadFile(awardsJSONFilename);
        String jsonString = String.join(" ", lines);

        awardsList = JSONUtilities.getObjectMapper().readValue(jsonString, new TypeReference<List<CLICSAward>>() {});
        return awardsList;
    }

}
