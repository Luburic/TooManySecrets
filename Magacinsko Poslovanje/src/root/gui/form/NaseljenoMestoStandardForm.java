package root.gui.form;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.action.ZoomFormAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;
import root.util.Lookup;
import root.util.MetaSurogateDisplay;

public class NaseljenoMestoStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoom = new JButton("...");
	private String qsifra;
	private ZoomFormAction drzavaZoom;

	protected JComboBox<ComboBoxPair> cmbDrzava;
	// naseljenoMesto
	protected JTextField tfSifraMesta = new JTextField(5);
	protected JTextField tfNazivMesta = new JTextField(20);

	public NaseljenoMestoStandardForm(GenericForm returning) {
		super(returning);
		setTitle("Naseljena mesta");

		// Next mehanizam ce da proveri da li postoji columnlist i koliko ima clanova. Ako ima jedan odmah generisi
		// formu, ako ne dropdown lista. Klikom na konkretnu akciju, otvaramo formu, i uzimamo id drzave, formiramo
		// where clausulu i selektujemo. Bice problem kod visestrukih, tu cemo hardkodovati
		drzavaZoom = new ZoomFormAction(new DrzavaStandardForm(this));
		btnZoom.addActionListener(drzavaZoom);

		JLabel lblSifra = new JLabel("Šifra mesta:");
		JLabel lblNaziv = new JLabel("Naziv mesta:");
		JLabel lblDrzava = new JLabel("Država:");
		try {
			cmbDrzava = new JComboBox<ComboBoxPair>(Lookup.getDrzave());
			cmbDrzava.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					drzavaZoom.setId(((ComboBoxPair) e.getItem()).getId());
				}
			});
			drzavaZoom.setId(((ComboBoxPair) cmbDrzava.getSelectedItem()).getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		dataPanel.add(lblSifra);
		dataPanel.add(tfSifraMesta, "wrap, gapx 15px");

		dataPanel.add(lblNaziv);
		dataPanel.add(tfNazivMesta, "wrap,gapx 15px, span 3");

		dataPanel.add(lblDrzava);
		dataPanel.add(cmbDrzava, "gapx 15px");

		dataPanel.add(btnZoom);

		setupTable();
	}

	@Override
	public void setupTable() {
		List<MetaSurogateDisplay> listForJoin = new ArrayList<MetaSurogateDisplay>();
		MetaSurogateDisplay temp = new MetaSurogateDisplay();
		temp.setTableCode("Drzava");
		temp.setIdColumnName("id_drzave");
		temp.getDisplayColumnName().add("naziv_drzave");
		listForJoin.add(temp);
		tableModel = TableModelCreator.createTableModel("Mesto", listForJoin);
		super.setupTable();
		try {
			tableModel.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getQsifra() {
		return qsifra;
	}

	public void setQsifra(String qsifra) {
		this.qsifra = qsifra;
	}

}
