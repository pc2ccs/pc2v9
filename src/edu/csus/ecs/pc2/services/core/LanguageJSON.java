package edu.csus.ecs.pc2.services.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * Language JSON.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class LanguageJSON extends JSONUtilities {
    
    private ObjectMapper mapper = new ObjectMapper();
    

    public String createJSON(IInternalContest contest, Language language, int languageNumber) {
        
        ObjectNode element = mapper.createObjectNode();
        ArrayNode childNode = mapper.createArrayNode();
        element.put("id",  Integer.toString(languageNumber));
        element.put("name", language.getDisplayName());
        childNode.add(element);
        
        return stripOuterJSON(childNode.toString());
    }
}
