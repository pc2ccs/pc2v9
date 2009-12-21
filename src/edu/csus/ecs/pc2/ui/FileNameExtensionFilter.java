/**
 * 
 */
package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.util.HashSet;

import javax.swing.filechooser.FileFilter;

/**
 * Flexible FileFilter based on allowed extenstions.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class FileNameExtensionFilter extends FileFilter {

    private String description = "";
    private HashSet<String> extList = new HashSet<String>();
    
    /**
     * 
     */
    public FileNameExtensionFilter(String desc, String... exts) {
        description=desc;
        extList.clear();
        for (String ext : exts) {
            extList.add("."+ext); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File f) {
        boolean result = false;
        if (f.isDirectory()) {
            result = true;
        } else {
            String fileName = f.getName();
            for (String ext : extList) {
               if(fileName.endsWith(ext)) {
                   result = true;
                   break;
               }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

}
