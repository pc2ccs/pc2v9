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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.CLICSScoreboard;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.exports.ccs.ResultRow;

/**
 * Utilities for file comparison
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilities {
    
    private static  ObjectMapper objectMapper = null;
    
    
    public static FileComparison createScoreboardJSONFileComparison(String tsvFileName, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + tsvFileName;
        String secondFilename = targetDir + File.separator + tsvFileName;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);
        
        try {
            
            List<TeamScoreRow> firstTeamScoreRows = FileComparisonUtilities.loadTeamRows (fileComparison.getFirstFilename());
            List<TeamScoreRow> secondTeamScoreRows = FileComparisonUtilities.loadTeamRows (fileComparison.getSecondFilename());

            
            
        } catch (Exception e) {
            // TODO 760 handle exception
        }

        return fileComparison;

    }
//   

    public static FileComparison createAwardJSONFileComparison(String tsvFileName, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + tsvFileName;
        String secondFilename = targetDir + File.separator + tsvFileName;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

//        System.out.println("debug 22 createJSONFileComparison for " + firstFilename);
//        System.out.println("debug 22 createJSONFileComparison for " + secondFilename);
        
        try {
            
            List<CLICSAward> firstAwardRows = FileComparisonUtilities.loadAwardRows(fileComparison.getFirstFilename());
            List<CLICSAward> secondAwardRows = FileComparisonUtilities.loadAwardRows(fileComparison.getSecondFilename());
            
            /**
             * Map first awards cisation to awards class
             */
            Map<String, CLICSAward> firstFileMap = new HashMap<String, CLICSAward>();
            for (CLICSAward clicsAwardOne : firstAwardRows) {
                String key = fileComparisonKey.getKey(clicsAwardOne);
                firstFileMap.put(key, clicsAwardOne);
//                System.out.println("debug 22 stuf4 "+key+" "+clicsAwardOne.toJSON());
            }
            
            for (CLICSAward clicsAward : secondAwardRows) {
                String key = fileComparisonKey.getKey(clicsAward);
                CLICSAward firstAward = firstFileMap.get(key);
                
                if (firstAward != null) {
                    
                    String valueOne = firstAward.getCitation();
                    String fieldName = "citation";
                    String valueTwo = clicsAward.getCitation();
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null, key);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                    
                    // TODO 760 compare other fields like team ids
                    
                    firstFileMap.remove(key);
                } else {
                    String fieldName = "citation";
                    String valueTwo = clicsAward.getCitation();
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, null, valueTwo, null, key);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord); 
                    
                    // TODO 760 compare other fields like team ids
                }
            }
            
            Set<String> awardKeySet = firstFileMap.keySet();
            String[] awardkeys = (String[]) awardKeySet.toArray(new String[awardKeySet.size()]);
            Arrays.sort(awardkeys);
            
            for (String key : awardkeys) {
                CLICSAward award = firstFileMap.get(key);
                
                String fieldName = "citation";
                String valueOne = award.getCitation();
                FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, null, null, key);
                fileComparison.addfieldCompareRecord(fieldCompareRecord); 
                
                // TODO 760 compare other fields like team ids
            }
            
            
        } catch (Exception e) {
            e.printStackTrace(); // TODO 760 handle exception
        }

        return fileComparison;
    }

    public static FileComparison createTSVFileComparison(String tsvFilename, String sourceDir, String targetDir, IFileComparisonKey fileComparisonKey) {

        String firstFilename = sourceDir + File.separator + tsvFilename;
        String secondFilename = targetDir + File.separator + tsvFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        String[] columnList = { "CMS Id", "rank", "medal", "solved", "penalty", "last sovlved time" };

        try {

            /**
             * Map first results lines to cms id
             */
            Map<String, String> firstFileMap = new HashMap<String, String>();

            String[] lines = Utilities.loadFile(firstFilename);
            for (String line : lines) {

                if (!line.trim().startsWith("result")) {
                    String key = fileComparisonKey.getKey(line);
                    firstFileMap.put(key, line);
                }
            }

            String[] secondFilelines = Utilities.loadFile(secondFilename);
            for (String secondLine : secondFilelines) {

                String[] secondLineFields = secondLine.split(Constants.TAB);

                if (!secondLine.trim().startsWith("result")) {
                    ResultRow row = new ResultRow(secondLine);

                    String key = fileComparisonKey.getKey(secondLine);
                    String firstLine = firstFileMap.get(key);

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

                for (int i = 0; i < columnList.length; i++) {
                    String fieldName = columnList[i];
                    String valueOne = firstLineFields[i];
                    FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, null, null, keyName);
                    fileComparison.addfieldCompareRecord(fieldCompareRecord);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExecuteUtilities.rethrow(e); // TODO 760 handle file
        }

        return fileComparison;
    }

    
    public static String prettyPrint(Object JSONObject) throws JsonProcessingException {
        String json = getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(JSONObject);
        return json;
    }

    public static ObjectMapper getObjectMapper() {

        // TODO REFACTOR move this into a utility class
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        return objectMapper;
    }

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
            return row.getRank() + ":" + row.getTeam_id() +":"+row.getScore();
            
        }
    }

    public static List<CLICSScoreboard> getCLICSScoreboardList(String filename) throws JsonParseException, JsonMappingException, IOException {
        String [] lines = Utilities.loadFile(filename);
        String jsonString = String.join(" ", lines);

        List<CLICSScoreboard> scoreboardList = objectMapper.readValue(jsonString, new TypeReference<List<CLICSScoreboard>>() {});
        for (CLICSScoreboard clicsScoreboard : scoreboardList) {
            System.out.println("debug 22 2 scoreboards "+clicsScoreboard);
        }
        return scoreboardList;
    }    

    public static List<CLICSScoreboard> getScoreboardJSON(String filename) {

        try {
            List<CLICSScoreboard> list = getCLICSScoreboardList(filename);
            return list;
        } catch (Exception e) {
            throw ExecuteUtilities.rethrow(e);
        }
    }
    
    public static  List<TeamScoreRow> loadTeamRows(String scoreboardJSONFilename) throws IOException {
        
        List<TeamScoreRow> rows = new ArrayList<TeamScoreRow>();
        
        String[] lines = Utilities.loadFile(scoreboardJSONFilename);
        String firstLineString = String.join(" ", lines);

        CLICSScoreboard clicsScoreboard = objectMapper.readValue(firstLineString, CLICSScoreboard.class);
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

        awardsList = objectMapper.readValue(jsonString, new TypeReference<List<CLICSAward>>() {
        });

        return awardsList;
    }

}
