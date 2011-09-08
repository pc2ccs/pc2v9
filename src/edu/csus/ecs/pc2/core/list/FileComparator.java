package edu.csus.ecs.pc2.core.list;

import java.io.File;
import java.util.Comparator;

/**
 * Compare File by name.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: FileComparator.java 217 2011-08-28 13:24:47Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/report/FileComparator.java $
public class FileComparator implements Comparator<File> {

    public int compare(File file1, File file2) {
        return file1.getName().compareTo(file2.getName());
    }
}
