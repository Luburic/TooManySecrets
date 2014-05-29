package ws.style.message;

import java.io.Reader;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
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
import util.DocumentTransform;
import util.Validation;
import beans.faktura.Faktura;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "FakturaPort", 
					serviceName = "FirmaServis",
					targetNamespace = "http://www.toomanysecrets.com/firmaServis",
					wsdlLocation = "WEB-INF/wsdl/Firma.wsdl")
public class FakturaProvider  implements Provider<DOMSource> {

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/firmaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	
	
	private Document message;
	
	public FakturaProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
    	try {
    		//ResourceBundle firmaBundle = ResourceBundle.getBundle("firmaA");
    		//String nazivFirme = firmaBundle.getString("naziv");
    		
			//serijalizacija DOM-a na ekran
    		System.out.println("\nInvoking FakturaProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			
			/*JAXBContext context = JAXBContext.newInstance("beans.faktura");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new URL(SCHEME_PATH));
			unmarshaller.setSchema(schema);*/
			
			SecurityClass security = new SecurityClass();
			Reader reader = Validation.createReader(document);
			Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/Faktura?xsd=../shema/FakturaCrypt.xsd","http://localhost:8080/ws_style/services/Faktura?xsd=xenc-schema.xsd"});
			
			if( doc == null )
				return new DOMSource(createResponse("Dokument nije validan po Crypt semi."));
			
			
			//treba da provalim kako da dobijem tu putanju posto za url nece da ga nadje :(
			String path = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\firmaa.jks";
			
			System.out.println("Pre dekriptovanja");
			DocumentTransform.printDocument(doc);
			
			
			//onaj properties file kojie je zakomentarisan na pocetku try bloka ce ovde uskociti
			Document decrypt = security.decrypt(doc, security.readPrivateKey("firmaa", "firmaa", path, "firmaa"));
			Reader reader1 = Validation.createReader(decrypt);
			decrypt = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/Faktura?xsd=../shema/FakturaSigned.xsd","http://localhost:8080/ws_style/services/Faktura?xsd=xmldsig-core-schema.xsd"});
			
			if( decrypt == null )
				return new DOMSource(createResponse("Dokument nije validan po Signed semi."));
			
			
			System.out.println("Posle dekriptovanja");
			DocumentTransform.printDocument(decrypt);
			
			if(!security.verifySignature(decrypt)) 
				return new DOMSource(createResponse("Dokument nije dobro potpisan."));
			
			
			//ovde ce ici provera za timestamp
			Element timestamp = (Element) decrypt.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
			String dateString = timestamp.getTextContent();
			//skidanje taga
			timestamp.getParentNode().removeChild(timestamp);
			
			//ovde ce ici provera za redni broj poruke
			Element redniBrojPoruke = (Element) decrypt.getElementsByTagNameNS(NAMESPACE_XSD, "redniBrojPoruke").item(0);
			//skidanje taga
			redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);
			
			//skidanje taga
			Element signature = (Element) decrypt.getElementsByTagName("ds:Signature").item(0);
			signature.getParentNode().removeChild(signature);
			System.out.println("Posle skidanja tagova************************************************************");
			DocumentTransform.printDocument(decrypt);
			
			Reader reader2 = Validation.createReader(decrypt);
			decrypt = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/Faktura?xsd=../shema/FakturaRaw.xsd"});
			
			if( decrypt == null )
				return new DOMSource(createResponse("Dokument nije validan po Raw semi."));
			
			/*
			Faktura faktura = (Faktura) unmarshaller.unmarshal(document);


			if(!validateContent(faktura)) {
				return new DOMSource(message);
			}
			*/
			
		
			//snimanje u bazu...
			
			
    	} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
      
    	return new DOMSource(createResponse("ok"));
	}

	
	 private Document createResponse(String notification) {
			
			DocumentBuilder documentBuilder = DocumentTransform.getDocumentBuilder();
			Document doc = documentBuilder.newDocument();
			
			Element rootEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:notif");
			rootEl.setAttributeNS(NAMESPACE_SPEC_NS, "xmlns:ns1", TARGET_NAMESPACE);
			doc.appendChild(rootEl);
			rootEl.appendChild(doc.createTextNode(notification));
			
			return doc;
		}
	
	
	
	
	/*private boolean validateSchema(Document document){
    	try{
	    	SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	    	Schema schema = factory.newSchema(new URL(SCHEME_PATH));
	    	Validator validator = schema.newValidator();
	    	validator.validate(new DOMSource(document));

    	}catch(Exception e){
    		System.out.println("Nije validna po shemi");
    		//e.printStackTrace();
    		return false;
    	}
    	
    	return true;
    }*/
	
	
	
	
	public boolean validateContent(Faktura fak) {
		boolean flag = true;
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
	}

}
