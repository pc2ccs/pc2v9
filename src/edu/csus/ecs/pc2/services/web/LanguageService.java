package edu.csus.ecs.pc2.services.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService to handle languages
 * @author ICPC
 *
 */
@Path("/languages")
@Produces(MediaType.APPLICATION_JSON)
public class LanguageService {

    private IInternalContest model;
    @SuppressWarnings("unused")
    private IInternalController controller;

    public LanguageService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current model languages in JSON format. 
     * The returned value is a JSON array with one language description per array element, matching the
     * description at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
     * 
     * @return a {@link Response} object containing the model languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguages() {

        // get the problems from the model
        Language[] languages = model.getLanguages();

        // get an object to map the language descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < languages.length; i++) {
            Language language = languages[i];
            dumpLanguage(mapper, childNode, language);
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{languageId}/")
    public Response getLanguage(@PathParam("languageId") String languageId) {
        // get the groups from the model
        Language[] languages = model.getLanguages();

        languageId = languageId.replaceAll("_", " ");
        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < languages.length; i++) {
            Language language = languages[i];
            if (language.getElementId().toString().equals(languageId)) {
                dumpLanguage(mapper, childNode, language);
            }
        }
        return Response.ok(childNode.toString(),MediaType.APPLICATION_JSON).build();
    
    }

    private void dumpLanguage(ObjectMapper mapper, ArrayNode childNode, Language language) {
        ObjectNode element = mapper.createObjectNode();
        element.put("id", language.getElementId().toString().replaceAll(" ", "_"));
        element.put("name", language.getDisplayName());
        childNode.add(element);

        
    }

}
