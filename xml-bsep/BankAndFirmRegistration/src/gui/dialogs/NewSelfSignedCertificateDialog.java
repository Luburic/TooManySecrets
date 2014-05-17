package gui.dialogs;

import gui.MainFrame;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import security.CertificateGenerator;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class NewSelfSignedCertificateDialog extends JDialog {

	private JLabel lbAlg = new JLabel("Signature Algorithm:");
	private JLabel lbValidity = new JLabel("Validity period (days):");
	private JLabel lbCN = new JLabel("Common Name (CN):");
	private JLabel lbOU = new JLabel("Organisation Unit (OU):");
	private JLabel lbO = new JLabel("Organisation Name (O):");
	private JLabel lbL = new JLabel("Locality Postcode (L):");
	private JLabel lbC = new JLabel("Country Code (C):");
	private JLabel lbE = new JLabel("Email (E):");

	private JTextField tfAlg = new JTextField();
	private JTextField tfValidity = new JTextField();
	private JTextField tfL = new JTextField();
	private JTextField tfOU = new JTextField();
	private JTextField tfCN = new JTextField();
	private JTextField tfO = new JTextField();
	private JTextField tfC = new JTextField();
	private JTextField tfE = new JTextField();

	private JButton btnOk = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");

	private int days = 0;
	private CertificateGenerator cert = new CertificateGenerator();

	public NewSelfSignedCertificateDialog(MainFrame mf) {
		
		super(mf, Dialog.ModalityType.APPLICATION_MODAL);
		setTitle("Issue self-signed certificate");		
		setResizable(false);
		MigLayout layout = new MigLayout(
				"",
				"20[]10[]20",
				"20[]20[]20[]10[]10[]10[]10[]10[]20[]20");
		setLayout(layout);

		add(lbAlg);
		tfAlg.setText("SHA1withRSA");
		tfAlg.setEditable(false);
		tfAlg.setMinimumSize(new Dimension(160,20));
		add(tfAlg,"wrap");

		add(lbValidity);
		tfValidity.setMinimumSize(new Dimension(60,20));
		add(tfValidity,"wrap");

		add(lbCN);
		tfCN.setMinimumSize(new Dimension(160,20));
		add(tfCN,"wrap");
		
		add(lbOU);
		tfOU.setMinimumSize(new Dimension(160,20));
		add(tfOU,"wrap");

		add(lbO);
		tfO.setMinimumSize(new Dimension(160,20));
		add(tfO,"wrap");		

		add(lbL);
		tfL.setMinimumSize(new Dimension(60,20));
		add(tfL,"wrap");

		add(lbC);
		tfC.setMinimumSize(new Dimension(60,20));
		add(tfC,"wrap");

		add(lbE);
		tfE.setMinimumSize(new Dimension(160,20));
		add(tfE,"wrap");

		add(btnOk, "gapleft 70");
		add(btnCancel);

		pack();
		setLocationRelativeTo(mf);
		
		btnCancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}			
		});

		btnOk.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(("").equals(tfValidity.getText())){
					JOptionPane.showMessageDialog(null, "Please, enter validity period!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfValidity.requestFocus();
					return;
				}
				try{
					days = Integer.valueOf(tfValidity.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Please, enter validity period in correct format!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfValidity.requestFocus();
					return;
				}
				String CN = tfCN.getText();
				if(("").equals(CN)){
					JOptionPane.showMessageDialog(null, "Please, enter common name!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfCN.requestFocus();
					return;
				}
				String OU = tfOU.getText();
				if(("").equals(OU)){
					JOptionPane.showMessageDialog(null, "Please, enter organisation unit!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfOU.requestFocus();
					return;
				}
				String O = tfO.getText();
				if(("").equals(O)){
					JOptionPane.showMessageDialog(null, "Please, enter organisation name!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfO.requestFocus();
					return;
				}
				String L =tfL.getText();
				if(("").equals(L)){
					JOptionPane.showMessageDialog(null, "Please, enter locality postcode!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfL.requestFocus();
					return;
				}
				try{
					Integer.valueOf(tfL.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Please, enter postcode in correct format!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfL.requestFocus();
					return;
				}
				
				String C = tfC.getText();
				if(("").equals(C)){
					JOptionPane.showMessageDialog(null, "Please, enter country code!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfC.requestFocus();
					return;
				}
				String E = tfE.getText();
				if(("").equals(E)){
					JOptionPane.showMessageDialog(null, "Please, enter email!","ERROR",JOptionPane.ERROR_MESSAGE);
					tfE.requestFocus();
					return;
				}

				int uid = 100;

				cert.generateSelfSignedCertificate(CN, O, OU, C, L, E, String.valueOf(uid), days);

				setVisible(false);
				dispose();
			}			
		});
	}

}
