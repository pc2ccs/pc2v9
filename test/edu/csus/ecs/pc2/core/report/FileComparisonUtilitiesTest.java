// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.CLICSAward;
import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.services.core.JSONUtilities;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilitiesTest extends AbstractTestCase {
    
    private static final String TESTDATA_RESULTS_DATA_DIR = "testdata/FileComparisonUtilitiesTest/resultscompwork/results";

    private IFileComparisonKey resultComparisonKey = new FileComparisonUtilities.ResultTSVKey();

    private IFileComparisonKey awardsComparisonKey = new FileComparisonUtilities.AwardKey();

    private IFileComparisonKey scoreComparisonKey = new FileComparisonUtilities.ScoreboardJSONKey();

    private ObjectMapper objectMapper = JSONUtilities.getObjectMapper();

    public void testcreateTSVFileComparison() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2";

        FileComparison comp = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, domjResultsDir, pc2ResultsDir, resultComparisonKey);

        assertEquals("Expecting number of comparisons", 255, comp.getComparedFields().size());
        assertEquals("Expecting no differences ", 0, comp.getNumberDifferences());

    }

    public void testAllpc2AndPrimary() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2";

        FileComparison resultsCompare = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, domjResultsDir, pc2ResultsDir, resultComparisonKey);
        FileComparison awardsFileCompare = FileComparisonUtilities.createAwardJSONFileComparison(Constants.AWARDS_JSON_FILENAME, domjResultsDir, pc2ResultsDir, awardsComparisonKey);
        FileComparison scoreboardJsonCompare = FileComparisonUtilities.createScoreboardJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, domjResultsDir, pc2ResultsDir, scoreComparisonKey);

        assertEquals("results diff count ", 0, resultsCompare.getNumberDifferences());
        assertEquals("awardsdiff count ", 0, awardsFileCompare.getNumberDifferences());
        assertEquals("scoreboard diff count ", 51, scoreboardJsonCompare.getNumberDifferences());

        assertEquals("results rows count ", 51, resultsCompare.getNumberRows());
        assertEquals("awardsdiff rows count ", 19, awardsFileCompare.getNumberRows());
        assertEquals("scoreboard rows count ", 51, scoreboardJsonCompare.getNumberRows());

    }

    public void testJSONAwardsCompare() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2";

        FileComparison comp = FileComparisonUtilities.createAwardJSONFileComparison(Constants.AWARDS_JSON_FILENAME, domjResultsDir, pc2ResultsDir, awardsComparisonKey);

        assertNotNull(comp);
        String[] lines = Utilities.loadFile(comp.getFirstFilename());
        String firstLineString = String.join(" ", lines);

        List<CLICSAward> firstAwardList = objectMapper.readValue(firstLineString, new TypeReference<List<CLICSAward>>() {
        });
        assertEquals("Expecting awards lines for " + comp.getFirstFilename(), 19, firstAwardList.size());

        lines = Utilities.loadFile(comp.getSecondFilename());
        String seconString = String.join(" ", lines);

        List<CLICSAward> secondAwardList = objectMapper.readValue(seconString, new TypeReference<List<CLICSAward>>() {
        });

        assertEquals("Expecting awards lines for " + comp.getSecondFilename(), 19, secondAwardList.size());

        List<FieldCompareRecord> fields = comp.getComparedFields();

        assertEquals("Expecting number of comparison rows", 57, fields.size());

        assertEquals("Expecting no differences ", 0, comp.getNumberDifferences());

        //        for (FieldCompareRecord fieldCompareRecord : fields) {
        //            System.out.println("debug 22 field "+fieldCompareRecord.toJSON());
        //        }

    }

    public void testloadTeamRows() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2";
        
        FileComparison comp = FileComparisonUtilities.createScoreboardJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, domjResultsDir, pc2ResultsDir, scoreComparisonKey);
        assertNotNull(comp);

        //        editFile(comp.getSecondFilename());
        //        editFile(comp.getFirstFilename());

        List<TeamScoreRow> firstTeamScoreRows = FileComparisonUtilities.loadTeamRows(comp.getFirstFilename());
        assertEquals("Expecting team score rows in " + comp.getFirstFilename(), 51, firstTeamScoreRows.size());

        List<TeamScoreRow> secondTeamScoreRows = FileComparisonUtilities.loadTeamRows(comp.getSecondFilename());
        assertEquals("Expecting team score rows in " + comp.getSecondFilename(), 51, secondTeamScoreRows.size());
    }

    public void testDiffComparison() throws Exception {

        String domjResultsDir = TESTDATA_RESULTS_DATA_DIR + "/domjudge";
        String pc2ResultsDir = TESTDATA_RESULTS_DATA_DIR + "/pc2data1";

        FileComparison resultsCompare = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, domjResultsDir, pc2ResultsDir, resultComparisonKey);
        FileComparison awardsFileCompare = FileComparisonUtilities.createAwardJSONFileComparison(Constants.AWARDS_JSON_FILENAME, domjResultsDir, pc2ResultsDir, awardsComparisonKey);
        FileComparison scoreboardJsonCompare = FileComparisonUtilities.createScoreboardJSONFileComparison(Constants.SCOREBOARD_JSON_FILENAME, domjResultsDir, pc2ResultsDir, scoreComparisonKey);

        //        editFile(scoreboardJsonCompare.getFirstFilename());
        //        editFile(scoreboardJsonCompare.getSecondFilename());

        assertEquals("results diff count ", 24, resultsCompare.getNumberDifferences());
        assertEquals("awardsdiff count ", 9, awardsFileCompare.getNumberDifferences());

        assertEquals("scoreboard diff count ", 194, scoreboardJsonCompare.getNumberDifferences());
    }
}
