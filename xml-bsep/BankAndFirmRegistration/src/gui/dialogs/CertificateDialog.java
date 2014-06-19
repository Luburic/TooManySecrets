package gui.dialogs;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

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
import util.CertificatesUtility;
import util.MyDatatypeConverter;
import banks.Bank;
import banks.RegisteredBanks;
import crlBanks.CrlBank;
import crlCentralna.CrlCentralna;
import firms.Firm;
import firms.RegisteredFirms;
import gui.tables.CertificatesTableModel;

@SuppressWarnings("serial")
public class CertificateDialog extends JDialog {

	private Bank bank;
	private Firm firm;
	private JTable tblGrid;
	private JPanel titlePanel;
	private JPanel addPanel;
	private CertificatesTableModel certificateTableModel;
	private String title = "";
	private JButton btnNew = new JButton("New");
	private JButton btnView = new JButton("View");
	private JButton btnRevoke = new JButton("Revoke");
	private JButton btnClose = new JButton("Close");
	private CrlCentralna crlCentralna;
	private RegisteredBanks registeredBanks;
	private CrlBank crlBank;
	private RegisteredFirms registeredFirms;
	private String bankName;

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

		btnRevoke.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int cel =  tblGrid.getSelectedRow();
				if(cel<0)
					return;
				X509Certificate cert = (X509Certificate) certificateTableModel.getValueAt(cel, 1);
				CrlCentralna.Bank banka = new CrlCentralna.Bank();
				crlCentralna = CrlCentralna.load();
				registeredBanks = RegisteredBanks.load();
				banka.setName(CertificatesUtility.getOwner(cert).toLowerCase());
				banka.setDate(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
				banka.addCertificateID(cert.getSerialNumber().toString());

				crlCentralna.addBank(banka);

				crlCentralna.store(crlCentralna.getBank());

				bank.removeCertificate(cert);
				registeredBanks.removeBank(bank.getSwiftCode());
				registeredBanks.addBank(bank);
				registeredBanks.store(registeredBanks.getBank());
				certificateTableModel.fillData(bank.getX509Certificate());
				certificateTableModel.fireTableDataChanged();

			}
		});
	}
	
	public CertificateDialog(Firm f, String name){
		setTitle("Certificates");
		firm = f;
		bankName = name;
		setSize(480,360);
		setLayout(new MigLayout("","[grow]","[grow][grow]"));
		setLocationRelativeTo(null);
		setResizable(false);
		title += "Certificates for Firm :  ";
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		certificateTableModel = new CertificatesTableModel(firm.getX509Certificate());
		tblGrid = new JTable(certificateTableModel);
		tblGrid.setRowHeight(20);

		JScrollPane scrollPane = new JScrollPane(tblGrid);		

		titlePanel = new JPanel();
		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		JPanel comp = new JPanel(new GridLayout(1, 1), false);
		JLabel label = new JLabel(title+firm.getName(), JLabel.CENTER);
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
				NewFirmDialog newCert =  new NewFirmDialog(bankName, firm);
				newCert.setVisible(true);
				certificateTableModel.fillData(firm.getX509Certificate());
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

		btnRevoke.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int cel =  tblGrid.getSelectedRow();
				if(cel<0)
					return;
				X509Certificate cert = (X509Certificate) certificateTableModel.getValueAt(cel, 1);
				CrlBank.Firm firma = new CrlBank.Firm();
				crlBank = CrlBank.load(bankName);
				registeredFirms = RegisteredFirms.load(bankName);
				firma.setName(CertificatesUtility.getOwner(cert).toLowerCase());
				firma.setDate(MyDatatypeConverter.parseDate(MyDatatypeConverter.printDate(new Date())));
				firma.addCertificateID(cert.getSerialNumber().toString());

				crlBank.addFirm(firma);

				crlBank.store(crlBank.getFirm(), bankName);

				firm.removeCertificate(cert);
				registeredFirms.removeFirm(firm.getPib());
				registeredFirms.addFirm(firm);
				registeredFirms.store(registeredFirms.getFirm(), bankName);
				certificateTableModel.fillData(firm.getX509Certificate());
				certificateTableModel.fireTableDataChanged();

			}
		});
	}

}
