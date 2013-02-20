package edu.csus.ecs.pc2.core.util;

/**
 * Unit tests.
 * 
 * Looks for samples, directories and other files from ATC.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TestAbstractTestCase extends AbstractTestCase {

    public void testSamplesExist() throws Exception {
        
        String dir = getTestSamplesSourceDirectory();
        assertDirectoryExists(dir);
        
        dir = getDataDirectory();
        ensureDirectory(dir);
        assertDirectoryExists(dir);
        
        dir =  getDataDirectory("testDirATC");
        assertDirectoryExists(dir);

        String filename = getSamplesSourceFilename(HELLO_SOURCE_FILENAME);
        assertFileExists(filename);

        filename = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
        assertFileExists(filename);
        
        // output directories
        
        dir = getOutputDataDirectory();
        assertDirectoryExists(dir);
        
        filename = getOutputTestFilename("atestfile");
        assertFileExists(filename);

    }

}
