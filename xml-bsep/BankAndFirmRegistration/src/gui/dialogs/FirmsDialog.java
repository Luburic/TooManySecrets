package gui.dialogs;

import firms.Firm;
import firms.RegisteredFirms;
import gui.MainFrame;
import gui.tables.FirmsTableModel;

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

@SuppressWarnings("serial")
public class FirmsDialog extends JDialog {
	
	private FirmsTableModel firmsTableModel = null;
	private JTable tblGrid;
	private JToolBar toolBar;
	private JButton btnAdd, btnDelete, btnCertificates;
	private RegisteredFirms registeredFirms = null;
	private String[] columns;
	private String bankName;

	public FirmsDialog(MainFrame mf, String name) {
	
		setLayout(new MigLayout("fill"));

		setSize(new Dimension(600, 400));
		setTitle("Firm registration");
		setLocationRelativeTo(mf);
		setModal(true);

		toolBar = new JToolBar();


		btnAdd = new JButton("New firm");
		
		toolBar.add(btnAdd);

		btnDelete = new JButton("Delete firm");
		toolBar.add(btnDelete);

		btnCertificates = new JButton("View firm certificates");
		toolBar.add(btnCertificates);

		add(toolBar, "dock north");
		
		registeredFirms = RegisteredFirms.load(name);
		bankName = name;
		
		initTable();
		JScrollPane scrollPane = new JScrollPane(tblGrid);
		add(scrollPane, "grow, wrap");
		
		btnAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				NewFirmDialog bd = new NewFirmDialog(MainFrame.getInstance(), bankName);
				bd.setVisible(true);
				firmsTableModel.fillTable(bd.getRegisteredFirms());
				firmsTableModel.fireTableDataChanged();
			}			
		});
		
		btnDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int sel = tblGrid.getSelectedRow();
				if(sel < 0) return;
				Firm tempFirm = registeredFirms.removeFirm((String) firmsTableModel.getValueAt(sel, 1));
				registeredFirms.store(registeredFirms.getFirm(), bankName);
				firmsTableModel.fillTable(registeredFirms);
				firmsTableModel.fireTableDataChanged();
				File fileToDelete = new File("./keystores/" + tempFirm.getName()+".jks");
				if(fileToDelete.exists()) {
					fileToDelete.delete();
				}
				File fileToDelete2 = new File("./keystores/" + tempFirm.getName()+".cer");
				if(fileToDelete2.exists()) {
					fileToDelete2.delete();
				}
			}			
		});
		
		btnCertificates.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int sel = tblGrid.getSelectedRow();
				if(sel < 0) return;
				CertificateDialog cd = new CertificateDialog(RegisteredFirms.load(bankName).getFirmByPib((String)firmsTableModel.getValueAt(sel, 1)), bankName);
				cd.setVisible(true);
			}			
		});

	}

	private void initTable(){
		tblGrid = new JTable();
		tblGrid.setRowHeight(20);
		tblGrid.setAutoCreateColumnsFromModel(false);
		firmsTableModel = new FirmsTableModel();
		firmsTableModel.fillTable(registeredFirms);
		tblGrid.setModel(firmsTableModel);
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
		for (int i = 0; i < FirmsTableModel.colHeaders.length; i++) {
			TableColumn column = new TableColumn(i);
			tblGrid.addColumn(column);
		}
		firmsTableModel.fireTableDataChanged();
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
