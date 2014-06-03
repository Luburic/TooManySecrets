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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import root.dbConnection.DBConnection;
import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.ZakljuciPrometniAction;
import root.gui.action.dialog.StavkaPrometaAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.DateLabelFormatter;
import root.util.verification.VerificationMethods;

public class PrometniDokumentStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPoslovnaGodina = new JButton("...");
	private JButton btnZoomOrgJedinicaU = new JButton("...");
	private JButton btnZoomOrgJedinicaIz = new JButton("...");
	private JButton btnZoomVrstaPrometa = new JButton("...");
	private JButton btnZoomPoslovniPartner = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JComboBox<ComboBoxPair> cmbOrgJedinicaU;
	protected JComboBox<ComboBoxPair> cmbOrgJedinicaIz;
	protected JComboBox<ComboBoxPair> cmbVrstaPrometa;
	protected JComboBox<ComboBoxPair> cmbPoslovniPartner;
	protected JTextField tfBrojPrometnogDokumenta = new JTextField(5);
	protected JDatePickerImpl dateDatumOtvaranja;
	protected JTextField dateDatumKnjizenja = new JTextField(10);
	protected JTextField tfStatusPrometnog = new JTextField(15);
	protected JLabel lblGreska1 = new JLabel();
	protected JLabel lblGreska2 = new JLabel();
	protected JLabel lblGreska3 = new JLabel();
	protected JLabel lblGreska4 = new JLabel();
	protected JLabel lblGreska5 = new JLabel();
	protected JRadioButton rbMagacin = new JRadioButton("Međumagacinski promet");
	protected JRadioButton rbPromet = new JRadioButton("Regularan promet");

	protected JButton btnProknjizi = new JButton(new ZakljuciPrometniAction(this));

	private JLabel lblGreskaB = new JLabel();
	private JLabel lblGreskaA = new JLabel();

	public PrometniDokumentStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Prometni dokumenti");
		setSize(1000, 400);

		JLabel lblBrojPopisnog = new JLabel("Broj*: ");
		JLabel lblDatumOtvaranja = new JLabel("Datum otvaranja*: ");
		JLabel lblDatumKnjizenja = new JLabel("Datum knjiženja: ");
		JLabel lblStatus = new JLabel("Status: ");
		JLabel lblMagacin = new JLabel("Magacin*: ");
		JLabel lblVrstaPrometa = new JLabel("Vrsta prometa*: ");
		JLabel lblGodina = new JLabel("Godina*: ");

		btnProknjizi.setEnabled(false);

		UtilDateModel model = new UtilDateModel(new Date());
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		dateDatumOtvaranja = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		tfBrojPrometnogDokumenta.setName("broj prometnog dokumenta");
		dateDatumOtvaranja.setName("datum prometnog");
		dateDatumKnjizenja.setName("datum knjiženja prometnog");
		tfStatusPrometnog.setName("status prometnog");
		lblGreska1.setForeground(Color.red);
		lblGreska2.setForeground(Color.red);
		lblGreska3.setForeground(Color.red);
		lblGreska4.setForeground(Color.red);
		lblGreska5.setForeground(Color.red);
		lblGreskaA.setForeground(Color.red);
		lblGreskaB.setForeground(Color.red);

		super.setupJoins("Poslovna_godina", "id_poslovne_godine", "godina", "godina");
		cmbGodina = new JComboBox<ComboBoxPair>();
		cmbGodina.insertItemAt(new ComboBoxPair(Constants.idGodine, Constants.godina), 0);
		cmbGodina.setSelectedIndex(0);
		cmbGodina.setEnabled(false);
		cmbGodina.setName("id poslovne godine");
		btnZoomPoslovnaGodina.setVisible(false);
		cmbOrgJedinicaU = super.setupJoinsWithComboBox(cmbOrgJedinicaU, "Organizaciona_jedinica", "id_jedinice",
				"id jedinice", "naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");
		cmbVrstaPrometa = super.setupJoinsWithComboBox(cmbVrstaPrometa, "Vrsta_prometa", "id_prometa", "id prometa",
				"sifra_prometa", "šifra prometa", false,
				" WHERE sifra_prometa = 'OT' OR sifra_prometa = 'NA' OR sifra_prometa = 'MM'");
		cmbOrgJedinicaIz = super.setupJoinsWithComboBox(cmbOrgJedinicaIz, "Organizaciona_jedinica", "id_jedinice",
				"id jedinice", "naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");
		cmbOrgJedinicaIz.insertItemAt(new ComboBoxPair(0, ""), 0);
		cmbOrgJedinicaIz.setSelectedIndex(0);
		cmbPoslovniPartner = super.setupJoinsWithComboBox(cmbPoslovniPartner, "Poslovni_partner",
				"id_poslovnog_partnera", "id poslovnog partnera", "naziv_poslovnog_partnera",
				"naziv poslovnog partnera", false, "");
		cmbPoslovniPartner.insertItemAt(new ComboBoxPair(0, ""), 0);
		cmbPoslovniPartner.setSelectedIndex(0);

		if (!childWhere.contains("id_jedinice")) {
			btnZoomOrgJedinicaU.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbOrgJedinicaU.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbOrgJedinicaU.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new OrganizacionaJedinicaStandardForm(cmbOrgJedinicaU, ""), id);
				}
			});
		} else {
			cmbOrgJedinicaU.setEnabled(false);
			for (int i = 0; i < cmbOrgJedinicaU.getItemCount(); i++) {
				if (cmbOrgJedinicaU.getItemAt(i).getId().equals(parentId)) {
					cmbOrgJedinicaU.setSelectedIndex(i);
					break;
				}
			}
			btnZoomOrgJedinicaU.setVisible(false);
		}
		if (!childWhere.contains("Org_id_jedinice")) {
			btnZoomOrgJedinicaIz.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbOrgJedinicaIz.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbOrgJedinicaIz.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new OrganizacionaJedinicaStandardForm(cmbOrgJedinicaIz, ""), id);
				}
			});
		} else {
			cmbOrgJedinicaIz.setEnabled(false);
			for (int i = 0; i < cmbOrgJedinicaIz.getItemCount(); i++) {
				if (cmbOrgJedinicaIz.getItemAt(i).getId().equals(parentId)) {
					cmbOrgJedinicaIz.setSelectedIndex(i);
					break;
				}
			}
			btnZoomOrgJedinicaIz.setVisible(false);
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
		if (!childWhere.contains("id_prometa")) {
			btnZoomVrstaPrometa.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbVrstaPrometa.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbVrstaPrometa.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new VrstaPrometaStandardForm(cmbVrstaPrometa, ""), id);
				}
			});
		} else {
			cmbVrstaPrometa.setEnabled(false);
			for (int i = 0; i < cmbVrstaPrometa.getItemCount(); i++) {
				if (cmbVrstaPrometa.getItemAt(i).getId().equals(parentId)) {
					cmbVrstaPrometa.setSelectedIndex(i);
					break;
				}
			}
			btnZoomVrstaPrometa.setVisible(false);
		}
		if (!childWhere.contains("id_poslovnog_partnera")) {
			btnZoomPoslovniPartner.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPoslovniPartner.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPoslovniPartner.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PoslovniPartnerStandardForm(cmbPoslovniPartner, ""), id);
				}
			});
		} else {
			cmbPoslovniPartner.setEnabled(false);
			for (int i = 0; i < cmbPoslovniPartner.getItemCount(); i++) {
				if (cmbPoslovniPartner.getItemAt(i).getId().equals(parentId)) {
					cmbPoslovniPartner.setSelectedIndex(i);
					break;
				}
			}
			btnZoomPoslovniPartner.setVisible(false);
		}

		cmbGodina.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinicaU.getSelectedIndex() != -1) {
					lblGreska1.setText("");
				}
			}
		});
		cmbOrgJedinicaU.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinicaU.getSelectedIndex() != -1) {
					lblGreska2.setText("");
				}
			}
		});
		cmbVrstaPrometa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbVrstaPrometa.getSelectedIndex() != -1) {
					lblGreska3.setText("");
				}
				if (((ComboBoxPair) cmbVrstaPrometa.getSelectedItem()).getCmbShow().equals("MM")) {
					rbMagacin.doClick();
				} else {
					rbPromet.doClick();
				}
			}
		});
		tfBrojPrometnogDokumenta.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() != 8) {
					lblGreska4.setText("");
				}
			}
		});
		dateDatumOtvaranja.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lblGreska5.setText("");
			}
		});
		rbMagacin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbMagacin.isSelected()) {
					cmbOrgJedinicaIz.setEnabled(true);
					cmbPoslovniPartner.setEnabled(false);
					cmbPoslovniPartner.setSelectedIndex(0);
				}
				lblGreskaA.setText("");
			}
		});
		rbPromet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbPromet.isSelected()) {
					cmbPoslovniPartner.setEnabled(true);
					cmbOrgJedinicaIz.setEnabled(false);
					cmbOrgJedinicaIz.setSelectedIndex(0);
				}
				lblGreskaB.setText("");
			}
		});
		cmbOrgJedinicaIz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinicaU.getSelectedIndex() != -1) {
					lblGreskaA.setText("");
				}
			}
		});
		cmbPoslovniPartner.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbPoslovniPartner.getSelectedIndex() != -1) {
					lblGreskaB.setText("");
				}
			}
		});

		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = tblGrid.getSelectedRow();
				if (i != -1) {
					String s = (String) tableModel.getValueAt(i, 9);
					if (s.trim().equals("u fazi formiranja")) {
						btnProknjizi.setEnabled(true);
						btnDelete.setEnabled(true);
						btnCommit.setEnabled(true);
					} else {
						btnProknjizi.setEnabled(false);
						btnDelete.setEnabled(false);
						btnCommit.setEnabled(false);
					}
				}
			}
		});

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rbMagacin);
		btnGroup.add(rbPromet);
		rbPromet.setSelected(true);
		cmbOrgJedinicaIz.setEnabled(false);

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina);
		dataPanel.add(btnZoomPoslovnaGodina);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinicaU);
		dataPanel.add(btnZoomOrgJedinicaU);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblVrstaPrometa);
		dataPanel.add(cmbVrstaPrometa);
		dataPanel.add(btnZoomVrstaPrometa);
		dataPanel.add(lblGreska3, "wrap, gapx 15px");

		dataPanel.add(rbMagacin);
		dataPanel.add(cmbOrgJedinicaIz);
		dataPanel.add(btnZoomOrgJedinicaIz);
		dataPanel.add(lblGreskaA, "wrap, gapx 15");

		dataPanel.add(rbPromet);
		dataPanel.add(cmbPoslovniPartner);
		dataPanel.add(btnZoomPoslovniPartner);
		dataPanel.add(lblGreskaB, "wrap, gapx 15");

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(tfBrojPrometnogDokumenta);
		dataPanel.add(lblGreska4, "wrap, gapx 15px");

		dataPanel.add(lblDatumOtvaranja);
		dataPanel.add(dateDatumOtvaranja, "span 4");
		dataPanel.add(lblGreska5, "wrap, gapx 15px");

		dataPanel.add(lblDatumKnjizenja);
		dateDatumKnjizenja.setEditable(false);
		dataPanel.add(dateDatumKnjizenja, "wrap");

		dataPanel.add(lblStatus);
		tfStatusPrometnog.setEditable(false);
		dataPanel.add(tfStatusPrometnog, "wrap, span 2");

		toolBar.add(btnProknjizi);
		toolBar.addSeparator();
		JPopupMenu popup = new JPopupMenu();
		popup.add(new StavkaPrometaAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		String customQuery = "";
		if (childWhere.equals("")) {
			customQuery = "SELECT id_prometnog_dokumenta, Poslovna_godina.id_poslovne_godine, Magacin1.id_jedinice, Vrsta_prometa.id_prometa,Magacin2.Org_id_jedinice, Poslovni_partner.id_poslovnog_partnera, broj_prometnog_dokumenta, datum_prometnog, datum_knjizenja_prometnog, status_prometnog, Poslovna_godina.godina, Magacin1.naziv_jedinice, Vrsta_prometa.sifra_prometa, Magacin2.naziv_jedinice, Poslovni_partner.naziv_poslovnog_partnera, prometni_version FROM Prometni_dokument JOIN Poslovna_godina ON Prometni_dokument.id_poslovne_godine = Poslovna_godina.id_poslovne_godine JOIN Organizaciona_jedinica Magacin1 ON Prometni_dokument.id_jedinice = Magacin1.id_jedinice JOIN Vrsta_prometa ON Prometni_dokument.id_prometa = Vrsta_prometa.id_prometa LEFT JOIN Organizaciona_jedinica Magacin2 ON Prometni_dokument.Org_id_jedinice = Magacin2.id_jedinice LEFT JOIN Poslovni_partner ON Prometni_dokument.id_poslovnog_partnera = Poslovni_partner.id_poslovnog_partnera WHERE Prometni_dokument.id_poslovne_godine = "
					+ Constants.idGodine + " ORDER BY broj_prometnog_dokumenta";
		} else {
			customQuery = "SELECT id_prometnog_dokumenta, Poslovna_godina.id_poslovne_godine, Organizaciona_jedinica.id_jedinice, Vrsta_prometa.id_prometa,Magacin2.Org_id_jedinice, Poslovni_partner.id_poslovnog_partnera, broj_prometnog_dokumenta, datum_prometnog, datum_knjizenja_prometnog, status_prometnog, Poslovna_godina.godina, Organizaciona_jedinica.naziv_jedinice, Vrsta_prometa.sifra_prometa, Magacin2.naziv_jedinice, Poslovni_partner.naziv_poslovnog_partnera, prometni_version FROM Prometni_dokument JOIN Poslovna_godina ON Prometni_dokument.id_poslovne_godine = Poslovna_godina.id_poslovne_godine JOIN Organizaciona_jedinica ON Prometni_dokument.id_jedinice = Organizaciona_jedinica.id_jedinice JOIN Vrsta_prometa ON Prometni_dokument.id_prometa = Vrsta_prometa.id_prometa LEFT JOIN Organizaciona_jedinica Magacin2 ON Prometni_dokument.Org_id_jedinice = Magacin2.id_jedinice LEFT JOIN Poslovni_partner ON Prometni_dokument.id_poslovnog_partnera = Poslovni_partner.id_poslovnog_partnera"
					+ childWhere
					+ " AND Prometni_dokument.id_poslovne_godine = "
					+ Constants.idGodine
					+ " ORDER BY broj_prometnog_dokumenta";
		}

		setupTable(customQuery);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Prometni dokument", joinColumn);
		tableModel.setColumnForSorting(6);
		super.setupTable(customQuery);
	}

	@Override
	protected void clearFields(boolean needFocus) {
		super.clearFields(needFocus);
		tfStatusPrometnog.setText("u fazi formiranja");
		cmbOrgJedinicaIz.setSelectedIndex(0);
		cmbPoslovniPartner.setSelectedIndex(0);
	}

	@Override
	public boolean verification() {
		if (cmbGodina.getSelectedIndex() == -1) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbGodina.requestFocus();
			return false;
		}
		if (cmbOrgJedinicaU.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbOrgJedinicaU.requestFocus();
			return false;
		}
		if (cmbVrstaPrometa.getSelectedIndex() == -1) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbVrstaPrometa.requestFocus();
			return false;
		}
		if (cmbOrgJedinicaIz.getSelectedIndex() == 0 && rbMagacin.isSelected()) {
			lblGreskaA.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPoslovniPartner.requestFocus();
			return false;
		}
		if (cmbPoslovniPartner.getSelectedIndex() == 0 && rbPromet.isSelected()) {
			lblGreskaB.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbPoslovniPartner.requestFocus();
			return false;
		}
		if (tfBrojPrometnogDokumenta.getText().equals("")) {
			lblGreska4.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfBrojPrometnogDokumenta.requestFocus();
			return false;
		}
		if (!VerificationMethods.containsNumbers(tfBrojPrometnogDokumenta.getText())) {
			lblGreska4.setText(Constants.VALIDATION_BROJ);
			tfBrojPrometnogDokumenta.requestFocus();
			return false;
		}
		if (dateDatumOtvaranja.getJDateInstantPanel().getModel().getValue() == null) {
			lblGreska5.setText(Constants.VALIDATION_MANDATORY_FIELD);
			dateDatumOtvaranja.requestFocus();
			return false;
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Stavka_prometa");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 6));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}

	public void proknjiziDokument() {
		String date = tableModel.getValueAt(tblGrid.getSelectedRow(), 7).toString();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String now = dateFormatter.format(Calendar.getInstance().getTime());
		if (now.compareTo(date) < 0) {
			JOptionPane.showMessageDialog(this, "Datum formiranja mora biti manji ili jednak današnjem", "Greška",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall(
					"{ call ProknjiziPromet(?, ?, ?, ?, ?, ?, ?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, tableModel.getValueAt(tblGrid.getSelectedRow(), 1));
			proc.setObject(3, tableModel.getValueAt(tblGrid.getSelectedRow(), 2));
			proc.setObject(4, tableModel.getValueAt(tblGrid.getSelectedRow(), 4));
			proc.setObject(5, tableModel.getValueAt(tblGrid.getSelectedRow(), 3));
			proc.setObject(6, ((ComboBoxPair) cmbVrstaPrometa.getSelectedItem()).getCmbShow());
			proc.setObject(7, now);
			proc.registerOutParameter(8, java.sql.Types.INTEGER);

			proc.executeUpdate();
			Integer retVal = proc.getInt(8);
			System.out.println(retVal);
			tableModel.setValueAt("proknjizen", tblGrid.getSelectedRow(), 9);
			tableModel.setValueAt(now, tblGrid.getSelectedRow(), 8);
			dateDatumKnjizenja.setText(now);
			tfStatusPrometnog.setText("proknjizen");
			DBConnection.getConnection().commit();
			proc.close();
			DBConnection.getConnection().close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void sync() {
		super.sync();
		cmbOrgJedinicaIz.setSelectedIndex(0);
		int k = tblGrid.getSelectedRow();
		if (k > -1) {
			for (int i = 0; i < cmbOrgJedinicaIz.getItemCount(); i++) {
				if (cmbOrgJedinicaIz.getItemAt(i).getId().equals(tableModel.getValueAt(k, 4))) {
					cmbOrgJedinicaIz.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	@Override
	public void setMode(int mode) {
		if (Constants.godinaZakljucena == true) {
			btnAdd.setEnabled(false);
			btnDelete.setEnabled(false);
			this.mode = mode;
			if (mode == Constants.MODE_SEARCH) {
				btnCommit.setEnabled(true);
				cmbOrgJedinicaU.setEnabled(true);
				cmbPoslovniPartner.setEnabled(true);
				cmbVrstaPrometa.setEnabled(true);
				rbMagacin.setEnabled(true);
				rbPromet.setEnabled(true);
				rbPromet.setSelected(true);
				tfBrojPrometnogDokumenta.setEnabled(true);
				tfStatusPrometnog.setEnabled(true);
				dateDatumOtvaranja.setEnabled(true);
				dateDatumKnjizenja.setEnabled(true);
				btnZoomOrgJedinicaIz.setVisible(true);
				btnZoomOrgJedinicaU.setVisible(true);
				btnZoomPoslovniPartner.setVisible(true);
				btnZoomVrstaPrometa.setVisible(true);
				clearFields(true);
			} else {
				btnCommit.setEnabled(false);
				cmbOrgJedinicaU.setEnabled(false);
				cmbPoslovniPartner.setEnabled(false);
				cmbVrstaPrometa.setEnabled(false);
				rbMagacin.setEnabled(false);
				rbPromet.setEnabled(false);
				tfBrojPrometnogDokumenta.setEnabled(false);
				tfStatusPrometnog.setEnabled(false);
				dateDatumOtvaranja.setEnabled(false);
				dateDatumKnjizenja.setEnabled(false);
				btnZoomOrgJedinicaIz.setVisible(false);
				btnZoomOrgJedinicaU.setVisible(false);
				btnZoomPoslovniPartner.setVisible(false);
				btnZoomVrstaPrometa.setVisible(false);
			}
		} else {
			super.setMode(mode);
			if (mode == Constants.MODE_SEARCH) {
				cmbOrgJedinicaU.setEnabled(true);
				cmbPoslovniPartner.setEnabled(true);
				cmbVrstaPrometa.setEnabled(true);
				rbMagacin.setEnabled(true);
				rbPromet.setEnabled(true);
				rbPromet.setSelected(true);
				tfBrojPrometnogDokumenta.setEnabled(true);
				tfStatusPrometnog.setEnabled(true);
				dateDatumOtvaranja.setEnabled(true);
				dateDatumKnjizenja.setEnabled(true);
				clearFields(true);
			} else {
				tfStatusPrometnog.setEnabled(false);
				dateDatumKnjizenja.setEnabled(false);
			}
		}
	}
}