/**
 * 
 */
package edu.csus.ecs.pc2.core.util;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author pc2@ecs.csus.edu
 *
 */
public final class JUnitUtilities {
    private static String baseDir = "."; //$NON-NLS-1$
    private static final String SEP = File.separator;
    private JUnitUtilities() {
        super();
    }

    /**
     * 
     * @param fileOrDir
     * @return null if fileOrDir not found, otherwise parent directory of fileOrDir.
     */
    public static String locate(String fileOrDir) {
        File location = new File(baseDir+File.separator+fileOrDir);
        if (location != null && location.exists()) {
            return baseDir;
        } else {
            // we need to find it
            if (baseDir.equals(".")) { //$NON-NLS-1$
                // good, start checking the path
                String newBase = searchCLASSPATH(fileOrDir);
                if (newBase != null) {
                    baseDir=newBase;
                    return newBase;
                }
            } else {
                // start over in . then start checking the path
                String newBase = search(fileOrDir, "."); //$NON-NLS-1$
                if (newBase != null) {
                    baseDir=newBase;
                    return newBase;
                } else {
                    newBase = searchCLASSPATH(fileOrDir);
                    if (newBase != null) {
                        baseDir=newBase;
                        return newBase;
                    }
                }
            }
        }
        // XXX grrr, there is nothing in System.getProperties() or System.getenv()
        // that tells us where we are :(  as of 20091206
        // XXX this is cruise control specific, and really should be changed
        //    when a branch is copied off
        String newBase = search(fileOrDir, "projects"+File.separator+"pc2v9"); //$NON-NLS-1$ //$NON-NLS-2$
        if (newBase != null) {
            baseDir=newBase;
            return newBase;
        }
        return null;
    }
    private static String searchCLASSPATH(String fileOrDir) {
        String location = null;
        String cp = System.getProperty("java.class.path");
        StringTokenizer st = new StringTokenizer(cp, File.pathSeparator);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            File dir = new File(token);
            if (dir.exists()) {
                if (dir.isFile() && dir.toString().endsWith("pc2.jar")) {
                    // usually in dist or lib, so up one directory
                    String newLocation = search(fileOrDir, dir.getParent() + SEP + "..");
                    if (newLocation != null) {
                        location = newLocation;
                        break;
                    }
                } else {
                    if (dir.isDirectory()) {
                        // try ../.. then . then ..
                        String newLocation = search(fileOrDir, token+SEP+".."+SEP+"..", token, token+SEP+"..");
                        if (newLocation != null) {
                            location = newLocation;
                            break;
                        }
                    }
                }
            }
        }
        return location;
    }
    private static String search(String fileOrDir, String...dirs) {
        for (String dir : dirs) {
            File location = new File(dir+File.separator+fileOrDir);
            if (location != null && location.exists()) {
                try {
                    return(new File(dir).getCanonicalPath());
                } catch (IOException e) {
                    System.out.println("failed to resolve "+location.toString());
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
