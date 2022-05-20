package edu.csus.ecs.pc2.exports.ccs;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.imports.clics.EventFeedLine;
import edu.csus.ecs.pc2.core.imports.clics.TeamAccount;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSEventFeedExractTest extends AbstractTestCase {

    /**
     * Test create TSV file from eventfeed test json.
     * 
     * @throws Exception
     */
    public void testprintTeamsTsvFile() throws Exception {

//        startExplorer(getDataDirectory());

        String file = getDataDirectory() + File.separator + "event-feed-teams.json";

//        editFile(file);

        String[] lines = Utilities.loadFile(file);
        List<TeamAccount> teamAccounts = EventFeedLine.getTeams(lines);
        assertNotNull(teamAccounts);

        assertEquals(155, teamAccounts.size());

        CLICSEventFeedExract extract = new CLICSEventFeedExract();

        String outputFile = getOutputDataDirectory() + File.separator + "new.teams.tsv";
        

        extract.printTeamsTsvFile(outputFile, teamAccounts);
        assertFileExists(outputFile);

        String[] outlines = Utilities.loadFile(outputFile);
        assertEquals("Expected lines in " + outputFile, 156, outlines.length);
        
        String outFileLoadaccounts = getOutputDataDirectory() + File.separator + "new.load_accounts.tsv";
        
        extract.printAccountLoadFile(outFileLoadaccounts, teamAccounts);
        assertFileExists(outFileLoadaccounts);
        
        outlines = Utilities.loadFile(outputFile);
        assertEquals("Expected lines in " + outFileLoadaccounts, 156, outlines.length);

//        startExplorer(getOutputDataDirectory());

    }
    
    public void testLoadPrimaryCCSFromYaml() throws Exception {

        String dir = getSampleContestsDirectory() + File.separator + "tenprobs"; // + File.separator + IContestLoader.CONFIG_DIRNAME;

        assertDirectoryExists(dir);

//        startExplorer(dir);

        CLICSEventFeedExract extract = new CLICSEventFeedExract();

        URL url = extract.locateCDPConfigDir(dir);
        assertNotNull("Unable to find config dir at " + dir, url);

        Map<String, String> map = extract.getCDPValues(url);

        assertNotNull("foo", map);

        String[] keys = { "login", "password", "Primary CCS URL" };
        for (String k : keys) {
            assertNotNull("expecting value from yaml for " + k, map.get(k));
        }

        assertEquals("For login expecting", "admin", map.get("login"));
        assertEquals("For password expecting", "admin", map.get("password"));
        assertEquals("For Primary CCS URL expecting", "https://localhost:50443/contest", map.get("Primary CCS URL"));

    }
}
