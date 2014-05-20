package ws.style.client;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
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
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.w3c.dom.Document;

public class FirmaClient {
	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/firmaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";

    public void testIt() {
    	
		try {
			//Kreiranje web servisa (dispatcher-a)
			URL wsdlLocation = new URL("http://localhost:8080/ws_style/services/Firma?wsdl");
			QName serviceName = new QName("http://www.toomanysecrets.com/firmaServis","FirmaServis");
			QName portName = new QName("http://www.toomanysecrets.com/firmaServis","FirmaPort");
			
			Service service = Service.create(wsdlLocation, serviceName);
			
			Dispatch<DOMSource> dispatch = service.createDispatch(portName, DOMSource.class, Service.Mode.PAYLOAD);
			
			//Poziv webservisa (dispatcher-a)
			Document doc = buildFakturaRequest();
			
			DOMSource response = dispatch.invoke(new DOMSource(doc));
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(System.out);
            transformer.transform(response, result);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private Document buildFakturaRequest()  {
		
		DocumentBuilder documentBuilder = getDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		
		
		
		
		/*
		
		 * KREIRA SE XML:
		 * 
		 * <ns1:sayHello xmlns:ns1="http://informatika.ftn.ns.ac.yu/ws/style/message">
		 * 		<ns1:firstName>
		 * 			Ime
		 * 		</ns1:firstName>
		 * 		<ns1:lastName>
		 * 			Prezime
		 * 		</ns1:lastName>
		 * </ns1:sayHello>
		 
		
		Element rootEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:sayHello");
		//rootEl.setAttributeNS(NAMESPACE_SPEC_NS, "xmlns:ns1", TARGET_NAMESPACE);
		doc.appendChild(rootEl);
		
		
		Element firstNameEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:firstName");
		firstNameEl.appendChild(doc.createTextNode(firstName));
		rootEl.appendChild(firstNameEl);
		
		Element lastNameEl = doc.createElementNS(TARGET_NAMESPACE, "ns1:lastName");
		lastNameEl.appendChild(doc.createTextNode(lastName));
		rootEl.appendChild(lastNameEl);*/
		
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
	
	public static void main(String[] args) {
		
		FirmaClient client = new FirmaClient();
		client.testIt();
    }

}