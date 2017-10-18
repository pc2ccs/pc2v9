package edu.csus.ecs.pc2.services.web;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService to handle languages
 * 
 * @author ICPC
 *
 */
@Path("/contest/languages")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class LanguageService implements Feature {

    private IInternalContest model;

    private IInternalController controller;

    private JSONTool jsonTool;

    public LanguageService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
        jsonTool = new JSONTool(model, controller);
    }

    /**
     * This method returns a representation of the current model languages in JSON format. The returned value is a JSON array with one language description per array element, matching the description
     * at {@link https://clics.ecs.baylor.edu/index.php/Draft_CCS_REST_interface#GET_baseurl.2Flanguages}.
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
            childNode.add(jsonTool.convertToJSON(language));
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Path("{languageId}/")
    public Response getLanguage(@PathParam("languageId") String languageId) {
        // get the languages from the model
        Language[] languages = model.getLanguages();

        // get an object to map the groups descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode childNode = mapper.createArrayNode();
        for (int i = 0; i < languages.length; i++) {
            Language language = languages[i];
            if (language.getElementId().toString().equals(languageId)) {
                childNode.add(jsonTool.convertToJSON(language));
            }
        }
        return Response.ok(childNode.toString(), MediaType.APPLICATION_JSON).build();

    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
