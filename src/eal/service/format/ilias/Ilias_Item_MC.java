package eal.service.format.ilias;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eal.service.format.eal.Item;
import eal.service.format.eal.Item_MC;


public class Ilias_Item_MC extends Ilias_Item {

	private Item_MC item;
	public final static String type = "MULTIPLE CHOICE QUESTION";


	public Ilias_Item_MC () {
		this (new Item_MC());
	}
	
	public Ilias_Item_MC (Item_MC item) {
		this.item = item;
	}

	@Override
	public void parse(Element xmlNode) throws XPathExpressionException {

		super.parse(xmlNode);

		List<String> answerIds = new ArrayList<String>();
		NodeList xmlLabels = (NodeList) xpath.evaluate("./presentation/flow//response_label", xmlNode, XPathConstants.NODESET);
		for (int index = 0; index < xmlLabels.getLength(); index++) {
			Node xmlLabel = xmlLabels.item(index);
			String id = (String) this.xpath.evaluate("./@ident", xmlLabel, XPathConstants.STRING);
			String text = (String) this.xpath.evaluate("./material/mattext/text()", xmlLabel, XPathConstants.STRING);
			answerIds.add(id);
			getItem_MC().addAnswer(text);
		}

		NodeList xmlResps = (NodeList) xpath.evaluate("./resprocessing/respcondition", xmlNode, XPathConstants.NODESET);
		for (int index = 0; index < xmlLabels.getLength(); index++) {
			Node xmlResp = xmlResps.item(index);

			String points = (String) this.xpath.evaluate("./setvar[@action=\"Add\"]/text()", xmlResp, XPathConstants.STRING);
			
			String id = (String) this.xpath.evaluate("./conditionvar/varequal/text()", xmlResp, XPathConstants.STRING);
			getItem_MC().setAnswerPoints(answerIds.indexOf(id), true, points);
			
			id = (String) this.xpath.evaluate("./conditionvar/varequal/not/text()", xmlResp, XPathConstants.STRING);
			getItem_MC().setAnswerPoints(answerIds.indexOf(id), false, points);
		}

		
		String min = (String) this.xpath.evaluate("./presentation/flow/response_lid/render_choice/@minnumber", xmlNode, XPathConstants.STRING);
		String max = (String) this.xpath.evaluate("./presentation/flow/response_lid/render_choice/@maxnumber", xmlNode, XPathConstants.STRING);
		getItem_MC().setMinNumber(min);
		getItem_MC().setMaxNumber(max);
	
	}
	
	
	@Override
	public Element createResprocessingElement (Document doc, String xmlIdent) {
		
		Element resprocessing = doc.createElement("resprocessing");
		
		Element outcomes = doc.createElement("outcomes");
		Element decvar = doc.createElement("decvar");
		outcomes.appendChild (decvar);
		resprocessing.appendChild(outcomes);
		
		for (int index=0; index<getItem_MC().getNumberOfAnswers(); index++) {
		
			for (boolean checked: new boolean[]{true, false}) {
				
				Element respcondition = doc.createElement("respcondition");
				respcondition.setAttribute ("continue", "Yes");
				
				Element conditionvar = doc.createElement("conditionvar");
				Element not = doc.createElement("not");
				Element varequal = doc.createElement("varequal");
				varequal.setTextContent(String.valueOf(index));
				varequal.setAttribute ("respident", xmlIdent);
				
				if (checked) {
					conditionvar.appendChild (varequal);
				} else {
					not.appendChild (varequal);
					conditionvar.appendChild (not);
				}
				respcondition.appendChild (conditionvar);
				
				Element setvar = doc.createElement("setvar");
				setvar.setTextContent( String.valueOf (this.getItem_MC().getAnswerPoints(index, checked)));
				setvar.setAttribute("action", "Add");
				
				respcondition.appendChild (setvar);
				resprocessing.appendChild (respcondition);
				
			}
			
		}
		
		return resprocessing;	
	}


	@Override
	public Element createResponseElement(Document doc, String xmlIdent) {
		
		Element render_choice = doc.createElement("render_choice");
		render_choice.setAttribute("shuffle", "Yes");
		render_choice.setAttribute("minnumber", String.valueOf(getItem_MC().getMinNumber()));
		render_choice.setAttribute("maxnumber", String.valueOf(getItem_MC().getMaxNumber()));
		
		for (int index=0; index<getItem_MC().getNumberOfAnswers(); index++) {
			
			Element response_label = doc.createElement("response_label");
			response_label.setAttribute("ident", String.valueOf(index));
			response_label.appendChild (createMaterialElement(doc, "text/html", getItem_MC().getAnswerText(index)));
			render_choice.appendChild (response_label);
		}

		Element response_lid = doc.createElement("response_lid");
		response_lid.setAttribute("ident", xmlIdent);
		response_lid.setAttribute("rcardinality", getCardinality());
		response_lid.appendChild (render_choice);
		return response_lid;		
	
		
	}


	

	@Override
	public Item getItem() {
		return this.item;
	}
	
	public Item_MC getItem_MC() {
		return this.item;
	}
	
	@Override
	public String getType() {
		return Ilias_Item_MC.type;
	}
	
	public String getCardinality () {
		return "Multiple";
	}
	
	
}
