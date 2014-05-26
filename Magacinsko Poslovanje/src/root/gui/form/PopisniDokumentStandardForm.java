package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import root.dbConnection.DBConnection;
import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.ZakljuciPopisniAction;
import root.gui.action.dialog.ClanKomisijeAction;
import root.gui.action.dialog.StavkaPopisaAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.DateLabelFormatter;
import root.util.Lookup;
import root.util.verification.JTextFieldLimit;

public class PopisniDokumentStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	protected JButton btnZakljuci = new JButton(new ZakljuciPopisniAction(this));

	private JButton btnZoomPoslovnaGodina = new JButton("...");
	private JButton btnZoomOrgJedinica = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JTextField tfBrojPopisnogDokumenta = new JTextField(5);
	protected JDatePickerImpl dateDatumOtvaranja;
	protected JTextField tfDatumKnjizenja = new JTextField(10);
	protected JTextField tfStatusPopisnog = new JTextField(3);
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();
	protected JLabel lblGreska4 = new JLabel();

	public PopisniDokumentStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Popisni dokumenti");

		JLabel lblBrojPopisnog = new JLabel("Broj*: ");
		JLabel lblDatumOtvaranja = new JLabel("Datum otvaranja*: ");
		JLabel lblDatumKnjizenja = new JLabel("Datum knjiženja: ");
		JLabel lblStatus = new JLabel("Status: ");
		JLabel lblMagacin = new JLabel("Magacin*: ");
		JLabel lblGodina = new JLabel("Godina*: ");
		tfBrojPopisnogDokumenta.setDocument(new JTextFieldLimit(5));
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);
		lblGreska4.setForeground(Color.red);

		UtilDateModel model = new UtilDateModel(new Date());
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		dateDatumOtvaranja = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		tfBrojPopisnogDokumenta.setName("broj popisnog dokumenta");
		dateDatumOtvaranja.setName("datum otvaranja");
		tfDatumKnjizenja.setName("datum knjiženja");
		tfStatusPopisnog.setName("status popisnog");

		super.setupJoins(cmbOrgJedinica, "Organizaciona_jedinica", "id_jedinice", "id jedinice", "naziv_jedinice",
				"naziv jedinice", false, " WHERE magacin = 1");
		try {
			cmbOrgJedinica = new JComboBox<>(Lookup.getMagacini());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		cmbGodina = super.setupJoins(cmbGodina, "Poslovna_godina", "id_poslovne_godine", "id poslovne godine",
				"godina", "godina", false, " WHERE zakljucena = 0");

		if (!childWhere.contains("id_jedinice")) {
			btnZoomOrgJedinica.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbOrgJedinica.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbOrgJedinica.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new OrganizacionaJedinicaStandardForm(cmbOrgJedinica, ""), id);
				}
			});
		} else {
			cmbOrgJedinica.setEnabled(false);
			btnZoomOrgJedinica.setVisible(false);
		}
		if (!childWhere.contains("id_poslovne_godine")) {
			btnZoomPoslovnaGodina.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbGodina.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbGodina.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PoslovnaGodinaStandardForm(cmbGodina, ""), id);
				}
			});
		} else {
			cmbGodina.setEnabled(false);
			btnZoomPoslovnaGodina.setVisible(false);
		}

		tfBrojPopisnogDokumenta.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska1.setText("");
				}
			}
		});
		datePanel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lblGreska2.setText("");
			}
		});
		cmbOrgJedinica.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinica.getSelectedIndex() != -1) {
					lblGreska3.setText("");
				}
			}
		});
		cmbGodina.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbGodina.getSelectedIndex() != -1) {
					lblGreska4.setText("");
				}
			}
		});

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(tfBrojPopisnogDokumenta);
		dataPanel.add(lblGreska1, "wrap,gapx 15px");

		dataPanel.add(lblDatumOtvaranja);
		dataPanel.add(dateDatumOtvaranja);
		dataPanel.add(lblGreska2, "wrap,gapx 15px");

		dataPanel.add(lblDatumKnjizenja);
		tfDatumKnjizenja.setEditable(false);
		dataPanel.add(tfDatumKnjizenja, "wrap, gapx 15px");

		dataPanel.add(lblStatus);
		tfStatusPopisnog.setEditable(false);
		dataPanel.add(tfStatusPopisnog, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica);
		dataPanel.add(lblGreska3, "wrap,gapx 15px");

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina);
		dataPanel.add(btnZoomPoslovnaGodina);
		dataPanel.add(lblGreska4, "wrap,gapx 15px");

		toolBar.add(btnZakljuci);
		toolBar.addSeparator();
		JPopupMenu popup = new JPopupMenu();
		popup.add(new StavkaPopisaAction());
		popup.add(new ClanKomisijeAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Popisni dokument", joinColumn);
		tableModel.setColumnForSorting(3);
		super.setupTable(customQuery);
	}

	@Override
	protected void clearFields(boolean needFocus) {
		super.clearFields(needFocus);
		tfStatusPopisnog.setText("F");
	}

	@Override
	public boolean verification() {
		if (tfBrojPopisnogDokumenta.getText().equals("")) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfBrojPopisnogDokumenta.requestFocus();
			return false;
		}
		if (dateDatumOtvaranja.getJDateInstantPanel().getModel().getValue() == null) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			dateDatumOtvaranja.requestFocus();
			return false;
		}
		if (cmbOrgJedinica.getSelectedIndex() == -1) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbOrgJedinica.requestFocus();
			return false;
		}
		if (cmbGodina.getSelectedIndex() == -1) {
			lblGreska4.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbGodina.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Stavke_popisnog_dokumenta");
	}

	public void proknjiziDokument() {
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			String now = dateFormatter.format(new Date());
			PreparedStatement stmt = DBConnection.getConnection().prepareStatement(
					"EXEC ProknjiziPopis @Id = ?, @Datum = ?");
			stmt.setInt(1, (int) tblGrid.getModel().getValueAt(tblGrid.getSelectedRow(), 0));
			stmt.setString(2, now);
			stmt.execute();
			int i = tblGrid.getSelectedRow();
			tableModel.setValueAt(now, i, 5);
			tableModel.setValueAt("P", i, 6);
			tableModel.fireTableDataChanged();
			tblGrid.getSelectionModel().setSelectionInterval(i, i);
			sync();
			stmt.close();
			DBConnection.getConnection().commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
