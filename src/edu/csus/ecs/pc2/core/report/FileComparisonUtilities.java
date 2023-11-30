// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Utilities for file comparison
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilities {
    
    
    private static final String CONTEST_NOT_FINALIZED_MESSAGE = "Contest not finalized cannot create awards";

    public static FileComparison createScoreboardJSONFileComparison(String jsonFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + jsonFilename;
        String secondFilename = targetDir + File.separator + jsonFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        long numberRows = 0;

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

                String fieldName = "rank";
                String valueOne = Integer.toString(firstRow.getRank());
                String valueTwo = Integer.toString(secondScoreRow.getRank());
                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                // TODO 760 Do we need to compare problems??   firstRowfirstRow.getProblems() ?
                fieldName = "num solved";
                valueOne = Long.toString(firstRow.getScore().getNum_solved());
                valueTwo = Long.toString(secondScoreRow.getScore().getNum_solved());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                fieldName = "total_time";
                valueOne = Long.toString(firstRow.getScore().getTotal_time());
                valueTwo = Long.toString(secondScoreRow.getScore().getTotal_time());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                fieldName = "team_id";
                valueOne = Long.toString(firstRow.getTeam_id());
                valueTwo = Long.toString(secondScoreRow.getTeam_id());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                // TODO 760 compare team names
                //                fieldName = "team name";
                //                valueOne = firstRow.getTeamName();
                //                valueTwo = teamScoreRow.getTeamName();
                //                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                //                fileComparison.addfieldCompareRecord(fieldCompareRecord);

            }

            for (int rowNum = minLines; rowNum < firstTeamScoreRows.size(); rowNum++) {
                TeamScoreRow firstRow = firstTeamScoreRows.get(rowNum);
                
                numberRows ++;
                String valueTwo = null;
                
                String fieldName = "rank";
                String valueOne = Integer.toString(firstRow.getRank());
                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                // TODO 760 Do we need to compare problems??  firstRowfirstRow.getProblems() ?
                fieldName = "num solved";
                valueOne = Long.toString(firstRow.getScore().getNum_solved());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                fieldName = "total_time";
                valueOne = Long.toString(firstRow.getScore().getTotal_time());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                fieldName = "team_id";
                valueOne = Long.toString(firstRow.getTeam_id());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                // TODO 760 compare team names
                //                fieldName = "team name";
                //                valueOne = firstRow.getTeamName();
                //                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                //                fileComparison.addfieldCompareRecord(fieldCompareRecord);
            }
            
            for (int rowNum = minLines; rowNum < firstTeamScoreRows.size(); rowNum++) {

                numberRows ++;
                
                TeamScoreRow teamScoreRow = firstTeamScoreRows.get(rowNum);
                
                String valueOne = null;

                String fieldName = "rank";
                String valueTwo = Integer.toString(teamScoreRow.getRank());
                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                // TODO 760 Do we need to compare problems??  firstRowfirstRow.getProblems() ?
                fieldName = "num solved";
                valueTwo = Long.toString(teamScoreRow.getScore().getNum_solved());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                fieldName = "total_time";
                valueTwo = Long.toString(teamScoreRow.getScore().getTotal_time());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

                fieldName = "team_id";
                valueTwo = Long.toString(teamScoreRow.getTeam_id());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, valueOne);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);

            }
        } catch (Exception e) {
            StaticLog.getLog().log(Level.WARNING, "Problem comparing scoreboard data/info", e);
            Utilities.printStackTrace(System.out, e);
        }

        fileComparison.setNumberRows(numberRows);

        return fileComparison;

    }
//   

    public static FileComparison createAwardJSONFileComparison(String jsonFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + jsonFilename;
        String secondFilename = targetDir + File.separator + jsonFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        long numberRows = 0;
        
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
             * Map first awards citation to awards class
             */
            Map<String, CLICSAward> firstFileMap = new HashMap<String, CLICSAward>();
            for (CLICSAward clicsAwardOne : firstAwardRows) {
                String key = fileComparisonKey.getKey(clicsAwardOne);
                firstFileMap.put(key, clicsAwardOne);
                
              
            }
            
            for (CLICSAward clicsAward : secondAwardRows) {
                String key = fileComparisonKey.getKey(clicsAward);
                CLICSAward firstAward = firstFileMap.get(key);
                
                numberRows++;
                
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
                    valueTwo = formatTeamList(firstAward.getTeam_ids());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                    
                    firstFileMap.remove(key);
                    
                    
                } else {
                    String fieldName = "citation";
                    
                    String valueOne = null;
                    
                    String valueTwo = clicsAward.getCitation();
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);

                    fieldName = "id";
                    valueTwo = clicsAward.getId();
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                    
                    fieldName = "team_ids";
                    valueTwo = formatTeamList(clicsAward.getTeam_ids());
                    fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                }
            }
            
            Set<String> awardKeySet = firstFileMap.keySet();
            String[] awardkeys = (String[]) awardKeySet.toArray(new String[awardKeySet.size()]);
            Arrays.sort(awardkeys);
            
            for (String key : awardkeys) {
                CLICSAward award = firstFileMap.get(key);
                numberRows++;
                
                String valueTwo = null;
                
                String fieldName = "citation";
                String valueOne = award.getCitation();
                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                fileComparison.addfieldCompareRecord(fieldCompareRecord); 
                
                fieldName = "id";
                valueTwo = award.getId();
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);
                
                fieldName = "team_ids";
                valueTwo = formatTeamList(award.getTeam_ids());
                fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                fileComparison.addfieldCompareRecord(fieldCompareRecord);
            }
            
        } catch (Exception e) {
            StaticLog.getLog().log(Level.WARNING, "Problem comparing scoreboard data/info", e);
            ExecuteUtilities.rethrow(e);
        }
        
        fileComparison.setNumberRows(numberRows);
        return fileComparison;
    }

    private static String formatTeamList(String[] teamIds) {
        Arrays.sort(teamIds);
        return String.join(", ", teamIds);
    }

    public static FileComparison createTSVFileComparison(String tsvFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + tsvFilename;
        String secondFilename = targetDir + File.separator + tsvFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        String[] columnList = { "CMS Id", "rank", "medal", "solved", "penalty", "last solved time" };

        long numberRows = 0;

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
            String[] remainingKeys = (String[]) remainingLinesFromFirstFileKeys.toArray(new String[remainingLinesFromFirstFileKeys.size()]);
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
                        throw new RuntimeException("Invalid results TSV file contents. " + Constants.NL + " in file " + filename);
                    } else {
                        throw new RuntimeException("Invalid results TSV file contents.  too few fields "+fields.length+Constants.NL+ //
                                " line: '"+line+"'"+Constants.NL+" in file "+filename);
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
            return clicsAward.getCitation();
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
