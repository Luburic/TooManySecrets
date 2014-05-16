package root.gui.form;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.action.NextFormButton;
import root.gui.action.ZoomFormAction;
import root.gui.action.dialog.PreduzeceAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class NaseljenoMestoStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");
	private ZoomFormAction drzavaZoom;

	protected JComboBox<ComboBoxPair> cmbDrzava;
	protected JTextField tfSifraMesta = new JTextField(5);
	protected JTextField tfNazivMesta = new JTextField(20);

	public NaseljenoMestoStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Naseljena mesta");

		JLabel lblSifra = new JLabel("Šifra mesta:");
		JLabel lblNaziv = new JLabel("Naziv mesta:");
		JLabel lblDrzava = new JLabel("Država:");
		tfNazivMesta.setName("naziv mesta");
		tfSifraMesta.setName("zip kod");

		cmbDrzava = super.setupJoins(cmbDrzava, "Drzava", "id_drzave", "id države", "naziv_drzave", "naziv države");
		if (childWhere.equals("")) {
			cmbDrzava.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					drzavaZoom.setId(((ComboBoxPair) e.getItem()).getId());
				}
			});
			drzavaZoom = new ZoomFormAction(new DrzavaStandardForm(cmbDrzava, ""));
			if (cmbDrzava.getItemCount() > 0) {
				drzavaZoom.setId(((ComboBoxPair) cmbDrzava.getSelectedItem()).getId());
			}
			btnZoom.addActionListener(drzavaZoom);
		} else {
			cmbDrzava.setEnabled(false);
		}

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraMesta, "wrap, gapx 15px");

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivMesta, "wrap,gapx 15px, span 3");

		dataPanel.add(lblDrzava);
		dataPanel.add(cmbDrzava, "gapx 15px");

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
		tableModel = TableModelCreator.createTableModel("Mesto", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
