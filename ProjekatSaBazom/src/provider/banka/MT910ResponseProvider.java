package provider.banka;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
import util.Validation;
import basexdb.banka.BankeSema;
import basexdb.registar.Registar;
import basexdb.util.BankaDBUtil;
import basexdb.util.RegistarDBUtil;
import beans.mt910.MT910;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "MT910ResponsePort", 
					serviceName = "MT910Response",
					targetNamespace = "http://www.toomanysecrets.com/MT910Response",
					wsdlLocation = "WEB-INF/wsdl/MT910Response.wsdl")
public class MT910ResponseProvider implements Provider<DOMSource> {
	
	
	private Document encryptedDocument;
	private BankeSema semaBanka;
	private Properties propReceiver;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
		try {
    		
    		System.out.println("\nInvoking MT103Provider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);
			
			Element esender = (Element) document.getElementsByTagName("MT910").item(0);
			String sender = esender.getAttribute("sender");
			
			Document decryptedDocument = MessageTransform.unpack(document,"MT910", "MT910", ConstantsXWS.NAMESPACE_XSD_MT910, propReceiver, "banka", "MT910");

			System.out.println("STIGAO U MT910 PROVIDERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
			DocumentTransform.printDocument(decryptedDocument);
			
			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/MT910Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}
			
			//semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
			
			Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");
			
			if (forSave != null) {
			
				Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT910,"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT910,"redniBrojPoruke").item(0);
				int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

				
				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT910);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT910);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
				
				
				DocumentTransform.printDocument(decryptedDocument);
			
				JAXBContext context = JAXBContext.newInstance("beans.mt910");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				MT910 mt910 = (MT910) unmarshaller.unmarshal(decryptedDocument);
			
			
				/*if(registar.getBanke().getBankaByCode(mt910.getSwiftBankePoverioca()) != null) {
					semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
					semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setBrojac(rbrPoruke);
					semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setTimestamp(dateString);
					BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));
					DocumentTransform.createNotificationResponse("423", "Izvrsena radnja.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					encryptedDocument = MessageTransform.packS("Notifikacija", "Notification", apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
				} else {*/
					
					DocumentTransform.createNotificationResponse("424", "Nije izvrsena radnja.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					encryptedDocument = MessageTransform.packS("Notifikacija", "Notification", apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
				//}
				
			
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DOMSource(encryptedDocument);
	}

}
