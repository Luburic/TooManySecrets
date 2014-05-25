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
import root.gui.action.dialog.MagacinskaKarticaAction;
import root.gui.action.dialog.StavkaPopisaAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;

public class ArtikalStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbGrupa;
	protected JTextField tfSifra = new JTextField(8);
	protected JTextField tfNaziv = new JTextField(20);
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();

	public ArtikalStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Artikli");

		JLabel lblSifra = new JLabel("Šifra*:");
		JLabel lblNaziv = new JLabel("Naziv*:");
		JLabel lblGrupa = new JLabel("Grupa*:");
		tfNaziv.setName("naziv artikla");
		tfSifra.setName("šifra artikla");
		tfSifra.setDocument(new JTextFieldLimit(8));
		tfNaziv.setDocument(new JTextFieldLimit(20));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);

		cmbGrupa = super.setupJoins(cmbGrupa, "Grupa_artikla", "id_grupe", "id grupe", "naziv_grupe", "naziv grupe",
				false);
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
			btnZoom.setVisible(false);
		}

		tfSifra.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});
		tfNaziv.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska2.setText("");
				}
			}
		});
		cmbGrupa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbGrupa.getSelectedIndex() != -1) {
					lblGreska3.setText("");
				}
			}
		});

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifra);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNaziv);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblGrupa);
		dataPanel.add(cmbGrupa);
		dataPanel.add(btnZoom);
		dataPanel.add(lblGreska3, "gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new MagacinskaKarticaAction());
		popup.add(new StavkaPopisaAction());
		popup.add(new StavkaPopisaAction());// Stavka prometa kada bude kreirana ide ovde
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Artikal", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (tfSifra.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfSifra.requestFocus();
			return false;
		}
		if (tfNaziv.getText().equals("")) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfNaziv.requestFocus();
			return false;
		}
		if (cmbGrupa.getSelectedIndex() == -1) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbGrupa.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Stavka_popisa, Stavka_prometa, Magacinska_kartica");
	}
}
