package root.gui.form;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import root.gui.MainFrame;
import root.gui.action.NextFormButton;
import root.gui.action.dialog.NaseljenoMestoAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ColumnList;
import root.util.ComboBoxPair;
import root.util.Lookup;

public class DrzavaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private ColumnList drzavaColumnList;

	private JTextField tfSifraDrzave = new JTextField(3);
	private JTextField tfNazivDrzave = new JTextField(20);

	public DrzavaStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Države");
		tfSifraDrzave.setName("šifra države");
		tfNazivDrzave.setName("naziv države");

		tableModel = TableModelCreator.createTableModel("Država", null);

		tfSifraDrzave.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				String sifraDrzave = tfSifraDrzave.getText().trim();
				try {
					tfNazivDrzave.setText(Lookup.getDrzava(sifraDrzave));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		JLabel lblSifra = new JLabel("Šifra države:");
		JLabel lblNaziv = new JLabel("Naziv države:");

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraDrzave, "wrap");
		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivDrzave, "pushx");

		JPopupMenu popup = new JPopupMenu();
		popup.add(new NaseljenoMestoAction());
		btnNextForm = new NextFormButton(this, popup);
		toolBar.add(btnNextForm);

		setupTable();
		setLocationRelativeTo(MainFrame.getInstance());
	}

	public ColumnList getDrzavaColumnList() {
		return drzavaColumnList;

	}

	public void setDrzavaColumnList(ColumnList drzavaColumnList) {
		this.drzavaColumnList = drzavaColumnList;
	}
}
