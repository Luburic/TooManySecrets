package provider.banka;

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

import util.DocumentTransform;
import util.MessageTransform;
import beans.mt910.MT910;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "OdobrenjePort", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class OdobrenjeProvider implements Provider<DOMSource>{

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";

	@Override
	public DOMSource invoke(DOMSource request) {
		// TODO Auto-generated method stub
		try {
    		
    		System.out.println("\nInvoking OdobrenjeProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			
			Document decryptedDocument = MessageTransform.unpack(document, "Odobrenje", "MT910", TARGET_NAMESPACE, null);
			
			JAXBContext context = JAXBContext.newInstance("beans.mt910");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			MT910 mt910=null;
			try {
				mt910 = (MT910) unmarshaller.unmarshal(decryptedDocument);
			} catch (Exception e) {
				return new DOMSource(decryptedDocument);
			}
			
			if(!validateContent(mt910))
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument odobrenje nije validan po sadrzaju.",TARGET_NAMESPACE));
			
			//sve je ok, mt910 se snimi u bazu
			
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DOMSource(DocumentTransform.createNotificationResponse("Primljeno odobrenje.",TARGET_NAMESPACE));
		}

	private boolean validateContent(MT910 mt910) {
		// TODO Auto-generated method stub
		return true;
	}
	

}
