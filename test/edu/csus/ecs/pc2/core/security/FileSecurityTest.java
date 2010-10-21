package edu.csus.ecs.pc2.core.security;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * JUnit test for FileSecurity.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FileSecurityTest extends TestCase {

    private String passwordString = "ThisPassword";

    static {
        insureDirectoriesRemoved();
    }
    
    protected static String  getTestDirectoryName(){
        String testDir = "testing";
        
        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }

    public FileSecurityTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    private static void insureDirectoriesRemoved() {

        boolean ableToRemoveDirectories = true;

        String[] dirNames = { "fileSecVPDir", "fileSecVPDirGCD", "getSecretK", "getSecretKTwo" };
        for (String filename : dirNames) {
            String name = getTestDirectoryName() + File.separator + filename;
            ableToRemoveDirectories = ableToRemoveDirectories && insureDirRemoved(name);
            // System.err.println("debug removing directory "+ableToRemoveDirectories+" "+name);
        }

        if (!ableToRemoveDirectories) {
            for (String filename : dirNames) {
                String name = getTestDirectoryName() + File.separator + filename;
                if (new File(name).exists()) {
                    System.err.println("Unable to remove directory: "+name);
                }
            }
            fail("Could not clear all directories created by previous test");
        }

    }

    /**
     * Removes all files and subdirectories.
     * 
     * @param dirName
     *            directory to start removing files from.
     * @return true if all files removed, else false
     */
    protected static boolean insureDirRemoved(String dirName) {
        File dir = null;
        boolean result = true;

        dir = new File(dirName);
        if (!dir.exists()) {
            return true; // nothing there, all done!!
        }

        String[] filesToRemove = dir.list();
        for (String dirEntryName : filesToRemove) {

            File file = new File(dirName + File.separator + dirEntryName);

            if (file.isDirectory()) {
                // recurse through any directories
                result &= insureDirRemoved(dirName + File.separator + dirEntryName);
            }
            result &= file.delete();
        }
        return (result);
    }

    public void testSaveWriteRead() {
        
        String testDir = getTestDirectoryName();
        
        FileSecurity fileSecurity = new FileSecurity(testDir);

        try {
            fileSecurity.saveSecretKey(passwordString.toCharArray());
        } catch (FileSecurityException e) {
            // 
            e.printStackTrace();
        }

        String cryptedFileName = testDir + File.separator + "secure.sld";

        try {
            fileSecurity.store(cryptedFileName, "SECRETINFORMATION");
        } catch (FileSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            String st = (String) fileSecurity.load(cryptedFileName);
            assertEquals("SECRETINFORMATION", st);
        } catch (FileSecurityException e) {
            e.printStackTrace();
            assert (false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void testVerifyPassword() {

        String dirname = getTestDirectoryName() + File.separator + "fileSecVPDir";

        FileSecurity fileSecurity = new FileSecurity(dirname);

        String cryptedFileName = dirname + File.separator + "secure.fil";

        String password = "foobar";

        try {

            fileSecurity.saveSecretKey(password.toCharArray());

            fileSecurity.verifyPassword(password.toCharArray());
        } catch (FileSecurityException exception) {
            failTest("Exception writeSealedFile " + cryptedFileName, exception);
            // System.out.println("debug Exception "+exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFile " + cryptedFileName, e);
        }
    }

    public void testVerifyPasswordNegative() {

        /**
         * Purposefully bad directory do not change name
         */
        String dirname = "/baddirname";

        FileSecurity fileSecurity = new FileSecurity(dirname);

        String cryptedFileName = dirname + File.separator + "secure.fil";

        String password = "foobar";

        // Negative Test

        try {
            fileSecurity.verifyPassword(password.toCharArray());
        } catch (FileSecurityException exception) {
            assert (true);
            // System.out.println("debug Exception "+exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFile " + cryptedFileName, e);
        }

    }

    public void testWriteSealedFileNegative() {

        /**
         * Purposefully bad directory do not change name
         */
        String badDirName = "/baddirname"; // save to bad directory

        FileSecurity fileSecurity = new FileSecurity(badDirName);

        String cryptedFileName = badDirName + File.separator + "secure.fil";

        try {
            fileSecurity.writeSealedFile(cryptedFileName, "SECRETINFORMATION");
        } catch (FileSecurityException exception) {
            assert (true); // bad dir does exist - this is what should happen
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFileNegative " + cryptedFileName, e);
        }
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.security.FileSecurity.getContestDirectory()'
     */
    public void testGetContestDirectory() {
        String dirname = getTestDirectoryName() + File.separator + "fileSecVPDirGCD" + File.separator;

        FileSecurity security = new FileSecurity(dirname);
        assertEquals("getContestDirectory", dirname, security.getContestDirectory());

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

        String dirname = getTestDirectoryName() + File.separator + "fileSecVPDir";

        try {
            FileSecurity security = new FileSecurity(dirname);

            String password = "miwok";

            security.saveSecretKey(password.toCharArray());

            String readPassword = security.getPassword();

            assertEquals(password, readPassword);

        } catch (Exception e) {
            failTest("Unexpected exception in getPassword  ", e);
        }

    }

    private void failTest(String string, Exception e) {
        System.err.println("Failed TEST " + string);
        System.err.flush();
        e.printStackTrace(System.err);
        fail(string);
    }

}
