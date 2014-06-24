package firma.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import beans.faktura.Faktura;
import beans.faktura.Faktura.Stavka;
import firma.gui.MainFrame;
import firma.gui.actions.ApproveAction;
import firma.gui.actions.DenyAction;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ViewStavkeDialog extends JDialog {
	private JTable table;
	private JPanel dataPanel = new JPanel();
	private Faktura faktura;

	public ViewStavkeDialog(Object fak) {
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
				new ViewStavkeDialog(((ListTableModel) table.getModel()).getRow(table.getSelectedRow()).get(0));
			}
		});

		JPanel buttonsPanel = new JPanel();

		JButton btnApprove = new JButton(new ApproveAction());
		JButton btnDeny = new JButton(new DenyAction());

		bottomPanel.add(dataPanel);

		buttonsPanel.setLayout(new MigLayout("wrap"));
		buttonsPanel.add(btnApprove);
		buttonsPanel.add(btnDeny);
		bottomPanel.add(buttonsPanel, "dock east");

		add(bottomPanel, "grow, wrap");
		setModal(true);
		setLocationRelativeTo(MainFrame.getInstance());
	}
}
