package jp.co.worksap.roster.rest.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.ws.WebServiceException;

import com.google.gson.Gson;

@Provider
public class WebServiceExceptionMapper implements
        ExceptionMapper<WebServiceException> {

    @Override
    public Response toResponse(WebServiceException ex) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(new String[]{ex.getMessage()})).type(MediaType.APPLICATION_JSON).build();
    }
}