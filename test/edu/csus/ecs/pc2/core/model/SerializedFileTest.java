package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.util.Base64;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * JUnit for  SerializedFile.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SerializedFileTest extends AbstractTestCase {

    private SampleContest sample = new SampleContest();
    
    @SuppressWarnings("unused")
    private String DATA_DIR_PATH = "SerializedFileTest" ;
    @SuppressWarnings("unused")
    private String testDataDirectoryName;
    
    @Override
    protected void setUp() throws Exception {
//        setCreateMissingDirectories(true);
        super.setUp();
    }
    
    public void testFilesNotFound() throws Exception {
        
        String[] names = { "Foo", "MissingFile.c", "NotThere" };
        for (String filename : names) {
            SerializedFile file = new SerializedFile(filename);

            assertEquals("Expecting zero byte buffer", 0, file.getBuffer().length);
            assertNotNull("Expecting exception set", file.getException());
            assertEquals("Expecting message", filename + " not found in SerializedFile", file.getErrorMessage());
        }

    }
    
    public void testClone() throws Exception {
        
        SerializedFile firstFile = new SerializedFile();
        
        SerializedFile clone = (SerializedFile) firstFile.clone();
        assertFalse("expecting external flag ", firstFile.isExternalFile());
        assertFalse("expecting external flag ", clone.isExternalFile());
        
        compareSerializedFile (firstFile, clone);
        
        String filename = "Foo";
        firstFile = new SerializedFile(filename, true);
        assertEquals("Expecting zero byte file ", 0, firstFile.getBuffer().length);
        assertNotNull("Expecting exception set", firstFile.getException());
        assertEquals("Expecting message", filename + " not found in SerializedFile", firstFile.getErrorMessage());
        assertTrue("expecting external flag ", firstFile.isExternalFile());
        
        clone = (SerializedFile) firstFile.clone();
        compareSerializedFile (firstFile, clone);
        
    }
    
//TODO:  the following test is only useful if the SerializedFile class has been upgraded to fix the problem with generation
//    of invalid SHA-1 codes.  See Bug 1216 (https://pc2.ecs.csus.edu/bugzilla/show_bug.cgi?id=1216) for further discussion of this issue.
//    public void testValidSHA1Computation() {
//        
//        //get the test .exe file to be used to generate an SHA1 code
//        testDataDirectoryName = getRootInputTestDataDirectory() + File.separator + DATA_DIR_PATH;
//        String testFileName = "javaClicsInterfaceValidator.jar";
//
//        //create a SerializedFile from the test file
//        String absolutePath = testDataDirectoryName + File.separator + testFileName ;
//        SerializedFile sf = new SerializedFile(absolutePath);
//        
//        //get the SHA1 code out of the SerializedFile
//        String actual = sf.getSHA1sum();
//        
//        //verify the code matches the expected value
//        String expected = "4eca592e02c44befaa8a90f0ec999186567b7e4e";
//        assertEquals("Wrong SHA1 code (message=" + sf.getErrorMessage() + "): ", expected, actual );
//        
//    }
    
    private void compareSerializedFile(SerializedFile firstFile, SerializedFile clone) {
    
        assertEquals("getErrorMessage ",  firstFile.getErrorMessage(), clone.getErrorMessage());
        assertEquals("getException ",  firstFile.getException(), clone.getException());
        assertEquals("external file flag ",  firstFile.isExternalFile(), clone.isExternalFile());
        
//        assertTrue("equals", firstFile.equals(clone));
    }

    public void testBuff2File() throws Exception {
        
        String filename = getOutputTestFilename("BadFileName");
        
        SerializedFile file = new SerializedFile();
        file.buffer2file(null, filename);
        
        assertNotNull("Expecting exception set", file.getException());
        assertEquals("Expecting message", "Exception in SerializeFile for file "+filename, file.getErrorMessage());
    
    }

    public void testEquals() throws Exception {
        
        String filename = sample.createSampleDataFile(getOutputTestFilename("datafile"));

        SerializedFile file1 = new SerializedFile(filename);

        assertFalse("equals for null returns false", file1.equals(null));
        assertTrue("the same file instance should be equal", file1.equals(file1));

        SerializedFile file2 = new SerializedFile(filename);
        assertTrue("the same file shold be equal ", file1.equals(file2));

        filename = sample.createSampleAnswerFile(getOutputTestFilename("datafile3"));

        SerializedFile file3 = new SerializedFile(filename);
        assertFalse("a third file should not be equal to the first file", file1.equals(file3));

    }
    
    public void testExternal() throws Exception {
        
        String filename = getOutputTestFilename("no_file");
        SerializedFile serializedFile = new SerializedFile(filename, true);
        
        assertTrue("Expect external flag file ", serializedFile.isExternalFile());
        
        assertFalse ("Not expectinf file ", serializedFile.getFile().isFile());
        
        assertNotNull("Expecting non null buffer", serializedFile.getBuffer());
        assertNotNull("Expecting non null buffer", serializedFile.getBuffer());

        String actualSum = serializedFile.getSHA1sum();
        
        assertNull ("Expecting null SHA string (no file) ",actualSum);
        
        filename = sample.createSampleDataFile(getOutputTestFilename("datafile"));

        serializedFile = new SerializedFile(filename, true);
        
        assertTrue("Expect external flag file ", serializedFile.isExternalFile());
        assertEquals("Expecting zero length buffer", 0, serializedFile.getBuffer().length);

        File file = new File(filename);
        String expectedSum = SerializedFile.generateSHA1(file);
        
        actualSum = serializedFile.getSHA1sum();
        
        assertNotNull ("Expecting SHA",actualSum);
        assertNotNull ("Expecting SHA string ",actualSum.length() > 0);  //?? does this make sense??
        
        assertEquals("Expecting same SHA",  expectedSum, actualSum);
        
    }
    
    /**
     * Tests whether a SerializedFile can be properly constructed from an array of bytes (as opposed
     * to being loaded from disk).
     * @throws Exception
     */
    public void testNonDiskFile() throws Exception {
        
        //data = ASCII NUL, STX, H, e, l, l, o, !
        byte [] data = {0x00, 0x02, 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x21};  //length=8
        
        SerializedFile serializedFile = new SerializedFile("HelloFileName", data);
        
   
        
        //check that the SerializedFile is not indicated as "on disk"
        assertFalse("Not expecting SerializedFile to be external ", serializedFile.isExternalFile());
        assertNull("Not expecting SerializedFile to be a disk file ", serializedFile.getFile());
        
        assertNotNull("Expecting non null buffer in SerializedFile", serializedFile.getBuffer());

        String actualSum = serializedFile.getSHA1sum();
        
        assertNotNull ("Expecting non-null SHA string in SerializedFile ",actualSum);
        assertTrue ("Expecting non-zero-length SHA string in SerializedFile ", actualSum.length() > 0); 
   
        assertEquals("Expecting SerializedFile buffer of length 8 ", 8, serializedFile.getBuffer().length);

        String expectedSum = SerializedFile.generateSHA1(data);
        assertEquals("Expecting same SHA",  expectedSum, actualSum);
        
        
        String s = new String(serializedFile.getBuffer());
        assertTrue("SerializedFile does not contain expected data", s.substring(2,8).equals("Hello!"));
    }
    

    private String encodeString(String string) {
        Base64.Encoder encoder = Base64.getEncoder();
        String base64String = encoder.encodeToString(string.getBytes());
        return base64String;
    }
    
    public void testIFileConstructor() throws Exception {
        
        IFile ifile = new IFileImpl("Hello.java", encodeString("// very small file "));
        SerializedFile serializedFile = new SerializedFile(ifile); 
        
        // check for file name populated
        assertNotNull("Name should not be null", serializedFile.getName());
        assertEquals("file name", "Hello.java", serializedFile.getName());
        assertEquals("abs path ", "./Hello.java", serializedFile.getAbsolutePath());
        assertEquals("buffer/bytes length ", 19, serializedFile.getBuffer().length);
        
    }
}
