package client.firma;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.Validation;

public class FakturaClient {
	
	public void testIt(String sender, String receiver, String cert, String inputFile) {
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver + "/services/Faktura?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/firmaServis", "FirmaServis");
			QName portName = new QName("http://www.toomanysecrets.com/firmaServis", "FakturaPort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.", SoapFault.FAULT_CODE_CLIENT, null);
			}
			Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);

			InputStream inputStreamSender = this.getClass().getClassLoader().getResourceAsStream(sender + ".properties");
			Properties propSender = new java.util.Properties();
			propSender.load(inputStreamSender);

			Document encrypted = MessageTransform.packS("Faktura", "Faktura", inputFile, propSender, cert,ConstantsXWS.NAMESPACE_XSD, "Faktura");

			
			
			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				
				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");					
					
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response), "Faktura", "Notification",ConstantsXWS.TARGET_NAMESPACE_FIRMA, propSender, "firma", "Notifikacija");
					
					decryptedDocument = DocumentTransform.postDecryptTransform(decryptedDocument, propSender, "firma", "Notifikacija");
					
					if(decryptedDocument==null) {
						System.out.println("Neuspesna obrada odgovora koji je stigao od web servisa.");
					}else {
						DocumentTransform.printDocument(decryptedDocument);
					}
					
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				}	
				
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
		FakturaClient fc = new FakturaClient();
		fc.testIt("firmaB", "firmaa", "cerfirmaa", "./FakturaTest/faktura-example1.xml");
	}
	
	
	
}
