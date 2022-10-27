// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSJsonUtilitiesTest extends AbstractTestCase {

    /**
     * Test load awards.json from file.
     * @throws Exception
     */
    public void testreadAwardsList() throws Exception {

        String dataDir = getDataDirectory(getName());

//        ensureDirectory(dataDir);
//        startExplorer(dataDir);

        String awardsFile = dataDir + File.separator + "bapc2020.awards.json";
        assertFileExists(awardsFile);

//        editFile (awardsFile);

        List<CLICSAward> awards = CLICSJsonUtilities.readAwardsList(awardsFile);

        assertEquals("Expecting same number of awards", 26, awards.size());

        // print assertCitationEquals based on data
//        for (CLICSAward clicsAward : awards) {
//            System.out.println("assertCitationEquals(awards, \""+
//                    clicsAward.getId()+"\", \""+clicsAward.getTeamIds().get(0)+"\");");
//        }

        assertCitationEquals(awards, "winner",  "2");
        
        assertCitationEquals(awards, "group-winner-3", "2");
        assertCitationEquals(awards, "group-winner-4", "28");
        assertCitationEquals(awards, "group-winner-5", "54");
        assertCitationEquals(awards, "group-winner-6", "6");
        assertCitationEquals(awards, "group-winner-7", "7");
        assertCitationEquals(awards, "group-winner-8", "9");
        assertCitationEquals(awards, "group-winner-9", "10");
        assertCitationEquals(awards, "group-winner-10", "11");
        assertCitationEquals(awards, "group-winner-11", "52");
        assertCitationEquals(awards, "group-winner-13", "19");
        assertCitationEquals(awards, "group-winner-14", "56");
        assertCitationEquals(awards, "group-winner-15", "21");
        assertCitationEquals(awards, "group-winner-17", "42");
        
        assertCitationEquals(awards, "first-to-solve-crashingcompetitioncomputer", "2");
        assertCitationEquals(awards, "first-to-solve-grindinggravel", "2");
        assertCitationEquals(awards, "first-to-solve-housenumbering", "2");
        assertCitationEquals(awards, "first-to-solve-lowestlatency", "2");
        assertCitationEquals(awards, "first-to-solve-kioskconstruction", "31");
        assertCitationEquals(awards, "first-to-solve-adjustedaverage", "36");
        assertCitationEquals(awards, "first-to-solve-equalisingaudio", "6");
        assertCitationEquals(awards, "first-to-solve-imperfectimperialunits", "7");
        assertCitationEquals(awards, "first-to-solve-bellevue", "10");
        assertCitationEquals(awards, "first-to-solve-failingflagship", "11");
        assertCitationEquals(awards, "first-to-solve-jaggedskyline", "11");
        assertCitationEquals(awards, "first-to-solve-dividingdna", "38");
        assertCitationEquals(awards, "winner", "2");
 

    }

    /**
     * Compare award for id with team number
     * @param awards
     * @param id the name of the award
     * @param team
     * @throws JsonProcessingException
     */
    private void assertCitationEquals(List<CLICSAward> awards, String id, String team) throws JsonProcessingException {
        
        CLICSAward award = findAward(awards, id);
        assertNotNull("Missing award for citation " + id, award);

        List<String> teams = award.getTeamIds();
        int expectedTeamCount = 1;
        assertEquals("Expecting only " + expectedTeamCount + "team ", 1, teams.size());

        String teamid = teams.get(0);
        assertEquals("Expected team for " + id, team, teamid);
    }

    /**
     * 
     * @param awards
     * @param citation
     * @return null if not found, otherwise award for citation
     * @throws JsonProcessingException
     */
    private CLICSAward findAward(List<CLICSAward> awards, String id) throws JsonProcessingException {
        for (CLICSAward clicsAward : awards) {
            if (clicsAward.getId() != null && clicsAward.getId().equals(id)) {
                return clicsAward;
            }
        }
        return null;
    }

}
