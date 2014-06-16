package provider.banka;

import java.io.IOException;
import java.io.InputStream;
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

import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaNalogPort", 
					serviceName = "BankaNalog",
					targetNamespace = "http://www.toomanysecrets.com/BankaNalog",
					wsdlLocation = "WEB-INF/wsdl/BankaNalog.wsdl")
public class NalogProvider implements Provider<DOMSource> {
	public NalogProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		try {
			// serijalizacija DOM-a na ekran
			System.out.println("\nInvoking NalogProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("/banka.properties");
			Properties propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			//Document decryptedDocument = MessageTransform.unpack(document, "Nalog", "Nalog",
					//ConstantsXWS.TARGET_NAMESPACE_BANKA, propReceiver);
			Document decryptedDocument = null;

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Nalog nalog = null;
			try {
				nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
			} catch (JAXBException e) {
				return new DOMSource(decryptedDocument);
			}

			if (!validateContent(nalog)) {
				// Radi sa greškom.
			}
			// snimanje u bazu...
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
		return new DOMSource(DocumentTransform.createNotificationResponse("Nalog uspesno obradjen.",
				ConstantsXWS.TARGET_NAMESPACE_BANKA));
	}

	private boolean validateContent(Nalog nalog) {
		return true;
	}

}
