package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.action.PickupAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;
import root.util.verification.VerificationMethods;

public class StavkaPopisaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPopisniDokument = new JButton("...");
	private JButton btnZoomArtikal = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbArtikal;
	protected JComboBox<ComboBoxPair> cmbPopisniDokument;
	protected JTextField tfPopisanaKolicina = new JTextField(12);
	protected JTextField tfKolicinaPoKnjigama = new JTextField(12);
	protected JTextField tfProsecnaCenaPopisa = new JTextField(12);
	protected JLabel lblGreska1 = new JLabel();

	public StavkaPopisaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Stavka popisnog dokumenta");

		JLabel lblPopisanaKolicina = new JLabel("Popisana količina*: ");
		JLabel lblKolicinaPoKnjigama = new JLabel("Količina po knjigama: ");
		JLabel lblProsecnaCenaPopisa = new JLabel("Prosečna cena: ");
		JLabel lblArtikal = new JLabel("Artikal: ");
		JLabel lblBrojPopisnog = new JLabel("Broj popisnog dokumenta: ");
		tfPopisanaKolicina.setDocument(new JTextFieldLimit(12));
		tfKolicinaPoKnjigama.setEditable(false);
		tfProsecnaCenaPopisa.setEditable(false);
		cmbArtikal.setEnabled(false);
		cmbPopisniDokument.setEnabled(false);
		lblGreska1.setForeground(Color.red);

		tfPopisanaKolicina.setName("popisana količina");
		tfKolicinaPoKnjigama.setName("količina po knjigama");
		tfProsecnaCenaPopisa.setName("prosečna cena popis");

		cmbArtikal = super.setupJoins(cmbArtikal, "Artikal", "id_artikla", "id artikla", "naziv_artikla",
				"naziv artikla", false, "");
		cmbPopisniDokument = super.setupJoins(cmbPopisniDokument, "Popisni_dokument", "id_popisnog_dokumenta",
				"id popisnog dokumenta", "broj_popisnog_dokumenta", "broj popisnog dokumenta", false, "");

		if (!childWhere.contains("id_artikla")) {
			btnZoomArtikal.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbArtikal.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbArtikal.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new ArtikalStandardForm(cmbArtikal, ""), id);
				}
			});
		} else {
			cmbArtikal.setEnabled(false);
			btnZoomArtikal.setVisible(false);
		}
		if (!childWhere.contains("id_popisnog_dokumenta")) {
			btnZoomPopisniDokument.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPopisniDokument.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPopisniDokument.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PopisniDokumentStandardForm(cmbPopisniDokument, ""), id);
				}
			});
		} else {
			cmbPopisniDokument.setEnabled(false);
			btnZoomPopisniDokument.setVisible(false);
		}

		tfPopisanaKolicina.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});

		dataPanel.add(lblArtikal);
		dataPanel.add(cmbArtikal, "gapx 15px");
		dataPanel.add(btnZoomArtikal, "gapx 15px");

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(cmbPopisniDokument);
		dataPanel.add(btnZoomPopisniDokument, "wrap, gapx 15px");

		dataPanel.add(lblPopisanaKolicina);
		dataPanel.add(tfPopisanaKolicina);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaPoKnjigama);
		dataPanel.add(tfKolicinaPoKnjigama, "wrap, gapx 15px");

		dataPanel.add(lblProsecnaCenaPopisa);
		dataPanel.add(tfProsecnaCenaPopisa, "wrap, gapx 15px");

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Stavka popisa", joinColumn);
		tableModel.setColumnForSorting(1);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfPopisanaKolicina.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfKolicinaPoKnjigama.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfKolicinaPoKnjigama.getText().trim())) {
			lblGreska1.setText(Constants.VALIDATION_BROJ);
			tfKolicinaPoKnjigama.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return true;
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 2));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
