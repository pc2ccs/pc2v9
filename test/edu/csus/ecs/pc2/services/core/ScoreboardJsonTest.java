// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.JSONObjectMapper;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.GroupList;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.standings.ScoringGroup;
import edu.csus.ecs.pc2.core.standings.StandingsHeader;
import edu.csus.ecs.pc2.core.standings.TeamStanding;
import edu.csus.ecs.pc2.core.standings.json.ScoreboardJsonModel;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ScoreboardJsonTest extends AbstractTestCase {
    
    public void testScoreboardJSONStandardContest () throws Exception {
        
        String outputDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(outputDirectoryName);
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createStandardContest();
        
        Log log = new Log(outputDirectoryName, getName()+".log");
        StaticLog.setLog(log);
        
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
        
        addRuns(contest, runsData);
        
        ScoreboardJson scoreboardJSON = new ScoreboardJson();
        
        String json = scoreboardJSON.createJSON(contest);
        
        assertNotNull(json);
        
        System.out.println("debug 22 FAUX "+json);
    }
    
    public void testNADC21Prac2EF() throws Exception {
        
        String outputDirectoryName = getOutputDataDirectory(getName());
        ensureDirectory(outputDirectoryName);
//        startExplorer(outputDirectoryName);
        
        String inputDirectoryName = getDataDirectory(getName());
        ensureDirectory(inputDirectoryName);
//        startExplorer(inputDirectoryName);
        
        String xmlFilename = inputDirectoryName + File.separator + "nadcPractice-event-feed.xml";
        
        assertFileExists(xmlFilename);
        
        String[] lines = Utilities.loadFile(xmlFilename);
        
        assertFalse("No lines in file "+xmlFilename, lines.length == 0);
        
        ScoreboardJson scoreboardJSON = new ScoreboardJson();
        
        String xml = lines[0];
        if (lines.length > 1) {
            xml = String.join("", lines);
        }
        
        String json = scoreboardJSON.createJSON(xml);
        
        assertNotNull(json);
        
        String [] datalines = {json};
        
        String jsonFilename =  outputDirectoryName + File.separator + "new.json";
        writeFileContents(jsonFilename, datalines);
        assertFileExists(jsonFilename);
//        editFile(jsonFilename); 
    }

    private void addRuns(IInternalContest contest, String[] runsData) throws Exception {
        for (String runInfoLine : runsData) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }
    }

    /**
     * Test parsing of a scoreboard XML into the ContestStandings class.
     * 
     * @throws Exception
     */
    public void testcreateContestStandings() throws Exception {

        /**
         * pc2 Standings XML for NADC Practice 2.
         */
        String xmlFilename = "testdata/ScoreboardJSONTest/testNADC21Prac2EF/nadcPractice-event-feed.xml";

        ContestStandings contestStandings = ScoreboardUtilites.createContestStandings(new File(xmlFilename));

        assertNotNull(contestStandings);

        StandingsHeader header = contestStandings.getStandingsHeader();

        assertEquals("standingsHeader title ", "NADC Practice 2", header.getTitle());

        List<TeamStanding> teamStandings = contestStandings.getTeamStandings();

        assertNotNull("Expected to find teamStandings   ", teamStandings);
        assertEquals("Expected teamstandings elements ", 171, teamStandings.size());

        int rowNum = 21;
        TeamStanding teamStanding = teamStandings.get(rowNum);

        assertEquals("For row " + rowNum + "  expected value for getFirstSolved ", "4", teamStanding.getFirstSolved());
        assertEquals("For row " + rowNum + "  expected value for getGroupRank ", "9", teamStanding.getGroupRank());
        assertEquals("For row " + rowNum + "  expected value for getIndex ", "21", teamStanding.getIndex());
        assertEquals("For row " + rowNum + "  expected value for getProblemsAttempted ", "12", teamStanding.getProblemsAttempted());
        assertEquals("For row " + rowNum + "  expected value for getRank ", "22", teamStanding.getRank());
        assertEquals("For row " + rowNum + "  expected value for getShortSchoolName ", "Rutgers Uni.", teamStanding.getShortSchoolName());
        assertEquals("For row " + rowNum + "  expected value for getTeamExternalId ", "488629", teamStanding.getTeamExternalId());
        assertEquals("For row " + rowNum + "  expected value for getTotalAttempts ", "19", teamStanding.getTotalAttempts());

        assertEquals("For header.getGroupCount() ", "5", header.getGroupCount());

        GroupList gList = header.getGrouplist();
        assertNotNull("Expected GroupList ", gList);

        List<ScoringGroup> scoreGroups = gList.getGroups();
        assertEquals("Expected list scoringGroup count ", "4", ""+scoreGroups.size());

        assertEquals("Expected value for getExternalId", "18475", scoreGroups.get(3).getExternalId());
        assertEquals("Expected value for getTitle", "ICPC North America South Division Championship", scoreGroups.get(3).getTitle());

    }

    // TODO TODAY debug 22
//    public void testUnMarshallScoreboardJsonModel() throws Exception {
//
//        // TODO create testdata scoreboard json
//        
//        String jsonFilename = "/tmp/nadc.kattis.scoreboard.json.format.txt";
//        
//        if (new File(jsonFilename).isFile()) {
//            
//            String jsonString = loadFileContents(new File(jsonFilename));
//            ScoreboardJsonModel model = createScoreboardJsonModel(jsonString);
//            
//            assertNotNull(model);
//        }
//    }


    public ScoreboardJsonModel createScoreboardJsonModel(String jsonString) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = JSONObjectMapper.getObjectMapper();
        ScoreboardJsonModel scoreJsonModel = mapper.readValue(jsonString, ScoreboardJsonModel.class);
        return scoreJsonModel;
    }
    
}


