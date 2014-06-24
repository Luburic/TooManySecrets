package firma.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import client.firma.FakturaClient;
import security.SecurityClass;
import util.MyDatatypeConverter;
import util.NSPrefixMapper;
import util.Validation;
import beans.faktura.Faktura;
import beans.faktura.Faktura.Zaglavlje;
import firma.gui.MainFrame;

@SuppressWarnings("serial")
public class ZaglavljeDialog extends JDialog {

	private JLabel lbIdPor = new JLabel("Id poruke:");
	private JLabel lbNazivDob = new JLabel("Naziv dobavljaèa:");
	private JLabel lbAdrDob = new JLabel("Adresa dobavljaca:");

	private JLabel lbPibDob = new JLabel("Pib dobavljaca(11):");
	private JLabel lbNazKup = new JLabel("Naziv kupca:");

	private JLabel lbAdrKup = new JLabel("Adresa kupca:");
	private JLabel lbPibKup = new JLabel("Pib kupca(11):");

	private JLabel lbBrRac = new JLabel("Broj racuna(6):");
	private JLabel lbDatRac = new JLabel("Datum racuna:");

	private JLabel lbVrRob = new JLabel("Vrednost robe:");
	private JLabel lbVrUsl = new JLabel("Vrednost usluga:");

	private JLabel lbUkupRU = new JLabel("Ukupno roba/usluge:");
	private JLabel lbUkupRab = new JLabel("Ukupan rabat:");

	private JLabel lbUkuPor = new JLabel("Ukupan porez:");
	private JLabel lbOznVal = new JLabel("Oznaka valute:");

	private JLabel lbIznUpl = new JLabel("Iznos za uplatu:");
	private JLabel lbUplRac = new JLabel("Uplata na racun(18):");

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
	private int brRac = 0;
	private BigDecimal vr =null; //vrsta robe
	private BigDecimal vu =null; //vrsta usluge
	private BigDecimal iz =null; //iznos uplate
	private BigDecimal uru =null; //ukupno robaIusluga
	private BigDecimal um =null; //ukupan rabat
	private BigDecimal up =null; // ukupan porez
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
		
		initialize();
		
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
				
				
				
				if(tfPibDob.getText().length()!=11) {
					JOptionPane.showMessageDialog(null,
							"Neispravna duzina za pib dobavljaca!", "Greska",
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
				
				
				if(tfPibKup.getText().length()!=11) {
					JOptionPane.showMessageDialog(null,
							"Neispravna duzina za pib kupca!", "Greska",
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
				
				if (tfBrRac.getText().length()!=6){
					JOptionPane.showMessageDialog(null,
							"Neodgovarajuca duzina racuna!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfBrRac.requestFocus();
					return;
				}
				
				

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
				
				
				try {
					vu= BigDecimal.valueOf(new Double(tfVrUsl.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost usluge!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfVrUsl.requestFocus();
					return;
				}
				

				if (("").equals(tfUkupRU.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos ukupne robe/usluge!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfUkupRU.requestFocus();
					return;
				}
				
				
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

				
				if (tfUplRac.getText().length()!=18) {
					JOptionPane.showMessageDialog(null,
							"Neispravna duzina racuna!",
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

					JAXBContext context = JAXBContext.newInstance("beans.faktura");
				
					marshaller = context.createMarshaller();
				
					marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",new NSPrefixMapper("faktura"));
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
					marshaller.marshal(fakturaZaSlanje, new File("./FakturaTest/Faktura2.xml"));
					
					Document doc = Validation.buildDocumentWithoutValidation("./FakturaTest/Faktura2.xml");
					Element faktura = (Element) doc.getElementsByTagName("faktura").item(0);
					faktura.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
					SecurityClass sc = new SecurityClass();
					sc.saveDocument(doc, "./FakturaTest/Faktura2.xml");
					
					
					FakturaClient fc = new FakturaClient();
					fc.testIt("firmaA", "firmab", "cerfirmab","./FakturaTest/Faktura2.xml");

					String message ="";
					JOptionPane.showMessageDialog(null,message,"Kreiranje fakture", JOptionPane.INFORMATION_MESSAGE);
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
	
	
	
	
	
	public void initialize(){
		

		tfIdPor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfIdPor.getText().length()>49){
					tfIdPor.setText(tfIdPor.getText().substring(0,tfIdPor.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfNazivDob.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfNazivDob.getText().length()>254){
					tfNazivDob.setText(tfNazivDob.getText().substring(0,tfNazivDob.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		
		tfAdrDob.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfAdrDob.getText().length()>49){
					tfAdrDob.setText(tfAdrDob.getText().substring(0,tfAdrDob.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfPibDob.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfPibDob.getText().length()>10){
					tfPibDob.setText(tfPibDob.getText().substring(0,tfPibDob.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfPibKup.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfPibKup.getText().length()>10){
					tfPibKup.setText(tfPibKup.getText().substring(0,tfPibKup.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfNazKup.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfNazKup.getText().length()>54){
					tfNazKup.setText(tfNazKup.getText().substring(0,tfNazKup.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		
		tfAdrKup.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfAdrKup.getText().length()>54){
					tfAdrKup.setText(tfAdrKup.getText().substring(0,tfAdrKup.getText().length()-1));
					return;
				}
			}
			
		});
		
		tfBrRac.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfBrRac.getText().length()>5){
					tfBrRac.setText(tfBrRac.getText().substring(0,tfBrRac.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfVrRob.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfVrRob.getText().length()>17){
					tfVrRob.setText(tfVrRob.getText().substring(0,tfVrRob.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfVrUsl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfVrUsl.getText().length()>17){
					tfVrUsl.setText(tfVrUsl.getText().substring(0,tfVrUsl.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfUkupRU.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfUkupRU.getText().length()>17){
					tfUkupRU.setText(tfUkupRU.getText().substring(0,tfUkupRU.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfUkupRab.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfUkupRab.getText().length()>17){
					tfUkupRab.setText(tfUkupRab.getText().substring(0,tfUkupRab.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfUkuPor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfUkuPor.getText().length()>17){
					tfUkuPor.setText(tfUkuPor.getText().substring(0,tfUkuPor.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		tfOznVal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfOznVal.getText().length()>2){
					tfOznVal.setText(tfOznVal.getText().substring(0,tfOznVal.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfIznUpl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfIznUpl.getText().length()>17){
					tfIznUpl.setText(tfIznUpl.getText().substring(0,tfIznUpl.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		tfUplRac.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(tfUplRac.getText().length()>17){
					tfUplRac.setText(tfUplRac.getText().substring(0,tfUplRac.getText().length()-1));
					return;
				}
			}
			
		});
		
		
		
		add(lbIdPor);
		tfIdPor.setMinimumSize(new Dimension(140, 20));
		add(tfIdPor, "wrap");

		add(lbNazivDob);
		tfNazivDob.setMinimumSize(new Dimension(140, 20));
		add(tfNazivDob, "wrap");

		add(lbAdrDob);
		tfAdrDob.setMinimumSize(new Dimension(140, 20));
		add(tfAdrDob, "wrap");

		add(lbPibDob);
		tfPibDob.setMinimumSize(new Dimension(140, 20));
		add(tfPibDob, "wrap");

		add(lbNazKup);
		tfNazKup.setMinimumSize(new Dimension(140, 20));
		add(tfNazKup, "wrap");

		add(lbAdrKup);
		tfAdrKup.setMinimumSize(new Dimension(140, 20));
		add(tfAdrKup, "wrap");
		tfDatRac.setFocusable(true);

		add(lbPibKup);
		tfPibKup.setMinimumSize(new Dimension(140, 20));
		add(tfPibKup, "wrap");

		add(lbBrRac);
		tfBrRac.setMinimumSize(new Dimension(60, 20));
		add(tfBrRac, "wrap");

		add(lbDatRac);
		tfDatRac.setMinimumSize(new Dimension(140, 20));
		add(tfDatRac);

		add(lbVrRob);
		tfVrRob.setMinimumSize(new Dimension(140, 20));
		add(tfVrRob, "wrap");

		add(lbVrUsl);
		tfVrUsl.setMinimumSize(new Dimension(140, 20));
		add(tfVrUsl, "wrap");

		add(lbUkupRU);
		tfUkupRU.setMinimumSize(new Dimension(140, 20));
		add(tfUkupRU, "wrap");

		add(lbUkupRab);
		tfUkupRab.setMinimumSize(new Dimension(140, 20));
		add(tfUkupRab, "wrap");

		add(lbUkuPor);
		tfUkuPor.setMinimumSize(new Dimension(140, 20));
		add(tfUkuPor, "wrap");

		add(lbOznVal);
		tfOznVal.setMinimumSize(new Dimension(40, 20));
		add(tfOznVal, "wrap");

		add(lbIznUpl);
		tfIznUpl.setMinimumSize(new Dimension(140, 20));
		add(tfIznUpl, "wrap");

		add(lbUplRac);
		tfUplRac.setMinimumSize(new Dimension(140, 20));
		add(tfUplRac, "wrap");

		add(lbDatVal);
		tfDatVal.setMinimumSize(new Dimension(140, 20));
		add(tfDatVal, "wrap");

		add(btnStv, "gapleft 70");
		add(btnZav);

		add(btnOk, "gapleft 10");
		add(btnCancel);
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
