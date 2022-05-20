package edu.csus.ecs.pc2.core.imports.clics;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;

/**
 * Test TeamAccount
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class TeamAccountTest extends TestCase {

    /**
     * Test a single account from a single line of JSON
     * 
     * @throws Exception
     */

    public void testOneTeam() throws Exception {

        // TODO handle parsing with groups array
//         String input = "{\"id\": \"105\", \"icpc_id\": \"449759\", \"name\": \"Kansas State University\", \"display_name\": \"Kansas State University\", \"organization_id\": \"k-state_edu\", \"group_ids\": [\"4\"]}";

        String input = "{\"id\": \"105\", \"icpc_id\": \"449759\", \"name\": \"Kansas State University\", \"display_name\": \"Kansas State University\", \"organization_id\": \"k-state_edu\"}";

        TeamAccount account = createTeamAccount(input);

        assertNotNull(account);

        assertEquals("Account id ", "105", account.getId());
        assertEquals("Account name ", "Kansas State University", account.getName());
        assertEquals("display_name ", "Kansas State University", account.getDisplay_name());
        assertEquals("organization_id ", "k-state_edu", account.getOrganization_id());

        String outJson = account.toJSON();

        assertNotNull(outJson);

    }

    private TeamAccount createTeamAccount(String json) throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        return (TeamAccount) mapper.readValue(json, TeamAccount.class);
    }

}
