// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * A set of methods for CLICS API JSON
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class CLICSJsonUtilities {
    
    private static ObjectMapper mapperField;

    /**
     * Return a list of CLICS Awards for a contest.
     * 
     * @param contest
     * @return
     */
    public static List<CLICSAward> createAwardsList (IInternalContest contest){
        List<CLICSAward> list = new ArrayList<CLICSAward>();
        
        Run[] runs = contest.getRuns();
        
        if (runs.length == 0) {
            return list;
        }
        
        
        return list;
    }
    
    /**
     * Load list of CLICS awards json from file.
     * @param filename
     * @return
     * @throws IOException 
     */
    public static List<CLICSAward> readAwardsList (String filename) throws IOException{
        List<CLICSAward> list = new ArrayList<CLICSAward>();
        
        String[] lines = Utilities.loadFile(filename);
        String json = String.join(" ",  lines);
        
        List<CLICSAward> inList  = getMapper().readValue(json, new TypeReference<List<CLICSAward>>(){});
        if (inList != null) {
            list = inList;
        }
        
        return list;
    }
        
    

    /**
     * Get an object mapper that ignores unknown properties.
     * 
     * @return an object mapper that ignores unknown properties
     */
    public static final ObjectMapper getMapper() {
        
        if (mapperField != null) {
            return mapperField;
        }

        mapperField = new ObjectMapper();
        mapperField.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapperField;
    }
    
}
