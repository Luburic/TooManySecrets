package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import root.gui.action.NextFormButton;
import root.gui.action.dialog.ArtikalAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.DateLabelFormatter;

public class PopisniDokumentStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;
	// Org jedinica i poslovna godina
	private JButton btnZoomPoslovnaGodina = new JButton("...");
	private JButton btnZoomOrgJedinica = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JTextField tfBrojPopisnogDokumenta = new JTextField(5);
	protected JDatePickerImpl dateDatumOtvaranja;
	protected JDatePickerImpl dateDatumKnjizenja;
	protected JCheckBox chkStatusPopisnog = new JCheckBox();

	public PopisniDokumentStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Popisni dokumenti");

		JLabel lblBrojPopisnog = new JLabel("Broj: ");
		JLabel lblDatumOtvaranja = new JLabel("Datum otvaranja: ");
		JLabel lblDatumKnjizenja = new JLabel("Datum knjiženja: ");
		JLabel lblStatus = new JLabel("Status: ");
		JLabel lblMagacin = new JLabel("Magacin: ");
		JLabel lblGodina = new JLabel("Godina: ");

		UtilDateModel model = new UtilDateModel();
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		dateDatumOtvaranja = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		model = new UtilDateModel();
		datePanel = new JDatePanelImpl(model);
		dateDatumKnjizenja = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		tfBrojPopisnogDokumenta.setName("broj popisnog dokumenta");
		dateDatumOtvaranja.setName("datum otvaranja");
		dateDatumKnjizenja.setName("datum knjiženja");
		chkStatusPopisnog.setName("status popisnog");
		// ######### Ovde će mi trebati posebna lookup funkcija za magacine (sa where).
		cmbOrgJedinica = super.setupJoins(cmbOrgJedinica, "Organizaciona_jedinica", "id_jedinice", "id jedinice",
				"naziv_jedinice", "naziv jedinice", false);
		cmbGodina = super.setupJoins(cmbGodina, "Poslovna_godina", "id_poslovne_godine", "id poslovne godine",
				"godina", "godina", false);

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
		}

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(tfBrojPopisnogDokumenta, "wrap,gapx 15px");

		dataPanel.add(lblDatumOtvaranja);
		dataPanel.add(dateDatumOtvaranja, "wrap, gapx 15px");

		dataPanel.add(lblDatumKnjizenja);
		dataPanel.add(dateDatumKnjizenja, "wrap, gapx 15px");

		dataPanel.add(lblStatus);
		dataPanel.add(chkStatusPopisnog, "wrap, gapx 15px");

		dataPanel.add(lblMagacin);
		dataPanel.add(cmbOrgJedinica);
		dataPanel.add(btnZoomOrgJedinica, "wrap, gapx 15px");

		dataPanel.add(lblGodina);
		dataPanel.add(cmbGodina);
		dataPanel.add(btnZoomPoslovnaGodina, "wrap, gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ArtikalAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Popisni dokument", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
