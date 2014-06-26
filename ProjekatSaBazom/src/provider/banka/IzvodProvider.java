package provider.banka;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
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
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;
import util.Validation;
import basexdb.banka.BankeSema;
import basexdb.banka.BankeSema.RealizovaniNalozi.RealizovanNalog;
import basexdb.util.BankaDBUtil;
import beans.izvod.Izvod;
import beans.nalog.Nalog;
import beans.presek.Presek;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaIzvodPort", 
					serviceName = "BankaIzvod",
					targetNamespace = "http://www.toomanysecrets.com/BankaIzvod",
					wsdlLocation = "WEB-INF/wsdl/BankaIzvod.wsdl")
public class IzvodProvider  implements Provider<DOMSource>{
	
	public static final String TARGET_NAMESPACE = "http://www.toomanysecrets.com/BankaIzvod";
	public static final String NAMESPACE_SPEC_NS = "http://www.w3.org/2000/xmlns/";
	private Properties propReceiver;
	private Document encrypted;
	private String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
	
	
	public IzvodProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public DOMSource invoke(DOMSource request) {
		try {
			// serijalizacija DOM-a na ekran
			System.out.println("\nInvoking FakturaProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);
			
			
			Element esender = (Element) document.getElementsByTagName("izvod").item(0);
			String sender = esender.getAttribute("sender");

			Document decryptedDocument = MessageTransform.unpack(document, "Izvod", "Izvod",
					ConstantsXWS.NAMESPACE_XSD_ZAHTEV, propReceiver, "firma", "Izvod");
			
			Document forSave = null;
			if(decryptedDocument != null) {
				Reader reader = Validation.createReader(decryptedDocument);
				forSave = Validation.buildDocumentWithValidation(reader, new String[]{ "http://localhost:8080/IzvodSigned.xsd","http://localhost:8080/xmldsig-core-schema.xsd"});
			}
			
			BankeSema semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
			semaBanka.setBrojacStavkiZaPresek(1);
			
			if (forSave != null) {
				Element timestamp = (Element) decryptedDocument
						.getElementsByTagNameNS(ConstantsXWS.NAMESPACE_XSD_ZAHTEV, "timestamp")
						.item(0);
				String dateString = timestamp.getTextContent();
				sender = SecurityClass.getOwner(decryptedDocument)
						.toLowerCase();
				decryptedDocument = MessageTransform
						.removeTimestamp(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_ZAHTEV);
				decryptedDocument = MessageTransform
						.removeRedniBrojPoruke(decryptedDocument, ConstantsXWS.NAMESPACE_XSD_ZAHTEV);
				decryptedDocument = MessageTransform
						.removeSignature(decryptedDocument);
				
				JAXBContext context = JAXBContext.newInstance("beans.izvod");
				Unmarshaller unmarshaller = context.createUnmarshaller();
				Izvod izvod = null;
				try {
					izvod = (Izvod) unmarshaller
							.unmarshal(decryptedDocument);
				} catch (JAXBException e) {
					encrypted = null;
					return new DOMSource(encrypted);
				}
				
				String brojRacuna = izvod.getBrojRacuna();
				BankeSema.KorisnickiRacuni.Racun racunIzBaze = semaBanka.getKorisnickiRacuni().getRacunByNazivKlijenta(sender);
				
				if(racunIzBaze == null) {
					System.out.println("456, Racun ne postoji u banci");
					encrypted = null;
				} else {
					
					List<Nalog> naloziUKorist = new ArrayList<>();
					List<Nalog> naloziNaTeret = new ArrayList<>();
					
					for(RealizovanNalog nalog : semaBanka.getRealizovaniNalozi().getRealizovanNalog()){
						if(brojRacuna.equals(nalog.getNalog().getRacunDuznika())){
							naloziNaTeret.add(nalog.getNalog());
						}else if(brojRacuna.equals(nalog.getNalog().getRacunPoverioca())){
							naloziUKorist.add(nalog.getNalog());
						}
					}
					
					//sve sto je bilo posle navedenog datuma u korist
					BigDecimal sumaUKorist = new BigDecimal(0);
					for(Nalog nalog : naloziUKorist){
						if(nalog.getDatumNaloga().compareTo(izvod.getDatum()) >= 0){
							sumaUKorist = sumaUKorist.add(nalog.getIznos());
						}
					}
					
					//sve sto je bilo posle navedenog datuma na teret
					BigDecimal sumaNaTeret = new BigDecimal(0);
					for(Nalog nalog : naloziNaTeret){
						if(nalog.getDatumNaloga().compareTo(izvod.getDatum()) >= 0){
							sumaNaTeret=sumaNaTeret.add(nalog.getIznos());
						}
					}
					
					BigDecimal prethodnoStanje = racunIzBaze.getStanje().subtract(sumaUKorist).add(sumaNaTeret);
					
					List<Nalog> naloziNaDan = new ArrayList<Nalog>();
					
					//svi nalozi u korist na datum
					for(Nalog nalog : naloziUKorist){
						if(nalog.getDatumNaloga().compareTo(izvod.getDatum()) == 0){
							naloziNaDan.add(nalog);
						}
					}
					//svi nalozi na teret na datum
					for(Nalog nalog : naloziNaTeret){
						if(nalog.getDatumNaloga().compareTo(izvod.getDatum()) == 0){
							naloziNaDan.add(nalog);
						}
					}
					
					BigDecimal ukupnoNaTeret = new BigDecimal(0);
					BigDecimal ukupnoUKorist = new BigDecimal(0);
					int brojNaloziNaTeret = 0;
					int brojNaloziUKorist = 0;
					for (Nalog nalog : naloziNaDan) {
						if (nalog.getRacunDuznika().equals(izvod.getBrojRacuna())) {
							brojNaloziNaTeret++;
							ukupnoNaTeret = ukupnoNaTeret.add(nalog.getIznos());
						} else {
							brojNaloziUKorist++;
							ukupnoUKorist = ukupnoUKorist.add(nalog.getIznos());
						}
					}
					
					BigDecimal novoStanje = prethodnoStanje.add(ukupnoUKorist.subtract(ukupnoNaTeret));
					int maxBrojStavkiZaPresek = semaBanka.getBrojacStavkiZaPresek();
					
					Presek presek = null;
					
					if((naloziNaDan.size()==0 ||
						(((izvod.getRedniBrojPreseka().subtract(BigInteger.valueOf(1))).multiply(BigInteger.valueOf(maxBrojStavkiZaPresek))).compareTo(BigInteger.valueOf(naloziNaDan.size())) > 0))) {
						//nema ni jednog naloga u preseku
						//ili se zahteva suvise visok presek tako da u njemu nema naloga
						presek = popuniPresekNull();
						presek.setSender(propReceiver.getProperty("naziv"));
					}else{
						//napravi odgovarajuci presek i posalji kao odgovor
						presek = popuniPresek(izvod, maxBrojStavkiZaPresek, prethodnoStanje, brojNaloziNaTeret, brojNaloziUKorist, ukupnoNaTeret, ukupnoUKorist, novoStanje, naloziNaDan);
						presek.setSender(propReceiver.getProperty("naziv"));
					}
					
					semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
					semaBanka.getBrojacPoslednjegPrimljenogZahtevaZaIzvod().getFirmaByNaziv(sender).setBrojac(
							semaBanka.getBrojacPoslednjegPrimljenogZahtevaZaIzvod().getFirmaByNaziv(sender).getBrojac()+1);
					semaBanka.getBrojacPoslednjegPrimljenogZahtevaZaIzvod().getFirmaByNaziv(sender).setTimestamp(dateString);
					BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));

					try {

						JAXBContext contextForSending = JAXBContext.newInstance("beans.presek");
						Marshaller marshaller = contextForSending.createMarshaller();
						marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
						
						String apsolute = DocumentTransform.class.getClassLoader().getResource("Notification.xml").toString().substring(6);
						System.out.println("NOTIFICATION RESPONSE URL: "+apsolute);
						marshaller.marshal(presek, new File(apsolute));
					
						
					} catch (PropertyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					encrypted = MessageTransform.packS("Presek", "Presek", apsolute, propReceiver, "cer"+sender ,ConstantsXWS.NAMESPACE_XSD_PRESEK, "Presek");
					DocumentTransform.printDocument(encrypted);
					if(encrypted != null) {
						semaBanka = BankaDBUtil.loadBankaDatabase(propReceiver.getProperty("address"));
						semaBanka.setBrojacPoslednjegPoslatogPreseka(semaBanka.getBrojacPoslednjegPoslatogPreseka()+1);
						BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));
					}
					
				}
			} else {
				return new DOMSource(null);
			}
			
			BankaDBUtil.storeBankaDatabase(semaBanka, propReceiver.getProperty("address"));
			
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
	
	public Presek popuniPresek(Izvod izvod, int maxBrojStavkiZaPresek, BigDecimal prethodnoStanje, int brojNaloziNaTeret, int brojNaloziUKorist, BigDecimal ukupnoNaTeret, BigDecimal ukupnoUKorist, BigDecimal novoStanje, List<Nalog> naloziNaDan ) {
		
		Presek presek = new Presek();
		Presek.Zaglavlje zaglavlje = new Presek.Zaglavlje();
		
		zaglavlje.setBrojRacuna(izvod.getBrojRacuna());
		zaglavlje.setDatumNaloga(izvod.getDatum());
		zaglavlje.setBrojPreseka(izvod.getRedniBrojPreseka().intValue());
		zaglavlje.setPrethodnoStanje(prethodnoStanje);
		zaglavlje.setBrojPromenaUKorist(brojNaloziUKorist);
		zaglavlje.setUkupnoUKorist(ukupnoUKorist);
		zaglavlje.setBrojPromenaNaTeret(brojNaloziNaTeret);
		zaglavlje.setUkupnoNaTeret(ukupnoNaTeret);
		zaglavlje.setNovoStanje(novoStanje);
		presek.setZaglavlje(zaglavlje);
		
		Presek.Stavke stavke = new Presek.Stavke();
		
		// stavke
		int min=(izvod.getRedniBrojPreseka().intValue()-1)*maxBrojStavkiZaPresek;
		int max=Math.min(izvod.getRedniBrojPreseka().intValue()*maxBrojStavkiZaPresek,naloziNaDan.size());
		for (int i=min;i<max;i++) {
			Nalog nalog = naloziNaDan.get(i);
			Presek.Stavke.Stavka stavka = new Presek.Stavke.Stavka();
			stavka.setDuznikNalogodavac(nalog.getDuznikNalogodavac());
			stavka.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
			stavka.setPrimalacPoverilac(nalog.getPrimalacPoverilac());
			stavka.setDatumNaloga(nalog.getDatumNaloga());
			stavka.setDatumValute(nalog.getDatumValute());
			stavka.setRacunDuznika(nalog.getRacunDuznika());
			stavka.setModelZaduzenja(nalog.getModelZaduzenja());
			stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
			stavka.setRacunPoverioca(nalog.getRacunPoverioca());
			stavka.setModelOdobrenja(nalog.getModelOdobrenja());
			stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaBrojOdobrenja());
			stavka.setIznos(nalog.getIznos());
			if (nalog.getRacunDuznika().equals(izvod.getBrojRacuna())){
				stavka.setSmer("-");
			}else{
				stavka.setSmer("+");
			}
			stavke.getStavka().add(stavka);
		}
		presek.setStavke(stavke);

		return presek;
		
	}
	
	public Presek popuniPresekNull() {
		
		Presek presek = new Presek();
		Presek.Zaglavlje zaglavlje = new Presek.Zaglavlje();
		
		zaglavlje.setBrojRacuna("000000000000000000");
		zaglavlje.setDatumNaloga(new Date());
		zaglavlje.setBrojPreseka(0);
		zaglavlje.setPrethodnoStanje(new BigDecimal(0));
		zaglavlje.setBrojPromenaUKorist(0);
		zaglavlje.setUkupnoUKorist(new BigDecimal(0));
		zaglavlje.setBrojPromenaNaTeret(0);
		zaglavlje.setUkupnoNaTeret(new BigDecimal(0));
		zaglavlje.setNovoStanje(new BigDecimal(0));
		presek.setZaglavlje(zaglavlje);
		
		Presek.Stavke stavke = new Presek.Stavke();
		
		// stavke
		Presek.Stavke.Stavka stavka = new Presek.Stavke.Stavka();
		stavka.setDuznikNalogodavac("0");
		stavka.setSvrhaPlacanja("0");
		stavka.setPrimalacPoverilac("0");
		stavka.setDatumNaloga(new Date());
		stavka.setDatumValute(new Date());
		stavka.setRacunDuznika("000000000000000000");
		stavka.setModelZaduzenja(0);
		stavka.setPozivNaBrojZaduzenja("0");
		stavka.setRacunPoverioca("000000000000000000");
		stavka.setModelOdobrenja(0);
		stavka.setPozivNaBrojOdobrenja("0");
		stavka.setIznos(new BigDecimal(0));
		stavka.setSmer("-");
	
		stavke.getStavka().add(stavka);
		presek.setStavke(stavke);

		return presek;
		
	}

	
}
