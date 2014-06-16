package provider.firma;

import java.io.IOException;
import java.io.InputStream;
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
import basexdb.RESTUtil;
import beans.faktura.Faktura;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "FakturaPort", serviceName = "FirmaServis", targetNamespace = ConstantsXWS.TARGET_NAMESPACE_FIRMA, wsdlLocation = "WEB-INF/wsdl/Firma.wsdl")
public class FakturaProvider implements Provider<DOMSource> {
	
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	
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

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("/firma.properties");
			Properties propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			Document decryptedDocument = MessageTransform.unpack(document, "Faktura", "Faktura",
					ConstantsXWS.TARGET_NAMESPACE_FIRMA, propReceiver, "firma", "Faktura");
			
			Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
			String dateString = timestamp.getTextContent();

			Date date = null;
			try {
				date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

			RESTUtil.sacuvajEntitet(decryptedDocument, propReceiver.getProperty("naziv"), false, sender, date, "Faktura", "firma");
			
			decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument);
			decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument);
			decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
			
			//radi proveru po sadrzaju

			JAXBContext context = JAXBContext.newInstance("beans.faktura");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Faktura faktura = null;
			try {
				faktura = (Faktura) unmarshaller.unmarshal(decryptedDocument);
			} catch (JAXBException e) {
				return new DOMSource(decryptedDocument);
			}

			if (!validateContent(faktura)) {
				// Radi sa greškom.
			}
			// snimanje u bazu...
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
		return new DOMSource(DocumentTransform.createNotificationResponse("Faktura uspesno obradjena.",
				ConstantsXWS.TARGET_NAMESPACE_FIRMA));
	}

	public boolean validateContent(Faktura fak) {
		/*boolean flag = true;
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
			//tempPorez = stavka.getPorez().doubleValue();

			if (tempKolicina * tempJedinicnaCena != tempVrednost) {
				message = createResponse("GRE�KA: Vrednost stavke "+ stavka.getRedniBroj()+ ". ne odgovara proizvodu koli�ine i jedini�ne cene.");
				flag = false;
				return flag;
			}

			else if (tempVrednost * tempProcenatRabata / 100 != tempIznosRabata) {
				message = createResponse( "GRE�KA: Vrednost rabata stavke "+ stavka.getRedniBroj()+ ". ne odgovara procentu rabata ukupne vrednosti.");
				flag = false;
				return flag;
			}

			else if (tempUmanjenoZaRabat != tempVrednost - tempIznosRabata) {
				message = createResponse("GRE�KA: Vrednost umanjena za rabat stavke "+ stavka.getRedniBroj()+ ". ne odgovara ukupnoj vrednosti umanjenoj za rabat.");

				flag = false;
				return flag;

			}

			ukupnoStavke += tempVrednost;
			//zaUplatuStavke += tempUmanjenoZaRabat + tempPorez;
			//ukupanPorezStavke += tempPorez;
			ukupanRabatStavke += tempIznosRabata;
		}

		ukupnoRobeIUsluge = fak.getZaglavlje().getUkupnoRobaIUsluge().doubleValue();
		zaUplatu = fak.getZaglavlje().getIznosZaUplatu().doubleValue();
		ukupanPorez = fak.getZaglavlje().getUkupanPorez().doubleValue();
		ukupanRabat = fak.getZaglavlje().getUkupanRabat().doubleValue();
		vrednostRobe = fak.getZaglavlje().getVrednostRobe().doubleValue();
		vrednostUsluga = fak.getZaglavlje().getVrednostUsluga().doubleValue();

		if (ukupnoRobeIUsluge != vrednostRobe + vrednostUsluga) {
			message = createResponse("GRE�KA: Ukupna vrednost robe i usluga u zaglavlju se ne sla�e sa zbirom vrednosti robe i usluga iz zaglavlja.");
			flag = false;
			return flag;
		}

		else if (ukupnoRobeIUsluge != ukupnoStavke) {
			message = createResponse("GRE�KA: Ukupna vrednost robe i usluga u zaglavlju je razli�ita od zbira vrednosti stavki.");
			flag = false;
			return flag;
		}

		else if (zaUplatu != ukupnoRobeIUsluge - ukupanRabat + ukupanPorez) {
			message = createResponse("GRE�KA: Iznos za uplatu se ne sla�e sa ukupnom vredno��u robe sa porezom umanjene za rabat.");
			flag = false;
			return flag;
		}

		else if (zaUplatu != zaUplatuStavke) {
			message = createResponse("GRE�KA: Iznos za uplatu iz zaglavlja se ne sla�e sa zbirom stavki posle odbijanja rabata i dodavanja poreza.");
			flag = false;
			return flag;
		}

		else if (ukupanPorez != ukupanPorezStavke) {
			message = createResponse("GRE�KA: Ukupan porez iz zaglavlja se ne sla�e sa zbirom poreza iz stavki.");
			flag = false;
			return flag;
		}

		else if (ukupanRabat != ukupanRabatStavke) {
			message = createResponse("GRE�KA: Ukupan rabat iz zaglavlja se ne sla�e sa zbirom rabata iz stavki.");
			flag = false;
			return flag;
		}


		message = createResponse("Faktura je popunjena bez gre�aka.");
		return flag;

		 */
		return true;
	}
}