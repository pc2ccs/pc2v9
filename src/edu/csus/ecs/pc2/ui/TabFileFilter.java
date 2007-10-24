package edu.csus.ecs.pc2.ui;

import javax.swing.filechooser.FileFilter;

/**
 * Filters JFileChooser to just .tab files (for ICPC Import)
 * 
 * @author: pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TabFileFilter extends FileFilter {
    /**
     * TabFileFilter constructor comment.
     */
    public TabFileFilter() {
        super();
    }

    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept(java.io.File f) {
        boolean result = false;
        if (f.isDirectory()) {
            result = true;
        } else {
            String ext = null;
            String fileName = f.getName();
            int i = fileName.lastIndexOf(".");
            if (i > 1) {
                ext = fileName.substring(i + 1);
                if (ext.equalsIgnoreCase("tab")) {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * The description of this filter.
     * 
     * @see FileView#getName
     */
    public String getDescription() {
        return "ICPC import files";
    }
}
