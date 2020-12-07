package edu.csus.ecs.pc2.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides a singleton instance of the Jackson {@link ObjectMapper},
 * a tool for mapping to/from JSON.
 * Use method {@link #getObjectMapper()} to obtain the singleton ObjectMapper instance.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class JSONObjectMapper {
    
    private static ObjectMapper objectMapper = null;
    
    /**
     * Returns a singleton instance of the Jackson {@link ObjectMapper}.
     * 
     *  The returned {@link ObjectMapper} is configured with default settings, with one exception:
     *  the value of {@link com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping} is set to 
     *  {@link com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping#NON_FINAL}.
     *  
     * @return a Jackson {@link ObjectMapper}
     */
    public static ObjectMapper getObjectMapper() {
        
        if (objectMapper==null) {
            objectMapper = new ObjectMapper();
            objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);            
        }
        return objectMapper;
    }

}
