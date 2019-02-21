package eal.service.format.ilias;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_SC;

public class Ilias_Item_SC extends Ilias_Item {

	
	private Item_SC item;
	
	
	public Ilias_Item_SC (Node xmlNode) throws XPathExpressionException {
		
		this.item = new Item_SC();
		List<String> answerIds = new ArrayList<String>();
		super.parse(xmlNode);
		
		NodeList xmlLabels = (NodeList) xpath.evaluate("./presentation/flow//response_label", xmlNode, XPathConstants.NODESET);
		for (int index=0; index<xmlLabels.getLength(); index++) {
			Node xmlLabel = xmlLabels.item(index);
			String id = (String) this.xpath.evaluate("./@ident", xmlLabel, XPathConstants.STRING);
			String text = (String) this.xpath.evaluate("./material/mattext/text()", xmlLabel, XPathConstants.STRING);
			
			answerIds.add(id);
			this.item.answers.add(this.item.new Answer(text));
		} 
		
		NodeList xmlResps = (NodeList) xpath.evaluate("./resprocessing/respcondition", xmlNode, XPathConstants.NODESET);
		for (int index=0; index<xmlLabels.getLength(); index++) {
			Node xmlResp = xmlResps.item(index);
			String id = (String) this.xpath.evaluate("./conditionvar/varequal/text()", xmlResp, XPathConstants.STRING);
			String points = (String) this.xpath.evaluate("./setvar[@action=\"Add\"]/text()", xmlResp, XPathConstants.STRING);
			try {
				this.item.answers.get(answerIds.indexOf(id)).points = Integer.valueOf(points);
			} catch (NumberFormatException e) {
			}
		}
		
	}

	






	@Override
	public Item getItem() {
		return this.item;
	}
}
