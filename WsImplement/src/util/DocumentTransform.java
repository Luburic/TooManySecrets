package util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

public class DocumentTransform {
	
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	
	public static DocumentBuilder getDocumentBuilder() {
		try {
			// Setup document builder
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			// validacija XML scheme
			docBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			return builder;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
    
	public static Document convertToDocument(DOMSource request) {
	    Document r = null;
	    try{
	    	DocumentBuilder db = getDocumentBuilder();
	    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "no");
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        StreamResult result = new StreamResult(baos);
	        transformer.transform(request, result);
	        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	        r = db.parse(bais);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return r;
	}
	
	
	
	public static void printDocument(Document document) {
		
		try {
			
			//Kreira se TransformerFactory
			TransformerFactory tFactory = TransformerFactory.newInstance();
			//Kreiramo transformer
			Transformer transformer = tFactory.newTransformer();
			//uvlacenje
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			//Posto je ulaz za transformaciju DOM kreiramo DOMSource
			DOMSource source = new DOMSource(document);
			//Izlaz je std.out, odnosno stream
			StreamResult result = new StreamResult(System.out);
			//Vrsi se transformacija
			transformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static Document createNotificationResponse(String notification,String TARGET_NAMESPACE) {
		
		DocumentBuilder documentBuilder = DocumentTransform.getDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		
		Element rootEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:notif");
		rootEl.setAttributeNS(NAMESPACE_SPEC_NS, "xmlns:ns1", TARGET_NAMESPACE);
		doc.appendChild(rootEl);
		rootEl.appendChild(doc.createTextNode(notification));
		
		return doc;
	}
}
