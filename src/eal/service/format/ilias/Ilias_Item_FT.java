package eal.service.format.ilias;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_FT;

public class Ilias_Item_FT extends Ilias_Item {

	private Item_FT item;
	public final static String type = "TEXT QUESTION";


	public Ilias_Item_FT () {
		this (new Item_FT());
	}
	
	public Ilias_Item_FT (Item_FT item) {
		this.item = item;
	}

	@Override
	public void parse(Element xmlNode) throws XPathExpressionException {
		
		super.parse(xmlNode);

		String points = (String) this.xpath.evaluate("./resprocessing/outcomes/decvar/@maxvalue", xmlNode, XPathConstants.STRING);
		try {
			this.item.points = Integer.valueOf(points);
		} catch (NumberFormatException e) {
			this.item.points = 0;
		}
	}


	@Override
	public Item getItem() {
		return this.item;
	}
	
	@Override
	public String getType() {
		return Ilias_Item_FT.type;
	}

	@Override
	public Element createResprocessingElement (Document doc, String xmlIdent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Element createResponseElement(Document doc, String xmlIdent) {
		// TODO Auto-generated method stub
		return null;
	}

}
