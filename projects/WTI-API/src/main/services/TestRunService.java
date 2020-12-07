package services;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketMsgType;
import communication.WTIWebsocket;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.listener.ITestRunListener;

public class TestRunService implements ITestRunListener {

	private String teamId;
	private WTIWebsocket client;

	public TestRunService(String pId, WTIWebsocket socket) {
		this.teamId = pId;
		client = socket;
	}

	@Override
	public void testRunDeleted(IRun arg0) {

	}

	@Override
	public void testRunSubmitted(IRun arg0) {

	}

	@Override
	public void testRunTestingCompleted(IRun arg0) {
		
		JsonObject builder = Json.createObjectBuilder()
				.add("type", WebsocketMsgType.TEST.name().toLowerCase())
				.add("id", String.format("%s-%s", arg0.getSiteNumber(), arg0.getNumber()))
				.add("teamId", this.teamId)
				.build();

		this.client.sendMessage(builder.toString());
	}

	@Override
	public void testRunUpdated(IRun arg0, boolean arg1) {

	}



}
