package provider.banka;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

import security.SecurityClass;
import util.ConstantsXWS;
import util.DocumentTransform;
import util.MessageTransform;

@Stateless
@ServiceMode(value = Service.Mode.PAYLOAD)
@WebServiceProvider(portName = "BankaIzvodPort", serviceName = "BankaIzvod", targetNamespace = ConstantsXWS.TARGET_NAMESPACE_BANKA_IZVOD, wsdlLocation = "WEB-INF/wsdl/BankaIzvod.wsdl")
public class IzvodProvider implements Provider<DOMSource> {
	private String message;
	private Document encrypted;
	private BigDecimal limit = new BigDecimal(250000);
	private Properties propReceiver;

	public IzvodProvider() {
	}

	@Override
	public DOMSource invoke(DOMSource request) {
		/*try {

			System.out.println("\nInvoking IzvodProvider\n");
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");

			Document document = DocumentTransform.convertToDocument(request);
			DocumentTransform.printDocument(document);
			System.out.println("-------------------REQUEST MESSAGE----------------------------------");
			System.out.println("\n");

			InputStream inputStreamReceiver = this.getClass().getClassLoader().getResourceAsStream("banka.properties");// /
			propReceiver = new Properties();
			propReceiver.load(inputStreamReceiver);

			Document decryptedDocument = MessageTransform.unpack(document, "Presek", "Presek",
					ConstantsXWS.TARGET_NAMESPACE_BANKA_IZVOD, propReceiver, "banka", "Presek");

			if (decryptedDocument == null) {
				return new DOMSource(encrypted);
			}

			String sender = SecurityClass.getOwner(decryptedDocument).toLowerCase();
			decryptedDocument = DocumentTransform.postDecryptTransform(decryptedDocument, propReceiver, "banka",
					"Presek");

			JAXBContext context = JAXBContext.newInstance("beans.nalog");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			ZahtevZaIzvod zahtevZaIzvod = (ZahtevZaIzvod) unmarshaller.unmarshal(decryptedDocument);

			if (!validateContent(zahtevZaIzvod)) {
				DocumentTransform.createNotificationResponse(message, ConstantsXWS.TARGET_NAMESPACE_BANKA_NALOG);
			} else {
				DbBank dbBank = DbBankUtil.loadDbBank(dbAddress);

				String brojRacunaKorisnika = zahtevZaIzvod.getBrojRacuna();
				TRacun racunKorisnika = dbBank.getRacunKlijenta(brojRacunaKorisnika);
				if (racunKorisnika == null) {
					LOG.info(bankName + ": " + "Racun korisnika ne postoji u ovoj banci");
					TInfoMessage infoMessage = new TInfoMessage();
					infoMessage.setCode("212");
					infoMessage.setMessage("Racun korisnika ne postoji u ovoj banci");
					throw new SimpleAnswer("", infoMessage);
				}

				List<NalogZaPrenos> naloziUKorist = new ArrayList<NalogZaPrenos>();
				List<NalogZaPrenos> naloziNaTeret = new ArrayList<NalogZaPrenos>();

				for (NalogZaPrenos nalog : dbBank.getRealizovaniNalozi().getNalogZaPrenos()) {
					if (brojRacunaKorisnika.equals(nalog.getRacunDuznika())) {
						naloziNaTeret.add(nalog);
					} else if (brojRacunaKorisnika.equals(nalog.getRacunPoverioca())) {
						naloziUKorist.add(nalog);
					}
				}

				// svi prihodi koji su bili nakon zadatog datuma
				BigDecimal sumaUKorist = new BigDecimal(0);
				for (NalogZaPrenos nalog : naloziUKorist) {
					// ako je na taj datum ili posle bila uplata
					if (nalog.getDatumNaloga().compare(zahtevZaIzvod.getDatum()) >= 0) {
						sumaUKorist = sumaUKorist.add(nalog.getIznos());
					}
				}

				// svi rashodi koji su bili nakon zadatog datuma
				BigDecimal sumaNaTeret = new BigDecimal(0);
				for (NalogZaPrenos nalog : naloziNaTeret) {
					// ako je na taj datum ili posle bila isplata
					if (nalog.getDatumNaloga().compare(zahtevZaIzvod.getDatum()) >= 0) {
						sumaNaTeret = sumaNaTeret.add(nalog.getIznos());
					}
				}

				// prethodno stanje je trenutno-sve uplate+sve isplate
				BigDecimal prethodnoStanje = racunKorisnika.getStanje().subtract(sumaUKorist).add(sumaNaTeret);

				List<NalogZaPrenos> naloziNaDan = new ArrayList<NalogZaPrenos>();

				// svi nalozi u korist na datum
				for (NalogZaPrenos nalog : naloziUKorist) {
					if (nalog.getDatumNaloga().compare(zahtevZaIzvod.getDatum()) == 0) {
						naloziNaDan.add(nalog);
					}
				}
				// svi nalozi na teret na datum
				for (NalogZaPrenos nalog : naloziNaTeret) {
					if (nalog.getDatumNaloga().compare(zahtevZaIzvod.getDatum()) == 0) {
						naloziNaDan.add(nalog);
					}
				}

				int velicinaPreseka = dbBank.getBrojStavkiPreseka();
				if ((naloziNaDan.size() == 0)
						|| (zahtevZaIzvod.getRedniBrojPreseka() - 1) * velicinaPreseka > naloziNaDan.size()) {
					// nema ni jednog naloga u preseku
					// ili se zahteva suvise visok presek tako da u njemu nema naloga
					LOG.info(bankName + ": " + "Ne postoji odgovarajuci presek");
					return null;
				} else {
					// napravi odgovarajuci presek i posalji kao odgovor
					LOG.info(bankName + ": " + "Uspesno generisan presek");
					Presek presek = createPresek(prethodnoStanje, naloziNaDan, zahtevZaIzvod, velicinaPreseka);
				}

				DocumentTransform.createNotificationResponse("Zahtev za izvod uspesno obradjen.",
						ConstantsXWS.TARGET_NAMESPACE_BANKA_IZVOD);
			}
			encrypted = MessageTransform.packS("Notifikacija", "Notification", apsolute, propReceiver, "cer" + sender,
					ConstantsXWS.NAMESPACE_XSD, "Notif");

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
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return new DOMSource(encrypted);*/
		return null;
	}

	/*private Presek createPresek(BigDecimal prethodnoStanje, List<NalogZaPrenos> naloziZaPresek,
			ZahtevZaIzvod zahtevZaIzvod, int velicinaPreseka) {

		Presek presek = new Presek();
		TZaglavljePresek zaglavlje = new TZaglavljePresek();

		zaglavlje.setBrojPreseka(zahtevZaIzvod.getRedniBrojPreseka());
		zaglavlje.setDatumNaloga(zahtevZaIzvod.getDatum());
		zaglavlje.setBrojRacuna(zahtevZaIzvod.getBrojRacuna());
		zaglavlje.setPrethodnoStanje(prethodnoStanje);
		presek.setZaglavlje(zaglavlje);

		Stavke stavke = new Stavke();

		BigDecimal sumaTeret = new BigDecimal(0);
		BigDecimal sumaKorist = new BigDecimal(0);
		int brojNaloziNaTeret = 0;
		int brojNaloziUKorist = 0;
		for (NalogZaPrenos nalog : naloziZaPresek) {
			if (nalog.getRacunDuznika().equals(zahtevZaIzvod.getBrojRacuna())) {
				brojNaloziNaTeret++;
				sumaTeret = sumaTeret.add(nalog.getIznos());
			} else {
				brojNaloziUKorist++;
				sumaKorist = sumaKorist.add(nalog.getIznos());
			}
		}

		presek.getZaglavlje().setBrojPromenaNaTeret(brojNaloziNaTeret);
		presek.getZaglavlje().setBrojPromenaUKorist(brojNaloziUKorist);
		presek.getZaglavlje().setUkupnoNaTeret(sumaTeret);
		presek.getZaglavlje().setUkupnoUKorist(sumaKorist);
		presek.getZaglavlje().setNovoStanje(prethodnoStanje.add(sumaKorist.subtract(sumaTeret)));

		// stavke
		int min = (zahtevZaIzvod.getRedniBrojPreseka() - 1) * velicinaPreseka;
		int max = Math.min(zahtevZaIzvod.getRedniBrojPreseka() * velicinaPreseka, naloziZaPresek.size());
		for (int i = min; i < max; i++) {
			NalogZaPrenos nalog = naloziZaPresek.get(i);
			TStavkaPresek stavka = new TStavkaPresek();
			stavka.setDatumNaloga(nalog.getDatumNaloga());
			stavka.setDatumValute(nalog.getDatumValute());
			stavka.setIznos(nalog.getIznos());
			stavka.setModelOdobrenja(nalog.getModelOdobrenja().intValue());
			stavka.setModelZaduzenja(nalog.getModelZaduzenja().intValue());
			stavka.setNalogodavac(nalog.getNalogodavac());
			stavka.setPoverilac(nalog.getPoverilac());
			stavka.setPozivNaBrojOdobrenja(nalog.getPozivNaOdobrenje());
			stavka.setPozivNaBrojZaduzenja(nalog.getPozivNaBrojZaduzenja());
			stavka.setRacunDuznika(nalog.getRacunDuznika());
			stavka.setRacunPoverioca(nalog.getRacunPoverioca());
			stavka.setSvrhaPlacanja(nalog.getSvrhaPlacanja());
			if (nalog.getRacunDuznika().equals(zahtevZaIzvod.getBrojRacuna())) {
				stavka.setSmer("t");
			} else {
				stavka.setSmer("k");
			}
			stavke.getStavka().add(stavka);
		}
		presek.setStavke(stavke);

		return presek;
	}*/

}
