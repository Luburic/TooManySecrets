package firma.gui.dialogs;

import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import firma.gui.MainFrame;

@SuppressWarnings("serial")
public class FakturaDialog extends JDialog{

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
	private JLabel lbIdPor = new JLabel("Id poruke:");

	/****************smaranjeeeeee**************************/
	
	
	private JTextField tfNazivDob = new JTextField();
	private JTextField tfAdrDob = new JTextField();
	
	private JTextField tfPibDob = new JTextField();
	private JTextField tfNazKup = new JTextField();
	
	private JTextField tfAdrKup = new JTextField();
	private JTextField tfPibKup = new JTextField();
	
	
	private JTextField tfBrRac = new JTextField();
	private JTextField tfDatRac = new JTextField();
	
	
	
	private JTextField tfVrRob = new JTextField();
	private JTextField tfVrUsl = new JTextField();
	
	private JTextField tfUkupRU = new JTextField();
	private JTextField tfUkupRab = new JTextField();
	
	private JTextField tfUkuPor = new JTextField();
	private JTextField tfOznVal = new JTextField();
	
	private JTextField tfIznUpl = new JTextField();
	private JTextField tfUplRac = new JTextField();
	
	private JTextField tfDatVal = new JTextField();
	private JTextField tfIdPor = new JTextField();
	
	/****************smaranjeeeeee**************************/
	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	//private CertificateGenerator cert = new CertificateGenerator();
	
	
	
	public FakturaDialog(MainFrame instance) {
		super(instance, Dialog.ModalityType.APPLICATION_MODAL);
		setTitle("Slanje fakture");		
		setSize(new Dimension(400,300));
		//setResizable(false);
		/*MigLayout layout = new MigLayout(
				"",
				"20[]10[]20",
				"20[]20[]20[]10[]10[]10[]10[]10[]20[]20");
		setLayout(layout);*/
		setLocationRelativeTo(instance);
		
		//forma za popunjavanje i slanje fakture
	}
	
}
