package root.gui.form;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.dialog.AnalitikaMagacinskeKarticeAction;
import root.gui.action.dialog.PrometniDokumentAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;

public class VrstaPrometaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JTextField tfSifraVrste = new JTextField(2);
	private JTextField tfNazivVrste = new JTextField(20);
	private final JLabel lblGreska1 = new JLabel();
	private final JLabel lblGreska2 = new JLabel();

	public VrstaPrometaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Vrsta prometa");
		tfSifraVrste.setName("šifra prometa");
		tfNazivVrste.setName("naziv prometa");
		tfSifraVrste.setDocument(new JTextFieldLimit(2));
		tfNazivVrste.setDocument(new JTextFieldLimit(20));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);

		tableModel = TableModelCreator.createTableModel("Vrsta prometa", null);

		JLabel lblSifra = new JLabel("Šifra vrste*:");
		JLabel lblNaziv = new JLabel("Naziv vrste*:");

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraVrste);
		dataPanel.add(lblGreska1, "wrap, gapx 15");
		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivVrste);
		dataPanel.add(lblGreska2, "wrap, gapx 15");

		tfSifraVrste.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				int k = e.getKeyChar();
				if (k != 8) {
					lblGreska2.setText("");
				}
			}
		});
		tfNazivVrste.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
				int k = e.getKeyChar();
				if (k != 8) {
					lblGreska2.setText("");
				}
			}
		});

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PrometniDokumentAction());
		popup.add(new AnalitikaMagacinskeKarticeAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public boolean verification() {
		if (tfSifraVrste.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfSifraVrste.requestFocus();
			return false;
		}
		if (tfNazivVrste.getText().equals("")) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNazivVrste.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Prometni_dokument", "Analitika_magacinske_kartice");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 2));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
