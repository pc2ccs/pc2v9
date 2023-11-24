// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService to handle languages
 * 
 * @author John Buck
 *
 */
@Path("/contests/{contestId}/languages")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class LanguageService implements Feature {

    @SuppressWarnings("unused")
    private IInternalContest model;

    @SuppressWarnings("unused")
    private IInternalController controller;

    public LanguageService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * Returns a representation of the current model languages in JSON format. The returned value is a JSON array with one language description per array element, complying with 2023-06
     * 
     * @param contestId The contest
     * @return a {@link Response} object containing the model languages in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLanguages(@PathParam("contestId") String contestId) {
       
        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == false) {
            return Response.status(Response.Status.NOT_FOUND).build();        
        }
        
        ArrayList<CLICSLanguage> llist = new ArrayList<CLICSLanguage>();
        
        // get the languages, one-at-a-time from the model
        for(Language language: model.getLanguages()) {
            if (language.isActive()) {
                llist.add(new CLICSLanguage(language));
            }
        }
        try {
            ObjectMapper mapper = JSONUtilities.getObjectMapper();
            String json = mapper.writeValueAsString(llist);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error creating JSON for languages " + e.getMessage()).build();
        }
    }

    /**
     * Returns a representation of the specified language for the specified contest in JSON format. The returned value is compliant with 2023-06
     * 
     * @param contestId The contest
     * @param languageId The language
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{languageId}/")
    public Response getLanguage(@PathParam("contestId") String contestId, @PathParam("languageId") String languageId) {
        
        // get the languages, one-at-a-time from the model
        for(Language language: model.getLanguages()) {
            if (language.isActive() && language.getElementId().toString().equals(languageId)) {
                return Response.ok(new CLICSLanguage(language).toJSON(), MediaType.APPLICATION_JSON).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     * 
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(SecurityContext sc) {
        return(new CLICSEndpoint("languages", JSONUtilities.getJsonProperties(CLICSLanguage.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
