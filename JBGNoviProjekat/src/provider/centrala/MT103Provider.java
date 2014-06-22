package provider.centrala;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
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
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.NSPrefixMapper;
import util.Validation;
import basexdb.RESTUtil;
import beans.mt103.MT103;
import beans.mt900.MT900;
import beans.mt910.MT910;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "CentralnaRTGSNalogPort", serviceName = "CentralnaRTGSNalog", targetNamespace = "http://www.toomanysecrets.com/CentralnaRTGSNalog", wsdlLocation = "WEB-INF/wsdl/CentralnaRTGSNalog.wsdl")
public class MT103Provider implements javax.xml.ws.Provider<DOMSource> {

	private Marshaller marshaller;
	private Document encryptedDocument;
	private Properties propReceiver;
	private String message;
	

	public MT103Provider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public DOMSource invoke(DOMSource request) {

		try {

			System.out.println("\nInvoking MT103Provider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");// /
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			Element esender = (Element) document.getElementsByTagName("mt103").item(0);
			String sender = esender.getAttribute("sender");
			Document decryptedDocument = MessageTransform.unpack(document,"MT103", "MT103",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG, propReceiver,"banka", "MT103"+sender);

			Reader reader = Validation.createReader(decryptedDocument);
			Document forSave = Validation.buildDocumentWithValidation(reader,new String[] { "http://localhost:8080/MT103Signed.xsd",
							"http://localhost:8080/xmldsig-core-schema.xsd" });

			Document valid = Validation.buildDocumentWithoutValidation(DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6));
			
			Element notification = (Element) valid.getElementsByTagName("notification").item(0);
			boolean flag = false;

			if (notification.getChildNodes().item(0).getTextContent().contains("MT103"))
				flag = true;

			if (flag == false) {
				Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD,"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

				Element root = (Element) decryptedDocument.getElementsByTagName("mt103").item(0);
				root.removeAttribute("sender");
				RESTUtil.sacuvajEntitet(forSave,propReceiver.getProperty("naziv"), false, sender, date,"MT103"+sender, "banka");

				JAXBContext context = JAXBContext.newInstance("beans.mt103");
				Unmarshaller unmarshaller = context.createUnmarshaller();

				MT103 mt103 = (MT103) unmarshaller.unmarshal(decryptedDocument);

				if(validateContent(mt103)) {
					MT900 mt900 = createMT900(mt103);
					if(mt900!=null) {
					encryptedDocument = MessageTransform.packS("MT900", "MT900","./MT900Test/mt900.xml", propReceiver, "cer"+sender,ConstantsXWS.NAMESPACE_XSD, "MT900"+sender);
					//poziv servisa koji prima odobrenje i servisa koji prima mt102-3
					}
				}
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DOMSource(encryptedDocument);

	}

	private MT900 createMT900(MT103 mt103) {
		MT900 mt=null;
		try {
			mt = new MT900();
			mt.setIdPorukeNaloga(mt103.getIdPoruke());
			mt.setDatumValute(mt103.getDatumValute());
			mt.setIdPoruke("");
			mt.setIznos(mt103.getIznos());
			mt.setObracunskiRacunBankeDuzinka(mt103.getObracunskiRacunBankeDuznika());
			mt.setSifraValute(mt103.getSifraValute());
			mt.setSwiftBankeDuznika("");
			

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt, new File("./MT900Test/mt900.xml"));
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mt;

	}

	private MT910 createMT910() {
		return new MT910();
	}

	private DOMSource forwardMT103() {
		return null;
	}

	public boolean validateContent(MT103 mt103) {
		message = "";
		return true;
	}
	
	
	
	private class MT103Client {
		/* JAXBContext con =JAXBContext.newInstance("beans.mt900"); 
					  marshaller =con.createMarshaller(); 
					  marshaller.setProperty(
					  "com.sun.xml.bind.namespacePrefixMapper",new
					  NSPrefixMapper());
					  marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT
					  ,Boolean.TRUE); marshaller.marshal(mt900, new
					  File("./MT900Test/MT900.xml"));
					  
					  
					  
					  Document docum =
					  Validation.buildDocumentWithoutValidation
					  ("./MT900Test/MT900.xml"); Element mt = (Element)
					  docum.getElementsByTagName("MT900").item(0);
					  mt.setAttribute
					  ("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance"
					  );
					  
					  
					  SecurityClass security = new SecurityClass();
					  security.saveDocument(docum, "./MT900Test/MT900.xml");
					  String inputFile = "./MT900Test/MT900.xml";
					  
					  
					  String alias=""; String password=""; String
					  keystoreFile=""; String keystorePassword="";
					  
					 encryptedDocument = MessageTransform.pack("MT103",
					 "MT900", inputFile, alias, password, keystoreFile,
					  keystorePassword, TARGET_NAMESPACE, NAMESPACE_XSD);
					  
					  //ako je encryptedDocument uspesno snimljen u bazu, znaci
					  da je pack uspesno izvrsen //if(encryptedDocument exist
					  in centralaDatabase){ forwardMT103(); createMT910(); //}*/
	}

}
