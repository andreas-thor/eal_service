package eal.service.format.ilias;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_FT;
import eal.service.format.eal.Item_MC;
import eal.service.format.eal.Item_SC;

public abstract class Ilias_Item {

	public static String DESCRIPTION_QUESTION_SEPARATOR = "<!-- EAL --><hr/>";

	public XPath xpath;
	public String xmlIdent;
	public Document doc;

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

	public void parse(Element xmlNode) throws XPathExpressionException {

		this.xpath = XPathFactory.newInstance().newXPath();
		this.xmlIdent = (String) this.xpath.evaluate("./@ident", xmlNode, XPathConstants.STRING);
		this.getItem().title = (String) this.xpath.evaluate("./@title", xmlNode, XPathConstants.STRING);

		String descques = (String) this.xpath.evaluate("./presentation/flow/material/mattext/text()", xmlNode, XPathConstants.STRING);
		// TODO: Images!!!

		int splitPoint = descques.indexOf(Ilias_Item.DESCRIPTION_QUESTION_SEPARATOR);
		if (splitPoint == -1) {
			this.getItem().description = "";
			this.getItem().question = new String(descques);
		} else {
			this.getItem().description = descques.substring(0, splitPoint);
			this.getItem().question = descques.substring(splitPoint + Ilias_Item.DESCRIPTION_QUESTION_SEPARATOR.length());
		}
	}

	public Element toXML(Document doc, String xmlIdent) throws ParserConfigurationException {

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
		flow.appendChild (createMaterialElement(doc, "text/html", getItem().description + DESCRIPTION_QUESTION_SEPARATOR + this.getItem().question));
		flow.appendChild (createResponseElement(doc, xmlIdent));
		presentation.appendChild (flow);
		result.appendChild (presentation);
		
		result.appendChild (createResprocessingElement(doc, xmlIdent));
		
		return result;
	}

	public abstract Element createResponseElement (Document doc, String xmlIdent);
	
	public abstract Element createResprocessingElement (Document doc, String xmlIdent);

	
	protected Element createMaterialElement (Document doc, String type, String value) {
		
		/* TODO Image processing */
		Element mattext = doc.createElement("mattext");
		mattext.setTextContent(value);
		mattext.setAttribute("texttype", type);

		Element material = doc.createElement("material");
		material.appendChild(mattext);
		
		return material;		
		
	}
	
	
}
