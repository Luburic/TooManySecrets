package provider.banka;



import java.io.File;
import java.io.Reader;
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
import util.NSPrefixMapper;
import util.Validation;
import beans.mt102.MT102;
import beans.mt102.MT102.PojedinacnaUplata;
import beans.mt102.MT102.ZaglavljeMT102;
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
				DocumentTransform.printDocument(DocumentTransform.convertToDocument(request));
				System.out.println("-------------------REQUEST MESSAGE----------------------------------");
				System.out.println("\n");
				
				SecurityClass security = new SecurityClass();
				Reader reader = Validation.createReader(DocumentTransform.convertToDocument(request));
				Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/Clearing?xsd=../shema/NalogCrypt.xsd","http://localhost:8080/ws_style/services/Clearing?xsd=xenc-schema.xsd"});
				
				if( doc == null ) 
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Crypt semi.",TARGET_NAMESPACE));
				
				String path = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\bankaa.jks";

				
				Document decrypted = security.decrypt(doc, security.readPrivateKey("bankaa", "bankaa", path, "bankaa"));
				Reader reader1 = Validation.createReader(decrypted);
				
				decrypted = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/Clearing?xsd=../shema/NalogSigned.xsd","http://localhost:8080/ws_style/services/Clearing?xsd=xmldsig-core-schema.xsd"});
				
				if( decrypted == null )
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Signed semi.",TARGET_NAMESPACE));
					
			
				
				if(!security.verifySignature(decrypted)) 
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije dobro potpisan.",TARGET_NAMESPACE));
					
				
				DOMSource timestampOK= Validation.validateTimestamp(TARGET_NAMESPACE, decrypted, "",0);
				
				if(timestampOK==null)
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument ne odgovara prema vremenu primanja.",TARGET_NAMESPACE));
				
				
				decrypted = DocumentTransform.convertToDocument(timestampOK);
				
				
				Element signature = (Element) decrypted.getElementsByTagName("ds:Signature").item(0);
				signature.getParentNode().removeChild(signature);
			
				
				Reader reader2 = Validation.createReader(decrypted);
				decrypted = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/Clearing?xsd=../shema/NalogRaw.xsd"});
				
				if( decrypted == null ) 
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Raw semi.",TARGET_NAMESPACE));
				
				
				
				
				
				
				JAXBContext context = JAXBContext.newInstance("beans.nalog");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				//Schema schema = schemaFactory.newSchema(new URL(SCHEME_PATH));
				//unmarshaller.setSchema(schema);
			
				Nalog nalog= (Nalog) unmarshaller.unmarshal(decrypted);
				
				if(!validateContent(nalog))
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po sadrzaju.",TARGET_NAMESPACE));
				
				//sve ok, snimanje primljenog kliring naloga u bazu banke A(this)
				
				
				
				//ako ima 5 primljenih MT102naloga
				//kreiranje mt102 zahteva i poziv servisa centrale
				
				//if(bank.databaseKliringList.count() > 4) {
					//DOMSource resp = callCentral(nalog);
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
		
		
		
		public DOMSource callCentral(Nalog nalog){
			
			MT102 mt102 = createMT102(nalog); 
			SecurityClass security = new SecurityClass();
			
		try {
			
			JAXBContext con = JAXBContext.newInstance("beans.mt102");
			marshaller = con.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt102, new File("./MT102Test/MT103.xml"));
			
			Document docum = Validation.buildDocumentWithoutValidation("./MT102Test/MT102.xml");
			Element mt = (Element) docum.getElementsByTagName("MT102").item(0);
			mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			
			security.saveDocument(docum, "./MT102Test/MT102.xml");
			String inputFile =  "./MT102Test/MT102.xml";
			
			//potpisivanje od banke A(this)
			String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
			String alias="";
			String password="";
			String keystoreFile="";
			String keystorePassword="";
			Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT102Signed.xsd", "MT102");
			
			if(signed == null)
				return new DOMSource(DocumentTransform.createNotificationResponse("Greska u potpisivanju.",TARGET_NAMESPACE));
			
			Document encrypted = null;
			
			
			encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "MT102");
			
			if(encrypted == null)
				return new DOMSource(DocumentTransform.createNotificationResponse("Greska u enkripciji.",TARGET_NAMESPACE));
			
			security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
		
			
			//pozivanje providera MT102 centralne banke
			try {
				//kreiranje servisa
				URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Banka?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
				QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "MT102Port");
				Service service = Service.create(wsdlLocation, serviceName);
				Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
				
				
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				
				
				
				
				//......................zaduzenje kao response....
				Document rdocument =DocumentTransform.convertToDocument(response);
				Reader reader3 = Validation.createReader(rdocument);
				Document doc2 = Validation.buildDocumentWithValidation(reader3,new String[]{ "http://localhost:8080/ws_style/services/Banka?xsd=../shema/ZaduzenjeCrypt.xsd","http://localhost:8080/ws_style/services/Banka?xsd=xenc-schema.xsd"});
				
				if( doc2 == null ) 
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Crypt semi.",TARGET_NAMESPACE));
				
				//treba da provalim kako da dobijem tu putanju posto za url nece da ga nadje :(
				String path2 = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\bankaa.jks";
				
				
				
				Document decrypt = security.decrypt(doc2, security.readPrivateKey("bankaa", "bankaa", path2, "bankaa"));
				Reader reader4 = Validation.createReader(decrypt);
				decrypt = Validation.buildDocumentWithValidation(reader4, new String[]{ "http://localhost:8080/ws_style/services/Banka?xsd=../shema/ZaduzenjeSigned.xsd","http://localhost:8080/ws_style/services/Banka?xsd=xmldsig-core-schema.xsd"});
				
				if( decrypt == null ) 
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Signed semi.",TARGET_NAMESPACE));
				
				
				if(!security.verifySignature(decrypt)) 
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije dobro potpisan.",TARGET_NAMESPACE));
				
				
			
				int dbRbr=0; //...redni broj poslednje primljenog zaduzenja od strane te centrale
				
				DOMSource timestampOK2= Validation.validateTimestamp(TARGET_NAMESPACE, decrypt, "",dbRbr);
				if(timestampOK2==null)
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument ne odgovara prema vremenu primanja.",TARGET_NAMESPACE));
				
				
				decrypt = DocumentTransform.convertToDocument(timestampOK2);
				
				
				Element signature2 = (Element) decrypt.getElementsByTagName("ds:Signature").item(0);
				signature2.getParentNode().removeChild(signature2);
			
				Reader reader5 = Validation.createReader(decrypt);
				decrypt = Validation.buildDocumentWithValidation(reader5, new String[]{ "http://localhost:8080/ws_style/services/Banka?xsd=../shema/ZaduzenjeRaw.xsd"});
				
				if( decrypt == null )
					return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Raw semi.",TARGET_NAMESPACE));
				
				//snimanje u bazu banke A(this) primljenog zaduzenja te centrale
				
				//skidanje para sa racuna firme koja je poslala nalog
				
			
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
				
			} 
			
		
		
		
		
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
				PojedinacnaUplata pu = new PojedinacnaUplata();
				//pu.set...
				//pu.set...
				//pu.set...
				mt102.getPojedinacnaUplata().add(pu);
				
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
}
