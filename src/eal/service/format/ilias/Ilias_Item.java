package eal.service.format.ilias;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item.Image;
import eal.service.format.eal.Item_FT;
import eal.service.format.eal.Item_MC;
import eal.service.format.eal.Item_SC;
import sun.misc.BASE64Decoder;

public abstract class Ilias_Item {

	public static String DESCRIPTION_QUESTION_SEPARATOR = "<!-- EAL --><hr/>";

	public XPath xpath;
	public String xmlIdent;

	
	
	
	
	
	public abstract Item getItem();

	public abstract String getType();

	public static Ilias_Item create(Item item) {

		if (item instanceof Item_SC) {
			return new Ilias_Item_SC((Item_SC) item);
		}
		if (item instanceof Item_MC) {
			return new Ilias_Item_MC((Item_MC) item);
		}
		if (item instanceof Item_FT) {
			return new Ilias_Item_FT((Item_FT) item);
		}
		return null;
	}

	
	
	public void parse(Element xmlNode, String name, Map<String, byte[]> content) throws XPathExpressionException {

		this.xpath = XPathFactory.newInstance().newXPath();
		this.xmlIdent = (String) this.xpath.evaluate("./@ident", xmlNode, XPathConstants.STRING);
		this.getItem().title = (String) this.xpath.evaluate("./@title", xmlNode, XPathConstants.STRING);

		String descques = (String) this.xpath.evaluate("./presentation/flow/material/mattext/text()", xmlNode, XPathConstants.STRING); 

		List<String> imageLabels = new ArrayList<String>();
		
		// TODO: Images!!!
		NodeList matimage = (NodeList) this.xpath.evaluate("./presentation/flow/material/matimage", xmlNode, XPathConstants.NODESET);
		for (int index=0; index<matimage.getLength(); index++) {
			Element img = (Element) matimage.item(index);
			String uri = img.getAttribute("uri");
			String[] split = uri.split("/");
			String filename = split[split.length-1];
			getItem().addImage(filename, content.get(name + "/" + uri));
			imageLabels.add (img.getAttribute("label"));
		}
		
		Pattern p = Pattern.compile("(<img[^>]+)src=[\"\\']([^\"]*)[\"\\']");
		Matcher m = p.matcher(descques);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {

			// if img is stored inline (src="data:image/png;base64,iVBOR....")  
			if (m.group(2).substring(0, 5).equalsIgnoreCase("data:")) {
				// TODO extract file; modify
				
				String filename = UUID.randomUUID().toString(); 
				String[] parts = m.group(2).split(",", 2);
				byte[] data = null;
				try {
					data = new BASE64Decoder().decodeBuffer(parts[1]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				byte[] data = Base64.getMimeDecoder().decode(parts[1]);  
				getItem().addImage(filename, data);
				m.appendReplacement(sb, m.group(1) + "src='[" + (getItem().getNumberOfImages()-1) + "]'");
				continue;
			}
			
			int index = imageLabels.indexOf(m.group(2));
			if (index > -1) {
				m.appendReplacement(sb, m.group(1) + "src='[" + index + "]'");
				continue;
			}
			
			// do not change anything
			m.appendReplacement(sb, m.group(1) + "src='" + m.group(2) + "'");
		}
		m.appendTail(sb);
		descques = sb.toString();
		
		int splitPoint = descques.indexOf(Ilias_Item.DESCRIPTION_QUESTION_SEPARATOR);
		if (splitPoint == -1) {
			this.getItem().description = "";
			this.getItem().question = new String(descques);
		} else {
			this.getItem().description = descques.substring(0, splitPoint);
			this.getItem().question = descques.substring(splitPoint + Ilias_Item.DESCRIPTION_QUESTION_SEPARATOR.length());
		}
	}

	public Element toXML(Document doc, String xmlIdent, int imgOffset) throws ParserConfigurationException {

		Element result = doc.createElement("item");
		result.setAttribute("ident", xmlIdent);
		result.setAttribute("title", getItem().title);
		result.setAttribute("maxattempts", "1");

		if (getItem().ealid != null) {
			Element c = doc.createElement("qticomment");
			c.setTextContent("[EALID:" + getItem().ealid.toString() + "]");
			result.appendChild(c);
		}

		Element d = doc.createElement("duration");
		d.setTextContent("P0Y0M0DT0H1M0S");
		result.appendChild(d);

		Element meta = doc.createElement("qtimetadata");
		Map<String, String> metaData = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("ILIAS_VERSION", "5.0.8 2015-11-24");
				put("QUESTIONTYPE", getType());
				put("AUTHOR", "author");
				put("additional_cont_edit_mode", "default");
				put("externalId", xmlIdent);
				if (getItem().ealid != null) {
					put("ealid", String.valueOf(getItem().ealid));
				}
				put("thumb_size", "");
				put("feedback_setting", "1");
			}
		};

		metaData.forEach((key, value) -> {
			Element l = doc.createElement("fieldlabel");
			l.setTextContent(key);
			Element e = doc.createElement("fieldentry");
			e.setTextContent(value);

			Element x = doc.createElement("qtimetadatafield");
			x.appendChild(l);
			x.appendChild(e);
			meta.appendChild(x);
		});

		Element itemmeta = doc.createElement("itemmetadata");
		itemmeta.appendChild(meta);
		result.appendChild(itemmeta);

		Element presentation = doc.createElement("presentation");
		presentation.setAttribute("label", getItem().title);
		Element flow = doc.createElement("flow");
		flow.appendChild(createMaterialElement(doc, "text/html", getItem().description + DESCRIPTION_QUESTION_SEPARATOR + this.getItem().question, imgOffset));
		flow.appendChild(createResponseElement(doc, xmlIdent));
		presentation.appendChild(flow);
		result.appendChild(presentation);

		result.appendChild(createResprocessingElement(doc, xmlIdent));

		return result;
	}

	public abstract Element createResponseElement(Document doc, String xmlIdent);

	public abstract Element createResprocessingElement(Document doc, String xmlIdent);

	protected Element createMaterialElement(Document doc, String type, String value, int imgOffset) {

		Pattern p = Pattern.compile("(<img[^>]+)src=[\"\\']\\[([\\d]+)\\][\"\\']");
		Matcher m = p.matcher(value);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, m.group(1) + "src=\"il_0_mob_" + (Integer.valueOf(m.group(2)).intValue()+imgOffset) + "\"");
		}
		m.appendTail(sb);
		value = sb.toString();		
		
		Element mattext = doc.createElement("mattext");
		mattext.setTextContent(value);
		mattext.setAttribute("texttype", type);

		Element material = doc.createElement("material");
		material.appendChild(mattext);

		/* TODO Image processing ... passt? */
		for (int index=0; index<getItem().getNumberOfImages(); index++) {
			Element matimage = doc.createElement("matimage");
			String label = "il_0_mob_" + (index+imgOffset);
			matimage.setAttribute("label", label);
			matimage.setAttribute("uri", "objects/" + label + "/" + getItem().getImageFilename(index));
			material.appendChild(matimage);
		}
		
		return material;

	}

}
