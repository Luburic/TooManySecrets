package root.gui.form;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.MainFrame;
import root.gui.action.NextFormButton;
import root.gui.action.dialog.NaseljenoMestoAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class VrstaPrometaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JTextField tfSifraVrste = new JTextField(2);
	private JTextField tfNazivVrste = new JTextField(20);

	public VrstaPrometaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Vrsta prometa");
		tfSifraVrste.setName("šifra prometa");
		tfNazivVrste.setName("naziv prometa");

		tableModel = TableModelCreator.createTableModel("Vrsta prometa", null);

		JLabel lblSifra = new JLabel("Šifra vrste:");
		JLabel lblNaziv = new JLabel("Naziv vrste:");

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraVrste, "wrap");
		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivVrste, "pushx");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new NaseljenoMestoAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable(null);
		setLocationRelativeTo(MainFrame.getInstance());
	}

	@Override
	public boolean verification() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allowDeletion() {
		// TODO Auto-generated method stub
		return false;
	}
}
