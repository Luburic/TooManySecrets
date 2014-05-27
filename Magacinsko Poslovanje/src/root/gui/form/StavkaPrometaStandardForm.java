package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.action.PickupAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.verification.JTextFieldLimit;
import root.util.verification.VerificationMethods;

public class StavkaPrometaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPopisniDokument = new JButton("...");
	private JButton btnZoomArtikal = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbArtikal;
	protected JComboBox<ComboBoxPair> cmbPrometniDokument;
	protected JTextField tfKolicinaPrometa = new JTextField(12);
	protected JTextField tfCenaPrometa = new JTextField(12);
	protected JTextField tfVrednostPrometa = new JTextField(12);
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();
	protected JLabel lblGreska4 = new JLabel();

	public StavkaPrometaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Stavka prometnog dokumenta");

		JLabel lblKolicinaPrometa = new JLabel("Količina*: ");
		JLabel lblCenaPrometa = new JLabel("Cena*: ");
		JLabel lblVrednostPrometa = new JLabel("Vrednost: ");
		JLabel lblArtikal = new JLabel("Artikal*: ");
		JLabel lblBrojPrometnog = new JLabel("Broj prometnog dokumenta*: ");
		tfKolicinaPrometa.setDocument(new JTextFieldLimit(12));
		tfVrednostPrometa.setDocument(new JTextFieldLimit(15));
		tfVrednostPrometa.setEditable(false);
		tfCenaPrometa.setDocument(new JTextFieldLimit(15));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);
		lblGreska4.setForeground(Color.red);

		tfKolicinaPrometa.setName("količina prometa");
		tfCenaPrometa.setName("cena prometa");
		tfVrednostPrometa.setName("vrednost prometa");

		cmbArtikal = super.setupJoins(cmbArtikal, "Artikal", "id_artikla", "id artikla", "naziv_artikla",
				"naziv artikla", false, "");
		cmbPrometniDokument = super.setupJoins(cmbPrometniDokument, "Popisni_dokument", "id_popisnog_dokumenta",
				"id popisnog dokumenta", "broj_popisnog_dokumenta", "broj popisnog dokumenta", false, "");

		cmbArtikal.setEnabled(false);
		cmbPrometniDokument.setEnabled(false);
		if (!childWhere.contains("id_artikla")) {
			btnZoomArtikal.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbArtikal.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbArtikal.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new ArtikalStandardForm(cmbArtikal, ""), id);
				}
			});
		} else {
			cmbArtikal.setEnabled(false);
			btnZoomArtikal.setVisible(false);
		}
		if (!childWhere.contains("id_popisnog_dokumenta")) {
			btnZoomPopisniDokument.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPrometniDokument.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPrometniDokument.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PopisniDokumentStandardForm(cmbPrometniDokument, ""), id);
				}
			});
		} else {
			cmbPrometniDokument.setEnabled(false);
			btnZoomPopisniDokument.setVisible(false);
		}

		cmbArtikal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbArtikal.getSelectedIndex() != -1) {
					lblGreska1.setText("");
				}
			}
		});
		cmbPrometniDokument.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbPrometniDokument.getSelectedIndex() != -1) {
					lblGreska2.setText("");
				}
			}
		});
		tfKolicinaPrometa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska3.setText("");
				}
			}
		});
		tfCenaPrometa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska4.setText("");
				}
			}
		});

		dataPanel.add(lblArtikal);
		dataPanel.add(cmbArtikal);
		dataPanel.add(btnZoomArtikal);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblBrojPrometnog);
		dataPanel.add(cmbPrometniDokument);
		dataPanel.add(btnZoomPopisniDokument);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaPrometa);
		dataPanel.add(tfKolicinaPrometa);
		dataPanel.add(lblGreska3, "wrap, gapx 15px");

		dataPanel.add(lblCenaPrometa);
		dataPanel.add(tfCenaPrometa);
		dataPanel.add(lblGreska4, "wrap, gapx 15px");

		dataPanel.add(lblVrednostPrometa);
		dataPanel.add(tfVrednostPrometa, "wrap, gapx 15px");

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Stavka popisa", joinColumn);
		tableModel.setColumnForSorting(1);
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		if (cmbArtikal.getSelectedIndex() == -1) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbArtikal.requestFocus();
			return false;
		}
		if (cmbPrometniDokument.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPrometniDokument.requestFocus();
			return false;
		}
		if (tfKolicinaPrometa.getText().equals("")) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfKolicinaPrometa.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfKolicinaPrometa.getText().trim())) {
			lblGreska3.setText(Constants.VALIDATION_BROJ);
			tfKolicinaPrometa.requestFocus();
			return false;
		}
		if (tfCenaPrometa.getText().equals("")) {
			lblGreska4.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfCenaPrometa.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return false;
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 2));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
