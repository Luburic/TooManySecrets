package gui.dialogs;

import gui.MainFrame;
import gui.tables.BanksTableModel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;
import banks.Bank;
import banks.RegisteredBanks;

@SuppressWarnings("serial")
public class BanksDialog extends JDialog {

	private BanksTableModel banksTableModel = null;
	private JTable tblGrid;
	private JToolBar toolBar;
	private JButton btnAdd, btnDelete, btnCertificates;
	private RegisteredBanks registeredBanks = null;
	private String[] columns;

	public BanksDialog(MainFrame mf) {
	
		setLayout(new MigLayout("fill"));

		setSize(new Dimension(600, 400));
		setTitle("Bank registration");
		setLocationRelativeTo(mf);
		setModal(true);

		toolBar = new JToolBar();


		btnAdd = new JButton("New bank");
		
		toolBar.add(btnAdd);

		btnDelete = new JButton("Delete bank");
		toolBar.add(btnDelete);

		btnCertificates = new JButton("View bank certificates");
		toolBar.add(btnCertificates);

		add(toolBar, "dock north");
		
		registeredBanks = RegisteredBanks.load();
		
		initTable();
		JScrollPane scrollPane = new JScrollPane(tblGrid);
		add(scrollPane, "grow, wrap");
		
		btnAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				NewBankDialog bd = new NewBankDialog(MainFrame.getInstance());
				bd.setVisible(true);
				banksTableModel.fillTable(bd.getRegisteredBanks());
				banksTableModel.fireTableDataChanged();
			}			
		});
		
		btnDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int sel = tblGrid.getSelectedRow();
				if(sel < 0) return;
				Bank tempBank = registeredBanks.removeBank((String) banksTableModel.getValueAt(sel, 1));
				registeredBanks.store(registeredBanks.getBank());
				banksTableModel.fillTable(registeredBanks);
				banksTableModel.fireTableDataChanged();
				File fileToDelete = new File("./keystores/" + tempBank.getName());
				if(fileToDelete.exists()) {
					fileToDelete.delete();
				}
			}			
		});
		
		btnCertificates.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int sel = tblGrid.getSelectedRow();
				if(sel < 0) return;
				CertificateDialog cd = new CertificateDialog(RegisteredBanks.load().getBank((String)banksTableModel.getValueAt(sel, 1)));
				cd.setVisible(true);
			}			
		});

	}

	private void initTable(){
		tblGrid = new JTable();
		tblGrid.setRowHeight(20);
		tblGrid.setAutoCreateColumnsFromModel(false);
		banksTableModel = new BanksTableModel();
		banksTableModel.fillTable(registeredBanks);
		tblGrid.setModel(banksTableModel);
		tblGrid.getTableHeader().setReorderingAllowed(false);

		tblGrid.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				//showCerts.setEnabled(true);
				//showCertsAction.setEnabled(true);
				//removeBank.setEnabled(true);
			}
		});
		for (int i = 0; i < BanksTableModel.colHeaders.length; i++) {
			TableColumn column = new TableColumn(i);
			tblGrid.addColumn(column);
		}
		banksTableModel.fireTableDataChanged();
	}
	
	protected int getColumn(String str) {
		int i = 0;
    	for (; i<columns.length; i++) {
    		if(tblGrid.getColumnName(i).equals(str))
    			break;
    	}
		return i;
	}

}
