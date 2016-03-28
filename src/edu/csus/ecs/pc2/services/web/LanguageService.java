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
     * This method returns a String representation of the current contest languages in JSON format. The returned string is a JSON array with one language description per array element.
     * 
     * @return a String containing the contest languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getLanguages() {

        // get the problems from the contest
        Language[] languages = contest.getLanguages();

        // map the language descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        String jsonLanguages = "{" + "\"languages\"" + ":" + "\"empty\"" + "}";
        try {
            jsonLanguages = mapper.writeValueAsString(languages);
        } catch (JsonProcessingException e) {
            // TODO: log exception
            e.printStackTrace();
            // TODO: return HTTP error
        }

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonLanguages;

    }
}
