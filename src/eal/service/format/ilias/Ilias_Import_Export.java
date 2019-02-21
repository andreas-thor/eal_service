package eal.service.format.ilias;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eal.service.format.eal.Item;

public class Ilias_Import_Export {

	private String name;
	private Map<String, StringBuilder> content;
	private DocumentBuilder builder;
	private XPath xpath;

	
	public static List<Item> parse (String filename, Map<String, StringBuilder> content) throws IOException, ParserConfigurationException, XPathExpressionException, SAXException {
		
		Ilias_Import_Export imp = new Ilias_Import_Export(filename, content);
		
		Map<String, Integer> mapXML2EAL = imp.getMappingXMLID2EALID();


		List<Item> res = new ArrayList<Item>();
		for (Ilias_Item ilias_item: imp.parseQTI(imp.getQTIDocument()) ) {
			ilias_item.getItem().ealid = mapXML2EAL.get(ilias_item.xmlid);
			res.add(ilias_item.getItem());
		}		
		
		return res;
	}
	
	
	private Ilias_Import_Export(String filename, Map<String, StringBuilder> content) throws IOException, ParserConfigurationException {

		if (!filename.substring(filename.length() - 4).equalsIgnoreCase(".zip")) {
			throw new IOException("File " + filename + " must have extension .zip!");
		}

		this.name = filename.substring(0, filename.length() - 4);
		this.content = content;
		this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver(){ 
		    public InputSource resolveEntity(String publicId, String systemId){ 
		        return new InputSource(new ByteArrayInputStream(new byte[0])); 
		    } 
		}); 
		
		this.xpath = XPathFactory.newInstance().newXPath();
	}

	private Map<String, Integer> getMappingXMLID2EALID() throws SAXException, IOException, XPathExpressionException {

		Map<String, Integer> result = new HashMap<String, Integer>();

		Document mainFileDoc = builder.parse(new ByteArrayInputStream(this.content.get(this.name + "/" + this.name + ".xml").toString().getBytes(StandardCharsets.UTF_8)));
		NodeList questions = (NodeList) xpath.evaluate("//PageObject/PageContent/Question", mainFileDoc, XPathConstants.NODESET);

		for (int index = 0; index < questions.getLength(); index++) {
			String qref = (String) xpath.evaluate("./@QRef", questions.item(index), XPathConstants.STRING); 
			String pcId = (String) xpath.evaluate("../@PCID", questions.item(index), XPathConstants.STRING);
			if (pcId.startsWith("EAL:")) {
				try {
					result.put(qref, Integer.valueOf(pcId.substring(4)));
				} catch (NumberFormatException e) {
				}
			}
		}

		return result;

	}

	private Document getQTIDocument() throws SAXException, IOException {

		for (String type : new String[] { "_qpl_", "_tst_" }) {
			if (this.name.contains(type)) {
				String nameQTIFile = this.name + "/" + this.name.replaceAll(type, "_qti_") + ".xml";
				return builder.parse(new ByteArrayInputStream(this.content.get(nameQTIFile).toString().getBytes(StandardCharsets.UTF_8)));
			}
		}
		throw new IOException("Could not find qti XML file. File " + this.name + " is neither qpl nor tst!");
	}

	
	private List<Ilias_Item> parseQTI(Document QTI) throws XPathExpressionException {

		List<Ilias_Item> result = new ArrayList<Ilias_Item>();

		NodeList items = (NodeList) xpath.evaluate("//item", QTI, XPathConstants.NODESET);
		for (int index = 0; index < items.getLength(); index++) {

			switch ((String) xpath.evaluate(".//qtimetadatafield[./fieldlabel='QUESTIONTYPE']/fieldentry", items.item(index), XPathConstants.STRING)) {

			case "SINGLE CHOICE QUESTION":
				result.add(new Ilias_Item_SC(items.item(index)));
				break;
			case "MULTIPLE CHOICE QUESTION":
				result.add(new Ilias_Item_MC(items.item(index)));
				break;
			case "TEXT QUESTION":
				result.add(new Ilias_Item_FT(items.item(index)));
				break;
			default:
				continue;
			}
		}

		return result;

	}

	public void parse() throws SAXException, IOException, XPathExpressionException {


	}

}
