package edu.csus.ecs.pc2.core.model;

import java.io.File;

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
        assertFalse("a different file shold not be equal ", file1.equals(file2));

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
        assertNotNull ("Expecting SHA string ",actualSum.length() > 0);
        
        assertEquals("Expecting same SHA",  expectedSum, actualSum);
        
    }
}
