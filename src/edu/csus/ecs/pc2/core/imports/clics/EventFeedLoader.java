package edu.csus.ecs.pc2.core.imports.clics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class EventFeedLoader {

    private static ObjectMapper mapper = new ObjectMapper();

    public static List<TeamAccount> createTeamAccounts (String [] lines) throws JsonParseException, JsonMappingException, IOException {
        List<TeamAccount> list = new ArrayList<TeamAccount>();
        for (String jsonLine  : lines) {
            //            ObjectMapper mapper = new ObjectMapper();
            //            EventFeedLine eFeedLine = EventFeedLine.fromJSON(jsonLine);
            //            TeamAccount teamAccount = mapper.readValue(eFeedLine.getData().toString(), TeamAccount.class);

            TeamAccount teamAccount = createTeamAccount(jsonLine);
            list.add(teamAccount);
        }

        return list;
    }

    public static TeamAccount createTeamAccount (String jsonLine) throws JsonParseException, JsonMappingException, IOException {
        //        TeamAccount teamAccount = mapper.readValue(json, TeamAccount.class);
        EventFeedLine eFeedLine = EventFeedLine.fromJSON(jsonLine);
        TeamAccount teamAccount = mapper.readValue(eFeedLine.getData().toString(), TeamAccount.class);
        return teamAccount;
    }


}
