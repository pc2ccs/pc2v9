package edu.csus.ecs.pc2.core.security;

import java.io.File;

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

    private String testDir = ".";
    
    static {
        insureDirectoriesRemoved();
    }
    
    public FileSecurityTest(){
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new FileSecurity( testDir);
    }
    
    private static void insureDirectoriesRemoved() {
        
        boolean ableToRemoveDirectories = true;
        
        String[] dirNames = { "fileSecVPDir", "fileSecVPDirGCD", "getSecretK", "getSecretKTwo" };
        for (String name : dirNames){
            ableToRemoveDirectories = ableToRemoveDirectories && insureDirRemoved (name);
//            System.err.println("debug removing directory "+ableToRemoveDirectories+" "+name);
        }
        
        if (! ableToRemoveDirectories){
            System.err.println("Warning could not clear all directories created by previous test");
            for (String name : dirNames){
                if (new File(name).exists()){
                    System.err.println("Dir "+name+" still exists");
                }
            }
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

        try {
            FileSecurity.saveSecretKey(passwordString.toCharArray());
        } catch (FileSecurityException e) {
            // 
            e.printStackTrace();
        }

        String cryptedFileName = testDir + File.separator + "secure.sld";

        try {
            FileSecurity.writeSealedFile(cryptedFileName, "SECRETINFORMATION");
        } catch (FileSecurityException e) {
            e.printStackTrace();
        }

        try {
            String st = (String) FileSecurity.readSealedFile(cryptedFileName);
            assertEquals("SECRETINFORMATION", st);
        } catch (FileSecurityException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testVerifyPassword() {

        String dirname = "fileSecVPDir";

        new FileSecurity( dirname);

        String cryptedFileName = dirname + File.separator + "secure.fil";

        String password = "foobar";

        try {

            FileSecurity.saveSecretKey(password.toCharArray());

            FileSecurity.verifyPassword(password.toCharArray());
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

        new FileSecurity(dirname);

        String cryptedFileName = dirname + File.separator + "secure.fil";

        String password = "foobar";

        // Negative Test

        try {
            FileSecurity.verifyPassword(password.toCharArray());
        } catch (FileSecurityException exception) {
            assert (true);
            // System.out.println("debug Exception "+exception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            failTest("Exception writeSealedFile " + cryptedFileName, e);
        }

    }

    public void testWriteSealedFileNegative() {

        String badDirName = "/baddirname"; // save to bad directory

        new FileSecurity(badDirName);

        String cryptedFileName = badDirName + File.separator + "secure.fil";

        try {
            FileSecurity.writeSealedFile(cryptedFileName, "SECRETINFORMATION");
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
        String dirname = "fileSecVPDirGCD"+File.separator;

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

        String dirname = "fileSecVPDir";

        try {
            FileSecurity security = new FileSecurity( dirname);

            String password = "miwok";

            FileSecurity.saveSecretKey(password.toCharArray());

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

}