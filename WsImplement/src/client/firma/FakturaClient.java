package client.firma;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.DocumentTransform;
import util.MessageTransform;

public class FakturaClient {
	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/firmaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	
	 public static void testIt(String alias, String password, String keystoreFile, String keystorePassword, String inputFile) {
		
			try {
				URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Faktura?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/firmaServis", "FirmaServis");
				QName portName = new QName("http://www.toomanysecrets.com/firmaServis", "FakturaPort");
			
				Service service = Service.create(wsdlLocation, serviceName);
				Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
			
						
				Document encrypted= MessageTransform.packS("Faktura", "Faktura", inputFile, alias, password, keystoreFile, keystorePassword, NAMESPACE_XSD);
						
				if(encrypted!=null) {
					
					//pitanje je da li sme ovako, za sada je hard-code da firmaA salje a firmaB prima
					//ali to moze sa dodatnim parametrima da se prosledjuje (u gui da se odradi neki combo box)
					Element faktura = (Element) encrypted.getElementsByTagNameNS(NAMESPACE_XSD, "faktura").item(0);
					Element propertieSender = encrypted.createElementNS(NAMESPACE_XSD, "propertieSender");
					faktura.appendChild(propertieSender);
					propertieSender.appendChild(encrypted.createTextNode("/firmaA.properties"));
					Element propertieReceiver = encrypted.createElementNS(NAMESPACE_XSD, "propertieReceiver");
					faktura.appendChild(propertieReceiver);
					propertieReceiver.appendChild(encrypted.createTextNode("/firmaB.properties"));
					//
				
					DOMSource response = dispatch.invoke(new DOMSource(encrypted));
						
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					DocumentTransform.printDocument(DocumentTransform.convertToDocument(response));
		            System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					}
					
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		 	
	 }
	 
	 public static void main(String[] args) {

		FakturaClient.testIt("firmaa", "firmaa", "./WEB-INF/keystores/firmaa.jks", "firmaa","./FakturaTest/faktura-example1.xml");
    }
}
