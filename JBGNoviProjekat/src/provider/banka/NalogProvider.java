package provider.banka;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Stateless;
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
import util.MyDatatypeConverter;
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

			Document decryptedDocument = MessageTransform.unpack(document,
					"Nalog", "Nalog", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG,
					propReceiver, "banka", "Nalog");

			System.out.println("-------------------decrypted----------------------------------");
			DocumentTransform.printDocument(decryptedDocument);
			System.out.println("-------------------decrypted----------------------------------");

			/*
			 * Element timestamp = (Element)
			 * decryptedDocument.getElementsByTagNameNS
			 * (ConstantsXWS.NAMESPACE_XSD, "timestamp").item(0); String
			 * dateString = timestamp.getTextContent();
			 * 
			 * Date date = null; try { date = new
			 * SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString); }
			 * catch (ParseException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 * 
			 * String sender =
			 * SecurityClass.getOwner(decryptedDocument).toLowerCase();
			 * 
			 * RESTUtil.sacuvajEntitet(decryptedDocument,
			 * propReceiver.getProperty("naziv"), false, sender, date, "Nalog",
			 * "banka");
			 * 
			 * decryptedDocument =
			 * MessageTransform.removeTimestamp(decryptedDocument);
			 * decryptedDocument =
			 * MessageTransform.removeRedniBrojPoruke(decryptedDocument);
			 * decryptedDocument =
			 * MessageTransform.removeSignature(decryptedDocument);
			 */

			/*
			 * JAXBContext context = JAXBContext.newInstance("beans.nalog");
			 * Unmarshaller unmarshaller = context.createUnmarshaller();
			 * 
			 * Nalog nalog = null; try { nalog = (Nalog)
			 * unmarshaller.unmarshal(decryptedDocument); } catch (JAXBException
			 * e) { return new DOMSource(decryptedDocument); }
			 * 
			 * if (!validateContent(nalog)) {
			 * DocumentTransform.createNotificationResponse
			 * (message,ConstantsXWS.TARGET_NAMESPACE_BANKA); }
			 * 
			 * 
			 * else {
			 * 
			 * 
			 * if(nalog.isHitno() || nalog.getIznos().compareTo(limit)!= -1) {
			 * //rtgs //.. DocumentTransform.createNotificationResponse(
			 * "Nalog uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA); }
			 * else { //clearing
			 * 
			 * 
			 * } }
			 */

			// String apsolute =
			// DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
			// encrypted = MessageTransform.packS("Notifikacija",
			// "Notification",apsolute, propReceiver,
			// "cer"+sender,ConstantsXWS.NAMESPACE_XSD, "Notifikacija");

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} /*
		 * catch (JAXBException e1) { e1.printStackTrace(); }
		 */catch (IOException e1) {
			e1.printStackTrace();
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
			mt.setIdPoruke(MessageTransform.randomString(50));
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
			e.printStackTrace();
		}

		return mt;
	}

}
