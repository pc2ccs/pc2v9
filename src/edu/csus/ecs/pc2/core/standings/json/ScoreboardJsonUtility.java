// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.standings.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.csus.ecs.pc2.core.standings.ContestStandings;

public class ScoreboardJsonUtility {
    
//    public String createScoreboardJSON (conetst)
    
    public static String createScoreboardJSON (ContestStandings contestStandings) throws JsonProcessingException {

        ScoreboardJsonModel model = new ScoreboardJsonModel(contestStandings);
        
        //Doug's original code:
//        ObjectMapper om = JSONObjectMapper.getObjectMapper();
//        String jsonString = om.writeValueAsString(model);    
        
        //jlc:
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objMapper.disableDefaultTyping();
        
//        String jsonString = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
        String jsonString = objMapper.writeValueAsString(model);

    
        return jsonString;
      }
    
}
