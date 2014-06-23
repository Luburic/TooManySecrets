package client.firma;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
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

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
import util.Validation;
import basexdb.firma.FirmeSema;
import basexdb.util.FirmaDBUtil;
import beans.nalog.Nalog;
import beans.notification.Notification;

public class NalogClient {
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
					Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
					String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();
					int brojac = semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getBankaByNaziv(owner).getBrojac();
					Date dateFromDb = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getBankaByNaziv(owner).getTimestamp());

					if(rbrPoruke <= brojac || dateFromDb.after(date) || dateFromDb.equals(date)) {
						JOptionPane.showMessageDialog(null,
								"Pokusaj napada",
								"Warning!!!", JOptionPane.INFORMATION_MESSAGE);
					}
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
		fc.createNalog();
		fc.testIt("firmaA", "bankaa", "cerbankaa", "./NalogTest/nalog.xml");
	}

	// params kasnije
	public Nalog createNalog() {

		Nalog nalog = null;
		try {
			nalog = new Nalog();
			nalog.setIdPoruke("123456");

			nalog.setDuznikNalogodavac("pera");
			nalog.setPrimalacPoverilac("djoka");

			nalog.setDatumNaloga(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
			nalog.setDatumValute(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));

			nalog.setHitno(true);

			nalog.setIznos(new BigDecimal(100500));

			nalog.setModelOdobrenja(97);
			nalog.setModelZaduzenja(97);

			nalog.setOznakaValute("rsd");
			nalog.setPozivNaBrojOdobrenja("111111");
			nalog.setPozivNaBrojZaduzenja(String.valueOf(111111));

			nalog.setRacunDuznika("111111111111111111");
			nalog.setRacunPoverioca("222222222222222222");

			nalog.setSvrhaPlacanja("reket");

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(nalog, new File("./NalogTest/nalog.xml"));
			
			Document doc = Validation.buildDocumentWithoutValidation("./NalogTest/nalog.xml");
			Element faktura = (Element) doc.getElementsByTagName("nalog").item(0);
			faktura.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			faktura.setAttribute("sender", "firmaa");
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, "./NalogTest/nalog.xml");

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nalog;
	}

}