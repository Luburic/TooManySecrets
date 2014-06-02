package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.dialog.PrometniDokumentAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;

public class PoslovniPartnerStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomMesto = new JButton("...");
	private JButton btnZoomPreduzece = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;
	protected JComboBox<ComboBoxPair> cmbMesto;

	protected JTextField tfNaziv = new JTextField(22);
	protected JTextField tfVrsta = new JTextField(2);
	protected JTextField tfAdresa = new JTextField(22);
	protected JCheckBox chkDobavljac = new JCheckBox();
	protected JCheckBox chkKupac = new JCheckBox();
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();
	protected JLabel lblGreska4 = new JLabel();
	protected JLabel lblGreska5 = new JLabel();

	public PoslovniPartnerStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Poslovni partneri");

		JLabel lblIme = new JLabel("Ime*:");
		JLabel lblVrsta = new JLabel("Vrsta*:");
		JLabel lblAdresa = new JLabel("Adresa*:");
		JLabel lblPreduzece = new JLabel("Preduzeće*:");
		JLabel lblMesto = new JLabel("Mesto*:");
		tfNaziv.setName("naziv poslovnog partnera");
		tfVrsta.setName("vrsta poslovnog partnera");
		tfAdresa.setName("adresa poslovnog partnera");
		tfNaziv.setDocument(new JTextFieldLimit(30));
		tfAdresa.setDocument(new JTextFieldLimit(30));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);
		lblGreska4.setForeground(Color.red);
		lblGreska5.setForeground(Color.red);

		if (Constants.idPreduzeca == 0) {
			cmbPreduzece = super.setupJoinsWithComboBox(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća",
					"naziv_preduzeca", "naziv preduzeća", false, "");
		} else {
			super.setupJoins("Preduzece", "id_preduzeca", "naziv_preduzeca", "naziv preduzeća");
			cmbPreduzece = new JComboBox<ComboBoxPair>();
			cmbPreduzece.insertItemAt(new ComboBoxPair(Constants.idPreduzeca, Constants.nazivPreduzeca), 0);
			cmbPreduzece.setSelectedIndex(0);
			cmbPreduzece.setEnabled(false);
			cmbPreduzece.setName("id preduzeća");
			btnZoomPreduzece.setVisible(false);
		}
		cmbMesto = super.setupJoinsWithComboBox(cmbMesto, "Mesto", "id_mesta", "id mesta", "naziv_mesta",
				"naziv mesta", false, "");

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
			for (int i = 0; i < cmbPreduzece.getItemCount(); i++) {
				if (cmbPreduzece.getItemAt(i).getId().equals(parentId)) {
					cmbPreduzece.setSelectedIndex(i);
					break;
				}
			}
			btnZoomPreduzece.setVisible(false);
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
			for (int i = 0; i < cmbMesto.getItemCount(); i++) {
				if (cmbMesto.getItemAt(i).getId().equals(parentId)) {
					cmbMesto.setSelectedIndex(i);
					break;
				}
			}
			btnZoomMesto.setVisible(false);
		}

		tfNaziv.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});
		tfAdresa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska3.setText("");
				}
			}
		});
		cmbPreduzece.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbPreduzece.getSelectedIndex() != -1) {
					lblGreska2.setText("");
				}
			}
		});
		cmbMesto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbMesto.getSelectedIndex() != -1) {
					lblGreska4.setText("");
				}
			}
		});
		chkDobavljac.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				lblGreska5.setText("");
			}
		});
		chkKupac.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				lblGreska5.setText("");
			}
		});

		dataPanel.add(lblIme);
		dataPanel.add(tfNaziv, "span 4");
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece, "span 4");
		dataPanel.add(btnZoomPreduzece);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblAdresa);
		dataPanel.add(tfAdresa, "span 4");
		dataPanel.add(lblGreska3, "wrap, gapx 15px");

		dataPanel.add(lblMesto);
		dataPanel.add(cmbMesto, "span 3");
		dataPanel.add(btnZoomMesto);
		dataPanel.add(lblGreska4, "wrap, gapx 15px");

		dataPanel.add(lblVrsta);
		dataPanel.add(chkDobavljac);
		dataPanel.add(new JLabel("dobavljač"));
		dataPanel.add(chkKupac);
		dataPanel.add(new JLabel("kupac"));
		tfVrsta.setVisible(false);
		dataPanel.add(tfVrsta);
		dataPanel.add(lblGreska5, "gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PrometniDokumentAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Poslovni partner", joinColumn);
		tableModel.setColumnForSorting(3);
		if (Constants.idPreduzeca != 0) {
			if (childWhere.equals("")) {
				tableModel.setWhereStmt(" WHERE Poslovni_partner1.id_preduzeca = " + Constants.idPreduzeca);
			} else {
				tableModel.setWhereStmt(childWhere + " AND Poslovni_partner1.id_preduzeca = " + Constants.idPreduzeca);
			}
		}
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

	@Override
	public boolean verification() {
		if (tfNaziv.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNaziv.requestFocus();
			return false;
		}
		if (cmbPreduzece.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPreduzece.requestFocus();
			return false;
		}
		if (tfAdresa.getText().equals("")) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfAdresa.requestFocus();
			return false;
		}
		if (cmbMesto.getSelectedIndex() == -1) {
			lblGreska4.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbMesto.requestFocus();
			return false;
		}
		if (!chkDobavljac.isSelected() && !chkKupac.isSelected()) {
			lblGreska5.setText(Constants.VALIDATION_CHECKBOX);
			chkDobavljac.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Prometni_dokument");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
