package edu.csus.ecs.pc2.core.security;

import java.io.File;

import edu.csus.ecs.pc2.core.log.Log;
import junit.framework.TestCase;

/**
 * JUnit test for FileSecurity.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FileSecurityTest extends TestCase {

    private Log pc2log = new Log("cryptTest");

    private FileSecurity fileSecurity = null;

    private String passwordString = "ThisPassword";

    private String testDir = ".";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fileSecurity = new FileSecurity(pc2log);
    }

    public void testSaveWriteRead() {

        try {
            fileSecurity.saveSecretKey(testDir, passwordString.toCharArray());
        } catch (FileSecurityException e) {
            // 
            e.printStackTrace();
        }

        String cryptedFileName = testDir + File.separator + "secure.sld";

        try {
            fileSecurity.writeSealedFile(cryptedFileName, "SECRETINFORMATION");
        } catch (FileSecurityException e) {
            e.printStackTrace();
        }

        try {
            String st = (String) fileSecurity.readSealedFile(cryptedFileName);
            assertEquals("SECRETINFORMATION", st);
        } catch (FileSecurityException e) {
            e.printStackTrace();
            assert (false);
        }
    }
}