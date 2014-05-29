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


public class NalogClient {

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	 public void testIt(String alias, String password, String keystoreFile, String keystorePassword, String inputFile) {
		 
		 	
			try {
			
			URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Banka?wsdl");
		
			QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
			QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "NalogPort");
			
			
			Service service = Service.create(wsdlLocation, serviceName);
			Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
			SecurityClass security = new SecurityClass();
			String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml"; 
			
			//potpisivanje
			Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/Banka?xsd=../shema/NalogSigned.xsd", "nalog");
			
			Document encrypted = null;
			
			//enkripcija
			if( signed != null ) {
				encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "nalog");
				security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
			}
			
			if(encrypted == null)
					return;

			
				
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				
				System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            StreamResult result = new StreamResult(System.out);
	            transformer.transform(response, result);
	            System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
	            
			} catch (MalformedURLException e) {
				e.printStackTrace();
			
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
				
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	 
	 }
	
	
	public static void main(String[] args) {
		NalogClient cl = new NalogClient();
		cl.testIt("firmaa", "firmaa", "./WEB-INF/keystores/firmaa.jks", "firmaa","./NalogTest/Nalog.xml");
		
	}

}
