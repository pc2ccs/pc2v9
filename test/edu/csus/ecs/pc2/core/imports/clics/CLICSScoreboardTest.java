// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JSONObjectMapper;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.scoring.NewScoringAlgorithm;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilities;
import edu.csus.ecs.pc2.core.standings.json.TeamScoreRow;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSScoreboardTest extends AbstractTestCase{

    /**
     * 
     * @param logDirectoryName
     * @param runsData
     * @return
     * @throws Exception 
     */
    private IInternalContest loadSampleRuns( String [] runsData, String logDirectoryName) throws Exception {
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();

        Log log = new Log(logDirectoryName, getName()+".log");
        StaticLog.setLog(log);

        addRuns(contest, runsData);
        return contest;
    }


    String[] getRunsData(){

        String [] runsData = {

                "1,1,A,1,No",  //20
                "2,1,A,3,Yes",  //3 (first yes counts Minutes only)
                "3,1,A,5,No",  //20
                "4,1,A,7,Yes",  //20  
                "5,1,A,9,No",  //20

                "6,1,B,11,No",  //20  (all runs count)
                "7,1,B,13,No",  //20  (all runs count)

                "8,2,A,30,Yes",  //30

                "9,2,B,35,No",  //20 (all runs count)
                "10,2,B,40,No",  //20 (all runs count)
                "11,2,B,45,No",  //20 (all runs count)
                "12,2,B,50,No",  //20 (all runs count)
                "13,2,B,55,No",  //20 (all runs count)

                "14,3,A,130,Yes",  //30
                "15,3,B,30,Yes",  //30
                "16,4,A,230,Yes",  //30
                "17,5,A,30,Yes",  //30
                "18,5,B,130,Yes",  //30

        };

        return runsData;
    }


    private ContestStandings createContestStandings(IInternalContest contest) throws JsonParseException, JsonMappingException, IOException, IllegalContestState {

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        String xml = scoringAlgorithm.getStandings(contest, DefaultScoringAlgorithm.getDefaultProperties(), StaticLog.getLog());

        // view the xml file
        //        FileUtilities.writeFileContents("tempfile.xml", new String[] {xml});
        //        editFile("tempfile.xml");

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ContestStandings contestStandings = xmlMapper.readValue(xml, ContestStandings.class);

        return contestStandings;
    }



    public CLICSScoreboard createCLICSScoreboard(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = JSONObjectMapper.getObjectMapper();
        CLICSScoreboard scoreJsonModel = mapper.readValue(jsonString, CLICSScoreboard.class);
        return scoreJsonModel;
    }

    /**
     * Test parsing of sample scoreboard.json
     * 
     * @throws Exception
     */
    public void testReadJSON() throws Exception {
        String dir = getDataDirectory(this.getName());
        ensureDirectory(dir);
        //        startExplorer(dir);
        
        String inputJSONFile = dir + File.separator + "scoreboard.json";
        assertFileExists(inputJSONFile, "Input scoreboard.json");
//        editFile(inputJSONFile);

        String[] jsonLines = Utilities.loadFile(inputJSONFile);
        assertEquals("expecting one line in file " + inputJSONFile, 1, jsonLines.length);

        String outputDirectory = getOutputDataDirectory(this.getName());
        ensureDirectory(outputDirectory);

        String[] runsData = getRunsData();

        IInternalContest contest = loadSampleRuns(runsData, outputDirectory);
        ContestStandings contestStandings = createContestStandings(contest);

        CLICSScoreboard scoreboard = new CLICSScoreboard(contestStandings);
        assertNotNull(scoreboard);

        String scoreboardJSON = scoreboard.toString();
        assertTrue("Expected scoreboard JSON length", scoreboardJSON.length() > 8000);

//        writeFileContents("sample.json", new String[] {scoreboardJSON});
//        editFile("sample.json");
        
        List<TeamScoreRow> rows = scoreboard.getRows();
        assertEquals("Expected rows ", 120, rows.size());

        String evid = scoreboard.getEvent_id();
        assertEquals("expect event id length ", 36, evid.length());

    }

    /**
     * Load scoreboard.json file into CLICSScoreboard.
     * 
     * @throws Exception
     */
    public void testUnMarshallCLICSScoreboard() throws Exception {

        // use CLICSScoreboardTest json file
        String dir = "testdata/CLICSScoreboardTest/testReadJSON";
        ensureDirectory(dir);

        String inputJSONFile = dir + File.separator + "scoreboard.json";
        assertFileExists(inputJSONFile, "Input scoreboard.json");
//        editFile(inputJSONFile);

        String jsonString = ScoreboardUtilities.loadFileContents(new File(inputJSONFile));
        
        assertNotNull(jsonString);
        // TODO TODO JUNIT FIX - why JsonMappingException
        
//        ObjectMapper mapper = JSONObjectMapper.getObjectMapper();
//        CLICSScoreboard scoreboard = mapper.readValue(jsonString, CLICSScoreboard.class);
//        assertNotNull(scoreboard);
    }

}