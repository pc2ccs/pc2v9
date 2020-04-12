package communication;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;

import config.Logging;

import javax.websocket.server.PathParam;

@ServerEndpoint("/WTISocket/{team_id}")
public class WTIWebsocketMediator {
	private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
	Logging logger = Logging.getLogger();

	@OnOpen
	public void connect(Session session, @PathParam("team_id") String teamId) {
		session.getUserProperties().put("team_id", teamId);
		session.setMaxIdleTimeout(0);
		sessions.add(session);
	}

	@OnMessage
	public String message(Session session, String message, @PathParam("team_id") String teamId) {
		JsonReader reader = Json.createReader(new StringReader(message));
		JsonObject jObj = reader.readObject();
		
		String tId = jObj.getString("teamId");
		
		
		for (Session s: sessions) {
			try {
			if(s.getUserProperties().get("team_id").equals(tId))
				s.getAsyncRemote().sendText(message);
			}
			catch(NullPointerException e) {e.printStackTrace(); this.logger.logError(e.getMessage());}
		}
		return message;
	}

	@OnClose
	public void close(Session session , @PathParam("team_id") String teamId) {
		Iterator<Session> sIt = sessions.iterator();
		while(sIt.hasNext()) {
			Session s = sIt.next();
			
			if(s.getUserProperties().get("team_id").equals(teamId))
				sIt.remove();
		}

	}
	
    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println(String.format("error in mediator: ", t));        
    } 

}
