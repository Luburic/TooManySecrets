package ws.style.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import beans.faktura.Faktura;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "FakturaPort", 
					serviceName = "FirmaServis",
					targetNamespace = "http://www.toomanysecrets.com/firmaServis",
					wsdlLocation = "WEB-INF/wsdl/Firma.wsdl")
public class FakturaProvider  implements Provider<DOMSource>{

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/firmaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String SCHEME_PATH = "http://localhost:8080/firma/services/Faktura?xsd=../shema/FakturaRaw.xsd";
	private DOMSource response;
	
	public FakturaProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DOMSource invoke(DOMSource request) {
		response = null;
		
    	try {
    		ResourceBundle firmaBundle = ResourceBundle.getBundle("firmaA");
    		String nazivFirme = firmaBundle.getString("naziv");
    		
			//serijalizacija DOM-a na ekran
    		System.out.println("\nInvoking FakturaProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document = convertToDocument(request);
			printDocument(document, System.out);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			
			JAXBContext context = JAXBContext.newInstance("beans.faktura");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new URL(SCHEME_PATH));
			unmarshaller.setSchema(schema);
			
			if(!validateSchema(document)){
				response = createResponse("Dokument nije validan po šemi.");
				return response;
			}
			Faktura faktura = (Faktura) unmarshaller.unmarshal(document);


			if(!validateContent(faktura)) {
				return response;
			}
			
			//snimanje u bazu...
			
    	} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return response;
	}

	
	 private Document buildNotification(String notification) {
			
			DocumentBuilder documentBuilder = getDocumentBuilder();
			Document doc = documentBuilder.newDocument();
			
			/*
			 * KREIRA SE XML
			 * 
			 * <ns1:strOutput xmlns:ns1=TARGET_NAMESPACE>
			 * 		OK/FAIL
			 * </ns1:strOutput>
			 * 
			 * 
			 */
			
			Element rootEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:strOutput");
			rootEl.setAttributeNS(NAMESPACE_SPEC_NS, "xmlns:ns1", TARGET_NAMESPACE);
			doc.appendChild(rootEl);
			
			rootEl.appendChild(doc.createTextNode(notification));
			
			return doc;
		}
	
	
	private DocumentBuilder getDocumentBuilder() {
		try {
			// Setup document builder
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);

			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			return builder;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
 
	public Document convertToDocument(DOMSource request) {
	    Document r = null;
	    try{
	    	DocumentBuilder db = getDocumentBuilder();
	    	Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "no");
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        StreamResult result = new StreamResult(baos);
	        transformer.transform(request, result);
	        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	        r = db.parse(bais);
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    return r;
	}
	
	
	public static void printDocument(Document doc, OutputStream out)
			throws IOException, TransformerException {
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		//transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		//transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		//transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
	}
	
	
	private boolean validateSchema(Document document){
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
    }
	
	
	private DOMSource createResponse(String message) {

		
			DOMSource response = null;
			try {
				DocumentBuilder documentBuilder = getDocumentBuilder();
				Document document = documentBuilder.newDocument();

				Element notif = document.createElementNS(TARGET_NAMESPACE, "notif");

				notif.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
				notif.setAttribute("xsi:schemaLocation",
								"http://www.informatika.ftn.uns.ac.rs/12750/fakturaService http://localhost:8080/firma/services/Faktura?xsd=../shema/FakturaRaw.xsd");
				document.appendChild(notif);

				Element responseContent = document.createElement("responseContent");
				responseContent.setTextContent(message);
				responseContent.setAttribute("xmlns", "");

				notif.appendChild(responseContent);

				response = new DOMSource(document);
			} catch (DOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return response;
		
	}
	
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
				response = createResponse("GREŠKA: Vrednost stavke "+ stavka.getRedniBroj()+ ". ne odgovara proizvodu kolièine i jediniène cene.");
				flag = false;
				return flag;
			}

			else if (tempVrednost * tempProcenatRabata / 100 != tempIznosRabata) {
				response = createResponse( "GREŠKA: Vrednost rabata stavke "+ stavka.getRedniBroj()+ ". ne odgovara procentu rabata ukupne vrednosti.");
				flag = false;
				return flag;
			}

			else if (tempUmanjenoZaRabat != tempVrednost - tempIznosRabata) {
				response = createResponse("GREŠKA: Vrednost umanjena za rabat stavke "+ stavka.getRedniBroj()+ ". ne odgovara ukupnoj vrednosti umanjenoj za rabat.");
				
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
			response = createResponse("GREŠKA: Ukupna vrednost robe i usluga u zaglavlju se ne slaže sa zbirom vrednosti robe i usluga iz zaglavlja.");
			flag = false;
			return flag;
		}

		else if (ukupnoRobeIUsluge != ukupnoStavke) {
			response = createResponse("GREŠKA: Ukupna vrednost robe i usluga u zaglavlju je razlièita od zbira vrednosti stavki.");
			flag = false;
			return flag;
		}

		else if (zaUplatu != ukupnoRobeIUsluge - ukupanRabat + ukupanPorez) {
			response = createResponse("GREŠKA: Iznos za uplatu se ne slaže sa ukupnom vrednošæu robe sa porezom umanjene za rabat.");
			flag = false;
			return flag;
		}

		else if (zaUplatu != zaUplatuStavke) {
			response = createResponse("GREŠKA: Iznos za uplatu iz zaglavlja se ne slaže sa zbirom stavki posle odbijanja rabata i dodavanja poreza.");
			flag = false;
			return flag;
		}

		else if (ukupanPorez != ukupanPorezStavke) {
			response = createResponse("GREŠKA: Ukupan porez iz zaglavlja se ne slaže sa zbirom poreza iz stavki.");
			flag = false;
			return flag;
		}

		else if (ukupanRabat != ukupanRabatStavke) {
			response = createResponse("GREŠKA: Ukupan rabat iz zaglavlja se ne slaže sa zbirom rabata iz stavki.");
			flag = false;
			return flag;
		}

		
		response = createResponse("Faktura je popunjena bez grešaka.");
		return flag;
	}

}
