package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
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

    public LanguageService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
    }

    /**
     * This method returns a String representation of the current contest languages in JSON format. 
     * The returned string is a JSON array with one language description per array element, matching the
     * description at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a String containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getLanguages() {

        // get the problems from the contest
        Language[] languages = contest.getLanguages();

        // get an object to map the language descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        
        //the spec defines that the return value is an array ( [] in JSON)
        String jsonOutput = "[";  
        
        try {
            boolean addedOne = false;
            
            //for each defined language
            for (Language lang : languages) {

                //if we've already added one or more langs, add a comma separator
                if (addedOne) {
                    jsonOutput += "," ;
                }
                
                //add the language-object opening bracket
                jsonOutput += "{";
                
                //add the ID of the language to the output JSON.  The spec requires the ID to be an integer; it is
                //based on the fact that the toString() for an ElementID is assumed to be of the form "someString-someUniqueNumber"
                String idString = lang.getElementId().toString();
                Long id = new Long(idString.substring(idString.lastIndexOf('-')+1));
                jsonOutput += "\"id\":" + mapper.writeValueAsString(id) + ",";
                
                //add the language name to the output JSON
                String name = lang.getDisplayName();
                jsonOutput += "\"name\":\"" + name + "\"";
                
                //add the language-object closing bracket
                jsonOutput += "}";
                
                addedOne = true;
            }

        } catch (JsonProcessingException e) {
            // TODO: log exception
            System.err.println ("LanguageService: error: " + e.getMessage());
            e.printStackTrace();
            // TODO: return HTTP error
        } catch (Exception e) {
            // TODO: log exception
            System.err.println ("LanguageService: error: " + e.getMessage());
            e.printStackTrace();
            // TODO: return HTTP error
        }
        jsonOutput += "]";

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonOutput;

    }
}
