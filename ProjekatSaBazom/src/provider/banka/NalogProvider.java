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
import java.util.Vector;

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
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.apache.cxf.binding.soap.SoapFault;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
//import util.AccountNumberISO7064Mod9710;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.MyDatatypeConverter;
import util.Validation;
import basexdb.banka.BankeSema;
import basexdb.banka.BankeSema.KorisnickiRacuni.Racun;
import basexdb.banka.BankeSema.NerealizovaniNalozi.NerealizovanNalog;
import basexdb.banka.BankeSema.RealizovaniNalozi.RealizovanNalog;
import basexdb.centralna.Centralna;
import basexdb.registar.Registar;
import basexdb.registar.Registar.Banke.Banka;
import basexdb.registar.Registar.Firme.Firma;
import basexdb.util.BankaDBUtil;
import basexdb.util.CentralnaDBUtil;
import basexdb.util.RegistarDBUtil;
import beans.mt102.MT102;
import beans.mt102.MT102.PojedinacneUplate;
import beans.mt102.MT102.PojedinacneUplate.PojedinacnaUplata;
import beans.mt102.MT102.ZaglavljeMT102;
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
	public BankeSema semaBanka;
	private String sender;
	private boolean rtgs;
	private Racun racunPosaljioca;

	public PojedinacneUplate stavke = new PojedinacneUplate();
	public String apsolute = DocumentTransform.class.getClassLoader().getResource("mt103.xml").toString().substring(6);


	public NalogProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		try {
			System.out.println("\nInvoking NalogProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			//DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			Element esender = (Element) document.getElementsByTagName("nalog").item(0);
			sender = esender.getAttribute("sender");

			Document decryptedDocument = MessageTransform.unpack(document,"Nalog", "Nalog", ConstantsXWS.NAMESPACE_XSD_NALOG, propReceiver,"firma", "Nalog");
			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/NalogSigned.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}

			semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));

			System.out.println("U BANCI A SE NALAZE SLEDECI PODACI");
			for(BankeSema.KorisnickiRacuni.Racun r : semaBanka.getKorisnickiRacuni().getRacun()){
				System.out.println("RACUN: "+r.getVlasnik());
			}

			Centralna semaCentralna = CentralnaDBUtil.loadCentralnaDatabase("http://localhost:8081/BaseX75/rest/centralna");

			System.out.println("U CENTRALNOJ SE NALAZE SLEDECE BANKE: ");
			for(Centralna.Banke.Banka b : semaCentralna.getBanke().getBanka()) {
				System.out.println("NAZIV: "+b.getNaziv());
				System.out.println("RACUN: "+b.getRacun());
				System.out.println("SWIFT: "+b.getSwift());
				System.out.println("STANJE: "+b.getStanje());
			}

			Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");
			System.out.println("U REGISTRU SU SELEDECI PODACI: ");
			for(Registar.Banke.Banka b : registar.getBanke().getBanka()) {
				System.out.println(b.getNaziv());
			}
			for(Registar.Firme.Firma b : registar.getFirme().getFirma()) {
				System.out.println(b.getNaziv());
			}
			System.out.println("KRAJ ISPISA ZA REGISTAR");

			if (forSave != null) {

				Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NALOG,"timestamp").item(0);
				String dateString = timestamp.getTextContent();
				Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_NALOG, "redniBrojPoruke").item(0);
				int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
				sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();


				decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NALOG);
				decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_NALOG);
				decryptedDocument = MessageTransform.removeSignature(decryptedDocument);

				JAXBContext context = JAXBContext.newInstance("beans.nalog");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				Nalog nalog = (Nalog) unmarshaller.unmarshal(decryptedDocument);


				if (!validateContent(nalog)) {
					DocumentTransform.createNotificationResponse("455", message, ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
				} 

				else {
					
					//update brojaca i timestampa
					System.out.println("Poslednji brojac primljenog naloga: "+semaBanka.getBrojacPoslednjegPrimljenogNaloga().getFirmaByNaziv(sender).getBrojac());
					semaBanka.getBrojacPoslednjegPrimljenogNaloga().getFirmaByNaziv(sender).setBrojac(rbrPoruke);
					semaBanka.getBrojacPoslednjegPrimljenogNaloga().getFirmaByNaziv(sender).setTimestamp(dateString);
					BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));
					
					Registar reg = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");

					String swiftDuznik = reg.getBanke().getBankaByCode(nalog.getRacunDuznika().substring(0, 3)).getSwift();
					String swiftPoverilac = reg.getBanke().getBankaByCode(nalog.getRacunPoverioca().substring(0, 3)).getSwift();

					//ukoliko su u istoj banci odmah izvrsi transfer novca
					if(swiftDuznik.equals(swiftPoverilac)){
						BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
						semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setStanje(racunPosaljioca.getStanje().subtract(nalog.getIznos()));

						RealizovanNalog rn = new RealizovanNalog();
						rn.setNalog(nalog);
						semaBanka.getRealizovaniNalozi().getRealizovanNalog().add(rn);

						NerealizovanNalog nrn = new NerealizovanNalog();
						nrn.setNalog(nalog);
						semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().remove(nrn);

						Racun racunPrimaoca = semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(reg.getFirme().getFirmaByRacun(nalog.getRacunPoverioca()).getNaziv());
						racunPrimaoca.setStanje(racunPrimaoca.getStanje().add(nalog.getIznos()));
						BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));
						DocumentTransform.createNotificationResponse("200", "Nalog uspesno obradjen.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);

					} else if (nalog.getRacunDuznika().equals(nalog.getRacunPoverioca()) && !sender.equals("")){
						BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
						semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setStanje(racunPosaljioca.getStanje().add(nalog.getIznos()));

						RealizovanNalog rn = new RealizovanNalog();
						rn.setNalog(nalog);
						semaBanka.getRealizovaniNalozi().getRealizovanNalog().add(rn);

						NerealizovanNalog nrn = new NerealizovanNalog();
						nrn.setNalog(nalog);
						semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().remove(nrn);

						BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));
						DocumentTransform.createNotificationResponse("200", "Nalog uspesno obradjen.", ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
					}

					else {

						rtgs = (nalog.isHitno() || nalog.getIznos().compareTo(limit) != -1); //rtgs flag
						if (rtgs) {
							createMT103(nalog);
							BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
							semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().add(nalog.getIznos()));
							BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));
							String apsolute = DocumentTransform.class.getClassLoader().getResource("mt103.xml").toString().substring(6);
							if(testItMT103(propReceiver, "centralnabanka","cercentralnabanka", apsolute)) {
								BankeSema semaBanka1 = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
								semaBanka1.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setStanje(semaBanka1.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getStanje().subtract(nalog.getIznos()));
								semaBanka1.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka1.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().subtract(nalog.getIznos()));
								RealizovanNalog rn = new RealizovanNalog();
								rn.setNalog(nalog);
								semaBanka1.getRealizovaniNalozi().getRealizovanNalog().add(rn);

								NerealizovanNalog nrn = new NerealizovanNalog();
								nrn.setNalog(nalog);
								semaBanka1.getNerealizovaniNalozi().getNerealizovanNalog().remove(nrn);

								BankaDBUtil.storeBankaDatabase(semaBanka1,  propReceiver.getProperty("address"));
								DocumentTransform.createNotificationResponse("200", "Nalog uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
							} else {
								BankeSema semaBanka1 = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
								semaBanka1.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka1.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().subtract(nalog.getIznos()));
								BankaDBUtil.storeBankaDatabase(semaBanka1,  propReceiver.getProperty("address"));
								DocumentTransform.createNotificationResponse("423", "Nalog nije uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
							}
						}
						else {

							String apsolute = DocumentTransform.class.getClassLoader().getResource("mt102.xml").toString().substring(6);						
							BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
							semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).setRezervisano(semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender).getRezervisano().add(nalog.getIznos()));
							//za primer samo stavke za kliring
							semaBanka.setBrojacStavkiZaKliring(2);
							BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));
							Vector<MT102> result = createOrUpdateMT102(nalog);

							if(result!=null){ 

								for (MT102 mt102 : result) {

									String path = saveSingleMT102(mt102);

									if(testItMT102(propReceiver,"centralnabanka","cercentralnabanka",path)){
										DocumentTransform.createNotificationResponse("423", "Nalog uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
									}else{
										DocumentTransform.createNotificationResponse("423", "Nalog nije uspesno obradjen.",ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
									}
								}

							}

						}

						String apsoluteNot = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
						encrypted = MessageTransform.packS("Notifikacija", "Notification",apsoluteNot, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
						BankeSema bsema = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
						if(encrypted != null) {
							bsema.setBrojacPoslednjePoslateNotifikacije(bsema.getBrojacPoslednjePoslateNotifikacije()+1);
							BankaDBUtil.storeBankaDatabase(bsema,  propReceiver.getProperty("address"));
						}
					}
				}
			} else {
				
				String apsoluteNot = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
				encrypted = MessageTransform.packS("Notifikacija", "Notification",apsoluteNot, propReceiver, "cer" + sender,ConstantsXWS.NAMESPACE_XSD_NOTIFICATION, "Notifikacija");
			}

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

		return new DOMSource(encrypted);
	}

	private boolean validateContent(Nalog nalog) {
		message = "";
		Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");
		BankeSema semaB = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));

		racunPosaljioca = semaB.getKorisnickiRacuni().getRacunByNazivKlijenta(sender);

		if(racunPosaljioca == null){
			message = "Racun posaljioca nije registrovan u banci.";
			return false;
		}

		if (racunPosaljioca.isBlokiran()) {
			message = "Racun posiljaoca je blokiran.";
			return false;
		}

		if(racunPosaljioca.getStanje().compareTo(racunPosaljioca.getRezervisano().add(nalog.getIznos())) == -1) {
			message = "Na racunu korisnika nema dovoljno sredstava da bi se izvrsila uplata.";
			return false;
		}

		Banka bankaPoverioca = registar.getBanke().getBankaByCode(nalog.getRacunPoverioca().substring(0, 3)); 

		if (bankaPoverioca == null) {
			message = "Ra�un poverioca nije registrovan ni u jednoj banci/ne postoji banka.";
			return false;
		}

		Firma firma = registar.getFirme().getFirmaByRacun(nalog.getRacunPoverioca());
		if (firma == null) {
			message = "Medju registrovanim firmama ne postoji ona kojoj se vr�i uplata.";
			return false;
		}
		
		//treba izgenerisati validan racun sto je jako cimanje ali to je provera
		/*if(!AccountNumberISO7064Mod9710.verify(racunPosaljioca.getBrojRacuna())) {
			message = "Racun nije registrovan u drugoj banci.";
			return false;
		}*/
		
		Centralna semaCentralna = CentralnaDBUtil.loadCentralnaDatabase("http://localhost:8081/BaseX75/rest/centralna");
		for(Centralna.Banke.Banka b : semaCentralna.getBanke().getBanka()) {
			if(b.getNaziv().equals(propReceiver.getProperty("naziv"))) {
				if(b.getStanje().compareTo(new BigDecimal(0)) == -1) {
					message = "Na obracunskom racunu banke nalogodavca nema doboljno novca.";
					return false;
				}
			}
		}

		NerealizovanNalog nrn = new NerealizovanNalog();
		nrn.setNalog(nalog);
		nrn.setVrstaNaloga("mt103");
		semaB.getNerealizovaniNalozi().getNerealizovanNalog().add(nrn);
		BankaDBUtil.storeBankaDatabase(semaB,  propReceiver.getProperty("address"));
		return true;
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
			mt103.setAttribute("sender", propReceiver.getProperty("naziv"));
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, apsolute);

			//DocumentTransform.printDocument(doc);

		} catch (Exception e) {
			e.printStackTrace();
		}

		RegistarDBUtil.storeRegistarDatabase(registar, "http://localhost:8081/BaseX75/rest/registar");
		//return mt;
	}


	private Vector<MT102> createOrUpdateMT102(Nalog nalog) {
		Registar registar = RegistarDBUtil.loadRegistarDatabase("http://localhost:8081/BaseX75/rest/registar");
		BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));


		PojedinacnaUplata pu = new PojedinacnaUplata();
		pu.setIdNalogaZaPlacanje(MessageTransform.randomString(50));
		pu.setDuznik(nalog.getDuznikNalogodavac());
		pu.setSvrhaUplate(nalog.getSvrhaPlacanja());
		pu.setPrimalac(nalog.getPrimalacPoverilac());
		pu.setDatumNaloga(nalog.getDatumNaloga());
		pu.setRacunDuznika(nalog.getRacunDuznika());
		pu.setModelZaduzenja(nalog.getModelZaduzenja());
		pu.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
		pu.setRacunPoverioca(nalog.getRacunPoverioca());
		pu.setModelOdobrenja(nalog.getModelOdobrenja());
		pu.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
		pu.setIznos(nalog.getIznos());
		pu.setSifraValute(nalog.getOznakaValute());
		stavke.getPojedinacnaUplata().add(pu);

		NerealizovanNalog nrn = new NerealizovanNalog();
		nrn.setNalog(nalog);
		nrn.setVrstaNaloga("mt102");
		semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().add(nrn);


		int clCounter = 0;

		for(int i=0; i<semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().size(); i++){
			if(semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().get(i).getVrstaNaloga().equalsIgnoreCase("mt102"))
				clCounter++;
		}


		if(clCounter == 3){
			Vector<MT102>result = new Vector<MT102>();
			Vector<Banka>banke = new Vector<Banka>();

			for(int i=0; i<semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().size(); i++){
				if(semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().get(i).getVrstaNaloga().equalsIgnoreCase("mt102")){
					Nalog nc= semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().get(i).getNalog();

					if(!banke.contains(registar.getBanke().getBankaByCode(nc.getRacunPoverioca().substring(0,3))))
						banke.add(registar.getBanke().getBankaByCode(nc.getRacunPoverioca().substring(0,3)));
				}
			}

			//za svaku target banku
			for (Banka banka : banke) {

				MT102 mt102 = new MT102();

				ZaglavljeMT102 zag = new ZaglavljeMT102();
				zag.setIdPoruke(MessageTransform.randomString(50));
				zag.setSwiftBankeDuznika(propReceiver.getProperty("swift"));
				zag.setObracunskiRacunBankeDuzinka(propReceiver.getProperty("obracunskiRac"));
				zag.setSwiftBankePoverioca(banka.getSwift());
				zag.setObracunskiRacunBankePoverioca(banka.getRacun()); //obracunski 

				BigDecimal sum = new BigDecimal(0);
				PojedinacnaUplata represent = null;
				PojedinacneUplate localUplate = new PojedinacneUplate();

				//sve pojedinacne uplate
				for (PojedinacnaUplata stavka: stavke.getPojedinacnaUplata()){
					Banka bankaStavke =registar.getBanke().getBankaByCode(stavka.getRacunPoverioca().substring(0,3));

					if(bankaStavke.equals(banka)) {
						represent = stavka;
						sum= sum.add(stavka.getIznos());
						localUplate.getPojedinacnaUplata().add(stavka);

					}
				}

				zag.setUkupanIznos(sum);
				zag.setSifraValute(represent.getSifraValute());
				zag.setDatumValute(new Date());
				zag.setDatum(new Date());
				zag.setSifraValute(represent.getSifraValute());

				mt102.setZaglavljeMT102(zag);
				mt102.setPojedinacneUplate(localUplate);
				result.add(mt102);

			}


			stavke.getPojedinacnaUplata().clear();

			for(int i=0; i<semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().size(); i++){
				if(semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().get(i).getVrstaNaloga().equalsIgnoreCase("mt102")){
					RealizovanNalog rn = new RealizovanNalog();
					rn.setNalog(semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().get(i).getNalog());
					semaBanka.getRealizovaniNalozi().getRealizovanNalog().add(rn);
					semaBanka.getNerealizovaniNalozi().getNerealizovanNalog().remove(i);
				}
			}
			BankaDBUtil.storeBankaDatabase(semaBanka,  propReceiver.getProperty("address"));
			return result;
		}
		return null;

	}



	public String saveSingleMT102(MT102 mt){

		String path=null;
		try {
			path = DocumentTransform.class.getClassLoader().getResource("mt102.xml").toString().substring(6);
			JAXBContext context = JAXBContext.newInstance("beans.mt102");
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
			marshaller.marshal(mt, new File(path));


			Document doc = Validation.buildDocumentWithoutValidation(path);
			if(doc==null){
				System.out.println("NULL JE DOCUMENT MT102");
			}
			Element mt102 = (Element) doc.getElementsByTagName("MT102").item(0);
			mt102.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
			mt102.setAttribute("sender", propReceiver.getProperty("naziv"));
			SecurityClass sc = new SecurityClass();
			sc.saveDocument(doc, path);
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
		return path;
	}


	public boolean testItMT103(Properties propSender, String receiver, String cert,String inputFile) {

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

			//DocumentTransform.printDocument(encrypted);

			BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propSender.getProperty("address"));
			if(encrypted != null) {
				semaBanka.setBrojacPoslednjegPoslatogMTNaloga(semaBanka.getBrojacPoslednjegPoslatogMTNaloga()+1);
				BankaDBUtil.storeBankaDatabase(semaBanka, propSender.getProperty("address"));
			}



			if (encrypted != null) {
				DOMSource response = dispatch.invoke(new DOMSource(encrypted));
				//ZADUZENJE

				if(response!=null) {
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");	
					Document decryptedDocument = MessageTransform.unpack(DocumentTransform.convertToDocument(response), "MT900", "MT900",ConstantsXWS.NAMESPACE_XSD_MT900, propSender, "banka", "MT900");

					//DocumentTransform.printDocument(decryptedDocument);
					System.out.println("-------------------RESPONSE MESSAGE---------------------------------");

					if(decryptedDocument != null){
						Element timestamp = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT900,"timestamp").item(0);
						String dateString = timestamp.getTextContent();
						Element rbrPorukeEl = (Element) decryptedDocument.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_MT900,"redniBrojPoruke").item(0);
						int rbrPoruke = Integer.parseInt(rbrPorukeEl.getTextContent());
						//String owner = SecurityClass.getOwner(decryptedDocument).toLowerCase();

						decryptedDocument = MessageTransform.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT900);
						decryptedDocument = MessageTransform.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_MT900);
						decryptedDocument = MessageTransform.removeSignature(decryptedDocument);


						//DocumentTransform.printDocument(decryptedDocument);


						String apsolute1 = DocumentTransform.class.getClassLoader().getResource("mt900.xml").toString().substring(6);

						SecurityClass sc = new SecurityClass();
						sc.saveDocument(decryptedDocument, apsolute1);
						JAXBContext context = JAXBContext.newInstance("beans.mt900");
						Unmarshaller unmarshaller = context.createUnmarshaller();
						@SuppressWarnings("unused")
						MT900 mt900 = (MT900) unmarshaller.unmarshal(new File(apsolute1));


						semaBanka = BankaDBUtil.loadBankaDatabase(propSender.getProperty("address"));
						semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setBrojac(rbrPoruke); //poslednje primljen mt
						semaBanka.getBrojacPoslednjePrimljeneNotifikacije().getCentralnabanka().setTimestamp(dateString); //poslednje primljen mt
						BankaDBUtil.storeBankaDatabase(semaBanka, propSender.getProperty("address"));
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

	public boolean testItMT102(Properties propSender, String receiver, String cert,String inputFile) {
		return true;
	}
}

