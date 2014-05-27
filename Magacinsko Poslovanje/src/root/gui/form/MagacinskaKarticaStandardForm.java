package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.PickupAction;
import root.gui.action.dialog.ArtikalAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

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

		cmbGodina = super.setupJoins(cmbGodina, "Poslovna_godina", "id_poslovne_godine", "id poslovne godine",
				"godina", "godina", false, " WHERE zakljucena = 0");
		cmbArtikal = super.setupJoins(cmbArtikal, "Artikal", "id_artikla", "id artikla", "naziv_artikla",
				"naziv artikla", false, "");
		cmbOrgJedinica = super.setupJoins(cmbOrgJedinica, "Organizaciona_jedinica", "id_jedinice", "id jedinice",
				"naziv_jedinice", "naziv jedinice", false, " WHERE magacin = 1");

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
			btnZoomOrgJedinica.setVisible(false);
		}

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina);
		dataPanel.add(btnZoomGodina, "gapx 15px");

		dataPanel.add(lblArtikal);
		dataPanel.add(cmbArtikal);
		dataPanel.add(btnZoomArtikal, "gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica, "wrap, gapx 15px");

		dataPanel.add(lblProsecnaCena);
		dataPanel.add(tfProsecnaCenaPopisa, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaPocetnogStanja);
		dataPanel.add(tfKolicinaPocetnog, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaUlaza);
		dataPanel.add(tfKolicinaUlaza, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaIzlaza);
		dataPanel.add(tfKolicinaIzlaza, "wrap, gapx 15px");

		dataPanel.add(lblVrednostPocetnogStanja);
		dataPanel.add(tfVrednostPocetnog, "wrap, gapx 15px");

		dataPanel.add(lblVrednostUlaza);
		dataPanel.add(tfVrednostUlaza, "wrap, gapx 15px");

		dataPanel.add(lblVrednostIzlaza);
		dataPanel.add(tfVrednostIzlaza, "wrap, gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ArtikalAction());// #####Treba analitika magacinske kartice ovde
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
	}

	@Override
	public void setupTable(String customQuery) {
		tableModel = TableModelCreator.createTableModel("Magacinska kartica", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable(customQuery);
	}

	@Override
	protected void clearFields(boolean needFocus) {
		super.clearFields(needFocus);
		tfKolicinaIzlaza.setText("0");
		tfKolicinaUlaza.setText("0");
		tfKolicinaPocetnog.setText("0");
		tfVrednostIzlaza.setText("0");
		tfVrednostUlaza.setText("0");
		tfVrednostPocetnog.setText("0");
	}

	@Override
	public boolean verification() {
		int n = tableModel.getRowCount();
		Integer id_artikla = ((ComboBoxPair) cmbArtikal.getSelectedItem()).getId();
		Integer id_magacina = ((ComboBoxPair) cmbOrgJedinica.getSelectedItem()).getId();
		Integer id_godine = ((ComboBoxPair) cmbGodina.getSelectedItem()).getId();

		for (int i = 0; i < n; i++) {
			if (tableModel.getValueAt(i, 1).equals(id_godine) && tableModel.getValueAt(i, 2).equals(id_artikla)
					&& tableModel.getValueAt(i, 3).equals(id_magacina)) {
				tblGrid.getSelectionModel().setSelectionInterval(i, i);
				JOptionPane.showConfirmDialog(this,
						"Magacinska kartica za dati artikal u datom magacinu i godini već postoji");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean allowDeletion() {
		return allowDeletion("Analtika_magacinske_kartice");
	}

	@Override
	protected void initPickup() {
		btnPickup = new JButton(new PickupAction(this, 0));
		toolBar.add(btnPickup);
		btnPickup.setEnabled(false);
	}
}
