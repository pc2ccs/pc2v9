package services;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketMsgType;
import communication.WTIWebsocket;
import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClarificationEventListener;

public class ClarificationService implements IClarificationEventListener {
	
	private String teamId;
	private WTIWebsocket client;
	
	public ClarificationService(String pId, WTIWebsocket socket) {
		this.teamId = pId;
		client = socket;
	}
	
	@Override
	public void clarificationAdded(IClarification arg0) {
        //This method is used by two (or more?) methods.
        //Whenever an announcement that this user should receive is made this method is called
        //Also whenever the team sends a clarification to be answered by the judges.
        if ( !arg0.isAnswered()) {  //case when the team sends a clarification to be answered.
            return;
        }
        String clarId = String.format("%s-%s", arg0.getSiteNumber(),arg0.getNumber());
        
        JsonObject builder = Json.createObjectBuilder()
                .add("type", WebsocketMsgType.ANNOUNCEMENT.name().toLowerCase())
                .add("id", clarId)
                .add("teamId", this.teamId)
                .build();
        
        this.client.sendMessage(builder.toString());
	}

	@Override
	public void clarificationAnswered(IClarification arg0) {
		
		String clarId = String.format("%s-%s", arg0.getSiteNumber(),arg0.getNumber());
		
		JsonObject builder = Json.createObjectBuilder()
				.add("type", WebsocketMsgType.CLARIFICATION.name().toLowerCase())
				.add("id", clarId)
				.add("teamId", this.teamId)
				.build();

		this.client.sendMessage(builder.toString());
	}

	@Override
	public void clarificationRemoved(IClarification arg0) {
		
	}

	@Override
	public void clarificationUpdated(IClarification arg0) {
		
	}
	
}
