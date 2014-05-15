package root.gui.form;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import root.gui.action.ZoomFormAction;
import root.gui.tablemodel.TableModelCreator;
import root.util.ComboBoxPair;

public class RadnikStandardForm extends GenericForm {
	private static final long serialVersionUID = 1L;

	private JButton btnZoomMesto = new JButton("...");
	private JButton btnZoomPreduzece = new JButton("...");
	private String qsifra;
	private ZoomFormAction mestoZoom;
	private ZoomFormAction preduzeceZoom;

	protected JComboBox<ComboBoxPair> cmbPreduzece;
	protected JComboBox<ComboBoxPair> cmbMesto;
	// naseljenoMesto
	protected JTextField tfIme = new JTextField(30);
	protected JTextField tfPrezime = new JTextField(30);
	protected JTextField tfJmbg = new JTextField(13);
	protected JTextField tfAdresa = new JTextField(30);

	public RadnikStandardForm(JComboBox<ComboBoxPair> returning, String childWhere) {
		super(returning, childWhere);
		setTitle("Radnici");

		JLabel lblIme = new JLabel("Ime:");
		JLabel lblPrezime = new JLabel("Prezime:");
		JLabel lblJmbg = new JLabel("JMBG:");
		JLabel lblAdresa = new JLabel("Adresa:");
		JLabel lblPreduzece = new JLabel("Preduzeće:");
		JLabel lblMesto = new JLabel("Mesto:");
		tfIme.setName("ime");
		tfPrezime.setName("prezime");
		tfAdresa.setName("jmbg");
		tfAdresa.setName("adresa");

		cmbPreduzece = super.setupJoins(cmbPreduzece, "Preduzece", "id_preduzeca", "id preduzeća", "naziv_preduzeca",
				"naziv preduzeća");
		cmbMesto = super.setupJoins(cmbMesto, "Mesto", "id_mesta", "id mesta", "naziv_mesta", "naziv mesta");

		if (!childWhere.contains("id_preduzeca")) {
			cmbPreduzece.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					preduzeceZoom.setId(((ComboBoxPair) e.getItem()).getId());
				}
			});
			preduzeceZoom = new ZoomFormAction(new PreduzeceStandardForm(cmbPreduzece, ""));
			if (cmbPreduzece.getItemCount() > 0) {
				preduzeceZoom.setId(((ComboBoxPair) cmbPreduzece.getSelectedItem()).getId());
			}
			btnZoomPreduzece.addActionListener(preduzeceZoom);
		} else {
			cmbPreduzece.setEnabled(false);
		}
		if (!childWhere.contains("id_mesta")) {
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
			btnZoomMesto.addActionListener(mestoZoom);
		} else {
			cmbMesto.setEnabled(false);
		}

		dataPanel.add(lblIme);
		dataPanel.add(tfIme, "wrap, gapx 15px");

		dataPanel.add(lblPrezime);
		dataPanel.add(tfPrezime, "wrap,gapx 15px");

		dataPanel.add(lblJmbg);
		dataPanel.add(tfJmbg, "wrap, gapx 15px, span 3");

		dataPanel.add(lblPreduzece);
		dataPanel.add(cmbPreduzece);
		dataPanel.add(btnZoomPreduzece, "wrap, gapx 15px");

		dataPanel.add(lblAdresa);
		dataPanel.add(tfAdresa, "wrap, gapx 15px");

		dataPanel.add(lblMesto);
		dataPanel.add(cmbMesto);
		dataPanel.add(btnZoomMesto, "wrap, gapx 15px");

		setupTable();
	}

	@Override
	public void setupTable() {
		tableModel = TableModelCreator.createTableModel("Radnik", joinColumn);
		tableModel.setColumnForSorting(2);
		super.setupTable();
	}

	public String getQsifra() {
		return qsifra;
	}

	public void setQsifra(String qsifra) {
		this.qsifra = qsifra;
	}

}
