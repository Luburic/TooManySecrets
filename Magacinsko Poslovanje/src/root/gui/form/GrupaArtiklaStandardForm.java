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
import root.gui.action.dialog.ArtikalAction;
import root.gui.action.dialog.GrupaArtiklaAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;

public class GrupaArtiklaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbGrupa;
	protected JTextField tfNazivGrupe = new JTextField(20);
	protected JLabel lblGreska1 = new JLabel();

	public GrupaArtiklaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Grupe artikla");

		JLabel lblNaziv = new JLabel("Naziv grupe*:");
		JLabel lblGrupa = new JLabel("Nadgrupa:");
		tfNazivGrupe.setName("naziv grupe");
		tfNazivGrupe.setDocument(new JTextFieldLimit(20));
		lblGreska1.setForeground(Color.red);

		cmbGrupa = super.setupJoins(cmbGrupa, "Grupa_artikla", "Gru_id_grupe", "Gru_id grupe", "naziv_grupe",
				"naziv nadgrupe", true);
		cmbGrupa.insertItemAt(new ComboBoxPair(0, ""), 0);
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbGrupa.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbGrupa.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new GrupaArtiklaStandardForm(cmbGrupa, ""), id);
				}
			});
		} else {
			cmbGrupa.setEnabled(false);
		}

		tfNazivGrupe.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivGrupe);
		dataPanel.add(lblGreska1, "wrap,gapx 15px");

		dataPanel.add(lblGrupa);
		dataPanel.add(cmbGrupa, "gapx 15px");

		dataPanel.add(btnZoom);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ArtikalAction());
		popup.add(new GrupaArtiklaAction(true));
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Grupa artikla", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfNazivGrupe.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNazivGrupe.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Artikal", "Grupa_artikla");
	}
}
