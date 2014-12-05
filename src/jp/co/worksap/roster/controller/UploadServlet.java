package jp.co.worksap.roster.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import jp.co.worksap.roster.rest.ImageServices;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;


@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
                 maxFileSize=1024*1024*10,      // 10MB
                 maxRequestSize=1024*1024*50)   // 50MB
public class UploadServlet extends HttpServlet {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
     * handles file upload
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html;charset=UTF-8");

    	String appPath = request.getServletContext().getRealPath("");
        String savePath = appPath + File.separator;

        // Create path components to save the file
//        final String path = request.getParameter("destination");
        final String folder = extractInventoryId(request.getPart("inventoryId"));
        final String path = savePath + "images\\" + folder + "\\";

        File fFolder = new File(path);
        if (!fFolder.exists()) {
        	fFolder.mkdirs();
        }

        final Part filePart = request.getPart("file");

        Pair<String, String> primaryAndLastImages = ImageServices.getPrimaryAndLastImage(appPath, Integer.parseInt(folder));
        final String fileName = String.valueOf((Integer.parseInt(primaryAndLastImages.getRight()) + 1));

        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();

        try {
            out = new FileOutputStream(new File(path + "\\" + fileName));
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        } catch (FileNotFoundException fne) {
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String extractInventoryId(Part part) {
    	String theString;
		try {
			theString = IOUtils.toString(part.getInputStream(), "UTF-8");
			return theString;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
        /*String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("id")) {
                return s.substring(s.indexOf("=") + 2, s.length()-1);
            }
        }
        return "";*/
    }
}