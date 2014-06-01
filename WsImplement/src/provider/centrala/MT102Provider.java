package provider.centrala;

import java.io.File;
import java.io.Reader;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
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
import beans.mt900.MT900;
import beans.mt910.MT910;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "MT102Port", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class MT102Provider implements javax.xml.ws.Provider<DOMSource>{

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	private Marshaller marshaller;
	private Document encrypted = null;
	
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
		try {
		    		
    		System.out.println("\nInvoking MT102Provider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			
			
			
			SecurityClass security = new SecurityClass();
			Reader reader = Validation.createReader(document);
			Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT103Crypt.xsd","http://localhost:8080/ws_style/services/Banka?xsd=xenc-schema.xsd"});
			
			if( doc == null )
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Crypt semi.", TARGET_NAMESPACE));
			
			
			//treba da provalim kako da dobijem tu putanju posto za url nece da ga nadje :(
			String path = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\banka.jks";
		
			
			//onaj properties file kojie je zakomentarisan na pocetku try bloka ce ovde uskociti
			Document decrypt = security.decrypt(doc, security.readPrivateKey("bankaa", "bankaa", path, "bankaa"));
			Reader reader1 = Validation.createReader(decrypt);
			decrypt = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT103Signed.xsd","http://localhost:8080/ws_style/services/Banka?xsd=xmldsig-core-schema.xsd"});
			
			if( decrypt == null )
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Signed semi.",TARGET_NAMESPACE));
			
		
			
			if(!security.verifySignature(decrypt)) 
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije dobro potpisan.",TARGET_NAMESPACE));
		
		
			DOMSource timestampOk= Validation.validateTimestamp(TARGET_NAMESPACE, decrypt, "",0);
			if(timestampOk==null)
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument ne odgovara prema vremenu primanja.",TARGET_NAMESPACE));
			
			
			decrypt = DocumentTransform.convertToDocument(timestampOk);
			
			
			//skidanje taga
			Element signature = (Element) decrypt.getElementsByTagName("ds:Signature").item(0);
			signature.getParentNode().removeChild(signature);
			
			Reader reader2 = Validation.createReader(decrypt);
			decrypt = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT103Raw.xsd"});
			
			if( decrypt == null )
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Raw semi.",TARGET_NAMESPACE));
			
			
			
			JAXBContext context = JAXBContext.newInstance("beans.mt102");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//Schema schema = schemaFactory.newSchema(new URL(SCHEME_PATH));
			//unmarshaller.setSchema(schema);
			
			MT102 mt102 = (MT102) unmarshaller.unmarshal(decrypt);
			
			if(!validateContent(mt102))
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po sadrzaju.",TARGET_NAMESPACE));
			
			
			//sve je ok, MT102 se snimi u bazu centrale
			//kreira se zaduzenje kao odgovor
			
			MT900 mt900 = createMT900(mt102);
			JAXBContext con = JAXBContext.newInstance("beans.mt900");
			marshaller = con.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt900, new File("./MT900Test/MT900.xml"));
			
			
			
			Document docum = Validation.buildDocumentWithoutValidation("./MT900Test/MT900.xml");
			Element mt = (Element) docum.getElementsByTagName("MT102").item(0);
			mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			
			security.saveDocument(docum, "./MT900Test/MT900.xml");
			String inputFile =  "./MT900Test/MT900.xml";
			
			//potpisivanje od mt102 providera
			String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
			String alias="";
			String password="";
			String keystoreFile="";
			String keystorePassword="";
			Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT900Signed.xsd", "MT900");
			
			if( signed == null )
				return new DOMSource(DocumentTransform.createNotificationResponse("Greska u potpisivanju.",TARGET_NAMESPACE));
			
			
			
			encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "MT900");
			
			if(encrypted == null)
				return new DOMSource(DocumentTransform.createNotificationResponse("Greska u enkripciji.",TARGET_NAMESPACE));
			
			security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
			
			
			//snimanje mt900 za slanje
			createMT910(); //invoke odobrenje
			forwardMT102(); //invoke mt1023
			
			
			
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
		return new DOMSource(encrypted);
		}

	
	
	
	
		private MT900 createMT900(MT102 mt102){
			MT900 mt = new MT900();
			return mt;
			
		}
	
	
		
		private DOMSource createMT910(){
			return null;
		}
		
		
		private DOMSource forwardMT102(){
			return null;
		}
		
		public boolean validateContent(MT102 mt102){
			return true;
		}
		
}
