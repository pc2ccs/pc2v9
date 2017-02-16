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
        
        ensureDirectory(outputDir+File.separator+"cwd");
        
//        startExplorer(outputDir);
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createStandardContest();
        contest.setClientId(contest.getAccounts(Type.SCOREBOARD).firstElement().getClientId());
        IInternalController controller = sample.createController(contest, true, false);
        
        ScoreboardPlugin plugin = new ScoreboardPlugin();
        plugin.setContestAndController(contest, controller);
        
        plugin.writeHTML(outputDir);
        
        // XXX would be nice if plugin did not write them to the CWD
        // these 2 files are written to the current working directory
        String [] cwdFiles = {
                "results.xml",
                "pc2export.dat"
        };
        // move then out of the CWD.
        for (String filename : cwdFiles) {
            File file = new File(filename);
            File destFile = new File(outputDir+File.separator+"cwd", filename);
            file.renameTo(destFile);
        }
        /**
         * Files expected to be created.
         */
        String [] expectedFiles = {
                "full.html", //
                "fullnums.html", //
                "iScoreBoard.php", //
                
                /**
                 * No longer creates these files.
                 * TODO deprecate ScoreboardPlugin and remove this unit test
                 */
//                "results.tsv", //
//                "scoreboard.json", //
//                "scoreboard.tsv", //
                "sumatt.html", //
                "summary.html", //
                "sumtime.html", //
        };
        
        
        for (String filename : expectedFiles) {
            assertFileExists(outputDir + File.separator + filename);
        }
        
    }

}
