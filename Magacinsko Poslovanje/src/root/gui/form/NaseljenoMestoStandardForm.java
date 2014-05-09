package root.gui.form;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import root.gui.action.ZoomFormAction;
import root.util.Lookup;

public class NaseljenoMestoStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");
	private String qsifra;

	protected JTextField tfSifraDrzave = new JTextField(5);
	protected JTextField tfNazivDrzave = new JTextField(20);
	// naseljenoMesto
	protected JTextField tfSifraMesta = new JTextField(5);
	protected JTextField tfNazivMesta = new JTextField(20);

	public NaseljenoMestoStandardForm() {
		super();
		setTitle("Naseljena mesta");

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

		btnZoom.addActionListener(new ZoomFormAction(this));

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Šifra");
		columnNames.add("Naziv");

		JLabel lblSifra = new JLabel("Šifra mesta:");
		JLabel lblNaziv = new JLabel("Naziv mesta:");
		JLabel lblSifraDrzave = new JLabel("Šifra države:");

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraMesta, "wrap, gapx 15px");

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivMesta, "wrap,gapx 15px, span 3");

		dataPanel.add(lblSifraDrzave);
		dataPanel.add(tfSifraDrzave, "gapx 15px");

		dataPanel.add(btnZoom);

		dataPanel.add(tfNazivDrzave, "pushx");
		tfNazivDrzave.setEditable(false);

		btnPickup.setEnabled(false);

		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				sync();
			}
		});
		// mode = Mod.MODE_EDIT;

	}

	public void setUpTable() {

		// NaseljenoMestoTableModel tableModel = new NaseljenoMestoTableModel(new String[] { "Šifra mesta",
		// "Naziv mesta",
		// "Šifra države", "Naziv države" }, 0);
		// tblGrid.setModel(tableModel);

		if (qsifra == null) {
			// tableModel.open();
		} else {
			// tableModel.openAsChildForm(qsifra);
			childAction = true;
		}

	}

	public String getQsifra() {
		return qsifra;
	}

	public void setQsifra(String qsifra) {
		this.qsifra = qsifra;
	}

}
