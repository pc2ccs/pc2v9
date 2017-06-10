package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.report.IReport;

/**
 * Compare report titles.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class ReportComparator implements Comparator<IReport>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -764147025954449471L;

    @Override
    public int compare(IReport report1, IReport report2) {
        
        //  return clientId1.getClientType().compareTo(clientId2.getClientType());
        return report1.getReportTitle().toUpperCase().compareTo(report2.getReportTitle().toUpperCase());
    }

}
