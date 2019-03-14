package eal.service.format.ilias;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_MC;
import eal.service.format.eal.Item_SC;

public class Ilias_Item_SC extends Ilias_Item_MC {

	private Item_SC item;
	public final static String type = "SINGLE CHOICE QUESTION";

	
	public Ilias_Item_SC() {
		this(new Item_SC());
	}
	
	public Ilias_Item_SC(Item_SC item) {
		super (null);
		this.item = item;
	}

//	public Ilias_Item_SC(Element xmlNode) throws XPathExpressionException {
//
//		this.item = new Item_SC();
//		List<String> answerIds = new ArrayList<String>();
//		super.parse(xmlNode);
//
//		NodeList xmlLabels = (NodeList) xpath.evaluate("./presentation/flow//response_label", xmlNode, XPathConstants.NODESET);
//		for (int index = 0; index < xmlLabels.getLength(); index++) {
//			Node xmlLabel = xmlLabels.item(index);
//			String id = (String) this.xpath.evaluate("./@ident", xmlLabel, XPathConstants.STRING);
//			String text = (String) this.xpath.evaluate("./material/mattext/text()", xmlLabel, XPathConstants.STRING);
//			answerIds.add(id);
//			this.item.addAnswer(text);
//		}
//
//		NodeList xmlResps = (NodeList) xpath.evaluate("./resprocessing/respcondition", xmlNode, XPathConstants.NODESET);
//		for (int index = 0; index < xmlLabels.getLength(); index++) {
//			Node xmlResp = xmlResps.item(index);
//			String id = (String) this.xpath.evaluate("./conditionvar/varequal/text()", xmlResp, XPathConstants.STRING);
//			String points = (String) this.xpath.evaluate("./setvar[@action=\"Add\"]/text()", xmlResp, XPathConstants.STRING);
//			this.item.setAnswerPoints(answerIds.indexOf(id), points);
//		}
//
//	}

	@Override
	public Element toXML(Document doc, String xmlIdent) throws ParserConfigurationException {

		Element result = super.toXML(doc, xmlIdent);

		return result;
	}


	@Override
	public Item getItem() {
		return this.item;
	}

	@Override
	public Item_MC getItem_MC() {
		return this.item;
	}
	
	@Override
	public String getType() {
		return Ilias_Item_SC.type;
	}

	@Override
	public String getCardinality () {
		return "Single";
	}

}
