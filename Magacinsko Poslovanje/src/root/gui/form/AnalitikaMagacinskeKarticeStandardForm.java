package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.action.PickupAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;

public class AnalitikaMagacinskeKarticeStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbVrsta;
	protected JComboBox<ComboBoxPair> cmbMagKartica;
	protected JTextField tfRedniBroj = new JTextField(3);
	protected JTextField tfSmer = new JTextField(1);
	protected JTextField tfKolicina = new JTextField(12);
	protected JTextField tfCena = new JTextField(12);
	protected JTextField tfVrednost = new JTextField(12);

	public AnalitikaMagacinskeKarticeStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Analitika magacinske kartice");

		btnDelete.setEnabled(false);
		btnAdd.setEnabled(false);

		JLabel lblRedniBroj = new JLabel("Redni broj:");
		JLabel lblSmer = new JLabel("Smer:");
		JLabel lblKolicina = new JLabel("Količina:");
		JLabel lblCena = new JLabel("Cena:");
		JLabel lblVrednost = new JLabel("Vrednost:");
		JLabel lblVrsta = new JLabel("Vrsta:");

		tfRedniBroj.setName("redni broj");
		tfSmer.setName("smer");
		tfKolicina.setName("količina");
		tfCena.setName("cena");
		tfVrednost.setName("vrednost");

		cmbMagKartica = super.setupJoinsWithComboBox(cmbMagKartica, "Magacinska_kartica", "id_magacinske_kartice",
				"id magacinske kartice", "id_magacinske_kartice", "id magacinske kartice", false, "");
		cmbVrsta = super.setupJoinsWithComboBox(cmbVrsta, "Vrsta_prometa", "id_prometa", "id prometa", "sifra_prometa",
				"šifra prometa", false, "");
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbVrsta.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbVrsta.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new DrzavaStandardForm(cmbVrsta, ""), id);
				}
			});
		} else {
			cmbVrsta.setEnabled(false);
			for (int i = 0; i < cmbVrsta.getItemCount(); i++) {
				if (cmbVrsta.getItemAt(i).getId().equals(parentId)) {
					cmbVrsta.setSelectedIndex(i);
					break;
				}
			}
			btnZoom.setVisible(false);
		}

		dataPanel.add(lblRedniBroj);
		dataPanel.add(tfRedniBroj, "wrap");

		dataPanel.add(lblSmer);
		dataPanel.add(tfSmer, "wrap");

		dataPanel.add(lblVrsta);
		dataPanel.add(cmbVrsta, "wrap");
		// dataPanel.add(btnZoom, "wrap 15px");

		dataPanel.add(lblKolicina);
		dataPanel.add(tfKolicina, "wrap");

		dataPanel.add(lblCena);
		dataPanel.add(tfCena, "wrap");

		dataPanel.add(lblVrednost);
		dataPanel.add(tfVrednost, "wrap");

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Analitika magacinske kartice", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
		tblGrid.removeColumn(tblGrid.getColumnModel().getColumn(6));
	}

	@Override
	public boolean verification() {
		return false;
	}

	@Override
	public boolean allowDeletion() {
		return false;
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;
		if (mode == Constants.MODE_SEARCH) {
			btnCommit.setEnabled(true);
			btnZoom.setEnabled(true);
			clearFields(true);
			cmbVrsta.setEnabled(true);
			tfCena.setEnabled(true);
			tfKolicina.setEnabled(true);
			tfRedniBroj.setEnabled(true);
			tfSmer.setEnabled(true);
			tfVrednost.setEnabled(true);
		} else {
			btnCommit.setEnabled(false);
			btnZoom.setEnabled(false);
			cmbVrsta.setEnabled(false);
			tfCena.setEnabled(false);
			tfKolicina.setEnabled(false);
			tfRedniBroj.setEnabled(false);
			tfSmer.setEnabled(false);
			tfVrednost.setEnabled(false);
		}
	}
}
