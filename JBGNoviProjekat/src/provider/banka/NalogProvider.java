package provider.banka;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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

import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
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
	String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);

	
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

			
			Element esender = (Element) document.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD, "sender").item(0);
			
			if(esender==null)
				return new DOMSource(encrypted);
			
			String sender = esender.getTextContent();
			System.out.println("****NALOG SENDER: "+sender);
			esender.getParentNode().removeChild(esender);

			
			
			Document decryptedDocument = MessageTransform.unpack(document,"Nalog", "Nalog",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG, propReceiver,"banka", "Nalog");
			
			if(decryptedDocument==null){ 
				encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer"+esender.getTextContent(),ConstantsXWS.NAMESPACE_XSD, "Notif");
				return new DOMSource(encrypted);
			}
			
			
			
			//String sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();
			decryptedDocument = DocumentTransform.postDecryptTransform(decryptedDocument, propReceiver, "banka", "Nalog");

			
			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Nalog nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
			
			
			if (!validateContent(nalog)) {
				DocumentTransform.createNotificationResponse(message,ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
			}

			else {

				if (nalog.isHitno() || nalog.getIznos().compareTo(limit) != -1) {
					
					MT103 mt103 = createMT103(nalog);
					
					if(mt103==null)
						DocumentTransform.createNotificationResponse("Nije moguce kreirati mt103 na onovu primljenog naloga.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					
					//MT103Client.testIt(propReceiver, "bankac","cerbankac","./MT103Test/mt103.xml" );
					//bez obzira sta se desava dalje sa mt103 klijentu firme se kreira odgovor da je njegov nalog obradjen
					DocumentTransform.createNotificationResponse("Nalog uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
				} else {
					// clearing

				}
			}
			encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD, "Notif");

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
		} catch (Exception e1) {
			e1.printStackTrace();
		} 

		return new DOMSource(encrypted);
	}

	private boolean validateContent(Nalog nalog) {
		message = "";

		if (nalog.isHitno() || nalog.getIznos().compareTo(limit) != -1) {
			
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
			
			

			JAXBContext context = JAXBContext.newInstance("beans.mt103");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt, new File("./MT103Test/mt103.xml"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}*/
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return mt;
	}
	
}
