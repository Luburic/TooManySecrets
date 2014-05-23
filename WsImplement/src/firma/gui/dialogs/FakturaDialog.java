package firma.gui.dialogs;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import beans.faktura.Faktura;
import beans.faktura.Faktura.Zaglavlje;
import net.miginfocom.swing.MigLayout;
import net.sourceforge.jdatepicker.DateModel;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.util.JDatePickerUtil;
import firma.gui.MainFrame;
import firma.gui.actions.StavkaAction;

@SuppressWarnings("serial")
public class FakturaDialog extends JDialog{

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
	

	/****************smaranjeeeeee**************************/
	
	private JTextField tfIdPor = new JTextField();
	private JTextField tfNazivDob = new JTextField();
	private JTextField tfAdrDob = new JTextField();
	
	private JTextField tfPibDob = new JTextField();
	private JTextField tfNazKup = new JTextField();
	
	private JTextField tfAdrKup = new JTextField();
	private JTextField tfPibKup = new JTextField();
	
	
	private JTextField tfBrRac = new JTextField();
	private JDatePickerImpl tfDatRac = new JDatePickerImpl(new JDatePanelImpl(null));
	
	private JTextField tfVrRob = new JTextField();
	private JTextField tfVrUsl = new JTextField();
	
	private JTextField tfUkupRU = new JTextField();
	private JTextField tfUkupRab = new JTextField();
	
	private JTextField tfUkuPor = new JTextField();
	private JTextField tfOznVal = new JTextField();
	
	private JTextField tfIznUpl = new JTextField();
	private JTextField tfUplRac = new JTextField();
	
	
	private JDatePickerImpl tfDatVal = new JDatePickerImpl(new JDatePanelImpl(null));
	
	/****************smaranjeeeeee**************************/
	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	
	public FakturaDialog(MainFrame instance) {
		super(instance);
		
		setTitle("Slanje fakture");	
		setResizable(false);
		setSize(new Dimension(400,300));
		setLayout(new MigLayout("fill"));
		
		
	//forma za popunjavanje zaglavlja fakture
	
	add(lbIdPor);
	tfIdPor.setMinimumSize(new Dimension(80,20));
	add(tfIdPor,"wrap");
	
	add(lbNazivDob);
	tfNazivDob.setMinimumSize(new Dimension(80,20));
	add(tfNazivDob,"wrap");
	
	
	add(lbAdrDob);
	tfAdrDob.setMinimumSize(new Dimension(80,20));
	add(tfAdrDob,"wrap");
	
	add(lbPibDob);
	tfPibDob.setMinimumSize(new Dimension(80,20));
	add(tfPibDob,"wrap");
	
	
	add(lbNazKup);
	tfNazKup.setMinimumSize(new Dimension(80,20));
	add(tfNazKup,"wrap");
	
	
	add(lbAdrKup);
	tfAdrKup.setMinimumSize(new Dimension(80,20));
	add(tfAdrKup,"wrap");
	tfDatRac.setFocusable(true);
	
	add(lbPibKup);
	tfPibKup.setMinimumSize(new Dimension(80,20));
	add(tfPibKup,"wrap");
	
	
	add(lbBrRac);
	tfBrRac.setMinimumSize(new Dimension(80,20));
	add(tfBrRac,"wrap");
	
	add(lbDatRac);
	tfDatRac.setMinimumSize(new Dimension(80,20));
	add(tfDatRac);
	
	
	
	add(lbVrRob);
	tfVrRob.setMinimumSize(new Dimension(80,20));
	add(tfVrRob,"wrap");
	
	add(lbVrUsl);
	tfVrUsl.setMinimumSize(new Dimension(80,20));
	add(tfVrUsl,"wrap");

	
	add(lbUkupRU);
	tfUkupRU.setMinimumSize(new Dimension(80,20));
	add(tfUkupRU,"wrap");
	
	add(lbUkupRab);
	tfUkupRab.setMinimumSize(new Dimension(80,20));
	add(tfUkupRab,"wrap");
	
	
	add(lbUkuPor);
	tfUkuPor.setMinimumSize(new Dimension(80,20));
	add(tfUkuPor,"wrap");
	
	
	add(lbOznVal);
	tfOznVal.setMinimumSize(new Dimension(80,20));
	add(tfOznVal,"wrap");
	
	
	add(lbIznUpl);
	tfIznUpl.setMinimumSize(new Dimension(80,20));
	add(tfIznUpl,"wrap");
	
	add(lbUplRac);
	tfUplRac.setMinimumSize(new Dimension(80,20));
	add(tfUplRac,"wrap");
	
	
	add(lbDatVal);
	tfDatVal.setMinimumSize(new Dimension(80,20));
	add(tfDatVal,"wrap");
	
	
	add(btnOk, "gapleft 70");
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
			
			if(("").equals(tfIdPor.getText())){
				JOptionPane.showMessageDialog(null, "Obavezan unos id-a poruke!","Greska",JOptionPane.ERROR_MESSAGE);
				tfIdPor.requestFocus();
				return;
			}
			
			if(("").equals(tfBrRac.getText())){
				JOptionPane.showMessageDialog(null, "Obavezan unos broja racuna!","Greska",JOptionPane.ERROR_MESSAGE);
				tfBrRac.requestFocus();
				return;
			}
			
			int brRac=0;
			
			try {
				brRac =Integer.valueOf(tfBrRac.getText());
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(null, "Pogresno unet broj racuna!","Greska",JOptionPane.ERROR_MESSAGE);
				tfBrRac.requestFocus();
				return;
			}
			
			
			if(("").equals(tfIznUpl.getText())){
				JOptionPane.showMessageDialog(null, "Obavezan unos iznosa za uplatu!","Greska",JOptionPane.ERROR_MESSAGE);
				tfIznUpl.requestFocus();
				return;
			}
		
			
			
			
			if(("").equals(tfUplRac.getText())){
				JOptionPane.showMessageDialog(null, "Obavezan unos racuna na koji se uplacuje!","Greska",JOptionPane.ERROR_MESSAGE);
				tfUplRac.requestFocus();
				return;
			}
			/*
			if(("").equals(tfDatRac.getJFormattedTextField().getText())){
				JOptionPane.showMessageDialog(null, "Obavezan unos datuma racuna!","Greska",JOptionPane.ERROR_MESSAGE);
				tfDatRac.requestFocus();
				return;
			}
			*/
			
			//ostale validacije..
			
			Zaglavlje zaglavlje = new Zaglavlje();
			
			zaglavlje.setAdresaDobavljaca(tfAdrDob.getText());
			zaglavlje.setAdresaKupca(tfAdrKup.getText());
			zaglavlje.setBrojRacuna(brRac);
			//zaglavlje.setDatumRacuna(tfDatRac.getJFormattedTextField().getText()); kaze stole bice string
			//zaglavlje.setDatumValute(tfDatVal.getJFormattedTextField().getText()); stole kaze
			zaglavlje.setIdPoruke(tfIdPor.getText());
			
			zaglavlje.setIznosZaUplatu(new BigDecimal(tfIznUpl.getText()));
			zaglavlje.setNazivDobavljaca(tfNazivDob.getText());
			zaglavlje.setNazivKupca(tfNazKup.getText());
			zaglavlje.setOznakaValute(tfOznVal.getText());
			zaglavlje.setPibDobavljaca(tfPibDob.getText());
			zaglavlje.setPibKupca(tfPibKup.getText());
			
			
			zaglavlje.setUkupanPorez(new BigDecimal(tfUkuPor.getText()));
			zaglavlje.setUkupanRabat(new BigDecimal(tfUkupRab.getText()));
			zaglavlje.setUkupnoRobaIUsluge(new BigDecimal(tfUkupRU.getText()));
			zaglavlje.setVrednostRobe(new BigDecimal(tfVrRob.getText()));
			zaglavlje.setVrednostUsluga(new BigDecimal(tfVrUsl.getText()));
			zaglavlje.setUplataNaRacun(tfUplRac.getText());
			
			Faktura novaFaktura = new Faktura();
			novaFaktura.setZaglavlje(zaglavlje);
			
			new StavkaDialog(novaFaktura).setVisible(true);
			
		}
	});
	
	
	
	}
	
}
