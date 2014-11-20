package jp.co.worksap.roster.rest.exceptions;

import javax.ejb.AccessLocalException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<AccessLocalException> {
	@Override
	public Response toResponse(AccessLocalException ex) {
		return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).build();
	}
}