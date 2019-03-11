package eal.service.format.ilias;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_FT;

public class Ilias_Item_FT extends Ilias_Item {

	private Item_FT item;


	public Ilias_Item_FT(Element xmlNode) throws XPathExpressionException {
		
		this.item = new Item_FT();
		parse(xmlNode);

		
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

}
