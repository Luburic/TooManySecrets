package ws.style.message;

import java.io.File;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
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
import beans.mt103.MT103;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "NalogPort", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class NalogProvider  implements Provider<DOMSource>{

	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	private Marshaller marshaller;
	
	
	public NalogProvider() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
		try {
			//serijalizacija DOM-a na ekran
			System.out.println("\nInvoking FakturaProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			SecurityClass security = new SecurityClass();
			Reader reader = Validation.createReader(document);
			
			Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/Nalog?xsd=../shema/NalogCrypt.xsd","http://localhost:8080/ws_style/services/Nalog?xsd=xenc-schema.xsd"});
			
			if( doc == null ) {
				Document response = createResponse("Dokument nije validan po Crypt semi.");
				return new DOMSource(response);
				
			}
			
			String path = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\firmaa.jks";

			
			Document decrypted = security.decrypt(doc, security.readPrivateKey("firmaa", "firmaa", path, "firmaa"));
			Reader reader1 = Validation.createReader(decrypted);
			
			decrypted = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/Nalog?xsd=../shema/NalogSigned.xsd","http://localhost:8080/ws_style/services/Nalog?xsd=xmldsig-core-schema.xsd"});
			
			if( decrypted == null ) {
				Document response = createResponse("Dokument nije validan po Signed semi.");
				return new DOMSource(response);
				
			}
			
			if(!security.verifySignature(decrypted)) {
				Document response = createResponse("Dokument nije dobro potpisan.");
				return new DOMSource(response);
				
			}
			
			
			Element timestamp = (Element) decrypted.getElementsByTagNameNS(NAMESPACE_XSD, "timestamp").item(0);
			String dateString = timestamp.getTextContent();
			//provera ok
			timestamp.getParentNode().removeChild(timestamp);
			
			
			
			Element redniBrojPoruke = (Element) decrypted.getElementsByTagNameNS(NAMESPACE_XSD, "redniBrojPoruke").item(0);
			//provera ok
			redniBrojPoruke.getParentNode().removeChild(redniBrojPoruke);
			
			Element signature = (Element) decrypted.getElementsByTagName("ds:Signature").item(0);
			//provera ok
			signature.getParentNode().removeChild(signature);
			
			
			Reader reader2 = Validation.createReader(decrypted);
			decrypted = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/Nalog?xsd=../shema/NalogRaw.xsd"});
			if( decrypted == null ) {
				Document raw = createResponse("Dokument nije validan po Raw semi.");
				return new DOMSource(raw);
				
			}
			
			
			
			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			//SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			//Schema schema = schemaFactory.newSchema(new URL(SCHEME_PATH));
			//unmarshaller.setSchema(schema);
		
			Nalog nalog= (Nalog) unmarshaller.unmarshal(decrypted);
			if(!validateContent(nalog)) {
				Document response = createResponse("Dokument nije validan sadrzaju.");
				return new DOMSource(response);
				
			}
			
			
			
		
			
			BigDecimal limit = new BigDecimal(250000.00);
			
			int res =nalog.getIznos().compareTo(limit);
			
			
			
			if(nalog.isHitno() || res != -1) {
			
				MT103 mt103 = createMT103();
				JAXBContext con = JAXBContext.newInstance("beans.mt103");
				marshaller = con.createMarshaller();
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
				marshaller.marshal(mt103, new File("./MT103Test/MT103.xml"));
				
				Document docum = Validation.buildDocumentWithoutValidation("./MT103Test/MT103.xml");
				Element mt = (Element) docum.getElementsByTagName("MT103").item(0);
				mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
				
				SecurityClass sc = new SecurityClass();
				sc.saveDocument(docum, "./MT103Test/MT103.xml");
				String inputFile =  "./MT103Test/MT103.xml";
				
				//potpisivanje od banke
				String outputFile = inputFile.substring(0, inputFile.length()-4) + "-signed.xml";
				String alias="";
				String password="";
				String keystoreFile="";
				String keystorePassword="";
				Document signed = security.addTimestampAndSign(alias, password, keystoreFile, keystorePassword, inputFile, outputFile, 0, " http://localhost:8080/ws_style/services/Banka?xsd=../shema/MT103Signed.xsd", "mt103");
				
				if( signed == null )
					return new DOMSource(createResponse("Greska u potpisivanju."));
				
				Document encrypted = null;
				
				
				encrypted = security.encrypt(signed, SecurityClass.generateDataEncryptionKey(), security.readCertificate(alias, password, keystoreFile, keystorePassword),NAMESPACE_XSD, "mt103");
				
				if(encrypted == null)
					return new DOMSource(createResponse("Greska u enkripciji."));
				
				security.saveDocument(encrypted, inputFile.substring(0, inputFile.length()-4) + "-crypted.xml");
				
				
				
			//pozivanje operacije servisa centrale
				
				try {
					
					URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Banka?wsdl");
					QName serviceName = new QName("http://www.toomanysecrets.com/bankaServis", "BankaServis");
					QName portName = new QName("http://www.toomanysecrets.com/bankaServis", "MT103Port");
					Service service = Service.create(wsdlLocation, serviceName);
					Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
				
					DOMSource response = dispatch.invoke(new DOMSource(encrypted));
					//poruka o zaduzenje response
					//skini pare sa racuna firme
					return new DOMSource(createResponse("Uspesno poslat i obradjen nalog."));
					
				}
				catch (MalformedURLException e) {
					e.printStackTrace();
				
				} catch (TransformerFactoryConfigurationError e) {
					e.printStackTrace();
					
				} 
				
				
		}
			
			
			
			
			
			
			else {
				createMT102();
				
				
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
	
	private Document createResponse(String notification) {
		
		DocumentBuilder documentBuilder = DocumentTransform.getDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		
		Element rootEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:notif");
		rootEl.setAttributeNS(NAMESPACE_SPEC_NS, "xmlns:ns1", TARGET_NAMESPACE);
		doc.appendChild(rootEl);
		rootEl.appendChild(doc.createTextNode(notification));
		
		return doc;
	}
	 
	
	private MT102 createMT102() {
		return null;
	}
	
	private MT103 createMT103() {
		return null;
	}
	
	
	public boolean validateContent(Nalog nalog) {
		return true;
	}


}
