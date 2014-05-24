package ws.style.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
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
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FakturaClient {
	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/firmaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	
	 public static void testIt(String path) {
		 //Kreiranje web servisa (dispatcher-a)
			try {
				URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Faktura?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/firmaServis", "FirmaServis");
				QName portName = new QName("http://www.toomanysecrets.com/firmaServis", "FakturaPort");
			
				Service service = Service.create(wsdlLocation, serviceName);
				Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
			
				Document doc = buildRequest(path);
				
				if (doc != null) {
				
				DOMSource response = dispatch.invoke(new DOMSource(doc));
				
				System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            StreamResult result = new StreamResult(System.out);
	            transformer.transform(response, result);
	            System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				}
				//ne postoji dokument
				
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		 	
	 }
	 
	 private static Document buildRequest(String path)  {
			
			DocumentBuilder documentBuilder = getDocumentBuilder();
			Document doc = null;
			
			// parsiranje xml-a
			try {
				doc = documentBuilder.parse(new File(path));
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			return doc;
		}
	 
	 
	 
	 private static DocumentBuilder getDocumentBuilder() {
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
	
	/* 
	 public static void main(String[] args) {
		
		FakturaClient client = new FakturaClient();
		client.testIt();
    }*/
}
