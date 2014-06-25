package provider.centrala;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
import util.Validation;
import basexdb.centralna.CentralnaSema;
import basexdb.util.CentralnaDBUtil;
import beans.mt103.MT103;
import beans.mt900.MT900;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "CentralnaRTGSNalogPort", 
					serviceName = "CentralnaRTGSNalog",
					targetNamespace = "http://www.toomanysecrets.com/CentralnaRTGSNalog",
					wsdlLocation = "WEB-INF/wsdl/CentralnaRTGSNalog.wsdl")
public class MT103Provider implements javax.xml.ws.Provider<DOMSource>{

	private Document encryptedDocument;
	private CentralnaSema semaBanka;
	private Properties propReceiver;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("mt900.xml").toString().substring(6);
	
	public MT103Provider() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public DOMSource invoke(DOMSource request) {
		
		try {
    		
    		System.out.println("\nInvoking MT103Provider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			Document document =DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");
			
			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);
			
			semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
			
			Element esender = (Element) document.getElementsByTagName("MT103").item(0);
			String sender = esender.getAttribute("sender");
			
			Document decryptedDocument = MessageTransform.unpack(document,"MT103", "MT103", ConstantsXWS.NAMESPACE_XSD_MT103, propReceiver, "banka", "MT103");

			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/MT103Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}

			if (forSave != null) {
			
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

				
				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT103);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT103);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
				
				
				DocumentTransform.printDocument(decryptedDocument);
			
				JAXBContext context = JAXBContext.newInstance("beans.mt103");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				MT103 mt103 = (MT103) unmarshaller.unmarshal(decryptedDocument);
			
			
				if(!validateContent(mt103)) {
					return new DOMSource(null);
				} else {
					//transfer novca
					for(CentralnaSema.Banke b : semaBanka.getBanke()) {
						if(b.getRacun().equals(mt103.getRacunPoverioca())) {
							b.setStanje(b.getStanje().add(mt103.getIznos()));
						} else if (b.getRacun().equals(mt103.getRacunDuznika())) {
							b.setStanje(b.getStanje().subtract(mt103.getIznos()));
						}
					}
					@SuppressWarnings("unused")
					MT900 mt900 = createMT900(mt103);
					encryptedDocument = MessageTransform.packS("MT900", "MT900", apsolute, propReceiver, "cer"+sender,ConstantsXWS.NAMESPACE_XSD_MT900, "MT900");
					semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
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
		}
		
		CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));
		return new DOMSource(encryptedDocument);
		
	}
	
	
	
	
	private MT900 createMT900(MT103 mt103){
		MT900 mt = null;
		try {
			mt = new MT900();
			mt.setIdPorukeNaloga(mt103.getIdPoruke());
			mt.setDatumValute(mt103.getDatumValute());
			mt.setIdPoruke("103");
			mt.setIznos(mt103.getIznos());
			mt.setObracunskiRacunBankeDuzinka(mt103.getObracunskiRacunBankeDuznika());
			mt.setSifraValute(mt103.getSifraValute());
			mt.setSwiftBankeDuznika(mt.getSwiftBankeDuznika());
			
			JAXBContext context = JAXBContext.newInstance("beans.mt900");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			marshaller.marshal(mt, new File(apsolute));
			
			Document doc = Validation.buildDocumentWithoutValidation(apsolute);
			Element mt900 = (Element) doc.getElementsByTagName("MT900").item(0);
			mt900.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt900.setAttribute("idPoruke", "103");
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolute);
			
		} catch (PropertyException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return mt;
		
	}
	
	
	public boolean validateContent(MT103 mt103){
		
		if(mt103 == null) {
			return false;
		} else {
			for(CentralnaSema.Banke b : semaBanka.getBanke()) {
				if(b.getRacun().equals(mt103.getRacunDuznika())) {
					if((b.getStanje().subtract(mt103.getIznos())).compareTo(mt103.getIznos()) == -1) {
						return false;
					}
				}
			}
			
		}
		return true;
	}

	
	
	private class MT910Client{
		/*
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
			
		
				forwardMT103(); 
				createMT910(); */
	}
	
	private class MT103Client{
		
	}
	
	
	
}

