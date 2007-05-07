package edu.csus.ecs.pc2.core.report;

import java.io.IOException;

import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Report Plugin interface.
 * @author pc2@ecs.csus.edu
 *
 */

// $HeadURL$
public interface IReport extends UIPlugin {

    /**
     * Create report to a file.
     * @param filename
     * @param filter
     * @throws IOException
     */
    void createReportFile (String filename, Filter filter) throws IOException;
    
    /**
     * Output report to a string array.
     * @param filter
     * @return report as a array of string
     */
    String [] createReport (Filter filter);
    
    /**
     * Create XML output for report
     * @param filter
     * @return
     */
    String createReportXML (Filter filter); 

    /**
     * Format title for report.
     * 
     * Used to display to the user to pick a report.
     * 
     * @return name of report
     */
    String getReportTitle ();
}
