package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class PoslovniPartnerStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomMesto = new JButton("...");
	private JButton btnZoomPreduzece = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;
	protected JComboBox<ComboBoxPair> cmbMesto;

	protected JTextField tfNaziv = new JTextField(30);
	protected JTextField tfVrsta = new JTextField(2);
	protected JTextField tfAdresa = new JTextField(30);
	protected JCheckBox chkDobavljac = new JCheckBox();
	protected JCheckBox chkKupac = new JCheckBox();

	public PoslovniPartnerStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Poslovni partneri");

		JLabel lblIme = new JLabel("Ime:");
		JLabel lblVrsta = new JLabel("Vrsta:");
		JLabel lblAdresa = new JLabel("Adresa:");
		JLabel lblPreduzece = new JLabel("Preduzeće:");
		JLabel lblMesto = new JLabel("Mesto:");
		tfNaziv.setName("naziv poslovnog partnera");
		tfVrsta.setName("vrsta poslovnog partnera");
		tfAdresa.setName("adresa poslovnog partnera");

		cmbPreduzece = super.setupJoins(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća", "naziv_preduzeca",
				"naziv preduzeća", false);
		cmbMesto = super.setupJoins(cmbMesto, "Mesto", "id_mesta", "id mesta", "naziv_mesta", "naziv mesta", false);

		if (!childWhere.contains("id_preduzeca")) {
			btnZoomPreduzece.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPreduzece.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPreduzece.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PreduzeceStandardForm(cmbPreduzece, ""), id);
				}
			});
		} else {
			cmbPreduzece.setEnabled(false);
		}
		if (!childWhere.contains("id_mesta")) {
			btnZoomMesto.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbMesto.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbMesto.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new NaseljenoMestoStandardForm(cmbMesto, ""), id);
				}
			});
		} else {
			cmbMesto.setEnabled(false);
		}

		dataPanel.add(lblIme);
		dataPanel.add(tfNaziv, "wrap, gapx 15px");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece);
		dataPanel.add(btnZoomPreduzece, "wrap, gapx 15px");

		dataPanel.add(lblAdresa);
		dataPanel.add(tfAdresa, "wrap, gapx 15px");

		dataPanel.add(lblMesto);
		dataPanel.add(cmbMesto);
		dataPanel.add(btnZoomMesto, "wrap, gapx 15px");

		dataPanel.add(lblVrsta);
		dataPanel.add(chkDobavljac);
		dataPanel.add(new JLabel("dobavljač"));
		dataPanel.add(chkKupac);
		dataPanel.add(new JLabel("kupac"));
		tfVrsta.setVisible(false);
		dataPanel.add(tfVrsta);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Poslovni partner", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	protected void getDataAndAddToRow(LinkedList<Object> newRow) {
		if (chkDobavljac.isSelected() && chkKupac.isSelected()) {
			tfVrsta.setText("O");
		} else if (chkDobavljac.isSelected()) {
			tfVrsta.setText("D");
		} else {
			tfVrsta.setText("K");
		}

		super.getDataAndAddToRow(newRow);
	}

	@Override
	public void sync() {
		super.sync();

		if (tfVrsta.getText().equals("O")) {
			chkDobavljac.setSelected(true);
			chkKupac.setSelected(true);
		} else if (tfVrsta.getText().equals("D")) {
			chkDobavljac.setSelected(true);
			chkKupac.setSelected(false);
		} else {
			chkKupac.setSelected(true);
			chkDobavljac.setSelected(false);
		}
	}
}
