package provider.banka;

import java.io.File;
import java.io.Reader;
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
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
import util.Validation;
import beans.mt103.MT103;
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
			DocumentTransform.printDocument(DocumentTransform.convertToDocument(request));
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			SecurityClass security = new SecurityClass();
			Reader reader = Validation.createReader(DocumentTransform.convertToDocument(request));
			Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/Rtgs?xsd=../shema/NalogCrypt.xsd","http://localhost:8080/ws_style/services/Rtgs?xsd=xenc-schema.xsd"});
			
			if( doc == null ) 
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Crypt semi.",TARGET_NAMESPACE));
			
			String path = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\bankaa.jks";

			
			Document decrypted = security.decrypt(doc, security.readPrivateKey("bankaa", "bankaa", path, "bankaa"));
			Reader reader1 = Validation.createReader(decrypted);
			
			decrypted = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/Rtgs?xsd=../shema/NalogSigned.xsd","http://localhost:8080/ws_style/services/Rtgs?xsd=xmldsig-core-schema.xsd"});
			
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
			decrypted = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/Rtgs?xsd=../shema/NalogRaw.xsd"});
			
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
				
				security.saveDocument(docum, "./MT103Test/MT103.xml");
				String inputFile =  "./MT103Test/MT103.xml";
				
				//potpisivanje od banke koja je primila nalog
				String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
				String alias="";
				String password="";
				String keystoreFile="";
				String keystorePassword="";
				Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT103Signed.xsd", "MT103");
				
				if(signed == null)
					return new DOMSource(DocumentTransform.createNotificationResponse("Greska u potpisivanju.",TARGET_NAMESPACE));
				
				Document encrypted = null;
				
				
				encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "MT103");
				
				if(encrypted == null)
					return new DOMSource(DocumentTransform.createNotificationResponse("Greska u enkripciji.",TARGET_NAMESPACE));
				
				security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
				//snimanje mt103 poruke za slanje u bazu.. 
				
				
			
				//pozivanje providera MT103 centralne banke
				try {
					//kreiranje servisa
					URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Banka?wsdl");
					QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
					QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "MT103Port");
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
					
					//snimanje u bazu primljenog zaduzenja od te centrale
					
					//skidanje para sa racuna firme
					
					
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


}
