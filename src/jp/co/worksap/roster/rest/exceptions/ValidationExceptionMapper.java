package jp.co.worksap.roster.rest.exceptions;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;

@Provider
public class ValidationExceptionMapper implements
        ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException ex) {
		List<String> res = new LinkedList<String>();
		for (ConstraintViolation<?> error : ex.getConstraintViolations()) {
			if (error.getPropertyPath() != null) {
				res.add(error.getPropertyPath().toString() + ": " + error.getMessage());
			} else {
				res.add(error.getMessage());
			}
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity((new Gson()).toJson(res)).type(MediaType.APPLICATION_JSON).build();
    }
}