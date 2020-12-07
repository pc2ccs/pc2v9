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
