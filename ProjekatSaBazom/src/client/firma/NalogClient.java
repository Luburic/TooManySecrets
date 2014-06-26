package client.firma;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.Validation;
import basexdb.firma.FirmeSema;
import basexdb.util.FirmaDBUtil;
import beans.nalog.Nalog;
import beans.notification.Notification;

public class NalogClient {
	private String sender;
	
	public void testIt(String sender, String receiver, String cert,
			String inputFile) {
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/BankaNalog?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/BankaNalog", "BankaNalog");
			QName portName = new QName("http://www.toomanysecrets.com/BankaNalog","BankaNalogPort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.",
						SoapFault.FAULT_CODE_CLIENT, null);
			}
			Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);

			InputStream inputStreamSender = this.getClass().getClassLoader().getResourceAsStream(sender + ".properties");
			Properties propSender = new java.util.Properties();
			propSender.load(inputStreamSender);
			sender = propSender.getProperty("naziv");
			
			Document encrypted = MessageTransform.packS("Nalog", "Nalog",inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD_NALOG,"Nalog");

			FirmeSema semaFirma = FirmaDBUtil.loadFirmaDatabase(propSender.getProperty("address"));
			if(encrypted != null) {
				semaFirma.setBrojacPoslednjegPoslatogNaloga(semaFirma.getBrojacPoslednjegPoslatogNaloga()+1);
			}
			
			
			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));

				if (response != null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification",ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender,"banka", "Notifikacija");
					DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
				if(decryptedDocument != null) {
					Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NOTIFICATION,"timestamp").item(0);
					String dateString = timestamp.getTextContent();
					Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NOTIFICATION,"redniBrojPoruke").item(0);
					
					int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
					String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();
					decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
					decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
					decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
					
					SecurityClass sc = new SecurityClass();
					sc.saveDocument(decryptedDocument, "./NalogTest/tempNot.xml");
					
					JAXBContext context = JAXBContext.newInstance("beans.notification");
					Unmarshaller unmarshaller = context.createUnmarshaller();
					Notification notification = (Notification) unmarshaller.unmarshal(new File("./NalogTest/tempNot.xml"));
					semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getBankaByNaziv(owner).setBrojac(rbrPoruke);
					semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getBankaByNaziv(owner).setTimestamp(dateString);
					
					
					JOptionPane.showMessageDialog(null,notification.getNotificationstring(),"Notification", JOptionPane.INFORMATION_MESSAGE);
				}
				FirmaDBUtil.storeFirmaDatabase(semaFirma, propSender.getProperty("address"));
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
		NalogClient fc = new NalogClient();
		//fc.createNalog();
		String path = "./TestXMLi/TestNalog01_ispravan_ista_banka.xml";
		//String path = "./TestXMLi/TestNalog02_ispravan_razlicita_banka_manje_250k.xml";
		//String path = "./TestXMLi/TestNalog03_ispravan_razlicita_banka_vece_250k.xml";
		//String path = "./TestXMLi/TestNalog04_neispravna_struktura.xml";
		//String path = "./TestXMLi/TestNalog05_neispravan_sadrzaj.xml";
		//String path = "./TestXMLi/TestNalog06_neispravan_sadrzaj_nema_racuna_ista_banka.xml";
		//String path = "./TestXMLi/TestNalog07_neispravan_sadrzaj_nema_racuna_druga_banka.xml";
		//String path = "./TestXMLi/TestNalog08_neispravan_sadrzaj_druga_banka_ne_postoji.xml";
		
		//setovati u banka_init i blokiran=true
		//String path = "./TestXMLi/TestNalog10_neispravan_blokiran_racun.xml";
		
		//nelikvidan setovati u bankaa_init i centrali xml , stanje=-20 npr
		//tring path = "./TestXMLi/TestNalog11_neispravan_obracunski_racun_nelikvidan.xml";
		
		fc.testIt("firmaA", "bankaa", "cerbankaa", path);
		
	}

	// params kasnije
	public Nalog createNalog() {
		String path = "./TestXMLi/TestNalog01_ispravan_ista_banka.xml";

		Nalog nalog = null;
		try {
			nalog = new Nalog();
			nalog.setSender("firmaa");
			nalog.setIdPoruke("123456");

			nalog.setDuznikNalogodavac("firmaa");
			nalog.setPrimalacPoverilac("firmaa");

			nalog.setDatumNaloga(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
			nalog.setDatumValute(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));

			nalog.setHitno(false);

			nalog.setIznos(new BigDecimal(100.00));

			nalog.setModelOdobrenja(97);
			nalog.setModelZaduzenja(97);

			nalog.setOznakaValute("RSD");
			nalog.setPozivNaBrojOdobrenja("111111");
			nalog.setPozivNaBrojZaduzenja("111111");

			nalog.setRacunDuznika("340111111111111111");
			nalog.setRacunPoverioca("360222222222222222");

			nalog.setSvrhaPlacanja("reket");

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(nalog, new File(path)); 
			
			Document doc = Validation.buildDocumentWithoutValidation(path);
			Element nal = (Element) doc.getElementsByTagName("nalog").item(0);
			nal.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			nal.setAttribute("sender","firmaa");
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc,path);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nalog;
	}

}