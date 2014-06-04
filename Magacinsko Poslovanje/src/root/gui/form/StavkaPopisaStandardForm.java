package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.action.PickupAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.Lookup;
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

	protected boolean notEditable = false;

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
		lblGreska1.setForeground(Color.red);

		tfPopisanaKolicina.setName("popisana količina");
		tfKolicinaPoKnjigama.setName("količina po knjigama");
		tfProsecnaCenaPopisa.setName("prosečna cena popis");

		btnAdd.setEnabled(false);
		btnDelete.setEnabled(false);
		btnZoomArtikal.setVisible(false);

		cmbArtikal = super.setupJoinsWithComboBox(cmbArtikal, "Artikal", "id_artikla", "id artikla", "naziv_artikla",
				"naziv artikla", false, "");
		cmbPopisniDokument = super.setupJoinsWithComboBox(cmbPopisniDokument, "Popisni_dokument",
				"id_popisnog_dokumenta", "id popisnog dokumenta", "broj_popisnog_dokumenta", "broj popisnog dokumenta",
				false, "");

		cmbArtikal.setEnabled(false);
		cmbPopisniDokument.setEnabled(false);
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
			for (int i = 0; i < cmbArtikal.getItemCount(); i++) {
				if (cmbArtikal.getItemAt(i).getId().equals(parentId)) {
					cmbArtikal.setSelectedIndex(i);
					break;
				}
			}
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
			for (int i = 0; i < cmbPopisniDokument.getItemCount(); i++) {
				if (cmbPopisniDokument.getItemAt(i).getId().equals(parentId)) {
					cmbPopisniDokument.setSelectedIndex(i);
					break;
				}
			}
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
		dataPanel.add(cmbArtikal);
		dataPanel.add(btnZoomArtikal, "wrap, gapx 15px");

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(cmbPopisniDokument);
		dataPanel.add(btnZoomPopisniDokument, "wrap, gapx 15px");

		dataPanel.add(lblPopisanaKolicina);
		dataPanel.add(tfPopisanaKolicina, "span 2");
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaPoKnjigama);
		dataPanel.add(tfKolicinaPoKnjigama, "wrap, span 2");

		dataPanel.add(lblProsecnaCenaPopisa);
		dataPanel.add(tfProsecnaCenaPopisa, "wrap, span 2");

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Stavka popisa", joinColumn);
		tableModel.setColumnForSorting(1);

		try {
			notEditable = Lookup.getZakljucen("Popisni_dokument", "status_popisnog", childWhere);
			if (notEditable) {
				tfPopisanaKolicina.setEditable(false);
				btnCommit.setEnabled(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfPopisanaKolicina.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfPopisanaKolicina.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfPopisanaKolicina.getText().trim())) {
			lblGreska1.setText(Constants.VALIDATION_BROJ);
			tfPopisanaKolicina.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return false;
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 2));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}

	@Override
	public void setMode(int mode) {
		super.setMode(mode);
		if (mode == Constants.MODE_SEARCH) {
			tfPopisanaKolicina.setEditable(true);
			btnZoomArtikal.setVisible(true);
			btnCommit.setEnabled(true);
		} else {
			btnZoomArtikal.setVisible(false);
			if (notEditable) {
				tfPopisanaKolicina.setEditable(false);
				btnCommit.setEnabled(false);
			}
		}
	}
}