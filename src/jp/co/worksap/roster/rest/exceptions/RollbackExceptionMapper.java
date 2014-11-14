package jp.co.worksap.roster.rest.exceptions;

import javax.transaction.RollbackException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;

@Provider
public class RollbackExceptionMapper implements
        ExceptionMapper<RollbackException> {

    @Override
    public Response toResponse(RollbackException ex) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity((new Gson()).toJson(new String[]{"An error happen while saving your data"})).type(MediaType.APPLICATION_JSON).build();
    }
}