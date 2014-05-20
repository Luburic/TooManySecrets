package ws.style.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ejb.Stateless;
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
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "HelloMessagePort", 
					serviceName = "HelloMessageService",
					targetNamespace = "http://informatika.ftn.ns.ac.yu/ws/style/message",
					wsdlLocation = "WEB-INF/wsdl/HelloMessage.wsdl")
public class HelloMessageProvider implements Provider<DOMSource> {
	
	public static final String TARGET_NAMESPACE = "http://informatika.ftn.ns.ac.yu/ws/style/message";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";

    public HelloMessageProvider() {
    }

    public DOMSource invoke(DOMSource request) {
    	DOMSource response = null;
    	try {
			//serijalizacija DOM-a na ekran
    		System.out.println("\nInvoking HelloMessageProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(System.out);
			transformer.transform(request, result);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			// preuzmi tekstualni sadrzaj elemenata firstName i lastName
			Document requestObject = convertToDocument(request);
			String firstName = requestObject.getElementsByTagNameNS(TARGET_NAMESPACE, "firstName").item(0).getTextContent();
			String lastName = requestObject.getElementsByTagNameNS(TARGET_NAMESPACE, "lastName").item(0).getTextContent();
			
			System.out.println("FIRSTNAME: " + firstName);
			System.out.println("LAST NAME: " + lastName);
			System.out.println("\n");
			//kreiranje odgovora
			System.out.println("-------------------RESPONSE MESSAGE----------------------------------");
			Document doc = buildHelloResponse(firstName, lastName);
			transformer.transform(new DOMSource(doc), result);
			System.out.println("-------------------RESPONSE MESSAGE----------------------------------");
			
			response = new DOMSource(doc);
			
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
		}
        return response;
    }
    
    private Document buildHelloResponse(String firstName, String lastName) {
		
		DocumentBuilder documentBuilder = getDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		
		/*
		 * KREIRA SE XML
		 * 
		 * <ns1:sayHelloResponse xmlns:ns1="http://informatika.ftn.ns.ac.yu/ws/style/message">
		 * 		Hello firstName lastName
		 * </ns1:sayHelloResponse>
		 * 
		 * 
		 */
		
		Element rootEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:sayHelloResponse");
		rootEl.setAttributeNS(NAMESPACE_SPEC_NS, "xmlns:ns1", TARGET_NAMESPACE);
		doc.appendChild(rootEl);
		
		rootEl.appendChild(doc.createTextNode("Hello " + firstName + " " + lastName));
		
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
    
}
