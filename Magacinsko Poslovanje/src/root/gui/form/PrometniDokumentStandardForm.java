package root.gui.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.dialog.StavkaPrometaAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.DateLabelFormatter;
import root.util.verification.VerificationMethods;

public class PrometniDokumentStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPoslovnaGodina = new JButton("...");
	private JButton btnZoomOrgJedinicaIz = new JButton("...");
	private JButton btnZoomOrgJedinicaU = new JButton("...");
	private JButton btnZoomVrstaPrometa = new JButton("...");
	private JButton btnZoomPoslovniPartner = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JComboBox<ComboBoxPair> cmbOrgJedinicaIz;
	protected JComboBox<ComboBoxPair> cmbOrgJedinicaU;
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

	public PrometniDokumentStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Prometni dokumenti");

		JLabel lblBrojPopisnog = new JLabel("Broj*: ");
		JLabel lblDatumOtvaranja = new JLabel("Datum otvaranja*: ");
		JLabel lblDatumKnjizenja = new JLabel("Datum knjiženja: ");
		JLabel lblStatus = new JLabel("Status: ");
		JLabel lblMagacin = new JLabel("Magacin iz kog se dobavlja*: ");
		JLabel lblVrstaPrometa = new JLabel("Vrsta prometa*: ");
		JLabel lblMagacinU = new JLabel("Magacin u koji se smešta: ");
		JLabel lblGodina = new JLabel("Godina*: ");
		JLabel lblPoslovniPartner = new JLabel("Poslovni partner: ");

		UtilDateModel model = new UtilDateModel();
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

		cmbGodina = super.setupJoins(cmbGodina, "Poslovna_godina", "id_poslovne_godine", "id poslovne godine",
				"godina", "godina", false, " WHERE zakljucena = 0");
		cmbOrgJedinicaIz = super.setupJoins(cmbOrgJedinicaIz, "Organizaciona_jedinica", "id_jedinice", "id jedinice",
				"naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");
		cmbVrstaPrometa = super.setupJoins(cmbVrstaPrometa, "Vrsta_prometa", "id_prometa", "id prometa",
				"naziv_prometa", "naziv prometa", false, "");
		cmbOrgJedinicaU = super.setupJoins(cmbOrgJedinicaU, "Organizaciona_jedinica", "id_jedinice", "id jedinice",
				"naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");
		cmbOrgJedinicaU.insertItemAt(new ComboBoxPair(0, ""), 0);
		cmbOrgJedinicaU.setSelectedIndex(0);
		cmbPoslovniPartner = super.setupJoins(cmbPoslovniPartner, "Poslovni_partner", "id_poslovnog_partnera",
				"id poslovnog partnera", "naziv_poslovnog_partnera", "naziv poslovnog partnera", false, "");
		cmbPoslovniPartner.insertItemAt(new ComboBoxPair(0, ""), 0);
		cmbPoslovniPartner.setSelectedIndex(0);

		if (!childWhere.contains("id_jedinice")) {
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
			btnZoomOrgJedinicaIz.setVisible(false);
		}
		if (!childWhere.contains("Org_id_jedinice")) {
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
			btnZoomOrgJedinicaU.setVisible(false);
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
			btnZoomPoslovniPartner.setVisible(false);
		}

		cmbGodina.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinicaIz.getSelectedIndex() != -1) {
					lblGreska1.setText("");
				}
			}
		});
		cmbOrgJedinicaIz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinicaIz.getSelectedIndex() != -1) {
					lblGreska2.setText("");
				}
			}
		});
		cmbVrstaPrometa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (cmbOrgJedinicaIz.getSelectedIndex() != -1) {
					lblGreska3.setText("");
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
					cmbOrgJedinicaU.setEnabled(true);
					cmbPoslovniPartner.setEnabled(false);
					cmbPoslovniPartner.setSelectedIndex(0);
				}
			}
		});
		rbPromet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rbPromet.isSelected()) {
					cmbPoslovniPartner.setEnabled(true);
					cmbOrgJedinicaU.setEnabled(false);
					cmbOrgJedinicaU.setSelectedIndex(0);
				}
			}
		});

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rbMagacin);
		btnGroup.add(rbPromet);
		rbPromet.setSelected(true);
		cmbOrgJedinicaU.setEnabled(false);

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina);
		dataPanel.add(btnZoomPoslovnaGodina);
		dataPanel.add(lblGreska1, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinicaIz);
		dataPanel.add(btnZoomOrgJedinicaIz);
		dataPanel.add(lblGreska2, "wrap, gapx 15px");

		dataPanel.add(lblVrstaPrometa);
		dataPanel.add(cmbVrstaPrometa);
		dataPanel.add(btnZoomVrstaPrometa);
		dataPanel.add(lblGreska3, "wrap, gapx 15px");

		dataPanel.add(rbMagacin);
		dataPanel.add(lblMagacinU);
		dataPanel.add(cmbOrgJedinicaU);
		dataPanel.add(btnZoomOrgJedinicaU, "wrap, gapx 15px");

		dataPanel.add(rbPromet);
		dataPanel.add(lblPoslovniPartner);
		dataPanel.add(cmbPoslovniPartner);
		dataPanel.add(btnZoomPoslovniPartner, "wrap, gapx 15px");

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(tfBrojPrometnogDokumenta);
		dataPanel.add(lblGreska4, "wrap, gapx 15px");

		dataPanel.add(lblDatumOtvaranja);
		dataPanel.add(dateDatumOtvaranja);
		dataPanel.add(lblGreska5, "wrap, gapx 15px");

		dataPanel.add(lblDatumKnjizenja);
		dataPanel.add(dateDatumKnjizenja, "wrap, gapx 15px");

		dataPanel.add(lblStatus);
		tfStatusPrometnog.setEditable(false);
		dataPanel.add(tfStatusPrometnog, "wrap, gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new StavkaPrometaAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		String customQuery = "SELECT id_prometnog_dokumenta, Poslovna_godina.id_poslovne_godine, Magacin1.id_jedinice, Vrsta_prometa.id_prometa,Magacin2.Org_id_jedinice, Poslovni_partner.id_poslovnog_partnera, broj_prometnog_dokumenta, datum_prometnog, datum_knjizenja_prometnog, status_prometnog, Poslovna_godina.godina, Magacin1.naziv_jedinice, Vrsta_prometa.naziv_prometa, Magacin2.naziv_jedinice, Poslovni_partner.naziv_poslovnog_partnera, prometni_version FROM Prometni_dokument JOIN Poslovna_godina ON Prometni_dokument.id_poslovne_godine = Poslovna_godina.id_poslovne_godine JOIN Organizaciona_jedinica Magacin1 ON Prometni_dokument.id_jedinice = Magacin1.id_jedinice JOIN Vrsta_prometa ON Prometni_dokument.id_prometa = Vrsta_prometa.id_prometa LEFT JOIN Organizaciona_jedinica Magacin2 ON Prometni_dokument.Org_id_jedinice = Magacin2.id_jedinice JOIN Poslovni_partner ON Prometni_dokument.id_poslovnog_partnera = Poslovni_partner.id_poslovnog_partnera";

		setupTable(customQuery);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Prometni dokument", joinColumn);
		tableModel.setColumnForSorting(2);

		super.setupTable(customQuery);
	}

	@Override
	protected void clearFields(boolean needFocus) {
		super.clearFields(needFocus);
		tfStatusPrometnog.setText("u fazi formiranja");
	}

	@Override
	public boolean verification() {
		if (cmbGodina.getSelectedIndex() == -1) {
			lblGreska1.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbGodina.requestFocus();
			return false;
		}
		if (cmbOrgJedinicaIz.getSelectedIndex() == -1) {
			lblGreska2.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbOrgJedinicaIz.requestFocus();
			return false;
		}
		if (cmbVrstaPrometa.getSelectedIndex() == -1) {
			lblGreska3.setText(Constants.VALIDATION_MANDATORY_FIELD);
			cmbVrstaPrometa.requestFocus();
			return false;
		}
		if (tfBrojPrometnogDokumenta.getText().equals("")) {
			lblGreska4.setText(Constants.VALIDATION_MANDATORY_FIELD);
			tfBrojPrometnogDokumenta.requestFocus();
			return false;
		}
		if (VerificationMethods.containsNumbers(tfBrojPrometnogDokumenta.getText())) {
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
}
