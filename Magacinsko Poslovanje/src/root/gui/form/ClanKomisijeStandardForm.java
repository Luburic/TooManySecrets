package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import root.gui.action.PickupAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;

public class ClanKomisijeStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomRadnik = new JButton("...");
	private JButton btnZoomPopisniDokument = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbRadnik;
	protected JComboBox<ComboBoxPair> cmbPopisniDokument;
	protected JRadioButton rbRadnik = new JRadioButton("Radnik");
	protected JRadioButton rbZPredsednik = new JRadioButton("Zamenik predsednika");
	protected JRadioButton rbPredsednik = new JRadioButton("Predsednik");
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();

	protected JTextField tfVrsta = new JTextField(2);

	public ClanKomisijeStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Član komisije");

		JLabel lblVrsta = new JLabel("Vrsta*:");
		JLabel lblRadnik = new JLabel("Radnik*:");
		JLabel lblPopisniDokument = new JLabel("Popisni dokument*:");
		tfVrsta.setName("vrsta člana");
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);

		cmbRadnik = super.setupJoins(cmbRadnik, "Radnik", "id_radnika", "id radnika", "jmbg", "jmbg", false, "");
		cmbPopisniDokument = super.setupJoins(cmbPopisniDokument, "Popisni_dokument", "id_popisnog_dokumenta",
				"id popisnog dokumenta", "broj_popisnog_dokumenta", "broj popisnog dokumenta", false, "");

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

		cmbRadnik.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbRadnik.getSelectedIndex() != -1) {
					lblGreska1.setText("");
				}
			}
		});
		cmbPopisniDokument.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbPopisniDokument.getSelectedIndex() != -1) {
					lblGreska2.setText("");
				}
			}
		});

		dataPanel.add(lblRadnik);
		dataPanel.add(cmbRadnik);
		dataPanel.add(btnZoomRadnik);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblPopisniDokument);
		dataPanel.add(cmbPopisniDokument);
		dataPanel.add(btnZoomPopisniDokument);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblVrsta);

		ButtonGroup group = new ButtonGroup();
		group.add(rbRadnik);
		rbRadnik.setSelected(true);
		group.add(rbZPredsednik);
		group.add(rbPredsednik);
		dataPanel.add(rbRadnik);
		dataPanel.add(rbZPredsednik);
		dataPanel.add(rbPredsednik);
		tfVrsta.setVisible(false);
		dataPanel.add(tfVrsta);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Član komisije", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	protected void getDataAndAddToRow(LinkedList<Object> newRow) {
		if (rbRadnik.isSelected()) {
			tfVrsta.setText("O");
		} else if (rbRadnik.isSelected()) {
			tfVrsta.setText("Z");
		} else {
			tfVrsta.setText("P");
		}

		super.getDataAndAddToRow(newRow);
	}

	@Override
	public void sync() {
		super.sync();

		if (tfVrsta.getText().equals("O")) {
			rbRadnik.setSelected(true);
		} else if (tfVrsta.getText().equals("Z")) {
			rbZPredsednik.setSelected(true);
		} else {
			rbPredsednik.setSelected(true);
		}
	}

	@Override
	public boolean verification() {
		if (cmbRadnik.getSelectedIndex() == -1) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbRadnik.requestFocus();
			return false;
		}
		if (cmbPopisniDokument.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPopisniDokument.requestFocus();
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
		btnPickup = new JButton(new PickupAction(this, 0));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
