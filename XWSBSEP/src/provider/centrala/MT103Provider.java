package provider.centrala;

import java.io.File;

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
import util.MessageTransform;
import util.NSPrefixMapper;
import util.Validation;
import beans.mt103.MT103;
import beans.mt900.MT900;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "MT103Port", 
					serviceName = "BankaServis",
					targetNamespace = "http://www.toomanysecrets.com/bankaServis",
					wsdlLocation = "WEB-INF/wsdl/Banka.wsdl")
public class MT103Provider implements javax.xml.ws.Provider<DOMSource>{

	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/bankaServis";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	public static final String NAMESPACE_XSD = "http://www.toomanysecrets.com/tipovi";
	private Marshaller marshaller;
	private Document encryptedDocument;
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
		try {
    		
    		System.out.println("\nInvoking MT103Provider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			//Document document =DocumentTransform.convertToDocument(request);
			//DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			
			
			//Document decryptedDocument =MessageTransform.unpack(document, "MT103", "MT103", TARGET_NAMESPACE, null);

			
			JAXBContext context = JAXBContext.newInstance("beans.mt103");
			Unmarshaller unmarshaller = context.createUnmarshaller();
		
			
			MT103 mt103=null;
			/*try {
				mt103 = (MT103) unmarshaller.unmarshal(decryptedDocument);
			} catch (Exception e) {
				return new DOMSource(decryptedDocument);
			}*/
			
			if(!validateContent(mt103))
				return new DOMSource(DocumentTransform.createNotificationResponse("Dokument nije validan po sadrzaju.",TARGET_NAMESPACE));
			
			
			//sve je ok, MT103 se snimi u bazu centrale
			
			
			
			//kreira se zaduzenje kao odgovor
			
			MT900 mt900 = createMT900(mt103);
			JAXBContext con = JAXBContext.newInstance("beans.mt900");
			marshaller = con.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt900, new File("./MT900Test/MT900.xml"));
			
			
			
			Document docum = Validation.buildDocumentWithoutValidation("./MT900Test/MT900.xml");
			Element mt = (Element) docum.getElementsByTagName("MT900").item(0);
			mt.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			
			
			SecurityClass security = new SecurityClass();
			security.saveDocument(docum, "./MT900Test/MT900.xml");
			String inputFile =  "./MT900Test/MT900.xml";
			
			
			String alias="";
			String password="";
			String keystoreFile="";
			String keystorePassword="";
			
			encryptedDocument = MessageTransform.pack("MT103", "MT900", inputFile, alias, password, keystoreFile, keystorePassword, TARGET_NAMESPACE, NAMESPACE_XSD);

			//ako je encryptedDocument uspesno snimljen u bazu, znaci da je pack uspesno izvrsen
			//if(encryptedDocument exist in centralaDatabase){
				forwardMT103(); 
				createMT910(); 
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
		return new DOMSource(encryptedDocument);
		
	}
	
	
	
	
	private MT900 createMT900(MT103 mt103){
		MT900 mt = new MT900();
		mt.setIdPorukeNaloga(mt103.getIdPoruke());
		mt.setDatumValute(mt103.getDatumValute());
		mt.setIdPoruke("");
		mt.setIznos(mt103.getIznos());
		mt.setObracunskiRacunBankeDuzinka(mt103.getObracunskiRacunBankeDuznika());
		mt.setSifraValute(mt103.getSifraValute());
		mt.setSwiftBankeDuznika("");
		return mt;
		
	}
	
	private DOMSource createMT910(){
		return null;
	}
	
	
	private DOMSource forwardMT103(){
		return null;
	}
	
	
	
	public boolean validateContent(MT103 mt103){
		return true;
	}
	
}


