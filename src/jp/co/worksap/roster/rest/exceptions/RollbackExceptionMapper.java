package jp.co.worksap.roster.rest.exceptions;

import java.sql.SQLException;

import javax.transaction.RollbackException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.exceptions.DatabaseException;

import com.google.gson.Gson;

@Provider
public class RollbackExceptionMapper implements
        ExceptionMapper<RollbackException> {

    @Override
    public Response toResponse(RollbackException ex) {
    	if (ex.getCause() != null && ex.getCause() instanceof DatabaseException && ex.getCause().getCause() != null && ex.getCause().getCause() instanceof SQLException) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity((new Gson()).toJson(new String[]{"The id is already used"})).type(MediaType.APPLICATION_JSON).build();
    	}

    	if (ex.getCause() != null && ex.getCause() instanceof ConstraintViolationException) {
    		return (new ValidationExceptionMapper()).toResponse((ConstraintViolationException)ex.getCause());
    	}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity((new Gson()).toJson(new String[]{"An error happen while saving your data"})).type(MediaType.APPLICATION_JSON).build();
    }
}