// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.CLICSScoreboard;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilitiesTest extends AbstractTestCase {

    private static final String TESTDATA_RESULTS_DATA_DIR = "testdata/resultscompwork/results";

    private IFileComparisonKey resultComparisonKey = new FileComparisonUtilities.ResultTSVKey();

    private IFileComparisonKey awardsComparisonKey = new FileComparisonUtilities.AwardKey();

    private IFileComparisonKey scoreComparisonKey = new FileComparisonUtilities.ScoreboardKey();
    
    private ObjectMapper objectMapper = FileComparisonUtilities.getObjectMapper();

//    private AwardKey awardsKey = new FileComparisonUtilities.AwardKey();
//
//    private ScoreboardKey scoreboardKey = new FileComparisonUtilities.ScoreboardKey();

    public void testcreateTSVFileComparison() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2";

        FileComparison comp = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, domjResultsDir, pc2ResultsDir, resultComparisonKey);

//        int count = 0;
//        List<FieldCompareRecord> compF = comp.getComparedFields();
//        for (FieldCompareRecord fieldCompareRecord : compF) {
//            count++;
//            System.out.println("debug 22 #" + count + " for field " + fieldCompareRecord.toJSON());
//        }

        assertEquals("Expecting number of comparisons", 255, comp.getComparedFields().size());

        // TODO 760 debug why there are differences in tsv compare
//        assertEquals("Expecting no differences ", 0, comp.getNumberDifferences());

    }

    public void testIdenticaltsvFiles() throws Exception {

        String dirOne = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String dirTwo = dirOne;

        FileComparison comp = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, dirOne, dirTwo, resultComparisonKey);

        assertEquals("Expecting number of comparisons", 255, comp.getComparedFields().size());
        List<FieldCompareRecord> compF = comp.getComparedFields();
        assertEquals("Expecting no differences ", 0, comp.getNumberDifferences());

    }

    public void testJSONAwardsCompare() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2";

        FileComparison comp = FileComparisonUtilities.createJSONFileComparison(Constants.AWARDS_JSON_FILENAME, domjResultsDir, pc2ResultsDir, awardsComparisonKey);

        assertNotNull(comp);
//        startExplorer(TESTDATA_RESULTS_DATA_DIR);
        
        // {"citation":"First to solve problem I","id":"first-to-solve-powerofdivisors","team_ids":["5"]},

//        CLICSAward

        String[] lines = Utilities.loadFile(comp.getFirstFilename());
        String firstLineString = String.join(" ", lines);

        // List<MyClass> myObjects = mapper.readValue(jsonInput, new TypeReference<List<MyClass>>(){});
        List<CLICSAward> awardList = objectMapper.readValue(firstLineString, new TypeReference<List<CLICSAward>>() {});
//        for (CLICSAward clicsAward : awardList) {
//            System.out.println("debug 22 1 awards "+clicsAward.toJSON());
//        }
        
        lines = Utilities.loadFile(comp.getSecondFilename());
        String seconString = String.join(" ", lines);
        
        awardList = objectMapper.readValue(seconString, new TypeReference<List<CLICSAward>>() {});
//        for (CLICSAward clicsAward : awardList) {
//            System.out.println("debug 22 2 awards "+clicsAward.toJSON());
//        }
//        
    }

    public void testJSONScoreboardCompare() throws Exception {

        String domjResultsDir = "testdata/resultscompwork/results/domjudge";
        String pc2ResultsDir = "testdata/resultscompwork/results/pc2";

        FileComparison comp = FileComparisonUtilities.createJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, domjResultsDir, pc2ResultsDir, scoreComparisonKey);

        assertNotNull(comp);
        
        
//        editFile(comp.getFirstFilename());
//        editFile(comp.getSecondFilename());
//        
//        String[] lines = Utilities.loadFile(comp.getFirstFilename());
//        String firstLineString = String.join(" ", lines);
//
//        // List<MyClass> myObjects = mapper.readValue(jsonInput, new TypeReference<List<MyClass>>(){});
//        List<CLICSScoreboard> scoreboardList = objectMapper.readValue(firstLineString, new TypeReference<List<CLICSScoreboard>>() {});
//        for (CLICSScoreboard clicsScoreboard : scoreboardList) {
//            System.out.println("debug 22 1 scoreboards "+clicsScoreboard);
//        }
//        
        
//        editFile(comp.getSecondFilename());
//
//        String [] 
//        lines = Utilities.loadFile(comp.getSecondFilename());
//        String seconString = String.join(" ", lines);
//        
//        List<CLICSScoreboard> 
//        scoreboardList = objectMapper.readValue(seconString, new TypeReference<List<CLICSScoreboard>>() {});
//        for (CLICSScoreboard clicsScoreboard : scoreboardList) {
//            System.out.println("debug 22 2 scoreboards "+clicsScoreboard);
//        }

        List<CLICSScoreboard> list;
        // TODO 760 handle domjudge format JSON
//        list = FileComparisonUtilities.getScoreboardJSON(comp.getFirstFilename());
        
        list = FileComparisonUtilities.getScoreboardJSON(comp.getSecondFilename());
        assertEquals("Expecting number of rows from "+comp.getSecondFilename(), 51, list.size());
        
    }

    @Override
    public void testForValidXML(String xml) throws Exception {
        // TODO Auto-generated method stub
        super.testForValidXML(xml);
    }

}
