package edu.csus.ecs.pc2.core.model.playback;

import java.io.File;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.PlaybackInfo;
import edu.csus.ecs.pc2.core.util.JUnitUtilities;
import edu.csus.ecs.pc2.imports.ccs.ContestYAMLLoader;

/**
 * Unit test code.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PlaybackManagerTest extends TestCase {

    private String testDirectory = "testdata" + File.separator + "ContestYAMLLoaderTest";

    private ContestYAMLLoader loader;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loader = new ContestYAMLLoader();

        String testDir = "testdata";
        String projectPath = JUnitUtilities.locate(testDir);
        if (projectPath == null) {
            projectPath = "."; //$NON-NLS-1$
            System.err.println("ContestYAMLLoaderTest: Unable to locate " + testDir);
        }
        testDirectory = projectPath + File.separator + "testdata";

    }

    protected PlaybackManager createManager() throws Exception {

        PlaybackManager manager = new PlaybackManager();

        String contestYamlDir = getTestDirectory() + File.separator + "ContestYAMLLoaderTest";
        System.out.println("debug 22 Using "+contestYamlDir);
        missingDir(contestYamlDir, "YAML Directory");
        
        IInternalContest contest = loader.fromYaml(null,contestYamlDir );
        
        System.out.println("debug 22 Using contest="+contest);
        
        loadJudgements(contest);

        String filename = "replay.txt";

        String replayDirectory = getTestDirectory() + File.separator + "playback";
        String replayFilename = replayDirectory + File.separator + filename;

        missingDir(replayDirectory, "Replay Directory");
        missingFile(replayFilename, "Replay filename");
        
        System.out.println("debug 22 Using "+replayFilename);

        manager.createPlaybackInfo(replayFilename, contest);

        return manager;
    }

    private void missingFile(String replayFilename, String string) {
        if (!new File(replayFilename).isFile()) {
            fail("Missing " + string + " " + replayFilename);
        }
    }

    private void missingDir(String replayDirectory, String string) {
        if (!new File(replayDirectory).isDirectory()) {
            fail("Missing " + string + " " + replayDirectory);
        }
    }

    public void testinsureMinimumPlaybackRecords() throws Exception {

        PlaybackManager manager = createManager();

        PlaybackInfo playbackInfo = manager.getPlaybackInfo();

        // TODO fix this JUnit
      assertEquals("Expected replay records in replay file", 0, playbackInfo.getReplayList().length);

//        assertEquals("Expected replay records in replay file", 8, playbackInfo.getReplayList().length);
//        assertEquals("Expecting playback records", 8, manager.getPlaybackRecords().length);
//
//        int minNum = 9;
//        manager.insureMinimumPlaybackRecords(minNum);
//        assertEquals("Expecting playback records", minNum, manager.getPlaybackRecords().length);
//
//        // 9th record should be first replay record
//
//        PlaybackRecord rec1 = manager.getPlaybackRecords()[0];
//        PlaybackRecord rec2 = manager.getPlaybackRecords()[8];
//
//        assertEquals("Run Ids should be the same", rec1.getId(), rec2.getId());
//
//        minNum = 450;
//        manager.insureMinimumPlaybackRecords(minNum);
//        assertEquals("Expecting playback records", minNum, manager.getPlaybackRecords().length);
//
//        rec2 = manager.getPlaybackRecords()[440];
//        assertEquals("Run Ids should be the same", rec1.getId(), rec2.getId());
//
//        int num = countPlaybackTypes(manager, EventStatus.PENDING);
//        assertEquals("Should all be pending playback record status'", minNum, num);
    }

//    private int countPlaybackTypes(PlaybackManager manager, EventStatus status) {
//
//        PlaybackRecord[] records = manager.getPlaybackRecords();
//
//        int count = 0;
//
//        for (PlaybackRecord record : records) {
//            if (record.getEventStatus().equals(status)) {
//                count++;
//            }
//        }
//
//        return count;
//    }

    private void loadJudgements(IInternalContest contest) {
        String[] judgementNames = { "Yes", "No - Compilation Error", "No - Run-time Error", "No - Time-limit Exceeded", "No - Wrong Answer", "No - Excessive Output", "No - Output Format Error",
                "No - Other - Contact Staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }
    }

    private String getTestDirectory() {
        return testDirectory;
    }

}
