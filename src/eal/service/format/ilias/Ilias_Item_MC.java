package eal.service.format.ilias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_MC;
import eal.service.format.eal.Item_SC;


public class Ilias_Item_MC extends Ilias_Item {

	private Item_MC item;

	
	public Ilias_Item_MC(Node xmlNode) throws XPathExpressionException {
		
		this.item = new Item_MC();
		parse(xmlNode);

		List<String> answerIds = new ArrayList<String>();
		NodeList xmlLabels = (NodeList) xpath.evaluate("./presentation/flow//response_label", xmlNode, XPathConstants.NODESET);
		for (int index = 0; index < xmlLabels.getLength(); index++) {
			Node xmlLabel = xmlLabels.item(index);

			String id = (String) this.xpath.evaluate("./@ident", xmlLabel, XPathConstants.STRING);
			String text = (String) this.xpath.evaluate("./material/mattext/text()", xmlLabel, XPathConstants.STRING);

			answerIds.add(id);
			this.item.answers.add(this.item.new Answer(text));
		}

		NodeList xmlResps = (NodeList) xpath.evaluate("./resprocessing/respcondition", xmlNode, XPathConstants.NODESET);
		for (int index = 0; index < xmlLabels.getLength(); index++) {
			Node xmlResp = xmlResps.item(index);

			String points = (String) this.xpath.evaluate("./setvar[@action=\"Add\"]/text()", xmlResp, XPathConstants.STRING);

			String id = (String) this.xpath.evaluate("./conditionvar/varequal/text()", xmlResp, XPathConstants.STRING);
			int idPos = answerIds.indexOf(id);
			if (idPos >= 0) {
				try {
					this.item.answers.get(idPos).points_pos = Integer.valueOf(points);
				} catch (NumberFormatException e) {
				}
			}
			
			id = (String) this.xpath.evaluate("./conditionvar/varequal/not/text()", xmlResp, XPathConstants.STRING);
			idPos = answerIds.indexOf(id);
			if (idPos >= 0) {
				try {
					this.item.answers.get(idPos).points_neg = Integer.valueOf(points);
				} catch (NumberFormatException e) {
				}
			}
		}

		
		String min = (String) this.xpath.evaluate("./presentation/flow/response_lid/render_choice/@minnumber", xmlNode, XPathConstants.STRING);
		String max = (String) this.xpath.evaluate("./presentation/flow/response_lid/render_choice/@maxnumber", xmlNode, XPathConstants.STRING);

		try {
			this.item.minnumber = Integer.valueOf(min);
		} catch (NumberFormatException e) {
			this.item.minnumber = 0;
		}
		
		try {
			this.item.maxnumber = Integer.valueOf(max);
		} catch (NumberFormatException e) {
			this.item.maxnumber = this.item.answers.size();
		}
	
		
	}



	

	@Override
	public Item getItem() {
		return this.item;
	}
}
