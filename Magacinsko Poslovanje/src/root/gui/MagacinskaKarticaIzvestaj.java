package root.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import root.dbConnection.DBConnection;
import root.util.ComboBoxPair;
import root.util.Constants;
import root.util.DateLabelFormatter;
import root.util.Lookup;

public class MagacinskaKarticaIzvestaj extends JDialog {
	private static final long serialVersionUID = 3409703900859791816L;

	private JLabel lblOdDatum = new JLabel("Od datuma:");
	private JDatePickerImpl odDatum;
	private JLabel lblDoDatum = new JLabel("Do datuma:");
	private JDatePickerImpl doDatum;
	private JLabel lblMagacin = new JLabel("Magacin:");
	private JComboBox<ComboBoxPair> cmbMagacini;
	private JLabel lblArtikal = new JLabel("Artikal:");
	private JComboBox<ComboBoxPair> cmbArtikal;
	private JButton jbOK = new JButton("Prosledi");
	private JButton jbCancel = new JButton("Odustani");

	public MagacinskaKarticaIzvestaj(Object idJedinice, Object idArtikla) {
		MigLayout migLayout = new MigLayout("wrap 2");
		setLayout(migLayout);
		setTitle("Robna kartica sa analitikom");
		setModal(true);

		UtilDateModel model = new UtilDateModel(new Date());
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		doDatum = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		model = new UtilDateModel(new Date());
		datePanel = new JDatePanelImpl(model);
		odDatum = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		try {
			cmbMagacini = new JComboBox<ComboBoxPair>(Lookup.getComboBoxEntity("Organizaciona_jedinica", "id_jedinice",
					"naziv_jedinice", " WHERE magacin = '1' AND id_preduzeca = " + Constants.idPreduzeca));
			cmbArtikal = new JComboBox<ComboBoxPair>(Lookup.getComboBoxEntity("Artikal", "id_artikla", "naziv_artikla",
					""));
			if (idJedinice != null) {
				for (int i = 0; i < cmbMagacini.getItemCount(); i++) {
					if (cmbMagacini.getItemAt(i).getId().equals(idJedinice)) {
						cmbMagacini.setSelectedIndex(i);
						break;
					}
				}
			}
			if (idArtikla != null) {
				for (int i = 0; i < cmbArtikal.getItemCount(); i++) {
					if (cmbArtikal.getItemAt(i).getId().equals(idArtikla)) {
						cmbArtikal.setSelectedIndex(i);
						break;
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		jbOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Map<String, Object> params = new HashMap<String, Object>(6);
					Date date1 = (Date) odDatum.getJDateInstantPanel().getModel().getValue();
					String dateString1 = "2000-01-01";
					if (date1 != null) {
						DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						dateString1 = formatter.format(date1);
					}
					Date date = (Date) doDatum.getJDateInstantPanel().getModel().getValue();
					String dateString = "2020-01-01";
					if (date != null) {
						DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						dateString = formatter.format(date);
					}
					params.put("odDatum", dateString1);
					params.put("doDatum", dateString);
					params.put("naziv_jedinice", MagacinskaKarticaIzvestaj.this.cmbMagacini.getSelectedItem()
							.toString());
					params.put("preduzece", Constants.nazivPreduzeca);
					params.put("id_jedinice",
							((ComboBoxPair) MagacinskaKarticaIzvestaj.this.cmbMagacini.getSelectedItem()).getId());
					Integer idArt = ((ComboBoxPair) MagacinskaKarticaIzvestaj.this.cmbArtikal.getSelectedItem())
							.getId();
					params.put("id_artikla", idArt);
					ArrayList<String> artikalInfo = Lookup.getArtikalInfo(idArt);
					params.put("nazivArtikla", artikalInfo.get(0));
					params.put("pakovanjeArtikla", artikalInfo.get(1));
					params.put("jedinicaArtikla", artikalInfo.get(2));
					JasperPrint jp = JasperFillManager.fillReport(
							getClass().getResource("/root/izvestaj/AnalitikaMagacinske.jasper").openStream(), params,
							DBConnection.getConnection());
					JasperViewer.viewReport(jp, false);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				dispose();
			}
		});

		add(lblMagacin);
		add(cmbMagacini, "wrap, gapx 15");
		add(lblArtikal);
		add(cmbArtikal, "wrap, gapx 15");
		add(lblOdDatum);
		add(odDatum, "wrap, gapx 15");
		add(lblDoDatum);
		add(doDatum, "wrap, gapx 15");
		add(jbOK);
		add(jbCancel);
		setSize(240, 240);
		this.setResizable(false);
		setLocationRelativeTo(null);
	}
}
