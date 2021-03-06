package client.firma;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
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
import util.Validation;
import basexdb.firma.FirmeSema;
import basexdb.util.FirmaDBUtil;
import beans.notification.Notification;

public class FakturaClient {
	public void testIt(String sender, String receiver, String cert, String inputFile) {
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver + "/services/Faktura?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/firmaServis", "FirmaServis");
			QName portName = new QName("http://www.toomanysecrets.com/firmaServis", "FakturaPort");

			Service service;
			try {
				service = Service.create(wsdlLocation, serviceName);
			} catch (Exception e) {
				throw Validation.generateSOAPFault("Server is not available.", SoapFault.FAULT_CODE_CLIENT, null);
			}
			Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);

			InputStream inputStreamSender = this.getClass().getClassLoader()
					.getResourceAsStream(sender + ".properties");
			Properties propSender = new java.util.Properties();
			propSender.load(inputStreamSender);

			Document encrypted = MessageTransform.packS("Faktura", "Faktura", inputFile, propSender, cert,
					ConstantsXWS.NAMESPACE_XSD_FAKTURA, "Faktura");

			FirmeSema semaFirma = FirmaDBUtil.loadFirmaDatabase(propSender.getProperty("address"));
			if(encrypted != null) {
				semaFirma.setBrojacPoslednjePoslateFakture(semaFirma.getBrojacPoslednjePoslateFakture()+1);
			}

			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));

				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");					
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response), "Faktura", "Notification",
							ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender, "firma", "Notifikacija");

					DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					if(decryptedDocument != null){
						Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NOTIFICATION,"timestamp").item(0);
						String dateString = timestamp.getTextContent();
						Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NOTIFICATION,"redniBrojPoruke").item(0);
						int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
						System.out.println("REDNI BROJ PRISTIGLE NOTIFIKACIJE IZ DOKUMENTA: "+rbrPoruke);
						Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
						String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();

						int brojac = semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getFirmaByNaziv(owner).getBrojac();
						System.out.println("REDNI BROJ POSLEDNJE PRIMLJENE IZ BAZE: "+brojac);
						Date dateFromDb = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getFirmaByNaziv(owner).getTimestamp());

						if(rbrPoruke <= brojac || dateFromDb.after(date) || dateFromDb.equals(date)) {
							JOptionPane.showMessageDialog(null,
									"Pokusaj napada",
									"Warning!!!", JOptionPane.INFORMATION_MESSAGE);
						}

						decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

						SecurityClass sc = new SecurityClass();
						sc.saveDocument(decryptedDocument, "./FakturaTest/tempNot.xml");
						//definisemo kontekst, tj. paket(e) u kome se nalaze bean-ovi
						JAXBContext context = JAXBContext.newInstance("beans.notification");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						Notification notification = (Notification) unmarshaller.unmarshal(new File("./FakturaTest/tempNot.xml"));

						semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getFirmaByNaziv(owner).setBrojac(rbrPoruke);
						semaFirma.getBrojacPoslednjePrimljeneNotifikacije().getFirmaByNaziv(owner).setTimestamp(dateString);


						JOptionPane.showMessageDialog(null,
								notification.getNotificationstring(),
								"Notification", JOptionPane.INFORMATION_MESSAGE);
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
		FakturaClient fc = new FakturaClient();
		String path = "./TestXMLi/TestFaktura1_ispravna.xml";
		//String path = "./TestXMLi/TestFaktura2_neispravna_struktura.xml";
		//String path = "./TestXMLi/TestFaktura3_neispravan_sadrzaj.xml";
		//String path = "./TestXMLi/TestFaktura4_neispravan_pib.xml";
		
		fc.testIt("firmaB", "firmaa", "cerfirmaa", path);
	}
}
