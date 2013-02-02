package edu.csus.ecs.pc2.core.model;

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
}
