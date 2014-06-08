package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import root.dbConnection.DBConnection;
import root.gui.action.IzvestajPopisAction;
import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.StornirajPopisniAction;
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
	protected JButton btnStorniraj = new JButton(new StornirajPopisniAction(this));
	protected JButton btnIzvestaj = new JButton(new IzvestajPopisAction(this));

	private JButton btnZoomPoslovnaGodina = new JButton("...");
	private JButton btnZoomOrgJedinica = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JTextField tfBrojPopisnogDokumenta = new JTextField(5);
	protected JDatePickerImpl dateDatumOtvaranja;
	protected JTextField tfDatumKnjizenja = new JTextField(10);
	protected JTextField tfStatusPopisnog = new JTextField(15);
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

		cmbOrgJedinica = super.setupJoinsWithComboBox(cmbOrgJedinica, "Organizaciona_jedinica", "id_jedinice",
				"id jedinice", "naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");

		super.setupJoins("Poslovna_godina", "id_poslovne_godine", "godina", "godina");
		cmbGodina = new JComboBox<ComboBoxPair>();
		cmbGodina.insertItemAt(new ComboBoxPair(Constants.idGodine, Constants.godina), 0);
		cmbGodina.setSelectedIndex(0);
		cmbGodina.setEnabled(false);
		cmbGodina.setName("id poslovne godine");
		btnZoomPoslovnaGodina.setVisible(false);

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
			for (int i = 0; i < cmbOrgJedinica.getItemCount(); i++) {
				if (cmbOrgJedinica.getItemAt(i).getId().equals(parentId)) {
					cmbOrgJedinica.setSelectedIndex(i);
					break;
				}
			}
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
		dateDatumOtvaranja.addActionListener(new ActionListener() {

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

		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = tblGrid.getSelectedRow();
				if (i != -1) {
					String s = (String) tableModel.getValueAt(i, 6);
					if (s.trim().equals("u fazi formiranja")) {
						btnZakljuci.setEnabled(true);
						btnDelete.setEnabled(true);
						btnStorniraj.setEnabled(false);
						btnIzvestaj.setEnabled(true);
					} else {
						btnZakljuci.setEnabled(false);
						btnDelete.setEnabled(false);
						btnIzvestaj.setEnabled(false);
						if (s.trim().equals("proknjizen") && !Constants.godinaZakljucena) {
							btnStorniraj.setEnabled(true);
						} else {
							btnStorniraj.setEnabled(false);
						}
					}
				}
			}
		});
		btnZakljuci.setEnabled(false);
		btnStorniraj.setEnabled(false);

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(tfBrojPopisnogDokumenta, "span 2");
		dataPanel.add(lblGreska1, "wrap,gapx 15px");

		dataPanel.add(lblDatumOtvaranja);
		dataPanel.add(dateDatumOtvaranja, "span 2");
		dataPanel.add(lblGreska2, "wrap,gapx 15px");

		dataPanel.add(lblDatumKnjizenja);
		tfDatumKnjizenja.setEditable(false);
		dataPanel.add(tfDatumKnjizenja, "wrap");

		dataPanel.add(lblStatus);
		tfStatusPopisnog.setEditable(false);
		dataPanel.add(tfStatusPopisnog, "wrap, span 2");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica);
		dataPanel.add(lblGreska3, "wrap,gapx 15px");

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina);
		dataPanel.add(btnZoomPoslovnaGodina);
		dataPanel.add(lblGreska4, "wrap,gapx 15px");

		toolBar.add(btnZakljuci);
		toolBar.add(btnStorniraj);
		toolBar.addSeparator();
		toolBar.add(btnIzvestaj);
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
		if (Constants.idGodine != 0) {
			if (childWhere.equals("")) {
				tableModel.setWhereStmt(" WHERE Popisni_dokument1.id_poslovne_godine = " + Constants.idGodine);
			} else {
				tableModel.setWhereStmt(childWhere + " AND Popisni_dokument1.id_poslovne_godine = "
						+ Constants.idGodine);
			}
		}
		super.setupTable(customQuery);
	}

	@Override
	protected void clearFields(boolean needFocus) {
		super.clearFields(needFocus);
		tfStatusPopisnog.setText("u fazi formiranja");
		try {
			tfBrojPopisnogDokumenta.setText(Lookup.getRedniBroj("Popisni_dokument", "broj_popisnog_dokumenta"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		return true;
	}

	public void proknjiziDokument() {
		String date = tableModel.getValueAt(tblGrid.getSelectedRow(), 4).toString();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String now = dateFormatter.format(Calendar.getInstance().getTime());
		if (now.compareTo(date) < 0) {
			JOptionPane.showMessageDialog(this, "Datum otvaranja mora biti manji ili jednak današnjem", "Greška",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall("{ call ProknjiziPopis(?, ?, ?, ?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, now);
			proc.setObject(3, tableModel.getValueAt(tblGrid.getSelectedRow(), 1));
			proc.setObject(4, tableModel.getValueAt(tblGrid.getSelectedRow(), 2));
			proc.registerOutParameter(5, java.sql.Types.INTEGER);

			proc.executeUpdate();
			Integer retVal = proc.getInt(5);
			System.out.println(retVal);
			tableModel.setValueAt("proknjizen", tblGrid.getSelectedRow(), 6);
			tableModel.setValueAt(now, tblGrid.getSelectedRow(), 5);
			tfDatumKnjizenja.setText(now);
			tfStatusPopisnog.setText("proknjizen");
			DBConnection.getConnection().commit();
			proc.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 3));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}

	@Override
	public void setMode(int mode) {
		if (Constants.godinaZakljucena == true) {
			btnAdd.setEnabled(false);
			btnDelete.setEnabled(false);
			btnZakljuci.setEnabled(false);
			btnStorniraj.setEnabled(false);
			this.mode = mode;
			if (mode == Constants.MODE_SEARCH) {
				btnCommit.setEnabled(true);
				cmbOrgJedinica.setEnabled(true);
				tfBrojPopisnogDokumenta.setEnabled(true);
				tfDatumKnjizenja.setEnabled(true);
				dateDatumOtvaranja.setEnabled(true);
				tfStatusPopisnog.setEnabled(true);
				btnZoomOrgJedinica.setVisible(true);
			} else {
				btnCommit.setEnabled(false);
				cmbOrgJedinica.setEnabled(false);
				tfBrojPopisnogDokumenta.setEnabled(false);
				tfDatumKnjizenja.setEnabled(false);
				dateDatumOtvaranja.setEnabled(false);
				tfStatusPopisnog.setEnabled(false);
				btnZoomOrgJedinica.setVisible(false);
			}
		} else {
			super.setMode(mode);
			if (mode == Constants.MODE_SEARCH) {
				cmbOrgJedinica.setEnabled(true);
				tfBrojPopisnogDokumenta.setEnabled(true);
				tfDatumKnjizenja.setEnabled(true);
				dateDatumOtvaranja.setEnabled(true);
				tfStatusPopisnog.setEnabled(true);
			} else {
				tfDatumKnjizenja.setEnabled(false);
				tfStatusPopisnog.setEnabled(false);
			}
		}
	}

	public void stornirajDokument() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String now = dateFormatter.format(Calendar.getInstance().getTime());
		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall("{ call StornirajPopis(?, ?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, now);
			proc.registerOutParameter(3, java.sql.Types.INTEGER);

			proc.executeUpdate();
			Integer retVal = proc.getInt(3);
			System.out.println(retVal);
			tableModel.setValueAt("storniran", tblGrid.getSelectedRow(), 6);
			tfStatusPopisnog.setText("storniran");
			DBConnection.getConnection().commit();
			proc.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void izvestaj() {
		if (!tableModel.getValueAt(tblGrid.getSelectedRow(), 6).toString().trim().equals("u fazi formiranja")
				&& this.getMode() != Constants.MODE_EDIT) {
			return;
		}
		try {
			Map<String, Object> params = new HashMap<String, Object>(5);
			params.put("preduzece", Constants.nazivPreduzeca);
			params.put("naziv_jedinice", cmbOrgJedinica.getSelectedItem().toString());
			params.put("id_popisa", tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			params.put("broj_popisa", (Integer) tableModel.getValueAt(tblGrid.getSelectedRow(), 3));
			params.put("datum_popisa", tableModel.getValueAt(tblGrid.getSelectedRow(), 4));
			JasperPrint jp = JasperFillManager.fillReport(
					getClass().getResource("/root/izvestaj/PopisniDokument.jasper").openStream(), params,
					DBConnection.getConnection());
			JasperViewer.viewReport(jp, false);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
