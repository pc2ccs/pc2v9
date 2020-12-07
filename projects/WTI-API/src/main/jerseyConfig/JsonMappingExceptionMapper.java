
package jerseyConfig;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException > {

	@Override
	public Response toResponse(JsonMappingException exception) {
		JsonObject builder = Json.createObjectBuilder()
				.add("message", "Mapping exception")
				.build();
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(builder)
                .type( MediaType.APPLICATION_JSON)
                .build();
	}


}
