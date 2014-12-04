package jp.co.worksap.roster.rest;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("images/")
public class ImageServices {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String[] index(@Context HttpServletRequest request, @QueryParam("inventoryId") int inventoryId) {
		String appPath = request.getServletContext().getRealPath("");
        String savePath = appPath + File.separator;
        final String path = savePath + "//WEB-INF//images//";

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        List<String> res = new LinkedList<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
				res.add(listOfFiles[i].getName());
			}
		}
        return res.toArray(new String[0]);
	}
}
