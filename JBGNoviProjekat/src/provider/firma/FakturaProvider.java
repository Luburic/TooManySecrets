package provider.firma;

import java.io.IOException;
import java.io.InputStream;
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

import client.firma.FakturaClient;
import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import beans.faktura.Faktura;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "FakturaPort", serviceName = "FirmaServis", targetNamespace = ConstantsXWS.TARGET_NAMESPACE_FIRMA, wsdlLocation = "WEB-INF/wsdl/Firma.wsdl")
public class FakturaProvider implements Provider<DOMSource> {

	private Properties propReceiver;
	private String message;
	private Document encrypted;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
	
	public FakturaProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		try {
			
			System.out.println("\nInvoking FakturaProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("firma.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);
			
			Element esender = (Element) document.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD, "sender").item(0);
			
			if(esender==null)
				return new DOMSource(encrypted);
			
			String sender = esender.getTextContent();
			System.out.println("****FAKTURA SENDER: "+sender);
			esender.getParentNode().removeChild(esender);
			
			
			
			Document decryptedDocument = MessageTransform.unpack(document, "Faktura", "Faktura",ConstantsXWS.TARGET_NAMESPACE_FIRMA, propReceiver, "firma", "Faktura");
			if(decryptedDocument==null){ 
				encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer"+sender,ConstantsXWS.NAMESPACE_XSD, "Notifikacija");
				return new DOMSource(encrypted);
			}
			
			
			//String sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();
			decryptedDocument = DocumentTransform.postDecryptTransform(decryptedDocument, propReceiver, "firma", "Faktura");
			
			if(decryptedDocument==null){ 
				DocumentTransform.createNotificationResponse("Greska nakon dekripcije.", ConstantsXWS.NAMESPACE_XSD);
				encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer"+sender,ConstantsXWS.NAMESPACE_XSD, "Notifikacija");
				return new DOMSource(encrypted);
				
			}
			
			JAXBContext context = JAXBContext.newInstance("beans.faktura");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Faktura faktura = (Faktura) unmarshaller.unmarshal(decryptedDocument);
			
			
			
			if (!validateContent(faktura)) {
				DocumentTransform.createNotificationResponse(message,ConstantsXWS.TARGET_NAMESPACE_FIRMA);
			}
			else {
				DocumentTransform.createNotificationResponse("Faktura uspesno obradjena.",ConstantsXWS.TARGET_NAMESPACE_FIRMA);
			}
			
			encrypted = MessageTransform.packS("Notifikacija", "Notification",apsolute, propReceiver, "cer"+sender,ConstantsXWS.NAMESPACE_XSD, "Notifikacija");
			
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

		
		if(fak == null) {
			message ="Greska:Neodgovarajuci tip dokumenta.";
			return false;
		}
		
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