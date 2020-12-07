package edu.csus.ecs.pc2.core;

import java.io.File;
import java.net.URL;
import java.util.List;

import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import junit.framework.TestCase;

public class FileUtilitiesTest extends TestCase {

    public void testfindCDPConfigDirectory() throws Exception {

        String[] dirs = {
                "tenprobs", //
                "samps/contests/mini", //
                "samps/contests/problemflagtest", //
                "samps/contests/sumithello", //
                "samps/contests/sumitMTC", //
        };

        for (String dirname : dirs) {
            assertNotNull("Expecting to find a CDP directory for: " + dirname, FileUtilities.findCDPConfigDirectory(new File(dirname)));
        }
    }

    public void testAllSamplesfindCDPConfigDirectory() throws Exception {
        List<String> sampCdps = getShortSampleContestDirnames();
        for (String sampcdpdir : sampCdps) {
            assertNotNull("Expecting to find a CDP directory for: " + sampcdpdir, FileUtilities.findCDPConfigDirectory(new File(sampcdpdir)));
        }
    }
    
    public void testSampleURL() throws Exception {
        List<String> sampCdps = getShortSampleContestDirnames();
        for (String sampcdpdir : sampCdps) {
            URL url = FileUtilities.findCDPConfigDirectoryURL(new File(sampcdpdir));
            assertNotNull("Expecting to find a CDP directory for: " + sampcdpdir, url);

            String expectedPath = FileUtilities.getCurrentDirectory() + File.separator + // 
                    FileUtilities.getSampleContestsDirectory() + File.separator + //
                    sampcdpdir + File.separator + //
                    IContestLoader.CONFIG_DIRNAME;

            File expFile = new File(expectedPath);
            File actFile = new File(url.getFile());
            assertEquals("Expecting config path ", expFile.getAbsolutePath(), actFile.getAbsolutePath());
        }
    }

    private List<String> getShortSampleContestDirnames() {
        return FileUtilities.getDirectoryEntries(FileUtilities.getSampleContestsDirectory(), true);
    }

}
