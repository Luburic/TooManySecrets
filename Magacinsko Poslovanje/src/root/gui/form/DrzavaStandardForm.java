package root.gui.form;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.MainFrame;
import root.gui.action.NextFormButton;
import root.gui.action.dialog.NaseljenoMestoAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.Lookup;
import root.util.verification.JTextFieldLimit;
import root.util.verification.VerificationMethods;

public class DrzavaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JTextField tfSifraDrzave = new JTextField(3);
	private JTextField tfNazivDrzave = new JTextField(20);
	private final JLabel lblGreska = new JLabel();
	private final JLabel lblGreska2 = new JLabel();

	public DrzavaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Države");
		tfSifraDrzave.setName("šifra države");
		tfNazivDrzave.setName("naziv države");

		tableModel = TableModelCreator.createTableModel("Država", null);

		tfSifraDrzave.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String sifraDrzave = tfSifraDrzave.getText().trim();
				try {
					String nazivDrzave = Lookup.getDrzava(sifraDrzave);
					if (!nazivDrzave.equals("")) {
						setMode(Constants.MODE_EDIT);
						int n = tableModel.getRowCount();
						for (int i = 0; i < n; i++) {
							if (sifraDrzave.equals(tableModel.getValueAt(i, 1))) {
								tblGrid.getSelectionModel().setSelectionInterval(i, i);
								break;
							}
						}
						tfNazivDrzave.setText(nazivDrzave);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		tfSifraDrzave.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				int k = e.getKeyChar();
				if (k != 8) {
					lblGreska.setText("");
					tfSifraDrzave.setBackground(Color.white);
				}
				if ((k < 65 || k > 122 || (k > 90 && k < 97)) && k != 8) {
					lblGreska.setText(Constants.VALIDATION_SIFRA);
				} else {
					if (VerificationMethods.containsValidCharacters(tfSifraDrzave.getText())) {
						lblGreska.setText("");
					} else {
						lblGreska.setText(Constants.VALIDATION_SIFRA);
					}
				}
			}
		});
		tfNazivDrzave.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				int k = e.getKeyChar();
				if (k != 8) {
					lblGreska2.setText("");
					tfNazivDrzave.setBackground(Color.white);
				}
			}
		});

		JLabel lblSifra = new JLabel("Šifra države*:");
		JLabel lblNaziv = new JLabel("Naziv države*:");
		lblGreska.setForeground(Color.red);

		dataPanel.add(lblSifra);
		tfSifraDrzave.setDocument(new JTextFieldLimit(3));
		dataPanel.add(tfSifraDrzave);
		dataPanel.add(lblGreska, "wrap");

		dataPanel.add(lblNaziv);
		tfNazivDrzave.setDocument(new JTextFieldLimit(20));
		dataPanel.add(tfNazivDrzave);
		dataPanel.add(lblGreska2, "pushx");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new NaseljenoMestoAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
		setLocationRelativeTo(MainFrame.getInstance());
	}

	@Override
	public boolean verification() {
		if (tfSifraDrzave.getText().equals("")) {
			lblGreska.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfSifraDrzave.requestFocus();
			return false;
		}
		if (VerificationMethods.containsValidCharacters(tfSifraDrzave.getText())) {
			lblGreska.setText(Constants.VALIDATION_SIFRA);
			tfSifraDrzave.requestFocus();
			return false;
		}
		if (tfNazivDrzave.getText().equals("")) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNazivDrzave.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {

		return false;
	}
}
