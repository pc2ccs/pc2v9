package services;

import javax.json.Json;
import javax.json.JsonObject;

import WebsocketEnums.WebsocketMsgType;
import communication.WTIWebsocket;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;

public class RunsService implements IRunEventListener {

	private String teamId;
	private WTIWebsocket client;

	public RunsService(String pId, WTIWebsocket socket) {
		this.teamId = pId;
		client = socket;
	}

	@Override
	public void runCheckedOut(IRun arg0, boolean arg1) {

		
	}

	@Override
	public void runCompiling(IRun arg0, boolean arg1) {

	}

	@Override
	public void runDeleted(IRun arg0) {

	}

	@Override
	public void runExecuting(IRun arg0, boolean arg1) {

	}

	@Override
	public void runJudged(IRun arg0, boolean arg1) {
		JsonObject builder = Json.createObjectBuilder()
				.add("type", WebsocketMsgType.JUDGED.name().toLowerCase())
				.add("id", String.format("%s-%s", arg0.getSiteNumber(), arg0.getNumber()))
				.add("teamId", this.teamId)
				.build();

		this.client.sendMessage(builder.toString());
	}

	@Override
	public void runJudgingCanceled(IRun arg0, boolean arg1) {

	}

	@Override
	public void runSubmitted(IRun arg0) {

	}

	@Override
	public void runUpdated(IRun arg0, boolean arg1) {

	}

	@Override
	public void runValidating(IRun arg0, boolean arg1) {

	}

}
