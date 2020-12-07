package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.util.Date;

import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class PacedFileInputStreamTester extends AbstractTestCase {

    
    /**
     * Test paced stream.
     * 
     * @throws Exception
     */
    public void testZeroSecondPaced() throws Exception {
        // TODO SOMEDAY uncomment and fix test
        
//        String datadir = getDataDirectory();
        
        
        
////        ensureDirectory(datadir);
////        startExplorer(datadir);
//        
//        String filename = datadir + File.separator + "csus-f2019.eventfeed.json";
////        editFile(filename);
//        
//        File infile = new File(filename);
//
//        int secondsPause = 0;
//        PacedFileInputStream stream = new PacedFileInputStream(infile, secondsPause);
//
//        long startTime = new Date().getTime();
//
//        int cnt = 0;
//        @SuppressWarnings("unused")
//        int inint;
//        while (-1 != (inint = stream.read())) {
//            cnt++;
//        }
//        int pc = stream.getPauseCount();
//        stream.close();
//
//        long elapsedtime = new Date().getTime() - startTime;
//
//        assertEquals("Expecting number of pauses in stream ", 1264, pc);
//        
//        if (isDebugMode()){
//            System.out.println("File: " + filename + " size " + infile.length());
//            System.out.println("bytes = " + cnt + " pauses count " + pc);
//            System.out.println("Time to process/read " + elapsedtime);
//        }

    }
    
    public void testOneSecondPaced() throws Exception {
        
        if (isFastJUnitTesting()) {
            return;
        }


        String filename = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
//        editFile(filename);
        File infile = new File(filename);

        int secondsPause = 1;
        PacedFileInputStream stream = new PacedFileInputStream(infile, secondsPause);

        long startTime = new Date().getTime();

        int cnt = 0;
        @SuppressWarnings("unused")
        int inint;
        while (0 != (inint = stream.read())) {
            cnt++;
        }
        
        int pc = stream.getPauseCount();
        stream.close();

        long elapsedtime = new Date().getTime() - startTime;

        assertEquals("Expecting number of pauses in stream ", 39, pc);

        if (isDebugMode()){
            System.out.println("File: " + filename + " size " + infile.length());
            System.out.println("bytes = " + cnt + " pauses count " + pc);
            System.out.println("Time to process/read " + elapsedtime);
        }
    }

}
