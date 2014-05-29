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
import root.gui.action.dialog.GodinaAction;
import root.gui.action.dialog.OrganizacionaJedinicaAction;
import root.gui.action.dialog.PoslovniPartnerAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;

public class PreduzeceStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbMesto;

	protected JTextField tfNaziv = new JTextField(22);
	protected JTextField tfBrojTelefona = new JTextField(14);
	protected JTextField tfAdresa = new JTextField(22);

	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();

	public PreduzeceStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Preduzeća");

		JLabel lblNaziv = new JLabel("Naziv preduzeća*:");
		JLabel lblBrojTelefona = new JLabel("Broj telefona:");
		JLabel lblAdresa = new JLabel("Adresa*:");
		JLabel lblMesto = new JLabel("Mesto*:");
		tfNaziv.setName("naziv preduzeća");
		tfBrojTelefona.setName("broj telefona preduzeća");
		tfAdresa.setName("adresa preduzeća");
		tfNaziv.setDocument(new JTextFieldLimit(30));
		tfAdresa.setDocument(new JTextFieldLimit(30));
		tfBrojTelefona.setDocument(new JTextFieldLimit(15));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);

		cmbMesto = super.setupJoinsWithComboBox(cmbMesto, "Mesto", "id_mesta", "id mesta", "naziv_mesta",
				"naziv mesta", false, "");
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
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
			btnZoom.setVisible(false);
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
					lblGreska2.setText("");
				}
			}
		});
		cmbMesto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbMesto.getSelectedIndex() != -1) {
					lblGreska3.setText("");
				}
			}
		});

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNaziv);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblBrojTelefona);
		dataPanel.add(tfBrojTelefona, "wrap,gapx 15px");

		dataPanel.add(lblAdresa);
		dataPanel.add(tfAdresa);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblMesto);
		dataPanel.add(cmbMesto);
		dataPanel.add(btnZoom);
		dataPanel.add(lblGreska3, "gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new GodinaAction());
		if (Constants.idPreduzeca != 0) {
			popup.add(new RadnikAction());
			popup.add(new OrganizacionaJedinicaAction());
			popup.add(new PoslovniPartnerAction());
		}
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);
		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Preduzeće", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfNaziv.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNaziv.requestFocus();
			return false;
		}
		if (tfAdresa.getText().equals("")) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfAdresa.requestFocus();
			return false;
		}
		if (cmbMesto.getSelectedIndex() == -1) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbMesto.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Poslovna_godina", "Radnik", "Magacin", "Organizaciona_jedinica");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 2));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
