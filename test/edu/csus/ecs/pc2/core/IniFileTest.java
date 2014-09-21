package edu.csus.ecs.pc2.core;

import java.io.File;
import java.net.MalformedURLException;

import junit.framework.TestCase;

/**
 * This class exists to check functionality
 * of the Ini class.
 */
public class IniFileTest extends TestCase {
    private String loadFile = "pc2v9.ini";
    protected void setUp() throws Exception {
        File dir = new File(loadFile);
        if (!dir.exists()) {
            // TODO, try to find this path in the environment
            dir = new File("projects" + File.separator + "pc2-9.1" + File.separator + loadFile);
            if (dir.exists()) {
                loadFile = dir.toString();
            } else {
                System.err.println("could not find " + loadFile);
            }
        }
    }

    /**
     * This is a test for bug 197
     */
    public void testOne() {
        new IniFile();
        try {
            IniFile.setIniURLorFile(loadFile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            assertNotNull("exception",null);
        }
        // work around lack of a load in setIniURLorFile
        new IniFile();
        assertTrue("_source defined", IniFile.containsKey("_source"));
    }
}
