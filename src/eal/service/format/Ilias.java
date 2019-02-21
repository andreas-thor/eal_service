package eal.service.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import eal.service.format.ilias.Ilias_Import_Export;
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
			List<Item> items = Ilias_Import_Export.parse(getFileName(part), getZipContent(part.getInputStream()));
			JSONObject json = Json_Import_Export.create(items);
			
			response.getWriter().append(json.toString());
			
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

	private Map<String, StringBuilder> getZipContent(InputStream in) throws IOException {

		Map<String, StringBuilder> result = new HashMap<String, StringBuilder>();

		// create a buffer to improve copy performance later.
		byte[] buffer = new byte[2048];

		// open the zip file stream
		ZipInputStream stream = new ZipInputStream(in);
		// now iterate through each item in the stream. The get next entry call will
		// return a ZipEntry for each file in the stream
		ZipEntry entry;
		while ((entry = stream.getNextEntry()) != null) {
			// Once we get the entry from the stream, the stream is positioned read to read
			// the raw data, and we keep reading until read returns 0 or less.
			StringBuilder sb = new StringBuilder();
			int len = 0;
			while ((len = stream.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, len));
			}

			result.put(entry.getName(), sb);
		}
		stream.close();
		return result;
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
