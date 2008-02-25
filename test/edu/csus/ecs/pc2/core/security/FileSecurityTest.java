package edu.csus.ecs.pc2.core.security;

import java.io.File;

import javax.crypto.SecretKey;

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

        fileSecurity = new FileSecurity(pc2log, testDir);
    }

    public void testSaveWriteRead() {

        try {
            fileSecurity.saveSecretKey(passwordString.toCharArray());
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

    public void testWriteSealedFileNegative() {

        String badDirName = "/baddirname";  // save to bad directory

        FileSecurity security = new FileSecurity(null, badDirName);

        String cryptedFileName = badDirName + File.separator + "secure.fil";

        try {
            security.writeSealedFile(cryptedFileName, "SECRETINFORMATION");
        } catch (FileSecurityException exception) {
            assert(true); // bad dir does exist - this is what should happen
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFileNegative " + cryptedFileName, e);
        }
    }

    public void testVerifyPassword() {

        String dirname = "fileSecVPDir";

        FileSecurity security = new FileSecurity(pc2log, dirname);

        String cryptedFileName = dirname + File.separator + "secure.fil";

        String password = "foobar";

        try {

            security.saveSecretKey(password.toCharArray());

            security.verifyPassword(password.toCharArray());
        } catch (FileSecurityException exception) {
            failTest("Exception writeSealedFile " + cryptedFileName, exception);
            // System.out.println("debug Exception "+exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFile " + cryptedFileName, e);
        }
    }

    public void testVerifyPasswordNegative() {

        String dirname = "/baddirname";

        FileSecurity security = new FileSecurity(null, dirname);

        String cryptedFileName = dirname + File.separator + "secure.fil";

        String password = "foobar";

        // Negative Test

        try {
            security.verifyPassword(password.toCharArray());
        } catch (FileSecurityException exception) {
            assert (true);
            // System.out.println("debug Exception "+exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFile " + cryptedFileName, e);
        }

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.getContestDirectory()'
     */
    public void testGetContestDirectory() {
        String dirname = "fileSecVPDirGCD";

        FileSecurity security = new FileSecurity(null, dirname);
        assertEquals("getContestDirectory", dirname, security.getContestDirectory());

    }



    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.getSecretKey()'
     */
    public void testGetSecretKey() {

        // this also is testSaveSecretKeySecretKeyCharArray() {

        String dirname = "getSecretK";

        String password = "foobar";

        FileSecurity security = new FileSecurity(pc2log, dirname);
        SecretKey inKey = createSecretKey();

        try {
            security.saveSecretKey(inKey, password.toCharArray());
            security = null;

            security = new FileSecurity(pc2log, dirname);

            SecretKey key = security.getSecretKey();

            assertNotNull(key);
            assertNotNull(key.getEncoded());

        } catch (Exception e) {
            failTest("getSecretKey ", e);
        }
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.saveSecretKey(PublicKey, String)'
     */
    public void testSaveSecretKeyPublicKeyString() {

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.saveSecretKey(char[])'
     */
    public void testSaveSecretKeyCharArray() {

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.getPassword()'
     */
    public void testGetPassword() {

        String dirname = "fileSecVPDir";

        try {
            FileSecurity security = new FileSecurity(pc2log, dirname);

            String password = "miwok";

            security.saveSecretKey(password.toCharArray());

            String readPassword = security.getPassword();

            assertEquals(password, readPassword);

        } catch (Exception e) {
            failTest("Unexpected exception in getPassword  ", e);
        }

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.writeSealedFile(String, Serializable)'
     */
    public void testWriteSealedFile() {

    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.readSealedFile(String)'
     */
    public void testReadSealedFile() {

    }
    
    @SuppressWarnings("unused")
    private void failTest(String string) {
        System.err.println("Failed TEST " + string);
        assertTrue(false);
    }

    private void failTest(String string, Exception e) {
        System.err.println("Failed TEST " + string);
        System.err.flush();
        e.printStackTrace(System.err);
        assertTrue(false);
    }

    private SecretKey createSecretKey() {
        Crypto crypto = new Crypto();
        return crypto.generateSecretKey(crypto.getPublicKey(), crypto.getPrivateKey());

    }
}