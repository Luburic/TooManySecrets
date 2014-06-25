package firma.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import util.ConstantsXWS;
import util.accessControl.TAkcija;
import beans.faktura.Faktura;
import beans.faktura.Faktura.Zaglavlje;
import firma.gui.MainFrame;
import firma.gui.actions.ApproveAction;
import firma.gui.actions.DenyAction;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ViewFakturaDialog extends AbstractViewDialog {

	private JTable table;
	private JPanel dataPanel = new JPanel();

	public ViewFakturaDialog() {
		setTitle("Pregled faktura");
		setLayout(new MigLayout("fill"));
		setSize(1000, 600);

		List<Faktura> fakture = new ArrayList<Faktura>();
		for (TAkcija akcija : ConstantsXWS.AKTIVNA_ROLA.getAkcije().getAkcija()) {
			if (akcija.getNazivAkcije().equals("odobrenjeFaktureIspodGranice")) {
				fakture = MainFrame.getInstance().getBaza().getFaktureZaSefa().getFaktura();
				break;
			}
			if (akcija.getNazivAkcije().equals("odobrenjeFaktureIznadGranice")) {
				fakture = MainFrame.getInstance().getBaza().getFaktureZaDirektora().getFaktura();
				break;
			}
		}
		ListTableModel model = new ListTableModel(
				Arrays.asList(new String[] { "faktura hold", "Id poruke", "Naziv dobavljaca", "Adresa dobavljaca",
						"Pib dobavljaca", "Naziv kupca", "Adresa kupca", "Pib kupca", "Broj racuna", "Datum racuna",
						"Vrednost robe", "Vrednost usluga", "Ukupno roba/usluge", "Ukupan rabat", "Ukupan porez",
						"Oznaka valute", "Iznos za uplatu", "Uplata na racun", "Datum valute" }));

		for (Faktura f : fakture) {
			Zaglavlje z = f.getZaglavlje();
			Object[] row = { f, z.getIdPoruke(), z.getNazivDobavljaca(), z.getAdresaDobavljaca(), z.getPibDobavljaca(),
					z.getNazivKupca(), z.getAdresaKupca(), z.getPibKupca(), z.getBrojRacuna(), z.getDatumRacuna(),
					z.getVrednostRobe(), z.getVrednostUsluga(), z.getUkupnoRobaIUsluge(), z.getUkupanRabat(),
					z.getUkupanPorez(), z.getOznakaValute(), z.getIznosZaUplatu(), z.getUplataNaRacun(),
					z.getDatumValute() };
			model.addRow(row);
		}

		model.setModelEditable(false);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.removeColumn(table.getColumnModel().getColumn(0));
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, "grow, wrap");
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new MigLayout("fillx"));

		dataPanel.setLayout(new MigLayout("gapx 15px"));
		JButton btnStavke = new JButton("Azuriraj stavke");
		btnStavke.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() > -1) {
					new ViewStavkeDialog(((ListTableModel) table.getModel()).getRow(table.getSelectedRow()).get(0),
							ViewFakturaDialog.this, table.getSelectedRow());
				}
			}
		});

		JPanel buttonsPanel = new JPanel();

		JButton btnApprove = new JButton(new ApproveAction(this));
		JButton btnDeny = new JButton(new DenyAction(this));

		dataPanel.add(btnStavke);
		bottomPanel.add(dataPanel);

		buttonsPanel.setLayout(new MigLayout("wrap"));
		buttonsPanel.add(btnApprove);
		buttonsPanel.add(btnDeny);
		bottomPanel.add(buttonsPanel, "dock east");

		add(bottomPanel, "grow, wrap");
		setModal(true);
		setLocationRelativeTo(MainFrame.getInstance());
	}

	public JTable getTable() {
		return table;
	}
}
