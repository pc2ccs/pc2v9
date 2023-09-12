// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

import java.util.List;

import edu.csus.ecs.pc2.core.imports.clics.FieldCompareRecord;
import edu.csus.ecs.pc2.core.imports.clics.FileComparison;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public class FileComparisonUtilitiesTest extends AbstractTestCase {

    public void testcreateTSVFileComparison() throws Exception {

        String domjResultsDir = "testdata/resultscompwork/results/domjudge";
        String pc2ResultsDir = "testdata/resultscompwork/results/pc2";

        FileComparison comp = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, domjResultsDir, pc2ResultsDir);
        
        assertEquals("Expecting number of comparisons", 255, comp.getComparedFields().size());
        
        List<FieldCompareRecord> compF = comp.getComparedFields();
        for (FieldCompareRecord fieldCompareRecord : compF) {
            System.out.println("debug 22 field "+fieldCompareRecord.toJSON());
        }
        assertEquals("Expecting no differences ", 0, comp.getNumberDifferences());
        
        
    }
    
    public void testIdenticalFiles() throws Exception {
        
        String dirOne = "testdata/resultscompwork/results/domjudge";
        String dirTwo = dirOne;

        FileComparison comp = FileComparisonUtilities.createTSVFileComparison(ResultsFile.RESULTS_FILENAME, dirOne, dirTwo);
        
        assertEquals("Expecting number of comparisons", 255, comp.getComparedFields().size());
        List<FieldCompareRecord> compF = comp.getComparedFields();
        assertEquals("Expecting no differences ", 0, comp.getNumberDifferences());

        
    }
    
    @Override
    public void testForValidXML(String xml) throws Exception {
        // TODO Auto-generated method stub
        super.testForValidXML(xml);
    }

}
