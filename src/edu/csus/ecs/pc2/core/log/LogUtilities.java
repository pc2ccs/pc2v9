package edu.csus.ecs.pc2.core.log;

import java.io.File;

import edu.csus.ecs.pc2.core.Utilities;

public class LogUtilities {
    
    /**
     * Add file name, size and contents to log.
     * 
     * @param log log to output to
     * @param filename name of file contents to add to log
     * @param maximumLinesToOutput max output lines from file
     * @param prefixString a prefix string to prepend to each line.
     */
    public static void addFileToLog(Log log, String filename, int maximumLinesToOutput, String prefixString){
        
        if (prefixString == null){
            prefixString = "";
        }
        
        try {
            String[] lines = Utilities.loadFile(filename, maximumLinesToOutput  + 1 );
            
            // need to add an eclipsis if file has more than maximumLinesToOutput
            if (lines.length > maximumLinesToOutput){
                lines[maximumLinesToOutput-1] = "...";
            }
            
            File file = new File (filename);
            
            long fileSize = file.length();

            log.info(prefixString+" File: "+filename+" bytes="+fileSize);
            for (String string : lines) {
                log.info(prefixString+" "+string);
            }
            
        } catch (Exception e) {
            log.log(Log.WARNING, prefixString + " Unable to add file contents, filename = "+filename+" "+e.getMessage());
        }
    }
}
