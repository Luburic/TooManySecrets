package firma.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

import org.basex.query.value.item.Str;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import security.SecurityClass;
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
import util.Validation;
import ws.style.client.FakturaClient;
import beans.faktura.Faktura;
import beans.faktura.Faktura.Zaglavlje;
import firma.gui.MainFrame;

@SuppressWarnings("serial")
public class ZaglavljeDialog extends JDialog {

	private JLabel lbIdPor = new JLabel("Id poruke:");
	private JLabel lbNazivDob = new JLabel("Naziv dobavljaca:");
	private JLabel lbAdrDob = new JLabel("Adresa dobavljaca:");

	private JLabel lbPibDob = new JLabel("Pib dobavljaca:");
	private JLabel lbNazKup = new JLabel("Naziv kupca:");

	private JLabel lbAdrKup = new JLabel("Adresa kupca:");
	private JLabel lbPibKup = new JLabel("Pib kupca:");

	private JLabel lbBrRac = new JLabel("Broj racuna:");
	private JLabel lbDatRac = new JLabel("Datum racuna:");

	private JLabel lbVrRob = new JLabel("Vrednost robe:");
	private JLabel lbVrUsl = new JLabel("Vrednost usluga:");

	private JLabel lbUkupRU = new JLabel("Ukupno roba/usluge:");
	private JLabel lbUkupRab = new JLabel("Ukupan rabat:");

	private JLabel lbUkuPor = new JLabel("Ukupan porez:");
	private JLabel lbOznVal = new JLabel("Oznaka valute:");

	private JLabel lbIznUpl = new JLabel("Iznos za uplatu:");
	private JLabel lbUplRac = new JLabel("Uplata na racun:");

	private JLabel lbDatVal = new JLabel("Datum valute:");

	/****************  **************************/

	private JTextField tfIdPor = new JTextField();
	private JTextField tfNazivDob = new JTextField();
	private JTextField tfAdrDob = new JTextField();

	private JTextField tfPibDob = new JTextField();
	private JTextField tfNazKup = new JTextField();

	private JTextField tfAdrKup = new JTextField();
	private JTextField tfPibKup = new JTextField();

	private JTextField tfBrRac = new JTextField();
	private JDatePickerImpl tfDatRac = new JDatePickerImpl(new JDatePanelImpl(
			null));

	private JTextField tfVrRob = new JTextField();
	private JTextField tfVrUsl = new JTextField();

	private JTextField tfUkupRU = new JTextField();
	private JTextField tfUkupRab = new JTextField();

	private JTextField tfUkuPor = new JTextField();
	private JTextField tfOznVal = new JTextField();

	private JTextField tfIznUpl = new JTextField();
	private JTextField tfUplRac = new JTextField();

	private JDatePickerImpl tfDatVal = new JDatePickerImpl(new JDatePanelImpl(
			null));

	/**************** **************************/
	private JButton btnOk = new JButton("Potvrdi");
	private JButton btnCancel = new JButton("Otkazi");
	private JButton btnStv = new JButton("Dodaj stavku");
	private JButton btnZav = new JButton("Zavrsi");

	private Faktura faktura;
	private ZaglavljeDialog fd;

	private Marshaller marshaller;

	public ZaglavljeDialog(MainFrame instance) {
		super(instance);
		fd = this;

		if (fd.getFaktura() == null) {
			btnStv.setEnabled(false);
			btnZav.setEnabled(false);
		}

		setTitle("Popunjavanje fakture");
		setResizable(false);
		setSize(new Dimension(400, 300));
		setLayout(new MigLayout("fill"));

		add(lbIdPor);
		tfIdPor.setMinimumSize(new Dimension(80, 20));
		add(tfIdPor, "wrap");

		add(lbNazivDob);
		tfNazivDob.setMinimumSize(new Dimension(80, 20));
		add(tfNazivDob, "wrap");

		add(lbAdrDob);
		tfAdrDob.setMinimumSize(new Dimension(80, 20));
		add(tfAdrDob, "wrap");

		add(lbPibDob);
		tfPibDob.setMinimumSize(new Dimension(80, 20));
		add(tfPibDob, "wrap");

		add(lbNazKup);
		tfNazKup.setMinimumSize(new Dimension(80, 20));
		add(tfNazKup, "wrap");

		add(lbAdrKup);
		tfAdrKup.setMinimumSize(new Dimension(80, 20));
		add(tfAdrKup, "wrap");
		tfDatRac.setFocusable(true);

		add(lbPibKup);
		tfPibKup.setMinimumSize(new Dimension(80, 20));
		add(tfPibKup, "wrap");

		add(lbBrRac);
		tfBrRac.setMinimumSize(new Dimension(80, 20));
		add(tfBrRac, "wrap");

		add(lbDatRac);
		// tfDatRac.setMinimumSize(new Dimension(40, 20));
		add(tfDatRac);

		add(lbVrRob);
		tfVrRob.setMinimumSize(new Dimension(50, 20));
		add(tfVrRob, "wrap");

		add(lbVrUsl);
		tfVrUsl.setMinimumSize(new Dimension(80, 20));
		add(tfVrUsl, "wrap");

		add(lbUkupRU);
		tfUkupRU.setMinimumSize(new Dimension(80, 20));
		add(tfUkupRU, "wrap");

		add(lbUkupRab);
		tfUkupRab.setMinimumSize(new Dimension(80, 20));
		add(tfUkupRab, "wrap");

		add(lbUkuPor);
		tfUkuPor.setMinimumSize(new Dimension(80, 20));
		add(tfUkuPor, "wrap");

		add(lbOznVal);
		tfOznVal.setMinimumSize(new Dimension(80, 20));
		add(tfOznVal, "wrap");

		add(lbIznUpl);
		tfIznUpl.setMinimumSize(new Dimension(80, 20));
		add(tfIznUpl, "wrap");

		add(lbUplRac);
		tfUplRac.setMinimumSize(new Dimension(80, 20));
		add(tfUplRac, "wrap");

		add(lbDatVal);
		tfDatVal.setMinimumSize(new Dimension(80, 20));
		add(tfDatVal, "wrap");

		add(btnStv, "gapleft 70");
		add(btnZav);

		add(btnOk, "gapleft 10");
		add(btnCancel);

		pack();
		setLocationRelativeTo(instance);

		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});

		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (("").equals(tfIdPor.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos id-a poruke!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfIdPor.requestFocus();
					return;
				}

				
				if (("").equals(tfNazivDob.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos naziva dobavljaca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfNazivDob.requestFocus();
					return;
				}

				if (("").equals(tfAdrDob.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos adrese dobavljaca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfAdrDob.requestFocus();
					return;
				}
				
				
				if (("").equals(tfPibDob.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos pib-a dobavljaca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfPibDob.requestFocus();
					return;
				}
				
				
				if (("").equals(tfNazKup.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos naziva kupca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfNazKup.requestFocus();
					return;
				}

				if (("").equals(tfAdrKup.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos adrese kupca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfAdrKup.requestFocus();
					return;
				}

				
				
				if (("").equals(tfPibKup.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos pib-a kupca!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfPibKup.requestFocus();
					return;
				}
				
				
				
				if (("").equals(tfBrRac.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos broja racuna!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfBrRac.requestFocus();
					return;
				}

				int brRac = 0;

				try {
					brRac = Integer.valueOf(tfBrRac.getText());
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Pogresno unet broj racuna!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfBrRac.requestFocus();
					return;
				}
				
				
				if (("").equals(tfDatRac.getJFormattedTextField().getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos datuma racuna!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfDatRac.requestFocus();
					return;
				}
				
				
				
				
				
				

				
			

				if (("").equals(tfVrRob.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos vrednosti robe!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfVrRob.requestFocus();
					return;
				}

				BigDecimal vr =null;
				try {
					vr= BigDecimal.valueOf(new Double(tfVrRob.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost robe!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfVrRob.requestFocus();
					return;
				}
				
				
				if (("").equals(tfVrUsl.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos vrednosti usluge!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfVrUsl.requestFocus();
					return;
				}
				
				BigDecimal vu =null;
				try {
					vu= BigDecimal.valueOf(new Double(tfVrUsl.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost usluge!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfVrUsl.requestFocus();
					return;
				}
				/***/
				

				if (("").equals(tfUkupRU.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos ukupne robe/usluge!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfUkupRU.requestFocus();
					return;
				}
				
				BigDecimal uru =null;
				try {
					uru= BigDecimal.valueOf(new Double(tfUkupRU.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost za ukupano robe i usluge!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfUkupRU.requestFocus();
					return;
				}
				/***/
				

				if (("").equals(tfUkupRab.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos ukupnog rabata!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfUkupRab.requestFocus();
					return;
				}
				
				/***/
				BigDecimal um =null;
				try {
					um= BigDecimal.valueOf(new Double(tfUkupRab.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost za ukupan rabat!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfUkupRab.requestFocus();
					return;
				}

				if (("").equals(tfUkuPor.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos ukupnog poreza!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfUkuPor.requestFocus();
					return;
				}
				BigDecimal up =null;
				try {
					up = BigDecimal.valueOf(new Double(tfUkuPor.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost za ukupan porez!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfUkuPor.requestFocus();
					return;
				}

				/***/
				
				if (("").equals(tfOznVal.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos oznake valute!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfOznVal.requestFocus();
					return;
				}

				if (("").equals(tfIznUpl.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos iznosa uplate!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfIznUpl.requestFocus();
					return;
				}
				
				BigDecimal iz =null;
				try {
					iz = BigDecimal.valueOf(new Double(tfIznUpl.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost iznosa uplate!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfIznUpl.requestFocus();
					return;
				}
				/***/
				
				

				if (("").equals(tfUplRac.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos racuna na koji se uplacuje!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfUplRac.requestFocus();
					return;
				}

				
				if (("").equals(tfDatVal.getJFormattedTextField().getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos datuma valute!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfDatVal.requestFocus();
					return;
				}


				Zaglavlje zaglavlje = new Zaglavlje();

				zaglavlje.setAdresaDobavljaca(tfAdrDob.getText());
				zaglavlje.setAdresaKupca(tfAdrKup.getText());
				zaglavlje.setBrojRacuna(brRac);
				zaglavlje.setVrednostRobe(vr);
				
				
				StringTokenizer st = new StringTokenizer(tfDatRac.getJFormattedTextField().getText(), ".");
				String dd = st.nextToken();	
				String mm = st.nextToken();
				String yyyy = st.nextToken();
				
				zaglavlje.setDatumRacuna(MyDatatypeConverter.parseDate(yyyy+"-"+mm+"-"+dd));
				
				
				
				StringTokenizer stt = new StringTokenizer(tfDatVal.getJFormattedTextField().getText(), ".");
				String ddd = stt.nextToken();
				String mmm = stt.nextToken();
				String yyyyy = stt.nextToken();
				zaglavlje.setDatumValute(MyDatatypeConverter.parseDate(yyyyy+"-"+mmm+"-"+ddd));
				
				
				zaglavlje.setIdPoruke(tfIdPor.getText());
				zaglavlje.setIznosZaUplatu(iz);
				zaglavlje.setNazivDobavljaca(tfNazivDob.getText());
				zaglavlje.setNazivKupca(tfNazKup.getText());
				zaglavlje.setOznakaValute(tfOznVal.getText());
				zaglavlje.setPibDobavljaca(tfPibDob.getText());
				zaglavlje.setPibKupca(tfPibKup.getText());
				zaglavlje.setUkupanPorez(up);
				zaglavlje.setUkupanRabat(um);
				zaglavlje.setUkupnoRobaIUsluge(uru);
				
				zaglavlje.setVrednostUsluga(vu);
				zaglavlje.setUplataNaRacun(tfUplRac.getText());

				faktura = new Faktura();
				faktura.setZaglavlje(zaglavlje);
				new StavkaDialog(fd).setVisible(true);

			}
		});

		btnStv.addActionListener(new ActionListener() {
			// faktura != null
			@Override
			public void actionPerformed(ActionEvent e) {
				new StavkaDialog(fd).setVisible(true);
			}
		});

		btnZav.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					Faktura fakturaZaSlanje = fd.getFaktura();

					JAXBContext context = JAXBContext
							.newInstance("beans.faktura");
					// Klasa za transformisanje objektnog modela u XML
					marshaller = context.createMarshaller();
					// na ovaj naci se setuje koji prefiks se koristi za koji
					// namespace
					marshaller.setProperty(
							"com.sun.xml.bind.namespacePrefixMapper",
							new NSPrefixMapper());
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
							Boolean.TRUE);
					marshaller.marshal(fakturaZaSlanje, new File(
							"./FakturaTest/Faktura2.xml"));
					
					Document doc = Validation.buildDocumentWithoutValidation("./FakturaTest/Faktura2.xml");
					Element faktura = (Element) doc.getElementsByTagName("faktura").item(0);
					faktura.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
					//faktura.setAttribute("xsi:schemaLocation", "http://www.toomanysecrets.com/tipovi file:/http://localhost:8080/ws_style/services/Faktura?xsd=../shema/FakturaRaw.xsd");
					SecurityClass sc = new SecurityClass();
					sc.saveDocument(doc, "./FakturaTest/Faktura2.xml");
					
					
					// isto ce se informacije o firmi (password i putanja i tako to citati iz nekog properties fajla pa ce se prosledjivati testIt metodi)
					FakturaClient.testIt("firmaa", "firmaa", "./WEB-INF/keystores/firmaa.jks", "firmaa","./FakturaTest/Faktura2.xml");

					JOptionPane.showMessageDialog(null,
							"Uspesno kreirana(i poslata*) faktura.",
							"Kreiranje fakture", JOptionPane.INFORMATION_MESSAGE);
					setVisible(false);
					
					
				} catch (PropertyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JAXBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
		});

	}

	public Faktura getFaktura() {
		return faktura;
	}

	public void setFaktura(Faktura faktura) {
		this.faktura = faktura;
	}

	public JButton getBtnStv() {
		return btnStv;
	}

	public void setBtnStv(JButton btnStv) {
		this.btnStv = btnStv;
	}

	public JButton getBtnOk() {
		return btnOk;
	}

	public void setBtnOk(JButton btnOk) {
		this.btnOk = btnOk;
	}

	public JButton getBtnZav() {
		return btnZav;
	}

	public void setBtnZav(JButton btnZav) {
		this.btnZav = btnZav;
	}

}
