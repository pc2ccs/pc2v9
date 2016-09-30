package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;

/**
 * WebService to handle languages
 * @author ICPC
 *
 */
@Path("/languages")
@Produces(MediaType.APPLICATION_JSON)
public class LanguageService {

    private IInternalContest contest;
    private IInternalController controller;

    public LanguageService(IInternalContest inContest, IInternalController inController) {
        super();
        this.contest = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest languages in JSON format. 
     * The returned value is a JSON array with one language description per array element, matching the
     * description at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguages() {

        // get the problems from the contest
        Language[] languages = contest.getLanguages();

        // get an object to map the language descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        
        //the spec defines that the return value is an array ( [] in JSON)
        String jsonOutput = "[";  
        
        try {
            boolean addedOne = false;
            
            //for each defined language
            int index = 1;
            for (Language lang : languages) {

                //if we've already added one or more langs, add a comma separator
                if (addedOne) {
                    jsonOutput += "," ;
                }
                
                //add the language-object opening bracket
                jsonOutput += "{";
                
                //add the ID of the language to the output JSON.  The spec requires the ID to be an integer; it is
                //based on the integer position of the language in the Languages array
                jsonOutput += "\"id\":" + mapper.writeValueAsString(index) + ",";
                
                //add the language name to the output JSON
                String name = lang.getDisplayName();
                jsonOutput += "\"name\":\"" + name + "\"";
                
                //add the language-object closing bracket
                jsonOutput += "}";
                
                addedOne = true;
                index++;
            }

        } catch (JsonProcessingException e) {
            //log exception
            controller.getLog().log(Log.WARNING, "LanguageService: JsonProcessingException while creating language JSON: " + e, e);
            e.printStackTrace();
            //return HTTP error response code containing the exception message
            return Response.serverError().entity(e.getMessage()).build();
            
        } catch (Exception e) {
            //log exception
            controller.getLog().log(Log.WARNING, "LanguageService: Exception creating language JSON: " + e, e);
            e.printStackTrace();
            //return HTTP error response code containing the exception message
            return Response.serverError().entity(e.getMessage()).build();
        }
        
        //if we get here, everything went ok building the language JSON string
        jsonOutput += "]";

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return Response.ok(jsonOutput,MediaType.APPLICATION_JSON).build();

    }
}
