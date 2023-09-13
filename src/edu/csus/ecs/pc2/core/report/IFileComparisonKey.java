package edu.csus.ecs.pc2.core.report;

/**
 * File Comparison methods.
 * 
 * @author Douglas A. Lane, PC^2 team pc2@ecs.csus.edu
 */
public interface IFileComparisonKey {
    
    /**
     * Get key for comparing/matching rows.
     * 
     * @param line  
     * @return a key unique for the line data
     */
    public String getKey (String line);

}
