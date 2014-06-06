package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.dbConnection.DBConnection;
import root.gui.action.NextFormButton;
import root.gui.action.NivelacijaAction;
import root.gui.action.PickupAction;
import root.gui.action.dialog.AnalitikaMagacinskeKarticeAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Constants;

public class MagacinskaKarticaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomOrgJedinica = new JButton("...");
	private JButton btnZoomArtikal = new JButton("...");
	private JButton btnZoomGodina = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbArtikal;
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JTextField tfProsecnaCenaPopisa = new JTextField(12);
	protected JTextField tfKolicinaPocetnog = new JTextField(12);
	protected JTextField tfKolicinaUlaza = new JTextField(12);
	protected JTextField tfKolicinaIzlaza = new JTextField(12);
	protected JTextField tfVrednostPocetnog = new JTextField(12);
	protected JTextField tfVrednostUlaza = new JTextField(12);
	protected JTextField tfVrednostIzlaza = new JTextField(12);

	protected JButton btnNivelacija = new JButton(new NivelacijaAction(this));

	public MagacinskaKarticaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Magacinska kartica");

		JLabel lblArtikal = new JLabel("Artikal: ");
		JLabel lblMagacin = new JLabel("Magacin: ");
		JLabel lblGodina = new JLabel("Godina: ");
		JLabel lblProsecnaCena = new JLabel("Prosečna cena: ");
		JLabel lblKolicinaPocetnogStanja = new JLabel("Kol. početnog stanja: ");
		JLabel lblKolicinaUlaza = new JLabel("Kol. ulaza: ");
		JLabel lblKolicinaIzlaza = new JLabel("Kol. izlaza: ");
		JLabel lblVrednostPocetnogStanja = new JLabel("Vrednost početnog stanja: ");
		JLabel lblVrednostUlaza = new JLabel("Vrednost ulaza: ");
		JLabel lblVrednostIzlaza = new JLabel("Vrednost izlaza: ");

		btnDelete.setEnabled(false);
		btnAdd.setEnabled(false);

		tfProsecnaCenaPopisa.setName("prosečna cena");
		tfKolicinaPocetnog.setName("količina početnog stanja");
		tfKolicinaUlaza.setName("količina ulaza");
		tfKolicinaIzlaza.setName("količina izlaza");
		tfVrednostPocetnog.setName("vrednost početnog stanja");
		tfVrednostUlaza.setName("vrednost ulaza");
		tfVrednostIzlaza.setName("vrednost izlaza");
		tfKolicinaIzlaza.setEditable(false);
		tfKolicinaUlaza.setEditable(false);
		tfKolicinaPocetnog.setEditable(false);
		tfVrednostIzlaza.setEditable(false);
		tfVrednostUlaza.setEditable(false);
		tfVrednostPocetnog.setEditable(false);

		super.setupJoins("Poslovna_godina", "id_poslovne_godine", "godina", "godina");
		cmbGodina = new JComboBox<ComboBoxPair>();
		cmbGodina.insertItemAt(new ComboBoxPair(Constants.idGodine, Constants.godina), 0);
		cmbGodina.setSelectedIndex(0);
		cmbGodina.setEnabled(false);
		cmbGodina.setName("id poslovne godine");
		btnZoomGodina.setVisible(false);
		cmbArtikal = super.setupJoinsWithComboBox(cmbArtikal, "Artikal", "id_artikla", "id artikla", "naziv_artikla",
				"naziv artikla", false, "");
		cmbOrgJedinica = super.setupJoinsWithComboBox(cmbOrgJedinica, "Organizaciona_jedinica", "id_jedinice",
				"id jedinice", "naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");

		if (!childWhere.contains("id_poslovne_godine")) {
			btnZoomGodina.addActionListener(new ActionListener() {
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
			btnZoomGodina.setVisible(false);
		}
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
			for (int i = 0; i < cmbArtikal.getItemCount(); i++) {
				if (cmbArtikal.getItemAt(i).getId().equals(parentId)) {
					cmbArtikal.setSelectedIndex(i);
					break;
				}
			}
			btnZoomArtikal.setVisible(false);
		}
		if (!childWhere.contains("id_organizacione_jedinice")) {
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

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina, "wrap");

		dataPanel.add(lblArtikal);
		dataPanel.add(cmbArtikal);
		dataPanel.add(btnZoomArtikal, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica, "wrap, gapx 15px");

		dataPanel.add(lblProsecnaCena);
		dataPanel.add(tfProsecnaCenaPopisa, "wrap");

		dataPanel.add(lblKolicinaPocetnogStanja);
		dataPanel.add(tfKolicinaPocetnog, "wrap");

		dataPanel.add(lblKolicinaUlaza);
		dataPanel.add(tfKolicinaUlaza, "wrap");

		dataPanel.add(lblKolicinaIzlaza);
		dataPanel.add(tfKolicinaIzlaza, "wrap");

		dataPanel.add(lblVrednostPocetnogStanja);
		dataPanel.add(tfVrednostPocetnog, "wrap");

		dataPanel.add(lblVrednostUlaza);
		dataPanel.add(tfVrednostUlaza, "wrap");

		dataPanel.add(lblVrednostIzlaza);
		dataPanel.add(tfVrednostIzlaza, "wrap");

		toolBar.add(btnNivelacija);
		toolBar.addSeparator();
		JPopupMenu popup = new JPopupMenu();
		popup.add(new AnalitikaMagacinskeKarticeAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Magacinska kartica", joinColumn);
		tableModel.setColumnForSorting(2);
		if (Constants.idGodine != 0) {
			if (childWhere.equals("")) {
				tableModel.setWhereStmt(" WHERE Magacinska_kartica1.id_poslovne_godine = " + Constants.idGodine);
			} else {
				tableModel.setWhereStmt(childWhere + " AND Magacinska_kartica1.id_poslovne_godine = "
						+ Constants.idGodine);
			}
		}
		super.setupTable(customQuery);
	}

	@Override
	public boolean verification() {
		return false;
	}

	@Override
	public boolean allowDeletion() {
		return false;
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 0));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;
		if (mode == Constants.MODE_SEARCH) {
			btnCommit.setEnabled(true);
			clearFields(true);
			cmbArtikal.setEnabled(true);
			cmbOrgJedinica.setEnabled(true);
			tfKolicinaIzlaza.setEditable(true);
			tfKolicinaPocetnog.setEditable(true);
			tfKolicinaUlaza.setEditable(true);
			tfProsecnaCenaPopisa.setEditable(true);
			tfVrednostIzlaza.setEditable(true);
			tfVrednostUlaza.setEditable(true);
			tfVrednostPocetnog.setEditable(true);
			btnZoomArtikal.setEnabled(true);
			btnZoomOrgJedinica.setEnabled(true);
		} else {
			btnCommit.setEnabled(false);
			cmbArtikal.setEnabled(false);
			cmbOrgJedinica.setEnabled(false);
			tfKolicinaIzlaza.setEditable(false);
			tfKolicinaPocetnog.setEditable(false);
			tfKolicinaUlaza.setEditable(false);
			tfProsecnaCenaPopisa.setEditable(false);
			tfVrednostIzlaza.setEditable(false);
			tfVrednostUlaza.setEditable(false);
			tfVrednostPocetnog.setEditable(false);
			btnZoomArtikal.setEnabled(false);
			btnZoomOrgJedinica.setEnabled(false);
		}
	}

	public void nivelacija() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String now = dateFormatter.format(Calendar.getInstance().getTime());
		try {
			CallableStatement proc = DBConnection.getConnection().prepareCall("{ call Nivelacija(?, ?) }");
			proc.setObject(1, tableModel.getValueAt(tblGrid.getSelectedRow(), 0));
			proc.setObject(2, now);
			proc.setObject(3, Constants.godinaZakljucena ? 1 : 0);

			proc.executeUpdate();
			DBConnection.getConnection().commit();
			proc.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
		}
	}
}