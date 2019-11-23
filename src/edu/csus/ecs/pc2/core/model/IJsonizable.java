package edu.csus.ecs.pc2.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Defines the interface implemented by classes which can be represented in JSON form.
 * 
 * @author john
 *
 */
public interface IJsonizable {

    /**
     * Returns an object constructed from the specified JSON String.
     *  
     * @param json a JSON string describing an object
     * @return an object constructed from JSON, or null if an exception occurred while mapping the JSON to an object
     * @throws JsonMappingException if the input JSON is invalid
     */
    public Object fromJSON(String json) ;
    
    /**
     * Returns a JSON string representation of this Object.
     * 
     * @return a String containing a JSON representation of this object, or null if an error occurs during creation of the JSON string
     * @throws JsonProcessingException if an error occurs during JSON generation
     */
    public String toJSON() ;

}
