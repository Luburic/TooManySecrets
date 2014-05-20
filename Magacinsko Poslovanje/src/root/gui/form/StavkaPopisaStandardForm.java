package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.dialog.ArtikalAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class StavkaPopisaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomPopisniDokument = new JButton("...");
	private JButton btnZoomArtikal = new JButton("...");
	protected JComboBox<ComboBoxPair> cmbArtikal;
	protected JComboBox<ComboBoxPair> cmbPopisniDokument;
	protected JTextField tfPopisanaKolicina = new JTextField(12);
	protected JTextField tfKolicinaPoKnjigama = new JTextField(12);
	protected JTextField tfProsecnaCenaPopisa = new JTextField(12);

	public StavkaPopisaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Stavka popisnog dokumenta");

		JLabel lblPopisanaKolicina = new JLabel("Popisana količina: ");
		JLabel lblKolicinaPoKnjigama = new JLabel("Količina po knjigama: ");
		JLabel lblProsecnaCenaPopisa = new JLabel("Prosečna cena: ");
		JLabel lblArtikal = new JLabel("Artikal: ");
		JLabel lblBrojPopisnog = new JLabel("Broj popisnog dokumenta: ");

		tfPopisanaKolicina.setName("popisana količina");
		tfKolicinaPoKnjigama.setName("količina po knjigama");
		tfProsecnaCenaPopisa.setName("prosečna cena popis");

		cmbArtikal = super.setupJoins(cmbArtikal, "Artikal", "id_artikla", "id artikla", "naziv_artikla",
				"naziv artikla", false);
		cmbPopisniDokument = super.setupJoins(cmbPopisniDokument, "Popisni_dokument", "id_popisnog_dokumenta",
				"id popisnog dokumenta", "broj_popisnog_dokumenta", "broj popisnog dokumenta", false);

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
		}
		if (!childWhere.contains("id_popisnog_dokumenta")) {
			btnZoomPopisniDokument.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbPopisniDokument.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbPopisniDokument.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new PopisniDokumentStandardForm(cmbPopisniDokument, ""), id);
				}
			});
		} else {
			cmbPopisniDokument.setEnabled(false);
		}

		dataPanel.add(lblArtikal);
		dataPanel.add(cmbArtikal, "gapx 15px");
		dataPanel.add(btnZoomArtikal, "gapx 15px");

		dataPanel.add(lblBrojPopisnog);
		dataPanel.add(cmbPopisniDokument);
		dataPanel.add(btnZoomPopisniDokument, "wrap, gapx 15px");

		dataPanel.add(lblPopisanaKolicina);
		dataPanel.add(tfPopisanaKolicina, "wrap, gapx 15px");

		dataPanel.add(lblKolicinaPoKnjigama);
		dataPanel.add(tfKolicinaPoKnjigama, "wrap, gapx 15px");

		dataPanel.add(lblProsecnaCenaPopisa);
		dataPanel.add(tfProsecnaCenaPopisa, "wrap, gapx 15px");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new ArtikalAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Stavka popisa", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
