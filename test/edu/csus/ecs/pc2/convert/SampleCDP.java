package edu.csus.ecs.pc2.convert;

import java.io.File;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Sample CDP access methods
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public final class SampleCDP {
    /**
     * Private constructor for utility class
     */
    private SampleCDP() {
        super();
    }
    /**
     * Get a file from the cdp.
     * 
     * @param filename
     * @return
     */
    public static String getCDPFile(String filename) {
        return getDir() + filename;
    }

    /**
     * Get CDP Event feed full filename.
     * 
     * @return
     */
    public static String getEventFeedFilename() {
        return getCDPFile("eventFeed" + File.separator + "events.xml");
    }

    /**
     * Get base CDP dir.
     * 
     * @return
     */
    public static String getDir() {
        return AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + "samplecdp" + File.separator;
    }

    public static String getConfigDir() {
        return getDir() + IContestLoader.CONFIG_DIRNAME + File.separator;
    }

    public static String getSubmissionsDir() {
        return getDir() + IContestLoader.SUBMISSIONS_DIRNAME + File.separator;
    }

}
