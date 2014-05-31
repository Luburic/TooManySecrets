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
import root.gui.action.dialog.PoslovniPartnerAction;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;
import root.util.verification.VerificationMethods;

public class NaseljenoMestoStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbDrzava;
	protected JTextField tfSifraMesta = new JTextField(8);
	protected JTextField tfNazivMesta = new JTextField(20);
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();

	public NaseljenoMestoStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Naseljena mesta");

		JLabel lblSifra = new JLabel("Zip kod*:");
		JLabel lblNaziv = new JLabel("Naziv mesta*:");
		JLabel lblDrzava = new JLabel("Država*:");
		tfNazivMesta.setName("naziv mesta");
		tfSifraMesta.setName("zip kod");
		tfSifraMesta.setDocument(new JTextFieldLimit(10));
		tfNazivMesta.setDocument(new JTextFieldLimit(10));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);

		cmbDrzava = super.setupJoinsWithComboBox(cmbDrzava, "Drzava", "id_drzave", "id države", "naziv_drzave",
				"naziv države", false, "");
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbDrzava.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbDrzava.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new DrzavaStandardForm(cmbDrzava, ""), id);
				}
			});
		} else {
			cmbDrzava.setEnabled(false);
			for (int i = 0; i < cmbDrzava.getItemCount(); i++) {
				if (cmbDrzava.getItemAt(i).getId().equals(parentId)) {
					cmbDrzava.setSelectedIndex(i);
					break;
				}
			}
			btnZoom.setVisible(false);
		}

		tfSifraMesta.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});
		tfNazivMesta.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska2.setText("");
				}
			}
		});
		cmbDrzava.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbDrzava.getSelectedIndex() != -1) {
					lblGreska3.setText("");
				}
			}
		});

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraMesta);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivMesta);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblDrzava);
		dataPanel.add(cmbDrzava);
		dataPanel.add(btnZoom);
		dataPanel.add(lblGreska3, "gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PreduzeceAction());
		if (Constants.idPreduzeca != 0) {
			popup.add(new RadnikAction());
			popup.add(new PoslovniPartnerAction());
		}
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Mesto", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfSifraMesta.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfSifraMesta.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfSifraMesta.getText().trim())) {
			lblGreska1.setText(Constants.VALIDATION_BROJ);
			tfSifraMesta.requestFocus();
			return false;
		}
		if (tfNazivMesta.getText().equals("")) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNazivMesta.requestFocus();
			return false;
		}
		if (cmbDrzava.getSelectedIndex() == -1) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbDrzava.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Radnik", "Preduzece", "Poslovni_partner");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
