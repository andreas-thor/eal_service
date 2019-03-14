package eal.service.format.ilias;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import eal.service.format.Exporter;
import eal.service.format.eal.Item;

public class Ilias_Export implements Exporter {

	@Override
	public void create(Item[] items, OutputStream out) throws Exception {
		// TODO Auto-generated method stub

		ZipOutputStream zip = new ZipOutputStream(out);

		addFileToZip (zip, "qpl.xml", getStreamFromDocument(createQPLFile(items)));
		addFileToZip (zip, "qti.xml", getStreamFromDocument(createQTIFile(items)));

		zip.close();
	}

	
	
	private void addFileToZip (ZipOutputStream zip, String name, InputStream is) throws IOException {
		
		ZipEntry qplZip = new ZipEntry(name);
		qplZip.setCreationTime(FileTime.fromMillis(java.lang.System.currentTimeMillis()));
		zip.putNextEntry(qplZip);

		byte[] readBuffer = new byte[2048];
		int amountRead;
		int written = 0;
		
		while ((amountRead = is.read(readBuffer)) > 0) {
			zip.write(readBuffer, 0, amountRead);
			written += amountRead;
		}
		zip.closeEntry();
		
	}
	
	private InputStream getStreamFromDocument (Document qpl) throws TransformerConfigurationException, TransformerException, TransformerFactoryConfigurationError {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(qpl);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
	
	
	
	private Document createQPLFile(Item[] items) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document qplDoc = builder.parse(getClass().getResourceAsStream("Ilias_QPL.xml"));
		
		for (int index = 0; index < items.length; index++) {
			Element xml_PO = qplDoc.createElement("PageObject");
			Element xml_PC = qplDoc.createElement("PageContent");
			Integer ealid = items[index].ealid;
			if (ealid != null) {
				xml_PC.setAttribute("PCID", "EAL:" + ealid);
			}
			Element xml_QU = qplDoc.createElement("Question");
			xml_QU.setAttribute("QRef", String.valueOf(index));
			xml_PC.appendChild(xml_QU);
			xml_PO.appendChild(xml_PC);
			qplDoc.getDocumentElement().appendChild(xml_PO);
		}

		return qplDoc;
	}

	private Document createQTIFile(Item[] items) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document qtiDoc = builder.parse(getClass().getResourceAsStream("Ilias_QTI.xml"));
		
		for (int index = 0; index < items.length; index++) {
			qtiDoc.getDocumentElement().appendChild(Ilias_Item.create(items[index]).toXML(qtiDoc, String.valueOf(index)));
		}
		
		return qtiDoc;
		
	}
	
	
}
