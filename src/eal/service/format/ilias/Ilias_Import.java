package eal.service.format.ilias;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eal.service.format.Importer;
import eal.service.format.eal.Item;

public class Ilias_Import implements Importer {

	private String name;
	private Map<String, StringBuilder> content;
	private DocumentBuilder builder;
	private XPath xpath;

	/**
	 * 
	 * @param filename
	 *            name of upload file (must be zip file)
	 * @param in
	 *            stream of file content (must be zip file)
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public Ilias_Import() throws ParserConfigurationException  {

		this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		this.builder.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) {
				return new InputSource(new ByteArrayInputStream(new byte[0]));
			}
		});
		this.xpath = XPathFactory.newInstance().newXPath();
	}

	@Override
	public Item[] parse(String filename, InputStream in) throws IOException, XPathExpressionException, SAXException {
		
		if (!filename.substring(filename.length() - 4).equalsIgnoreCase(".zip")) {
			throw new IOException("File " + filename + " must have extension .zip!");
		}

		this.name = filename.substring(0, filename.length() - 4);
		this.content = getZipContent(in);
		
		Map<String, Integer> mapXML2EAL = getMappingXMLID2EALID();
		return parseQTI().map(it -> {
			it.getItem().ealid = mapXML2EAL.get(it.xmlid);
			return it.getItem();
		}).toArray(Item[]::new);
		
	}
	
	


	private Stream<Ilias_Item> parseQTI() throws XPathExpressionException, SAXException, IOException {

		Document QTI = getQTIDocument();
		NodeList items = (NodeList) xpath.evaluate("//item", QTI, XPathConstants.NODESET);
		return IntStream.range(0, items.getLength()).mapToObj(index -> this.parseItem((Element) items.item(index))).filter(Objects::nonNull);
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

	private Document createQPLFile(Stream<Item> items) throws ParserConfigurationException, SAXException, IOException {

		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
			
		Document qplDoc = builder.parse(getClass().getResourceAsStream("Ilias_QPL.xml"));
		AtomicInteger index = new AtomicInteger(0);
		items.forEach(item -> {
			Element xml_PO = qplDoc.createElement("PageObject");
			Element xml_PC = qplDoc.createElement("PageContent");
			xml_PC.setAttribute("PCID", "EAL:" + item.ealid);
			Element xml_QU = qplDoc.createElement("Question");
			xml_QU.setAttribute("QRef", String.valueOf(index.getAndIncrement()));
			xml_PC.appendChild(xml_QU);
			xml_PO.appendChild (xml_PC);
			qplDoc.getDocumentElement().appendChild(xml_PO);
		});
		
		
		return qplDoc;
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

	private Ilias_Item parseItem(Element xmlItem) {

		try {
			String type = (String) xpath.evaluate(".//qtimetadatafield[./fieldlabel='QUESTIONTYPE']/fieldentry", xmlItem, XPathConstants.STRING);

			switch (type) {
			case "SINGLE CHOICE QUESTION":
				return new Ilias_Item_SC(xmlItem);
			case "MULTIPLE CHOICE QUESTION":
				return new Ilias_Item_MC(xmlItem);
			case "TEXT QUESTION":
				return new Ilias_Item_FT(xmlItem);
			default:
				return null;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	private static Map<String, StringBuilder> getZipContent(InputStream in) throws IOException {

		Map<String, StringBuilder> result = new HashMap<String, StringBuilder>();

		// create a buffer to improve copy performance later.
		byte[] buffer = new byte[2048];

		// open the zip file stream
		ZipInputStream stream = new ZipInputStream(in);
		// now iterate through each item in the stream. The get next entry call will
		// return a ZipEntry for each file in the stream
		ZipEntry entry;
		while ((entry = stream.getNextEntry()) != null) {
			// Once we get the entry from the stream, the stream is positioned read to read
			// the raw data, and we keep reading until read returns 0 or less.
			StringBuilder sb = new StringBuilder();
			int len = 0;
			while ((len = stream.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, len));
			}

			result.put(entry.getName(), sb);
		}
		stream.close();
		return result;
	}


	
}
