// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.report;

/**
 * Report File interface.
 * 
 * Provides a way to flag/indicate that a header/footer should be
 * suppressed esp for XML, tsv, etc. files.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public interface IReportFile extends IReport {

    /**
     * Suppress header and footer output.
     * 
     * @return true if to suppress, false otherwise.
     */
    boolean suppressHeaderFooter();

}
