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
import root.gui.action.dialog.GodinaAction;
import root.gui.action.dialog.RadnikAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class PreduzeceStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");
	private ZoomFormAction mestoZoom;

	protected JComboBox<ComboBoxPair> cmbMesto;

	protected JTextField tfNaziv = new JTextField(30);
	protected JTextField tfBrojTelefona = new JTextField(15);
	protected JTextField tfAdresa = new JTextField(30);

	public PreduzeceStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Preduzeća");

		JLabel lblNaziv = new JLabel("Naziv preduzeća:");
		JLabel lblBrojTelefona = new JLabel("Broj telefona:");
		JLabel lblAdresa = new JLabel("Adresa:");
		JLabel lblMesto = new JLabel("Mesto:");
		tfNaziv.setName("naziv preduzeća");
		tfBrojTelefona.setName("broj telefona preduzeća");
		tfAdresa.setName("adresa preduzeća");

		cmbMesto = super.setupJoins(cmbMesto, "Mesto", "id_mesta", "id mesta", "naziv_mesta", "naziv mesta");
		if (childWhere.equals("")) {
			cmbMesto.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					mestoZoom.setId(((ComboBoxPair) e.getItem()).getId());
				}
			});

			mestoZoom = new ZoomFormAction(new NaseljenoMestoStandardForm(cmbMesto, ""));
			if (cmbMesto.getItemCount() > 0) {
				mestoZoom.setId(((ComboBoxPair) cmbMesto.getSelectedItem()).getId());
			}
			btnZoom.addActionListener(mestoZoom);
		} else {
			cmbMesto.setEnabled(false);
		}

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNaziv, "wrap, gapx 15px");

		dataPanel.add(lblBrojTelefona);
		dataPanel.add(tfBrojTelefona, "wrap,gapx 15px, span 3");

		dataPanel.add(lblAdresa);
		dataPanel.add(tfAdresa, "wrap, gapx 15px");

		dataPanel.add(lblMesto);
		dataPanel.add(cmbMesto, "gapx 15px");
		dataPanel.add(btnZoom);

		JPopupMenu popup = new JPopupMenu();
		popup.add(new RadnikAction());
		popup.add(new GodinaAction());
		btnNextForm = new NextFormButton(this, popup);
		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Preduzeće", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}
}
