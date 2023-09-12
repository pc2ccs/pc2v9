// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteUtilities;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.exports.ccs.ResultRow;

/**
 * Utilities for file comparison
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilities {

    public static FileComparison createJSONFileComparison(String tsvFileName, String sourceDir, String targetDir) {

        String firstFilename = sourceDir + File.separator + tsvFileName;
        String secondFilename = targetDir + File.separator + tsvFileName;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        // TODO 760 code comparison

        return fileComparison;
    }

    public static FileComparison createTSVFileComparison(String tsvFilename, String sourceDir, String targetDir) {

        String firstFilename = sourceDir + File.separator + tsvFilename;
        String secondFilename = targetDir + File.separator + tsvFilename;
        FileComparison fileComparison = new FileComparison(firstFilename, secondFilename);

        String[] columnList = { "CMS Id", "rank", "medal", "solved", "penalty", "last sovlved time" };
        
        try {

            /**
             * Map first results lines to cms id
             */
            Map<Integer, String> firstFileMap = new HashMap<Integer, String>();

            String[] lines = Utilities.loadFile(firstFilename);
            for (String line : lines) {

                if (!line.trim().startsWith("result")) {
                    ResultRow row = new ResultRow(line.trim());
                    firstFileMap.put(row.getCmsId(), line);
                }
            }

            String[] secondFilelines = Utilities.loadFile(secondFilename);
            for (String secondLine : secondFilelines) {

                String[] secondLineFields = secondLine.split(Constants.TAB);

                if (!secondLine.trim().startsWith("result")) {
                    ResultRow row = new ResultRow(secondLine);

                    String firstLine = firstFileMap.get(row.getCmsId());

                    if (firstLine == null) {
                        // found in second file but not in first
                        String fieldName = secondLineFields[0];
                        for (int i = 1; i < columnList.length; i++) {
                            String valueTwo = secondLineFields[i];
                            FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, null, valueTwo, null);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);
                        }
                    } else {
                        // found matching cms id in both files
                        String fieldName = secondLineFields[0];
                        String[] firstLineFields = firstLine.split(Constants.TAB);

                        for (int i = 1; i < columnList.length; i++) {
                            String valueOne = firstLineFields[i];
                            String valueTwo = secondLineFields[i];
                            FieldCompareRecord fieldCompareRecord = new FieldCompareRecord(fieldName, valueOne, valueTwo, null);
                            fileComparison.addfieldCompareRecord(fieldCompareRecord);
                        }
                    }
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return objectMapper;
    }
    
}
