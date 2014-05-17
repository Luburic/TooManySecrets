package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.dialog.ArtikalAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class PopisniDokumentStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;
	// Org jedinica i poslovna godina
	private JButton btnZoomPoslovnaGodina = new JButton("...");
	private JButton btnZoomOrgJedinica = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbOrgJedinica;
	protected JComboBox<ComboBoxPair> cmbGodina;
	protected JTextField tfBrojPopisnogDokumenta = new JTextField(5);
	protected JTextField tfDatumOtvaranja = new JTextField(20);
	protected JTextField tfDatumKnjizenja = new JTextField(20);
	protected JCheckBox chkStatusPopisnog = new JCheckBox();

	public PopisniDokumentStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Popisni dokumenti");

		JLabel lblNaziv = new JLabel("Naziv grupe:");
		JLabel lblGrupa = new JLabel("Nadgrupa:");
		tfNazivGrupe.setName("naziv grupe");

		cmbPreduzece = super.setupJoins(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća", "naziv_preduzeca",
				"naziv preduzeća", false);
		cmbMesto = super.setupJoins(cmbMesto, "Mesto", "id_mesta", "id mesta", "naziv_mesta", "naziv mesta", false);

		if (!childWhere.contains("id_preduzeca")) {
			btnZoomPreduzece.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPreduzece.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPreduzece.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PreduzeceStandardForm(cmbPreduzece, ""), id);
				}
			});
		} else {
			cmbPreduzece.setEnabled(false);
		}
		if (!childWhere.contains("id_mesta")) {
			btnZoomMesto.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbMesto.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbMesto.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new NaseljenoMestoStandardForm(cmbMesto, ""), id);
				}
			});
		} else {
			cmbMesto.setEnabled(false);
		}

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivGrupe, "wrap,gapx 15px, span 3");

		dataPanel.add(lblGrupa);
		dataPanel.add(cmbGrupa, "gapx 15px");

		dataPanel.add(btnZoom);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ArtikalAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Grupa artikla", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
