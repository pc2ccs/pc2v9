package edu.csus.ecs.pc2.imports.ccs;

import java.util.Vector;

/**
 * Utilities for reading lists.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: CCSList.java 177 2011-04-08 02:25:07Z laned $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9sandbox/trunk/src/edu/csus/ecs/pc2/imports/ccs/CCSList.java $
public final class CCSListUtilities {
    
    private CCSListUtilities() {
        super();
    }

    /**
     * Remove comment and blank lines.
     * 
     * @param lines
     * @return
     */
    public static String[] filterOutCommentLines(String[] lines) {
        if (lines.length < 1) {
            return lines;
        }

        Vector<String> newLines = new Vector<String>();

        for (String line : lines) {

            if (line.trim().startsWith("#")) {
                continue;
            }

            if (line.trim().length() == 0) {
                continue;
            }
            newLines.add(line);
        }

        return (String[]) newLines.toArray(new String[newLines.size()]);
    }
}
