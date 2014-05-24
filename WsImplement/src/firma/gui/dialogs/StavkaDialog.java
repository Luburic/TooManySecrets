package firma.gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private FakturaDialog fd;
	
	
	
	public StavkaDialog(FakturaDialog dialog) {
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
				
				//validacije....
				
				
				Stavka s = new Stavka();
				//s.set...
				//s.set...
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
