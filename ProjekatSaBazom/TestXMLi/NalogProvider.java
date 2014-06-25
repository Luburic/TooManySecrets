package provider.banka;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Provider;
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
import basexdb.banka.BankeSema.KorisnickiRacuni.Racun;
import basexdb.banka.BankeSema.RealizovaniNalozi.RealizovanNalog;
import basexdb.registar.Registar;
import basexdb.util.BankaDBUtil;
import basexdb.util.RegistarDBUtil;
import beans.mt103.MT103;
import beans.mt900.MT900;
import beans.nalog.Nalog;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaNalogPort", serviceName = "BankaNalog", targetNamespace = ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG, wsdlLocation = "WEB-INF/wsdl/BankaNalog.wsdl")
public class NalogProvider implements Provider<DOMSource> {

	private String message;
	private Document encrypted;
	private BigDecimal limit = new BigDecimal(250000);
	private Properties propReceiver;
	private String sender;
	private boolean rtgs;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("mt103.xml").toString().substring(6);
	private MT900 mt900;
	protected BankeSema semaBanka;


	public NalogProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		try {

			System.out.println("\nInvoking NalogProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			Element esender = (Element) document.getElementsByTagName("nalog").item(0);
			sender = esender.getAttribute("sender");
			
			semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));

			Document decryptedDocument = MessageTransform.unpack(document,"Nalog", "Nalog",ConstantsXWS.NAMESPACE_XSD_NALOG, propReceiver,"firma", "Nalog");
			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/NalogSigned.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}

			semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));

			if (forSave != null) {
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();
				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NALOG);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NALOG);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

				JAXBContext context = JAXBContext.newInstance("beans.nalog");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				Nalog nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);

				BankeSema.KorisnickiRacuni.Racun racunIzBazeDuznik = semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender);
				BankeSema.KorisnickiRacuni.Racun racunIzBazePoverilac = semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender);


				if(racunIzBazeDuznik == null) {
					System.out.println("456, Racun ne postoji u banci");
					return new DOMSource(encrypted);
					//ako su racuni u ustoj banci
				} else if(racunIzBazePoverilac != null) {
					boolean transakcijaObavljena = obaviTransakciju(nalog, propReceiver);
					if(transakcijaObavljena) {
						DocumentTransform.createNotificationResponse("200", "Nalog uspesno obradjen.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					} else {
						DocumentTransform.createNotificationResponse("455", "Nalog nije uspesno obradjen.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					}
				} else {

					rtgs = (nalog.isHitno() || nalog.getIznos().compareTo(limit) != -1);

					if (!validateContent(nalog)) {
						DocumentTransform.createNotificationResponse("455", message, ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					} else {

						if (rtgs) {
							MT103 mt103 = createMT103(nalog);
							if(mt103 != null) {
								semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().add(nalog.getIznos()));
								MT103Client cl = new MT103Client();
								String apsolute = DocumentTransform.class.getClassLoader().getResource("mt103.xml").toString().substring(6);
								if(cl.testIt(propReceiver, "centralnabanka","cercentralnabanka", apsolute )) {
									if(mt900 != null) {
										semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().subtract(mt900.getIznos()));
										semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setStanje(
												semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getStanje().subtract(nalog.getIznos()));
										DocumentTransform.createNotificationResponse("200", "Nalog uspesno obradjen.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
									} else {
										semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().subtract(nalog.getIznos()));
										DocumentTransform.createNotificationResponse("423", "Nalog nije uspesno obradjen.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
									}
								} else {
									semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().subtract(nalog.getIznos()));
									DocumentTransform.createNotificationResponse("423", "Nalog nije uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
								}
							} else {
								semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().subtract(nalog.getIznos()));
								DocumentTransform.createNotificationResponse("500", "Internal error", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
							}
						} else {
							//nalozi na cekanju


						}
					}
				}
			}
			String apsoluteNot = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
			encrypted = MessageTransform.packS("Notifikacija", "Notification",apsoluteNot, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");

			if(encrypted != null) {
				semaBanka.setBrojacPoslednjePoslateNotifikacije(semaBanka.getBrojacPoslednjePoslateNotifikacije()+1);
			}
			BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));

		return new DOMSource(encrypted);
	}

	private boolean validateContent(Nalog nalog) {
		message = "";
		Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");

		if(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getStanje().subtract(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano())
				.compareTo(nalog.getIznos()) == -1) {
			message = "Na racunu korisnika nema dovoljno sredstava da bi se izvrsila uplata.";
			return false;
		} else if (registar.getFirme().getFirmaByRacun(nalog.getRacunPoverioca()) == null) {
			message = "Nepostojeci racun u drugoj banci.";
			return false;
		} else if (registar.getBanke().getBankaByCode(nalog.getRacunPoverioca().substring(0, 3)) == null) {
			message = "Neposotjeca banka.";
			return false;
		} else if (semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).isBlokiran()) {
			message = "Racun posiljaoca je blokiran.";
			return false;
		}

		return true;
	}

	private MT103 createMT103(Nalog nalog) {

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
		return mt;
	}



	private class MT103Client {
		public boolean testIt(Properties propSender, String receiver, String cert,String inputFile) {

			try {
				URL wsdlLocation = new URL("http://localhost:8080/" + receiver+ "/services/CentralnaRTGSNalog?wsdl");
				QName serviceName = new QName("http://www.toomanysecrets.com/CentralnaRTGSNalog", "CentralnaRTGSNalog");
				QName portName = new QName("http://www.toomanysecrets.com/CentralnaRTGSNalog","CentralnaRTGSNalogPort");

				Service service;
				try {
					service = Service.create(wsdlLocation, serviceName);
				} catch (Exception e) {
					throw Validation.generateSOAPFault("Server is not available.",
							SoapFault.FAULT_CODE_CLIENT, null);
				}
				Dispatch<DOMSource> dispatch = service.createDispatch(portName,DOMSource.class, Service.Mode.PAYLOAD);


				Document encrypted = MessageTransform.packS("MT103", "MT103",inputFile, propSender, cert, ConstantsXWS.NAMESPACE_XSD_MT103,"MT103");

				DocumentTransform.printDocument(encrypted);

				if(encrypted != null) {
					semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
				}

				if (encrypted != null) {
					semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
					DOMSource response = dispatch.invoke(new DOMSource(encrypted));

					if(response!=null) {
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
						Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response), "MT900", "MT900",ConstantsXWS.NAMESPACE_XSD_MT900, propSender, "banka", "MT900");

						DocumentTransform.printDocument(decryptedDocument);
						System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

						if(decryptedDocument != null){
							Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT900,"timestamp").item(0);
							String dateString = timestamp.getTextContent();
							Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT900,"redniBrojPoruke").item(0);
							int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());

							decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT900);
							decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT900);
							decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

							String apsolute = DocumentTransform.class.getClassLoader().getResource("mt900.xml").toString().substring(6);
							semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setBrojac(rbrPoruke);
							semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setTimestamp(dateString);
							SecurityClass sc = new SecurityClass();
							sc.saveDocument(decryptedDocument, apsolute);
							JAXBContext context = JAXBContext.newInstance("beans.mt900");
							Unmarshaller unmarshaller = context.createUnmarshaller();
							mt900 = (MT900) unmarshaller.unmarshal(new File(apsolute));

							semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setBrojac(rbrPoruke);
							semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setTimestamp(dateString);
						}else{
							return false;
						}
					}else {
						return false;
					}
				}else {
					System.out.println("Nema para u banci.");
					DocumentTransform.createNotificationResponse("555", "Banka bankrotirala", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
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

	public boolean obaviTransakciju(Nalog nalog, Properties prop) {
		
		if(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getStanje()
				.subtract(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano())
				.compareTo(nalog.getIznos()) == -1 || semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).isBlokiran()) {
			return false;
		}
		semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setStanje(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getStanje().subtract(nalog.getIznos()));
		semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setStanje(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getStanje().add(nalog.getIznos()));
		RealizovanNalog realizovan = new RealizovanNalog();
		realizovan.setNalog(nalog);
		semaBanka.getRealizovaniNalozi().getRealizovanNalog().add(realizovan);
		
		return true;

	}
}

