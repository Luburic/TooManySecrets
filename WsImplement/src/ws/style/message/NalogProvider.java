package ws.style.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;

import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
		return new DOMSource(createResponse("Nalog uspesno primljen. Obrada u toku.."));
		
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
	 
	
	private void createRequestToCb() {
		
	}

}
