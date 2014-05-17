package gui.dialogs;

import gui.MainFrame;

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
import banks.Bank;
import banks.RegisteredBanks;

@SuppressWarnings("serial")
public class NewBankDialog extends JDialog {

	private JLabel lblAlgorithm = new JLabel("Signature Algorithm:");
	private JLabel lblValidity = new JLabel("Validity period (days):");
	private JLabel lblCN = new JLabel("Name:");
	private JLabel lblOU = new JLabel("Organisation Name (OU):");
	private JLabel lblO = new JLabel("Organisation Name (O):");
	private JLabel lblL = new JLabel("Locality Postcode (L):");
	private JLabel lblC = new JLabel("Country Code (C):");
	private JLabel lblE = new JLabel("Email (E):");
	private JLabel lblSwift = new JLabel("ID (4 letters):");

	private JTextField txtAlgorithm = new JTextField();
	private JTextField txtValidity = new JTextField();
	private JTextField txtL = new JTextField();
	private JTextField txtCN = new JTextField();
	private JTextField txtOU = new JTextField();
	private JTextField txtO = new JTextField();
	private JTextField txtC = new JTextField();
	private JTextField txtE = new JTextField();
	private JTextField txtSwift = new JTextField();

	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	private int days = 0;

	private Bank bank;
	private RegisteredBanks registeredBanks;
	private CertificateGenerator cert;

	public NewBankDialog(MainFrame mf) {

		setModal(true);		
		bank = new Bank();
		registeredBanks = RegisteredBanks.load();
		setResizable(false);
		setTitle("Bank Registration");
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
					JOptionPane.showMessageDialog(null, "Please, enter bank name!","ERROR",JOptionPane.ERROR_MESSAGE);
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
				String swift = txtSwift.getText().trim();
				if(("").equals(swift)){
					JOptionPane.showMessageDialog(null, "Please, enter bank id!","ERROR",JOptionPane.ERROR_MESSAGE);
					txtE.requestFocus();
					return;
				}
				if(swift.length()!=4){
					JOptionPane.showMessageDialog(null, "Bank id must be exactly 4 letters long!","ERROR",JOptionPane.ERROR_MESSAGE);
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

				swift += C.substring(0, 2) + loc.substring(0, 2);
				for(Bank b : registeredBanks.getBank()) {
					if(b.getSwiftCode().equalsIgnoreCase(swift)) {
						JOptionPane.showMessageDialog(null, "Please, valid bank id!","ERROR",JOptionPane.ERROR_MESSAGE);
						txtE.requestFocus();
						return;
					}
				}

				bank.setSwiftCode(swift);
				bank.setName(CN);
				bank.setOrganisationName(O);
				bank.setCountry(C);
				bank.setPostcode(loc);
				bank.setEmail(E);
				if(bank.getId() == null){
					String acc = String.valueOf((int) (1000*Math.random()));
					bank.setId(acc);
					while(registeredBanks.getBank().contains(bank)) { 
						acc = String.valueOf((int) (1000*Math.random()));
						bank.setId(acc);
					}
				}
				cert = new CertificateGenerator();
				X509Certificate certificate = cert.generateChainedCertificate(CN, O, OU, C, loc, E, swift, days, true);
				
				bank.addCertificate(certificate);
				setVisible(false);
				dispose();
				registeredBanks.addBank(bank);
				registeredBanks.store(registeredBanks.getBank());
			}			
		});


	}
	
	public NewBankDialog(Bank b) {
		
		registeredBanks = RegisteredBanks.load();
		bank = b;
		setModal(true);
		setResizable(false);
		setTitle("Bank Registration");
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
		txtCN.setText(bank.getName());
		txtCN.setEditable(false);
		
		bankP.add(lblO);
		txtO.setMinimumSize(new Dimension(160,20));
		bankP.add(txtO,"wrap");
		txtO.setText(bank.getOrganisationName());
		txtO.setEditable(false);

		bankP.add(lblL);
		txtL.setMinimumSize(new Dimension(60,20));
		bankP.add(txtL,"wrap");
		txtL.setText(bank.getPostcode());
		txtL.setEditable(false);

		bankP.add(lblC);
		txtC.setMinimumSize(new Dimension(60,20));
		bankP.add(txtC,"wrap");
		txtC.setText(bank.getCountry());
		txtC.setEditable(false);

		bankP.add(lblE);
		txtE.setMinimumSize(new Dimension(160,20));
		bankP.add(txtE,"wrap");
		txtE.setText(bank.getEmail());
		txtE.setEditable(false);

		bankP.add(lblSwift);
		txtSwift.setMinimumSize(new Dimension(60,20));
		bankP.add(txtSwift,"wrap");	
		txtSwift.setText(bank.getSwiftCode());
		txtSwift.setEditable(false);
		
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
				X509Certificate certificate = cert.generateChainedCertificate(CN, O, OU, C, loc, E, swift, days, true);
				
				bank.addCertificate(certificate);
				setVisible(false);
				dispose();
				registeredBanks.removeBank(swift);
				registeredBanks.addBank(bank);
				registeredBanks.store(registeredBanks.getBank());
			}			
		});
		
		
	}

	public RegisteredBanks getRegisteredBanks() {
		return registeredBanks;
	}





}
