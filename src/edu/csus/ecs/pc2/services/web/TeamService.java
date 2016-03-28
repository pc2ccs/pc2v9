package edu.csus.ecs.pc2.services.web;

import java.util.Vector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * WebService for handling teams
 * @author ICPC
 *
 */
@Path("/teams")
@Produces(MediaType.APPLICATION_JSON)
public class TeamService {

    private IInternalContest contest;

    public TeamService(IInternalContest contest, IInternalController controller) {
        super();
        this.contest = contest;
    }

    /**
     * This method returns a String representation of the current contest teams in JSON format. The returned string is a JSON array with one team description per array element.
     * 
     * @return a String containing the contest teams in JSON form
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProblems() {

        // get the team accounts from the contest
        Vector<Account> vectorOfTeams = contest.getAccounts(Type.TEAM);

        // copy the team accounts into an array
        Account[] teams = new Account[vectorOfTeams.size()];
        for (int i = 0; i < teams.length; i++) {
            teams[i] = vectorOfTeams.get(i);
        }

        // map the team descriptions into JSON form
        ObjectMapper mapper = new ObjectMapper();

        // define a default string in case the mapper fails
        String jsonTeams = "{" + "\"teams\"" + ":" + "\"empty\"" + "}";
        try {
            jsonTeams = mapper.writeValueAsString(teams);
        } catch (JsonProcessingException e) {
            // TODO: log exception
            // TODO Auto-generated catch block
            e.printStackTrace();
            // TODO: return HTTP error
        }

        // TODO: figure out how to set the Response to "OK" (or whether this is necessary)
        // return Response.status(Response.Status.OK).build();

        // output the response to the requester (note that this actually returns it to Jersey,
        // which forwards it to the caller as the HTTP response).
        return jsonTeams;

    }

}
