package firma.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import beans.faktura.Faktura.Stavka;
import firma.gui.MainFrame;

@SuppressWarnings("serial")
public class StavkaDialog extends JDialog{
	
	private JLabel lbRbr = new JLabel("Redni broj:");
	private JLabel lbNazRU = new JLabel("Naziv robe/usluge:");
	private JLabel lbKol = new JLabel("Kolicina:");

	private JLabel lbJedMer = new JLabel("Jedinica mere:");
	private JLabel lbJedCena = new JLabel("Jedinicna cena:");

	private JLabel lbVred = new JLabel("Vrednost:");
	private JLabel lbProcRab = new JLabel("Procenat rabata:");

	private JLabel lbIznRab = new JLabel("Iznos rabata:");
	private JLabel lbUmRab = new JLabel("Umanjeno(rabat):");

	private JLabel lbUkPor = new JLabel("Ukupan porez:");
	/********************************************************/
	private JTextField tfRbr = new JTextField();
	private JTextField tfNazRU = new JTextField();
	private JTextField tfKol = new JTextField();

	private JTextField tfJedMer = new JTextField();
	private JTextField tfJedCena = new JTextField();

	private JTextField tfVred = new JTextField();
	private JTextField tfProcRab = new JTextField();

	private JTextField tfIznRab = new JTextField();
	private JTextField tfUmRab = new JTextField();
	private JTextField tfUkPor = new JTextField();
	/********************************************************/
	
	
	private JButton btnOk = new JButton("Potvrdi");
	private JButton btnCancel = new JButton("Otkazi");
	private ZaglavljeDialog fd;
	
	
	
	public StavkaDialog(ZaglavljeDialog dialog) {
		// TODO Auto-generated constructor stub
		super();
		this.fd = dialog;
		setTitle("Popunjavanje stavke");	
		setResizable(false);
		setSize(new Dimension(400,300));
		setLayout(new MigLayout("fill"));
		
		
		add(lbRbr);
		tfRbr.setMinimumSize(new Dimension(80, 20));
		add(tfRbr, "wrap");
		
		add(lbNazRU);
		tfNazRU.setMinimumSize(new Dimension(80, 20));
		add(tfNazRU, "wrap");
		
		add(lbKol);
		tfKol.setMinimumSize(new Dimension(80, 20));
		add(tfKol, "wrap");
		
	
		

		add(lbJedMer);
		tfJedMer.setMinimumSize(new Dimension(80, 20));
		add(tfJedMer, "wrap");
		
		add(lbJedCena);
		tfJedCena.setMinimumSize(new Dimension(80, 20));
		add(tfJedCena, "wrap");
		
		add(lbVred);
		tfVred.setMinimumSize(new Dimension(80, 20));
		add(tfVred, "wrap");
		
		add(lbProcRab);
		tfProcRab.setMinimumSize(new Dimension(80, 20));
		add(tfProcRab, "wrap");
		
	
		
		add(lbIznRab);
		tfIznRab.setMinimumSize(new Dimension(80, 20));
		add(tfIznRab, "wrap");
		
		add(lbUmRab);
		tfUmRab.setMinimumSize(new Dimension(80, 20));
		add(tfUmRab, "wrap");
		
		add(lbUkPor);
		tfUkPor.setMinimumSize(new Dimension(80, 20));
		add(tfUkPor, "wrap");
		
		
		
		add(btnOk, "gapleft 70");
		add(btnCancel);
		pack();
		setLocationRelativeTo(MainFrame.getInstance());
		
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});

		
		
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Stavka s = new Stavka();
				
				
				if (("").equals(tfRbr.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos rednog broja stavke!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfRbr.requestFocus();
					return;
				}
				
				
				
				int rbr = 0;
				
				try {
					rbr = Integer.valueOf(tfRbr.getText());
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidan redni broj!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfRbr.requestFocus();
					return;
				}
				s.setRedniBroj(rbr);
				
				

				if (("").equals(tfNazRU.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos naziva robe/usluge!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfNazRU.requestFocus();
					return;
				}
				s.setNazivRobeIliUsluge(tfNazRU.getText());
				
				
				
				BigDecimal kol =null;
				try {
					kol = BigDecimal.valueOf(new Double(tfKol.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost za kolicinu!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfKol.requestFocus();
					return;
				}
				s.setKolicina(kol);
				
				
				
				if (("").equals(tfJedMer.getText())) {
					JOptionPane.showMessageDialog(null,
							"Obavezan unos jedinice mere!", "Greska",
							JOptionPane.ERROR_MESSAGE);
					tfJedMer.requestFocus();
					return;
				}
				s.setJedinicaMere(tfJedMer.getText());
				
				
				BigDecimal jc =null;
				try {
					jc = BigDecimal.valueOf(new Double(tfJedCena.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednos za jedinicnu cenu!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfJedCena.requestFocus();
					return;
				}
				
				s.setJedinicnaCena(jc);
				
				
				
				BigDecimal vr =null;
				try {
					vr = BigDecimal.valueOf(new Double(tfVred.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfVred.requestFocus();
					return;
				}
				s.setVrednost(vr);
				
				
				BigDecimal pr =null;
				try {
					pr = BigDecimal.valueOf(new Double(tfProcRab.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost procene rabata!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfProcRab.requestFocus();
					return;
				}
				s.setProcenatRabata(pr);
				
				
				
				BigDecimal ir =null;
				try {
					ir = BigDecimal.valueOf(new Double(tfIznRab.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednos za iznos rabata!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfIznRab.requestFocus();
					return;
				}
				
				
				s.setIznosRabata(ir);
				
				
				BigDecimal um =null;
				try {
					um= BigDecimal.valueOf(new Double(tfUmRab.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost za umanjen rabat!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfUmRab.requestFocus();
					return;
				}
				s.setUmanjenoZaRabat(um);
				
				
				
				BigDecimal up =null;
				try {
					up = BigDecimal.valueOf(new Double(tfUkPor.getText()));
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"Nevalidna vrednost za ukupan porez!",
							"Greska", JOptionPane.ERROR_MESSAGE);
					tfUkPor.requestFocus();
					return;
				}
				s.setUkupanPorez(up);
				
				
				
				
				
				fd.getFaktura().getStavka().add(s);
				
				
				fd.getBtnStv().setEnabled(true);
				fd.getBtnZav().setEnabled(true);
				
				fd.getBtnOk().setEnabled(false);
				
				JOptionPane.showMessageDialog(null,"Uspesno uneta stavka.", "Unos stavke",JOptionPane.INFORMATION_MESSAGE);
				setVisible(false);
				
			}

		});
	}
	
}
