package firma.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import util.ConstantsXWS;
import util.DateLabelFormatter;
import util.accessControl.TAkcija;
import beans.nalog.Nalog;
import firma.gui.MainFrame;
import firma.gui.actions.ApproveAction;
import firma.gui.actions.DenyAction;
import firma.gui.tables.ListTableModel;

@SuppressWarnings("serial")
public class ViewNalogDialog extends AbstractViewDialog {

	private JTable table;
	private JPanel dataPanel = new JPanel();
	private JDatePickerImpl dateDatumNaloga = new JDatePickerImpl(new JDatePanelImpl(new UtilDateModel(new Date())),
			new DateLabelFormatter());
	private JDatePickerImpl dateDatumValute = new JDatePickerImpl(new JDatePanelImpl(new UtilDateModel(new Date())),
			new DateLabelFormatter());
	private JCheckBox chkHitno = new JCheckBox();

	public ViewNalogDialog() {
		setTitle("Pregled naloga");
		setLayout(new MigLayout("fill"));
		setSize(1000, 600);

/*		List<Nalog> nalozi = new ArrayList<Nalog>();
		for (TAkcija akcija : ConstantsXWS.AKTIVNA_ROLA.getAkcije().getAkcija()) {
			if (akcija.getNazivAkcije().equals("odobrenjeNalogIspodGranice")) {
				nalozi = MainFrame.getInstance().getBaza().getNalogZaSefa().getNalog();
				break;
			}
			if (akcija.getNazivAkcije().equals("odobrenjeNalogIznadGranice")) {
				nalozi = MainFrame.getInstance().getBaza().getNalogZaDirektora().getNalog();
				break;
			}

		}*/

		ListTableModel model = new ListTableModel(Arrays.asList(new String[] { "nalog hold", "Dužnik",
				"Svrha plaćanja", "Primalac", "Iznos" }));
/*
		for (Nalog n : nalozi) {
			Object[] row = { n, n.getDuznikNalogodavac(), n.getSvrhaPlacanja(), n.getPrimalacPoverilac(), n.getIznos() };
			model.addRow(row);
		}*/

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
		JPanel buttonsPanel = new JPanel();

		JButton btnApprove = new JButton("Odobri nalog");
		btnApprove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() != -1) {
					Nalog nalog = (Nalog) ((ListTableModel) table.getModel()).getRow(table.getSelectedRow()).get(0);
					nalog.setDatumNaloga((Date) dateDatumNaloga.getJDateInstantPanel().getModel().getValue());
					nalog.setDatumValute((Date) dateDatumValute.getJDateInstantPanel().getModel().getValue());
					nalog.setHitno(chkHitno.isSelected());

					AbstractAction a = new ApproveAction(ViewNalogDialog.this);
					a.actionPerformed(e);
				}
			}
		});
		JButton btnDeny = new JButton(new DenyAction(this));

		dataPanel.add(new JLabel("Datum naloga: "));
		dataPanel.add(dateDatumNaloga, "wrap");
		dataPanel.add(new JLabel("Datum valute: "));
		dataPanel.add(dateDatumValute, "wrap");
		dataPanel.add(new JLabel("Hitno: "));
		dataPanel.add(chkHitno, "wrap");

		bottomPanel.add(dataPanel);

		buttonsPanel.setLayout(new MigLayout("wrap"));
		buttonsPanel.add(btnApprove);
		buttonsPanel.add(btnDeny);
		bottomPanel.add(buttonsPanel, "dock east");

		add(bottomPanel, "grow, wrap");
		setModal(true);
		setLocationRelativeTo(MainFrame.getInstance());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() || table.getSelectedRow() == -1)
					return;
				sync();
			}
		});
	}

	public JTable getTable() {
		return table;
	}

	private void sync() {
		Nalog nalog = (Nalog) ((ListTableModel) table.getModel()).getRow(table.getSelectedRow()).get(0);
		String date = nalog.getDatumNaloga().toString();
		if (date.contains("-")) {
			String[] dates = date.split("\\-");
			dateDatumNaloga.getModel().setDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1,
					Integer.parseInt(dates[2]));
		}
		date = nalog.getDatumValute().toString();
		if (date.contains("-")) {
			String[] dates = date.split("\\-");
			dateDatumValute.getModel().setDate(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1,
					Integer.parseInt(dates[2]));
		}
	}
}
