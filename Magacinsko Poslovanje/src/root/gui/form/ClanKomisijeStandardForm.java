package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class ClanKomisijeStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomRadnik = new JButton("...");
	private JButton btnZoomPopisniDokument = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbRadnik;
	protected JComboBox<ComboBoxPair> cmbPopisniDokument;

	protected JTextField tfVrsta = new JTextField(2);

	public ClanKomisijeStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Član komisije");

		JLabel lblVrsta = new JLabel("Vrsta:");
		JLabel lblRadnik = new JLabel("Radnik:");
		JLabel lblPopisniDokument = new JLabel("Popisni dokument:");
		tfVrsta.setName("vrsta člana");

		cmbRadnik = super.setupJoins(cmbRadnik, "Radnik", "id_radnika", "id radnika", "jmbg", "jmbg", false);
		cmbPopisniDokument = super.setupJoins(cmbPopisniDokument, "Popisni_dokument", "id_popisnog_dokumenta",
				"id popisnog dokumenta", "broj_popisnog_dokumenta", "broj popisnog dokumenta", false);

		if (!childWhere.contains("id_radnika")) {
			btnZoomRadnik.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbRadnik.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbRadnik.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new RadnikStandardForm(cmbRadnik, ""), id);
				}
			});
		} else {
			cmbRadnik.setEnabled(false);
			btnZoomRadnik.setVisible(false);
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

		dataPanel.add(lblRadnik);
		dataPanel.add(cmbRadnik);
		dataPanel.add(btnZoomRadnik, "wrap, gapx 15px");

		dataPanel.add(lblPopisniDokument);
		dataPanel.add(cmbPopisniDokument);
		dataPanel.add(btnZoomRadnik, "wrap, gapx 15px");

		dataPanel.add(lblVrsta);
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
		// Treba za radiobutton

		super.getDataAndAddToRow(newRow);
	}

	@Override
	public void sync() {
		super.sync();

		// radio button
	}

	@Override
	public boolean verification() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowDeletion() {
		// TODO Auto-generated method stub
		return false;
	}
}
