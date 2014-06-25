package firma.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import basexdb.util.FirmaDBUtil;
import beans.faktura.Faktura;
import beans.faktura.Faktura.Stavka;
import beans.faktura.Faktura.Zaglavlje;
import firma.gui.MainFrame;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ViewStavkeDialog extends JDialog {
	private JTable table;
	private JPanel dataPanel = new JPanel();
	private Faktura faktura;
	private ViewFakturaDialog parent;
	private int index;

	public ViewStavkeDialog(Object fak, ViewFakturaDialog parentWindow, int index) {
		parent = parentWindow;
		this.index = index;
		setTitle("Pregled stavki fakture");
		setLayout(new MigLayout("fill"));
		setSize(1000, 600);

		faktura = (Faktura) fak;

		ListTableModel model = new ListTableModel(Arrays.asList(new String[] { "Redni broj", "Naziv robe/usluge",
				"Količina", "Jedinica mere", "Jedinična cena", "Vrednost", "Procenat rabata", "Iznos rabata",
				"Umanjeno (rabat)", "Ukupan porez" }));

		for (Stavka s : faktura.getStavka()) {
			Object[] row = { s.getRedniBroj(), s.getKolicina(), s.getJedinicaMere(), s.getJedinicnaCena(),
					s.getVrednost(), s.getProcenatRabata(), s.getIznosRabata(), s.getUmanjenoZaRabat(),
					s.getUkupanPorez() };
			model.addRow(row);
		}
		model.setModelEditable(false);
		model.setColumnEditable(4, true);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.removeColumn(table.getColumnModel().getColumn(0));
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, "grow, wrap");
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new MigLayout("fillx"));

		dataPanel.setLayout(new MigLayout("gapx 15px"));
		JButton btnSacuvajIzmene = new JButton("Prihvati izmene");
		btnSacuvajIzmene.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double ukupnoStavke = 0, zaUplatuStavke = 0, ukupanPorezStavke = 0, ukupanRabatStavke = 0;

				ListTableModel model = (ListTableModel) table.getModel();
				for (int i = 0; i < model.getRowCount(); i++) {
					Double jedinicnaCena = Double.valueOf(model.getRow(i).get(4).toString());
					Double kolicina = Double.valueOf(model.getRow(i).get(2).toString());
					Double procenatRabat = Double.valueOf(model.getRow(i).get(6).toString());
					Double vrednost = jedinicnaCena * kolicina;
					Double iznosRabata = vrednost * procenatRabat / 100;
					Double vrednostUmanjenuZaRabat = vrednost - iznosRabata;
					Double porez = Double.valueOf(model.getRow(i).get(9).toString());
					faktura.getStavka().get(i).setJedinicnaCena(BigDecimal.valueOf(jedinicnaCena));
					faktura.getStavka().get(i).setVrednost(BigDecimal.valueOf(vrednost));
					faktura.getStavka().get(i).setIznosRabata(BigDecimal.valueOf(iznosRabata));
					faktura.getStavka().get(i).setUmanjenoZaRabat(BigDecimal.valueOf(vrednostUmanjenuZaRabat));

					ukupnoStavke += vrednost;
					zaUplatuStavke += vrednostUmanjenuZaRabat + porez;
					ukupanPorezStavke += porez;
					ukupanRabatStavke += iznosRabata;
				}

				faktura.getZaglavlje().setVrednostRobe(BigDecimal.valueOf(ukupnoStavke));
				faktura.getZaglavlje().setUkupanPorez(BigDecimal.valueOf(ukupanPorezStavke));
				faktura.getZaglavlje().setUkupnoRobaIUsluge(BigDecimal.valueOf(ukupnoStavke));
				faktura.getZaglavlje().setIznosZaUplatu(BigDecimal.valueOf(zaUplatuStavke));
				faktura.getZaglavlje().setUkupanRabat(BigDecimal.valueOf(ukupanRabatStavke));

				if (zaUplatuStavke > 50000) {
					MainFrame.getInstance().getBaza().getFaktureZaSefa().getFaktura().remove(faktura);
					MainFrame.getInstance().getBaza().getFaktureZaDirektora().getFaktura().add(faktura);
					FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
							"http://localhost:8081/BaseX75/rest/firmaa");
					((ListTableModel) parent.getTable().getModel()).removeRows(ViewStavkeDialog.this.index);
					;
				} else {
					FirmaDBUtil.storeFirmaDatabase(MainFrame.getInstance().getBaza(),
							"http://localhost:8081/BaseX75/rest/firmaa");
					Zaglavlje z = faktura.getZaglavlje();
					((ListTableModel) parent.getTable().getModel()).replaceRow(
							ViewStavkeDialog.this.index,
							Arrays.asList(new Object[] { faktura, z.getIdPoruke(), z.getNazivDobavljaca(),
									z.getAdresaDobavljaca(), z.getPibDobavljaca(), z.getNazivKupca(),
									z.getAdresaKupca(), z.getPibKupca(), z.getBrojRacuna(), z.getDatumRacuna(),
									z.getVrednostRobe(), z.getVrednostUsluga(), z.getUkupnoRobaIUsluge(),
									z.getUkupanRabat(), z.getUkupanPorez(), z.getOznakaValute(), z.getIznosZaUplatu(),
									z.getUplataNaRacun(), z.getDatumValute() }));
				}
				dispose();
			}
		});

		JPanel buttonsPanel = new JPanel();

		dataPanel.add(btnSacuvajIzmene);
		bottomPanel.add(dataPanel);

		buttonsPanel.setLayout(new MigLayout("wrap"));
		bottomPanel.add(buttonsPanel, "dock east");

		add(bottomPanel, "grow, wrap");
		setModal(true);
		setLocationRelativeTo(MainFrame.getInstance());
	}
}
