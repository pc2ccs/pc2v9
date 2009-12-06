package edu.csus.ecs.pc2.core.model;

import java.io.File;

import edu.csus.ecs.pc2.core.util.JUnitUtilities;

import junit.framework.TestCase;
/**
 * Tests for ProblemDataFiles.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 *
 */
public class ProblemDataFilesTest extends TestCase {
    private String loadDir = "testdata" + File.separator;

    protected void setUp() throws Exception {
        String projectPath=JUnitUtilities.locate(loadDir);
        if (projectPath == null) {
            throw new Exception("Unable to locate "+loadDir);
        }
        File dir = new File(projectPath + File.separator + loadDir);
        if (dir.exists()) {
            loadDir = dir.toString() + File.separator;
        } else {
            System.err.println("could not find " + loadDir);
            throw new Exception("Unable to locate "+loadDir);
        }
        super.setUp();
    }

    public void testEmpty() {
        Problem p1 = new Problem("A");
        ProblemDataFiles pdf1 = new ProblemDataFiles(p1);
        ProblemDataFiles pdf2 = null;
        try {
            pdf2 = pdf1.copy(p1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assertTrue("copy failure", false);
            // if this was in clone, we would either fail the clone
            // or blank out the validator/data/answer file names.
        }
        assertTrue("copy failed", pdf1.isSameAs(pdf2));
    }
    
    public void testOne() {
        Problem p1 = new Problem("A");
        ProblemDataFiles pdf1 = new ProblemDataFiles(p1);
        SerializedFile dataFile;
        dataFile = new SerializedFile(loadDir+"pdftest"+File.separator+"sumit.dat");
        pdf1.setJudgesDataFile(dataFile);
        SerializedFile[] fileArray = new SerializedFile[1];
        fileArray[0] = dataFile;
        pdf1.setJudgesAnswerFiles(fileArray);
        ProblemDataFiles pdf2 = null;
        
        try {
            pdf2 = pdf1.copy(p1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assertTrue("copy failure", false);
            // if this was in clone, we would either fail the clone
            // or blank out the validator/data/answer file names.
        }
        assertTrue("copy failed with 1 answer/1 data file", pdf1.isSameAs(pdf2));
        
    }
    public void testTwo() {
        Problem p1 = new Problem("A");
        ProblemDataFiles pdf1 = new ProblemDataFiles(p1);
        SerializedFile dataFile;
        dataFile = new SerializedFile(loadDir+"pdftest"+File.separator+"sumit.dat");
        pdf1.setJudgesDataFile(dataFile);
        SerializedFile[] fileArray = new SerializedFile[2];
        fileArray[0] = dataFile;
        fileArray[1] = new SerializedFile(loadDir+"pdftest"+File.separator+"sumit.ans");
        pdf1.setJudgesAnswerFiles(fileArray);
        ProblemDataFiles pdf2 = null;
        
        try {
            pdf2 = pdf1.copy(p1);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assertTrue("copy failure", false);
            // if this was in clone, we would either fail the clone
            // or blank out the validator/data/answer file names.
        }
        assertTrue("copy failed with 2 answer/1 data file", pdf1.isSameAs(pdf2));
        
    }
}
