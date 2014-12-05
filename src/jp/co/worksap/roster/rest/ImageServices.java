package jp.co.worksap.roster.rest;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import jp.co.worksap.roster.ejb.InventoryEJB;
import jp.co.worksap.roster.entity.Inventory;
import jp.co.worksap.roster.rest.modelview.ImageInfo;

import org.apache.commons.lang3.tuple.Pair;

@Path("images/")
@Stateless
public class ImageServices {
	@EJB
	InventoryEJB inventoryEJB;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String[] index(@Context HttpServletRequest request, @QueryParam("inventoryId") int inventoryId) {
		String appPath = request.getServletContext().getRealPath("");
		return ImageServices.getImages(appPath, inventoryId);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setPrimary(ImageInfo imageInfo) {
        inventoryEJB.updatePrimaryImage(imageInfo.getInventoryId(), imageInfo.getImageId());

		return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response destroy(@Context HttpServletRequest request, @QueryParam("inventoryId") int inventoryId, @QueryParam("imageId") int imageId) {
		String appPath = request.getServletContext().getRealPath("");

		Inventory inventory = inventoryEJB.findInventory(inventoryId);
		if (inventory.getPrimaryImageId() == imageId) {
			String[] images = ImageServices.getImages(appPath, inventoryId);

			boolean foundPrimaryReplacement = false;
			for (String image: images) {
				if (Integer.parseInt(image) != imageId) {
					inventoryEJB.updatePrimaryImage(inventoryId, Integer.parseInt(image));
					foundPrimaryReplacement = true;
					break;
				}
			}
			if (!foundPrimaryReplacement) {
				inventoryEJB.updatePrimaryImage(inventoryId, -1);
			}
		}

		String savePath = appPath + File.separator;
        final String path = savePath + "\\images\\" + inventoryId + "\\" + imageId;

        if ((new File(path)).delete()) {
        	return Response.status(Status.ACCEPTED).type(MediaType.APPLICATION_JSON).build();
        } else {
        	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).build();
        }
	}

	public static String[] getImages(String appPath, int inventoryId) {
        String savePath = appPath + File.separator;
        final String path = savePath + "\\images\\" + inventoryId + "\\";

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        List<String> res = new LinkedList<String>();

        if (listOfFiles != null) {
	        for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					res.add(listOfFiles[i].getName());
				}
			}
        }
        return res.toArray(new String[0]);
	}

	public static Pair<String, String> getPrimaryAndLastImage(String appPath, int inventoryId) {
		String[] images = getImages(appPath, inventoryId);

		if (images.length > 0) {
			int[] imagesInt = new int[images.length];
			for (int i = 0; i<images.length; i++) {
				imagesInt[i] = Integer.parseInt(images[i]);
			}
			Arrays.sort(imagesInt);
			return Pair.of(String.valueOf(imagesInt[0]), String.valueOf(imagesInt[imagesInt.length-1]));
		} else {
			return Pair.of("-1", "-1");
		}
	}
}
