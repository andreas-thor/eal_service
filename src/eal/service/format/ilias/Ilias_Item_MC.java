package eal.service.format.ilias;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_MC;


public class Ilias_Item_MC extends Ilias_Item {

	private Item_MC item;

	
	public Ilias_Item_MC(Element xmlNode) throws XPathExpressionException {
		
		this.item = new Item_MC();
		parse(xmlNode);

		List<String> answerIds = new ArrayList<String>();
		NodeList xmlLabels = (NodeList) xpath.evaluate("./presentation/flow//response_label", xmlNode, XPathConstants.NODESET);
		for (int index = 0; index < xmlLabels.getLength(); index++) {
			Node xmlLabel = xmlLabels.item(index);

			String id = (String) this.xpath.evaluate("./@ident", xmlLabel, XPathConstants.STRING);
			String text = (String) this.xpath.evaluate("./material/mattext/text()", xmlLabel, XPathConstants.STRING);

			answerIds.add(id);
			this.item.addAnswer(text);
		}

		NodeList xmlResps = (NodeList) xpath.evaluate("./resprocessing/respcondition", xmlNode, XPathConstants.NODESET);
		for (int index = 0; index < xmlLabels.getLength(); index++) {
			Node xmlResp = xmlResps.item(index);

			String points = (String) this.xpath.evaluate("./setvar[@action=\"Add\"]/text()", xmlResp, XPathConstants.STRING);
			
			String id = (String) this.xpath.evaluate("./conditionvar/varequal/text()", xmlResp, XPathConstants.STRING);
			this.item.setAnswerPoints(answerIds.indexOf(id), true, points);
			
			id = (String) this.xpath.evaluate("./conditionvar/varequal/not/text()", xmlResp, XPathConstants.STRING);
			this.item.setAnswerPoints(answerIds.indexOf(id), false, points);
		}

		
		String min = (String) this.xpath.evaluate("./presentation/flow/response_lid/render_choice/@minnumber", xmlNode, XPathConstants.STRING);
		String max = (String) this.xpath.evaluate("./presentation/flow/response_lid/render_choice/@maxnumber", xmlNode, XPathConstants.STRING);
		this.item.setMinNumber(min);
		this.item.setMaxNumber(max);
	
		
	}



	

	@Override
	public Item getItem() {
		return this.item;
	}
}
