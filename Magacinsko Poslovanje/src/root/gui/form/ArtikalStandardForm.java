package root.gui.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class ArtikalStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");

	protected JComboBox<ComboBoxPair> cmbGrupa;
	protected JTextField tfSifra = new JTextField(8);
	protected JTextField tfNaziv = new JTextField(20);

	public ArtikalStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Artikli");

		JLabel lblSifra = new JLabel("Šifra:");
		JLabel lblNaziv = new JLabel("Naziv:");
		JLabel lblGrupa = new JLabel("Grupa:");
		tfNaziv.setName("naziv artikla");
		tfSifra.setName("šifra artikla");

		cmbGrupa = super.setupJoins(cmbGrupa, "Grupa_artikla", "id_grupe", "id grupe", "naziv_grupe", "naziv grupe",
				false);
		if (childWhere.equals("")) {
			btnZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int id = 0;
					if (cmbGrupa.getSelectedItem() != null) {
						id = ((ComboBoxPair) cmbGrupa.getSelectedItem()).getId();
					}
					prepareDialogForZoom(new GrupaArtiklaStandardForm(cmbGrupa, ""), id);
				}
			});
		} else {
			cmbGrupa.setEnabled(false);
		}

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifra, "wrap, gapx 15px");

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNaziv, "wrap,gapx 15px, span 3");

		dataPanel.add(lblGrupa);
		dataPanel.add(cmbGrupa, "gapx 15px");

		dataPanel.add(btnZoom);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new PreduzeceAction());
		popup.add(new RadnikAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Artikal", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
