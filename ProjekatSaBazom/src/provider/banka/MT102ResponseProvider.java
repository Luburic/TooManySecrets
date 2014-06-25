package provider.banka;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
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
import beans.mt102.MT102;
import beans.mt102.MT102.PojedinacneUplate.PojedinacnaUplata;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "MT102ResponsePort", 
					serviceName = "MT102Response",
					targetNamespace = "http://www.toomanysecrets.com/MT102Response",
					wsdlLocation = "WEB-INF/wsdl/MT102Response.wsdl")
public class MT102ResponseProvider implements Provider<DOMSource> {
	
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
			
			Element esender = (Element) document.getElementsByTagName("MT102").item(0);
			String sender = esender.getAttribute("sender");
			
			Document decryptedDocument = MessageTransform.unpack(document,"MT102", "MT102", ConstantsXWS.NAMESPACE_XSD_MT102, propReceiver, "banka", "MT102");

			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/MT102Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}
			
			semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
			
			Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");
			
			if (forSave != null) {
			
				Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT102,"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT102,"redniBrojPoruke").item(0);
				int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

				
				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT102);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT102);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
				
				
				DocumentTransform.printDocument(decryptedDocument);
			
				JAXBContext context = JAXBContext.newInstance("beans.mt102");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				MT102 mt102 = (MT102) unmarshaller.unmarshal(decryptedDocument);
			
				semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
				if(mt102 != null && (registar.getBanke().getBankaByCode(mt102.getZaglavljeMT102().getSwiftBankePoverioca()) != null)) {
					BigDecimal ukupanIznos = mt102.getZaglavljeMT102().getUkupanIznos();
					BigDecimal pojedinacneSuma = new BigDecimal(0);
					for(PojedinacnaUplata p : mt102.getPojedinacneUplate().getPojedinacnaUplata()) {
						pojedinacneSuma.add(p.getIznos());
					}
					if(ukupanIznos.compareTo(pojedinacneSuma) != 0) {
						DocumentTransform.createNotificationResponse("426", "Ukupan iznos i suma stavki se ne poklapaju.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
						encryptedDocument = MessageTransform.packS("Notifikacija", "Notification", apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
					} else {
						for(PojedinacnaUplata p : mt102.getPojedinacneUplate().getPojedinacnaUplata()) {
							semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(p.getPrimalac()).setStanje(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(p.getPrimalac()).getStanje().add(p.getIznos()));
						}
					}

					semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setBrojac(rbrPoruke);
					semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setTimestamp(dateString);
					BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));
					DocumentTransform.createNotificationResponse("423", "Izvrsena radnja.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					encryptedDocument = MessageTransform.packS("Notifikacija", "Notification", apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
				} else {
					
					DocumentTransform.createNotificationResponse("424", "Nije izvrsena radnja.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					encryptedDocument = MessageTransform.packS("Notifikacija", "Notification", apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
				}
				
			
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
