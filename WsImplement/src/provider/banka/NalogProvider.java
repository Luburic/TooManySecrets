package provider.banka;

import java.io.InputStream;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.Document;

import util.DocumentTransform;
import util.MessageTransform;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "NalogPort", serviceName = "NalogServis", targetNamespace = "http://www.toomanysecrets.com/bankaServis", wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class NalogProvider implements Provider<DOMSource> {
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";

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

			Document decryptedDocument = MessageTransform.unpack(document, "Nalog", "Nalog", TARGET_NAMESPACE,
					propReceiver);

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Nalog nalog = null;
			try {
				nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
			} catch (JAXBException e) {
				return new DOMSource(decryptedDocument);
			}

			if (!validateContent(nalog)) {
				// posalji gresku
			}
			// snimanje u bazu...
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DOMSource(DocumentTransform.createNotificationResponse("Nalog uspesno obradjena."));
	}

	public boolean validateContent(Nalog nalog) {
		return true;
	}
}