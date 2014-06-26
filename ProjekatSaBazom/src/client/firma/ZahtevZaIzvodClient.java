package client.firma;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
import beans.presek.Presek;

public class ZahtevZaIzvodClient {
	
	public Presek testIt(String sender, String receiver, String cert, String inputFile) {
		Presek presek = null;
		try {
			URL wsdlLocation = new URL("http://localhost:8080/" + receiver + "/services/BankaIzvod?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/BankaIzvod", "BankaIzvod");
			QName portName = new QName("http://www.toomanysecrets.com/BankaIzvod", "BankaIzvodPort");

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

			Document encrypted = MessageTransform.packS("Izvod", "Izvod", inputFile, propSender, cert,
					ConstantsXWS.NAMESPACE_XSD_ZAHTEV, "Izvod");
			
			FirmeSema semaFirma = FirmaDBUtil.loadFirmaDatabase(propSender.getProperty("address"));
			if(encrypted != null) {
				semaFirma.setBrojacPoslednjegZahtevaZaIzvod(semaFirma.getBrojacPoslednjegZahtevaZaIzvod()+1);
				FirmaDBUtil.storeFirmaDatabase(semaFirma, propSender.getProperty("address"));
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				
				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");					
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response), "Presek", "Presek",
							ConstantsXWS.NAMESPACE_XSD_PRESEK, propSender, "firma", "Presek");
				
					//DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");
					if(decryptedDocument != null){
						Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_PRESEK,"timestamp").item(0);
						String dateString = timestamp.getTextContent();
						Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_PRESEK,"redniBrojPoruke").item(0);
						int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
						System.out.println("REDNI BROJ PRISTIGLE NOTIFIKACIJE IZ DOKUMENTA: "+rbrPoruke);
						Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
						String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();
						
						int brojac = semaFirma.getBrojacPoslednjegPrimljenogPreseka().getBankaByNaziv(owner).getBrojac();
						System.out.println("REDNI BROJ POSLEDNJE PRIMLJENE IZ BAZE: "+brojac);
						Date dateFromDb = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(semaFirma.getBrojacPoslednjegPrimljenogPreseka().getBankaByNaziv(owner).getTimestamp());
						
						if(rbrPoruke <= brojac || dateFromDb.after(date) || dateFromDb.equals(date)) {
							System.out.println("Pokusaj napada");
						}
						//RESTUtil.sacuvajEntitet(decryptedDocument,propSender.getProperty("naziv"), false, owner, date, "Notifikacija", "firma");
						decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_PRESEK);
						decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_PRESEK);
						decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
						
						//DocumentTransform.printDocument(decryptedDocument);
						

						SecurityClass sc = new SecurityClass();
						sc.saveDocument(decryptedDocument, "./PresekTest/presek.xml");
						//definisemo kontekst, tj. paket(e) u kome se nalaze bean-ovi
						JAXBContext context = JAXBContext.newInstance("beans.presek");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						presek = (Presek) unmarshaller.unmarshal(new File("./PresekTest/presek.xml"));
						
						semaFirma = FirmaDBUtil.loadFirmaDatabase(propSender.getProperty("address"));
						semaFirma.getBrojacPoslednjegPrimljenogPreseka().getBankaByNaziv(owner).setBrojac(rbrPoruke);
						semaFirma.getBrojacPoslednjegPrimljenogPreseka().getBankaByNaziv(owner).setTimestamp(dateString);
						FirmaDBUtil.storeFirmaDatabase(semaFirma, propSender.getProperty("address"));
						
					} else {
						System.out.println("Ne postoji racun u banci");
						return null;
					}
					FirmaDBUtil.storeFirmaDatabase(semaFirma, propSender.getProperty("address"));
				} else {
					System.out.println("Greska!");
				}
				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return presek;
	}

	public static void main(String[] args) {
		String path = "./TestXMLi/TestZahtevZaIzvod1_jedan_presek.xml";
		//String path = "./TestXMLi/TestZahtevZaIzvod2_vise_preseka.xml";
		//String path = "./TestXMLi/TestZahtevZaIzvod3_nema_preseka.xml";
		ZahtevZaIzvodClient zzi = new ZahtevZaIzvodClient();
		int cnt = 0;
		List<Presek> preseci = new ArrayList<>();
		Presek presek = zzi.testIt("firmaA", "bankaa", "cerbankaa", "./IzvodTest/izvod.xml");
		while((presek.getStavke().getStavka().size() > 0 && !presek.getZaglavlje().getBrojRacuna().equals("000000000000000000")) && (presek != null)) {
			preseci.add(presek);
			presek = zzi.testIt("firmaA", "bankaa", "cerbankaa", "./IzvodTest/izvod"+(++cnt)+".xml");
		}
		for(Presek p : preseci) {
			System.out.println("ZAGLAVLJE: "+p.getZaglavlje().getNovoStanje());
			System.out.println("STAVKE: "+p.getStavke().getStavka().get(0).getDuznikNalogodavac());
		}
	}

}
