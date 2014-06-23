package provider.banka;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
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
import util.MyDatatypeConverter;
import util.Validation;
import basexdb.RESTUtil;
import beans.mt103.MT103;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaNalogPort", serviceName = "BankaNalog", targetNamespace = "http://www.toomanysecrets.com/BankaNalog", wsdlLocation = "WEB-INF/wsdl/BankaNalog.wsdl")
public class NalogProvider implements Provider<DOMSource> {

	private String message;
	private Document encrypted;
	private BigDecimal limit = new BigDecimal(250000);
	private Properties propReceiver;

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

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");// /
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);
			
			Element esender = (Element) document.getElementsByTagName("nalog").item(0);
			String sender = esender.getAttribute("sender");

			Document decryptedDocument = MessageTransform.unpack(document,"Nalog", "Nalog",ConstantsXWS.NAMESPACE_XSD_NALOG, propReceiver,"banka", "Nalog");
			
			DocumentTransform.printDocument(decryptedDocument);
			
			Reader reader = Validation.createReader(decryptedDocument);
			Document forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/NalogSigned.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			
			//Reader reader1 = Validation.createReader(decryptedDocument);
			//Document notification = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/NotificationSigned.xsd"});
			/*Document valid = Validation.buildDocumentWithoutValidation(DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6));
			Element notification = (Element) valid.getElementsByTagName("notification").item(0);
			boolean flag = false;
			if(notification.getChildNodes().item(0).getTextContent().contains("Nalog"))
				flag = true;*/


			if (forSave != null) {
				Element timestamp = (Element) decryptedDocument
						.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NALOG,
								"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
						.parse(dateString);
				sender = SecurityClass.getOwner(decryptedDocument)
						.toLowerCase();
				
				decryptedDocument = MessageTransform
						.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NALOG);
				decryptedDocument = MessageTransform
						.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NALOG);
				decryptedDocument = MessageTransform
						.removeSignature(decryptedDocument);
				
				Element root = (Element) decryptedDocument.getElementsByTagName("nalog").item(0);
				root.removeAttribute("sender");
				
				RESTUtil.sacuvajEntitet(forSave,
						propReceiver.getProperty("naziv"), false, sender, date,
						"Nalog", "banka");
				
				JAXBContext context = JAXBContext.newInstance("beans.nalog");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				Nalog nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
				if (!validateContent(nalog)) {
					DocumentTransform.createNotificationResponse("455", message,ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
				}

				else {

					if (nalog.isHitno() || nalog.getIznos().compareTo(limit) != -1) {
						
						MT103 mt103 = createMT103(nalog);
						
						if(mt103==null)
							DocumentTransform.createNotificationResponse("456", "Nije moguce kreirati mt103 na onovu primljenog naloga.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
						
						//MT103Client.testIt(propReceiver, "bankac","cerbankac","./MT103Test/mt103.xml" );
						//bez obzira sta se desava dalje sa mt103 klijentu firme se kreira odgovor da je njegov nalog obradjen
						DocumentTransform.createNotificationResponse("200", "Nalog uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					} else {
						// clearing

					}
				}
			}
			String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
			encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notif");

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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new DOMSource(encrypted);
	}

	private boolean validateContent(Nalog nalog) {
		message = "";

		if (nalog.isHitno() || nalog.getIznos().compareTo(limit) != -1) {
			// rtgs
		} else {
			// clearing
		}

		return true;
	}

	private MT103 createMT103(Nalog nalog) {
		MT103 mt = null;
		try {
			mt = new MT103();
			/*mt.setIdPoruke(MessageTransform.randomString(50));
			mt.setSwiftBankeDuznika(propReceiver.getProperty("swift"));
			mt.setObracunskiRacunBankeDuznika(propReceiver.getProperty("obracunskiRac"));

			String bankPropertiesFileName = MessageTransform.checkBank(nalog.getRacunPoverioca().substring(0, 3));
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(bankPropertiesFileName + ".properties");
			Properties reciver = new Properties();
			reciver.load(is);

			mt.setSwiftBankePoverioca(reciver.getProperty("swift"));
			mt.setObracunskiRacunBankePoverioca(reciver.getProperty("obracunskiRac"));

			mt.setDuznik(nalog.getDuznikNalogodavac());
			mt.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
			mt.setPrimalac(nalog.getPrimalacPoverilac());
			mt.setDatumNaloga(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
			mt.setDatumValute(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
			mt.setRacunDuznika(nalog.getRacunDuznika());
			mt.setModelZaduzenja(nalog.getModelZaduzenja());
			mt.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
			mt.setRacunPoverioca(nalog.getRacunPoverioca());
			mt.setModelOdobrenja(nalog.getModelOdobrenja());
			mt.setPozivNaBrojOdobrenja(String.valueOf(nalog.getPozivNaBrojOdobrenja()));
			mt.setIznos(nalog.getIznos());
			mt.setSifraValute(nalog.getOznakaValute());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mt;
	}

}
