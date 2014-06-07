package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import root.dbConnection.DBConnection;
import root.gui.action.PickupAction;
import root.gui.action.ZakljuciGodinuAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.Lookup;
import root.util.verification.JTextFieldLimit;
import root.util.verification.VerificationMethods;

public class PoslovnaGodinaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbPreduzece;

	protected JTextField tfGodina = new JTextField(4);
	protected JCheckBox chkZakljucena = new JCheckBox();
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();

	protected JButton btnZakljuci = new JButton(new ZakljuciGodinuAction(this));

	public PoslovnaGodinaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Poslovna godina");

		JLabel lblGodina = new JLabel("Godina*:");
		JLabel lblZakljucena = new JLabel("Zaključena:");
		JLabel lblPreduzece = new JLabel("Preduzeće*:");
		tfGodina.setName("godina");
		chkZakljucena.setName("zaključena");
		chkZakljucena.setEnabled(false);

		tfGodina.setDocument(new JTextFieldLimit(4));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);

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
			btnZoom.setVisible(false);
		}
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
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
			btnZoom.setVisible(false);
		}

		tfGodina.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
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

		dataPanel.add(lblGodina);
		dataPanel.add(tfGodina, "span 2");
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		chkZakljucena.setEnabled(false);
		dataPanel.add(lblZakljucena);
		dataPanel.add(chkZakljucena, "wrap");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece);
		dataPanel.add(btnZoom);
		dataPanel.add(lblGreska2, "gapx 15px");

		if (Constants.idPreduzeca != 0) {
			toolBar.add(btnZakljuci);
			toolBar.addSeparator();
		}

		setupTable(null);

		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = tblGrid.getSelectedRow();
				if (i != -1) {
					if (tableModel.getValueAt(i, 3).equals("Ne")) {
						btnZakljuci.setEnabled(true);
					} else {
						btnZakljuci.setEnabled(false);
					}
				}
			}
		});
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Poslovna godina", joinColumn);
		tableModel.setColumnForSorting(2);
		if (Constants.idPreduzeca != 0) {
			if (childWhere.equals("")) {
				tableModel.setWhereStmt(" WHERE Poslovna_godina1.id_preduzeca = " + Constants.idPreduzeca);
			} else {
				tableModel.setWhereStmt(childWhere + " AND Poslovna_godina1.id_preduzeca = " + Constants.idPreduzeca);
			}
		}
		super.setupTable(customQuery);
		int i = tblGrid.getSelectedRow();
		if (i != -1) {
			if (tableModel.getValueAt(i, 3).equals("Da")) {
				btnZakljuci.setEnabled(false);
			}
		}
	}

	@Override
	public boolean verification() {
		try {
			int i = Lookup.getBrojOtvorenihGodina();
			if (i > 1) {
				JOptionPane.showMessageDialog(this, "Nije dozvoljeno imati više od dve otvorene poslovne godine.",
						"Greška", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (tfGodina.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfGodina.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfGodina.getText().trim())) {
			lblGreska1.setText(Constants.VALIDATION_BROJ);
			tfGodina.requestFocus();
			return false;
		}
		Integer godina = Integer.parseInt(tfGodina.getText());
		int narednaGodina = Calendar.getInstance().get(Calendar.YEAR) + 1;
		if (godina < 2000 || godina > narednaGodina) {
			lblGreska1.setText("Vrednost mora biti između 2000 i " + narednaGodina + ".");
			tfGodina.requestFocus();
			return false;
		}
		if (cmbPreduzece.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPreduzece.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Magacinska_kartica", "Prometni_dokument", "Popisni_dokument");
	}

	public void zakljuciGodinu() {
		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall("{ call ZakljuciGodinu(?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, tableModel.getValueAt(tblGrid.getSelectedRow(), 1));
			proc.executeUpdate();
			DBConnection.getConnection().commit();
			proc.close();
			tableModel.setValueAt("Da", tblGrid.getSelectedRow(), 3);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 2));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
