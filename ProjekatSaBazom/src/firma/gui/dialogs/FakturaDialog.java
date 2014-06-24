package firma.gui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

import util.ConstantsXWS;
import util.accessControl.TAkcija;
import beans.faktura.Faktura;
import beans.faktura.Faktura.Zaglavlje;
import firma.gui.MainFrame;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class FakturaDialog extends JDialog {

	private JTable table;
	private JPanel mainPanel = new JPanel();

	public FakturaDialog() {
		setTitle("Pregled faktura");
		setSize(800, 600);

		List<Faktura> fakture = new ArrayList<Faktura>();
		for (TAkcija akcija : ConstantsXWS.AKTIVNA_ROLA.getAkcije().getAkcija()) {
			if (akcija.getNazivAkcije().equals("odobrenjeFaktureIspodGranice")) {
				MainFrame.getInstance().getBaza().getFaktureZaSefa().getFaktura();
				break;
			}
			if (akcija.getNazivAkcije().equals("odobrenjeFaktureIznadGranice")) {
				MainFrame.getInstance().getBaza().getFaktureZaDirektora().getFaktura();
				break;
			}
		}
		ListTableModel model = new ListTableModel(Arrays.asList(new String[] { "Id poruke", "Naziv dobavljaca",
				"Adresa dobavljaca", "Pib dobavljaca", "Naziv kupca", "Adresa kupca", "Pib kupca", "Broj racuna",
				"Datum racuna", "Vrednost robe", "Vrednost usluga", "Ukupno roba/usluge", "Ukupan rabat",
				"Ukupan porez", "Oznaka valute", "Iznos za uplatu", "Uplata na racun", "Datum valute", }));

		for (Faktura f : fakture) {
			Zaglavlje z = f.getZaglavlje();
			Object[] row = { z.getIdPoruke(), z.getNazivDobavljaca(), z.getAdresaDobavljaca(), z.getPibDobavljaca(),
					z.getNazivKupca(), z.getAdresaKupca(), z.getPibKupca(), z.getBrojRacuna(), z.getDatumRacuna(),
					z.getVrednostRobe(), z.getVrednostUsluga(), z.getUkupnoRobaIUsluge(), z.getUkupanRabat(),
					z.getUkupanPorez(), z.getOznakaValute(), z.getIznosZaUplatu(), z.getUplataNaRacun(),
					z.getDatumValute() };
			model.addRow(row);
		}

		table = new JTable(model);
		mainPanel.add(table);
		this.add(mainPanel);

		setLocationRelativeTo(MainFrame.getInstance());
	}
}
