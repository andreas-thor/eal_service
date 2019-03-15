package eal.service.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import eal.service.format.eal.Item;
import eal.service.format.ilias.Ilias_Export;
import eal.service.format.ilias.Ilias_Import;
import eal.service.format.json.Json_Import_Export;
import eal.service.format.json.Json_Item_SC;

/**
 * Servlet implementation class Ilias
 */
@WebServlet("/Ilias")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class Ilias extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public Ilias() {
		super();
	}



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Part part = request.getPart("file");

		try {
			Item[] items = new Ilias_Import().parse(getFileName(part), part.getInputStream());

//			response.setCharacterEncoding("UTF-8");
//			new Json_Import_Export().create(items, response.getOutputStream());
			
			

//	        response.getOutputStream().write(baos.toByteArray());
//	        response.flushBuffer();
//	        baos.close();
	        
			String name = System.currentTimeMillis() + "__0__qpl_1";

			response.setHeader("Content-Disposition", "attachment; filename=\"" + name + ".zip\"");
	        response.setHeader("Content-Type", "application/zip");
			new Ilias_Export(name).create(items, response.getOutputStream());
			
//			response.getWriter().append(json.toString());
			
		} catch (XPathExpressionException | SAXException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// for (Map.Entry<ZipEntry, StringBuilder> entry : fileContent.entrySet()) {
		//
		// String s = String.format("Entry: %s len %d added %TD",
		// entry.getKey().getName(), entry.getKey().getSize(), new
		// Date(entry.getKey().getTime()));
		// response.getWriter().append("\n" + s);
		// response.getWriter().append("\n" + entry.getValue().length());
		// }
		//
		// response.getWriter().append("aServed xat:
		// ").append(request.getContextPath());

	}

	

	/**
	 * Utility method to get file name from HTTP header content-disposition
	 */
	private String getFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		System.out.println("content-disposition header= " + contentDisp);
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2, token.length() - 1);
			}
		}
		return "";
	}

}
