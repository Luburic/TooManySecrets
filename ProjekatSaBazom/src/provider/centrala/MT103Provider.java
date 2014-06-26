package provider.centrala;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
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
import util.Validation;
import basexdb.centralna.Centralna;
import basexdb.util.CentralnaDBUtil;
import beans.mt103.MT103;
import beans.mt900.MT900;
import beans.mt910.MT910;
import beans.notification.Notification;


@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "CentralnaRTGSNalogPort", 
serviceName = "CentralnaRTGSNalog",
targetNamespace = "http://www.toomanysecrets.com/CentralnaRTGSNalog",
wsdlLocation = "WEB-INF/wsdl/CentralnaRTGSNalog.wsdl")
public class MT103Provider implements javax.xml.ws.Provider<DOMSource>{

	private Document encryptedDocument;
	public Centralna semaBanka;
	private Properties propReceiver;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("mt900.xml").toString().substring(6);
	private String apsolutePath = DocumentTransform.class.getClassLoader().getResource("mt910.xml").toString().substring(6);
	private String apsolutePath103 = DocumentTransform.class.getClassLoader().getResource("mt103.xml").toString().substring(6);
	private String notificationPath = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
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
			//DocumentTransform.printDocument(document);
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
			
			System.out.println("U CENTRALNOJ SE NALAZE SLEDECE BANKE: ");
			for(Centralna.Banke.Banka b : semaBanka.getBanke().getBanka()) {
				System.out.println("NAZIV: "+b.getNaziv());
				System.out.println("RACUN: "+b.getRacun());
				System.out.println("SWIFT: "+b.getSwift());
				System.out.println("STANJE: "+b.getStanje());
			}



			if (forSave != null) {

				Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT103,"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT103,"redniBrojPoruke").item(0);
				int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();
				
				semaBanka.getBrojacPoslednjegPrimljenogMTNaloga().getBankaByNaziv(sender).setBrojac(rbrPoruke);
				semaBanka.getBrojacPoslednjegPrimljenogMTNaloga().getBankaByNaziv(sender).setTimestamp(dateString);
				CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));

				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT103);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT103);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);


				System.out.println("UUUUSAOOOOOOOOOOOOOO U MT103 PROVIDER");
				//DocumentTransform.printDocument(decryptedDocument);
				System.out.println("UUUUSAOOOOOOOOOOOOOO U MT103 PROVIDER");

				JAXBContext context = JAXBContext.newInstance("beans.mt103");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				mt103 = (MT103) unmarshaller.unmarshal(decryptedDocument);
				
				if(mt103 == null) {
					System.out.println("MT103 JE NULLL");
				}


				if(!validateContent(mt103)) {
					return new DOMSource(null);
				} else {
					
					System.out.println("OBRACUNSKI RACUN BANKE DUZNIKA: " + mt103.getObracunskiRacunBankeDuznika());
					System.out.println("OBRACUNSKI RACUN BANKE POVERIOCA: " + mt103.getObracunskiRacunBankePoverioca());
					for(Centralna.Banke.Banka b : semaBanka.getBanke().getBanka()) {
						if(b.getRacun().equals(mt103.getObracunskiRacunBankeDuznika())) {
							semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
							b.setStanje(b.getStanje().subtract(mt103.getIznos()));
							CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));
						} else if (b.getRacun().equals(mt103.getObracunskiRacunBankePoverioca())) {
							semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
							b.setStanje(b.getStanje().add(mt103.getIznos()));
							CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));
						}
					}
					//call clients
					createMT910(mt103);
					createMT103(mt103);
					String reciver = null;
					int size = semaBanka.getBanke().getBanka().size();
					System.out.println("VELICINA LISTE CENTRALNE BANKE"+size);
					for(Centralna.Banke.Banka b : semaBanka.getBanke().getBanka()) {
						System.out.println("SWIFT SVAKE BANKE: " + b.getSwift());
					}
					for(Centralna.Banke.Banka b : semaBanka.getBanke().getBanka()) {
						if(b.getSwift().equals(mt103.getSwiftBankePoverioca())) {
							reciver = b.getNaziv();
							System.out.println("RECIVER KOME SE SALJE MT103 i MT910: "+reciver);
						}
					}
					if(testIt910(propReceiver, reciver, "cer"+reciver, apsolutePath)) {
						System.out.println("USPESNO PROSAO!!!");
					} else {
						System.out.println("NIJE USPESNO PROSAO!!!");
					}
					if(testIt103(propReceiver, reciver, "cer"+reciver, apsolutePath103)) {
						System.out.println("USPESNO PROSAO!!!");
					} else {
						System.out.println("NIJE USPESNO PROSAO!!!");
					}
					createMT900(mt103);
					encryptedDocument = MessageTransform.packS("MT900", "MT900", apsolute, propReceiver, "cer"+sender, ConstantsXWS.NAMESPACE_XSD_MT900, "MT900");
					semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
					semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
					CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));
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
			mt.setSwiftBankeDuznika(mt103.getSwiftBankeDuznika());

			JAXBContext context = JAXBContext.newInstance("beans.mt900");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			marshaller.marshal(mt, new File(apsolute));

			Document doc = Validation.buildDocumentWithoutValidation(apsolute);
			Element mt900 = (Element) doc.getElementsByTagName("MT900").item(0);
			mt900.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt900.setAttribute("idPoruke", "MT900");
			SecurityClass sc = new SecurityClass();
			String apsolute1 = DocumentTransform.class.getClassLoader().getResource("mt900.xml").toString().substring(6);
			sc.saveDocument(doc, apsolute1);

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

	private void createMT103(MT103 mt103) {

		try {
			mt103.setIdPoruke("MT103MT103");
			JAXBContext context = JAXBContext.newInstance("beans.mt103");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt103, new File(apsolutePath103));
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		Document doc = Validation.buildDocumentWithoutValidation(apsolutePath103);
		if(doc==null){
			System.out.println("NULL JE DOCUMENT MT103");
		}
		Element mt103El = (Element) doc.getElementsByTagName("MT103").item(0);
		mt103El.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		mt103El.setAttribute("sender", "centralnabanka");
		SecurityClass sc = new SecurityClass();
		sc.saveDocument(doc, apsolutePath103);
	}


	public boolean validateContent(MT103 mt103){
		if(semaBanka == null) {
			System.out.println("SEMA JE NULLL U VALIDACIJI");
		}
		System.out.println("SEMA CENTRALNE NIJE NULL I VRSI SE VALIDACIJA DA LI POSTOJI BANKA SA POSLATIM SWIFT KODOM");
		for (Centralna.Banke.Banka b : semaBanka.getBanke().getBanka()) {
			if(b.getSwift().equals(mt103.getSwiftBankeDuznika()) || b.getSwift().equals(mt103.getSwiftBankePoverioca())) {
				System.out.println("Ne postoji banka sa dostavljenim swift kodom");
				return true;
			}
		}
		return false;
	}

	private void createMT910(MT103 mt103) {
		MT910 mt = null;
		try {
			mt = new MT910();
			mt.setIdPorukeNaloga(mt103.getIdPoruke());
			mt.setDatumValute(mt103.getDatumValute());
			mt.setIdPoruke(MessageTransform.randomString(50));
			mt.setIznos(mt103.getIznos());
			mt.setObracunskiRacunBankePoverioca(mt103.getObracunskiRacunBankeDuznika());
			mt.setSifraValute(mt103.getSifraValute());
			mt.setSwiftBankePoverioca(mt103.getSwiftBankeDuznika());

			JAXBContext context = JAXBContext.newInstance("beans.mt910");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);

			String apsolutePathTemp = DocumentTransform.class.getClassLoader().getResource("mt910.xml").toString().substring(6);
			marshaller.marshal(mt, new File(apsolutePathTemp));

			Document doc = Validation.buildDocumentWithoutValidation(apsolutePathTemp);
			Element mt910 = (Element) doc.getElementsByTagName("MT910").item(0);
			mt910.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt910.setAttribute("idPoruke", "MT910");
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolutePathTemp);

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



	public boolean testIt910(Properties propSender, String receiver, String cert,String inputFile) {

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


			Document encrypted = MessageTransform.packS("MT910", "MT910", inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD_MT910, "MT910");

			//DocumentTransform.printDocument(encrypted);

			if(encrypted != null) {
				semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
				semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
				CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));
			}



			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));

				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification", ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender,"banka", "Notifikacija");

					//DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

					if(decryptedDocument != null){

						decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeSignature(decryptedDocument);


						SecurityClass sc = new SecurityClass();
						sc.saveDocument(decryptedDocument, notificationPath);
						JAXBContext context = JAXBContext.newInstance("beans.notification");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						Notification notification = (Notification) unmarshaller.unmarshal(new File(notificationPath));

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

	public boolean testIt103(Properties propSender, String receiver, String cert,String inputFile) {

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

			//DocumentTransform.printDocument(encrypted);

			if(encrypted != null) {
				semaBanka = CentralnaDBUtil.loadCentralnaDatabase(propReceiver.getProperty("address"));
				semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
				CentralnaDBUtil.storeCentralnaDatabase(semaBanka, propReceiver.getProperty("address"));
			}



			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));

				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response),"Nalog", "Notification", ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, propSender,"banka", "Notifikacija");

					//DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

					if(decryptedDocument != null){

						decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NOTIFICATION);
						decryptedDocument = MessageTransform.removeSignature(decryptedDocument);


						SecurityClass sc = new SecurityClass();
						sc.saveDocument(decryptedDocument, notificationPath);
						JAXBContext context = JAXBContext.newInstance("beans.notification");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						Notification notification = (Notification) unmarshaller.unmarshal(new File(notificationPath));

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


