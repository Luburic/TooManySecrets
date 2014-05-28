package ws.style.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
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

import security.SecurityClass;

public class FakturaClient {
	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/firmaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	
	 public static void testIt(String alias, String password, String keystoreFile, String keystorePassword, String inputFile) {
		 //Kreiranje web servisa (dispatcher-a)
			try {
				URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Faktura?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/firmaServis", "FirmaServis");
				QName portName = new QName("http://www.toomanysecrets.com/firmaServis", "FakturaPort");
			
				Service service = Service.create(wsdlLocation, serviceName);
				Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
			
				SecurityClass security = new SecurityClass();
				
				String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
				
				Document forCrypt = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/Faktura?xsd=../shema/FakturaSigned.xsd", "faktura");
				if( forCrypt != null ) {
					forCrypt = security.encrypt(forCrypt, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "faktura");
					security.saveDocument(forCrypt, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
					
					if( forCrypt != null) {
						
						DOMSource response = dispatch.invoke(new DOMSource(forCrypt));
						
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
						Transformer transformer = TransformerFactory.newInstance().newTransformer();
			        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			            StreamResult result = new StreamResult(System.out);
			            transformer.transform(response, result);
			            System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					}
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
	 
	 public static void main(String[] args) {

		FakturaClient.testIt("firmaa", "firmaa", "./WEB-INF/keystores/firmaa.jks", "firmaa","./FakturaTest/faktura-example1.xml");
    }
}
