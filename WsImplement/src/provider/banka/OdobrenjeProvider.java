package provider.banka;

import java.io.Reader;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
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
import beans.mt910.MT910;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "OdobrenjePort", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class OdobrenjeProvider implements Provider<DOMSource>{

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";

	@Override
	public DOMSource invoke(DOMSource request) {
		// TODO Auto-generated method stub
		try {
    		
    		System.out.println("\nInvoking OdobrenjeProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			
			SecurityClass security = new SecurityClass();
			Reader reader = Validation.createReader(DocumentTransform.convertToDocument(request));
			Document doc = Validation.buildDocumentWithValidation(reader,new String[]{ "http://localhost:8080/ws_style/services/Odobrenje?xsd=../shema/MT910Crypt.xsd","http://localhost:8080/ws_style/services/Odobrenje?xsd=xenc-schema.xsd"});
			
			if( doc == null ) 
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Crypt semi.",TARGET_NAMESPACE));
			
			String path = "C:\\apache-tomee-plus-1.5.0\\webapps\\ws_style\\keystores\\bankaa.jks";

			
			Document decrypted = security.decrypt(doc, security.readPrivateKey("bankaa", "bankaa", path, "bankaa"));
			Reader reader1 = Validation.createReader(decrypted);
			
			decrypted = Validation.buildDocumentWithValidation(reader1, new String[]{ "http://localhost:8080/ws_style/services/Odobrenje?xsd=../shema/MT910Signed.xsd","http://localhost:8080/ws_style/services/Odobrenje?xsd=xmldsig-core-schema.xsd"});
			
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
			decrypted = Validation.buildDocumentWithValidation(reader2, new String[]{ "http://localhost:8080/ws_style/services/Odobrenje?xsd=../shema/MT910Raw.xsd"});
			
			if( decrypted == null ) 
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po Raw semi.",TARGET_NAMESPACE));
			
			JAXBContext context = JAXBContext.newInstance("beans.mt910");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			MT910 mt910 = (MT910) unmarshaller.unmarshal(decrypted);
			
			if(!validateContent(mt910))
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po sadrzaju.",TARGET_NAMESPACE));
			
			
			//sve je ok, mt910 se snimi u bazu
			//kreira se notification kao odgovor
			
			
			
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
		return new DOMSource(DocumentTransform.createNotificationResponse("Primljeno odobrenje.",TARGET_NAMESPACE));
		}

	private boolean validateContent(MT910 mt910) {
		// TODO Auto-generated method stub
		return true;
	}
	

}
