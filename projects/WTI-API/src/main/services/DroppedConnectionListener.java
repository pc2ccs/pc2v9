package services;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketMsgType;
import communication.WTIWebsocket;
import edu.csus.ecs.pc2.api.listener.IConnectionEventListener;

/**
 * This class is a Service which listens for a dropped connection to the PC2 Server, and on droppedConnection
 * sends a message to the team client via the appropriate websocket.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class DroppedConnectionListener implements IConnectionEventListener {

	
	private String teamId;
	private WTIWebsocket client;

	/**
	 * Constructs a DroppedConnectionListener associated with the specified team and its corresponding websocket.
	 * 
	 * @param pId a String identifying the team for which dropped connections are to be listened for.
	 * @param socket the {@link WTIWebsocket} connected to the team client.
	 */
	public DroppedConnectionListener(String pId, WTIWebsocket socket) {
		this.teamId = pId;
		client = socket;
	}
	

	/**
	 * This method is invoked when the team browser client identified by "teamId" and associated with
	 * websocket "client" has received a "droppedConnection" notification from the PC2 Server, for example
	 * because an Admin or Server has invoked a "forced logout" for the team.
	 * The method builds a JSON notification string for a dropped connection and sends it to the team
	 * via the websocket.
	 */
	@Override
	public void connectionDropped() {

		JsonObject builder = Json.createObjectBuilder().add("type", WebsocketMsgType.CONNECTION_DROPPED.name().toLowerCase())
				.add("id", "1-1") // arg0.getSite().getName(),arg0.getSite().getNumber() randomly throws null
									// pointer error... using the arbitrary value "1-1" since id is really useless here
				.add("teamId", this.teamId).build();

		this.client.sendMessage(builder.toString());
	}
}
