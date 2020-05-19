package communication;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WTIWebsocket  {
	private Session session;
	
	public WTIWebsocket(URI url) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, url);
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().contains("connection refused")) {
				System.err.println("WTIWebsocket connection refused (is the WTI Jetty server running?)");
			}
			throw new RuntimeException(String.format("Error in WTIWebsocket: %s", e));
		}
	}
	
	public WTIWebsocket() {}

	@OnOpen
	public void connect(Session session) {
		this.session = session;
		this.session.setMaxIdleTimeout(0);
	}

	@OnMessage
	public void message(String message) {

	}

	@OnClose
	public void close() {
		this.session = null;
	}

	public void sendMessage(String message) {
		this.session.getAsyncRemote().sendText(message);
	}
	
	@OnError
	public void onError(Throwable throwable) {
		System.err.println("WTIWebsocket.error() called with ");
		System.err.println("  Throwable = " + throwable.toString());
	}

}
