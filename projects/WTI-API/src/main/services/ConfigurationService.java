package services;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketType;
import communication.WTIWebsocket;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.ContestEvent.EventType;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;

public class ConfigurationService implements IConfigurationUpdateListener {

	
	private String teamId;
	private WTIWebsocket client;

	public ConfigurationService(String pId, WTIWebsocket socket) {
		this.teamId = pId;
		client = socket;
	}
	
	@Override
	public void configurationItemAdded(ContestEvent arg0) {
		
	}

	@Override
	public void configurationItemRemoved(ContestEvent arg0) {
		
	}

	@Override
	public void configurationItemUpdated(ContestEvent arg0) {

		if(arg0.getEventType().name().equals(EventType.CONTEST_CLOCK.name())) {
			
			JsonObject builder = Json.createObjectBuilder()
					.add("type", WebsocketType.CONTEST_CLOCK.name().toLowerCase())
					.add("id", "1-1") // arg0.getSite().getName(),arg0.getSite().getNumber() randomly throws null pointer error... "1-1" since id is really useless here
					.add("teamId", this.teamId)
					.build();

			this.client.sendMessage(builder.toString());
		}
	}

}
