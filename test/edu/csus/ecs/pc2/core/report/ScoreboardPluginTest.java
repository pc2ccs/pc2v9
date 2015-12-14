package edu.csus.ecs.pc2.core.report;

import java.io.File;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit Test
 * @author laned
 */
public class ScoreboardPluginTest extends AbstractTestCase {
    
    /**
     * Test plugin using standard test mock contest.
     * 
     * @throws Exception
     */
    public void testSimpleOutput() throws Exception {
        
        String outputDir = getOutputDataDirectory();
        
        removeDirectory(outputDir); // remove previous test data
        
        ensureDirectory(outputDir);
        
//        startExplorer(outputDir);
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createStandardContest();
        contest.setClientId(contest.getAccounts(Type.SCOREBOARD).firstElement().getClientId());
        IInternalController controller = sample.createController(contest, true, false);
        
        ScoreboardPlugin plugin = new ScoreboardPlugin();
        plugin.setContestAndController(contest, controller);
        
        plugin.writeHTML(outputDir);
        
        /**
         * Files expected to be created.
         */
        String [] expectedFiles = {
                "full.html", //
                "fullnums.html", //
                "iScoreBoard.php", //
                "results.tsv", //
                "scoreboard.json", //
                "scoreboard.tsv", //
                "sumatt.html", //
                "summary.html", //
                "sumtime.html", //
        };
        
        
        for (String filename : expectedFiles) {
            assertFileExists(outputDir + File.separator + filename);
        }
        
    }

}
