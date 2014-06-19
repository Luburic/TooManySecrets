package gui.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.X509Certificate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import security.CertificateGenerator;
import firms.Firm;
import firms.RegisteredFirms;
import gui.MainFrame;

@SuppressWarnings("serial")
public class NewFirmDialog extends JDialog {
	
	private JLabel lblAlgorithm = new JLabel("Signature Algorithm:");
	private JLabel lblValidity = new JLabel("Validity period (days):");
	private JLabel lblCN = new JLabel("Name:");
	private JLabel lblOU = new JLabel("Organisation Name (OU):");
	private JLabel lblO = new JLabel("Organisation Name (O):");
	private JLabel lblL = new JLabel("Locality Postcode (L):");
	private JLabel lblC = new JLabel("Country Code (C):");
	private JLabel lblE = new JLabel("Email (E):");
	private JLabel lblAddress = new JLabel("Address (A):");
	private JLabel lblAcc = new JLabel("Account (Acc):");
	private JLabel lblSwift = new JLabel("PIB (11 letters max):");

	private JTextField txtAlgorithm = new JTextField();
	private JTextField txtValidity = new JTextField();
	private JTextField txtL = new JTextField();
	private JTextField txtCN = new JTextField();
	private JTextField txtOU = new JTextField();
	private JTextField txtO = new JTextField();
	private JTextField txtC = new JTextField();
	private JTextField txtE = new JTextField();
	private JTextField txtAddress = new JTextField();
	private JTextField txtAcc = new JTextField();
	private JTextField txtSwift = new JTextField();

	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	private int days = 0;

	private Firm firm;
	private String bankName;
	private RegisteredFirms registeredFirms;
	private CertificateGenerator cert;

	public NewFirmDialog(MainFrame mf, String name) {

		setModal(true);
		bankName = name;
		firm = new Firm();
		registeredFirms = RegisteredFirms.load(name);
		setResizable(false);
		setTitle("Firm Registration");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new MigLayout());

		JPanel certP = new JPanel();
		certP.setLayout(new MigLayout(
				"",
				"10[]20[]10",
				"10[]10[]10[]10"));
		certP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Certificate data"));

		certP.add(lblAlgorithm);
		txtAlgorithm.setText("SHA1withRSA");
		txtAlgorithm.setEditable(false);
		txtAlgorithm.setMinimumSize(new Dimension(160,20));
		certP.add(txtAlgorithm,"wrap");

		certP.add(lblValidity);
		txtValidity.setMinimumSize(new Dimension(60,20));
		certP.add(txtValidity,"wrap");

		MigLayout layout = new MigLayout(
				"",
				"10[]10[]10",
				"10[]10[]10[]10[]10[]10[]10");		
		JPanel bankP = new JPanel();
		bankP.setLayout(layout);
		bankP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Firm data"));

		bankP.add(lblCN);
		txtCN.setMinimumSize(new Dimension(160,20));
		bankP.add(txtCN,"wrap");

		bankP.add(lblOU);
		txtOU.setMinimumSize(new Dimension(160,20));
		bankP.add(txtOU,"wrap");	

		bankP.add(lblO);
		txtO.setMinimumSize(new Dimension(160,20));
		bankP.add(txtO,"wrap");		

		bankP.add(lblL);
		txtL.setMinimumSize(new Dimension(60,20));
		bankP.add(txtL,"wrap");

		bankP.add(lblC);
		txtC.setMinimumSize(new Dimension(60,20));
		bankP.add(txtC,"wrap");

		bankP.add(lblE);
		txtE.setMinimumSize(new Dimension(160,20));
		bankP.add(txtE,"wrap");

		bankP.add(lblSwift);
		txtSwift.setMinimumSize(new Dimension(60,20));
		bankP.add(txtSwift,"wrap");	
		
		bankP.add(lblAddress);
		txtAddress.setMinimumSize(new Dimension(60,20));
		bankP.add(txtAddress,"wrap");
		
		bankP.add(lblAcc);
		txtAcc.setMinimumSize(new Dimension(60,20));
		bankP.add(txtAcc,"wrap");

		JPanel okP = new JPanel();
		okP.setLayout(new MigLayout("","[]20[]","10[]10"));
		okP.add(btnOk, "gapleft 90");
		okP.add(btnCancel);

		add(bankP, "wrap");
		add(certP, "wrap");
		add(okP, "wrap");		

		pack();		
		setLocationRelativeTo(null);

		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}			
		});

		btnOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String CN = txtCN.getText().trim();
				if(("").equals(CN)){
					JOptionPane.showMessageDialog(null, "Please, enter firm name!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtCN.requestFocus();
					return;
				}
				String OU = txtOU.getText().trim();
				if(("").equals(OU)){
					JOptionPane.showMessageDialog(null, "Please, enter organisation unit!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtOU.requestFocus();
					return;
				}
				String O = txtO.getText().trim();
				if(("").equals(O)){
					JOptionPane.showMessageDialog(null, "Please, enter organisation name!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtO.requestFocus();
					return;
				}
				String loc =txtL.getText().trim();
				if(("").equals(loc)){
					JOptionPane.showMessageDialog(null, "Please, enter locality postcode!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtL.requestFocus();
					return;
				}
				try{
					Integer.valueOf(txtL.getText().trim());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Please, enter postcode in correct format!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtL.requestFocus();
					return;
				}
				String C = txtC.getText().trim();
				if(("").equals(C)){
					JOptionPane.showMessageDialog(null, "Please, enter country code!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtC.requestFocus();
					return;
				}
				if(C.length()<2){
					JOptionPane.showMessageDialog(null, "Country code must be at least 2 letters long!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtC.requestFocus();
					return;
				}
				String E = txtE.getText();
				if(("").equals(E)){
					JOptionPane.showMessageDialog(null, "Please, enter email!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtE.requestFocus();
					return;
				}
				String pib = txtSwift.getText().trim();
				if(("").equals(pib)){
					JOptionPane.showMessageDialog(null, "Please, enter firm pib!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtE.requestFocus();
					return;
				}
				if(pib.length()> 11){
					JOptionPane.showMessageDialog(null, "Bank id must be less than 12 letters long!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtC.requestFocus();
					return;
				}
				if(("").equals(txtValidity.getText())){
					JOptionPane.showMessageDialog(null, "Please, enter validity period!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtValidity.requestFocus();
					return;
				}
				try{
					days = Integer.valueOf(txtValidity.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Please, enter validity period in correct format!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtValidity.requestFocus();
					return;
				}
				
				String address = txtAddress.getText().trim();
				if(("").equals(address)){
					JOptionPane.showMessageDialog(null, "Please, enter firm address!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtAddress.requestFocus();
					return;
				}
				
				String account = txtAcc.getText().trim();
				if(("").equals(account)){
					JOptionPane.showMessageDialog(null, "Please, enter firm account!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtAcc.requestFocus();
					return;
				}

				if(registeredFirms.getFirm().size() > 0){
					for(Firm f : registeredFirms.getFirm()) {
						if(f.getPib().equalsIgnoreCase(pib)) {
							JOptionPane.showMessageDialog(null, "Please, valid pib!","ERROR",JOptionPane.ERROR_MESSAGE);
							txtSwift.requestFocus();
							return;
						}
					}
				}

				firm.setName(CN);
				firm.setOrganisationName(OU);
				firm.setEmail(E);
				firm.setCountry(C);
				firm.setPib(pib);
				firm.setPostCode(loc);
				firm.setAccount(account);
				firm.setAddress(address);
				
				cert = new CertificateGenerator();
				X509Certificate certificate = cert.generateChainedCertificate(CN, O, OU, C, loc, E, pib, days, false, bankName);
				
				firm.addCertificate(certificate);
				setVisible(false);
				dispose();
				registeredFirms.addFirm(firm);
				registeredFirms.store(registeredFirms.getFirm(), bankName);
				

				
			}			
		});


	}
	
	public NewFirmDialog(String name, Firm f) {
		
		registeredFirms = RegisteredFirms.load(name);
		firm = f;
		bankName = name;
		
		setModal(true);
		setResizable(false);
		setTitle("Firm Registration");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new MigLayout());

		JPanel certP = new JPanel();
		certP.setLayout(new MigLayout(
				"",
				"10[]20[]10",
				"10[]10[]10[]10"));
		certP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Certificate data"));

		certP.add(lblAlgorithm);
		txtAlgorithm.setText("SHA1withRSA");
		txtAlgorithm.setEditable(false);
		txtAlgorithm.setMinimumSize(new Dimension(160,20));
		certP.add(txtAlgorithm,"wrap");

		certP.add(lblValidity);
		txtValidity.setMinimumSize(new Dimension(60,20));
		certP.add(txtValidity,"wrap");

		MigLayout layout = new MigLayout(
				"",
				"10[]10[]10",
				"10[]10[]10[]10[]10[]10[]10");		
		JPanel bankP = new JPanel();
		bankP.setLayout(layout);
		bankP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Bank data"));

		bankP.add(lblCN);
		txtCN.setMinimumSize(new Dimension(160,20));
		bankP.add(txtCN,"wrap");
		txtCN.setText(firm.getName());
		txtCN.setEditable(false);
		
		bankP.add(lblO);
		txtO.setMinimumSize(new Dimension(160,20));
		bankP.add(txtO,"wrap");
		txtO.setText(firm.getOrganisationName());
		txtO.setEditable(false);

		bankP.add(lblL);
		txtL.setMinimumSize(new Dimension(60,20));
		bankP.add(txtL,"wrap");
		txtL.setText(firm.getPostCode());
		txtL.setEditable(false);

		bankP.add(lblC);
		txtC.setMinimumSize(new Dimension(60,20));
		bankP.add(txtC,"wrap");
		txtC.setText(firm.getCountry());
		txtC.setEditable(false);

		bankP.add(lblE);
		txtE.setMinimumSize(new Dimension(160,20));
		bankP.add(txtE,"wrap");
		txtE.setText(firm.getEmail());
		txtE.setEditable(false);

		bankP.add(lblSwift);
		txtSwift.setMinimumSize(new Dimension(60,20));
		bankP.add(txtSwift,"wrap");	
		txtSwift.setText(firm.getPib());
		txtSwift.setEditable(false);
		
		bankP.add(lblAddress);
		txtAddress.setMinimumSize(new Dimension(60,20));
		bankP.add(txtAddress,"wrap");	
		txtAddress.setText(firm.getAddress());
		txtAddress.setEditable(false);
		
		bankP.add(lblAcc);
		txtAcc.setMinimumSize(new Dimension(60,20));
		bankP.add(txtAcc,"wrap");	
		txtAcc.setText(firm.getAccount());
		txtAcc.setEditable(false);
		
		txtValidity.requestFocus();

		JPanel okP = new JPanel();
		okP.setLayout(new MigLayout("","[]20[]","10[]10"));
		okP.add(btnOk, "gapleft 90");
		okP.add(btnCancel);

		add(bankP, "wrap");
		add(certP, "wrap");
		add(okP, "wrap");		

		pack();		
		setLocationRelativeTo(null);

		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}			
		});

		btnOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				String CN = txtCN.getText().trim();
				String OU = txtOU.getText().trim();
				String O = txtO.getText().trim();
				String loc =txtL.getText().trim();
				String C = txtC.getText().trim();
				String E = txtE.getText();
				String swift = txtSwift.getText().trim();
				
				try{
					days = Integer.valueOf(txtValidity.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Please, enter validity period in correct format!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtValidity.requestFocus();
					return;
				}

				cert = new CertificateGenerator();
				X509Certificate certificate = cert.generateChainedCertificate(CN, O, OU, C, loc, E, swift, days, false, bankName);
				
				firm.addCertificate(certificate);
				setVisible(false);
				dispose();
				registeredFirms.removeFirm(swift);
				registeredFirms.addFirm(firm);
				registeredFirms.store(registeredFirms.getFirm(), bankName);
			}			
		});
		
		
	}

	public RegisteredFirms getRegisteredFirms() {
		return registeredFirms;
	}

}
