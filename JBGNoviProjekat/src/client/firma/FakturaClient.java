package client.firma;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.Validation;
import basexdb.RESTUtil;
import beans.notification.Notification;

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

			InputStream inputStreamSender = this.getClass().getClassLoader()
					.getResourceAsStream(sender + ".properties");
			Properties propSender = new java.util.Properties();
			propSender.load(inputStreamSender);

			Document encrypted = MessageTransform.packS("Faktura", "Faktura", inputFile, propSender, cert,
					ConstantsXWS.NAMESPACE_XSD, "Faktura");

			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				
				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");					
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response), "Faktura", "Notification",
							ConstantsXWS.TARGET_NAMESPACE_FIRMA, propSender, "firma", "Notifikacija");
				
					DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD,"timestamp").item(0);
					String dateString = timestamp.getTextContent();
					Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
					String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();
					RESTUtil.sacuvajEntitet(decryptedDocument,propSender.getProperty("naziv"), false, owner, date, "Notifikacija", "firma");
					decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument);
					decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument);
					decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
					
					SecurityClass sc = new SecurityClass();
					sc.saveDocument(decryptedDocument, "./FakturaTest/tempNot.xml");
					//definisemo kontekst, tj. paket(e) u kome se nalaze bean-ovi
					JAXBContext context = JAXBContext.newInstance("beans.notification");
					Unmarshaller unmarshaller = context.createUnmarshaller();
					Notification notification = (Notification) unmarshaller.unmarshal(new File("./FakturaTest/tempNot.xml"));
					
					JOptionPane.showMessageDialog(null,
							notification.getNotificationstring(),
							"Notification", JOptionPane.INFORMATION_MESSAGE);
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
