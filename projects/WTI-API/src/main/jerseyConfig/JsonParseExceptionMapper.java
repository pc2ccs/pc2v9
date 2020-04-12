package jerseyConfig;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

@Provider
public class JsonParseExceptionMapper  implements ExceptionMapper<JsonParseException> {

	@Override
	public Response toResponse(JsonParseException exception) {
		JsonObject builder = Json.createObjectBuilder()
				.add("message", "Parse Exception. Invalid Property")
				.build();
	       return Response
	                .status(Response.Status.BAD_REQUEST)
	                .entity(builder)
	                .type( MediaType.APPLICATION_JSON)
	                .build();
	}
}
