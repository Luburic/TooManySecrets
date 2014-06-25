package provider.centrala;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
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
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.Validation;
import basexdb.banka.BankeSema;
import basexdb.centralna.CentralnaSema;
import basexdb.registar.Registar;
import basexdb.util.BankaDBUtil;
import basexdb.util.CentralnaDBUtil;
import basexdb.util.RegistarDBUtil;
import beans.mt103.MT103;
import beans.mt900.MT900;
import beans.mt910.MT910;
import beans.nalog.Nalog;
import beans.notification.Notification;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "CentralnaRTGSNalogPort", 
					serviceName = "CentralnaRTGSNalog",
					targetNamespace = "http://www.toomanysecrets.com/CentralnaRTGSNalog",
					wsdlLocation = "WEB-INF/wsdl/CentralnaRTGSNalog.wsdl")
public class MT103Provider implements javax.xml.ws.Provider<DOMSource>{

	private Document encryptedDocument;
	private CentralnaSema semaBanka;
	private String message;
	private Properties propReceiver;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("mt900.xml").toString().substring(6);
	private String apsolutePath = DocumentTransform.class.getClassLoader().getResource("mt910.xml").toString().substring(6);
	private String apsolutePath103 = DocumentTransform.class.getClassLoader().getResource("mt103.xml").toString().substring(6);
	private MT103 mt103;
	
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
			
			Element esender = (Element) document.getElementsByTagName("MT103").item(0);
			String sender = esender.getAttribute("sender");
			
			Document decryptedDocument = MessageTransform.unpack(document,"MT103", "MT103", ConstantsXWS.NAMESPACE_XSD_MT103, propReceiver, "banka", "MT103");

			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/MT103Signed.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}
			
			semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
			
			
			
			if (forSave != null) {
			
				Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT103,"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT103,"redniBrojPoruke").item(0);
				int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
				Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateString);
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();

				
				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT103);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT103);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);
				
				
				DocumentTransform.printDocument(decryptedDocument);
			
				JAXBContext context = JAXBContext.newInstance("beans.mt103");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				mt103 = (MT103) unmarshaller.unmarshal(decryptedDocument);
			
			
				if(!validateContent(mt103)) {
					//DocumentTransform.createNotificationResponse("455", message,ConstantsXWS.TARGET_NAMESPACE_CENTRALNA_BANKA_MT103);
					//return null;
					return new DOMSource(null);
				}else {
					//call clients
					createMT900(mt103);
					encryptedDocument = MessageTransform.packS("MT900", "MT900", apsolute, propReceiver, "cer"+sender,ConstantsXWS.NAMESPACE_XSD_MT900, "MT900");
				}
				MT910Client mt910Client = new MT910Client();
				MT103Client mt103Client = new MT103Client();
				if(mt910Client.testIt(propReceiver, mt103.getPrimalac(), "cer"+mt103.getPrimalac(), apsolutePath) &&
						mt103Client.testIt(propReceiver, mt103.getPrimalac(), "cer"+mt103.getPrimalac(), apsolutePath103)) {
					System.out.println("Sve je ok.");
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DOMSource(encryptedDocument);
		
	}
	
	
	
	
	private void createMT900(MT103 mt103) {
		MT900 mt = null;
		try {
			mt = new MT900();
			mt.setIdPorukeNaloga(mt103.getIdPoruke());
			mt.setDatumValute(mt103.getDatumValute());
			mt.setIdPoruke(MessageTransform.randomString(50));
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
			mt900.setAttribute("idPoruke", mt103.getIdPoruke());
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolute);
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createMT103(Nalog nalog) {

		Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");
		MT103 mt = null;
		try {
			mt = new MT103();
			mt.setSender(propReceiver.getProperty("naziv"));
			mt.setIdPoruke("103");
			mt.setSwiftBankeDuznika(propReceiver.getProperty("swift"));
			mt.setObracunskiRacunBankeDuznika(propReceiver.getProperty("obracunskiRac"));
			mt.setSwiftBankePoverioca(registar.getBanke().getBankaByCode(nalog.getRacunPoverioca().substring(0,3)).getSwift());
			mt.setObracunskiRacunBankePoverioca(registar.getBanke().getBankaByCode(nalog.getRacunPoverioca().substring(0,3)).getRacun());
			mt.setDuznik(nalog.getDuznikNalogodavac());
			mt.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
			mt.setPrimalac(nalog.getPrimalacPoverilac());
			mt.setDatumNaloga(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
			mt.setDatumValute(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
			mt.setRacunDuznika(nalog.getRacunDuznika());
			mt.setModelZaduzenja(nalog.getModelZaduzenja());
			mt.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
			mt.setRacunPoverioca(nalog.getRacunPoverioca());
			mt.setModelOdobrenja(nalog.getModelOdobrenja());
			mt.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
			mt.setIznos(nalog.getIznos());
			mt.setSifraValute(nalog.getOznakaValute());


			JAXBContext context = JAXBContext.newInstance("beans.mt103");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt, new File(apsolute));


			Document doc = Validation.buildDocumentWithoutValidation(apsolute);
			if(doc==null){
				System.out.println("NULL JE DOCUMENT MT103");
			}
			Element mt103 = (Element) doc.getElementsByTagName("MT103").item(0);
			mt103.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt103.setAttribute("sender",propReceiver.getProperty("naziv"));
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolute);

		} catch (Exception e) {
			e.printStackTrace();
		}

		RegistarDBUtil.storeRegistarDatabase(registar, "http://localhost:8081/BaseX75/rest/registar");
	}
	
	
	public boolean validateContent(MT103 mt103){
		return true;
	}
	
	
	private void createMT910(MT103 mt103){
		MT910 mt = null;
		try {
			mt = new MT910();
			mt.setIdPorukeNaloga("MT103");
			mt.setDatumValute(mt103.getDatumValute());
			mt.setIdPoruke("MT103");
			mt.setIznos(mt103.getIznos());
			mt.setObracunskiRacunBankePoverioca(mt103.getObracunskiRacunBankePoverioca());
			mt.setSifraValute(mt103.getSifraValute());
			mt.setSwiftBankePoverioca(mt.getSwiftBankePoverioca());
			
			JAXBContext context = JAXBContext.newInstance("beans.mt910");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			
			marshaller.marshal(mt, new File(apsolute));
			

			Document doc = Validation.buildDocumentWithoutValidation(apsolutePath);
			Element mt910 = (Element) doc.getElementsByTagName("MT910").item(0);
			mt910.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt910.setAttribute("idPoruke", "MT103");
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolute);
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private class MT910Client {
		
		public boolean testIt(Properties propSender, String receiver, String cert,String inputFile) {

			try {
				URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/MT910Response?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/MT910Response", "MT910Response");
				QName portName = new QName("http://www.toomanysecrets.com/MT910Response","MT910ResponsePort");

				Service service;
				try {
					service = Service.create(wsdlLocation, serviceName);
				} catch (Exception e) {
					throw Validation.generateSOAPFault("Server is not available.",
							SoapFault.FAULT_CODE_CLIENT, null);
				}
				Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);

				createMT910(mt103);
				Document encrypted = MessageTransform.packS("MT910", "MT910", inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD_MT910, "MT910");

				DocumentTransform.printDocument(encrypted);

				BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propSender.getProperty("address"));
				if(encrypted != null) {
					semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
					BankaDBUtil.storeBankaDatabase(semaBanka, propSender.getProperty("address"));
				}



				if (encrypted != null) {
					DOMSource response = dispatch.invoke(new DOMSource(encrypted));

					if(response!=null) {
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
						Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification", ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender,"banka", "Notifikacija");

						DocumentTransform.printDocument(decryptedDocument);
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

						if(decryptedDocument != null){

							decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
							decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
							decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

							
							SecurityClass sc = new SecurityClass();
							sc.saveDocument(decryptedDocument, apsolute);
							JAXBContext context = JAXBContext.newInstance("beans.notification");
							Unmarshaller unmarshaller = context.createUnmarshaller();
							Notification notification = (Notification) unmarshaller.unmarshal(new File(apsolute));
							
							System.out.println("Notification message: " + notification.getNotificationstring());
							
							return true;


						}else{
							return false;
						}

					}else {
						return false;
					}
				}else {
					return false;
				}
			}catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		}
		
		
	}
	
	private class MT103Client{
		
		public boolean testIt(Properties propSender, String receiver, String cert,String inputFile) {

			try {
				URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/MT103Response?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/MT103Response", "MT103Response");
				QName portName = new QName("http://www.toomanysecrets.com/MT103Response","MT103ResponsePort");

				Service service;
				try {
					service = Service.create(wsdlLocation, serviceName);
				} catch (Exception e) {
					throw Validation.generateSOAPFault("Server is not available.",
							SoapFault.FAULT_CODE_CLIENT, null);
				}
				Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);


				Document encrypted = MessageTransform.packS("MT103", "MT103", inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD_MT103, "MT103");

				DocumentTransform.printDocument(encrypted);

				BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propSender.getProperty("address"));
				if(encrypted != null) {
					semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
					BankaDBUtil.storeBankaDatabase(semaBanka, propSender.getProperty("address"));
				}



				if (encrypted != null) {
					DOMSource response = dispatch.invoke(new DOMSource(encrypted));

					if(response!=null) {
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
						Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification", ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender,"banka", "Notifikacija");

						DocumentTransform.printDocument(decryptedDocument);
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

						if(decryptedDocument != null){

							decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
							decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
							decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

							
							SecurityClass sc = new SecurityClass();
							sc.saveDocument(decryptedDocument, apsolute);
							JAXBContext context = JAXBContext.newInstance("beans.notification");
							Unmarshaller unmarshaller = context.createUnmarshaller();
							Notification notification = (Notification) unmarshaller.unmarshal(new File(apsolute));
							
							System.out.println("Notification message: " + notification.getNotificationstring());
							
							return true;


						}else{
							return false;
						}

					}else {
						return false;
					}
				}else {
					return false;
				}
			}catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;

		}
		
		
		
	}
	
	
	
}


