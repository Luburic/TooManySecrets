package provider.banka;



import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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
import util.NSPrefixMapper;
import util.Validation;
import beans.mt102.MT102;
import beans.mt102.MT102.PojedinacnaUplata;
import beans.mt102.MT102.ZaglavljeMT102;
import beans.mt900.MT900;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "NalogClearingPort", 
		serviceName = "BankaServis", 
		targetNamespace = "http://www.toomanysecrets.com/bankaServis", 
		wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class NalogClearingProvider implements Provider<DOMSource> {

	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	private Marshaller marshaller;
	
		public NalogClearingProvider() {
			// TODO Auto-generated constructor stub
		}
	
		@Override
		public DOMSource invoke(DOMSource request) {
			try {
				
				System.out.println("\nInvoking ClearingProvider\n");
				System.out.println("-------------------REQUEST MESSAGE----------------------------------");
				Document document =DocumentTransform.convertToDocument(request);
				DocumentTransform.printDocument(document);
				System.out.println("-------------------REQUEST MESSAGE----------------------------------");
				System.out.println("\n");
				
				Document decryptedDocument =MessageTransform.unpack(document, "Clearing", "Nalog", TARGET_NAMESPACE, null, null);

				
				JAXBContext context = JAXBContext.newInstance("beans.nalog");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				
			
				Nalog nalog=null;
				try {
					nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);
				} catch (Exception e) {
					return new DOMSource(decryptedDocument);
				}
				
				
				if(!validateContent(nalog))
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po sadrzaju.",TARGET_NAMESPACE));
				
				//sve ok, snimanje primljenog kliring naloga u bazu banke A(this)
				
				
				
				//ako ima 5 primljenih MT102naloga
				//kreiranje mt102 zahteva i poziv servisa centrale
				
				//if(bank.databaseKliringList.count() > 4) {
					//DOMSource resp = sendClearing(nalog);
						//if(resp!=null)	
							//return resp;
				//}
				
			}
			catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
				
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new DOMSource(DocumentTransform.createNotificationResponse("Nalog(kliring tipa)uspesno primljen.",TARGET_NAMESPACE));

	
		}
		
		public boolean validateContent(Nalog nalog) {
			return true;
		}
		
		
		
		public DOMSource sendClearing(Nalog nalog){
			
		try {
			MT102 mt102 = createMT102(nalog); 
			SecurityClass security = new SecurityClass();
			
			JAXBContext con = JAXBContext.newInstance("beans.mt102");
			marshaller = con.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt102, new File("./MT102Test/MT102.xml"));
			
			Document docum = Validation.buildDocumentWithoutValidation("./MT102Test/MT102.xml");
			Element mt = (Element) docum.getElementsByTagName("mt102").item(0);
			mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			
			security.saveDocument(docum, "./MT102Test/MT102.xml");
			String inputFile =  "./MT102Test/MT102.xml";
			
			
			String alias="";
			String password="";
			String keystoreFile="";
			String keystorePassword="";			
			
			Document encryptedDocument = MessageTransform.pack("/Clearing", "MT102", inputFile, alias, password, keystoreFile, keystorePassword, keystoreFile, keystorePassword);
			
			//if(encryptedDocument saved in database) {
			
					//pozivanje providera MT102 centralne banke
					try {
						//kreiranje servisa
						URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Banka?wsdl");
						QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
						QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "MT102Port");
						Service service = Service.create(wsdlLocation, serviceName);
						Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
						
						
						DOMSource response = dispatch.invoke(new DOMSource(encryptedDocument));
						
						
						
						
						//......................zaduzenje kao response....
						Document rdocument =DocumentTransform.convertToDocument(response);
						
						Document decryptedDocument = MessageTransform.unpack(rdocument, "Clearing", "Zaduzenje", TARGET_NAMESPACE, null, null);
						
						JAXBContext context = JAXBContext.newInstance("beans.mt900");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						
					
						MT900 zaduzenje=null;
						try {
							zaduzenje = (MT900) unmarshaller.unmarshal(decryptedDocument);
						} catch (Exception e) {
							return new DOMSource(decryptedDocument);
						}
						
						
						if(!validateContentZaduzenje(zaduzenje))
							return new DOMSource(DocumentTransform.createNotificationResponse("Doukment zaduzenje nije validan po sadrzaju.",TARGET_NAMESPACE));
						
						//snimanje zaduzenja primljenog od centrale u bazu banke				
						//skidanje para sa racuna firme
						
					}catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (TransformerFactoryConfigurationError e) {
						e.printStackTrace();	
					} 
			
			//ako je pri enkripciji - pozivu pack metode doslo do greske
			//}else {
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
	
		return new DOMSource(null);
			
		}
		
		
		public MT102 createMT102(Nalog nalog) {
			//banka treba da proveri u bazi da li ima kreirano zaglavlje kliringa
			//za bank destination iz naloga...ako ima ,koristi se postojece zaglavlje kliringa,
			//i dodaje se stavka pristiglog naloga..ako nema, kreira se novo zaglavlje  
			//kliringa za tu banku i dodaje stavka iz naloga
			MT102 mt102=null;
			boolean exist = false;
			
			/*for(MT102 mt: mt102DatabseList){
				ZaglavljeMT102 zag102 = mt102.getZaglavljeMT102();
				
				if(nalog.getRacunPoverioca().startsWith(zag102.getObracunskiRacunBankePoverioca().substring(0, 2))){
					mt102 = mt;
					exist = true;
					break;
				}
			}*/
			
			if(exist) {
				//PojedinacnaUplata pu = new PojedinacnaUplata();
				//pu.set...
				//pu.set...
				//pu.set...
				//mt102.getPojedinacnaUplata().add(pu);
				
			}else {
				PojedinacnaUplata pu = new PojedinacnaUplata();
				//pu.set...
				//pu.set...
				//pu.set...
				ZaglavljeMT102 z102 = new ZaglavljeMT102();
				//z102.set..
				//z102.set..
				//z102.set..
				mt102 = new MT102();
				mt102.setZaglavljeMT102(z102);
				mt102.getPojedinacnaUplata().add(pu);
			}
			
			
			return mt102;
		}
		
		
		
		private boolean validateContentZaduzenje(MT900 zaduzenje) {
			return true;
		}
}
