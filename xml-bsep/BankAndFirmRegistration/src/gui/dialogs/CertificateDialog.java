package gui.dialogs;

import gui.tables.CertificatesTableModel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.Certificate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import banks.Bank;

@SuppressWarnings("serial")
public class CertificateDialog extends JDialog {
	
	private Bank bank;
	private JTable tblGrid;
	private JPanel titlePanel;
	private JPanel addPanel;
	private CertificatesTableModel certificateTableModel;
	private String title = "";
	private JButton btnNew = new JButton("New");
	private JButton btnView = new JButton("View");
	private JButton btnRevoke = new JButton("Revoke");
	private JButton btnClose = new JButton("Close");

	public CertificateDialog(Bank b){
		setTitle("Certificates");
		bank = b;
		setSize(480,360);
		setLayout(new MigLayout("","[grow]","[grow][grow]"));
		setLocationRelativeTo(null);
		setResizable(false);
		title += "Certificates for Bank :  ";
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		certificateTableModel = new CertificatesTableModel(bank.getX509Certificate());
		tblGrid = new JTable(certificateTableModel);
		tblGrid.setRowHeight(20);

		JScrollPane scrollPane = new JScrollPane(tblGrid);		

		titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		JPanel comp = new JPanel(new GridLayout(1, 1), false);
		JLabel label = new JLabel(title+bank.getName(), JLabel.CENTER);
		comp.add(label);
		titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		titlePanel.add(comp);		

		addPanel = new JPanel(new MigLayout("","[]","[][][][]"));
		addPanel.add(btnView, "wrap 20, w 80!");
		btnView.setEnabled(false);
		addPanel.add(btnNew, "wrap, w 80!");
		addPanel.add(btnRevoke, "wrap 130, w 80!");
		btnRevoke.setEnabled(false);
		addPanel.add(btnClose, "wrap, w 80!");

		add(titlePanel,"grow, wrap");
		add(scrollPane,"grow, split 2");
		add(addPanel,"grow, wrap");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		btnNew.addActionListener(new ActionListener (){
			public void actionPerformed(ActionEvent e) {
				NewBankDialog newCert =  new NewBankDialog(bank);
				newCert.setVisible(true);
				certificateTableModel.fillData(bank.getX509Certificate());
			}			
		});


		btnClose.addActionListener(new ActionListener (){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();				
			}			
		});

		btnView.addActionListener(new ActionListener (){
			public void actionPerformed(ActionEvent e) {
				int sel =  tblGrid.getSelectedRow();
				if(sel<0) return;
				Certificate cert = (Certificate) certificateTableModel.getValueAt(sel, 1);
				ViewCertificateDialog sd = new ViewCertificateDialog(cert);
				sd.setVisible(true);		
			}			
		});

		tblGrid.getTableHeader().setReorderingAllowed(false);
		tblGrid.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		tblGrid.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				btnView.setEnabled(true);
				btnRevoke.setEnabled(true);
			}
		});		
	}

}
