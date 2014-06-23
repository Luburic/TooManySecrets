package provider.firma;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
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
import basexdb.firma.FirmeSema;
import basexdb.util.FirmaDBUtil;
import beans.faktura.Faktura;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "FakturaPort", serviceName = "FirmaServis", targetNamespace = ConstantsXWS.TARGET_NAMESPACE_FIRMA, wsdlLocation = "WEB-INF/wsdl/Firma.wsdl")
public class FakturaProvider implements Provider<DOMSource> {
	
	//public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	private Properties propReceiver;
	private String message;
	private Document encrypted;
	
	public FakturaProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		try {
			// serijalizacija DOM-a na ekran
			System.out.println("\nInvoking FakturaProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("firma.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);
			
			
			Element esender = (Element) document.getElementsByTagName("faktura").item(0);
			String sender = esender.getAttribute("sender");

			Document decryptedDocument = MessageTransform.unpack(document, "Faktura", "Faktura",
					ConstantsXWS.NAMESPACE_XSD_FAKTURA, propReceiver, "firma", "Faktura");
			
			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/FakturaSigned.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}
			
			
			//Document valid = Validation.buildDocumentWithoutValidation(DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6));
			//Element notification = (Element) valid.getElementsByTagName("notification").item(0);
			/*Document valid = Validation.buildDocumentWithoutValidation(DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6));
			Element notification = (Element) valid.getElementsByTagName("notification").item(0);
			boolean flag = false;
			if(notification.getChildNodes().item(0).getTextContent().contains("Faktura"))
				flag = true;*/
			
			FirmeSema semaFirma = FirmaDBUtil.loadFirmaDatabase(propReceiver.getProperty("address"));
			
			if (forSave != null) {
				Element timestamp = (Element) decryptedDocument
						.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_FAKTURA, "timestamp")
						.item(0);
				String dateString = timestamp.getTextContent();
				/*Date date = null;
				try {
					date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
							.parse(dateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				sender = SecurityClass.getOwner(decryptedDocument)
						.toLowerCase();
				/*RESTUtil.sacuvajEntitet(decryptedDocument,
						propReceiver.getProperty("naziv"), false, sender, date,
						"Faktura", "firma");*/
				decryptedDocument = MessageTransform
						.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_FAKTURA);
				decryptedDocument = MessageTransform
						.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_FAKTURA);
				decryptedDocument = MessageTransform
						.removeSignature(decryptedDocument);
				
				//Element root = (Element) decryptedDocument.getElementsByTagName("faktura").item(0);
				//root.removeAttribute("sender");
				
				JAXBContext context = JAXBContext.newInstance("beans.faktura");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				Faktura faktura = null;
				try {
					faktura = (Faktura) unmarshaller
							.unmarshal(decryptedDocument);
				} catch (JAXBException e) {
					return new DOMSource(decryptedDocument);
				}
				if (!validateContent(faktura)) {
					DocumentTransform.createNotificationResponse("455", message,
							ConstantsXWS.TARGET_NAMESPACE_FIRMA);
					/*RESTUtil.sacuvajEntitet(forSave,
							propReceiver.getProperty("naziv"), false, sender, date,
							"Faktura", "firma");*/
					//update brojac
					semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(sender).setBrojac(
							semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(sender).getBrojac()+1);
					semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(sender).setTimestamp(dateString);
					semaFirma.getNeplaceneFakture().getFaktura().add(faktura);
				} else {
					DocumentTransform.createNotificationResponse("200",
							"Faktura uspesno obradjena.",
							ConstantsXWS.TARGET_NAMESPACE_FIRMA);
					/*RESTUtil.sacuvajEntitet(forSave,
							propReceiver.getProperty("naziv"), false, sender, date,
							"Faktura", "firma");*/
					semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(sender).setBrojac(
							semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(sender).getBrojac()+1);
					semaFirma.getBrojacPoslednjePrimljeneFakture().getFirmaByNaziv(sender).setTimestamp(dateString);
					semaFirma.getNeplaceneFakture().getFaktura().add(faktura);
				}
			}
			//propSender=proprReciver
			String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
			
			encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer"+sender ,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
			if(encrypted != null) {
				semaFirma.setBrojacPoslednjePoslateNotifikacije(semaFirma.getBrojacPoslednjePoslateNotifikacije()+1);
			}
			FirmaDBUtil.storeFirmaDatabase(semaFirma, propReceiver.getProperty("address"));
			
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
		}

		return new DOMSource(encrypted);
	}

public boolean validateContent(Faktura fak) {
		
		
		double tempKolicina, tempJedinicnaCena, tempVrednost, tempProcenatRabata, tempUmanjenoZaRabat, tempPorez, tempIznosRabata;
		double ukupnoRobeIUsluge, zaUplatu, ukupanPorez, ukupanRabat, vrednostRobe, vrednostUsluga, ukupnoStavke, zaUplatuStavke, ukupanPorezStavke, ukupanRabatStavke;

		ukupnoStavke = 0;
		zaUplatuStavke = 0;
		ukupanPorezStavke = 0;
		ukupanRabatStavke = 0;

		List<Faktura.Stavka> stavke = fak.getStavka();

		
		
		
		for (Faktura.Stavka stavka : stavke) {

			tempKolicina = stavka.getKolicina().doubleValue();
			tempJedinicnaCena = stavka.getJedinicnaCena().doubleValue();
			tempVrednost = stavka.getVrednost().doubleValue();
			tempIznosRabata = stavka.getIznosRabata().doubleValue();
			tempProcenatRabata = stavka.getProcenatRabata().doubleValue();
			tempUmanjenoZaRabat = stavka.getUmanjenoZaRabat().doubleValue();
			tempPorez = stavka.getUkupanPorez().doubleValue();

			if (tempKolicina * tempJedinicnaCena != tempVrednost) {
				message ="Greska: Vrednost stavke "+ stavka.getRedniBroj()+ ". ne odgovara proizvodu kolicine i jedinicne cene.";
				return false;
			}

			else if (tempVrednost * tempProcenatRabata / 100 != tempIznosRabata) {
				message =  "Greska: Vrednost rabata stavke "+ stavka.getRedniBroj()+ ". ne odgovara procentu rabata ukupne vrednosti.";
				return false;
			}

			else if (tempUmanjenoZaRabat != tempVrednost - tempIznosRabata) {
				message = "Greska: Vrednost umanjena za rabat stavke "+ stavka.getRedniBroj()+ ". ne odgovara ukupnoj vrednosti umanjenoj za rabat.";
				return false;

			}

			ukupnoStavke += tempVrednost;
			zaUplatuStavke += tempUmanjenoZaRabat + tempPorez;
			ukupanPorezStavke += tempPorez;
			ukupanRabatStavke += tempIznosRabata;
		}

		ukupnoRobeIUsluge = fak.getZaglavlje().getUkupnoRobaIUsluge().doubleValue();
		zaUplatu = fak.getZaglavlje().getIznosZaUplatu().doubleValue();
		ukupanPorez = fak.getZaglavlje().getUkupanPorez().doubleValue();
		ukupanRabat = fak.getZaglavlje().getUkupanRabat().doubleValue();
		vrednostRobe = fak.getZaglavlje().getVrednostRobe().doubleValue();
		vrednostUsluga = fak.getZaglavlje().getVrednostUsluga().doubleValue();

		if (ukupnoRobeIUsluge != vrednostRobe + vrednostUsluga) {
			message = "Greska: Ukupna vrednost robe i usluga ne odgovara sumi vrednosti robe i usluga(zaglavlje fakture).";
			return false;
		}

		//
		else if (ukupnoRobeIUsluge != ukupnoStavke) {
			message = "Greska: Ukupna vrednost robe i usluga ne odgovara sumi vrednosti stavki.";
			return false;
		}

		else if (zaUplatu != ukupnoRobeIUsluge - ukupanRabat + ukupanPorez) {
			message = "Greska: Iznos za uplatu ne odgovara vrednosti robe sa porezom, umanjene za rabat(zaglavlje fakture).";
			return false;
		}

		else if (zaUplatu != zaUplatuStavke) {
			message = "Greska: Iznos za uplatu iz zaglavlja ne odgovara sumi vrednosti stavki sa porezom, umanjene za rabat";
			return false;
		}

		else if (ukupanPorez != ukupanPorezStavke) {
			message = "Greska: Ukupan porez iz zaglavlja ne odgovara sumi poreza iz stavki.";
			return false;
		}

		else if (ukupanRabat != ukupanRabatStavke) {
			message = "Greska: Ukupan rabat iz zaglavlja se ne odgovara sumi rabata iz stavki.";
			return false;
		}

		return true;

	}
}