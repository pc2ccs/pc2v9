package edu.csus.ecs.pc2.core.report;

import java.io.IOException;
import java.io.PrintWriter;

import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Report Plugin interface.
 * @author pc2@ecs.csus.edu
 * @version $Id$
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
     * @return report in XML as a string
     */
    String createReportXML (Filter filter); 
    
    /**
     * Write report (no header or footer).
     * 
     * @param printWriter
     */
    void writeReport(PrintWriter printWriter) throws Exception;

    /**
     * Format title for report.
     * 
     * Used to display to the user to pick a report.
     * 
     * @return name of report
     */
    String getReportTitle ();

    /**
     * Get filter for report.
     * @return the filter for the report.
     */
    Filter getFilter();
    
    /**
     * Set filter for report.
     * @param filter
     */
    void setFilter(Filter filter);
    
    /**
     * Print header for report.
     * @param printWriter
     */
    void printHeader(PrintWriter printWriter);
    
    /**
     * Print footer for report.
     * @param printWriter
     */
    void printFooter(PrintWriter printWriter);
}
