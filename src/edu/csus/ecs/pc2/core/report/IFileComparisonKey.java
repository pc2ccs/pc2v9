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
     * @param object may be a String or Object
     * @return a key unique for the object data
     */
    public String getKey (Object object);

}
