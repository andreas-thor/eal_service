package eal.service.format.ilias;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eal.service.format.Exporter;
import eal.service.format.eal.Item;

public class Ilias_Export implements Exporter {

	private String name;
	
	public Ilias_Export(String name) {
		this.name = name;
	}
	
	@Override
	public void create(Item[] items, OutputStream out) throws Exception {

		
		/* for DEBUG only */
//		items = Arrays.copyOfRange(items, 0, 25);
//		items = Arrays.copyOfRange(items, 25, 26);
		
		String qplName = name + ".xml";
		String qtiName = name.replace("_qpl_", "_qti_") + ".xml";
		
		ZipOutputStream zip = new ZipOutputStream(out);
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document qtiDoc = builder.newDocument();
		qtiDoc.appendChild(qtiDoc.createElement("questestinterop"));
		
		int imgOffset = 23;
		for (int index = 0; index < items.length; index++) {
			Item item = items[index];
			qtiDoc.getDocumentElement().appendChild(Ilias_Item.create(item).toXML(qtiDoc, this.getQuestionIdent(index), imgOffset));
			
			for (int imgIndex=0; imgIndex<item.getNumberOfImages(); imgIndex++) {
				addFileToZip (zip, this.name + "/objects/il_0_mob_" + (imgIndex+imgOffset) + "/" + item.getImageFilename(imgIndex), new ByteArrayInputStream(item.getImageContent(imgIndex)/*.toString().getBytes()*/));
			}
			
			imgOffset += item.getNumberOfImages();
		}
		
		addFileToZip (zip, name + "/" + qplName, getStreamFromDocument(createQPLFile(items)));
		addFileToZip (zip, name + "/" + qtiName, getStreamFromDocument(qtiDoc));
		zip.close();
	}

	
	
	private void addFileToZip (ZipOutputStream zip, String name, InputStream is) throws IOException {
		
		ZipEntry qplZip = new ZipEntry(name);
		qplZip.setCreationTime(FileTime.fromMillis(java.lang.System.currentTimeMillis()));
		zip.putNextEntry(qplZip);

		byte[] readBuffer = new byte[2048];
		int amountRead;
		
		while ((amountRead = is.read(readBuffer)) > 0) {
			zip.write(readBuffer, 0, amountRead);
		}
		zip.closeEntry();
		
	}
	
	private InputStream getStreamFromDocument (Document doc) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError, UnsupportedEncodingException, IOException {

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    OutputStreamWriter osw = new OutputStreamWriter(outputStream, "UTF-8");
	    transformer.transform(new DOMSource(doc), new StreamResult(osw));
	    
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
	
	
	private String getQuestionIdent (int index) {
		return "il_0_qst_" + index;
	}
	
	private Document createQPLFile(Item[] items) throws ParserConfigurationException, SAXException, IOException {

		String xml = String.format("<ContentObject Type=\"Questionpool_Test\"><MetaData><General Structure=\"Hierarchical\"><Identifier Catalog=\"EAL\" Entry=\"%s\" /><Title Language=\"de\">Exported from EALService at %s</Title><Language Language=\"de\" /><Description Language=\"de\" /><Keyword Language=\"en\" /></General></MetaData></ContentObject>",
				this.name,
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
			);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document qplDoc = builder.parse(new InputSource(new StringReader(xml)));
		
		for (int index = 0; index < items.length; index++) {
			Element xml_PO = qplDoc.createElement("PageObject");
			Element xml_PC = qplDoc.createElement("PageContent");
			Integer ealid = items[index].ealid;
			if (ealid != null) {
				xml_PC.setAttribute("PCID", "EAL:" + ealid);
			}
			Element xml_QU = qplDoc.createElement("Question");
			xml_QU.setAttribute("QRef", this.getQuestionIdent(index));
			xml_PC.appendChild(xml_QU);
			xml_PO.appendChild(xml_PC);
			qplDoc.getDocumentElement().appendChild(xml_PO);
		}

		return qplDoc;
	}

	private Document createQTIFile(Item[] items) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document qtiDoc = builder.newDocument();
		qtiDoc.appendChild(qtiDoc.createElement("questestinterop"));
		
		int imgOffset = 0;
		for (int index = 0; index < items.length; index++) {
			qtiDoc.getDocumentElement().appendChild(Ilias_Item.create(items[index]).toXML(qtiDoc, this.getQuestionIdent(index), imgOffset));
			imgOffset += items[index].getNumberOfImages();
		}
		
		return qtiDoc;
		
	}
	
	
}
