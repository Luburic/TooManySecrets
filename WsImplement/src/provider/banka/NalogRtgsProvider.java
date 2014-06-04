package provider.banka;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
import util.Validation;
import beans.mt103.MT103;
import beans.mt900.MT900;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "NalogRtgsPort", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class NalogRtgsProvider  implements Provider<DOMSource>{

	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	private Marshaller marshaller;
	
	
	public NalogRtgsProvider() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
		try {
			
			System.out.println("\nInvoking RtgsProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			Document decryptedDocument = MessageTransform.unpack(document, "Rtgs", "Nalog", TARGET_NAMESPACE, null);
			
			
			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			
		
			Nalog nalog=null;
			try {
				nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
			} catch (Exception e1) {
				return new DOMSource(decryptedDocument);
			}
			
			if(!validateContent(nalog))
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nalog nije validan po sadrzaju.",TARGET_NAMESPACE));
			
			//ako je validan, snimanje primljenog naloga u bazu
			
			
		
			//i formiranje MT103 zahteva za centralu..
				MT103 mt103 = createMT103(nalog); 
				JAXBContext con = JAXBContext.newInstance("beans.mt103");
				marshaller = con.createMarshaller();
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
				marshaller.marshal(mt103, new File("./MT103Test/MT103.xml"));
				
				Document docum = Validation.buildDocumentWithoutValidation("./MT103Test/MT103.xml");
				Element mt = (Element) docum.getElementsByTagName("MT103").item(0);
				mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
				
				SecurityClass security = new SecurityClass();
				security.saveDocument(docum, "./MT103Test/MT103.xml");
				String inputFile =  "./MT103Test/MT103.xml";
				
				
				String alias="";
				String password="";
				String keystoreFile="";
				String keystorePassword="";
				
				
				
				Document encryptedDocument = MessageTransform.pack("Rtgs", "MT103", inputFile, alias, password, keystoreFile, keystorePassword, keystoreFile, keystorePassword);
				
			
				//if(encryptedDocument exist in database) {
						//pozivanje providera MT103 centralne banke
						try {
							//kreiranje servisa
							URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Banka?wsdl");
							QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
							QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "MT103Port");
							Service service = Service.create(wsdlLocation, serviceName);
							Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
						
							
							
							
							DOMSource response = dispatch.invoke(new DOMSource(encryptedDocument));
							
							
							
							
							//......................zaduzenje kao response....
							Document rdocument =DocumentTransform.convertToDocument(response);
							Document decryptedDocument2 = MessageTransform.unpack(rdocument, "Rtgs", "Zaduzenje", TARGET_NAMESPACE, null);
							
							JAXBContext context2 = JAXBContext.newInstance("beans.mt900");
							Unmarshaller unmarshaller2 = context2.createUnmarshaller();
							
						
							MT900 zaduzenje=null;
							try {
								zaduzenje = (MT900) unmarshaller2.unmarshal(decryptedDocument2);
							} catch (Exception e) {
								return new DOMSource(decryptedDocument);
							}
							
							
							if(!validateContentZaduzenje(zaduzenje))
								return new DOMSource(DocumentTransform.createNotificationResponse("Doukment zaduzenje nije validan po sadrzaju.",TARGET_NAMESPACE));
							
							
							//snimanje u bazu primljenog zaduzenja od te centrale
							
							//skidanje para sa racuna firme
							
							
						}
						catch (MalformedURLException e) {
							e.printStackTrace();
						
						} catch (TransformerFactoryConfigurationError e) {
							e.printStackTrace();	
						} 
			
			//greska u enkripciji			
			//}else{
				//return new DOMSource(encryptedDocument);
			//}
				
				
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DOMSource(DocumentTransform.createNotificationResponse("Nalog(rtgs tipa)uspesno primljen i obradjen.",TARGET_NAMESPACE));

		
	}
	

	
	
	private MT103 createMT103(Nalog nalog) {
		
		MT103 mt = new MT103();
		mt.setIdPoruke(nalog.getIdPoruke());
		mt.setSwiftBankeDuznika("");
		mt.setObracunskiRacunBankeDuznika("");
		mt.setSwiftBankePoverioca("");
		mt.setObracunskiRacunBankePoverioca("");
		mt.setDuznik(nalog.getDuznikNalogodavac());
		mt.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
		mt.setPrimalac(nalog.getPrimalacPoverilac());
		mt.setDatumNaloga(nalog.getDatumNaloga());
		
		mt.setDatumValute(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
		mt.setRacunDuznika(nalog.getRacunDuznika());
		mt.setModelZaduzenja(nalog.getModelZaduzenja());
		mt.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		mt.setRacunPoverioca(nalog.getRacunPoverioca());
		mt.setModelOdobrenja(nalog.getModelOdobrenja());
		mt.setPozivNaBrojOdobrenja(String.valueOf(nalog.getPozivNaBrojOdobrenja()));
		mt.setIznos(nalog.getIznos());
		mt.setSifraValute(nalog.getOznakaValute());
		return mt;
	}
	
	
	public boolean validateContent(Nalog nalog) {
		return true;
	}

	public boolean validateContentZaduzenje(MT900 zaduzenje) {
		return true;
	}
}
