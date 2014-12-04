package jp.co.worksap.roster.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.co.worksap.roster.entity.TimeZone;

@Path("/timezones")
public class TimeZoneService {

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public String[] index() {
		return TimeZone.getTimeZones();
	}
}
