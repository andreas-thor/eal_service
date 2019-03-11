package eal.service.format.ilias;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eal.service.format.eal.Item;

public abstract class Ilias_Item  {

	public static String DESCRIPTION_QUESTION_SEPARATOR = "<!-- EAL --><hr/>";
	
	public XPath xpath;
	public String xmlid;
	public Document doc;
	
	public abstract Item getItem();
	
	public void parse(Element xmlNode) throws XPathExpressionException {
		
		this.xpath = XPathFactory.newInstance().newXPath();
		this.xmlid = (String) this.xpath.evaluate("./@ident", xmlNode, XPathConstants.STRING);
		this.getItem().title = (String) this.xpath.evaluate("./@title", xmlNode, XPathConstants.STRING);
		
		
		String descques = (String) this.xpath.evaluate("./presentation/flow/material/mattext/text()", xmlNode, XPathConstants.STRING);
		// TODO: Images!!!
		
		int splitPoint = descques.indexOf(Ilias_Item.DESCRIPTION_QUESTION_SEPARATOR);
		if (splitPoint == -1) {
			this.getItem().description = "";
			this.getItem().question = new String (descques);
		} else {
			this.getItem().description = descques.substring(0, splitPoint);
			this.getItem().question = descques.substring(splitPoint + Ilias_Item.DESCRIPTION_QUESTION_SEPARATOR.length());
		}
	}
	
	public Node toXML () throws ParserConfigurationException {
	
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		Element result = doc.createElement("item");
		
		result.setAttribute("ident", this.xmlid);
		
		
		
		
		return result;
	}
	


	
	
	
}
