// Copyright (C) 1989-2025 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBException;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.exports.ccs.ResultsFile;
import edu.csus.ecs.pc2.services.core.JSONUtilities;
import edu.csus.ecs.pc2.services.eventFeed.WebServer;
/**
 * Webservice to handle results requests.
 * This can be used to retrieve results.tsv, results.csv or results.json depending on the query param.
 *
 * This is a non-CLICS standard endpoint.
 *
 * @author John Buck
 */
@Path("/contests/{contestId}/results")
@Produces(MediaType.APPLICATION_JSON)
@Provider
@Singleton
public class ResultsService implements Feature {

    public static final String RESULTS_FORMAT_TSV = "tsv";
    public static final String RESULTS_FORMAT_CSV = "csv";
    public static final String RESULTS_FORMAT_JSON = "json";

    private IInternalContest model;
    private IInternalController controller;

    public ResultsService(IInternalContest inContest, IInternalController inController) {
        super();
        this.model = inContest;
        this.controller = inController;
    }

    /**
     * This method returns a representation of the current contest results in one of several formats.
     *
     * @param servletRequest
     * @param sc
     * @param contestId
     * @param format - desired return format
     * @return {@link Response} object containing the requested results in the desired format
     */
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    public Response getResults(@Context HttpServletRequest servletRequest, @Context SecurityContext sc, @PathParam("contestId") String contestId,
            @QueryParam("format") String format) {

        // check contest id
        if(contestId.equals(model.getContestIdentifier()) == true) {
            ContestTime contestTime = model.getContestTime();
            // verify contest has started and user is admin
            if (contestTime.getElapsedMS() > 0 && sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN)) {

                ResultsFile resultsFile = new ResultsFile();
                String [] lines;

                // Default format is TSV if not supplied.  One might expect it to be JSON since this
                // is a CLICS api, however, since this is an "extension" to the API to facilitate
                // the ICPC historical (hysterical?) results format, it makes some sense to make
                // the default TSV.
                if (format == null) {
                    format = RESULTS_FORMAT_TSV;
                }
                // make sure we can do the format
                if (format.equalsIgnoreCase(RESULTS_FORMAT_CSV)) {
                    return(createResultsResponse(resultsFile.createCSVFileLines(model), MediaType.TEXT_PLAIN));
                }
                if(format.equalsIgnoreCase(RESULTS_FORMAT_TSV)) {
                    lines = resultsFile.createTSVFileLines(model);
                    return(createResultsResponse(resultsFile.createTSVFileLines(model), MediaType.TEXT_PLAIN));
                }
                if(format.equalsIgnoreCase(RESULTS_FORMAT_JSON)) {
                    // We first make a TSV file, then create json from that
                    try {
                        CLICS_Ex_Results results = new CLICS_Ex_Results(model, resultsFile.createTSVFileLines(model));
                        return Response.ok(results.toJSON(), MediaType.APPLICATION_JSON).build();
                    } catch (IllegalContestState | JAXBException | IOException e) {
                        controller.getLog().log(Log.WARNING, "Exception creating PC2 results JSON: " + e.getMessage(), e);
                        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
                    }
                } else {
                    // do not show (return) the results if a bad requested format
                    return Response.status(Status.BAD_REQUEST).build();
                }
            } else {
                // do not show (return) the results if the contest has not
                // been started and the requester is not special)
                return Response.status(Status.FORBIDDEN).build();
            }
        }
        // Contest not found
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Returns a response created by appending newlines to each string in passed
     * in array.  The response has the specified mime type.  This is used to create TSV
     * and CSV response.
     *
     * @param lines Array of strings comprising the data to return
     * @param mimeType desired mime type of response.
     * @return ready to return Response
     */
    private Response createResultsResponse(String [] lines, String mimeType)
    {
        StringBuilder result = new StringBuilder();
        for(String line: lines) {
            result.append(line);
            result.append(JSON202306Utilities.NL);
        }
        return Response.ok(result.toString(), mimeType).build();
    }

    /**
     * Retrieve access information about this endpoint for the supplied user's security context
     *
     * @param contest The contest is included in case the inclusion of a property depends on the permissions
     *        set for the connected client.  It is included for uniformity since this method is called as a result
     *        of introspection, and the caller does not know what the callee may need.  Therefore, the contest is
     *        always included, as is the SecurityContext below (for the same reason).
     * @param sc User's security information
     * @return CLICSEndpoint object if the user can access this endpoint's properties, null otherwise
     */
    public static CLICSEndpoint getEndpointProperties(IInternalContest contest, SecurityContext sc) {
        // Only admins can use this endpoint.  Returning null will not show the endpoint in the access list
        if(!sc.isUserInRole(WebServer.WEBAPI_ROLE_ADMIN) && !sc.isUserInRole(WebServer.WEBAPI_ROLE_JUDGE)) {
            return(null);
        }
        return(new CLICSEndpoint("results", JSONUtilities.getJsonProperties(CLICS_Ex_Results.class)));
    }

    @Override
    public boolean configure(FeatureContext arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
