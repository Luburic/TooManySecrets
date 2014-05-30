package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.dialog.ClanKomisijeAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;
import root.util.verification.VerificationMethods;

public class RadnikStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomMesto = new JButton("...");
	private JButton btnZoomPreduzece = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;
	protected JComboBox<ComboBoxPair> cmbMesto;

	protected JTextField tfIme = new JTextField(30);
	protected JTextField tfPrezime = new JTextField(30);
	protected JTextField tfJmbg = new JTextField(13);
	protected JTextField tfAdresa = new JTextField(30);
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();
	protected JLabel lblGreska4 = new JLabel();
	protected JLabel lblGreska5 = new JLabel();
	protected JLabel lblGreska6 = new JLabel();

	public RadnikStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Radnici");

		JLabel lblIme = new JLabel("Ime*:");
		JLabel lblPrezime = new JLabel("Prezime*:");
		JLabel lblJmbg = new JLabel("JMBG*:");
		JLabel lblAdresa = new JLabel("Adresa*:");
		JLabel lblPreduzece = new JLabel("Preduzeće*:");
		JLabel lblMesto = new JLabel("Mesto*:");
		tfIme.setName("ime");
		tfPrezime.setName("prezime");
		tfJmbg.setName("jmbg");
		tfAdresa.setName("adresa");
		tfIme.setDocument(new JTextFieldLimit(30));
		tfPrezime.setDocument(new JTextFieldLimit(30));
		tfJmbg.setDocument(new JTextFieldLimit(13));
		tfAdresa.setDocument(new JTextFieldLimit(30));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);
		lblGreska4.setForeground(Color.red);
		lblGreska5.setForeground(Color.red);
		lblGreska6.setForeground(Color.red);

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
			btnZoomMesto.setVisible(false);
		}

		tfIme.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});
		tfPrezime.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska2.setText("");
				}
			}
		});
		tfJmbg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska3.setText("");
				}
			}
		});
		tfAdresa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska5.setText("");
				}
			}
		});
		cmbMesto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbMesto.getSelectedIndex() != -1) {
					lblGreska6.setText("");
				}
			}
		});
		cmbPreduzece.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbPreduzece.getSelectedIndex() != -1) {
					lblGreska4.setText("");
				}
			}
		});

		dataPanel.add(lblIme);
		dataPanel.add(tfIme);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblPrezime);
		dataPanel.add(tfPrezime);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblJmbg);
		dataPanel.add(tfJmbg);
		dataPanel.add(lblGreska3, "wrap, gapx 15px");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece);
		dataPanel.add(btnZoomPreduzece);
		dataPanel.add(lblGreska4, "wrap, gapx 15px");

		dataPanel.add(lblAdresa);
		dataPanel.add(tfAdresa);
		dataPanel.add(lblGreska5, "wrap, gapx 15px");

		dataPanel.add(lblMesto);
		dataPanel.add(cmbMesto);
		dataPanel.add(btnZoomMesto);
		dataPanel.add(lblGreska6, "wrap, gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ClanKomisijeAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Radnik", joinColumn);
		tableModel.setColumnForSorting(3);
		if (Constants.idPreduzeca != 0) {
			if (childWhere.equals("")) {
				tableModel.setWhereStmt(" WHERE Radnik1.id_preduzeca = " + Constants.idPreduzeca);
			} else {
				tableModel.setWhereStmt(childWhere + " AND Radnik1.id_preduzeca = " + Constants.idPreduzeca);
			}
		}
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfIme.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfIme.requestFocus();
			return false;
		}
		if (tfPrezime.getText().equals("")) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfPrezime.requestFocus();
			return false;
		}
		if (tfJmbg.getText().equals("")) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfJmbg.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfJmbg.getText())) {
			lblGreska3.setText(Constants.VALIDATION_BROJ);
			tfJmbg.requestFocus();
			return false;
		} else {
			if (tfJmbg.getText().length() != 13) {
				lblGreska3.setText("JMBG treba da ima tračno 13 cifri.");
				tfJmbg.requestFocus();
				return false;
			}
		}
		if (cmbPreduzece.getSelectedIndex() == -1) {
			lblGreska4.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPreduzece.requestFocus();
			return false;
		}
		if (tfAdresa.getText().equals("")) {
			lblGreska5.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfAdresa.requestFocus();
			return false;
		}
		if (cmbMesto.getSelectedIndex() == -1) {
			lblGreska6.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbMesto.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Clan_komisije");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 5));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
