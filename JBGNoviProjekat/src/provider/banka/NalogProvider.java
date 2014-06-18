package provider.banka;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import basexdb.RESTUtil;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaNalogPort", 
					serviceName = "BankaNalog",
					targetNamespace = "http://www.toomanysecrets.com/BankaNalog",
					wsdlLocation = "WEB-INF/wsdl/BankaNalog.wsdl")
public class NalogProvider implements Provider<DOMSource> {
	private Properties propReceiver;
	private String message;
	private Document encrypted;
	
	public NalogProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		try {
			
			System.out.println("\nInvoking NalogProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("/banka.properties");
			Properties propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			Document decryptedDocument = MessageTransform.unpack(document, "Nalog", "Nalog",
					ConstantsXWS.TARGET_NAMESPACE_BANKA, propReceiver, "banka", "Nalog");
			
			Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD, "timestamp").item(0);
			String dateString = timestamp.getTextContent();

			Date date = null;
			try {
				date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

			RESTUtil.sacuvajEntitet(decryptedDocument, propReceiver.getProperty("naziv"), false, sender, date, "Nalog", "banka");
			
			decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument);
			decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument);
			decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
			
			
			
			
			
			

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Nalog nalog = null;
			try {
				nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
			} catch (JAXBException e) {
				return new DOMSource(decryptedDocument);
			}

			if (!validateContent(nalog)) {
				DocumentTransform.createNotificationResponse(message,ConstantsXWS.TARGET_NAMESPACE_BANKA);
			}
			else {
				DocumentTransform.createNotificationResponse("Nalog uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA);
			}
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new DOMSource(encrypted);
	}

	private boolean validateContent(Nalog nalog) {
		message ="";
		return true;
	}

}
