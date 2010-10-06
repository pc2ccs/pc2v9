package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.report.IReport;

/**
 * Report comparator by Report Title.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ReportNameByComparator implements Comparator<IReport>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6187488261582047310L;

    public int compare(IReport report1, IReport report2) {
        return report1.getReportTitle().compareTo(report2.getReportTitle());
    }
}
