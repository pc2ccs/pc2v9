package jerseyConfig;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@Provider
public class UnrecognizedPropertyExceptionMapper  implements ExceptionMapper<UnrecognizedPropertyException> {

	@Override
	public Response toResponse(UnrecognizedPropertyException exception) {
		JsonObject builder = Json.createObjectBuilder()
				.add("message", "UnrecognizedProperty")
				.build();
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(builder)
                .type( MediaType.APPLICATION_JSON)
                .build();
	}
}
