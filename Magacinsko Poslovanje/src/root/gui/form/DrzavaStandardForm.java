package root.gui.form;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import root.gui.MainFrame;
import root.gui.tablemodel.GenericTableModel;
import root.gui.tablemodel.TableModelCreator;
import root.util.ColumnList;
import root.util.Lookup;

public class DrzavaStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private GenericTableModel tableModel;

	private ColumnList drzavaColumnList;

	private JTextField tfSifraDrzave = new JTextField(5);
	private JTextField tfNazivDrzave = new JTextField(20);

	public DrzavaStandardForm() throws SQLException {
		super();
		setTitle("Države");
		tfSifraDrzave.setName("šifra države");
		tfNazivDrzave.setName("naziv države");

		tableModel = TableModelCreator.createTableModel("Država", null);
		tblGrid.setModel(tableModel);

		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				sync();
			}
		});

		try {
			tableModel.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}

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
